package com.example.EmployementCRUD.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.example.employementCRUD.dto.EmployeeRequestDTO;
import com.example.employementCRUD.dto.EmployeeResponseDTO;
import com.example.employementCRUD.entity.Employee;
import com.example.employementCRUD.exception.DuplicateEmployeeException;
import com.example.employementCRUD.exception.EmployeeNotFoundException;
import com.example.employementCRUD.mapper.EmployeeMapper;
import com.example.employementCRUD.repository.EmployeeRepository;
import com.example.employementCRUD.service.EmployeeServiceImpl;

@ExtendWith(MockitoExtension.class)  // This annotation is used to enable Mockito support in JUnit 5 tests.
public class EmployeeServiceTest {
	
	@Mock  // This annotation is used to create a mock/fake instance of the EmployeeRepository class.
	private EmployeeRepository employeeRepository;
	
	@Mock
    private EmployeeMapper employeeMapper;

	@InjectMocks  // This annotation is used to inject the mock dependencies into the EmployeeService class.
	private EmployeeServiceImpl employeeService;

	// Test methods for EmployeeService can be added here
	
	@Test
	void createEmployee_ShouldCreateEmployeeSuccessfully() {

	    // Arrange
	    EmployeeRequestDTO request = new EmployeeRequestDTO(
	            "Abhishek",
	            "abhishek@gmail.com",
	            "IT",
	            75000.0
	    );

	    Employee employee = new Employee();
	    employee.setName("Abhishek");
	    employee.setEmail("abhishek@gmail.com");
	    employee.setDepartment("IT");
	    employee.setSalary(75000.0);

	    Employee savedEmployee = new Employee();
	    savedEmployee.setId(1L);
	    savedEmployee.setName("Abhishek");
	    savedEmployee.setEmail("abhishek@gmail.com");
	    savedEmployee.setDepartment("IT");
	    savedEmployee.setSalary(75000.0);

	    EmployeeResponseDTO response = new EmployeeResponseDTO(
	            1L,
	            "Abhishek",
	            "abhishek@gmail.com",
	            "IT",
	            75000.0,
	            null,
	            null
	    );

	    // Mock repository behavior
	    when(employeeRepository.existsByEmail(request.getEmail()))
	            .thenReturn(false);

	    when(employeeMapper.toEntity(request))
	            .thenReturn(employee);

	    when(employeeRepository.save(employee))
	            .thenReturn(savedEmployee);

	    when(employeeMapper.toResponseDTO(savedEmployee))
	            .thenReturn(response);

	    // Act
	    EmployeeResponseDTO result = employeeService.createEmployee(request);

	    // Assert
	    assertNotNull(result);
	    assertEquals(1L, result.getId());
	    assertEquals("Abhishek", result.getName());
	    assertEquals("abhishek@gmail.com", result.getEmail());

	    // Verify repository methods were called
	    verify(employeeRepository).existsByEmail(request.getEmail());
	    verify(employeeRepository).save(employee);

	    // Verify mapper methods were called
	    verify(employeeMapper).toEntity(request);
	    verify(employeeMapper).toResponseDTO(savedEmployee);
	}
	
	@Test
	void createEmployee_ShouldThrowException_WhenEmailAlreadyExists() {

	    // Arrange
	    EmployeeRequestDTO request = new EmployeeRequestDTO(
	            "Abhishek",
	            "abhishek@gmail.com",
	            "IT",
	            75000.0
	    );

	    when(employeeRepository.existsByEmail(request.getEmail()))
	            .thenReturn(true);

	    // Act + Assert
	    DuplicateEmployeeException exception =
	            assertThrows(
	                    DuplicateEmployeeException.class,
	                    () -> employeeService.createEmployee(request)
	            );

	    assertEquals(
	            "Employee already exists with email: abhishek@gmail.com",
	            exception.getMessage()
	    );

	    // Verify save was never called
	    org.mockito.Mockito.verify(employeeRepository,
	            org.mockito.Mockito.never())
	            .save(any(Employee.class));
	}
	
	@Test
	void getEmployeeById_ShouldReturnEmployeeSuccessfully() {

	    // Arrange
	    Long employeeId = 1L;

	    Employee employee = new Employee();
	    employee.setId(employeeId);
	    employee.setName("Abhishek");
	    employee.setEmail("abhishek@gmail.com");
	    employee.setDepartment("IT");
	    employee.setSalary(75000.0);

	    EmployeeResponseDTO response = new EmployeeResponseDTO(
	            1L,
	            "Abhishek",
	            "abhishek@gmail.com",
	            "IT",
	            75000.0,
	            null,
	            null
	    );

	    when(employeeRepository.findById(employeeId))
	            .thenReturn(java.util.Optional.of(employee));

	    when(employeeMapper.toResponseDTO(employee))
	            .thenReturn(response);

	    // Act
	    EmployeeResponseDTO result =
	            employeeService.getEmployeeById(employeeId);

	    // Assert
	    assertNotNull(result);
	    assertEquals(1L, result.getId());
	    assertEquals("Abhishek", result.getName());

	    verify(employeeRepository).findById(employeeId);
	    verify(employeeMapper).toResponseDTO(employee);
	}
	
	@Test
	void getEmployeeById_ShouldThrowException_WhenEmployeeDoesNotExist() {

	    // Arrange
	    Long employeeId = 999L;

	    when(employeeRepository.findById(employeeId))
	            .thenReturn(java.util.Optional.empty());

	    // Act + Assert
	    EmployeeNotFoundException exception =
	            assertThrows(
	                    EmployeeNotFoundException.class,
	                    () -> employeeService.getEmployeeById(employeeId)
	            );

	    assertEquals(
	            "Employee not found with id: 999",
	            exception.getMessage()
	    );

	    verify(employeeRepository).findById(employeeId);

	    verify(employeeMapper,
	            org.mockito.Mockito.never())
	            .toResponseDTO(any(Employee.class));
	}
	
	@Test
	void getAllEmployees_ShouldReturnEmployeesWithPagination() {

	    // Arrange
	    Employee employee1 = new Employee();

	    employee1.setId(1L);
	    employee1.setName("Abhishek");
	    employee1.setEmail("abhishek@gmail.com");
	    employee1.setDepartment("IT");
	    employee1.setSalary(75000.0);

	    Employee employee2 = new Employee();

	    employee2.setId(2L);
	    employee2.setName("Rahul");
	    employee2.setEmail("rahul@gmail.com");
	    employee2.setDepartment("Finance");
	    employee2.setSalary(65000.0);

	    EmployeeResponseDTO response1 = new EmployeeResponseDTO(
	            1L,
	            "Abhishek",
	            "abhishek@gmail.com",
	            "IT",
	            75000.0,
	            null,
	            null
	    );

	    EmployeeResponseDTO response2 = new EmployeeResponseDTO(
	            2L,
	            "Rahul",
	            "rahul@gmail.com",
	            "Finance",
	            65000.0,
	            null,
	            null
	    );

	    Page<Employee> employeePage =
	            new PageImpl<>(
	                    java.util.List.of(employee1, employee2),
	                    PageRequest.of(
	                            0,
	                            10,
	                            Sort.by("id").ascending()
	                    ),
	                    2
	            );

	    when(employeeRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
	            .thenReturn(employeePage);

	    when(employeeMapper.toResponseDTO(employee1))
	            .thenReturn(response1);

	    when(employeeMapper.toResponseDTO(employee2))
	            .thenReturn(response2);

	    // Act
	    Page<EmployeeResponseDTO> result =
	            employeeService.getAllEmployees(
	                    0,
	                    10,
	                    "id",
	                    "asc"
	            );

	    // Assert
	    assertNotNull(result);

	    assertEquals(2, result.getContent().size());

	    assertEquals(
	            "Abhishek",
	            result.getContent().get(0).getName()
	    );

	    assertEquals(
	            "Rahul",
	            result.getContent().get(1).getName()
	    );

	    assertEquals(0, result.getNumber());
	    assertEquals(10, result.getSize());
	    assertEquals(2, result.getTotalElements());
	    assertEquals(1, result.getTotalPages());

	    // Verify repository
	    verify(employeeRepository)
	            .findAll(any(org.springframework.data.domain.Pageable.class));

	    // Verify mapper
	    verify(employeeMapper).toResponseDTO(employee1);
	    verify(employeeMapper).toResponseDTO(employee2);
	}
	
	@Test
	void updateEmployee_ShouldUpdateEmployeeSuccessfully() {

	    // Arrange
	    Long employeeId = 1L;

	    EmployeeRequestDTO request = new EmployeeRequestDTO(
	            "Abhishek Updated",
	            "abhishek.updated@gmail.com",
	            "Engineering",
	            85000.0
	    );

	    Employee existingEmployee = new Employee();

	    existingEmployee.setId(employeeId);
	    existingEmployee.setName("Abhishek");
	    existingEmployee.setEmail("abhishek@gmail.com");
	    existingEmployee.setDepartment("IT");
	    existingEmployee.setSalary(75000.0);

	    EmployeeResponseDTO response = new EmployeeResponseDTO(
	            employeeId,
	            "Abhishek Updated",
	            "abhishek.updated@gmail.com",
	            "Engineering",
	            85000.0,
	            null,
	            null
	    );

	    when(employeeRepository.findById(employeeId))
	            .thenReturn(java.util.Optional.of(existingEmployee));

	    when(employeeRepository.save(existingEmployee))
	            .thenReturn(existingEmployee);

	    when(employeeMapper.toResponseDTO(existingEmployee))
	            .thenReturn(response);

	    // Act
	    EmployeeResponseDTO result =
	            employeeService.updateEmployee(employeeId, request);

	    // Assert
	    assertNotNull(result);

	    assertEquals("Abhishek Updated", result.getName());
	    assertEquals(
	            "abhishek.updated@gmail.com",
	            result.getEmail()
	    );

	    assertEquals("Engineering", result.getDepartment());
	    assertEquals(85000.0, result.getSalary());

	    // Verify database interaction
	    verify(employeeRepository).findById(employeeId);
	    verify(employeeRepository).save(existingEmployee);

	    // Verify mapper
	    verify(employeeMapper).toResponseDTO(existingEmployee);
	}
	
	@Test
	void updateEmployee_ShouldThrowException_WhenEmployeeDoesNotExist() {

	    // Arrange
	    Long employeeId = 999L;

	    EmployeeRequestDTO request = new EmployeeRequestDTO(
	            "Abhishek",
	            "abhishek@gmail.com",
	            "IT",
	            75000.0
	    );

	    when(employeeRepository.findById(employeeId))
	            .thenReturn(java.util.Optional.empty());

	    // Act + Assert
	    EmployeeNotFoundException exception =
	            assertThrows(
	                    EmployeeNotFoundException.class,
	                    () -> employeeService.updateEmployee(
	                            employeeId,
	                            request
	                    )
	            );

	    assertEquals(
	            "Employee not found with id: 999",
	            exception.getMessage()
	    );

	    verify(employeeRepository).findById(employeeId);

	    verify(employeeRepository,
	            never())
	            .save(any(Employee.class));
	}
	
	@Test
	void deleteEmployee_ShouldDeleteEmployeeSuccessfully() {

	    // Arrange
	    Long employeeId = 1L;

	    Employee employee = new Employee();

	    employee.setId(employeeId);
	    employee.setName("Abhishek");
	    employee.setEmail("abhishek@gmail.com");

	    when(employeeRepository.findById(employeeId))
	            .thenReturn(java.util.Optional.of(employee));

	    // Act
	    employeeService.deleteEmployee(employeeId);

	    // Assert
	    verify(employeeRepository).findById(employeeId);
	    verify(employeeRepository).delete(employee);
	}
	
	@Test
	void deleteEmployee_ShouldThrowException_WhenEmployeeDoesNotExist() {

	    // Arrange
	    Long employeeId = 999L;

	    when(employeeRepository.findById(employeeId))
	            .thenReturn(java.util.Optional.empty());

	    // Act + Assert
	    EmployeeNotFoundException exception =
	            assertThrows(
	                    EmployeeNotFoundException.class,
	                    () -> employeeService.deleteEmployee(employeeId)
	            );

	    assertEquals(
	            "Employee not found with id: 999",
	            exception.getMessage()
	    );

	    verify(employeeRepository).findById(employeeId);

	    verify(employeeRepository,
	            never())
	            .delete(any(Employee.class));
	}
	
	

}
