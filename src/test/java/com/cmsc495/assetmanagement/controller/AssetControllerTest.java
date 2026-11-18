
package com.cmsc495.assetmanagement.controller;

import com.cmsc495.assetmanagement.model.Asset;
import com.cmsc495.assetmanagement.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssetRepository assetRepository;

    @BeforeEach
    void setUp() {
        assetRepository.deleteAll();
    }

    @Test
    void getAllAssetsReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void createAssetReturnsSavedAsset() throws Exception {
        String json = """
                {
                    "name": "Dell Latitude 5420",
                    "category": "Laptop",
                    "serialNumber": "TEST-001",
                    "status": "Available"
                }
                """;

        mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dell Latitude 5420"))
                .andExpect(jsonPath("$.status").value("Available"));
    }

    @Test
    void getAssetByIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/assets/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMissingAssetReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/assets/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Updated Laptop",
                                    "category": "Laptop",
                                    "serialNumber": "TEST-002",
                                    "status": "Assigned"
                                }
                                """))
                .andExpect(status().isNotFound());
    }
}
