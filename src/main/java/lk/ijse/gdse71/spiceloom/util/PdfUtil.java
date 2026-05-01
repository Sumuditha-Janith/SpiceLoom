package lk.ijse.gdse71.spiceloom.util;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import lk.ijse.gdse71.spiceloom.entity.Order;
import lk.ijse.gdse71.spiceloom.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class PdfUtil {

    public ByteArrayInputStream createOrderReceipt(Order order, List<OrderItem> items) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("SpiceLoom - Order Receipt")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Order ID: " + order.getOrderId()));
            document.add(new Paragraph("Date: " + order.getOrderDate().toString()));
            document.add(new Paragraph("Customer: " + order.getUser().getUsername()));
            document.add(new Paragraph("Status: " + order.getStatus()));
            document.add(new Paragraph("\n"));

            Table table = new Table(4);
            table.addHeaderCell("Product");
            table.addHeaderCell("Unit Price (Rs.)");
            table.addHeaderCell("Quantity");
            table.addHeaderCell("Subtotal (Rs.)");

            for (OrderItem item : items) {
                table.addCell(item.getProduct().getName());
                table.addCell(String.format("%.2f", item.getPriceAtTime()));
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.format("%.2f", item.getPriceAtTime() * item.getQuantity()));
            }
            document.add(table);

            document.add(new Paragraph("\nTotal: Rs. " + String.format("%.2f", order.getTotalAmount())).setBold());
            document.add(new Paragraph("\nThank you for your purchase!"));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream createSalesReport(List<Order> orders) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("SpiceLoom - Sales Report")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            Table table = new Table(5);
            table.addHeaderCell("Order ID");
            table.addHeaderCell("Date");
            table.addHeaderCell("Customer");
            table.addHeaderCell("Total (Rs.)");
            table.addHeaderCell("Status");

            double grandTotal = 0;
            for (Order order : orders) {
                table.addCell(String.valueOf(order.getOrderId()));
                table.addCell(order.getOrderDate().toLocalDate().toString());
                table.addCell(order.getUser().getUsername());
                table.addCell(String.format("%.2f", order.getTotalAmount()));
                table.addCell(order.getStatus().name());
                grandTotal += order.getTotalAmount();
            }
            document.add(table);
            document.add(new Paragraph("\nGrand Total Sales: Rs. " + String.format("%.2f", grandTotal)).setBold());

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating report", e);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}