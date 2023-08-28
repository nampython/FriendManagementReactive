package org.example.service.companyservice;

import org.example.model.Response;
import org.example.model.company.Department;
import org.example.repository.companyrepository.DepartmentRepository;
import org.example.repository.companyrepository.EmployeeRepository;
import org.example.repository.companyrepository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired

    public DepartmentServiceImpl(EmployeeRepository employeeRepository, OrganizationRepository organizationRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Mono<ResponseEntity<Response>> getAllDepartments() {
        return null;
    }


    @Override
    public Mono<Department> getDepartmentById(Integer id) {
        return departmentRepository.findById(1);
    }
}
