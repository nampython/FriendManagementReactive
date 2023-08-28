package org.example.repository.companyrepository;

import org.example.model.company.Employee;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface EmployeeRepository extends R2dbcRepository<Employee, Integer> {
    Mono<Employee> findByDepartmentId(Integer departmentId);
}
