package com.mss.repository;

import com.mss.dto.ServiceTypeFiltersQueryDto;
import com.mss.model.ServiceType;
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

@Data
@Repository
public class ServiceTypeCustomRepository {
    /**
     * An EntityManager instance is associated with a persistence context.
     * A persistence context is a set of entity instances in which for any
     * persistent entity identity there is a unique entity instance.
     */
    private final EntityManager entityManager;

    public Page<ServiceType> findFilteredServiceTypes(ServiceTypeFiltersQueryDto filters, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<ServiceType> serviceTypes = cq.from(ServiceType.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getTypeOfService())) {
            predicates.add(cb.like(cb.lower(serviceTypes.get("typeOfService")), "%" + filters.getTypeOfService().toLowerCase() + "%"));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getDescription())) {
            predicates.add(cb.like(cb.lower(serviceTypes.get("description")), "%" + filters.getDescription().toLowerCase() + "%"));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getPriceMin())) {
            predicates.add(cb.greaterThanOrEqualTo(serviceTypes.get("price"), filters.getPriceMin()));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getPriceMax())) {
            predicates.add(cb.lessThanOrEqualTo(serviceTypes.get("price"), filters.getPriceMax()));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getServiceId())) {
            predicates.add(serviceTypes.get("service").get("id").in(filters.getServiceId()));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(serviceTypes.get("createdAt")));
        cq.select(serviceTypes).distinct(true);

        TypedQuery<ServiceType> query = entityManager.createQuery(cq);
        int totalRows = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(query.getResultList(), pageable, totalRows);
    }
}
