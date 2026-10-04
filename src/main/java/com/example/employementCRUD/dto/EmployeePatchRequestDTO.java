package com.example.employementCRUD.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePatchRequestDTO {
	
		@Size(min = 2, max = 50,
	          message = "Employee name must be between 2 and 50 characters")
	    private String name;

	    @Email(message = "Please provide a valid email address")
	    private String email;

	    @Size(min = 2, max = 30,
	          message = "Department must be between 2 and 30 characters")
	    private String department;

	    @Positive(message = "Salary must be greater than zero")
	    private Double salary;

}
