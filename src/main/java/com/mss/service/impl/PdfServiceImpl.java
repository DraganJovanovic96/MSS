package com.mss.service.impl;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
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
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Service implementation for generating PDF invoices.
 * This class provides methods to create, format, and populate invoice PDFs
 * based on service details, customer information, and associated service types.
 * It manages layout elements such as headers, tables, dividers, and footers
 * to produce a professional and structured document.
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
     * Creates a PDF invoice for a given service ID.
     *
     * @param serviceId the ID of the service to create an invoice for.
     * @return a byte array containing the generated PDF.
     */
    private PdfFont unicodeFont;

    @Override
    public byte[] createInvoicePdf(Long serviceId) {
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        Service service = serviceRepository.findOneById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service with that id doesn't exist"));
        Customer customer = service.getVehicle().getCustomer();
        List<ServiceType> allServiceTypes = service.getServiceTypes();
        List<ServiceType> serviceTypes = new ArrayList<>();

        for (ServiceType serviceType : allServiceTypes) {
            if (!serviceType.getDeleted()) {
                serviceTypes.add(serviceType);
            }
        }

        double totalPrice = calculateTotalPrice(serviceTypes);

        try {
            PdfWriter writer = new PdfWriter(dataStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDoc);

            // Load Unicode font
            this.unicodeFont = loadUnicodeFont();

            addHeader(document, totalPrice, service);
            addBillingInformation(document, customer);
            dividerWholeWidth(document);
            document.add(new Paragraph("Storitve").setFont(unicodeFont).setBold());
            addServiceTable(document, serviceTypes);
            dividerHalfWidth(document);
            addTotalPrice(document, totalPrice);
            dividerWholeWidth(document);
            document.add(new Paragraph("\n \n \n ").setFont(unicodeFont));
            grayLine(document);
            addFooter(document);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataStream.toByteArray();
    }

    private PdfFont loadUnicodeFont() throws IOException {
        try (InputStream fontStream = getClass().getResourceAsStream("/fonts/arial.ttf")) {
            if (fontStream == null) {
                throw new IOException("Font file not found in resources");
            }
            return PdfFontFactory.createFont(
                    fontStream.readAllBytes(),
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            );
        }
    }

    /**
     * Generates a filename for the invoice based on the service and customer details.
     *
     * @param serviceId the ID of the service.
     * @return a string representing the generated filename for the invoice.
     */
    @Override
    public String generateInvoiceFilename(Long serviceId) {
        Service service = serviceRepository.findOneById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service with that id doesn't exist"));

        Customer customer = service.getVehicle().getCustomer();
        return "INVOICE_" + customer.getFirstname() + "_" + customer.getLastname() + "_" + service.getInvoiceCode() + ".pdf";
    }

    /**
     * Formats a LocalDate object into a string with the format "dd. MMMM yyyy".
     *
     * @param date the date to format.
     * @return the formatted date string.
     */
    private String formatLocalDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy")
                .withLocale(Locale.forLanguageTag("sl-SI"));
        return date.format(formatter);
    }

    /**
     * Calculates the total price based on the list of service types.
     *
     * @param serviceTypes the list of service types.
     * @return the total price.
     */
    private double calculateTotalPrice(List<ServiceType> serviceTypes) {
        double total = 0;
        for (ServiceType serviceType : serviceTypes) {
            total += serviceType.getPrice() * serviceType.getQuantity();
        }
        return total;
    }

    /**
     * Adds the header section to the PDF document.
     *
     * @param document   the PDF document.
     * @param totalPrice the total price of the services.
     * @param service    the service object.
     */
    private void addHeader(Document document, double totalPrice, Service service) {
        float[] columnWidths = {190F, 190F, 190F};
        Table table = new Table(columnWidths);

        Cell titleCell = new Cell()
                .add(new Paragraph("RAČUN")
                        .setFont(unicodeFont)
                        .setFontSize(20))
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(20);

        Paragraph invoiceDetails = new Paragraph()
                .add(new Paragraph("Št. računa:").setFont(unicodeFont).setFontSize(14).setBold())
                .add(new Paragraph(service.getInvoiceCode()).setFont(unicodeFont).setFontSize(14))
                .add(new Paragraph("Datum: ").setFont(unicodeFont).setFontSize(14).setBold())
                .add(new Paragraph(formatLocalDate(LocalDate.now())).setFont(unicodeFont).setFontSize(14))
                .setTextAlignment(TextAlignment.RIGHT);

        table.addCell(titleCell);
        table.addCell(addLogo());
        table.addCell(new Cell().add(invoiceDetails).setBorder(Border.NO_BORDER));
        document.add(table);
        grayLine(document);
    }

    /**
     * Adds the logo to the header section.
     *
     * @return a cell containing the logo image.
     */
    private Cell addLogo() {
        try {
            ImageData imageData = ImageDataFactory.create(getClass().getResource("/images/mssLogo.png"));
            Image logo = new Image(imageData);
            logo.setWidth(85);
            logo.setHorizontalAlignment(HorizontalAlignment.LEFT);
            logo.setMarginLeft(30);
            return new Cell().add(logo).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
        } catch (Exception e) {
            e.printStackTrace();
            return new Cell().add(new Paragraph("Logo Error").setFont(unicodeFont));
        }
    }

    /**
     * Adds a gray line divider to the document.
     *
     * @param document the PDF document.
     */
    private void grayLine(Document document) {
        document.add(new Table(new float[]{190f * 3})
                .setBorder(new SolidBorder(new DeviceGray(0.5f), 2)));
    }

    /**
     * Adds a half-width divider to the document.
     *
     * @param document the PDF document.
     */
    private void dividerHalfWidth(Document document) {
        float[] twoSmallNumbers = {200f, 95f};
        Table dividerHalfWidth = new Table(twoSmallNumbers);
        Border dashedBorder = new DashedBorder(new DeviceGray(0.5f), 0.5f);
        dividerHalfWidth.setBorder(dashedBorder);
        dividerHalfWidth.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        document.add(dividerHalfWidth);
    }

    /**
     * Adds billing information to the document.
     *
     * @param document the PDF document.
     * @param customer the customer object containing billing information.
     */
    private void addBillingInformation(Document document, Customer customer) {
        document.add(new Paragraph("\nPodatki o zaračunavanju")
                .setFont(unicodeFont)
                .setBold());

        float[] columnWidths = {280f, 280f};
        Table table = new Table(columnWidths);

        table.addCell(new Cell().add(new Paragraph("Ime:").setFont(unicodeFont).setBold()).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Ime podjetja:").setFont(unicodeFont).setBold().setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(customer.getFirstname() + " " + customer.getLastname()).setFont(unicodeFont)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Hugo").setFont(unicodeFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(("Naslov:")).setFont(unicodeFont).setBold()).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Naslov podjetja:").setFont(unicodeFont).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        table.addCell(new Cell().add(new Paragraph(customer.getAddress()).setFont(unicodeFont)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Brez naslova").setFont(unicodeFont)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        table.addCell(new Cell().add(new Paragraph(("Telefon:")).setFont(unicodeFont).setBold()).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(("Telefon podjetja:")).setFont(unicodeFont).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        table.addCell(new Cell().add(new Paragraph(customer.getPhoneNumber()).setFont(unicodeFont)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("+386 (0)70 485 930").setFont(unicodeFont)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

        document.add(table);
    }

    /**
     * Adds a table of services to the document.
     *
     * @param document     the PDF document.
     * @param serviceTypes the list of service types to add to the table.
     */
    private void addServiceTable(Document document, List<ServiceType> serviceTypes) {
        float[] columnWidths = {142.5F, 142.5F, 142.5F, 142.5F};
        Table serviceTable = new Table(columnWidths);

        serviceTable.addHeaderCell(createHeaderCell("Opis"));
        serviceTable.addHeaderCell(createHeaderCell("Vrsta storitve"));
        serviceTable.addHeaderCell(createHeaderCell("Količina"));
        serviceTable.addHeaderCell(createHeaderCell("Cena v €").setTextAlignment(TextAlignment.RIGHT));

        for (ServiceType serviceType : serviceTypes) {
            serviceTable.addCell(createDataCell(serviceType.getDescription()));
            serviceTable.addCell(createDataCell(serviceType.getTypeOfService()));
            serviceTable.addCell(createDataCell(String.valueOf(serviceType.getQuantity())));
            serviceTable.addCell(createDataCell(String.valueOf(serviceType.getPrice())).setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(serviceTable);
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFont(unicodeFont))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(new DeviceGray(0f), 0.7f)
                .setFontColor(new DeviceGray(1.0f))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createDataCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFont(unicodeFont))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private void dividerWholeWidth(Document document) {
        float[] fullWidth = {190f * 3};
        Table divider2 = new Table(fullWidth);
        divider2.setBorder(new DashedBorder(new DeviceGray(0.5f), 0.5f));
        document.add(divider2);
    }

    /**
     * Adds the total price to the document.
     *
     * @param document   the PDF document.
     * @param totalPrice the total price of the services.
     */
    private void addTotalPrice(Document document, double totalPrice) {
        Paragraph totalPriceParagraph = new Paragraph(String.valueOf(totalPrice))
                .setFont(unicodeFont)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(14)
                .setBold();
        totalPriceParagraph.setMarginTop(10).setMarginBottom(10);
        document.add(totalPriceParagraph);
    }

    /**
     * Adds the footer to the document.
     *
     * @param document the PDF document.
     */
    private void addFooter(Document document) {
        document.add(new Paragraph("Ime: Darko Vasić").setFont(unicodeFont));
        document.add(new Paragraph("Telefon: +386 (0)70 485 930").setFont(unicodeFont));
    }
}