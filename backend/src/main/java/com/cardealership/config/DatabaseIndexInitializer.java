package com.cardealership.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test & !integration & !dev")
public class DatabaseIndexInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseIndexInitializer.class);

    @Autowired
    private EntityManager entityManager;

    @PostConstruct
    @Transactional
    public void initIndexes() {
        try {
            entityManager.createNativeQuery("CREATE EXTENSION IF NOT EXISTS pg_trgm").executeUpdate();
            log.info("pg_trgm extension enabled");
        } catch (Exception e) {
            log.warn("Could not enable pg_trgm (not a PostgreSQL DB?): {}", e.getMessage());
        }

        String[] indexes = {
            "CREATE INDEX IF NOT EXISTS idx_vehicles_make_trgm ON vehicles USING GIN (lower(make) gin_trgm_ops)",
            "CREATE INDEX IF NOT EXISTS idx_vehicles_model_trgm ON vehicles USING GIN (lower(model) gin_trgm_ops)",
            "CREATE INDEX IF NOT EXISTS idx_vehicles_category ON vehicles (category)",
            "CREATE INDEX IF NOT EXISTS idx_vehicles_price ON vehicles (price)",
            "CREATE INDEX IF NOT EXISTS idx_vehicles_make_model ON vehicles (make, model)",
            "CREATE INDEX IF NOT EXISTS idx_vehicles_category_price ON vehicles (category, price)"
        };

        for (String sql : indexes) {
            try {
                entityManager.createNativeQuery(sql).executeUpdate();
                log.debug("Created index: {}", sql);
            } catch (Exception e) {
                log.warn("Could not create index: {} — {}", sql, e.getMessage());
            }
        }

        log.info("Database indexes initialized");
    }
}
