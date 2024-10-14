package com.mss.repository;

import com.mss.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * Find a customer by their phone number.
     *
     * @param phoneNumber the phone number of the customer
     * @param isDeleted the boolean representing deletion of the customer
     * @return an Optional containing the customer if found, or empty if not
     */
    Optional<Customer> findByPhoneNumberAndDeleted(String phoneNumber,boolean isDeleted);

    /**
     * Find a customer by their phone number ignores deleted.
     *
     * @param phoneNumber the phone number of the customer
     * @return an Optional containing the customer if found, or empty if not
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * Find a customer by their id if they are not soft deleted.
     *
     * @param customerId the id of the customer
     * @return an Optional containing the customer if found, or empty if not
     */
    @Query("SELECT c FROM Customer c WHERE c.id = :customerId AND c.deleted = :isDeleted")
    Optional<Customer> findActiveById(@Param("customerId") Long customerId, @Param("isDeleted") boolean isDeleted);
}
