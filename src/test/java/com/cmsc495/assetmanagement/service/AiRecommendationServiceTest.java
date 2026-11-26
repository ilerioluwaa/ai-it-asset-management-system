
package com.cmsc495.assetmanagement.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.sun.net.httpserver.HttpServer;

class AiRecommendationServiceTest {

    private AssetService assetService;

    private EmployeeService employeeService;

    private HttpServer mockServer;

    private String baseUrl;

    private final AtomicReference<String> capturedRequest =
            new AtomicReference<>();

    private final AtomicReference<String> capturedAuthorization =
            new AtomicReference<>();

    private final AtomicReference<String> capturedMethod =
            new AtomicReference<>();

    private int providerStatus;

    private String providerResponse;

    @BeforeEach
    void setUp() throws IOException {

        assetService = mock(AssetService.class);

        employeeService = mock(EmployeeService.class);

        Map<String, Object> assetStatistics =
                new LinkedHashMap<>();

        assetStatistics.put("totalAssets", 10L);

        assetStatistics.put("availableAssets", 4L);

        assetStatistics.put("assignedAssets", 3L);

        assetStatistics.put("maintenanceAssets", 2L);

        assetStatistics.put("retiredAssets", 1L);

        assetStatistics.put("totalEmployees", 5L);

        Map<String, Object> employeeStatistics =
                new LinkedHashMap<>();

        employeeStatistics.put("totalEmployees", 5L);

        employeeStatistics.put("totalDepartments", 2L);

        when(assetService.getDashboardStatistics())
                .thenReturn(assetStatistics);

        when(employeeService.getEmployeeStatistics())
                .thenReturn(employeeStatistics);

        providerStatus = 200;

        providerResponse = """
                {
                    "choices": [
                        {
                            "message": {
                                "content": "Review the two assets under maintenance before purchasing additional equipment."
                            }
                        }
                    ]
                }
                """;

        mockServer = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0),
                0
        );

        mockServer.createContext(
                "/v1/chat/completions",
                exchange -> {

                    try {

                        capturedMethod.set(
                                exchange.getRequestMethod()
                        );

                        capturedAuthorization.set(
                                exchange.getRequestHeaders()
                                        .getFirst("Authorization")
                        );

                        capturedRequest.set(
                                new String(
                                        exchange.getRequestBody()
                                                .readAllBytes(),
                                        StandardCharsets.UTF_8
                                )
                        );

                        byte[] responseBytes =
                                providerResponse.getBytes(
                                        StandardCharsets.UTF_8
                                );

                        exchange.getResponseHeaders().set(
                                "Content-Type",
                                "application/json"
                        );

                        exchange.sendResponseHeaders(
                                providerStatus,
                                responseBytes.length
                        );

                        exchange.getResponseBody()
                                .write(responseBytes);

                    } finally {

                        exchange.close();
                    }
                }
        );

        mockServer.start();

        baseUrl = "http://127.0.0.1:"
                + mockServer.getAddress().getPort()
                + "/v1";
    }

    @AfterEach
    void tearDown() {

        if (mockServer != null) {

            mockServer.stop(0);
        }
    }

    // Test helper

    private AiRecommendationService createService(
            boolean enabled,
            String apiKey,
            String model,
            String url) {

        return new AiRecommendationService(
                assetService,
                employeeService,
                enabled,
                apiKey,
                model,
                url
        );
    }

    private AiRecommendationService configuredService() {

        return createService(
                true,
                "test-api-key",
                "test-model",
                baseUrl
        );
    }

    // AI configuration tests

    @Test
    void reportDisabledAiService() {

        AiRecommendationService service =
                createService(
                        false,
                        "",
                        "",
                        ""
                );

        Map<String, Object> status =
                service.getAiStatus();

        assertEquals(
                false,
                status.get("enabled")
        );

        assertEquals(
                false,
                status.get("configured")
        );

        assertEquals(
                true,
                status.get("humanReviewRequired")
        );
    }

    @Test
    void reportConfiguredAiService() {

        Map<String, Object> status =
                configuredService().getAiStatus();

        assertEquals(
                true,
                status.get("enabled")
        );

        assertEquals(
                true,
                status.get("configured")
        );

        assertEquals(
                "test-model",
                status.get("model")
        );

        assertEquals(
                true,
                status.get("humanReviewRequired")
        );
    }

    @Test
    void reportMissingModelConfiguration() {

        AiRecommendationService service =
                createService(
                        true,
                        "test-api-key",
                        "",
                        baseUrl
                );

        Map<String, Object> status =
                service.getAiStatus();

        assertEquals(
                false,
                status.get("configured")
        );
    }

    @Test
    void reportMissingProviderUrl() {

        AiRecommendationService service =
                createService(
                        true,
                        "test-api-key",
                        "test-model",
                        ""
                );

        Map<String, Object> status =
                service.getAiStatus();

        assertEquals(
                false,
                status.get("configured")
        );
    }

    @Test
    void rejectRecommendationWhenAiDisabled() {

        AiRecommendationService service =
                createService(
                        false,
                        "",
                        "",
                        ""
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        service::generateRecommendations
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getStatusCode()
        );

        assertNull(capturedRequest.get());
    }

    @Test
    void rejectRecommendationWithoutModel() {

        AiRecommendationService service =
                createService(
                        true,
                        "test-api-key",
                        "",
                        baseUrl
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        service::generateRecommendations
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getStatusCode()
        );

        assertNull(capturedRequest.get());
    }

    @Test
    void rejectRecommendationWithoutProviderUrl() {

        AiRecommendationService service =
                createService(
                        true,
                        "test-api-key",
                        "test-model",
                        ""
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        service::generateRecommendations
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getStatusCode()
        );
    }

    // AI provider communication tests

    @Test
    void generateRecommendationsSuccessfully() {

        AiRecommendationService.AiRecommendationResponse result =
                configuredService()
                        .generateRecommendations();

        assertNotNull(result);

        assertNotNull(result.recommendation());

        assertTrue(
                result.recommendation()
                        .contains("maintenance")
        );

        assertEquals(
                "test-model",
                result.model()
        );

        assertNotNull(result.generatedAt());

        assertTrue(result.responseTimeMs() >= 0);

        assertTrue(result.humanReviewRequired());
    }

    @Test
    void sendPostRequestToAiProvider() {

        configuredService()
                .generateRecommendations();

        assertEquals(
                "POST",
                capturedMethod.get()
        );
    }

    @Test
    void includeConfiguredModelInProviderRequest() {

        configuredService()
                .generateRecommendations();

        assertNotNull(capturedRequest.get());

        assertTrue(
                capturedRequest.get()
                        .contains("test-model")
        );
    }

    @Test
    void sendAuthorizationHeaderWhenApiKeyConfigured() {

        configuredService()
                .generateRecommendations();

        assertEquals(
                "Bearer test-api-key",
                capturedAuthorization.get()
        );
    }

    @Test
    void supportLocalProviderWithoutApiKey() {

        AiRecommendationService service =
                createService(
                        true,
                        "",
                        "local-test-model",
                        baseUrl
                );

        AiRecommendationService.AiRecommendationResponse result =
                service.generateRecommendations();

        assertNotNull(result.recommendation());

        assertEquals(
                "local-test-model",
                result.model()
        );

        assertNull(capturedAuthorization.get());
    }

    // Inventory integration tests

    @Test
    void retrieveStatisticsFromBothModules() {

        configuredService()
                .generateRecommendations();

        verify(assetService, times(1))
                .getDashboardStatistics();

        verify(employeeService, times(1))
                .getEmployeeStatistics();
    }

    @Test
    void includeInventoryStatisticsInRecommendationResponse() {

        AiRecommendationService.AiRecommendationResponse result =
                configuredService()
                        .generateRecommendations();

        Map<String, Object> inventory =
                result.inventoryData();

        assertEquals(
                10L,
                ((Number) inventory.get("totalAssets"))
                        .longValue()
        );

        assertEquals(
                4L,
                ((Number) inventory.get("availableAssets"))
                        .longValue()
        );

        assertEquals(
                3L,
                ((Number) inventory.get("assignedAssets"))
                        .longValue()
        );

        assertEquals(
                2L,
                ((Number) inventory.get("maintenanceAssets"))
                        .longValue()
        );

        assertEquals(
                1L,
                ((Number) inventory.get("retiredAssets"))
                        .longValue()
        );

        assertEquals(
                5L,
                ((Number) inventory.get("totalEmployees"))
                        .longValue()
        );

        assertEquals(
                2L,
                ((Number) inventory.get("totalDepartments"))
                        .longValue()
        );
    }

    // Responsible AI and privacy tests

    @Test
    void excludeSensitiveInformationFromProviderRequest() {

        Map<String, Object> assetStatistics =
                new LinkedHashMap<>(
                        assetService.getDashboardStatistics()
                );

        assetStatistics.put(
                "serialNumber",
                "PRIVATE-SERIAL-123"
        );

        assetStatistics.put(
                "assignedTo",
                "Confidential Employee Name"
        );

        Map<String, Object> employeeStatistics =
                new LinkedHashMap<>(
                        employeeService.getEmployeeStatistics()
                );

        employeeStatistics.put(
                "email",
                "private.employee@example.com"
        );

        employeeStatistics.put(
                "name",
                "Confidential Employee Name"
        );

        when(assetService.getDashboardStatistics())
                .thenReturn(assetStatistics);

        when(employeeService.getEmployeeStatistics())
                .thenReturn(employeeStatistics);

        configuredService()
                .generateRecommendations();

        String request = capturedRequest.get();

        assertNotNull(request);

        assertFalse(
                request.contains("PRIVATE-SERIAL-123")
        );

        assertFalse(
                request.contains("Confidential Employee Name")
        );

        assertFalse(
                request.contains("private.employee@example.com")
        );
    }

    @Test
    void requireHumanReviewForRecommendations() {

        AiRecommendationService.AiRecommendationResponse result =
                configuredService()
                        .generateRecommendations();

        assertTrue(
                result.humanReviewRequired()
        );
    }

    @Test
    void preventAiFromModifyingInventoryRecords() {

        configuredService()
                .generateRecommendations();

        verify(assetService)
                .getDashboardStatistics();

        verify(employeeService)
                .getEmployeeStatistics();

        verifyNoMoreInteractions(
                assetService,
                employeeService
        );
    }

    @Test
    void excludeApiKeyFromRecommendationResponse() {

        AiRecommendationService.AiRecommendationResponse result =
                configuredService()
                        .generateRecommendations();

        assertFalse(
                result.toString()
                        .contains("test-api-key")
        );
    }

    // Provider failure tests

    @Test
    void handleProviderServerError() {

        providerStatus = 500;

        providerResponse = """
                {
                    "error": "Internal provider error"
                }
                """;

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> configuredService()
                                .generateRecommendations()
                );

        assertEquals(
                HttpStatus.BAD_GATEWAY,
                exception.getStatusCode()
        );
    }

    @Test
    void handleProviderAuthenticationFailure() {

        providerStatus = 401;

        providerResponse = """
                {
                    "error": "Invalid provider credentials"
                }
                """;

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> configuredService()
                                .generateRecommendations()
                );

        assertEquals(
                HttpStatus.BAD_GATEWAY,
                exception.getStatusCode()
        );
    }

    @Test
    void handleProviderRateLimit() {

        providerStatus = 429;

        providerResponse = """
                {
                    "error": "Rate limit exceeded"
                }
                """;

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> configuredService()
                                .generateRecommendations()
                );

        assertEquals(
                HttpStatus.BAD_GATEWAY,
                exception.getStatusCode()
        );
    }

    @Test
    void rejectEmptyProviderChoices() {

        providerResponse = """
                {
                    "choices": []
                }
                """;

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> configuredService()
                                .generateRecommendations()
                );

        assertEquals(
                HttpStatus.BAD_GATEWAY,
                exception.getStatusCode()
        );
    }

    @Test
    void rejectBlankRecommendation() {

        providerResponse = """
                {
                    "choices": [
                        {
                            "message": {
                                "content": " "
                            }
                        }
                    ]
                }
                """;

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> configuredService()
                                .generateRecommendations()
                );

        assertEquals(
                HttpStatus.BAD_GATEWAY,
                exception.getStatusCode()
        );
    }

    @Test
    void handleMalformedProviderResponse() {

        providerResponse = "Invalid JSON response";

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> configuredService()
                                .generateRecommendations()
                );

        assertEquals(
                HttpStatus.BAD_GATEWAY,
                exception.getStatusCode()
        );
    }

    // Provider URL security tests

    @Test
    void rejectInsecureRemoteProviderUrl() {

        AiRecommendationService service =
                createService(
                        true,
                        "test-api-key",
                        "test-model",
                        "http://example.com/v1"
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        service::generateRecommendations
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getStatusCode()
        );

        assertNull(capturedRequest.get());
    }

    @Test
    void rejectUnsupportedProviderProtocol() {

        AiRecommendationService service =
                createService(
                        true,
                        "test-api-key",
                        "test-model",
                        "ftp://example.com/v1"
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        service::generateRecommendations
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getStatusCode()
        );
    }

    @Test
    void rejectProviderUrlContainingCredentials() {

        AiRecommendationService service =
                createService(
                        true,
                        "test-api-key",
                        "test-model",
                        "https://user:password@example.com/v1"
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        service::generateRecommendations
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getStatusCode()
        );
    }
}
