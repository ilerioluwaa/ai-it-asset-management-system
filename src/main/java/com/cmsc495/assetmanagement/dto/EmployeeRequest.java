
package com.cmsc495.assetmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents incoming employee registration
 * and update requests.
 */
public record EmployeeRequest(

        @NotBlank(message = "Employee name is required")
        @Size(
            max = 150,
            message = "Employee name cannot exceed 150 characters"
        )
        String name,

        @NotBlank(message = "Employee email is required")
        @Email(message = "Enter a valid email address")
        @Size(
            max = 254,
            message = "Email cannot exceed 254 characters"
        )
        String email,

        @NotBlank(message = "Department is required")
        @Size(
            max = 100,
            message = "Department cannot exceed 100 characters"
        )
        String department

) {
}
