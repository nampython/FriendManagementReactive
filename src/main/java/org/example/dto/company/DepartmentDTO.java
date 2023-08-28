package org.example.dto.company;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.model.company.Employee;
import org.example.model.company.Organization;

import java.util.List;

public interface DepartmentDTO {
    class Request {
        String id;
    }

    @Builder
    @Getter
    @Setter
    class Response {
        Integer id;
        String name;
        List<Employee> employees;
        Organization organization;
    }
}
