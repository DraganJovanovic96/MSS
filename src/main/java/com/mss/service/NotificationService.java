package com.mss.service;

import com.mss.dto.EmailCustomerDto;
import org.springframework.mail.MailException;

/**
 * NotificationService interface for sending notification emails and other types to customers.
 * The NotificationService interface contains methods that will be implemented is NotificationServiceImpl.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface NotificationService {

    /**
     * Sends an email to the customer notifying them that their service is complete and their vehicle is ready for pick-up.
     *
     * This method formats an email message with the customer's information and the details of the completed service,
     * and then sends the email to the specified recipient.
     *
     * @param emailCustomerDto the DTO containing the customer's email and service details, including the vehicle information.
     *                         This should include the customer's email address, vehicle details, and any additional service
     *                         information such as the service completion status or invoice.
     *
     * @throws MailException if there is an error while sending the email.
     */
    void sendServiceOverEmail(EmailCustomerDto emailCustomerDto);
}
