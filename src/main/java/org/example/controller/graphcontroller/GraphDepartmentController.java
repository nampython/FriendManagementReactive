package org.example.controller.graphcontroller;

import org.example.model.Response;
import org.example.model.company.Department;
import org.example.service.companyservice.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
public class GraphDepartmentController {
    private static final String GET_DEPARTMENTS = "/departments";
    private static final String GET_DEPARTMENT_BY_ID = "/departments/{id}";
    private final DepartmentService departmentService;

    @Autowired
    public GraphDepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping(GET_DEPARTMENTS)
    public Mono<ResponseEntity<Response>> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping(GET_DEPARTMENT_BY_ID)
    public Mono<Department> getDepartmentById(@PathVariable String id) {
        return departmentService.getDepartmentById(Integer.valueOf(id));
    }
}
