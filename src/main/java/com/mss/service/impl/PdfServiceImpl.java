package com.mss.service.impl;

import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.mss.model.Customer;
import com.mss.model.Service;
import com.mss.model.ServiceType;
import com.mss.repository.ServiceRepository;
import com.mss.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {
    private final ServiceRepository serviceRepository;

    @Override
    public byte[] createInvoicePdf(Long serviceId) {
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        Service service = serviceRepository.findOneById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service with that id doesn't exist"));
        Customer customer = service.getVehicle().getCustomer();
        List<ServiceType> serviceTypes = service.getServiceTypes();

        float totalPrice = calculateTotalPrice(serviceTypes);

        try {
            PdfWriter writer = new PdfWriter(dataStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDoc);

            addHeader(document, totalPrice);
            addBillingInformation(document, customer);
            dividerWholeWidth(document);
            document.add(new Paragraph("Services").setBold());
            addServiceTable(document, serviceTypes);
            dividerHalfWidth(document);
            addTotalPrice(document, totalPrice);
            dividerWholeWidth(document);
            document.add(new Paragraph("\n \n \n "));
            grayLine(document);
            addFooter(document);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataStream.toByteArray();
    }

    @Override
    public String generateInvoiceFilename(Long serviceId) {
        Service service = serviceRepository.findOneById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service with that id doesn't exist"));

        Customer customer = service.getVehicle().getCustomer();
        return "INVOICE_" + customer.getFirstname() + "_" + customer.getLastname() + ".pdf";
    }

    private String formatLocalDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy");
        return date.format(formatter);
    }

    private float calculateTotalPrice(List<ServiceType> serviceTypes) {
        float total = 0;
        for (ServiceType serviceType : serviceTypes) {
            total += serviceType.getPrice();
        }
        return total;
    }

    private void addHeader(Document document, float totalPrice) {
        float twoCol = 285f;
        float twoCol150 = 285f + 150f;
        float[] twoColWidth = {twoCol, twoCol150};


        Table table = new Table(twoColWidth);
        Cell titleCell = new Cell()
                .add(new Paragraph("INVOICE")
                        .setFontSize(20))
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER);

        Paragraph invoiceDetails = new Paragraph()
                .add(new Paragraph("Invoice no:").setFontSize(14).setBold())
                .add(" BZ30129" + "\n").setFontSize(14)
                .add(new Paragraph("Date: ").setFontSize(14).setBold())
                .add(new Paragraph(formatLocalDate(LocalDate.now())).setFontSize(14))
                .setTextAlignment(TextAlignment.RIGHT);

        table.addCell(titleCell);
        table.addCell(new Cell().add(invoiceDetails).setBorder(Border.NO_BORDER));
        document.add(table);
        grayLine(document);
    }

    private void grayLine(Document document) {
        document.add(new Table(new float[]{190f * 3}).setBorder(new SolidBorder(new DeviceGray(0.5f), 2)));
    }

    private void dividerHalfWidth(Document document) {
        float[] twoSmallNumbers = {200f, 95f};
        Table dividerHalfWidth = new Table(twoSmallNumbers);
        Border dashedBorder = new DashedBorder(new DeviceGray(0.5f), 0.5f);
        dividerHalfWidth.setBorder(dashedBorder);
        dividerHalfWidth.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        document.add(dividerHalfWidth);
    }

    private void addBillingInformation(Document document, Customer customer) {
        document.add(new Paragraph("\nBilling Information:").setBold());
        document.add(new Paragraph("Name: \n").setBold());
        document.add(new Paragraph(customer.getFirstname() + " " + customer.getLastname()));
        document.add(new Paragraph("Address: \n").setBold());
        document.add(new Paragraph(customer.getAddress()));
        document.add(new Paragraph("Phone: \n").setBold());
        document.add(new Paragraph(customer.getPhoneNumber()));
    }

    private void addServiceTable(Document document, List<ServiceType> serviceTypes) {
        float[] columnWidths = {190F, 190F, 190F};
        Table serviceTable = new Table(columnWidths);
        serviceTable.addHeaderCell(new Cell()
                .add(new Paragraph("Description")
                        .setTextAlignment(TextAlignment.LEFT))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(new DeviceGray(0f), 0.7f)
                .setFontColor(new DeviceGray(1.0f)));
        serviceTable.addHeaderCell(new Cell()
                .add(new Paragraph("Type of service")
                        .setTextAlignment(TextAlignment.CENTER))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(new DeviceGray(0f), 0.7f)
                .setFontColor(new DeviceGray(1.0f)));
        serviceTable.addHeaderCell(new Cell()
                .add(new Paragraph("Price in €")
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(new DeviceGray(0f), 0.7f)
                .setFontColor(new DeviceGray(1.0f)));

        for (ServiceType serviceType : serviceTypes) {
            serviceTable.addCell(new Cell().add(new Paragraph(serviceType.getDescription())).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            serviceTable.addCell(new Cell().add(new Paragraph(serviceType.getTypeOfService())).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            serviceTable.addCell(new Cell().add(new Paragraph(String.valueOf(serviceType.getPrice()))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(serviceTable);
    }

    private void dividerWholeWidth(Document document) {
        float threeCol = 190f;
        float[] fullWidth = {threeCol * 3};
        Table divider2 = new Table(fullWidth);
        Border dashedBorder = new DashedBorder(new DeviceGray(0.5f), 0.5f);
        divider2.setBorder(dashedBorder);
        document.add(divider2);
    }

    private void addTotalPrice(Document document, float totalPrice) {
        Paragraph totalPriceParagraph = new Paragraph(String.valueOf(totalPrice))
                .setTextAlignment(TextAlignment.RIGHT) // Align text to the right
                .setFontSize(14) // Set font size, adjust as needed
                .setBold(); // Make the text bold if desired

        // Add some spacing if needed
        totalPriceParagraph.setMarginTop(10); // Adjust margin as needed
        totalPriceParagraph.setMarginBottom(10); // Adjust margin as needed

        document.add(totalPriceParagraph);
    }

    private void addFooter(Document document) {
        // Calculate available space before adding the footer
        float availableHeight = document.getPdfDocument().getDefaultPageSize().getHeight()
                - document.getBottomMargin()
                - document.getTopMargin()
                - document.getRenderer().getCurrentArea().getBBox().getY();

        float footerHeight = 720f; // Adjust this value based on your footer's height

        // Check if adding the footer will exceed the page's height
        if (availableHeight < footerHeight) {
            document.add(new AreaBreak());
        }

        document.add(new Paragraph("If you have any question about this invoice please contact: \n")
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(2));

        document.add(new Paragraph("Name: Darko")
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(2));

        document.add(new Paragraph("Phone: 0618367218")
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(2));

        document.add(new Paragraph("E-mail: darkoneki@gmail.com")
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(2));
    }
}
