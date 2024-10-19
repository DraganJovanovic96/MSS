package com.mss.service;

import org.springframework.web.server.ResponseStatusException;

/**
 * PdfService interface for generating PDF documents.
 * This service provides methods to create PDFs based on different criteria.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface PdfService {
    /**
     * Creates an invoice PDF for a given service ID.
     *
     * @param serviceId the ID of the service for which the invoice is generated
     * @return a byte array containing the generated PDF document
     */
    byte[] createInvoicePdf(Long serviceId);

    /**
     * Generates a filename for the invoice associated with the given service ID.
     *
     * @param serviceId the ID of the service for which the invoice filename is generated
     * @return a string representing the generated invoice filename, including the customer's name
     * @throws ResponseStatusException if no service exists with the given ID
     */
    String generateInvoiceFilename(Long serviceId);
}
