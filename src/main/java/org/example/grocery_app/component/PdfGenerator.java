package org.example.grocery_app.component;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.example.grocery_app.dto.HisabBookDto;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

@Component
public class PdfGenerator {

    public void generatePriceSettlementReport(LocalDate date, List<HisabBookDto> settlements, String outputPath) throws Exception {
        // Step 1: Set up PDF writer and document
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputPath));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Step 2: Add title
        document.add(new Paragraph("Price Settlement Report for: " + date).setBold().setFontSize(14));

        // Step 3: Define table columns (8 columns)
        float[] columnWidths = {120, 60, 80, 80, 80, 80, 200, 200};
        Table table = new Table(columnWidths);

        // Step 4: Table headers
        table.addCell("Product Name");
        table.addCell("Qty");
        table.addCell("Product Price");
        table.addCell("Shopkeeper Price");
        table.addCell("Total Price");
        table.addCell("Profit");
        table.addCell("Shopkeeper payment received");
        table.addCell("Admin pay the payment");

        // Step 5: Add rows for each settlement
        for (HisabBookDto ps : settlements) {
            table.addCell(ps.getProductName());
            table.addCell(String.valueOf(ps.getProductQuantity()));
            table.addCell(String.valueOf(ps.getProductPrice()));
            table.addCell(String.valueOf(ps.getShopkeeperPrice()));
            table.addCell(String.valueOf(ps.getTotalPrice()));
            table.addCell(String.valueOf(ps.getGetProfit()));
            table.addCell(ps.isPaidToShopkeeper() ? "PAID" : "NOT PAID");
            table.addCell(ps.isPaymentDoneByAdmin() ? "PAID" : "NO");
        }

        // Step 6: Add table to document and close
        document.add(table);
        document.close();
    }
}
