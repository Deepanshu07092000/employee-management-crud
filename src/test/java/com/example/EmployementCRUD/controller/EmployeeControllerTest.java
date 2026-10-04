package com.example.EmployementCRUD.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.employementCRUD.controller.EmployeeController;
import com.example.employementCRUD.dto.EmployeeRequestDTO;
import com.example.employementCRUD.dto.EmployeeResponseDTO;
import com.example.employementCRUD.exception.DuplicateEmployeeException;
import com.example.employementCRUD.exception.EmployeeNotFoundException;
import com.example.employementCRUD.service.EmployeeService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(EmployeeController.class)  // This annotation is used to test Spring MVC controllers. It sets up the application context with only the specified controller and its dependencies, allowing for focused testing of the controller's behavior.
public class EmployeeControllerTest {
	
	 	@Autowired
	    private MockMvc mockMvc;         // This is a Spring MVC testing utility that allows you to perform HTTP requests and assertions on the responses. It is automatically configured by the @WebMvcTest annotation.

	    @Autowired
	    private ObjectMapper objectMapper;  // This is a utility class from the Jackson library that is used for converting Java objects to JSON and vice versa. It is automatically configured by Spring Boot.

	    @MockitoBean
	    private EmployeeService employeeService;
	    
	    @Test
	    void getEmployeeById_ShouldReturn200() throws Exception {

	        EmployeeResponseDTO response = new EmployeeResponseDTO(
	                1L,
	                "Abhishek",
	                "abhishek@gmail.com",
	                "IT",
	                75000.0,
	                null,
	                null
	        );

	        when(employeeService.getEmployeeById(1L))
	                .thenReturn(response);

	        mockMvc.perform(
	                get("/api/employees/1")
	        )
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.id").value(1))
	        .andExpect(jsonPath("$.name").value("Abhishek"))
	        .andExpect(jsonPath("$.email").value("abhishek@gmail.com"));
	    }
	    
	    @Test
	    void getEmployeeById_ShouldReturn404_WhenEmployeeDoesNotExist()
	            throws Exception {

	        when(employeeService.getEmployeeById(999L))
	                .thenThrow(
	                        new EmployeeNotFoundException(
	                                "Employee not found with id: 999"
	                        )
	                );

	        mockMvc.perform(
	                get("/api/employees/999")
	        )
	        .andExpect(status().isNotFound())
	        .andExpect(jsonPath("$.status").value(404))
	        .andExpect(jsonPath("$.message").value("Employee not found with id: 999"))
	        .andExpect(jsonPath("$.path").value("/api/employees/999"));
	    }
	    
	    @Test
	    void createEmployee_ShouldReturn201() throws Exception {

	        EmployeeRequestDTO request = new EmployeeRequestDTO(
	                "Abhishek",
	                "abhishek@gmail.com",
	                "IT",
	                75000.0
	        );

	        EmployeeResponseDTO response = new EmployeeResponseDTO(
	                1L,
	                "Abhishek",
	                "abhishek@gmail.com",
	                "IT",
	                75000.0,
	                null,
	                null
	        );

	        when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
	                .thenReturn(response);

	        mockMvc.perform(
	                post("/api/employees")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(objectMapper.writeValueAsString(request))
	        )
	        .andExpect(status().isCreated())
	        .andExpect(jsonPath("$.id").value(1))
	        .andExpect(jsonPath("$.name").value("Abhishek"))
	        .andExpect(jsonPath("$.email").value("abhishek@gmail.com"));
	    }
	    
	    @Test
	    void createEmployee_ShouldReturn400_WhenRequestIsInvalid()
	            throws Exception {

	        EmployeeRequestDTO request = new EmployeeRequestDTO(
	                "",
	                "invalid-email",
	                "",
	                -500.0
	        );

	        mockMvc.perform(
	                post("/api/employees")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(objectMapper.writeValueAsString(request))
	        )
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.status").value(400));
	    }
	    
	    @Test
	    void createEmployee_ShouldReturn409_WhenEmailAlreadyExists()
	            throws Exception {

	        EmployeeRequestDTO request = new EmployeeRequestDTO(
	                "Abhishek",
	                "abhishek@gmail.com",
	                "IT",
	                75000.0
	        );

	        when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
	                .thenThrow(
	                        new DuplicateEmployeeException(
	                                "Employee already exists with email: "
	                                        + "abhishek@gmail.com"
	                        )
	                );

	        mockMvc.perform(
	                post("/api/employees")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(objectMapper.writeValueAsString(request))
	        )
	        .andExpect(status().isConflict())
	        .andExpect(jsonPath("$.status").value(409))
	        .andExpect(jsonPath("$.message")
	                .value("Employee already exists with email: "
	                        + "abhishek@gmail.com"));
	    }
	    
	    @Test
	    void getAllEmployees_ShouldReturn200WithPagination()
	            throws Exception {

	        // Arrange

	        EmployeeResponseDTO employee1 = new EmployeeResponseDTO(
	                1L,
	                "Abhishek",
	                "abhishek@gmail.com",
	                "IT",
	                75000.0,
	                null,
	                null
	        );

	        EmployeeResponseDTO employee2 = new EmployeeResponseDTO(
	                2L,
	                "Rahul",
	                "rahul@gmail.com",
	                "Finance",
	                65000.0,
	                null,
	                null
	        );

	        Page<EmployeeResponseDTO> employeePage =
	                new PageImpl<>(
	                        java.util.List.of(employee1, employee2),
	                        PageRequest.of(
	                                0,
	                                10,
	                                Sort.by("id").ascending()
	                        ),
	                        2
	                );

	        when(employeeService.getAllEmployees(
	                0,
	                10,
	                "id",
	                "asc"
	        )).thenReturn(employeePage);

	        // Act + Assert

	        mockMvc.perform(
	                get("/api/employees")
	        )
	        .andExpect(status().isOk())

	        // Page content
	        .andExpect(jsonPath("$.content.length()").value(2))

	        .andExpect(jsonPath("$.content[0].name")
	                .value("Abhishek"))

	        .andExpect(jsonPath("$.content[1].name")
	                .value("Rahul"))

	        // Pagination information
	        .andExpect(jsonPath("$.number").value(0))
	        .andExpect(jsonPath("$.size").value(10))
	        .andExpect(jsonPath("$.totalElements").value(2))
	        .andExpect(jsonPath("$.totalPages").value(1));
	    }
	    
	    @Test
	    void updateEmployee_ShouldReturn200() throws Exception {

	        EmployeeRequestDTO request = new EmployeeRequestDTO(
	                "Abhishek Updated",
	                "abhishek.updated@gmail.com",
	                "Engineering",
	                85000.0
	        );

	        EmployeeResponseDTO response = new EmployeeResponseDTO(
	                1L,
	                "Abhishek Updated",
	                "abhishek.updated@gmail.com",
	                "Engineering",
	                85000.0,
	                null,
	                null
	        );

	        when(employeeService.updateEmployee(
	                eq(1L),
	                any(EmployeeRequestDTO.class)
	        )).thenReturn(response);

	        mockMvc.perform(
	                put("/api/employees/1")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(objectMapper.writeValueAsString(request))
	        )
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.id").value(1))
	        .andExpect(jsonPath("$.department")
	                .value("Engineering"))
	        .andExpect(jsonPath("$.salary")
	                .value(85000.0));
	    }
	    
	    @Test
	    void deleteEmployee_ShouldReturn204() throws Exception {

	        doNothing()
	                .when(employeeService)
	                .deleteEmployee(1L);

	        mockMvc.perform(
	                delete("/api/employees/1")
	        )
	        .andExpect(status().isNoContent());
	    }
	    
	    @Test
	    void deleteEmployee_ShouldReturn404_WhenEmployeeDoesNotExist()
	            throws Exception {

	        doThrow(
	                new EmployeeNotFoundException(
	                        "Employee not found with id: 999"
	                )
	        )
	        .when(employeeService)
	        .deleteEmployee(999L);

	        mockMvc.perform(
	                delete("/api/employees/999")
	        )
	        .andExpect(status().isNotFound())
	        .andExpect(jsonPath("$.status").value(404))
	        .andExpect(jsonPath("$.message")
	                .value("Employee not found with id: 999"));
	    }
	    
	    @Test
	    void getAllEmployees_ShouldAcceptPaginationAndSortingParameters()
	            throws Exception {

	        // Arrange

	        EmployeeResponseDTO employee1 = new EmployeeResponseDTO(
	                1L,
	                "Abhishek",
	                "abhishek@gmail.com",
	                "IT",
	                75000.0,
	                null,
	                null
	        );

	        Page<EmployeeResponseDTO> employeePage =
	                new PageImpl<>(
	                        java.util.List.of(employee1),
	                        PageRequest.of(
	                                1,
	                                5,
	                                Sort.by("salary").descending()
	                        ),
	                        6
	                );

	        when(employeeService.getAllEmployees(
	                1,
	                5,
	                "salary",
	                "desc"
	        )).thenReturn(employeePage);

	        // Act + Assert

	        mockMvc.perform(
	                get("/api/employees")
	                        .param("page", "1")
	                        .param("size", "5")
	                        .param("sortBy", "salary")
	                        .param("sortDir", "desc")
	        )
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.content.length()").value(1))
	        .andExpect(jsonPath("$.content[0].name")
	                .value("Abhishek"))
	        .andExpect(jsonPath("$.number").value(1))
	        .andExpect(jsonPath("$.size").value(5))
	        .andExpect(jsonPath("$.totalElements").value(6))
	        .andExpect(jsonPath("$.totalPages").value(2));
	    }
	
	

}
