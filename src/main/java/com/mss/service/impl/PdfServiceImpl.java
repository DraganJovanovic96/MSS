package com.mss.service.impl;

import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
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

/**
 * Implementation of the PdfService interface for generating PDF documents.
 * This class provides the implementation of methods defined in the PdfService interface.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {
    /**
     * The repository used to retrieve service data.
     */
    private final ServiceRepository serviceRepository;

    /**
     * Creates an invoice PDF for the given service ID.
     *
     * @param serviceId the ID of the service for which the invoice is generated
     * @return a byte array containing the generated PDF document
     */
    @Override
    public byte[] createInvoicePdf(Long serviceId) {
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        Service service = serviceRepository.findOneById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service with that id doesn't exist"));
        Customer customer = service.getVehicle().getCustomer();
        List<ServiceType> serviceTypes = service.getServiceTypes();

        float totalPrice = 0;
        float threeCol = 190f;
        float twoCol = 285f;
        float twoCol150 = 285f + 150f;
        float twoColWidth[] = {twoCol, twoCol150};
        float fullWidth[] = {threeCol * 3};
        Border border = new SolidBorder(new DeviceGray(0.5f), 2);

        try {
            PdfWriter writer = new PdfWriter(dataStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDoc);

            Table table = new Table(twoColWidth);

            Cell titleCell = new Cell()
                    .add(new Paragraph("INVOICE")
                            .setFontSize(20))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBorder(Border.NO_BORDER);

            Paragraph invoiceDetails = new Paragraph()
                    .add("Invoice no: BZ30129\n").setFontSize(14)
                    .add("Invoice date: " + formatLocalDate(LocalDate.now())).setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT);

            table.addCell(titleCell);
            table.addCell(new Cell().add(invoiceDetails).setBorder(Border.NO_BORDER));

            Table divider = new Table(fullWidth);
            divider.setBorder(border);

            float[] columnWidths = {200F, 100F, 100F};
            Table table2 = new Table(columnWidths);
            table2.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()));
            table2.addHeaderCell(new Cell().add(new Paragraph("Type of service").setBold()));
            table2.addHeaderCell(new Cell().add(new Paragraph("Price in €").setBold()));

            for (ServiceType serviceType : serviceTypes) {
                table2.addCell(new Cell().add(new Paragraph(serviceType.getDescription())));
                table2.addCell(new Cell().add(new Paragraph(serviceType.getTypeOfService())));
                table2.addCell(new Cell().add(new Paragraph(String.valueOf(serviceType.getPrice()))));
                totalPrice += serviceType.getPrice();
            }

            document.add(table);
            document.add(divider);
            document.add(new Paragraph("\nBilling Information:"));
            document.add(new Paragraph("Name: " + customer.getFirstname() + " " + customer.getLastname()));
            document.add(new Paragraph("Address: " + customer.getAddress()));
            document.add(new Paragraph("Phone: " + customer.getPhoneNumber()));
            document.add(new Paragraph("\n"));
            document.add(table2);
            document.add(new Paragraph(String.valueOf(totalPrice)).setBold().setTextAlignment(TextAlignment.RIGHT));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataStream.toByteArray();
    }

    /**
     * Generates a filename for the invoice associated with the given service ID.
     * The filename is structured as "INVOICE_Firstname_Lastname.pdf".
     *
     * @param serviceId the ID of the service for which the invoice filename is generated
     * @return a string representing the generated invoice filename, including the customer's name
     * @throws ResponseStatusException if no service exists with the given ID
     */
    @Override
    public String generateInvoiceFilename(Long serviceId) {
        Service service = serviceRepository.findOneById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service with that id doesn't exist"));

        Customer customer = service.getVehicle().getCustomer();
        return "INVOICE_" + customer.getFirstname() + "_" + customer.getLastname() + ".pdf";
    }

    /**
     * Formats a LocalDate to a string in the format "dd. MMMM yyyy".
     * Example: "12. August 2024".
     *
     * @param date the LocalDate to format
     * @return the formatted date as a string
     */
    private String formatLocalDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy");
        return date.format(formatter);
    }
}
