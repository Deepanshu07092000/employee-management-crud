package com.example.employementCRUD.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String department;
    private Double salary;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Automatically executed before inserting a new employee
    // Execute this method immediately before JPA persists/inserts the entity.
    @PrePersist
    protected void onCreate() {

        LocalDateTime currentTime = LocalDateTime.now();

        createdAt = currentTime;
        updatedAt = currentTime;
    }


    // Automatically executed before updating an employee
    // When an existing employee is modified:
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
