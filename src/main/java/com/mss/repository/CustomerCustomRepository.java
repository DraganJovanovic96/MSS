package com.mss.repository;

import com.mss.dto.CustomerFiltersQueryDto;
import com.mss.model.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Repository
public class CustomerCustomRepository {
    /**
     * An EntityManager instance is associated with a persistence context.
     * A persistence context is a set of entity instances in which for any
     * persistent entity identity there is a unique entity instance.
     */
    private final EntityManager entityManager;

    /**
     * Retrieves a paginated list of customers based on the provided filters.
     * The method constructs a dynamic query using the criteria API to filter customers
     * by full name, address, phone number, and vehicle IDs.
     * The phone number is formatted to only contain numeric digits before filtering.
     *
     * @param filters  the {@link CustomerFiltersQueryDto} containing the filter criteria
     *                 for customers. If any field is null, it will be ignored in the query.
     * @param pageable the {@link Pageable} object containing pagination information such as
     *                 page number and page size.
     * @return a {@link Page} of {@link Customer} objects that match the filter criteria.
     * The page contains the list of customers, pagination details, and total number of rows.
     */
    public Page<Customer> findFilteredCustomers(CustomerFiltersQueryDto filters, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Customer> customer = cq.from(Customer.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getFullName())) {
            Expression<String> fullName = cb.concat(customer.get("firstname"), " ");
            fullName = cb.concat(fullName, customer.get("lastname"));
            predicates.add(cb.like(cb.lower(fullName), "%" + filters.getFullName().toLowerCase() + "%"));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getAddress())) {
            predicates.add(cb.like(cb.lower(customer.get("address")), "%" + filters.getAddress().toLowerCase() + "%"));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getPhoneNumber())) {
            filters.setPhoneNumber(filters.getPhoneNumber().replaceAll("[^0-9]", ""));
            predicates.add(cb.like(cb.lower(customer.get("phoneNumber")), "%" + filters.getPhoneNumber() + "%"));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getVehicleIds()) && !filters.getVehicleIds().isEmpty()) {
            predicates.add(customer.get("vehicle").get("id").in(filters.getVehicleIds()));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.asc(customer.get("firstname")));
        cq.select(customer).distinct(true);

        TypedQuery<Customer> query = entityManager.createQuery(cq);
        int totalRows = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(query.getResultList(), pageable, totalRows);
    }
}
