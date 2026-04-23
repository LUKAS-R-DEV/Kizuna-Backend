package com.kizuna.data_service.metrics.controller;

import com.kizuna.data_service.metrics.dtos.InventoryMetricsDto;
import com.kizuna.data_service.metrics.dtos.ProductionMetricsDto;
import com.kizuna.data_service.metrics.dtos.QualityMetricsDto;
import com.kizuna.data_service.metrics.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@AllArgsConstructor
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/inventory")
    public ResponseEntity<InventoryMetricsDto>  inventoryMetrics() {
        return ResponseEntity.ok(dashboardService.getInventoryMetrics());
    }

    @GetMapping("/production")
    public ResponseEntity<ProductionMetricsDto> productionMetrics() {
        return ResponseEntity.ok(dashboardService.getProductionMetrics());
    }

    @GetMapping("/quality")
    public ResponseEntity<QualityMetricsDto> qualityMetrics() {
        return ResponseEntity.ok(dashboardService.getQualityMetrics());
    }
}
