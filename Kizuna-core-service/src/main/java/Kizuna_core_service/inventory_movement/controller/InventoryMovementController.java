package Kizuna_core_service.inventory_movement.controller;

import Kizuna_core_service.inventory_movement.dto.InventoryMovementRequestDto;
import Kizuna_core_service.inventory_movement.dto.InventoryMovementResponseDto;
import Kizuna_core_service.inventory_movement.dto.InventoryMovementUpdateDto;
import Kizuna_core_service.inventory_movement.service.InventoryMovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory-movements")
public class InventoryMovementController {

    private final InventoryMovementService inventoryMovementService;
    public InventoryMovementController(InventoryMovementService inventoryMovementService) {
        this.inventoryMovementService = inventoryMovementService;
    }
    @GetMapping
    public List<InventoryMovementResponseDto> findAll() {
        return inventoryMovementService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<InventoryMovementResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryMovementService.findById(id));
    }

    @PostMapping
    public ResponseEntity<InventoryMovementResponseDto> create(@Valid @RequestBody InventoryMovementRequestDto requestDto) {
        InventoryMovementResponseDto responseDto = inventoryMovementService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<InventoryMovementResponseDto> update(@Valid @PathVariable Long id, @RequestBody InventoryMovementUpdateDto requestDto) {
        InventoryMovementResponseDto responseDto = inventoryMovementService.update(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }



}
