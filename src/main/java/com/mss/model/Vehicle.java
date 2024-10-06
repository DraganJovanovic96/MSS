package com.mss.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "vehicles")
@SQLDelete(sql = "UPDATE vehicles SET deleted = true WHERE id=?")
@FilterDef(name = "deletedVehicleFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedVehicleFilter", condition = "deleted = :isDeleted")
public class Vehicle extends BaseEntity<Long> {
    /**
     * Manufacturer name.
     */
    @Column
    private String manufacturer;

    /**
     * Model name.
     */
    @Column(unique = true)
    private String model;

    /**
     * Vehicle plate.
     */
    @Column(unique = true)
    private String vehiclePlate;

    /**
     * Vin (vehicle identification number).
     */
    @Column
    private String vin;

    /**
     * Year when the vehicle was manufactured.
     */
    @Column
    private int yearOfManufacture;

    /**
     * Owner of vehicles.
     */
    @ManyToOne
    private Customer customer;

    /**
     * Vehicle's services.
     */
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<Service> services = new ArrayList<>();
}
