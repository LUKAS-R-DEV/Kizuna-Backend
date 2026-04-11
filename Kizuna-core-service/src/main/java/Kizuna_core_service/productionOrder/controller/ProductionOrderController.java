package Kizuna_core_service.productionOrder.controller;

import Kizuna_core_service.productionOrder.domain.ProductionOrderStatus;
import Kizuna_core_service.productionOrder.dto.ProductionOrderRequestDto;
import Kizuna_core_service.productionOrder.dto.ProductionOrderResponseDto;
import Kizuna_core_service.productionOrder.service.ProductionOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/production-order")
public class ProductionOrderController {
    private final ProductionOrderService productionOrderService;
    public ProductionOrderController(ProductionOrderService productionOrderService) {
        this.productionOrderService = productionOrderService;
    }
    @PreAuthorize("hasRole('PLANNER')")
    @GetMapping
    public List<ProductionOrderResponseDto> findAll(){
        return productionOrderService.findAll();
    }
    @GetMapping("/status/{status}")
    public List<ProductionOrderResponseDto> findByStatus(ProductionOrderStatus status){
        return productionOrderService.findByStatus(status);
    }
    @PreAuthorize("hasRole('PLANNER')")
    @PostMapping
    public ResponseEntity<ProductionOrderResponseDto> create(@Valid @RequestBody ProductionOrderRequestDto requestDto){
        ProductionOrderResponseDto productionOrderResponseDto = productionOrderService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productionOrderResponseDto);
    }
    @PreAuthorize("hasRole('OPERATOR')")
    @PostMapping("/{id}/start")
    public ResponseEntity<ProductionOrderResponseDto> start(@PathVariable Long id){
        ProductionOrderResponseDto productionOrderResponseDto = productionOrderService.start(id);
        return ResponseEntity.ok(productionOrderResponseDto);
    }
    @PreAuthorize("hasRole('OPERATOR')")
    @PostMapping("/{id}/finish")
    public ResponseEntity<ProductionOrderResponseDto> finish(@PathVariable Long id){
        ProductionOrderResponseDto productionOrderResponseDto = productionOrderService.finish(id);
        return ResponseEntity.ok(productionOrderResponseDto);
    }
    @PreAuthorize("hasRole('PLANNER')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ProductionOrderResponseDto> cancel(@PathVariable Long id){
        ProductionOrderResponseDto productionOrderResponseDto = productionOrderService.cancel(id);
        return ResponseEntity.ok(productionOrderResponseDto);
    }
    @PreAuthorize("hasRole('PLANNER')")
    @GetMapping("/queue")
    public List<ProductionOrderResponseDto> getQueue(){
        return productionOrderService.findByStatus(ProductionOrderStatus.PLANNED);
    }
    @PreAuthorize("hasRole('PLANNER')")
    @PutMapping("/{id}/reorder")
    public void reorder(@PathVariable Long id, @RequestParam int position) {
        productionOrderService.moveOrder(id, position);
    }
}
