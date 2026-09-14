package com.mss.mapper;

import com.mss.dto.CustomerReportDto;
import com.mss.model.CustomerReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper for converting between CustomerReport entities and DTOs.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Mapper(componentModel = "spring")
public interface CustomerReportMapper {

    /**
     * Convert a CustomerReport entity to a CustomerReportDto.
     *
     * @param customerReport the customer report entity
     * @return the customer report DTO
     */
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "customer", target = "customerName", qualifiedByName = "customerToString")
    @Mapping(source = "customer.email", target = "customerEmail")
    @Mapping(source = "vehicle", target = "vehicleInfo", qualifiedByName = "vehicleToString")
    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "userToString")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "service.id", target = "serviceId")
    @Mapping(source = "issueDescription", target = "issueDescription")
    @Mapping(source = "photoUrls", target = "photoUrls")
    CustomerReportDto customerReportToCustomerReportDto(CustomerReport customerReport);

    /**
     * Convert a list of CustomerReport entities to a list of CustomerReportDtos.
     *
     * @param customerReports the list of customer report entities
     * @return the list of customer report DTOs
     */
    List<CustomerReportDto> customerReportsToCustomerReportDtos(List<CustomerReport> customerReports);

    /**
     * Convert vehicle to string representation.
     *
     * @param vehicle the vehicle entity
     * @return vehicle info string
     */
    @Named("vehicleToString")
    default String vehicleToString(com.mss.model.Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return vehicle.getManufacturer() + " " + vehicle.getModel() + " (" + vehicle.getVehiclePlate() + ")";
    }

    /**
     * Convert customer to string representation.
     *
     * @param customer the customer entity
     * @return customer name string
     */
    @Named("customerToString")
    default String customerToString(com.mss.model.Customer customer) {
        if (customer == null) {
            return null;
        }
        return customer.getFirstname() + " " + customer.getLastname();
    }

    /**
     * Convert user to string representation.
     *
     * @param user the user entity
     * @return user name string
     */
    @Named("userToString")
    default String userToString(com.mss.model.User user) {
        if (user == null) {
            return null;
        }
        return user.getFirstname() + " " + user.getLastname();
    }

    /**
     * Extract customer ID from customer entity.
     *
     * @param customer the customer entity
     * @return customer ID
     */
    @Named("customerIdExtractor")
    default Long customerIdExtractor(com.mss.model.Customer customer) {
        if (customer == null) {
            return null;
        }
        return customer.getId();
    }

    /**
     * Extract vehicle ID from vehicle entity.
     *
     * @param vehicle the vehicle entity
     * @return vehicle ID
     */
    @Named("vehicleIdExtractor")
    default Long vehicleIdExtractor(com.mss.model.Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return vehicle.getId();
    }

    /**
     * Extract user ID from user entity.
     *
     * @param user the user entity
     * @return user ID
     */
    @Named("userIdExtractor")
    default Long userIdExtractor(com.mss.model.User user) {
        if (user == null) {
            return null;
        }
        return user.getId();
    }
}
