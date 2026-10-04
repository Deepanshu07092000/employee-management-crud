package com.example.employementCRUD.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.employementCRUD.dto.EmployeePatchRequestDTO;
import com.example.employementCRUD.dto.EmployeeRequestDTO;
import com.example.employementCRUD.dto.EmployeeResponseDTO;
import com.example.employementCRUD.entity.Employee;
import com.example.employementCRUD.mapper.EmployeeMapper;
import com.example.employementCRUD.repository.EmployeeRepository;


import com.example.employementCRUD.exception.DuplicateEmployeeException;
import com.example.employementCRUD.exception.EmployeeNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
	
	private final EmployeeRepository employeeRepository;
	private final EmployeeMapper employeeMapper;
	
	
	 //--------- CREATE
    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {

        log.info("Creating new employee with email: {}", request.getEmail());
        
     // Check whether email already exists
        if (employeeRepository.existsByEmail(request.getEmail())) {

            log.warn("Employee already exists with email: {}",
                    request.getEmail());

            throw new DuplicateEmployeeException("Employee already exists with email: "+ request.getEmail());
        }

        // Convert DTO to Entity
        Employee employee = employeeMapper.toEntity(request);

        // Save Entity into database
        Employee savedEmployee = employeeRepository.save(employee);

        log.info("Employee created successfully with id: {}", savedEmployee.getId());

        // Convert Entity to Response DTO
        return employeeMapper.toResponseDTO(savedEmployee);
    }
    
 //---------- GET BY ID
    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {

        log.info("Fetching employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                	.orElseThrow(() -> {
                    log.warn("Employee not found with id: {}", id);
                    return new EmployeeNotFoundException("Employee not found with id: " + id);
                });

        return employeeMapper.toResponseDTO(employee);
    }

    //---------- GET ALL
    @Override
    public Page<EmployeeResponseDTO> getAllEmployees(int page,int size,String sortBy,String sortDir) {

        log.info(
                "Fetching employees - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page,
                size,
                sortBy,
                sortDir
        );

        Sort sort;

        if ("desc".equalsIgnoreCase(sortDir)) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employeePage =
                employeeRepository.findAll(pageable);

        return employeePage.map(
                employee -> employeeMapper.toResponseDTO(employee)
        );
    }

 //------------ UPDATE
    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO request) {

        log.info("Updating employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with id: {}", id);

                    return new EmployeeNotFoundException("Employee not found with id: " + id);
                });

        // Update existing entity fields
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());

        // Save updated entity
        Employee updatedEmployee = employeeRepository.save(employee);

        log.info("Employee updated successfully with id: {}", id);

        return employeeMapper.toResponseDTO(updatedEmployee);
    }
    
    //------------ DELETE
    @Override
    public void deleteEmployee(Long id) {

        log.info("Deleting employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with id: {}", id);

                    return new EmployeeNotFoundException("Employee not found with id: " + id);
                });

        employeeRepository.delete(employee);

        log.info("Employee deleted successfully with id: {}", id);
    }
    
    //------------ PARTIAL UPDATE
    @Override
    public EmployeeResponseDTO patchEmployee(Long id,EmployeePatchRequestDTO request) {

        log.info("Partially updating employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with id: " + id));

        if (request.getName() != null) {
            employee.setName(request.getName());
        }

        if (request.getEmail() != null) {

            if (!request.getEmail().equals(employee.getEmail()) && employeeRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmployeeException("Employee with email already exists: " + request.getEmail());
            }

            employee.setEmail(request.getEmail());
        }

        if (request.getDepartment() != null) {
            employee.setDepartment(request.getDepartment());
        }

        if (request.getSalary() != null) {
            employee.setSalary(request.getSalary());
        }

        Employee updatedEmployee = employeeRepository.save(employee);

        log.info("Employee partially updated successfully with id: {}", id);

        return employeeMapper.toResponseDTO(updatedEmployee);
    }

}
