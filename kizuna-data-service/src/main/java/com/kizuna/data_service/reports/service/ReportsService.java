package com.kizuna.data_service.reports.service;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.kizuna.data_service.domain.*;
import com.kizuna.data_service.metrics.service.DashboardService;
import com.kizuna.data_service.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class ReportsService {

    private final DashboardService dashboardService;
    private final ProductionEventRepository productionRepository;
    private final InventoryEventRepository inventoryRepository;
    private final QualityInspectionEventRepository qualityRepository;

    public byte[] generateFullReport(LocalDateTime start, LocalDateTime end) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // =========================
            // HEADER
            // =========================
            document.add(new Paragraph("KIZUNA - RELATÓRIO OPERACIONAL")
                    .setBold()
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Período: " + start + " até " + end)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            // =========================
            // RESUMO (DASHBOARD)
            // =========================
            document.add(new Paragraph("Resumo Geral").setBold());

            var production = dashboardService.getProductionMetrics();
            var inventory = dashboardService.getInventoryMetrics();
            var quality = dashboardService.getQualityMetrics();

            document.add(new Paragraph(
                    "Produção → Total: " + production.totalOrders() +
                            " | Iniciadas: " + production.StartedOrders() +
                            " | Finalizadas: " + production.finishedOrders()
            ));

            document.add(new Paragraph(
                    "Inventário → Total Itens: " + inventory.totalItems() +
                            " | Estoque Baixo: " + inventory.lowStockItems()
            ));

            document.add(new Paragraph(
                    "Qualidade → Aprovados: " + quality.aprovedOrders() +
                            " | Rejeitados: " + quality.rejectedOrders()
            ));

            document.add(new Paragraph("\n"));

            // =========================
            // PRODUÇÃO DETALHADA
            // =========================
            document.add(new Paragraph("Ordens de Produção").setBold());

            List<ProductionEvent> productions =
                    productionRepository.findByTimestampBetween(start, end);

            Table productionTable = new Table(4);
            productionTable.addCell("Ordem");
            productionTable.addCell("Receita");
            productionTable.addCell("Tipo");
            productionTable.addCell("Data");

            for (ProductionEvent p : productions) {
                productionTable.addCell(String.valueOf(p.getOrderId()));
                productionTable.addCell(p.getRecipeName());
                productionTable.addCell(p.getType());
                productionTable.addCell(p.getTimestamp().toString());
            }

            document.add(productionTable);
            document.add(new Paragraph("\n"));

            // =========================
            // INVENTÁRIO
            // =========================
            document.add(new Paragraph("Movimentações de Inventário").setBold());

            List<InventoryEvent> inventoryEvents =
                    inventoryRepository.findByTimestampBetween(start, end);

            Table inventoryTable = new Table(4);
            inventoryTable.addCell("Item");
            inventoryTable.addCell("Quantidade");
            inventoryTable.addCell("Tipo");
            inventoryTable.addCell("Data");

            for (InventoryEvent i : inventoryEvents) {
                inventoryTable.addCell(i.getInventoryName());
                inventoryTable.addCell(String.valueOf(i.getQuantity()));
                inventoryTable.addCell(i.getType());
                inventoryTable.addCell(i.getTimestamp().toString());
            }

            document.add(inventoryTable);
            document.add(new Paragraph("\n"));

            // =========================
            // QUALIDADE
            // =========================
            document.add(new Paragraph("Inspeções de Qualidade").setBold());

            List<QualityInspectionEvent> inspections =
                    qualityRepository.findByTimestampBetween(start, end);

            Table qualityTable = new Table(4);
            qualityTable.addCell("Ordem");
            qualityTable.addCell("Produto");
            qualityTable.addCell("Resultado");
            qualityTable.addCell("Data");

            for (QualityInspectionEvent q : inspections) {
                qualityTable.addCell(String.valueOf(q.getOrderId()));
                qualityTable.addCell(q.getProductName());
                qualityTable.addCell(q.getResult());
                qualityTable.addCell(q.getTimestamp().toString());
            }

            document.add(qualityTable);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório", e);
        }

        return out.toByteArray();
    }
}
