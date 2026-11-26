
package com.cmsc495.assetmanagement.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cmsc495.assetmanagement.dto.EmployeeRequest;
import com.cmsc495.assetmanagement.model.Employee;
import com.cmsc495.assetmanagement.repository.AssetRepository;
import com.cmsc495.assetmanagement.repository.EmployeeRepository;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AssetRepository assetRepository;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            AssetRepository assetRepository) {

        this.employeeRepository = employeeRepository;
        this.assetRepository = assetRepository;
    }

    // Retrieve employees with search and pagination

    public Page<EmployeeResponse> getEmployees(
            String keyword,
            String department,
            int page,
            int size) {

        if (page < 0 || size < 1 || size > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be non-negative and size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("name").ascending()
                        .and(Sort.by("id").ascending()));

        return employeeRepository.searchEmployees(
                normalizeFilter(keyword),
                normalizeFilter(department),
                pageable)
                .map(this::toResponse);
    }

    // Retrieve an individual employee

    public EmployeeResponse getEmployeeById(Long id) {

        return toResponse(findEmployee(id));
    }

    // Register a new employee

    @Transactional
    public EmployeeResponse createEmployee(
            EmployeeRequest request) {

        validateRequest(request);

        String email = normalizeEmail(request.email());

        if (employeeRepository
                .existsByEmailIgnoreCase(email)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An employee with this email already exists");
        }

        Employee employee = new Employee();

        employee.setName(request.name().trim());
        employee.setEmail(email);
        employee.setDepartment(
                request.department().trim());

        return toResponse(
                employeeRepository.saveAndFlush(employee));
    }

    // Update an existing employee

    @Transactional
    public EmployeeResponse updateEmployee(
            Long id,
            EmployeeRequest request) {

        validateRequest(request);

        Employee employee = findEmployee(id);

        String email = normalizeEmail(request.email());

        if (employeeRepository
                .existsByEmailIgnoreCaseAndIdNot(
                        email, id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another employee already uses this email");
        }

        employee.setName(request.name().trim());
        employee.setEmail(email);
        employee.setDepartment(
                request.department().trim());

        return toResponse(
                employeeRepository.saveAndFlush(employee));
    }

    // Delete an employee safely

    @Transactional
    public void deleteEmployee(Long id) {

        Employee employee = findEmployee(id);

        if (assetRepository
                .existsByAssignedEmployee_Id(id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Return or reassign all employee assets before deleting this employee");
        }

        employeeRepository.delete(employee);
        employeeRepository.flush();
    }

    // Retrieve available department names

    public List<String> getDepartments() {

        return employeeRepository
                .findDistinctDepartments();
    }

    // Retrieve employee statistics

    public Map<String, Object> getEmployeeStatistics() {

        Map<String, Object> statistics =
                new LinkedHashMap<>();

        Map<String, Long> departmentCounts =
                new LinkedHashMap<>();

        for (Object[] row :
                employeeRepository.countEmployeesByDepartment()) {

            departmentCounts.put(
                    (String) row[0],
                    ((Number) row[1]).longValue());
        }

        statistics.put(
                "totalEmployees",
                employeeRepository.count());

        statistics.put(
                "totalDepartments",
                departmentCounts.size());

        statistics.put(
                "employeesByDepartment",
                departmentCounts);

        return statistics;
    }

    // Internal employee lookup

    private Employee findEmployee(Long id) {

        if (id == null || id <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid employee ID is required");
        }

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Employee not found: " + id));
    }

    // Normalize employee email addresses

    private String normalizeEmail(String email) {

        return email.trim()
                .toLowerCase(Locale.ROOT);
    }

    // Normalize optional search filters

    private String normalizeFilter(String value) {

        return value == null || value.isBlank()
                ? null
                : value.trim();
    }

    // Additional service-level input validation

    private void validateRequest(
            EmployeeRequest request) {

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Employee information is required");
        }

        validateText(
                request.name(),
                150,
                "Employee name");

        validateText(
                request.email(),
                254,
                "Employee email");

        validateText(
                request.department(),
                100,
                "Department");
    }

    private void validateText(
            String value,
            int maxLength,
            String fieldName) {

        if (value == null || value.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    fieldName + " is required");
        }

        if (value.trim().length() > maxLength) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    fieldName + " exceeds the maximum length");
        }
    }

    // Convert database entities into API responses

    private EmployeeResponse toResponse(
            Employee employee) {

        long assignedAssetCount =
                assetRepository
                        .countByAssignedEmployee_Id(
                                employee.getId());

        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                assignedAssetCount);
    }

    // Employee API response data transfer object

    public record EmployeeResponse(
            Long id,
            String name,
            String email,
            String department,
            long assignedAssetCount) {
    }
}
