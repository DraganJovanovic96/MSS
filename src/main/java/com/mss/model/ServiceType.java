package com.mss.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

@Data
@Entity
@Table(name = "service_types")
@SQLDelete(sql = "UPDATE service_types SET deleted = true WHERE id=?")
@FilterDef(name = "deletedServiceTypeFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedServiceTypeFilter", condition = "deleted = :isDeleted")
public class ServiceType extends BaseEntity<Long> {
    /**
     * The type of service provided.
     */
    @Column
    private String typeOfService;

    /**
     * The description of service provided.
     */
    @Column
    private String description;

    /**
     * The price of service provided.
     */
    @Column
    private double price;

    /**
     * The code of part provided.
     */
    @Column
    private String partCode;

    /**
     * The quantity of services/parts provided.
     */
    @Column(nullable = false)
    @Min(1)
    private Integer quantity = 1;

    /**
     * The user who performed the service.
     */
    @ManyToOne
    private Service service;
}
