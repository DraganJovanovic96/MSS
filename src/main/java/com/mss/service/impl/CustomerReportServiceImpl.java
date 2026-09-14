package com.mss.service.impl;

import com.mss.dto.CustomerReportCreateDto;
import com.mss.dto.CustomerReportDto;
import com.mss.dto.CustomerReportUpdateDto;
import com.mss.enumeration.CustomerReportStatus;
import com.mss.enumeration.Role;
import com.mss.mapper.CustomerReportMapper;
import com.mss.model.Customer;
import com.mss.model.CustomerReport;
import com.mss.model.User;
import com.mss.model.Vehicle;
import com.mss.repository.CustomerReportRepository;
import com.mss.repository.CustomerRepository;
import com.mss.repository.ServiceRepository;
import com.mss.repository.UserRepository;
import com.mss.repository.VehicleRepository;
import com.mss.service.CustomerReportService;
import com.mss.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

/**
 * Implementation of the CustomerReportService interface.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomerReportServiceImpl implements CustomerReportService {

    private final CustomerReportRepository customerReportRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final UserService userService;
    private final CustomerReportMapper customerReportMapper;
    private final EntityManager entityManager;

    /**
     * Created CUSTOMER_REPORT_FILTER attribute, so we can change Filter easily if needed.
     */
    private static final String CUSTOMER_REPORT_FILTER = "deletedCustomerReportFilter";

    @Override
    @Transactional
    public CustomerReportDto createCustomerReport(CustomerReportCreateDto customerReportCreateDto) {
        User createdBy = userService.getUserFromAuthentication();

        Customer customer = customerRepository.findById(customerReportCreateDto.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        Vehicle vehicle = vehicleRepository.findById(customerReportCreateDto.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        CustomerReport customerReport = CustomerReport.builder()
                .customer(customer)
                .vehicle(vehicle)
                .createdBy(createdBy)
                .issueDescription(customerReportCreateDto.getIssueDescription())
                .status(CustomerReportStatus.PENDING)
                .build();

        CustomerReport savedReport = customerReportRepository.save(customerReport);
        return customerReportMapper.customerReportToCustomerReportDto(savedReport);
    }

    @Override
    public Page<CustomerReportDto> getMyCustomerReports(int page, int size) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(CUSTOMER_REPORT_FILTER);
        filter.setParameter("isDeleted", false);

        User currentUser = userService.getUserFromAuthentication();
        Page<CustomerReport> reports = customerReportRepository.findByCreatedByIdOrderByCreatedAtDesc(
            currentUser.getId(), 
            PageRequest.of(page, size)
        );

        session.disableFilter(CUSTOMER_REPORT_FILTER);

        List<CustomerReportDto> dtos = customerReportMapper.customerReportsToCustomerReportDtos(reports.getContent());
        return new PageImpl<>(dtos, reports.getPageable(), reports.getTotalElements());
    }

    @Override
    public Page<CustomerReportDto> getAllCustomerReports(int page, int size) {
        User currentUser = userService.getUserFromAuthentication();
        if (currentUser.getRole() != Role.ADMIN && 
            currentUser.getRole() != Role.RECEPTIONIST && 
            currentUser.getRole() != Role.MECHANIC) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins, receptionists, and mechanics can view all customer reports");
        }

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(CUSTOMER_REPORT_FILTER);
        filter.setParameter("isDeleted", false);

        Page<CustomerReport> reports = customerReportRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));

        session.disableFilter(CUSTOMER_REPORT_FILTER);

        List<CustomerReportDto> dtos = customerReportMapper.customerReportsToCustomerReportDtos(reports.getContent());
        return new PageImpl<>(dtos, reports.getPageable(), reports.getTotalElements());
    }

    @Override
    public Page<CustomerReportDto> getPendingCustomerReports(int page, int size) {
        User currentUser = userService.getUserFromAuthentication();
        if (currentUser.getRole() != Role.ADMIN && 
            currentUser.getRole() != Role.RECEPTIONIST && 
            currentUser.getRole() != Role.MECHANIC) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins, receptionists, and mechanics can view pending customer reports");
        }

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(CUSTOMER_REPORT_FILTER);
        filter.setParameter("isDeleted", false);

        Page<CustomerReport> reports = customerReportRepository.findByStatusOrderByCreatedAtDesc(
            CustomerReportStatus.PENDING, 
            PageRequest.of(page, size)
        );

        session.disableFilter(CUSTOMER_REPORT_FILTER);

        List<CustomerReportDto> dtos = customerReportMapper.customerReportsToCustomerReportDtos(reports.getContent());
        return new PageImpl<>(dtos, reports.getPageable(), reports.getTotalElements());
    }

    @Override
    public CustomerReportDto getCustomerReportById(Long reportId) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(CUSTOMER_REPORT_FILTER);
        filter.setParameter("isDeleted", false);

        CustomerReport report = customerReportRepository.findOneById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer report not found"));

        session.disableFilter(CUSTOMER_REPORT_FILTER);

        User currentUser = userService.getUserFromAuthentication();
        if (currentUser.getRole() != Role.ADMIN && 
            currentUser.getRole() != Role.RECEPTIONIST && 
            currentUser.getRole() != Role.MECHANIC &&
            !report.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own customer reports");
        }

        return customerReportMapper.customerReportToCustomerReportDto(report);
    }

    @Override
    @Transactional
    public CustomerReportDto updateCustomerReport(Long reportId, CustomerReportUpdateDto customerReportUpdateDto) {
        User currentUser = userService.getUserFromAuthentication();
        if (currentUser.getRole() != Role.ADMIN && 
            currentUser.getRole() != Role.RECEPTIONIST ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins, receptionists, and mechanics can update customer reports");
        }

        CustomerReport report = customerReportRepository.findOneById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer report not found"));

        if (customerReportUpdateDto.getStatus() != null) {
            report.setStatus(customerReportUpdateDto.getStatus());

            if (customerReportUpdateDto.getStatus() == CustomerReportStatus.ASSIGNED) {
                report.setAssignedAt(Instant.now());
            }
        }

        if (customerReportUpdateDto.getUserId() != null) {
            User assignedTo = userRepository.findById(customerReportUpdateDto.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            report.setUser(assignedTo);
        }

        if (customerReportUpdateDto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(customerReportUpdateDto.getCustomerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
            report.setCustomer(customer);
        }

        if (customerReportUpdateDto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(customerReportUpdateDto.getVehicleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
            report.setVehicle(vehicle);
        }

        if (customerReportUpdateDto.getServiceId() != null) {
            com.mss.model.Service service = serviceRepository.findById(customerReportUpdateDto.getServiceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
            report.setService(service);
            service.setCustomerReport(report);
            serviceRepository.save(service);
        }

        if (customerReportUpdateDto.getIssueDescription() != null) {
            report.setIssueDescription(customerReportUpdateDto.getIssueDescription());
        }

        CustomerReport updatedReport = customerReportRepository.save(report);
        return customerReportMapper.customerReportToCustomerReportDto(updatedReport);
    }

    @Override
    @Transactional
    public void deleteCustomerReport(Long reportId) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(CUSTOMER_REPORT_FILTER);
        filter.setParameter("isDeleted", false);

        CustomerReport report = customerReportRepository.findOneById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer report not found"));

        session.disableFilter(CUSTOMER_REPORT_FILTER);

        User currentUser = userService.getUserFromAuthentication();
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.RECEPTIONIST) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete customer reports");
        }
        report.setDeletedAt(Instant.now());
        userRepository.flush();
        customerReportRepository.delete(report);
    }

    @Override
    @Transactional
    public void updatePhotoUrls(Long reportId, String photoUrls) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(CUSTOMER_REPORT_FILTER);
        filter.setParameter("isDeleted", false);

        CustomerReport report = customerReportRepository.findOneById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer report not found"));

        session.disableFilter(CUSTOMER_REPORT_FILTER);

        report.setPhotoUrls(photoUrls);
        customerReportRepository.save(report);
    }
}
