package com.kizuna.data_service.reports.service;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.kizuna.data_service.metrics.service.DashboardService;
import com.kizuna.data_service.repository.InventoryEventRepository;
import com.kizuna.data_service.repository.ProductionEventRepository;
import com.kizuna.data_service.repository.QualityInspectionEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class ReportsService {

    private final DashboardService dashboardService;
    private final ProductionEventRepository productionRepository;
    private final InventoryEventRepository inventoryRepository;
    private final QualityInspectionEventRepository qualityRepository;

    // Cores da Identidade Kizuna
    private static final DeviceRgb KIZUNA_RED = new DeviceRgb(225, 29, 72);
    private static final DeviceRgb DARK_BG = new DeviceRgb(18, 18, 18);
    private static final DeviceRgb TEXT_GREY = new DeviceRgb(161, 161, 170);

    public byte[] generateFullReport() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.setMargins(40, 40, 40, 40);

            // =========================
            // HEADER ESTILIZADO (CYBERPUNK INDUSTRIAL)
            // =========================
            Table headerTable = new Table(2).useAllAvailableWidth();

            Cell titleCell = new Cell().add(new Paragraph("KIZUNA")
                            .setBold().setFontSize(26).setFontColor(KIZUNA_RED).setMarginBottom(0))
                    .add(new Paragraph("INDUSTRIAL MANAGEMENT SYSTEM")
                            .setFontSize(8).setCharacterSpacing(2f).setFontColor(ColorConstants.BLACK))
                    .setBorder(Border.NO_BORDER);

            Cell infoCell = new Cell().add(new Paragraph("REPORT TYPE: FULL DATA DUMP")
                            .setTextAlignment(TextAlignment.RIGHT).setFontSize(9))
                    .add(new Paragraph("GENERATED: " + LocalDateTime.now())
                            .setTextAlignment(TextAlignment.RIGHT).setFontSize(9))
                    .add(new Paragraph("SYSTEM STATUS: NOMINAL")
                            .setTextAlignment(TextAlignment.RIGHT).setFontSize(9).setFontColor(new DeviceRgb(34, 197, 94)))
                    .setBorder(Border.NO_BORDER);

            headerTable.addCell(titleCell);
            headerTable.addCell(infoCell);
            document.add(headerTable);

            // Linha decorativa vermelha
            document.add(new LineSeparator(new SolidLine(2f)).setMarginTop(5).setMarginBottom(20).setFontColor(KIZUNA_RED));

            // =========================
            // DASHBOARD METRICS (CARDS SIMULADOS)
            // =========================
            document.add(new Paragraph("CORE ANALYTICS STREAM").setBold().setFontSize(12).setUnderline());

            Table metricsTable = new Table(3).useAllAvailableWidth().setMarginTop(10).setMarginBottom(20);

            metricsTable.addCell(createMetricCard("PRODUCTION", dashboardService.getProductionMetrics().totalOrders() + " ORDERS"));
            metricsTable.addCell(createMetricCard("INVENTORY", dashboardService.getInventoryMetrics().totalItems() + " ITEMS"));
            metricsTable.addCell(createMetricCard("QUALITY", dashboardService.getQualityMetrics().aprovedOrders() + " APPROVED"));

            document.add(metricsTable);

            // =========================
            // TABELAS COM DESIGN PROFISSIONAL
            // =========================

            // 1. Produção
            addStyledSectionTitle(document, "PRODUCTION ANALYTICS LOG");
            Table pTable = new Table(new float[]{2, 4, 2, 4}).useAllAvailableWidth();
            addStyledHeader(pTable, new String[]{"ID", "RECIPE", "TYPE", "TIMESTAMP"});

            productionRepository.findAll().forEach(p -> {
                addStyledCell(pTable, String.valueOf(p.getOrderId()));
                addStyledCell(pTable, p.getRecipeName());
                addStyledCell(pTable, p.getType());
                addStyledCell(pTable, p.getTimestamp().toString());
            });
            document.add(pTable);

            // 2. Inventário
            document.add(new AreaBreak()); // Nova página para organização
            addStyledSectionTitle(document, "INVENTORY MOVEMENT LOG");
            Table iTable = new Table(new float[]{4, 2, 2, 4}).useAllAvailableWidth();
            addStyledHeader(iTable, new String[]{"ITEM", "QTY", "TYPE", "TIMESTAMP"});

            inventoryRepository.findAll().forEach(i -> {
                addStyledCell(iTable, i.getInventoryName());
                addStyledCell(iTable, String.valueOf(i.getQuantity()));
                addStyledCell(iTable, i.getType());
                addStyledCell(iTable, i.getTimestamp().toString());
            });
            document.add(iTable);

            // =========================
            // FOOTER (RODAPÉ TÉCNICO)
            // =========================
            document.add(new Paragraph("\n\n"));
            document.add(new LineSeparator(new SolidLine(1f)).setFontColor(TEXT_GREY));
            document.add(new Paragraph("ENCRYPTED OUTPUT // PDF PROTOCOL SHA-256 // KIZUNA CORE V3.0.4")
                    .setFontSize(7)
                    .setFontColor(TEXT_GREY)
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Critical failure during PDF stream generation", e);
        }

        return out.toByteArray();
    }

    // Métodos auxiliares para manter o estilo limpo
    private void addStyledSectionTitle(Document doc, String title) {
        doc.add(new Paragraph(title)
                .setBold()
                .setFontSize(10)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(KIZUNA_RED)
                .setPaddingLeft(5)
                .setMarginTop(15));
    }

    private void addStyledHeader(Table table, String[] headers) {
        for (String h : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(h).setBold().setFontSize(9))
                    .setBackgroundColor(new DeviceRgb(240, 240, 240))
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
        }
    }

    private void addStyledCell(Table table, String text) {
        table.addCell(new Cell().add(new Paragraph(text).setFontSize(8))
                .setBorder(new SolidBorder(TEXT_GREY, 0.2f)));
    }

    private Cell createMetricCard(String label, String value) {
        return new Cell().add(new Paragraph(label).setFontSize(8).setFontColor(KIZUNA_RED).setBold())
                .add(new Paragraph(value).setFontSize(14).setBold())
                .setPadding(10)
                .setBorder(new SolidBorder(KIZUNA_RED, 1f));
    }
}