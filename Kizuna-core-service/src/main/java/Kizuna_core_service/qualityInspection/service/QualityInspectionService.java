package Kizuna_core_service.qualityInspection.service;

import Kizuna_core_service.inventory.domain.Inventory;
import Kizuna_core_service.inventory.repository.InventoryRepository;
import Kizuna_core_service.inventory_movement.domain.InventoryMovement;
import Kizuna_core_service.inventory_movement.domain.MovementType;
import Kizuna_core_service.inventory_movement.repository.InventoryMovementRepository;
import Kizuna_core_service.productionOrder.domain.ProductionOrder;
import Kizuna_core_service.productionOrder.domain.ProductionOrderStatus;
import Kizuna_core_service.productionOrder.repository.ProductionOrderRepository;
import Kizuna_core_service.productionOrder.service.ProductionOrderService;
import Kizuna_core_service.qualityInspection.domain.QualityInspection;
import Kizuna_core_service.qualityInspection.domain.QualityInspectionStatus;
import Kizuna_core_service.qualityInspection.dto.QualityInspectionRequestDto;
import Kizuna_core_service.qualityInspection.dto.QualityInspectionResponseDto;
import Kizuna_core_service.qualityInspection.repository.QualityInspectionRepository;
import Kizuna_core_service.shared.exception.BusinessException;
import Kizuna_core_service.shared.exception.NotFoundException;
import Kizuna_core_service.shared.util.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QualityInspectionService {
    private final QualityInspectionRepository qualityInspectionRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductionOrderService productionOrderService;
    private final InventoryMovementRepository inventoryMovementRepository;


    public QualityInspectionService(QualityInspectionRepository qualityInspectionRepository, ProductionOrderRepository productionOrderRepository, InventoryRepository inventoryRepository, ProductionOrderService productionOrderService, InventoryMovementRepository inventoryMovementRepository, SecurityUtils securityUtils) {
        this.qualityInspectionRepository = qualityInspectionRepository;
        this.productionOrderRepository = productionOrderRepository;
        this.inventoryRepository = inventoryRepository;
        this.productionOrderService = productionOrderService;
        this.inventoryMovementRepository = inventoryMovementRepository;
    }

    public List<QualityInspectionResponseDto> findAll(){
        return qualityInspectionRepository.findAll().stream().map(this::qualityInspectionResponseDto).toList();
    }
    public QualityInspectionResponseDto findById(Long id){
        QualityInspection qualityInspection = qualityInspectionRepository.findById(id).orElseThrow(() -> new NotFoundException("Quality inspection not found"));
        return qualityInspectionResponseDto(qualityInspection);
    }

    @Transactional
    public QualityInspectionResponseDto create(QualityInspectionRequestDto qualityInspectionRequestDto){
        ProductionOrder productionOrder=productionOrderRepository.findById(qualityInspectionRequestDto.productionOrderId()).orElseThrow(()-> new NotFoundException("Quality inspection not found"));
        if(productionOrder.getInspection()==true){
            throw new BusinessException("Production order already passed quality inspection");
        }
        QualityInspection qualityInspection=new QualityInspection();
        qualityInspection.setInspectedBy(SecurityUtils.getUsername());
        qualityInspection.setNotes(qualityInspectionRequestDto.notes());
        qualityInspection.setStatus(qualityInspectionRequestDto.status());
        qualityInspection.setProductionOrder(productionOrder);
        qualityInspection.setCreatedAt(LocalDateTime.now());

        if(qualityInspection.getStatus().equals(QualityInspectionStatus.REJECTED)){
            productionOrder.setStatus(ProductionOrderStatus.REJECTED);
            productionOrder.setInspection(true);
            qualityInspectionRepository.save(qualityInspection);
            productionOrderRepository.save(productionOrder);
            productionOrderService.reorderQueue();
            return qualityInspectionResponseDto(qualityInspection);
        }
        productionOrderRepository.save(productionOrder);
        Inventory inventory=productionOrder.getRecipe().getProduct();
        Double quantityProduced = productionOrder.getQuantityToProduce().doubleValue();

        String reason = "Quality approved - ProductionOrder ID: " + productionOrder.getId();

        InventoryMovement movement = new InventoryMovement(
                inventory,
                quantityProduced,
                reason,
                MovementType.ENTRY
        );

        inventory.addStock(quantityProduced);
        inventory.updateStatus();

        inventoryMovementRepository.save(movement);
        inventoryRepository.save(inventory);
        productionOrderRepository.save(productionOrder);
        return qualityInspectionResponseDto(qualityInspection);
    }
    private QualityInspectionResponseDto qualityInspectionResponseDto(QualityInspection qualityInspection){
        return new QualityInspectionResponseDto(qualityInspection.getId(),qualityInspection.getProductionOrder().getRecipe().getName(),qualityInspection.getStatus(),qualityInspection.getNotes(),qualityInspection.getInspectedBy(),qualityInspection.getCreatedAt());
    }





}
