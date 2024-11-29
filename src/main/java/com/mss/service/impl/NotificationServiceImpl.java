package com.mss.service.impl;

import com.mss.dto.EmailCustomerDto;
import com.mss.service.NotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

/**
 * The NotificationServiceImpl implements NotificationService and
 * all methods that are in NotificationRepository.
 * Dependency injection was used to get beans of VehicleRepository and VehicleMapper.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    /**
     * The Service used to send email.
     */
    private final EmailServiceImpl emailService;

    /**
     * Sends an email to the customer notifying them that their service is complete and their vehicle is ready for pick-up.
     * <p>
     * This method formats an email message with the customer's information and the details of the completed service,
     * and then sends the email to the specified recipient.
     *
     * @param emailCustomerDto the DTO containing the customer's email and service details, including the vehicle information.
     *                         This should include the customer's email address, vehicle details, and any additional service
     *                         information such as the service completion status or invoice.
     * @throws MailException      if there is an error while sending the email.
     */
    @Override
    public void sendServiceOverEmail(EmailCustomerDto emailCustomerDto) {
        String subject = "Your Vehicle Service is Complete - Pick Up Ready";

        String htmlMessage = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Service Completion Notification</title>
                  <style>
                    body {
                      font-family: Arial, sans-serif;
                      margin: 0;
                      padding: 0;
                      background-color: #f4f4f9;
                    }
                    .email-container {
                      max-width: 600px;
                      margin: 20px auto;
                      background: #ffffff;
                      border: 1px solid #dddddd;
                      border-radius: 8px;
                      overflow: hidden;
                      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }
                    .email-header {
                      background-color: #2c2b29;
                      padding: 20px;
                      text-align: center;
                    }
                    .email-header img {
                      max-width: 150px;
                    }
                    .email-body {
                      padding: 20px;
                    }
                    .email-body h1 {
                      color: #333333;
                      font-size: 24px;
                    }
                    .email-body p {
                      color: #555555;
                      font-size: 16px;
                      line-height: 1.5;
                    }
                     .info-section {
                                background-color: #f4f7f9;
                                padding: 15px;
                                margin-top: 20px;
                                border-radius: 8px;
                                box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
                              }
                              .info-section p {
                                font-size: 16px;
                                color: #333333;
                                line-height: 1.5;
                                margin: 10px 0;
                              }
                              .info-section .info-title {
                                font-weight: bold;
                                color: #4e5d6d;
                              }
                
                    .vehicle-info {
                      display: flex;
                      flex-direction: column;
                      margin: 20px 0;
                      padding: 15px;
                      background-color: #e9ecef;
                      border: 1px dashed #6c757d;
                      font-size: 18px;
                      color: #004085;
                      text-align: left;
                    }
                    .email-footer {
                      padding: 20px;
                      text-align: center;
                      background-color: #f8f9fa;
                      color: #6c757d;
                      font-size: 14px;
                    }
                    .email-footer a {
                      color: #007bff;
                      text-decoration: none;
                    }
                    .email-footer a:hover {
                      text-decoration: underline;
                    }
                  </style>
                </head>
                <body>
                  <div class="email-container">
                    <div class="email-header">
                      <img src="https://i.imghippo.com/files/pQi9349bTk.png" alt="Logo">
                    </div>
                    <div class="email-body">
                      <h1>Your Vehicle Service is Complete!</h1>
                      <p>We are happy to inform you that your vehicle service has been completed successfully. You can now pick up your vehicle.</p>
                
                    <div class="info-section">
                                <p><span class="info-title">Customer Name:</span> %s</p>
                                <p><span class="info-title">Vehicle:</span> %s</p>
                                <p><span class="info-title">Invoice Code:</span> %s</p>
                    </div>
                
                      <p>If you have any questions or need further assistance, feel free to contact us. Otherwise, feel free to visit us and pick up your car at your convenience.</p>
                    </div>
                    <div class="email-footer">
                      <p>Need help? Contact us at <a href="mailto:support@mss.com">support@mss.com</a></p>
                      <p>&copy; 2024 MSS. All rights reserved.</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(emailCustomerDto.getCustomerName(), emailCustomerDto.getVehicleManufacturerAndModel(), emailCustomerDto.getInvoiceCode());

        try {
            emailService.sendVerificationEmail(emailCustomerDto.getCustomerEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
