
package com.cmsc495.assetmanagement.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
class SecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void configureMockMvc() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // Test helper methods

    private String validAssetRequest() {
        return """
                {
                    "name": "Security Test Laptop",
                    "category": "Laptop",
                    "serialNumber": "SECURITY-001",
                    "status": "AVAILABLE"
                }
                """;
    }

    private String validEmployeeRequest() {
        return """
                {
                    "name": "Security Test Employee",
                    "email": "security@example.com",
                    "department": "Information Technology"
                }
                """;
    }

    // Authentication tests

    @Test
    void redirectUnauthenticatedUserToLogin()
            throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(unauthenticated());
    }

    @Test
    void allowPublicAccessToLoginPage()
            throws Exception {

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void authenticateAdministratorWithValidCredentials()
            throws Exception {

        mockMvc.perform(
                formLogin()
                        .user("testadmin")
                        .password("TestAdmin123!")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(
                        authenticated()
                                .withUsername("testadmin")
                                .withRoles("ADMIN")
                );
    }

    @Test
    void rejectInvalidAdministratorPassword()
            throws Exception {

        mockMvc.perform(
                formLogin()
                        .user("testadmin")
                        .password("IncorrectPassword")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void rejectUnknownUsername()
            throws Exception {

        mockMvc.perform(
                formLogin()
                        .user("unknown-user")
                        .password("TestAdmin123!")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    // Unauthenticated API access tests

    @Test
    void rejectUnauthenticatedAssetRetrieval()
            throws Exception {

        mockMvc.perform(
                get("/api/assets")
                        .servletPath("/api/assets")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectUnauthenticatedEmployeeRetrieval()
            throws Exception {

        mockMvc.perform(
                get("/api/employees")
                        .servletPath("/api/employees")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectUnauthenticatedAssetCreation()
            throws Exception {

        mockMvc.perform(
                post("/api/assets")
                        .servletPath("/api/assets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssetRequest())
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectUnauthenticatedEmployeeCreation()
            throws Exception {

        mockMvc.perform(
                post("/api/employees")
                        .servletPath("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployeeRequest())
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectUnauthenticatedAiStatusAccess()
            throws Exception {

        mockMvc.perform(
                get("/api/ai/status")
                        .servletPath("/api/ai/status")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectUnauthenticatedAiRecommendationRequest()
            throws Exception {

        mockMvc.perform(
                post("/api/ai/recommendations")
                        .servletPath("/api/ai/recommendations")
                        .with(csrf())
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectUnauthenticatedCsrfTokenAccess()
            throws Exception {

        mockMvc.perform(
                get("/api/csrf")
                        .servletPath("/api/csrf")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isUnauthorized());
    }

    // Administrator authorization tests

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void allowAdministratorToAccessAssets()
            throws Exception {

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void allowAdministratorToAccessEmployees()
            throws Exception {

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void allowAdministratorToAccessDashboardStatistics()
            throws Exception {

        mockMvc.perform(get("/api/assets/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAssets").isNumber());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void allowAdministratorToAccessAiStatus()
            throws Exception {

        mockMvc.perform(get("/api/ai/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.humanReviewRequired")
                        .value(true));
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void allowAdministratorToRetrieveCsrfToken()
            throws Exception {

        mockMvc.perform(get("/api/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.headerName").isNotEmpty())
                .andExpect(jsonPath("$.parameterName")
                        .value("_csrf"));
    }

    // Unauthorized role tests

    @Test
    @WithMockUser(username = "standard-user", roles = "USER")
    void preventStandardUserFromAccessingAssets()
            throws Exception {

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "standard-user", roles = "USER")
    void preventStandardUserFromAccessingEmployees()
            throws Exception {

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "standard-user", roles = "USER")
    void preventStandardUserFromCreatingAssets()
            throws Exception {

        mockMvc.perform(
                post("/api/assets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssetRequest())
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "standard-user", roles = "USER")
    void preventStandardUserFromUpdatingAssets()
            throws Exception {

        mockMvc.perform(
                put("/api/assets/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssetRequest())
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "standard-user", roles = "USER")
    void preventStandardUserFromDeletingAssets()
            throws Exception {

        mockMvc.perform(
                delete("/api/assets/1")
                        .with(csrf())
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "standard-user", roles = "USER")
    void preventStandardUserFromGeneratingAiRecommendations()
            throws Exception {

        mockMvc.perform(
                post("/api/ai/recommendations")
                        .with(csrf())
        )
                .andExpect(status().isForbidden());
    }

    // CSRF protection tests

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectAssetCreationWithoutCsrfToken()
            throws Exception {

        mockMvc.perform(
                post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssetRequest())
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectAssetUpdateWithoutCsrfToken()
            throws Exception {

        mockMvc.perform(
                put("/api/assets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssetRequest())
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectAssetDeletionWithoutCsrfToken()
            throws Exception {

        mockMvc.perform(delete("/api/assets/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectEmployeeCreationWithoutCsrfToken()
            throws Exception {

        mockMvc.perform(
                post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployeeRequest())
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectAssetAssignmentWithoutCsrfToken()
            throws Exception {

        mockMvc.perform(
                post("/api/assets/1/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "employeeId": 1
                                }
                                """)
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectAiRequestWithoutCsrfToken()
            throws Exception {

        mockMvc.perform(
                post("/api/ai/recommendations")
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectInvalidCsrfToken()
            throws Exception {

        mockMvc.perform(
                post("/api/assets")
                        .with(csrf().useInvalidToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssetRequest())
        )
                .andExpect(status().isForbidden());
    }

    // Security response header tests

    @Test
    @WithMockUser(roles = "ADMIN")
    void includeContentSecurityPolicyHeader()
            throws Exception {

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(header().exists(
                        "Content-Security-Policy"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void preventClickjacking()
            throws Exception {

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "X-Frame-Options", "DENY"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void preventContentTypeSniffing()
            throws Exception {

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "X-Content-Type-Options", "nosniff"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void includeCacheControlHeader()
            throws Exception {

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Cache-Control"));
    }

    // Application health monitoring tests

    @Test
    void allowPublicHealthCheck()
            throws Exception {

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void preventPublicAccessToApplicationInformation()
            throws Exception {

        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // Logout tests

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void logoutAdministratorSuccessfully()
            throws Exception {

        mockMvc.perform(
                post("/logout")
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"))
                .andExpect(unauthenticated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectLogoutWithoutCsrfToken()
            throws Exception {

        mockMvc.perform(post("/logout"))
                .andExpect(status().isForbidden());
    }
}
