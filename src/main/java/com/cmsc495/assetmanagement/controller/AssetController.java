
package com.cmsc495.assetmanagement.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cmsc495.assetmanagement.dto.AssetRequest;
import com.cmsc495.assetmanagement.model.AssetStatus;
import com.cmsc495.assetmanagement.service.AssetService;
import com.cmsc495.assetmanagement.service.AssetService.AssetResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * REST API controller for IT asset management.
 *
 * Delegates business logic, validation, and database
 * operations to the AssetService layer.
 */
@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    // Retrieve assets with pagination and filtering

    @GetMapping
    public ResponseEntity<Page<AssetResponse>> getAssets(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            AssetStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(
                assetService.getAssets(
                        keyword,
                        status,
                        page,
                        size));
    }

    // Retrieve dashboard statistics

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>>
            getStatistics() {

        return ResponseEntity.ok(
                assetService.getDashboardStatistics());
    }

    // Retrieve an individual asset

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> getAssetById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                assetService.getAssetById(id));
    }

    // Register a new asset

    @PostMapping
    public ResponseEntity<AssetResponse> createAsset(
            @Valid @RequestBody AssetRequest request) {

        AssetResponse createdAsset =
                assetService.createAsset(request);

        URI location = URI.create(
                "/api/assets/" + createdAsset.id());

        return ResponseEntity
                .created(location)
                .body(createdAsset);
    }

    // Update an existing asset

    @PutMapping("/{id}")
    public ResponseEntity<AssetResponse> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequest request) {

        return ResponseEntity.ok(
                assetService.updateAsset(id, request));
    }

    // Assign an available asset to an employee

    @PostMapping("/{id}/assign")
    public ResponseEntity<AssetResponse> assignAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentRequest request) {

        return ResponseEntity.ok(
                assetService.assignAsset(
                        id,
                        request.employeeId()));
    }

    // Return an assigned asset to available inventory

    @PostMapping("/{id}/unassign")
    public ResponseEntity<AssetResponse> unassignAsset(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                assetService.unassignAsset(id));
    }

    // Delete an existing asset

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(
            @PathVariable Long id) {

        assetService.deleteAsset(id);

        return ResponseEntity.noContent().build();
    }

    // Request DTO for employee allocation

    public record AssignmentRequest(

            @NotNull(message = "Employee ID is required")
            @Positive(message = "Employee ID must be positive")
            Long employeeId

    ) {
    }
}
