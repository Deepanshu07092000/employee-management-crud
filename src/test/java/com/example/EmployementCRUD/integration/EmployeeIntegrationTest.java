package com.example.EmployementCRUD.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.employementCRUD.dto.EmployeeRequestDTO;
import com.example.employementCRUD.dto.EmployeeResponseDTO;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest         //This annotation is used to indicate that the class is a Spring Boot test class. It tells Spring Boot to look for a main configuration class (one with @SpringBootApplication, for example) and use that to start a Spring application context for the test. This allows you to test your application in an environment that closely resembles the production environment.
@AutoConfigureMockMvc   //This annotation is used to enable and configure MockMvc, which is a class provided by Spring for testing Spring MVC applications. It allows you to perform HTTP requests and assertions on the responses without starting a full web server. By using this annotation, you can test your controllers and endpoints in isolation.
public class EmployeeIntegrationTest {
	
	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	void createEmployee_ShouldCreateEmployeeSuccessfully()
	        throws Exception {

	    EmployeeRequestDTO request = new EmployeeRequestDTO(
	            "Integration User",
	            "integration@gmail.com",
	            "IT",
	            70000.0
	    );

	    mockMvc.perform(
	            post("/api/employees")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(
	                            objectMapper.writeValueAsString(request)
	                    )
	    )
	    .andExpect(status().isCreated())
	    .andExpect(jsonPath("$.id").exists())
	    .andExpect(jsonPath("$.name").value("Integration User"))
	    .andExpect(jsonPath("$.email").value("integration@gmail.com"))
	    .andExpect(jsonPath("$.department").value("IT"))
	    .andExpect(jsonPath("$.salary").value(70000.0));
	}
	
	@Test
	void createAndGetEmployee_ShouldWorkSuccessfully()
	        throws Exception {

	    // CREATE
	    EmployeeRequestDTO request = new EmployeeRequestDTO(
	            "Integration Test",
	            "integration2@gmail.com",
	            "Engineering",
	            80000.0
	    );

	    MvcResult createResult = mockMvc.perform(
	            post("/api/employees")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(
	                            objectMapper.writeValueAsString(request)
	                    )
	    )
	    .andExpect(status().isCreated())
	    .andReturn();

	    String responseBody =
	            createResult.getResponse().getContentAsString();

	    EmployeeResponseDTO response =
	            objectMapper.readValue(
	                    responseBody,
	                    EmployeeResponseDTO.class
	            );

	    Long employeeId = response.getId();

	    // GET
	    mockMvc.perform(get("/api/employees/" + employeeId))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.id").value(employeeId))
	    .andExpect(jsonPath("$.name").value("Integration Test"))
	    .andExpect(jsonPath("$.email").value("integration2@gmail.com"));
	}
	
	@Test
	void createEmployee_ShouldReturn400_WhenRequestIsInvalid()
	        throws Exception {

	    EmployeeRequestDTO request = new EmployeeRequestDTO(
	            "",
	            "wrong-email",
	            "",
	            -100.0
	    );

	    mockMvc.perform(
	            post("/api/employees")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(
	                            objectMapper.writeValueAsString(request)
	                    )
	    )
	    .andExpect(status().isBadRequest())
	    .andExpect(jsonPath("$.status").value(400));
	}
	
	@Test
	void createEmployee_ShouldReturn409_WhenEmailAlreadyExists()
	        throws Exception {

	    EmployeeRequestDTO request = new EmployeeRequestDTO(
	            "First Employee",
	            "duplicate@gmail.com",
	            "IT",
	            70000.0
	    );

	    // First request should succeed
	    mockMvc.perform(
	            post("/api/employees")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(
	                            objectMapper.writeValueAsString(request)
	                    )
	    )
	    .andExpect(status().isCreated());

	    // Second request with same email should fail
	    mockMvc.perform(
	            post("/api/employees")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(
	                            objectMapper.writeValueAsString(request)
	                    )
	    )
	    .andExpect(status().isConflict())
	    .andExpect(jsonPath("$.status").value(409));
	}
	
	@Test
	void deleteEmployee_ShouldReturn204()
	        throws Exception {

	    // First create employee
	    EmployeeRequestDTO request = new EmployeeRequestDTO(
	            "Delete Test",
	            "delete@gmail.com",
	            "IT",
	            60000.0
	    );

	    MvcResult createResult = mockMvc.perform(
	            post("/api/employees")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(
	                            objectMapper.writeValueAsString(request)
	                    )
	    )
	    .andExpect(status().isCreated())
	    .andReturn();

	    String responseBody =
	            createResult.getResponse().getContentAsString();

	    EmployeeResponseDTO response =
	            objectMapper.readValue(
	                    responseBody,
	                    EmployeeResponseDTO.class
	            );

	    Long employeeId = response.getId();

	    // DELETE
	    mockMvc.perform(
	            delete("/api/employees/" + employeeId)
	    )
	    .andExpect(status().isNoContent());

	    // Verify employee no longer exists
	    mockMvc.perform(get("/api/employees/" + employeeId))
	    .andExpect(status().isNotFound());
	}
	
	@Test
	void getAllEmployees_ShouldReturnPaginatedEmployees()
	        throws Exception {

	    // Create employee 1
	    EmployeeRequestDTO employee1 = new EmployeeRequestDTO(
	            "Abhishek",
	            "pagination1@gmail.com",
	            "IT",
	            75000.0
	    );

	    mockMvc.perform(
	            post("/api/employees")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(employee1))
	    )
	    .andExpect(status().isCreated());

	    // Create employee 2
	    EmployeeRequestDTO employee2 = new EmployeeRequestDTO(
	            "Rahul",
	            "pagination2@gmail.com",
	            "Finance",
	            65000.0
	    );

	    mockMvc.perform(
	            post("/api/employees")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(employee2))
	    )
	    .andExpect(status().isCreated());

	    // Get first page
	    mockMvc.perform(
	            get("/api/employees")
	                    .param("page", "0")
	                    .param("size", "1")
	    )
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.content.length()").value(1))
	    .andExpect(jsonPath("$.number").value(0))
	    .andExpect(jsonPath("$.size").value(1));
	}
	
	@Test
	void getAllEmployees_ShouldSortEmployeesByName()
	        throws Exception {

	    mockMvc.perform(
	            get("/api/employees")
	                    .param("page", "0")
	                    .param("size", "10")
	                    .param("sortBy", "name")
	                    .param("sortDir", "asc")
	    )
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.content").exists())
	    .andExpect(jsonPath("$.number").value(0))
	    .andExpect(jsonPath("$.size").value(10));
	}

}
