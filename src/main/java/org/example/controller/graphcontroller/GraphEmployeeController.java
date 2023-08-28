package org.example.controller.graphcontroller;

import org.example.model.company.Employee;
import org.example.repository.companyrepository.DepartmentRepository;
import org.example.repository.companyrepository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class GraphEmployeeController {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    @Autowired
    public GraphEmployeeController(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping(value = "/employees")
    public Flux<Employee> getAllEmployees() {

//        employeeRepository.

        return employeeRepository.findAll();
    }
}
