package com.kizuna.data_service.reports.controller;

import com.kizuna.data_service.reports.service.ReportsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportsService reportsService;

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generate(
            @RequestParam String start,
            @RequestParam String end
    ) {

        byte[] pdf = reportsService.generateFullReport(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
