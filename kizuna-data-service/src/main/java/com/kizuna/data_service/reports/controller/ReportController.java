package com.kizuna.data_service.reports.controller;

import com.kizuna.data_service.domain.InventoryEvent;
import com.kizuna.data_service.domain.ProductionEvent;
import com.kizuna.data_service.domain.QualityInspectionEvent;
import com.kizuna.data_service.reports.service.ReportsService;
import com.kizuna.data_service.repository.InventoryEventRepository;
import com.kizuna.data_service.repository.ProductionEventRepository;
import com.kizuna.data_service.repository.QualityInspectionEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportsService reportsService;
    private final ProductionEventRepository productionRepository;
    private final InventoryEventRepository inventoryRepository;
    private final QualityInspectionEventRepository qualityRepository;

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generate() {
        byte[] pdf = reportsService.generateFullReport();

        String fileName = "KIZUNA_CORE_DUMP_" + LocalDateTime.now().getNano() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/production-events")
    public ResponseEntity<List<ProductionEvent>> getProductionEvents() {
        return ResponseEntity.ok(productionRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")));
    }

    @GetMapping("/inventory-events")
    public ResponseEntity<List<InventoryEvent>> getInventoryEvents() {
        return ResponseEntity.ok(inventoryRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")));
    }

    @GetMapping("/quality-events")
    public ResponseEntity<List<QualityInspectionEvent>> getQualityEvents() {
        return ResponseEntity.ok(qualityRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")));
    }

}
