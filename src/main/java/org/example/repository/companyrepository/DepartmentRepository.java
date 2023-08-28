package org.example.repository.companyrepository;

import org.example.model.company.Department;
import org.springframework.data.r2dbc.repository.R2dbcRepository;


public interface DepartmentRepository extends R2dbcRepository<Department, Integer> {
}
