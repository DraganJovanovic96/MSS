package com.mss.repository;

import com.mss.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
     * @param isDeleted   the boolean representing deletion of the customer
     * @return an Optional containing the customer if found, or empty if not
     */
    Optional<Customer> findByPhoneNumberAndDeleted(String phoneNumber, boolean isDeleted);

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
    Optional<Customer> findOneById(Long customerId);

    /**
     * Finds all customers that are marked as deleted.
     *
     * @return A list of customers that are marked as deleted.
     */
    @Query("SELECT c FROM Customer c WHERE c.deleted = true")
    List<Customer> findAllDeletedCustomers();

    /**
     * Permanently deletes a Customer entity from the database by its ID.
     *
     * <p>This method executes a DELETE operation on the Customer entity,
     * removing the record with the specified ID from the database. This operation
     * is not reversible and will permanently remove the entity.</p>
     *
     * @param customerId the ID of the Customer entity to be deleted
     */
    @Modifying
    @Query("DELETE FROM Customer c WHERE c.id = :customerId")
    void permanentlyDeleteCustomerById(Long customerId);
}
