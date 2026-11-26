
package com.cmsc495.assetmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class EmployeeControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AssetRepository assetRepository;

    @BeforeEach
    void setUp() {

        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        assetRepository.deleteAll();

        employeeRepository.deleteAll();
    }

    // Test helper methods

    private Employee createEmployee(
            String name,
            String email,
            String department) {

        Employee employee = new Employee(
                name,
                email,
                department
        );

        return employeeRepository.saveAndFlush(employee);
    }

    private Asset createAssignedAsset(Employee employee) {

        Asset asset = new Asset(
                "Dell Latitude 5420",
                "Laptop",
                "ASSIGNED-SN-001",
                AssetStatus.ASSIGNED
        );

        asset.setAssignedEmployee(employee);

        return assetRepository.saveAndFlush(asset);
    }

    private String validEmployeeRequest() {

        return """
                {
                    "name": "John Smith",
                    "email": "john@example.com",
                    "department": "Information Technology"
                }
                """;
    }

    // Employee creation tests

    @Test
    void createEmployeeSuccessfully() throws Exception {

        mockMvc.perform(
                post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployeeRequest())
        )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name")
                        .value("John Smith"))
                .andExpect(jsonPath("$.email")
                        .value("john@example.com"))
                .andExpect(jsonPath("$.department")
                        .value("Information Technology"))
                .andExpect(jsonPath("$.assignedAssetCount")
                        .value(0));

        assertEquals(1, employeeRepository.count());
    }

    @Test
    void rejectEmployeeWithMissingName()
            throws Exception {

        String request = """
                {
                    "email": "john@example.com",
                    "department": "Information Technology"
                }
                """;

        mockMvc.perform(
                post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(
                        "$.validationErrors.name").exists());

        assertEquals(0, employeeRepository.count());
    }

    @Test
    void rejectEmployeeWithBlankName()
            throws Exception {

        String request = """
                {
                    "name": " ",
                    "email": "john@example.com",
                    "department": "Information Technology"
                }
                """;

        mockMvc.perform(
                post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(
                        "$.validationErrors.name").exists());
    }

    @Test
    void rejectEmployeeWithMissingEmail()
            throws Exception {

        String request = """
                {
                    "name": "John Smith",
                    "department": "Information Technology"
                }
                """;

        mockMvc.perform(
                post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(
                        "$.validationErrors.email").exists());
    }

    @Test
    void rejectInvalidEmailAddress()
            throws Exception {

        String request = """
                {
                    "name": "John Smith",
                    "email": "invalid-email",
                    "department": "Information Technology"
                }
                """;

        mockMvc.perform(
                post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(
                        "$.validationErrors.email").exists());

        assertEquals(0, employeeRepository.count());
    }

    @Test
    void rejectEmployeeWithBlankDepartment()
            throws Exception {

        String request = """
                {
                    "name": "John Smith",
                    "email": "john@example.com",
                    "department": " "
                }
                """;

        mockMvc.perform(
                post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(
                        "$.validationErrors.department")
                        .exists());
    }

    @Test
    void rejectDuplicateEmployeeEmail()
            throws Exception {

        createEmployee(
                "Existing Employee",
                "john@example.com",
                "Engineering"
        );

        mockMvc.perform(
                post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployeeRequest())
        )
                .andExpect(status().isConflict());

        assertEquals(1, employeeRepository.count());
    }

    @Test
    void rejectDuplicateEmailIgnoringCase()
            throws Exception {

        createEmployee(
                "Existing Employee",
                "john@example.com",
                "Engineering"
        );

        String request = """
                {
                    "name": "Another Employee",
                    "email": "JOHN@EXAMPLE.COM",
                    "department": "Operations"
                }
                """;

        mockMvc.perform(
                post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isConflict());

        assertEquals(1, employeeRepository.count());
    }

    @Test
    void normalizeEmployeeEmailToLowercase()
            throws Exception {

        String request = """
                {
                    "name": "John Smith",
                    "email": "JOHN@EXAMPLE.COM",
                    "department": "Engineering"
                }
                """;

        mockMvc.perform(
                post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email")
                        .value("john@example.com"));
    }

    // Employee retrieval tests

    @Test
    void retrieveAllEmployees() throws Exception {

        createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        createEmployee(
                "Jane Doe",
                "jane@example.com",
                "Operations"
        );

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(2));
    }

    @Test
    void retrieveEmployeeById() throws Exception {

        Employee employee = createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        mockMvc.perform(
                get("/api/employees/{id}",
                        employee.getId())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("John Smith"))
                .andExpect(jsonPath("$.email")
                        .value("john@example.com"))
                .andExpect(jsonPath("$.department")
                        .value("Engineering"));
    }

    @Test
    void returnNotFoundForMissingEmployee()
            throws Exception {

        mockMvc.perform(
                get("/api/employees/{id}",
                        Long.MAX_VALUE)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void retrieveEmployeeWithAssignedAssetCount()
            throws Exception {

        Employee employee = createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        createAssignedAsset(employee);

        mockMvc.perform(
                get("/api/employees/{id}",
                        employee.getId())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedAssetCount")
                        .value(1));
    }

    // Search, filtering and pagination tests

    @Test
    void searchEmployeesByName() throws Exception {

        createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        createEmployee(
                "Jane Doe",
                "jane@example.com",
                "Operations"
        );

        mockMvc.perform(
                get("/api/employees")
                        .param("keyword", "John")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value("John Smith"));
    }

    @Test
    void searchEmployeesByEmail() throws Exception {

        createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        createEmployee(
                "Jane Doe",
                "jane@example.com",
                "Operations"
        );

        mockMvc.perform(
                get("/api/employees")
                        .param("keyword", "jane@example.com")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value("Jane Doe"));
    }

    @Test
    void filterEmployeesByDepartment()
            throws Exception {

        createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        createEmployee(
                "Jane Doe",
                "jane@example.com",
                "Operations"
        );

        mockMvc.perform(
                get("/api/employees")
                        .param("department", "Engineering")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(1))
                .andExpect(jsonPath("$.content[0].department")
                        .value("Engineering"));
    }

    @Test
    void paginateEmployeeDirectory() throws Exception {

        for (int i = 1; i <= 12; i++) {

            createEmployee(
                    "Employee " + i,
                    "employee" + i + "@example.com",
                    "Engineering"
            );
        }

        mockMvc.perform(
                get("/api/employees")
                        .param("page", "0")
                        .param("size", "10")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(10));

        mockMvc.perform(
                get("/api/employees")
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
                get("/api/employees")
                        .param("page", "-1")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectExcessivePageSize() throws Exception {

        mockMvc.perform(
                get("/api/employees")
                        .param("size", "101")
        )
                .andExpect(status().isBadRequest());
    }

    // Employee update tests

    @Test
    void updateEmployeeSuccessfully() throws Exception {

        Employee employee = createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        String request = """
                {
                    "name": "John Smith Updated",
                    "email": "john.updated@example.com",
                    "department": "Cybersecurity"
                }
                """;

        mockMvc.perform(
                put("/api/employees/{id}",
                        employee.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("John Smith Updated"))
                .andExpect(jsonPath("$.email")
                        .value("john.updated@example.com"))
                .andExpect(jsonPath("$.department")
                        .value("Cybersecurity"));

        Employee updated = employeeRepository
                .findById(employee.getId())
                .orElseThrow();

        assertEquals(
                "Cybersecurity",
                updated.getDepartment()
        );
    }

    @Test
    void rejectDuplicateEmailDuringUpdate()
            throws Exception {

        createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        Employee employee = createEmployee(
                "Jane Doe",
                "jane@example.com",
                "Operations"
        );

        String request = """
                {
                    "name": "Jane Doe",
                    "email": "john@example.com",
                    "department": "Operations"
                }
                """;

        mockMvc.perform(
                put("/api/employees/{id}",
                        employee.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isConflict());
    }

    @Test
    void returnNotFoundWhenUpdatingMissingEmployee()
            throws Exception {

        mockMvc.perform(
                put("/api/employees/{id}",
                        Long.MAX_VALUE)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployeeRequest())
        )
                .andExpect(status().isNotFound());
    }

    // Asset and employee integration test

    @Test
    void employeeNameUpdatePreservesAssetRelationship()
            throws Exception {

        Employee employee = createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        Asset asset = createAssignedAsset(employee);

        String request = """
                {
                    "name": "John Smith Updated",
                    "email": "john@example.com",
                    "department": "Engineering"
                }
                """;

        mockMvc.perform(
                put("/api/employees/{id}",
                        employee.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedAssetCount")
                        .value(1));

        mockMvc.perform(
                get("/api/assets/{id}", asset.getId())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedTo")
                        .value("John Smith Updated"))
                .andExpect(jsonPath("$.employeeId")
                        .value(employee.getId().intValue()))
                .andExpect(jsonPath("$.status")
                        .value("ASSIGNED"));
    }

    // Employee deletion tests

    @Test
    void deleteEmployeeSuccessfully()
            throws Exception {

        Employee employee = createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        mockMvc.perform(
                delete("/api/employees/{id}",
                        employee.getId())
                        .with(csrf())
        )
                .andExpect(status().isNoContent());

        assertFalse(
                employeeRepository.existsById(
                        employee.getId())
        );
    }

    @Test
    void preventDeletionOfEmployeeWithAssignedAssets()
            throws Exception {

        Employee employee = createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        createAssignedAsset(employee);

        mockMvc.perform(
                delete("/api/employees/{id}",
                        employee.getId())
                        .with(csrf())
        )
                .andExpect(status().isConflict());

        assertTrue(
                employeeRepository.existsById(
                        employee.getId())
        );

        assertEquals(1, assetRepository.count());
    }

    @Test
    void allowEmployeeDeletionAfterAssetUnassignment()
            throws Exception {

        Employee employee = createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        Asset asset = createAssignedAsset(employee);

        mockMvc.perform(
                post("/api/assets/{id}/unassign",
                        asset.getId())
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("AVAILABLE"));

        mockMvc.perform(
                delete("/api/employees/{id}",
                        employee.getId())
                        .with(csrf())
        )
                .andExpect(status().isNoContent());

        assertFalse(
                employeeRepository.existsById(
                        employee.getId())
        );

        assertTrue(
                assetRepository.existsById(asset.getId())
        );
    }

    @Test
    void returnNotFoundWhenDeletingMissingEmployee()
            throws Exception {

        mockMvc.perform(
                delete("/api/employees/{id}",
                        Long.MAX_VALUE)
                        .with(csrf())
        )
                .andExpect(status().isNotFound());
    }

    // Department and statistics tests

    @Test
    void retrieveDistinctDepartments()
            throws Exception {

        createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        createEmployee(
                "Jane Doe",
                "jane@example.com",
                "Engineering"
        );

        createEmployee(
                "Alex Brown",
                "alex@example.com",
                "Operations"
        );

        mockMvc.perform(
                get("/api/employees/departments")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(2))
                .andExpect(jsonPath("$[0]")
                        .value("Engineering"))
                .andExpect(jsonPath("$[1]")
                        .value("Operations"));
    }

    @Test
    void retrieveAccurateEmployeeStatistics()
            throws Exception {

        createEmployee(
                "John Smith",
                "john@example.com",
                "Engineering"
        );

        createEmployee(
                "Jane Doe",
                "jane@example.com",
                "Engineering"
        );

        createEmployee(
                "Alex Brown",
                "alex@example.com",
                "Operations"
        );

        mockMvc.perform(
                get("/api/employees/statistics")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees")
                        .value(3))
                .andExpect(jsonPath("$.totalDepartments")
                        .value(2))
                .andExpect(jsonPath(
                        "$.employeesByDepartment.Engineering")
                        .value(2))
                .andExpect(jsonPath(
                        "$.employeesByDepartment.Operations")
                        .value(1));
    }
}
