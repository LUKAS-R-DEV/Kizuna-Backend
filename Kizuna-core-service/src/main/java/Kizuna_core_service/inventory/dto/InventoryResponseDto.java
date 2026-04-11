package Kizuna_core_service.inventory.dto;

import Kizuna_core_service.inventory.domain.Status;

public record InventoryResponseDto(Long id, String name, String category, String location, Double quantity, Double minStock, String supplier, Status status, Boolean active) {
}
