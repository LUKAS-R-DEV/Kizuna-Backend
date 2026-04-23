package com.kizuna.data_service.metrics.service;
import com.kizuna.data_service.dto.*;
import com.kizuna.data_service.metrics.dtos.InventoryMetricsDto;
import com.kizuna.data_service.metrics.dtos.ProductionMetricsDto;
import com.kizuna.data_service.metrics.dtos.QualityMetricsDto;
import com.kizuna.data_service.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Service
public class DashboardService {

    private final ProductionEventRepository productionRepository;
    private final InventoryEventRepository inventoryRepository;
    private final QualityInspectionEventRepository qualityRepository;

    // ======================
    // PRODUÇÃO
    // ======================
    public ProductionMetricsDto getProductionMetrics() {

        long total = productionRepository.count();

        long started = productionRepository.countByType("STARTED");
        long finished = productionRepository.countByType("FINISHED");

        return new ProductionMetricsDto(total, started, finished);
    }

    // ======================
    // INVENTÁRIO
    // ======================
    public InventoryMetricsDto getInventoryMetrics() {

        long totalItems = inventoryRepository.count();

        long lowStock = inventoryRepository.countByQuantityLessThan(10);

        return new InventoryMetricsDto(totalItems, lowStock);
    }

    // ======================
    // QUALIDADE
    // ======================
    public QualityMetricsDto getQualityMetrics() {

        long approved = qualityRepository.countByResult("APPROVED");
        long rejected = qualityRepository.countByResult("REJECTED");

        return new QualityMetricsDto(approved, rejected);
    }
}
