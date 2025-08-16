package com.yourcompany.ems.database;

import com.yourcompany.ems.models.Employee;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    private final Connection connection;

    public EmployeeDAO(Connection connection) {
        this.connection = connection;
    }

    public Employee getEmployeeByEmail(String email) {
        String sql = "SELECT * FROM employees WHERE email = ?";
        Employee emp = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    emp = new Employee();
                    emp.setId(rs.getInt("id"));
                    emp.setName(rs.getString("name"));
                    emp.setEmail(rs.getString("email"));
                    emp.setPhone(rs.getString("phone"));
                    emp.setPosition(rs.getString("position"));
                    emp.setDepartment(rs.getString("department"));
                    Date hireDate = rs.getDate("hire_date");
                    if (hireDate != null) {
                        emp.setHireDate(hireDate.toLocalDate());
                    }
                    emp.setSalary(rs.getBigDecimal("salary"));
                    emp.setAvatarPath(rs.getString("avatar_path"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emp;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id"));
                emp.setName(rs.getString("name"));
                emp.setEmail(rs.getString("email"));
                emp.setPhone(rs.getString("phone"));
                emp.setPosition(rs.getString("position"));
                emp.setDepartment(rs.getString("department"));
                Date hireDate = rs.getDate("hire_date");
                if (hireDate != null) {
                    emp.setHireDate(hireDate.toLocalDate());
                }
                emp.setSalary(rs.getBigDecimal("salary"));
                emp.setAvatarPath(rs.getString("avatar_path"));

                employees.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public void addEmployee(Employee emp) {
        String sql = "INSERT INTO employees (name, email, phone, position, department, hire_date, salary, avatar_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emp.getName());
            pstmt.setString(2, emp.getEmail());
            pstmt.setString(3, emp.getPhone());
            pstmt.setString(4, emp.getPosition());
            pstmt.setString(5, emp.getDepartment());
            pstmt.setDate(6, Date.valueOf(emp.getHireDate()));
            pstmt.setBigDecimal(7, emp.getSalary());
            pstmt.setString(8, emp.getAvatarPath());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEmployee(Employee emp) {
        String sql = "UPDATE employees SET name=?, email=?, phone=?, position=?, department=?, hire_date=?, salary=?, avatar_path=? WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emp.getName());
            pstmt.setString(2, emp.getEmail());
            pstmt.setString(3, emp.getPhone());
            pstmt.setString(4, emp.getPosition());
            pstmt.setString(5, emp.getDepartment());
            pstmt.setDate(6, Date.valueOf(emp.getHireDate()));
            pstmt.setBigDecimal(7, emp.getSalary());
            pstmt.setString(8, emp.getAvatarPath());
            pstmt.setInt(9, emp.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}





