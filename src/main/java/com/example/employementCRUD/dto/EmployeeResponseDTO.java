package com.example.employementCRUD.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {
	
	private Long id;

    private String name;

    private String email;

    private String department;

    private Double salary;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
