
package com.cmsc495.assetmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;

import com.cmsc495.assetmanagement.model.Asset;
import com.cmsc495.assetmanagement.model.AssetStatus;
import com.cmsc495.assetmanagement.model.Employee;
import com.cmsc495.assetmanagement.repository.AssetRepository;
import com.cmsc495.assetmanagement.repository.EmployeeRepository;

@SpringBootTest
@ActiveProfiles("test")
@WithMockUser(
        username = "testadmin",
        roles = "ADMIN"
)
class AssetControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {

        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        assetRepository.deleteAll();

        employeeRepository.deleteAll();

        employee = employeeRepository.saveAndFlush(
                new Employee(
                        "John Smith",
                        "john@example.com",
                        "Information Technology"
                )
        );
    }

    // Test helper methods

    private Asset createAsset(
            String name,
            String serialNumber,
            AssetStatus status) {

        Asset asset = new Asset(
                name,
                "Laptop",
                serialNumber,
                status
        );

        if (status == AssetStatus.ASSIGNED) {

            asset.setAssignedEmployee(employee);
        }

        return assetRepository.saveAndFlush(asset);
    }

    private String validAssetRequest() {

        return """
                {
                    "name": "Dell Latitude 5420",
                    "category": "Laptop",
                    "serialNumber": "SN-001",
                    "status": "AVAILABLE"
                }
                """;
    }

    // Asset creation tests

    @Test
    void createAssetSuccessfully() throws Exception {

        mockMvc.perform(
                post("/api/assets")
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(validAssetRequest())
        )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name")
                        .value("Dell Latitude 5420"))
                .andExpect(jsonPath("$.category")
                        .value("Laptop"))
                .andExpect(jsonPath("$.serialNumber")
                        .value("SN-001"))
                .andExpect(jsonPath("$.status")
                        .value("AVAILABLE"));

        assertEquals(1, assetRepository.count());
    }

    @Test
    void rejectAssetWithMissingName() throws Exception {

        String request = """
                {
                    "category": "Laptop",
                    "serialNumber": "SN-002",
                    "status": "AVAILABLE"
                }
                """;

        mockMvc.perform(
                post("/api/assets")
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(
                        "$.validationErrors.name").exists());

        assertEquals(0, assetRepository.count());
    }

    @Test
    void rejectAssetWithBlankCategory() throws Exception {

        String request = """
                {
                    "name": "Dell Laptop",
                    "category": " ",
                    "serialNumber": "SN-003",
                    "status": "AVAILABLE"
                }
                """;

        mockMvc.perform(
                post("/api/assets")
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(
                        "$.validationErrors.category").exists());
    }

    @Test
    void rejectAssetWithMissingSerialNumber()
            throws Exception {

        String request = """
                {
                    "name": "Dell Laptop",
                    "category": "Laptop",
                    "status": "AVAILABLE"
                }
                """;

        mockMvc.perform(
                post("/api/assets")
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(
                        "$.validationErrors.serialNumber")
                        .exists());
    }

    @Test
    void rejectDuplicateSerialNumber() throws Exception {

        createAsset(
                "Existing Laptop",
                "SN-001",
                AssetStatus.AVAILABLE
        );

        mockMvc.perform(
                post("/api/assets")
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(validAssetRequest())
        )
                .andExpect(status().isConflict());

        assertEquals(1, assetRepository.count());
    }

    @Test
    void rejectDuplicateSerialNumberIgnoringCase()
            throws Exception {

        createAsset(
                "Existing Laptop",
                "SN-001",
                AssetStatus.AVAILABLE
        );

        String request = """
                {
                    "name": "Another Laptop",
                    "category": "Laptop",
                    "serialNumber": "sn-001",
                    "status": "AVAILABLE"
                }
                """;

        mockMvc.perform(
                post("/api/assets")
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isConflict());

        assertEquals(1, assetRepository.count());
    }

    @Test
    void rejectInvalidAssetStatus() throws Exception {

        String request = """
                {
                    "name": "Dell Laptop",
                    "category": "Laptop",
                    "serialNumber": "SN-004",
                    "status": "INVALID"
                }
                """;

        mockMvc.perform(
                post("/api/assets")
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectAssignedAssetWithoutEmployee()
            throws Exception {

        String request = """
                {
                    "name": "Dell Laptop",
                    "category": "Laptop",
                    "serialNumber": "SN-005",
                    "status": "ASSIGNED"
                }
                """;

        mockMvc.perform(
                post("/api/assets")
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isBadRequest());

        assertEquals(0, assetRepository.count());
    }

    @Test
    void createAssetWithEmployeeAssignment()
            throws Exception {

        String request = """
                {
                    "name": "Assigned Laptop",
                    "category": "Laptop",
                    "serialNumber": "SN-006",
                    "status": "ASSIGNED",
                    "employeeId": %d
                }
                """.formatted(employee.getId());

        mockMvc.perform(
                post("/api/assets")
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status")
                        .value("ASSIGNED"))
                .andExpect(jsonPath("$.employeeId")
                        .value(employee.getId().intValue()))
                .andExpect(jsonPath("$.assignedTo")
                        .value("John Smith"));
    }

    // Asset retrieval tests

    @Test
    void retrieveAllAssets() throws Exception {

        createAsset(
                "Dell Laptop",
                "SN-101",
                AssetStatus.AVAILABLE
        );

        createAsset(
                "HP Laptop",
                "SN-102",
                AssetStatus.AVAILABLE
        );

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(2));
    }

    @Test
    void retrieveAssetById() throws Exception {

        Asset asset = createAsset(
                "Dell Laptop",
                "SN-103",
                AssetStatus.AVAILABLE
        );

        mockMvc.perform(
                get("/api/assets/{id}", asset.getId())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Dell Laptop"))
                .andExpect(jsonPath("$.serialNumber")
                        .value("SN-103"));
    }

    @Test
    void returnNotFoundForMissingAsset()
            throws Exception {

        mockMvc.perform(
                get("/api/assets/{id}", Long.MAX_VALUE)
        )
                .andExpect(status().isNotFound());
    }

    // Search, filtering and pagination tests

    @Test
    void searchAssetsByKeyword() throws Exception {

        createAsset(
                "Dell Latitude",
                "SN-201",
                AssetStatus.AVAILABLE
        );

        createAsset(
                "HP EliteBook",
                "SN-202",
                AssetStatus.AVAILABLE
        );

        mockMvc.perform(
                get("/api/assets")
                        .param("keyword", "Dell")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value("Dell Latitude"));
    }

    @Test
    void filterAssetsByStatus() throws Exception {

        createAsset(
                "Available Laptop",
                "SN-203",
                AssetStatus.AVAILABLE
        );

        createAsset(
                "Maintenance Laptop",
                "SN-204",
                AssetStatus.MAINTENANCE
        );

        mockMvc.perform(
                get("/api/assets")
                        .param("status", "MAINTENANCE")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(1))
                .andExpect(jsonPath("$.content[0].status")
                        .value("MAINTENANCE"));
    }

    @Test
    void paginateAssetInventory() throws Exception {

        for (int i = 1; i <= 12; i++) {

            createAsset(
                    "Laptop " + i,
                    "PAGE-" + i,
                    AssetStatus.AVAILABLE
            );
        }

        mockMvc.perform(
                get("/api/assets")
                        .param("page", "0")
                        .param("size", "10")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(10));

        mockMvc.perform(
                get("/api/assets")
                        .param("page", "1")
                        .param("size", "10")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(2));
    }

    @Test
    void rejectNegativePageNumber() throws Exception {

        mockMvc.perform(
                get("/api/assets")
                        .param("page", "-1")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectExcessivePageSize() throws Exception {

        mockMvc.perform(
                get("/api/assets")
                        .param("size", "101")
        )
                .andExpect(status().isBadRequest());
    }

    // Asset update tests

    @Test
    void updateAssetSuccessfully() throws Exception {

        Asset asset = createAsset(
                "Original Laptop",
                "SN-301",
                AssetStatus.AVAILABLE
        );

        String request = """
                {
                    "name": "Updated Laptop",
                    "category": "Laptop",
                    "serialNumber": "SN-301",
                    "status": "MAINTENANCE"
                }
                """;

        mockMvc.perform(
                put("/api/assets/{id}", asset.getId())
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Updated Laptop"))
                .andExpect(jsonPath("$.status")
                        .value("MAINTENANCE"));

        Asset updated = assetRepository
                .findById(asset.getId())
                .orElseThrow();

        assertEquals(
                "Updated Laptop",
                updated.getName()
        );

        assertEquals(
                AssetStatus.MAINTENANCE,
                updated.getStatus()
        );
    }

    @Test
    void returnNotFoundWhenUpdatingMissingAsset()
            throws Exception {

        mockMvc.perform(
                put("/api/assets/{id}", Long.MAX_VALUE)
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(validAssetRequest())
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectDuplicateSerialNumberDuringUpdate()
            throws Exception {

        createAsset(
                "First Laptop",
                "SN-302",
                AssetStatus.AVAILABLE
        );

        Asset secondAsset = createAsset(
                "Second Laptop",
                "SN-303",
                AssetStatus.AVAILABLE
        );

        String request = """
                {
                    "name": "Second Laptop",
                    "category": "Laptop",
                    "serialNumber": "SN-302",
                    "status": "AVAILABLE"
                }
                """;

        mockMvc.perform(
                put("/api/assets/{id}", secondAsset.getId())
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isConflict());
    }

    // Peer review regression test

    @Test
    void updateAssetPreservesExistingEmployeeAssignment()
            throws Exception {

        Asset asset = createAsset(
                "Original Assigned Laptop",
                "SN-304",
                AssetStatus.ASSIGNED
        );

        String request = """
                {
                    "name": "Updated Assigned Laptop",
                    "category": "Laptop",
                    "serialNumber": "SN-304",
                    "status": "ASSIGNED"
                }
                """;

        mockMvc.perform(
                put("/api/assets/{id}", asset.getId())
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Updated Assigned Laptop"))
                .andExpect(jsonPath("$.assignedTo")
                        .value("John Smith"))
                .andExpect(jsonPath("$.employeeId")
                        .value(employee.getId().intValue()))
                .andExpect(jsonPath("$.status")
                        .value("ASSIGNED"));

        mockMvc.perform(
                get("/api/assets/{id}", asset.getId())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedTo")
                        .value("John Smith"));
    }

    @Test
    void preventReactivationOfRetiredAsset()
            throws Exception {

        Asset asset = createAsset(
                "Retired Laptop",
                "SN-305",
                AssetStatus.RETIRED
        );

        String request = """
                {
                    "name": "Retired Laptop",
                    "category": "Laptop",
                    "serialNumber": "SN-305",
                    "status": "AVAILABLE"
                }
                """;

        mockMvc.perform(
                put("/api/assets/{id}", asset.getId())
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isConflict());
    }

    // Asset allocation tests

    @Test
    void assignAvailableAssetToEmployee()
            throws Exception {

        Asset asset = createAsset(
                "Available Laptop",
                "SN-401",
                AssetStatus.AVAILABLE
        );

        String request = """
                {
                    "employeeId": %d
                }
                """.formatted(employee.getId());

        mockMvc.perform(
                post("/api/assets/{id}/assign",
                        asset.getId())
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("ASSIGNED"))
                .andExpect(jsonPath("$.assignedTo")
                        .value("John Smith"));
    }

    @Test
    void rejectAssignmentToMissingEmployee()
            throws Exception {

        Asset asset = createAsset(
                "Available Laptop",
                "SN-402",
                AssetStatus.AVAILABLE
        );

        String request = """
                {
                    "employeeId": %d
                }
                """.formatted(Long.MAX_VALUE);

        mockMvc.perform(
                post("/api/assets/{id}/assign",
                        asset.getId())
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectAssignmentOfUnavailableAsset()
            throws Exception {

        Asset asset = createAsset(
                "Maintenance Laptop",
                "SN-403",
                AssetStatus.MAINTENANCE
        );

        String request = """
                {
                    "employeeId": %d
                }
                """.formatted(employee.getId());

        mockMvc.perform(
                post("/api/assets/{id}/assign",
                        asset.getId())
                        .with(csrf())
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isConflict());
    }

    @Test
    void unassignAssetSuccessfully() throws Exception {

        Asset asset = createAsset(
                "Assigned Laptop",
                "SN-404",
                AssetStatus.ASSIGNED
        );

        mockMvc.perform(
                post("/api/assets/{id}/unassign",
                        asset.getId())
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("AVAILABLE"))
                .andExpect(jsonPath("$.employeeId")
                        .doesNotExist())
                .andExpect(jsonPath("$.assignedTo")
                        .doesNotExist());

        assertEquals(
                0,
                assetRepository.countByAssignedEmployee_Id(
                        employee.getId())
        );
    }

    @Test
    void rejectUnassignmentOfAvailableAsset()
            throws Exception {

        Asset asset = createAsset(
                "Available Laptop",
                "SN-405",
                AssetStatus.AVAILABLE
        );

        mockMvc.perform(
                post("/api/assets/{id}/unassign",
                        asset.getId())
                        .with(csrf())
        )
                .andExpect(status().isConflict());
    }

    // Asset deletion tests

    @Test
    void deleteAssetSuccessfully() throws Exception {

        Asset asset = createAsset(
                "Unused Laptop",
                "SN-501",
                AssetStatus.AVAILABLE
        );

        mockMvc.perform(
                delete("/api/assets/{id}", asset.getId())
                        .with(csrf())
        )
                .andExpect(status().isNoContent());

        assertFalse(
                assetRepository.existsById(asset.getId())
        );
    }

    @Test
    void preventDeletionOfAssignedAsset()
            throws Exception {

        Asset asset = createAsset(
                "Assigned Laptop",
                "SN-502",
                AssetStatus.ASSIGNED
        );

        mockMvc.perform(
                delete("/api/assets/{id}", asset.getId())
                        .with(csrf())
        )
                .andExpect(status().isConflict());

        assertEquals(1, assetRepository.count());
    }

    @Test
    void returnNotFoundWhenDeletingMissingAsset()
            throws Exception {

        mockMvc.perform(
                delete("/api/assets/{id}", Long.MAX_VALUE)
                        .with(csrf())
        )
                .andExpect(status().isNotFound());
    }

    // Dashboard statistics test

    @Test
    void retrieveAccurateDashboardStatistics()
            throws Exception {

        createAsset(
                "Available Laptop",
                "SN-601",
                AssetStatus.AVAILABLE
        );

        createAsset(
                "Assigned Laptop",
                "SN-602",
                AssetStatus.ASSIGNED
        );

        createAsset(
                "Maintenance Laptop",
                "SN-603",
                AssetStatus.MAINTENANCE
        );

        createAsset(
                "Retired Laptop",
                "SN-604",
                AssetStatus.RETIRED
        );

        mockMvc.perform(
                get("/api/assets/statistics")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAssets")
                        .value(4))
                .andExpect(jsonPath("$.availableAssets")
                        .value(1))
                .andExpect(jsonPath("$.assignedAssets")
                        .value(1))
                .andExpect(jsonPath("$.maintenanceAssets")
                        .value(1))
                .andExpect(jsonPath("$.retiredAssets")
                        .value(1))
                .andExpect(jsonPath("$.totalEmployees")
                        .value(1))
                .andExpect(jsonPath(
                        "$.assetsByCategory.Laptop")
                        .value(4));
    }
}
