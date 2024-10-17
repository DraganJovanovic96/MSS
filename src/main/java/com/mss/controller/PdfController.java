package com.mss.controller;

import com.mss.dto.CustomerDto;
import com.mss.service.PdfService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling PDF download requests.
 * This controller provides an endpoint for generating and downloading PDF invoices
 * based on a given service ID.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/download-invoice")
@CrossOrigin
public class PdfController {
    /**
     * The service used to for PDF.
     */
    private final PdfService pdfService;

    /**
     * Endpoint for downloading a service invoice as a PDF.
     * This method generates a PDF document for the given service ID and returns it as a downloadable response.
     *
     * @param serviceId the ID of the service for which the invoice is generated
     * @return a ResponseEntity containing the generated PDF as a byte array
     */
    @GetMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get Service invoice")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service invoice successfully fetched.", response = CustomerDto.class),
            @ApiResponse(code = 404, message = "Service invoice doesn't exist.")
    })
    public ResponseEntity<byte[]> downloadInvoice(@Valid @PathVariable Long serviceId) {
        byte[] pdfBytes = pdfService.createInvoicePdf(serviceId);
        String filename = pdfService.generateInvoiceFilename(serviceId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
