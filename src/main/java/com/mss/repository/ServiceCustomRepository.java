package com.mss.repository;

import com.mss.dto.ServiceFiltersQueryDto;
import com.mss.model.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@Data
public class ServiceCustomRepository {
    /**
     * An EntityManager instance is associated with a persistence context.
     * A persistence context is a set of entity instances in which for any
     * persistent entity identity there is a unique entity instance.
     */
    private final EntityManager entityManager;

    public Page<Service> findFilteredServices(ServiceFiltersQueryDto filters, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Service> service = cq.from(Service.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getInvoiceCode())) {
            predicates.add(cb.like(cb.lower(service.get("invoiceCode")), "%" + filters.getInvoiceCode().toLowerCase() + "%"));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getStartDate())) {
            predicates.add(cb.greaterThanOrEqualTo(service.get("startDate"), filters.getStartDate()));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getEndDate())) {
            predicates.add(cb.lessThanOrEqualTo(service.get("endDate"), filters.getEndDate()));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getVehicleIds()) && !filters.getVehicleIds().isEmpty()) {
            predicates.add(service.get("vehicle").get("id").in(filters.getVehicleIds()));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getUserIds()) && !filters.getUserIds().isEmpty()) {
            predicates.add(service.get("user").get("id").in(filters.getUserIds()));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.asc(service.get("endDate")), cb.desc(service.get("endDate")));
        cq.select(service).distinct(true);

        TypedQuery<Service> query = entityManager.createQuery(cq);
        int totalRows = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(query.getResultList(), pageable, totalRows);
    }
}
