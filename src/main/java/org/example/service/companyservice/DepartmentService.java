package org.example.service.companyservice;

import org.example.model.Response;
import org.example.model.company.Department;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface DepartmentService {
    Mono<ResponseEntity<Response>> getAllDepartments();
    Mono<Department> getDepartmentById(Integer id);
}
