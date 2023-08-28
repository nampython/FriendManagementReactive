package org.example.model.company;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee", schema = "friendsmanagement", catalog = "")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name ="position")
    private String position;
    @Column(name = "salary")
    private int salary;
    @Column(name = "age")
    private int age;
    @Column(name = "department_id")
    private String departmentId;
    @Transient
    public Department department;
    @Column(name = "organization_id")
    private String organizationId;
}
