package Kizuna_core_service.inventory_movement.service;

import Kizuna_core_service.inventory.domain.Inventory;
import Kizuna_core_service.inventory.repository.InventoryRepository;
import Kizuna_core_service.inventory_movement.domain.InventoryMovement;
import Kizuna_core_service.inventory_movement.domain.MovementType;
import Kizuna_core_service.inventory_movement.dto.InventoryMovementRequestDto;
import Kizuna_core_service.inventory_movement.dto.InventoryMovementResponseDto;
import Kizuna_core_service.inventory_movement.dto.InventoryMovementUpdateDto;
import Kizuna_core_service.inventory_movement.repository.InventoryMovementRepository;
import Kizuna_core_service.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryMovementService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    public InventoryMovementService(InventoryMovementRepository inventoryMovementRepository,InventoryRepository inventoryRepository) {
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.inventoryRepository = inventoryRepository;
    }
        public List<InventoryMovementResponseDto> findAll(){
            return inventoryMovementRepository.findAll().stream().map(this::toResponseInventoryMovement).toList();
        }
        public InventoryMovementResponseDto findById(Long id){
            InventoryMovement inventoryMovement=inventoryMovementRepository.findById(id).orElseThrow(() -> new NotFoundException("InventoryMovement not found"));
            return toResponseInventoryMovement(inventoryMovement);
        }
    @Transactional
    public InventoryMovementResponseDto create(InventoryMovementRequestDto requestDto){
        InventoryMovement inventoryMovement=new InventoryMovement();
        Inventory inventory=inventoryRepository.findById(requestDto.inventoryId()).orElseThrow(() -> new NotFoundException("Inventory not found"));
        inventoryMovement.setInventory(inventory);
        inventoryMovement.setType(requestDto.type());
        inventoryMovement.setQuantity(requestDto.quantity());
        if(inventoryMovement.getType() == MovementType.ENTRY){
            inventory.addStock(inventoryMovement.getQuantity());
        }else{
            inventory.removeStock(inventoryMovement.getQuantity());
        }
        inventoryMovementRepository.save(inventoryMovement);
        inventoryRepository.save(inventory);
        return toResponseInventoryMovement(inventoryMovement);
    }
    @Transactional
    public InventoryMovementResponseDto update(Long id, InventoryMovementUpdateDto requestDto){
        InventoryMovement inventoryMovement=inventoryMovementRepository.findById(id).orElseThrow(() -> new NotFoundException("InventoryMovement not found"));
        inventoryMovement.setReason(requestDto.reason());
        inventoryMovement.setUpdatedAt(LocalDateTime.now());
        inventoryMovementRepository.save(inventoryMovement);
        return toResponseInventoryMovement(inventoryMovement);
    }





    private final InventoryMovementResponseDto toResponseInventoryMovement(InventoryMovement inventoryMovement){
            return new InventoryMovementResponseDto(inventoryMovement.getId(),inventoryMovement.getInventory().getId(),inventoryMovement.getInventory().getName(),inventoryMovement.getQuantity(),inventoryMovement.getReason(),inventoryMovement.getCreatedAt(),inventoryMovement.getType(),inventoryMovement.getUpdatedAt());
        }


}
