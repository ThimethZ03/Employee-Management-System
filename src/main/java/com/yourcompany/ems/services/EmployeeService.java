package com.yourcompany.ems.services;

import com.yourcompany.ems.database.EmployeeDAO;
import com.yourcompany.ems.models.Employee;

import java.sql.Connection;
import java.util.List;

public class EmployeeService {
    private final EmployeeDAO employeeDAO;

    public EmployeeService(Connection connection) {
        this.employeeDAO = new EmployeeDAO(connection);
    }

    public List<Employee> getAllEmployees() {
        try {
            return employeeDAO.getAllEmployees();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addEmployee(Employee employee) {
        try {
            employeeDAO.addEmployee(employee);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEmployee(Employee employee) {
        try {
            employeeDAO.updateEmployee(employee);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(int id) {
        try {
            employeeDAO.deleteEmployee(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

