package com.cmsc495.assetmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmsc495.assetmanagement.model.Employee;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

}