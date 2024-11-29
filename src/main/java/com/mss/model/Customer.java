package com.mss.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "customers")
@SQLDelete(sql = "UPDATE customers SET deleted = true WHERE id=?")
@FilterDef(name = "deletedCustomerFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedCustomerFilter", condition = "deleted = :isDeleted")
public class Customer extends BaseEntity<Long> {
    /**
     * The customer's firstname.
     */
    @Column
    private String firstname;

    /**
     * The customer's lastname.
     */
    @Column
    private String lastname;

    /**
     * The customer's address.
     */
    @Column
    private String address;

    /**
     * The customer's email.
     */
    @Size(max = 320)
    private String email;

    /**
     * The customer's phone number.
     */
    @Column(unique = true)
    private String phoneNumber;

    /**
     * The customer's vehicles.
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles = new ArrayList<>();
}
