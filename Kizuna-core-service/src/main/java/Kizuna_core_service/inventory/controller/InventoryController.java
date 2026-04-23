package Kizuna_core_service.inventory.controller;

import Kizuna_core_service.inventory.dto.InventoryMovementDto;
import Kizuna_core_service.inventory.dto.InventoryRequestDto;
import Kizuna_core_service.inventory.dto.InventoryResponseDto;
import Kizuna_core_service.inventory.dto.InventoryUpdateDto;
import Kizuna_core_service.inventory.service.InventoryService;
import Kizuna_core_service.shared.dto.ApiResponseGeneric;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'EXECUTIVE', 'PLANNER')")
    @GetMapping
    public List<InventoryResponseDto> findAll() {
        return inventoryService.findAll();
    }

    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER', 'EXECUTIVE', 'PLANNER')")
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDto> findById(@PathVariable Long id) {
        InventoryResponseDto inventory = inventoryService.findById(id);
        return ResponseEntity.ok(inventory);
    }
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    @PostMapping
    public ResponseEntity<InventoryResponseDto> create(@Valid @RequestBody InventoryRequestDto requestDto) {
        InventoryResponseDto inventory = inventoryService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventory);
    }
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    @PostMapping("/{id}/movement/entry")
    public ResponseEntity<ApiResponseGeneric> createEntryMovement( @Valid @RequestBody InventoryMovementDto inventoryMovementDto) {
        ApiResponseGeneric apiResponseGeneric=inventoryService.entryInventory(inventoryMovementDto);
        return ResponseEntity.ok(apiResponseGeneric);
    }
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    @PostMapping("/{id}/movement/exit")
    public ResponseEntity<ApiResponseGeneric> createExitMovement(@Valid @RequestBody InventoryMovementDto inventoryMovementDto) {
        ApiResponseGeneric apiResponseGeneric=inventoryService.exitInventory(inventoryMovementDto);
        return ResponseEntity.ok(apiResponseGeneric);
    }
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponseDto> update(@Valid @PathVariable Long id, @RequestBody InventoryUpdateDto updateDto) {
        InventoryResponseDto inventory = inventoryService.update(id, updateDto);
        return ResponseEntity.ok(inventory);
    }
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<InventoryResponseDto> disable(@PathVariable Long id) {
        InventoryResponseDto inventory = inventoryService.disable(id);
        return ResponseEntity.ok(inventory);
    }
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    @PatchMapping("/{id}/enable")
    public ResponseEntity<InventoryResponseDto> enable(@PathVariable Long id) {
        InventoryResponseDto inventory = inventoryService.enable(id);
        return ResponseEntity.ok(inventory);
    }

}
