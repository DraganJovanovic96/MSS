package com.mss.repository;

import com.mss.dto.VehicleFiltersQueryDto;
import com.mss.model.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.Data;
import jakarta.persistence.criteria.Path;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Repository
public class VehicleCustomRepository {
    /**
     * An EntityManager instance is associated with a persistence context.
     * A persistence context is a set of entity instances in which for any
     * persistent entity identity there is a unique entity instance.
     */
    private final EntityManager entityManager;

    public Page<Vehicle> findFilteredVehicles(VehicleFiltersQueryDto filters, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Vehicle> vehicle = cq.from(Vehicle.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getManufacturer())) {
            predicates.add(cb.like(cb.lower(vehicle.get("manufacturer")), "%" + filters.getManufacturer().toLowerCase() + "%"));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getModel())) {
            predicates.add(cb.like(cb.lower(vehicle.get("model")), "%" + filters.getModel().toLowerCase() + "%"));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getVehiclePlate())) {
            Expression<String> vehiclePlate = cb.lower(cb.function("regexp_replace", String.class,
                    vehicle.get("vehiclePlate"), cb.literal("[^a-zA-Z0-9]"), cb.literal("")));

            String filterPlate = filters.getVehiclePlate().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

            predicates.add(cb.like(cb.lower(vehiclePlate), "%" + filterPlate + "%"));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getVin())) {
            predicates.add(cb.like(cb.lower(vehicle.get("vin")), "%" + filters.getVin().toLowerCase() + "%"));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getYearOfManufacture())) {
            predicates.add(cb.equal(vehicle.get("yearOfManufacture"), filters.getYearOfManufacture()));
        }

        if (Objects.nonNull(filters) && Objects.nonNull(filters.getCustomerId())) {
            predicates.add(vehicle.get("customer").get("id").in(filters.getCustomerId()));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        
        boolean isNestedFieldSort = applySorting(cb, cq, vehicle, filters);
        
        cq.select(vehicle);
        if (!isNestedFieldSort) {
            cq.distinct(true);
        }

        TypedQuery<Vehicle> query = entityManager.createQuery(cq);
        int totalRows = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(query.getResultList(), pageable, totalRows);
    }

    /**
     * Applies dynamic sorting to the criteria query based on the sortBy and sortDirection parameters
     * in the filters DTO. If no sorting parameters are provided, applies default sorting.
     *
     * @param cb      the CriteriaBuilder
     * @param cq      the CriteriaQuery
     * @param vehicle the Root<Vehicle> entity
     * @param filters the VehicleFiltersQueryDto containing sorting parameters
     * @return true if sorting requires distinct to be removed (nested field or case-insensitive), false otherwise
     */
    private boolean applySorting(CriteriaBuilder cb, CriteriaQuery cq, Root<Vehicle> vehicle, VehicleFiltersQueryDto filters) {
        if (Objects.nonNull(filters) && Objects.nonNull(filters.getSortBy()) && !filters.getSortBy().isEmpty()) {
            String sortBy = filters.getSortBy();
            String sortDirection = Objects.nonNull(filters.getSortDirection()) && filters.getSortDirection().equalsIgnoreCase("desc") ? "desc" : "asc";
            
            jakarta.persistence.criteria.Order order;
            boolean isNestedField = sortBy.contains(".");
            boolean isCaseInsensitiveField = sortBy.equals("manufacturer") || sortBy.equals("model") || sortBy.equals("vehiclePlate") || sortBy.equals("vin");
            
            if (isNestedField) {
                String[] parts = sortBy.split("\\.");
                Path<Object> path = vehicle.get(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    path = path.get(parts[i]);
                }
                order = sortDirection.equals("asc") ? cb.asc(path) : cb.desc(path);
            } else if (isCaseInsensitiveField) {
                order = sortDirection.equals("asc") ? cb.asc(cb.lower(vehicle.get(sortBy))) : cb.desc(cb.lower(vehicle.get(sortBy)));
            } else {
                order = sortDirection.equals("asc") ? cb.asc(vehicle.get(sortBy)) : cb.desc(vehicle.get(sortBy));
            }
            
            cq.orderBy(order);
            return isNestedField || isCaseInsensitiveField;
        } else {
            cq.orderBy(cb.asc(cb.lower(vehicle.get("manufacturer"))));
            return true;
        }
    }
}
