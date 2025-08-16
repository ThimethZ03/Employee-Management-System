package com.yourcompany.ems.services;

import com.yourcompany.ems.database.PayrollDAO;
import com.yourcompany.ems.models.Payroll;

import java.sql.SQLException;
import java.util.List;

public class PayrollService {
    private final PayrollDAO payrollDAO = new PayrollDAO();

    public List<Payroll> getAllPayrolls() throws SQLException {
        return payrollDAO.getAllPayrolls();
    }

    public boolean addPayroll(Payroll payroll) throws SQLException {
        return payrollDAO.addPayroll(payroll);
    }

    public boolean deletePayroll(int payrollId) throws SQLException {
        return payrollDAO.deletePayroll(payrollId);
    }
}

