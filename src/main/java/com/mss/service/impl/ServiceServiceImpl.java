package com.mss.service.impl;

import com.mss.dto.*;
import com.mss.mapper.ServiceMapper;
import com.mss.mapper.UserMapper;
import com.mss.mapper.VehicleMapper;
import com.mss.model.*;
import com.mss.repository.*;
import com.mss.service.ServiceService;
import com.mss.service.ServiceTypeService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The ServiceServiceImpl implements ServiceService and
 * all methods that are in ServiceRepository.
 * Dependency injection was used to get beans of ServiceRepository and ServiceMapper.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
    /**
     * The repository used to retrieve service data.
     */
    private final ServiceRepository serviceRepository;

    /**
     * The repository used to retrieve service data.
     */
    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * The service used to retrieve service data.
     */
    private final ServiceTypeService serviceTypeService;

    /**
     * The custom repository used to retrieve service data.
     */
    private final ServiceCustomRepository serviceCustomRepository;

    /**
     * The repository used to retrieve user data.
     */
    private final UserRepository userRepository;

    /**
     * The repository used to retrieve vehicle data.
     */
    private final VehicleRepository vehicleRepository;

    /**
     * The repository used to retrieve customer data.
     */
    private final CustomerRepository customerRepository;

    /**
     * The mapper used to convert service data between ServiceDto and Service entities.
     */
    private final ServiceMapper serviceMapper;

    /**
     * The mapper used to convert service data between ServiceDto and Service entities.
     */
    private final UserMapper userMapper;

    /**
     * The mapper used to convert service data between ServiceDto and Service entities.
     */
    private final VehicleMapper vehicleMapper;

    /**
     * Created SERVICE_FILTER attribute, so we can change Filter easily if needed.
     */
    private static final String SERVICE_FILTER = "deletedServiceFilter";

    /**
     * An EntityManager instance is associated with a persistence context.
     * A persistence context is a set of entity instances in which for any
     * persistent entity identity there is a unique entity instance.
     */
    private final EntityManager entityManager;

    /**
     * Counts the number of services based on their deletion status.
     *
     * @param isDeleted A boolean indicating the deletion status of services to be counted.
     *                  If {@code true}, counts only deleted services.
     *                  If {@code false}, counts only active services.
     * @return The total number of services matching the specified deletion status.
     */
    @Override
    public long getServiceCount(boolean isDeleted) {
        return serviceRepository.countByDeleted(isDeleted);
    }

    /**
     * Gets the revenue based on two dates.
     *
     * @param isDeleted  A boolean indicating the deletion status of services to be counted.
     *                   If {@code true}, counts only deleted services.
     *                   If {@code false}, counts only active services.
     * @param twoDateDto
     * @return The total number of services matching the specified deletion status.
     */
    @Override
    public double getRevenue(boolean isDeleted, TwoDateDto twoDateDto) {
        double revenue = 0;
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_FILTER);
        filter.setParameter("isDeleted", isDeleted);
        List<Service> services = serviceRepository.findServicesByDateRange(twoDateDto.getStartDate(), twoDateDto.getEndDate());

        for (Service service : services) {
            revenue += serviceTypeService.findRevenueForService(isDeleted, service);
        }

        session.disableFilter(SERVICE_FILTER);
        return revenue;
    }

    /**
     * Gets the number of parts based on two dates.
     *
     * @param isDeleted  A boolean indicating the deletion status of services to be counted.
     *                   If {@code true}, counts only deleted services.
     *                   If {@code false}, counts only active services.
     * @param twoDateDto tho dates for range when service could have started.
     * @return The total number of services matching the specified deletion status.
     */
    @Override
    public Integer getParts(boolean isDeleted, TwoDateDto twoDateDto) {
        Integer numberOfParts = 0;

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_FILTER);
        filter.setParameter("isDeleted", isDeleted);
        List<Service> services = serviceRepository.findServicesByDateRange(twoDateDto.getStartDate(), twoDateDto.getEndDate());

        for (Service service : services) {
            numberOfParts += serviceTypeService.findNumberOfPartsForService(isDeleted, service);
        }

        session.disableFilter(SERVICE_FILTER);
        return numberOfParts;
    }

    /**
     * Retrieves a list of aggregated data for generating a pie chart.
     * This method calculates revenue grouped by specific criteria (e.g., service invoice code)
     * within the specified date range and based on the soft deletion status.
     *
     * @param isDeleted  a boolean flag indicating whether to include soft-deleted records (true) or not (false).
     * @param twoDateDto an object containing the start and end dates for filtering the data.
     * @return a list of {@code PieChartServiceDto} objects, where each object represents an individual
     * segment of the pie chart with its name (e.g., invoice code) and value (e.g., revenue).
     */
    @Override
    public List<PieChartServiceDto> getInfoForPieChart(boolean isDeleted, TwoDateDto twoDateDto) {
        if (twoDateDto == null || twoDateDto.getStartDate() == null || twoDateDto.getEndDate() == null) {
            throw new IllegalArgumentException("Start and end dates must not be null");
        }

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_FILTER);
        filter.setParameter("isDeleted", isDeleted);
        List<Service> services = serviceRepository.findServicesByDateRange(twoDateDto.getStartDate(), twoDateDto.getEndDate());
        List<PieChartServiceDto> pieChartServiceDtos;

        pieChartServiceDtos = services.stream()
                .map(service -> {
                    PieChartServiceDto pieChartServiceDto = new PieChartServiceDto();
                    pieChartServiceDto.setInvoiceCode(service.getInvoiceCode());
                    pieChartServiceDto.setRevenue(serviceTypeService.findRevenueForService(isDeleted, service));
                    return pieChartServiceDto;
                })
                .collect(Collectors.toList());

        session.disableFilter(SERVICE_FILTER);

        return pieChartServiceDtos;
    }

    /**
     * Retrieves revenue information for mechanics within a specified date range.
     * Groups the total revenue by mechanic and returns a list of DTOs containing
     * each mechanic's name and total revenue.
     *
     * @param isDeleted  a boolean indicating whether to include deleted services.
     * @param twoDateDto an object containing the start and end dates for filtering services.
     * @return a list of {@link PieChartMechanicDto} objects, each representing a mechanic and their total revenue.
     * @throws IllegalArgumentException if the {@code twoDateDto} is null, or its start and end dates are null.
     */
    @Override
    public List<PieChartMechanicDto> getInfoForMechanicPieChart(boolean isDeleted, TwoDateDto twoDateDto) {
        if (twoDateDto == null || twoDateDto.getStartDate() == null || twoDateDto.getEndDate() == null) {
            throw new IllegalArgumentException("Start and end dates must not be null");
        }

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_FILTER);
        filter.setParameter("isDeleted", isDeleted);

        List<Service> services = serviceRepository.findServicesByDateRange(twoDateDto.getStartDate(), twoDateDto.getEndDate());

        Map<Long, Double> revenueByMechanic = services.stream()
                .collect(Collectors.groupingBy(
                        service -> service.getUser().getId(),
                        Collectors.summingDouble(service -> serviceTypeService.findRevenueForService(isDeleted, service))
                ));

        List<PieChartMechanicDto> mechanicRevenueList = revenueByMechanic.entrySet().stream()
                .map(entry -> {
                    Long mechanicId = entry.getKey();
                    Double revenue = entry.getValue();

                    User user = userRepository.findById(mechanicId)
                            .orElseThrow(() -> new IllegalArgumentException("Mechanic not found with ID: " + mechanicId));

                    PieChartMechanicDto dto = new PieChartMechanicDto();
                    dto.setMechanicName(user.getFirstname() + " " + user.getLastname());
                    dto.setRevenue(revenue);
                    return dto;
                })
                .collect(Collectors.toList());

        session.disableFilter(SERVICE_FILTER);

        return mechanicRevenueList;
    }

    /**
     * Retrieves data for a pie chart representing Customer information for revenue.
     *
     * @param isDeleted  a boolean indicating whether to include only deleted data (true)
     *                   or non-deleted data (false) in the result.
     * @param twoDateDto an object containing two date values that define the range for the data query.
     *                   The range includes a start date and an end date.
     * @return a list of {@link PieChartCustomerDto} objects, each representing data
     * for a segment of the pie chart.
     */
    @Override
    public List<PieChartCustomerDto> getInfoForCustomerPieChart(boolean isDeleted, TwoDateDto twoDateDto) {
        if (twoDateDto == null || twoDateDto.getStartDate() == null || twoDateDto.getEndDate() == null) {
            throw new IllegalArgumentException("Start and end dates must not be null");
        }

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_FILTER);
        filter.setParameter("isDeleted", isDeleted);

        List<Service> services = serviceRepository.findServicesByDateRange(twoDateDto.getStartDate(), twoDateDto.getEndDate());

        Map<Long, Double> revenueByCustomer = services.stream()
                .collect(Collectors.groupingBy(
                        service -> service.getVehicle().getCustomer().getId(),
                        Collectors.summingDouble(service -> serviceTypeService.findRevenueForService(isDeleted, service))
                ));

        List<PieChartCustomerDto> customerRevenueList = revenueByCustomer.entrySet().stream()
                .map(entry -> {
                    Long customerId = entry.getKey();
                    Double revenue = entry.getValue();

                    Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

                    PieChartCustomerDto dto = new PieChartCustomerDto();
                    dto.setCustomerName(customer.getFirstname() + " " + customer.getLastname());
                    dto.setRevenue(revenue);
                    return dto;
                })
                .collect(Collectors.toList());

        session.disableFilter(SERVICE_FILTER);

        return customerRevenueList;
    }

    /**
     * Retrieves a list of all services that are not deleted.
     *
     * @return A list of ServiceDto objects representing the services.
     */
    @Override
    public List<ServiceDto> getAllServices(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_FILTER);
        filter.setParameter("isDeleted", isDeleted);
        List<Service> services = serviceRepository.findAll();
        session.disableFilter(SERVICE_FILTER);

        return serviceMapper.serviceToServiceDtos(services);
    }

    /**
     * Saves a new service based on the provided ServiceCreateDto.
     * <p>
     * This method retrieves the user and vehicle associated with the provided user ID and vehicle ID
     * from the ServiceCreateDto. If both the user and vehicle exist, the service is mapped from the DTO,
     * associated with the user and vehicle, and then saved to the repository.
     * The saved service is then converted to a ServiceDto and returned.
     * If either the user or vehicle is not found, a ResponseStatusException is thrown with a 404 status code.
     *
     * @param serviceCreateDto the data transfer object containing service information to be saved.
     * @return the ServiceDto representing the saved service.
     * @throws ResponseStatusException if the user or vehicle with the provided IDs does not exist.
     */
    @Override
    public ServiceDto saveService(ServiceCreateDto serviceCreateDto) {
        User user = userRepository.findOneById(serviceCreateDto.getUserId())
                .map(userPresent -> {
                    if (userPresent.getDeleted()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "User with that id already exists and is deleted, check your deleted resources.");
                    }
                    return userPresent;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist"));

        Vehicle vehicle = vehicleRepository.findOneById(serviceCreateDto.getVehicleId())
                .map(vehiclePresent -> {
                    if (vehiclePresent.getDeleted()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle with that id already exists and is deleted, check your deleted resources.");
                    }
                    return vehiclePresent;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle doesn't exist"));

        Service service = serviceMapper.serviceCreateDtoToService(serviceCreateDto);
        service.setVehicle(vehicle);
        service.setUser(user);
        service.setInvoiceCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        vehicleRepository.save(vehicle);

        return serviceMapper.serviceToServiceDto(serviceRepository.save(service));
    }

    /**
     * @param serviceId the unique identifier of the service to retrieve
     * @param isDeleted boolean that represents if service is deleted or now
     * @return ServiceDto that contains service data
     */
    @Override
    public ServiceDto findServiceById(Long serviceId, boolean isDeleted) {
        Service service = serviceRepository.findOneById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service with this id doesn't exist"));

        return serviceMapper.serviceToServiceDto(service);
    }

    /**
     * @param serviceId parameter that is unique to entity
     */
    @Override
    public void deleteService(Long serviceId) {
        serviceRepository.findById(serviceId)
                .map(service -> {
                    if (Boolean.TRUE.equals(service.getDeleted())) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service is already deleted.");
                    }

                    for (ServiceType serviceType : service.getServiceTypes()) {
                        if (Boolean.FALSE.equals(serviceType.getDeleted()) && Boolean.FALSE.equals(serviceType.getDeletedByCascade())) {
                            serviceType.setDeletedByCascade(true);
                            serviceTypeRepository.save(serviceType);
                        }
                    }
                    return service;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service is not found."));

        serviceRepository.deleteById(serviceId);
    }

    /**
     * Retrieves a paginated list of services based on the provided filters and deletion status.
     * The method applies a filter to include or exclude deleted services and then retrieves
     * services that match the criteria specified in the {@link ServiceFiltersQueryDto}.
     * The services are mapped to {@link ServiceDto} objects before being returned as a paginated result.
     *
     * @param isDeleted              a boolean indicating whether to include deleted services in the results.
     * @param serviceFiltersQueryDto the {@link ServiceFiltersQueryDto} containing the filter criteria for services.
     *                               If any field is null, it will be ignored in the query.
     * @param page                   the page number for pagination.
     * @param pageSize               the size of each page for pagination.
     * @return a {@link Page} of {@link ServiceDto} objects representing the services that match the filter criteria.
     * The page contains the list of services, pagination details, and total number of rows.
     */
    @Override
    public Page<ServiceDto> findFilteredServices(boolean isDeleted, ServiceFiltersQueryDto serviceFiltersQueryDto, Integer page, Integer pageSize) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_FILTER);
        filter.setParameter("isDeleted", isDeleted);

        Page<Service> resultPage = serviceCustomRepository.findFilteredServices(serviceFiltersQueryDto, PageRequest.of(page, pageSize));
        List<Service> services = resultPage.getContent();

        List<ServiceDto> serviceDtos = new ArrayList<ServiceDto>();

        for (Service service : services) {
            ServiceDto serviceDto = serviceMapper.serviceToServiceDto(service);
            serviceDto.setRevenuePerService(serviceTypeService.findRevenueForService(false, service));
            service.getServiceTypes();
            serviceDtos.add(serviceDto);
        }

        session.disableFilter(SERVICE_FILTER);

        return new PageImpl<>(serviceDtos, resultPage.getPageable(), resultPage.getTotalElements());
    }

    /**
     * This method first calls the serviceRepository's findFilteredServices method
     * to retrieve a Page of Service objects that match the query.
     * It then iterates over the Service objects and retrieves the associated Vehicle and User objects
     * using the getVehicle and getUser methods.
     *
     * @param isDeleted              boolean representing deleted objects
     * @param serviceFiltersQueryDto {@link ServiceFiltersQueryDto} object which contains query parameters
     * @param page                   int number of wanted page
     * @param pageSize               number of results per page
     * @return a Page of ServiceDto objects that match the specified query
     */
    @Override
    public Page<ServiceWithUserDto> findFilteredServicesWithCustomers(boolean isDeleted, ServiceFiltersQueryDto serviceFiltersQueryDto, Integer page, Integer pageSize) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_FILTER);
        filter.setParameter("isDeleted", isDeleted);

        Page<Service> resultPage = serviceCustomRepository.findFilteredServicesWithCustomer(serviceFiltersQueryDto, PageRequest.of(page, pageSize));
        List<Service> services = resultPage.getContent();

        List<ServiceWithUserDto> serviceWithUserDto = new ArrayList<ServiceWithUserDto>();

        for (Service service : services) {
            ServiceWithUserDto serviceDto = serviceMapper.serviceToServiceWithUserDto(service);
            serviceWithUserDto.add(serviceDto);
        }

        session.disableFilter(SERVICE_FILTER);

        return new PageImpl<>(serviceWithUserDto, resultPage.getPageable(), resultPage.getTotalElements());
    }

    /**
     * Updates an existing service with the provided details.
     *
     * <p>This method accepts a {@link ServiceUpdateDto} containing updated information for a
     * specific service, modifies the service's properties accordingly, and returns the updated
     * {@link ServiceDto} object. This operation typically includes updating service attributes.</p>
     *
     * @param serviceUpdateDto a DTO containing the updated details of the service
     * @return {@link ServiceDto} the updated service data, encapsulated in a DTO for response
     */
    @Override
    @Transactional
    public ServiceDto updateCustomer(ServiceUpdateDto serviceUpdateDto) {
        Service service = serviceRepository.findOneById(serviceUpdateDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Service with this id doesn't exist"));

        Vehicle vehicle = vehicleRepository.findOneById(serviceUpdateDto.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Vehicle with this id doesn't exist"));

        User user = userRepository.findOneById(serviceUpdateDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " User with this id doesn't exist"));

        service.setUpdatedAt(Instant.now());
        service.setDeleted(serviceUpdateDto.getDeleted());
        service.setStartDate(serviceUpdateDto.getStartDate());
        service.setEndDate(serviceUpdateDto.getEndDate());
        service.setCurrentMileage(serviceUpdateDto.getCurrentMileage());
        service.setNextServiceMileage(serviceUpdateDto.getNextServiceMileage());
        service.setVehicle(vehicle);
        service.setUser(user);

        for (ServiceType serviceType : service.getServiceTypes()) {
            if (Boolean.TRUE.equals(serviceType.getDeletedByCascade()) && Boolean.TRUE.equals(serviceType.getDeleted())) {
                serviceType.setDeleted(false);
                serviceType.setDeletedByCascade(false);
                serviceTypeRepository.save(serviceType);
            }
        }

        serviceRepository.save(service);
        entityManager.flush();

        return serviceMapper.serviceToServiceDto(service);
    }
}
