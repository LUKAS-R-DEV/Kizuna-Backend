package Kizuna_core_service.inventory.service;

import Kizuna_core_service.inventory.domain.Inventory;
import Kizuna_core_service.inventory.dto.InventoryRequestDto;
import Kizuna_core_service.inventory.dto.InventoryResponseDto;
import Kizuna_core_service.inventory.dto.InventoryUpdateDto;
import Kizuna_core_service.inventory.repository.InventoryRepository;
import Kizuna_core_service.shared.exception.BusinessException;
import Kizuna_core_service.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    public List<InventoryResponseDto> findAll(){
        return inventoryRepository.findByActiveTrue().stream().map(this::toResponseDto).toList();
    }
    public InventoryResponseDto findById(Long id){
       Inventory inventory = inventoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Inventory not found"));
       return toResponseDto(inventory);
    }
    @Transactional
    public InventoryResponseDto create(InventoryRequestDto requestDto){
        Inventory inventory=new Inventory();
        inventory.setName(requestDto.name());
        inventory.setQuantity(requestDto.quantity());
        if(inventory.getQuantity()<0){
            throw new BusinessException("Quantity cannot be negative");
        }
        inventory.setMinStock(requestDto.minStock());
        if(inventory.getMinStock()<0){
            throw new BusinessException("Min stock cannot be negative");
        }
        inventory.setSupplier(requestDto.supplier());

        inventory.setCategory(requestDto.category());
        inventory.setLocation(requestDto.location());
        inventory.setActive(true);
        inventory.updateStatus();
        inventoryRepository.save(inventory);
        return toResponseDto(inventory);
    }
@Transactional
    public InventoryResponseDto update(Long id, InventoryUpdateDto updateDto){
        Inventory inventory=inventoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Inventory not found"));
        inventory.setName(updateDto.name());
        inventory.setCategory(updateDto.category());
        inventory.setLocation(updateDto.location());
        inventoryRepository.save(inventory);
        return toResponseDto(inventory);
    }
@Transactional
    public InventoryResponseDto disable(Long id){
        Inventory inventory=inventoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Inventory not found"));
        inventory.setActive(false);
        inventoryRepository.save(inventory);
        return toResponseDto(inventory);
    }
    private InventoryResponseDto toResponseDto(Inventory inventory){
        return new InventoryResponseDto(inventory.getId(),inventory.getName(),inventory.getCategory(),inventory.getLocation(),inventory.getQuantity(),inventory.getMinStock(),inventory.getSupplier(),inventory.getStatus(),inventory.getActive());
    }

}
