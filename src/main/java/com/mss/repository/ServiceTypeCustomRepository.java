package com.mss.repository;

import com.mss.dto.ServiceTypeFiltersQueryDto;
import com.mss.model.ServiceType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Path;
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

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getPartCode())) {
            predicates.add(cb.like(cb.lower(serviceTypes.get("partCode")), "%" + filters.getPartCode().toLowerCase() + "%"));
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
        
        boolean isNestedFieldSort = applySorting(cb, cq, serviceTypes, filters);
        
        cq.select(serviceTypes);
        if (!isNestedFieldSort) {
            cq.distinct(true);
        }

        TypedQuery<ServiceType> query = entityManager.createQuery(cq);
        int totalRows = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(query.getResultList(), pageable, totalRows);
    }

    /**
     * Applies dynamic sorting to the criteria query based on the sortBy and sortDirection parameters
     * in the filters DTO. If no sorting parameters are provided, applies default sorting.
     *
     * @param cb            the CriteriaBuilder
     * @param cq            the CriteriaQuery
     * @param serviceTypes  the Root<ServiceType> entity
     * @param filters       the ServiceTypeFiltersQueryDto containing sorting parameters
     * @return true if sorting requires distinct to be removed (nested field or case-insensitive), false otherwise
     */
    private boolean applySorting(CriteriaBuilder cb, CriteriaQuery cq, Root<ServiceType> serviceTypes, ServiceTypeFiltersQueryDto filters) {
        if (Objects.nonNull(filters) && Objects.nonNull(filters.getSortBy()) && !filters.getSortBy().isEmpty()) {
            String sortBy = filters.getSortBy();
            String sortDirection = Objects.nonNull(filters.getSortDirection()) && filters.getSortDirection().equalsIgnoreCase("desc") ? "desc" : "asc";
            
            jakarta.persistence.criteria.Order order;
            boolean isNestedField = sortBy.contains(".");
            boolean isCaseInsensitiveField = sortBy.equals("typeOfService") || sortBy.equals("description") || sortBy.equals("partCode");
            
            if (isNestedField) {
                String[] parts = sortBy.split("\\.");
                Path<Object> path = serviceTypes.get(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    path = path.get(parts[i]);
                }
                order = sortDirection.equals("asc") ? cb.asc(path) : cb.desc(path);
            } else if (isCaseInsensitiveField) {
                order = sortDirection.equals("asc") ? cb.asc(cb.lower(serviceTypes.get(sortBy))) : cb.desc(cb.lower(serviceTypes.get(sortBy)));
            } else {
                order = sortDirection.equals("asc") ? cb.asc(serviceTypes.get(sortBy)) : cb.desc(serviceTypes.get(sortBy));
            }
            
            cq.orderBy(order);
            return isNestedField || isCaseInsensitiveField;
        } else {
            cq.orderBy(cb.desc(serviceTypes.get("createdAt")));
            return false;
        }
    }
}
