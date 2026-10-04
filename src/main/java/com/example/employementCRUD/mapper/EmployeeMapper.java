package com.example.employementCRUD.mapper;

import org.springframework.stereotype.Component;

import com.example.employementCRUD.dto.EmployeeRequestDTO;
import com.example.employementCRUD.dto.EmployeeResponseDTO;
import com.example.employementCRUD.entity.Employee;

@Component  // This annotation indicates that this class is a Spring component and can be automatically detected and registered as a bean in the Spring application context.
public class EmployeeMapper {
	
	 public Employee toEntity(EmployeeRequestDTO request) {

	        Employee employee = new Employee();

	        employee.setName(request.getName());
	        employee.setEmail(request.getEmail());
	        employee.setDepartment(request.getDepartment());
	        employee.setSalary(request.getSalary());

	        return employee;

	 }
	 
	 public EmployeeResponseDTO toResponseDTO(Employee employee) {

	        EmployeeResponseDTO response = new EmployeeResponseDTO();

	        response.setId(employee.getId());
	        response.setName(employee.getName());
	        response.setEmail(employee.getEmail());
	        response.setDepartment(employee.getDepartment());
	        response.setSalary(employee.getSalary());
	        response.setCreatedAt(employee.getCreatedAt());
	        response.setUpdatedAt(employee.getUpdatedAt());

	        return response;
	    }
}	 
	 
