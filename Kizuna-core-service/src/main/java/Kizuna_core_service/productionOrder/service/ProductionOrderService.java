package Kizuna_core_service.productionOrder.service;

import Kizuna_core_service.inventory.domain.Inventory;
import Kizuna_core_service.inventory.repository.InventoryRepository;
import Kizuna_core_service.inventory_movement.domain.InventoryMovement;
import Kizuna_core_service.inventory_movement.domain.MovementType;
import Kizuna_core_service.inventory_movement.repository.InventoryMovementRepository;
import Kizuna_core_service.productionOrder.domain.ProductionOrder;
import Kizuna_core_service.productionOrder.domain.ProductionOrderStatus;
import Kizuna_core_service.productionOrder.dto.ProductionOrderRequestDto;
import Kizuna_core_service.productionOrder.dto.ProductionOrderResponseDto;
import Kizuna_core_service.productionOrder.messaging.ProductionEventPublisher;
import Kizuna_core_service.productionOrder.repository.ProductionOrderRepository;
import Kizuna_core_service.recipe.domain.Recipe;
import Kizuna_core_service.recipe.domain.RecipeItem;
import Kizuna_core_service.recipe.repository.RecipeRepository;
import Kizuna_core_service.shared.exception.BusinessException;
import Kizuna_core_service.shared.exception.NotFoundException;
import Kizuna_core_service.shared.integration.UserResponseDto;
import Kizuna_core_service.shared.util.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service

public class ProductionOrderService {
    private final ProductionOrderRepository productionOrderRepository;
    private final RecipeRepository recipeRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductionEventPublisher publisher;
    private final OperatorValidateService operatorValidateService;

    public ProductionOrderService(ProductionOrderRepository productionOrderRepository, RecipeRepository recipeRepository, InventoryRepository inventoryRepository, InventoryMovementRepository inventoryMovementRepository, ProductionEventPublisher publisher, OperatorValidateService operatorValidateService) {
        this.productionOrderRepository = productionOrderRepository;
        this.recipeRepository = recipeRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.publisher = publisher;
        this.operatorValidateService = operatorValidateService;
    }

    public List<ProductionOrderResponseDto> findAll() {
        Boolean isAdmin = SecurityUtils.getRoles().contains("ADMIN");
        Boolean isPlanner = SecurityUtils.getRoles().contains("PLANNER");

        if(isAdmin || isPlanner){
            return productionOrderRepository.findAll().stream().map(this::productionOrderResponseDto).toList();
        }
        String userId=SecurityUtils.getUserId();
        return productionOrderRepository.findByOperatorId(userId).stream().map(this::productionOrderResponseDto).toList();
    }

    public ProductionOrderResponseDto findById(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id).orElseThrow(() -> new NotFoundException("Production order not found"));
        return productionOrderResponseDto(productionOrder);
    }

    public List<ProductionOrderResponseDto> findByStatus(ProductionOrderStatus status) {
        return productionOrderRepository.findByStatus(status).stream().map(this::productionOrderResponseDto).toList();
    }

    @Transactional
    public ProductionOrderResponseDto create(ProductionOrderRequestDto requestDto) {

        Recipe recipe = recipeRepository.findById(requestDto.recipeId()).orElseThrow(() -> new NotFoundException("Recipe not found"));
        ProductionOrder productionOrder = new ProductionOrder();
        productionOrder.setQuantityToProduce(requestDto.quantityToProduce());
        productionOrder.setStatus(ProductionOrderStatus.PLANNED);
        productionOrder.setCreatedBy(SecurityUtils.getUsername());
        productionOrder.setRecipe(recipe);
        productionOrder.setPriority(requestDto.priority());
        productionOrder.setDeadline(requestDto.deadline());
        UserResponseDto operator = operatorValidateService.validateOperator(requestDto.operatorId());
        productionOrder.setOperatorId(operator.keycloakId());
        productionOrder.setOperatorName(operator.username());
        System.out.println(operator.username());
        System.out.println(operator.keycloakId());
        productionOrder.setEstimatedTotalTime(productionOrder.getQuantityToProduce() * recipe.getEstimatedProductionTime());
        productionOrderRepository.save(productionOrder);
        reorderQueue();
        publisher.sendEvent(productionOrder.getId(), productionOrder.getRecipe().getName(), "PLANNED");
        return productionOrderResponseDto(productionOrder);
    }

    @Transactional
    public ProductionOrderResponseDto start(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id).orElseThrow(() -> new NotFoundException("Production order not found"));
        if (!productionOrder.getStatus().equals(ProductionOrderStatus.PLANNED)) {
            throw new BusinessException("Production order is not planned");
        }
        Recipe recipe = productionOrder.getRecipe();

        if (recipe.getItems().isEmpty()) {
            throw new BusinessException("Recipe has no items");
        }

        List<Inventory> inventoriesToUpdate = new ArrayList<>();
        List<InventoryMovement> movements = new ArrayList<>();
        for (RecipeItem item : recipe.getItems()) {
            Inventory inventory = item.getInventory();
            Double consumption = item.getQuantity() * productionOrder.getQuantityToProduce();
            if (inventory.getQuantity() < consumption) {
                throw new BusinessException("Not enough stock for item " + inventory.getName());
            }
        }
        for (RecipeItem item : recipe.getItems()) {
            Inventory inventory = item.getInventory();
            Double consumption = item.getQuantity() * productionOrder.getQuantityToProduce();
            inventory.removeStock(consumption);
            String reason = "Production order ID: " + productionOrder.getId();
            InventoryMovement inventoryMovement = new InventoryMovement(inventory, consumption, reason, MovementType.EXIT);
            movements.add(inventoryMovement);
            inventoriesToUpdate.add(inventory);
        }
        productionOrder.setStatus(ProductionOrderStatus.IN_PROGRESS);
        productionOrder.setStartTime(LocalDateTime.now());

        inventoryRepository.saveAll(inventoriesToUpdate);
        inventoryMovementRepository.saveAll(movements);
        productionOrderRepository.save(productionOrder);
        reorderQueue();
        publisher.sendEvent(productionOrder.getId(), productionOrder.getRecipe().getName(), "START");
        return productionOrderResponseDto(productionOrder);




    }

    @Transactional
    public ProductionOrderResponseDto finish(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id).orElseThrow(() -> new NotFoundException("Production order not found"));
        if (!productionOrder.getStatus().equals(ProductionOrderStatus.IN_PROGRESS)) {
            throw new BusinessException("Production order is not in progress");
        }
        productionOrder.setStatus(ProductionOrderStatus.WAITING_INSPECTION);
        productionOrder.setEndTime(LocalDateTime.now());
        productionOrderRepository.save(productionOrder);
        publisher.sendEvent(productionOrder.getId(), productionOrder.getRecipe().getName(), "FINISH");
        return productionOrderResponseDto(productionOrder);
    }

    @Transactional
    public ProductionOrderResponseDto cancel(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id).orElseThrow(() -> new NotFoundException("Production order not found"));
        if (productionOrder.getStatus().equals(ProductionOrderStatus.WAITING_INSPECTION) || productionOrder.getStatus().equals(Kizuna_core_service.productionOrder.domain.ProductionOrderStatus.FINISHED_BY_TIME)) {
            throw new BusinessException("Production order is already completed");
        }
        productionOrder.setStatus(ProductionOrderStatus.CANCELLED);
        productionOrder.setEndTime(LocalDateTime.now());
        productionOrderRepository.save(productionOrder);
        reorderQueue();
        publisher.sendEvent(productionOrder.getId(), productionOrder.getRecipe().getName(), "CANCELLED");
        return productionOrderResponseDto(productionOrder);
    }
    public void moveOrder(Long id, int newPosition) {

        List<ProductionOrder> queue = productionOrderRepository
                .findByStatus(ProductionOrderStatus.PLANNED);

        queue.sort(Comparator.comparing(ProductionOrder::getQueuePosition));

        ProductionOrder order = queue.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Order not found"));

        queue.remove(order);
        queue.add(newPosition - 1, order);

        for (int i = 0; i < queue.size(); i++) {
            queue.get(i).setQueuePosition(i + 1);
        }

        productionOrderRepository.saveAll(queue);
    }





    public Long calculateRemainingTime(ProductionOrder order) {
        if (order.getStartTime() == null) {
            return order.getEstimatedTotalTime();
        }
        long elapsed = Duration.between(order.getStartTime(), LocalDateTime.now()).toMinutes();
        long remaing = order.getEstimatedTotalTime() - elapsed;
        return Math.max(0, remaing);

    }

    public Double calculateProgress(ProductionOrder order) {
        if (order.getStartTime() == null) {
            return 0.0;
        }
        if(order.getStatus().equals(ProductionOrderStatus.CANCELLED)){
            return 0.0;
        }
        long total = order.getEstimatedTotalTime();
        long elapsed = Duration.between(order.getStartTime(), LocalDateTime.now()).toMinutes();
        double progress = ((double) elapsed / total) * 100;
        return Math.min(progress, 100.0);
    }

    public LocalDateTime calculateETA(ProductionOrder order) {
        if (order.getStartTime() == null) {
            return null;
        }
        return order.getStartTime().plusMinutes(order.getEstimatedTotalTime());
    }

    public ProductionOrderStatus calculateStatus(ProductionOrder order) {
        if (order.getStatus() == ProductionOrderStatus.WAITING_INSPECTION ||
                order.getStatus() == ProductionOrderStatus.CANCELLED ||
                order.getStatus() == ProductionOrderStatus.FINISHED_BY_TIME) {
            return order.getStatus();
        }

        if (order.getStartTime() == null) {
            return ProductionOrderStatus.PLANNED;
        }

        long elapsed = Duration.between(order.getStartTime(), LocalDateTime.now()).toMinutes();

        if (elapsed >= order.getEstimatedTotalTime()) {
            return ProductionOrderStatus.FINISHED_BY_TIME;
        }

        return ProductionOrderStatus.IN_PROGRESS;
    }
    @Transactional
    public void autoFinishByTime() {
        List<ProductionOrder> orders = productionOrderRepository
                .findByStatus(ProductionOrderStatus.IN_PROGRESS);

        for (ProductionOrder order : orders) {
            long elapsed = Duration.between(order.getStartTime(), LocalDateTime.now()).toMinutes();

            if (elapsed >= order.getEstimatedTotalTime()) {
                order.setStatus(ProductionOrderStatus.FINISHED_BY_TIME);
                order.setEndTime(LocalDateTime.now());
                productionOrderRepository.save(order);
            }
        }
    }

    @Transactional
    public void reorderQueue(){
        List<ProductionOrder> orders=productionOrderRepository.findByStatus(ProductionOrderStatus.PLANNED);

        orders.sort(Comparator
                .comparing(ProductionOrder::getPriority, Comparator.nullsLast(Integer::compareTo)).reversed()
                .thenComparing(ProductionOrder::getDeadline, Comparator.nullsLast(LocalDateTime::compareTo)));

        for(int i=0;i<orders.size();i++){
            orders.get(i).setQueuePosition(i+1);
        }
        productionOrderRepository.saveAll(orders);

    }

    private ProductionOrderResponseDto productionOrderResponseDto(ProductionOrder productionOrder) {
        return ProductionOrderResponseDto.builder().id(productionOrder.getId()).recipeName(productionOrder.getRecipe().getName())
                .quantityToProduce(productionOrder.getQuantityToProduce())
                .startTime(productionOrder.getStartTime())
                .createdBy(SecurityUtils.getUsername())
                .calculatedStatus(calculateStatus(productionOrder)).status(productionOrder.getStatus())
                .progress(calculateProgress(productionOrder))
                .eta(calculateETA(productionOrder))
                .remainingTime(calculateRemainingTime(productionOrder))
                .priority(productionOrder.getPriority())
                .queuePosition(productionOrder.getQueuePosition())
                .estimatedTotalTime(productionOrder.getEstimatedTotalTime())
                .endTime(productionOrder.getEndTime())
                .deadline(productionOrder.getDeadline())
                .queuePosition(productionOrder.getQueuePosition())
                .operatorId(productionOrder.getOperatorId())
                .operatorName(productionOrder.getOperatorName())
                .build();

    }

}
