package com.example.employementCRUD.service;

import org.springframework.data.domain.Page;

import com.example.employementCRUD.dto.EmployeePatchRequestDTO;
import com.example.employementCRUD.dto.EmployeeRequestDTO;
import com.example.employementCRUD.dto.EmployeeResponseDTO;

public interface EmployeeService {
	
	// Create a new employee
	EmployeeResponseDTO createEmployee(EmployeeRequestDTO request);

	// Get an employee by ID
	EmployeeResponseDTO getEmployeeById(Long id);
	
	// Get all employees
	Page<EmployeeResponseDTO> getAllEmployees(
	        int page,
	        int size,
	        String sortBy,
	        String sortDir
	);
	
	// Update an existing employee
	EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO request);
	
	// Delete an employee by ID
	void deleteEmployee(Long id);
	
	// Partial update an existing employee
	EmployeeResponseDTO patchEmployee(Long id, EmployeePatchRequestDTO request);

}
