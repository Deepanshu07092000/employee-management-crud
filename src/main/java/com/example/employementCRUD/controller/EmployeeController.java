package com.example.employementCRUD.controller;


import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.employementCRUD.dto.EmployeePatchRequestDTO;
import com.example.employementCRUD.dto.EmployeeRequestDTO;
import com.example.employementCRUD.dto.EmployeeResponseDTO;
import com.example.employementCRUD.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {
	
	private final EmployeeService employeeService;
	
	//--------- Create Employee
	//--@Valid is used to validate the request body based on the constraints defined in the EmployeeRequestDTO class.
	@PostMapping
	public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeRequestDTO request) {
		
		log.info("Received request to create employee: {}", request);
		EmployeeResponseDTO response = employeeService.createEmployee(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	//---- Get Employee by ID
	@GetMapping("/{id}")
	public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id){
		
		log.info("Received request to get employee by id: {}", id);
		EmployeeResponseDTO response = employeeService.getEmployeeById(id);
		return ResponseEntity.ok(response);
	}
	
	//---- Get All Employees 
	@GetMapping
	public ResponseEntity<Page<EmployeeResponseDTO>> getAllEmployees(@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "id") String sortBy,
	        @RequestParam(defaultValue = "asc") String sortDir) {

	    log.info(
	            "Received request to fetch employees - page: {}, size: {}, sortBy: {}, sortDir: {}",
	            page,
	            size,
	            sortBy,
	            sortDir
	    );

	    Page<EmployeeResponseDTO> employees =
	            employeeService.getAllEmployees(
	                    page,
	                    size,
	                    sortBy,
	                    sortDir
	            );

	    return ResponseEntity.ok(employees);
	}
	
	//---- Update Employee
	@PutMapping("/{id}")
	public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequestDTO request){
		
		log.info("Received request to update employee with id: {}", id);
		EmployeeResponseDTO response = employeeService.updateEmployee(id, request);
		return ResponseEntity.ok(response);
		
		
	}
	
	//---- Delete Employee
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id){
		
		log.info("Received request to delete employee with id: {}", id);
		employeeService.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}
	
	//-- Partial Update
	@PatchMapping("/{id}")
	public ResponseEntity<EmployeeResponseDTO> patchEmployee(@PathVariable Long id, @Valid @RequestBody EmployeePatchRequestDTO request) {
		
		log.info("Received request to partial update the  employee with id: {}", id);
	    EmployeeResponseDTO response = employeeService.patchEmployee(id, request);
	    return ResponseEntity.ok(response);
	}

}
