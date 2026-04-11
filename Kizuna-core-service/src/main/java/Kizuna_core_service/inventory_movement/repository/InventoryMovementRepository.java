package Kizuna_core_service.inventory_movement.repository;

import Kizuna_core_service.inventory_movement.domain.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

}
