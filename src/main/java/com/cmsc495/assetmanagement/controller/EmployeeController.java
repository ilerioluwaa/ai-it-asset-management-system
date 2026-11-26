
package com.cmsc495.assetmanagement.controller;

import java.net.URI;
import java.util.List;
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

import com.cmsc495.assetmanagement.dto.EmployeeRequest;
import com.cmsc495.assetmanagement.service.EmployeeService;
import com.cmsc495.assetmanagement.service.EmployeeService.EmployeeResponse;

import jakarta.validation.Valid;

/**
 * REST API controller for employee management.
 *
 * Delegates employee validation, business logic,
 * and database operations to EmployeeService.
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(
            EmployeeService employeeService) {

        this.employeeService = employeeService;
    }

    // Retrieve employees with search and pagination

    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>>
            getEmployees(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            String department,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(
                employeeService.getEmployees(
                        keyword,
                        department,
                        page,
                        size));
    }

    // Retrieve employee statistics

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>>
            getStatistics() {

        return ResponseEntity.ok(
                employeeService.getEmployeeStatistics());
    }

    // Retrieve distinct department names

    @GetMapping("/departments")
    public ResponseEntity<List<String>>
            getDepartments() {

        return ResponseEntity.ok(
                employeeService.getDepartments());
    }

    // Retrieve an individual employee

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse>
            getEmployeeById(

            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeService.getEmployeeById(id));
    }

    // Register a new employee

    @PostMapping
    public ResponseEntity<EmployeeResponse>
            createEmployee(

            @Valid @RequestBody
            EmployeeRequest request) {

        EmployeeResponse createdEmployee =
                employeeService.createEmployee(request);

        URI location = URI.create(
                "/api/employees/" + createdEmployee.id());

        return ResponseEntity
                .created(location)
                .body(createdEmployee);
    }

    // Update an existing employee

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse>
            updateEmployee(

            @PathVariable Long id,

            @Valid @RequestBody
            EmployeeRequest request) {

        return ResponseEntity.ok(
                employeeService.updateEmployee(
                        id,
                        request));
    }

    // Delete an employee without assigned assets

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id) {

        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }
}
