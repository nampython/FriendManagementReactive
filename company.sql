drop table if exists organization;
create table organization (
  organization_id int primary key auto_increment,
  name varchar(255)
);

drop table if exists department;
create table department (
  department_id int primary key auto_increment,
  name varchar(255),
  organization_id int,
  foreign key (organization_id) references organization(organization_id)
);

drop table if exists employee;
create table employee (
  id int primary key auto_increment,
  first_name varchar(255),
  last_name varchar(255),
  position varchar(255),
  salary int,
  age int,
  department_id int,
  organization_id int,
  foreign key (department_id) references department(department_id),
  foreign key (organization_id) references organization(organization_id)
);

-- insert data into organization table
insert into organization (name) values
  ('organization 1'),
  ('organization 2'),
  ('organization 3'),
  ('organization 4'),
  ('organization 5'),
  ('organization 6'),
  ('organization 7'),
  ('organization 8'),
  ('organization 9'),
  ('organization 10');

-- insert data into department table
insert into department (name, organization_id) values
  ('department 1', 1),
  ('department 2', 1),
  ('department 3', 2),
  ('department 4', 2),
  ('department 5', 3),
  ('department 6', 4),
  ('department 7', 4),
  ('department 8', 5),
  ('department 9', 6),
  ('department 10', 7);

-- insert data into employee table
insert into employee (first_name, last_name, position, salary, age, department_id, organization_id) values
  ('john', 'doe', 'manager', 5000, 30, 1, 1),
  ('jane', 'smith', 'assistant', 3000, 25, 1, 2),
  ('michael', 'johnson', 'engineer', 4000, 28, 2, 3),
  ('emily', 'williams', 'analyst', 3500, 26, 2, 4),
  ('david', 'brown', 'technician', 3200, 27, 3, 5),
  ('sarah', 'davis', 'supervisor', 4500, 32, 4, 6),
  ('robert', 'taylor', 'coordinator', 3800, 29, 4, 7),
  ('olivia', 'anderson', 'developer', 4200, 31, 5, 8),
  ('daniel', 'clark', 'designer', 3700, 26, 6, 9),
  ('sophia', 'lee', 'administrator', 3900, 28, 7, 10);

select * from employee;
select * from department;
select * from  organization;



create database employee;

insert into organization (name) values ('Test1');
insert into organization (name) values ('Test2');
insert into organization (name) values ('Test3');
insert into organization (name) values ('Test4');
insert into organization (name) values ('Test5');
insert into department (name, organization_id) values ('Test1', 1);
insert into department (name, organization_id) values ('Test2', 1);
insert into department (name, organization_id) values ('Test3', 1);
insert into department (name, organization_id) values ('Test4', 2);
insert into department (name, organization_id) values ('Test5', 2);
insert into department (name, organization_id) values ('Test6', 3);
insert into department (name, organization_id) values ('Test7', 4);
insert into department (name, organization_id) values ('Test8', 5);
insert into department (name, organization_id) values ('Test9', 5);
insert into employee (first_name, last_name, position, salary, age, department_id, organization_id) values ('John', 'Smith', 'Developer', 10000, 30, 1, 1);
insert into employee (first_name, last_name, position, salary, age, department_id, organization_id) values ('Adam', 'Hamilton', 'Developer', 12000, 35, 1, 1);
insert into employee (first_name, last_name, position, salary, age, department_id, organization_id) values ('Tracy', 'Smith', 'Architect', 15000, 40, 1, 1);
insert into employee (first_name, last_name, position, salary, age, department_id, organization_id) values ('Lucy', 'Kim', 'Developer', 13000, 25, 2, 1);
insert into employee (first_name, last_name, position, salary, age, department_id, organization_id) values ('Peter', 'Wright', 'Director', 50000, 50, 4, 2);
insert into employee (first_name, last_name, position, salary, age, department_id, organization_id) values ('Alan', 'Murray', 'Developer', 20000, 37, 4, 2);
insert into employee (first_name, last_name, position, salary, age, department_id, organization_id) values ('Pamela', 'Anderson', 'Analyst', 7000, 27, 4, 2);

select * from employee;
select * from department;


