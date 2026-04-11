package Kizuna_core_service.inventory.controller;

import Kizuna_core_service.inventory.dto.InventoryRequestDto;
import Kizuna_core_service.inventory.dto.InventoryResponseDto;
import Kizuna_core_service.inventory.dto.InventoryUpdateDto;
import Kizuna_core_service.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<InventoryResponseDto> findAll() {
        return inventoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDto> findById(@PathVariable Long id) {
        InventoryResponseDto inventory = inventoryService.findById(id);
        return ResponseEntity.ok(inventory);
    }
    @PostMapping
    public ResponseEntity<InventoryResponseDto> create(@Valid @RequestBody InventoryRequestDto requestDto) {
        InventoryResponseDto inventory = inventoryService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventory);
    }
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponseDto> update(@Valid @PathVariable Long id, @RequestBody InventoryUpdateDto updateDto) {
        InventoryResponseDto inventory = inventoryService.update(id, updateDto);
        return ResponseEntity.ok(inventory);
    }
    @PatchMapping("/{id}/disable")
    public ResponseEntity<InventoryResponseDto> disable(@PathVariable Long id) {
        InventoryResponseDto inventory = inventoryService.disable(id);
        return ResponseEntity.ok(inventory);
    }

}
