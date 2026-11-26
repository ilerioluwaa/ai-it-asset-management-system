
package com.cmsc495.assetmanagement.dto;

import com.cmsc495.assetmanagement.model.AssetStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Represents incoming asset registration
 * and update requests.
 */
public record AssetRequest(

        @NotBlank(message = "Asset name is required")
        @Size(
            max = 150,
            message = "Asset name cannot exceed 150 characters"
        )
        String name,

        @NotBlank(message = "Asset category is required")
        @Size(
            max = 100,
            message = "Category cannot exceed 100 characters"
        )
        String category,

        @NotBlank(message = "Serial number is required")
        @Size(
            max = 100,
            message = "Serial number cannot exceed 100 characters"
        )
        String serialNumber,

        @NotNull(message = "Asset status is required")
        AssetStatus status,

        @Positive(message = "Employee ID must be positive")
        Long employeeId

) {
}
