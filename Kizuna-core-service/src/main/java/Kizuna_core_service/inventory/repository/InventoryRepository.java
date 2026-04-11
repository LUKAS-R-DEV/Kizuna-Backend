package Kizuna_core_service.inventory.repository;

import Kizuna_core_service.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByActiveTrue();
}
