package com.mss.service.impl;

import com.mss.model.Customer;
import com.mss.model.Service;
import com.mss.model.ServiceType;
import com.mss.model.Vehicle;
import com.mss.repository.*;
import com.mss.service.PermanentDeletionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * The {@code PermanentlyDeleteServiceImpl} class implements the {@link PermanentDeletionService} interface,
 * providing functionality to permanently delete softly deleted resources from the database.
 * Dependency injection is used to obtain instances of the necessary repository beans for handling
 * different entity types (Vehicle, User, Customer, Service, and ServiceType).
 * <p>
 * This service runs a scheduled job to periodically remove all resources marked as deleted.
 * </p>
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class PermanentlyDeleteServiceImpl implements PermanentDeletionService {
    /**
     * The repository used to retrieve vehicle data.
     */
    private final VehicleRepository vehicleRepository;

    /**
     * The repository used to retrieve user data.
     */
    private final UserRepository userRepository;

    /**
     * The repository used to retrieve customer data.
     */
    private final CustomerRepository customerRepository;

    /**
     * The repository used to retrieve service data.
     */
    private final ServiceRepository serviceRepository;

    /**
     * The repository used to retrieve service type data.
     */
    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * Permanently deletes all resources that are marked as deleted from the database.
     * This method retrieves lists of all softly deleted Vehicles, Users, Customers,
     * Services, and ServiceTypes, and calls the respective repository methods to
     * permanently delete them.
     * <p>
     * This method is scheduled to run periodically based on the cron expression specified.
     * </p>
     */
    @Override
    @Transactional
    @Scheduled(cron = "0 59 23 ? * SUN", zone = "GMT+2")
//    @Scheduled(cron = "0 0/1 * 1/1 * *", zone = "GMT+2") --> This is just for testing
//    and will be deleted later on.
    public void permanentlyDeleteResources() {
        List<Customer> deletedCustomers = customerRepository.findAllDeletedCustomers();
        List<Vehicle> deletedVehicles = vehicleRepository.findAllDeletedVehicles();
        List<Service> deletedServices = serviceRepository.findAllDeletedServices();
        List<ServiceType> deletedServiceTypes = serviceTypeRepository.findAllDeletedServiceTypes();

        deletedServiceTypes.forEach(serviceType ->
                serviceTypeRepository.permanentlyDeleteServiceTypeById(serviceType.getId()));

        deletedServices.forEach(service ->
                serviceRepository.permanentlyDeleteServiceById(service.getId()));

        deletedVehicles.forEach(vehicle ->
                vehicleRepository.permanentlyDeleteVehicleById(vehicle.getId()));

        deletedCustomers.forEach(customer ->
                customerRepository.permanentlyDeleteCustomerById(customer.getId()));
    }
}
