
package com.cmsc495.assetmanagement;

import java.sql.Connection;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import com.cmsc495.assetmanagement.controller.AiController;
import com.cmsc495.assetmanagement.controller.AssetController;
import com.cmsc495.assetmanagement.controller.EmployeeController;
import com.cmsc495.assetmanagement.repository.AssetRepository;
import com.cmsc495.assetmanagement.repository.EmployeeRepository;
import com.cmsc495.assetmanagement.service.AiRecommendationService;
import com.cmsc495.assetmanagement.service.AssetService;
import com.cmsc495.assetmanagement.service.EmployeeService;

@SpringBootTest
@ActiveProfiles("test")
class AssetmanagementApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    // Application startup test

    @Test
    void contextLoads() {

        assertNotNull(applicationContext);
    }

    // Controller initialization tests

    @Test
    void assetControllerLoads() {

        assertNotNull(
                applicationContext.getBean(
                        AssetController.class)
        );
    }

    @Test
    void employeeControllerLoads() {

        assertNotNull(
                applicationContext.getBean(
                        EmployeeController.class)
        );
    }

    @Test
    void aiControllerLoads() {

        assertNotNull(
                applicationContext.getBean(
                        AiController.class)
        );
    }

    // Service initialization tests

    @Test
    void assetServiceLoads() {

        assertNotNull(
                applicationContext.getBean(
                        AssetService.class)
        );
    }

    @Test
    void employeeServiceLoads() {

        assertNotNull(
                applicationContext.getBean(
                        EmployeeService.class)
        );
    }

    @Test
    void aiRecommendationServiceLoads() {

        assertNotNull(
                applicationContext.getBean(
                        AiRecommendationService.class)
        );
    }

    // Repository initialization tests

    @Test
    void assetRepositoryLoads() {

        assertNotNull(
                applicationContext.getBean(
                        AssetRepository.class)
        );
    }

    @Test
    void employeeRepositoryLoads() {

        assertNotNull(
                applicationContext.getBean(
                        EmployeeRepository.class)
        );
    }

    // Database connectivity test

    @Test
    void databaseConnectionIsAvailable()
            throws Exception {

        assertNotNull(dataSource);

        try (Connection connection =
                     dataSource.getConnection()) {

            assertNotNull(connection);

            assertFalse(connection.isClosed());

            assertTrue(connection.isValid(5));
        }
    }

    // Test environment isolation

    @Test
    void applicationUsesIsolatedTestDatabase()
            throws Exception {

        try (Connection connection =
                     dataSource.getConnection()) {

            String databaseUrl =
                    connection.getMetaData().getURL();

            assertTrue(
                    databaseUrl.startsWith("jdbc:h2:mem:"),
                    "Tests must use an isolated H2 database."
            );
        }
    }

    // AI test environment configuration

    @Test
    void externalAiProviderIsDisabledDuringTesting() {

        AiRecommendationService aiService =
                applicationContext.getBean(
                        AiRecommendationService.class);

        assertEquals(
                false,
                aiService.getAiStatus().get("enabled")
        );
    }

    // Security configuration test

    @Test
    void securityFilterChainLoads() {

        assertTrue(
                applicationContext.containsBean(
                        "springSecurityFilterChain")
        );
    }
}
