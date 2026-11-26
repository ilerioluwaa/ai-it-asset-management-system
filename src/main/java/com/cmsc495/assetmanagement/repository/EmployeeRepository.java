
package com.cmsc495.assetmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cmsc495.assetmanagement.model.Employee;

@Repository
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    // Employee email validation

    Optional<Employee> findByEmailIgnoreCase(
            String email);

    boolean existsByEmailIgnoreCase(
            String email);

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            Long id);

    // Department filtering

    List<Employee> findByDepartmentIgnoreCase(
            String department);

    Page<Employee> findByDepartmentIgnoreCase(
            String department,
            Pageable pageable);

    long countByDepartmentIgnoreCase(
            String department);

    // Employee name lookup

    List<Employee> findByNameContainingIgnoreCase(
            String name);

    // Search, filtering and pagination

    @Query("""
        SELECT e FROM Employee e
        WHERE (
            :keyword IS NULL
            OR LOWER(e.name)
                LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(e.email)
                LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(e.department)
                LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (
            :department IS NULL
            OR LOWER(e.department) = LOWER(:department)
        )
        """)
    Page<Employee> searchEmployees(
            @Param("keyword") String keyword,
            @Param("department") String department,
            Pageable pageable);

    // Department statistics

    @Query("""
        SELECT e.department, COUNT(e)
        FROM Employee e
        GROUP BY e.department
        ORDER BY COUNT(e) DESC
        """)
    List<Object[]> countEmployeesByDepartment();

    // Retrieve distinct departments

    @Query("""
        SELECT DISTINCT e.department
        FROM Employee e
        ORDER BY e.department
        """)
    List<String> findDistinctDepartments();

}
