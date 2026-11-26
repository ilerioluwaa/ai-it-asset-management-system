
package com.cmsc495.assetmanagement.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cmsc495.assetmanagement.dto.AssetRequest;
import com.cmsc495.assetmanagement.model.Asset;
import com.cmsc495.assetmanagement.model.AssetStatus;
import com.cmsc495.assetmanagement.model.Employee;
import com.cmsc495.assetmanagement.repository.AssetRepository;
import com.cmsc495.assetmanagement.repository.EmployeeRepository;

@Service
@Transactional(readOnly = true)
public class AssetService {

    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;

    public AssetService(
            AssetRepository assetRepository,
            EmployeeRepository employeeRepository) {

        this.assetRepository = assetRepository;
        this.employeeRepository = employeeRepository;
    }

    // Retrieve assets with search, filtering and pagination

    public Page<AssetResponse> getAssets(
            String keyword,
            AssetStatus status,
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
                Sort.by(Sort.Direction.DESC, "id"));

        String searchKeyword =
                keyword == null || keyword.isBlank()
                        ? null
                        : keyword.trim();

        return assetRepository
                .searchAssets(searchKeyword, status, pageable)
                .map(this::toResponse);
    }

    // Retrieve an individual asset

    public AssetResponse getAssetById(Long id) {
        return toResponse(findAsset(id));
    }

    // Register a new asset

    @Transactional
    public AssetResponse createAsset(AssetRequest request) {

        validateRequest(request);

        String serialNumber = normalizeSerialNumber(
                request.serialNumber());

        if (assetRepository
                .existsBySerialNumberIgnoreCase(serialNumber)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An asset with this serial number already exists");
        }

        Asset asset = new Asset();

        asset.setName(request.name().trim());
        asset.setCategory(request.category().trim());
        asset.setSerialNumber(serialNumber);

        applyAssignment(
                asset,
                request.status(),
                request.employeeId());

        return toResponse(
                assetRepository.saveAndFlush(asset));
    }

    // Update an existing asset

    @Transactional
    public AssetResponse updateAsset(
            Long id,
            AssetRequest request) {

        validateRequest(request);

        Asset asset = findAsset(id);

        String serialNumber = normalizeSerialNumber(
                request.serialNumber());

        if (assetRepository
                .existsBySerialNumberIgnoreCaseAndIdNot(
                        serialNumber, id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another asset already uses this serial number");
        }

        // Retired assets cannot return to active service.

        if (asset.getStatus() == AssetStatus.RETIRED
                && request.status() != AssetStatus.RETIRED) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Retired assets cannot be returned to active service");
        }

        asset.setName(request.name().trim());
        asset.setCategory(request.category().trim());
        asset.setSerialNumber(serialNumber);

        Long employeeId = request.employeeId();

        // Preserve the existing employee assignment when
        // an assigned asset is edited without a new employee ID.

        if (request.status() == AssetStatus.ASSIGNED
                && employeeId == null
                && asset.getAssignedEmployee() != null) {

            employeeId = asset
                    .getAssignedEmployee()
                    .getId();
        }

        applyAssignment(
                asset,
                request.status(),
                employeeId);

        return toResponse(
                assetRepository.saveAndFlush(asset));
    }

    // Assign an asset to an employee

    @Transactional
    public AssetResponse assignAsset(
            Long assetId,
            Long employeeId) {

        Asset asset = findAsset(assetId);

        if (asset.getStatus() != AssetStatus.AVAILABLE) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only available assets can be allocated");
        }

        Employee employee = findEmployee(employeeId);

        asset.setAssignedEmployee(employee);
        asset.setStatus(AssetStatus.ASSIGNED);

        return toResponse(
                assetRepository.saveAndFlush(asset));
    }

    // Return an assigned asset to inventory

    @Transactional
    public AssetResponse unassignAsset(Long assetId) {

        Asset asset = findAsset(assetId);

        if (asset.getStatus() != AssetStatus.ASSIGNED) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This asset is not currently assigned");
        }

        asset.setAssignedEmployee(null);
        asset.setStatus(AssetStatus.AVAILABLE);

        return toResponse(
                assetRepository.saveAndFlush(asset));
    }

    // Delete an asset safely

    @Transactional
    public void deleteAsset(Long id) {

        Asset asset = findAsset(id);

        if (asset.getStatus() == AssetStatus.ASSIGNED
                || asset.getAssignedEmployee() != null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Return the asset before deleting it");
        }

        assetRepository.delete(asset);
    }

    // Retrieve dashboard inventory statistics

    public Map<String, Object> getDashboardStatistics() {

        Map<String, Object> statistics =
                new LinkedHashMap<>();

        Map<String, Long> statusCounts =
                new LinkedHashMap<>();

        for (AssetStatus status : AssetStatus.values()) {
            statusCounts.put(status.name(), 0L);
        }

        for (Object[] row :
                assetRepository.countAssetsByStatus()) {

            AssetStatus status = (AssetStatus) row[0];

            long count = ((Number) row[1]).longValue();

            statusCounts.put(status.name(), count);
        }

        Map<String, Long> categoryCounts =
                new LinkedHashMap<>();

        for (Object[] row :
                assetRepository.countAssetsByCategory()) {

            categoryCounts.put(
                    (String) row[0],
                    ((Number) row[1]).longValue());
        }

        long totalAssets = statusCounts.values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();

        statistics.put("totalAssets", totalAssets);

        statistics.put(
                "availableAssets",
                statusCounts.get("AVAILABLE"));

        statistics.put(
                "assignedAssets",
                statusCounts.get("ASSIGNED"));

        statistics.put(
                "maintenanceAssets",
                statusCounts.get("MAINTENANCE"));

        statistics.put(
                "retiredAssets",
                statusCounts.get("RETIRED"));

        statistics.put(
                "totalEmployees",
                employeeRepository.count());

        statistics.put(
                "assetsByStatus",
                statusCounts);

        statistics.put(
                "assetsByCategory",
                categoryCounts);

        return statistics;
    }

    // Internal asset lookup

    private Asset findAsset(Long id) {

        return assetRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Asset not found: " + id));
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

    // Enforce asset assignment consistency

    private void applyAssignment(
            Asset asset,
            AssetStatus status,
            Long employeeId) {

        if (status == AssetStatus.ASSIGNED) {

            if (employeeId == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "An assigned asset requires an employee");
            }

            asset.setAssignedEmployee(
                    findEmployee(employeeId));

        } else {

            if (employeeId != null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Only assigned assets can have an employee");
            }

            asset.setAssignedEmployee(null);
        }

        asset.setStatus(status);
    }

    // Normalize serial numbers consistently

    private String normalizeSerialNumber(
            String serialNumber) {

        return serialNumber.trim()
                .toUpperCase(Locale.ROOT);
    }

    // Additional service-level input validation

    private void validateRequest(AssetRequest request) {

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Asset information is required");
        }

        validateText(
                request.name(),
                150,
                "Asset name");

        validateText(
                request.category(),
                100,
                "Asset category");

        validateText(
                request.serialNumber(),
                100,
                "Serial number");

        if (request.status() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Asset status is required");
        }
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

    // Convert database entities into safe API responses

    private AssetResponse toResponse(Asset asset) {

        Employee employee = asset.getAssignedEmployee();

        return new AssetResponse(
                asset.getId(),
                asset.getName(),
                asset.getCategory(),
                asset.getSerialNumber(),
                asset.getStatus(),
                employee == null
                        ? null : employee.getId(),
                employee == null
                        ? null : employee.getName());
    }

    // API response data transfer object

    public record AssetResponse(
            Long id,
            String name,
            String category,
            String serialNumber,
            AssetStatus status,
            Long employeeId,
            String assignedTo) {
    }
}
