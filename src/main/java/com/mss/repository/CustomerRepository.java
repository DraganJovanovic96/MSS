package com.mss.repository;

import com.mss.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The CustomerRepository interface extends JpaRepository to inherit JPA-based CRUD methods and custom
 * methods for accessing and modifying Customer entities.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    /**
     * A method for finding all customers based on first and last name.
     *
     * @param firstname First name of the customer
     * @param lastname  Last name of the customer
     * @return List of customers that are fetched
     */
    List<Customer> findByFirstnameAndLastname(String firstname, String lastname);
}
