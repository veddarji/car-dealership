package com.cardealership.repository;

import com.cardealership.entity.Vehicle;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VehicleSpecifications {

    public static Specification<Vehicle> searchByFilters(String make, String model, String category,
                                                          BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(make)) {
                predicates.add(cb.like(cb.lower(root.get("make")),
                        "%" + make.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(model)) {
                predicates.add(cb.like(cb.lower(root.get("model")),
                        "%" + model.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(category)) {
                predicates.add(cb.equal(cb.lower(root.get("category")),
                        category.toLowerCase()));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}