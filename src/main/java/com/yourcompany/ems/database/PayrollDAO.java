package com.yourcompany.ems.database;

import com.yourcompany.ems.models.Payroll;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    public List<Payroll> getAllPayrolls() throws SQLException {
        List<Payroll> payrollList = new ArrayList<>();
        String query = "SELECT p.id, p.employee_id, e.name, p.basic_salary, p.bonuses, p.deductions, p.net_salary, p.payment_date " +
                "FROM payroll p INNER JOIN employees e ON p.employee_id = e.id ORDER BY p.payment_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Payroll payroll = new Payroll();
                payroll.setId(rs.getInt("id"));
                payroll.setEmployeeId(rs.getInt("employee_id"));
                payroll.setEmployeeName(rs.getString("name"));
                payroll.setBasicSalary(rs.getDouble("basic_salary"));
                payroll.setBonuses(rs.getDouble("bonuses"));
                payroll.setDeductions(rs.getDouble("deductions"));
                payroll.setNetSalary(rs.getDouble("net_salary"));
                payroll.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payrollList.add(payroll);
            }
        }
        return payrollList;
    }

    public boolean addPayroll(Payroll payroll) throws SQLException {
        String query = "INSERT INTO payroll (employee_id, basic_salary, bonuses, deductions, net_salary, payment_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, payroll.getEmployeeId());
            ps.setDouble(2, payroll.getBasicSalary());
            ps.setDouble(3, payroll.getBonuses());
            ps.setDouble(4, payroll.getDeductions());
            ps.setDouble(5, payroll.getNetSalary());
            ps.setDate(6, Date.valueOf(payroll.getPaymentDate()));

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePayroll(int payrollId) throws SQLException {
        String query = "DELETE FROM payroll WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, payrollId);
            return ps.executeUpdate() > 0;
        }
    }

    // Add updatePayroll() if needed for editing payroll entries
}

