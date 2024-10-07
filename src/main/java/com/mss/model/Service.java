package com.mss.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "services")
@SQLDelete(sql = "UPDATE services SET deleted = true WHERE id=?")
@FilterDef(name = "deletedServiceFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedServiceFilter", condition = "deleted = :isDeleted")
public class Service extends BaseEntity<Long> {
    /**
     * The start date of the service.
     */
    @Column
    private LocalDate startDate;

    /**
     * The end date of the service.
     */
    @Column
    private LocalDate endDate;

    /**
     * Current mileage on the vehicle.
     */
    @Column
    private int currentMileage;

    /**
     * Recommended mileage for next service.
     */
    @Column
    private int nextServiceMileage;

    /**
     * The vehicle service is provided on.
     */
    @ManyToOne
    private Vehicle vehicle;

    /**
     * The user who performed the service.
     */
    @ManyToOne
    private User user;

    /**
     * Service types connected to service.
     */
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<ServiceType> serviceTypes = new ArrayList<>();
}
