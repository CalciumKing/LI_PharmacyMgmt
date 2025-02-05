package com.example.li_pharmacy;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.*;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public class GeneratePDF {
    private final static String imgSrc = "bin/images/OCLogo.png";
    public static void main(String[] args) {
        try {
            PdfWriter writer = new PdfWriter("src/main/resources/com/example/li_pharmacy/mypdf.pdf");
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            ImageData data = ImageDataFactory.create(imgSrc);
            
            Table header = new Table(2)
                    .setWidth(UnitValue.createPercentValue(100))
                    .addCell(
                            new Cell()
                                    .setBorder(Border.NO_BORDER)
                                    .add(
                                            new Image(data)
                                                    .setHeight(80)
                                                    .setWidth(80)
                                    )
                    )
                    .addCell(
                            new Cell()
                                    .setBorder(Border.NO_BORDER)
                                    .setTextAlignment(TextAlignment.CENTER)
                                    .add(
                                            new Paragraph("Invoice Report")
                                                    .setFontSize(25)
                                                    .setBold()
                                    )
                                    .add(new Paragraph("476 North Ave, Rochester, MA 02770"))
                                    .add(new Paragraph("(508) 763-8011"))
                                    .add(new Paragraph("ingerslevlk@oldcolony.info"))
                    );
            
            Table table = new Table(4)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMargins(50, 0, 50, 0)
                    .setWidth(UnitValue.createPercentValue(100))
                    
                    .addCell(createParagraphCell("Name"))
                    .addCell(createParagraphCell("Unit Price"))
                    .addCell(createParagraphCell("Quantity"))
                    .addCell(createParagraphCell("Total Price"))
                    
                    .addCell(createParagraphCell("").setHeight(25))
                    .addCell(createParagraphCell(""))
                    .addCell(createParagraphCell(""))
                    .addCell(createParagraphCell(""));
            
            Paragraph subtotal = new Paragraph("Subtotal:"),
                    tax = new Paragraph("Tax:"),
                    total = new Paragraph("Grand Total:"),
                    amount = new Paragraph("Amount Paid:"),
                    change = new Paragraph("Change:");
            
            document.add(header)
                    .add(table)
                    .add(subtotal)
                    .add(tax)
                    .add(total)
                    .add(amount)
                    .add(change)
                    .close();
        } catch (FileNotFoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    private static Cell createParagraphCell(String text) {
        return new Cell()
                .add(
                        new Paragraph(text)
                                .setFontSize(15)
                                .setBold()
                );
    }
}