package com.mss.repository;

import com.mss.dto.VehicleFiltersQueryDto;
import com.mss.model.Vehicle;
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
        cq.orderBy(cb.asc(vehicle.get("manufacturer")));
        cq.select(vehicle).distinct(true);

        TypedQuery<Vehicle> query = entityManager.createQuery(cq);
        int totalRows = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(query.getResultList(), pageable, totalRows);
    }
}
