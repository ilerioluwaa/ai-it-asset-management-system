
package com.cmsc495.assetmanagement.controller;

import java.util.Map;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmsc495.assetmanagement.service.AiRecommendationService;
import com.cmsc495.assetmanagement.service.AiRecommendationService.AiRecommendationResponse;

/**
 * REST API controller for AI-powered inventory analysis.
 *
 * Provides advisory recommendations based on current
 * asset and employee statistics.
 *
 * AI recommendations cannot directly modify inventory
 * or employee records.
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiRecommendationService
            aiRecommendationService;

    public AiController(
            AiRecommendationService aiRecommendationService) {

        this.aiRecommendationService =
                aiRecommendationService;
    }

    // Retrieve AI configuration status

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>>
            getAiStatus() {

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(
                    aiRecommendationService.getAiStatus());
    }

    // Generate AI-powered inventory recommendations

    @PostMapping("/recommendations")
    public ResponseEntity<AiRecommendationResponse>
            generateRecommendations() {

        AiRecommendationResponse response =
                aiRecommendationService
                        .generateRecommendations();

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(response);
    }
}
