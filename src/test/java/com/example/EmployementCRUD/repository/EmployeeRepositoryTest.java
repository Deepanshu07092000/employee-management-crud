package com.example.EmployementCRUD.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.example.employementCRUD.repository.EmployeeRepository;

@DataJpaTest
public class EmployeeRepositoryTest {
	
	@Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testRepository() {

    }

}
