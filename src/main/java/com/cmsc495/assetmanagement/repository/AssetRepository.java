
package com.cmsc495.assetmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cmsc495.assetmanagement.model.Asset;
import com.cmsc495.assetmanagement.model.AssetStatus;

@Repository
public interface AssetRepository
        extends JpaRepository<Asset, Long> {

    // Serial number validation

    Optional<Asset> findBySerialNumberIgnoreCase(
            String serialNumber);

    boolean existsBySerialNumberIgnoreCase(
            String serialNumber);

    boolean existsBySerialNumberIgnoreCaseAndIdNot(
            String serialNumber,
            Long id);

    // Asset status filtering

    List<Asset> findByStatus(AssetStatus status);

    Page<Asset> findByStatus(
            AssetStatus status,
            Pageable pageable);

    long countByStatus(AssetStatus status);

    // Asset category filtering

    List<Asset> findByCategoryIgnoreCase(
            String category);

    long countByCategoryIgnoreCase(
            String category);

    // Employee assignment management

    List<Asset> findByAssignedEmployee_Id(
            Long employeeId);

    boolean existsByAssignedEmployee_Id(
            Long employeeId);

    long countByAssignedEmployee_Id(
            Long employeeId);

    // Search, filtering and pagination

    @Query("""
        SELECT a FROM Asset a
        WHERE (
            :keyword IS NULL
            OR LOWER(a.name)
                LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(a.category)
                LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(a.serialNumber)
                LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (
            :status IS NULL
            OR a.status = :status
        )
        """)
    Page<Asset> searchAssets(
            @Param("keyword") String keyword,
            @Param("status") AssetStatus status,
            Pageable pageable);

    // Inventory statistics for dashboard and AI analysis

    @Query("""
        SELECT a.category, COUNT(a)
        FROM Asset a
        GROUP BY a.category
        ORDER BY COUNT(a) DESC
        """)
    List<Object[]> countAssetsByCategory();

    @Query("""
        SELECT a.status, COUNT(a)
        FROM Asset a
        GROUP BY a.status
        """)
    List<Object[]> countAssetsByStatus();

}
