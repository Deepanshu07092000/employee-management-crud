package com.example.employementCRUD.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employementCRUD.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
	boolean existsByEmail(String email);

}
