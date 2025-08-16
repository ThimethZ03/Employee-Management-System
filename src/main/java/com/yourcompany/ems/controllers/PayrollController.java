package com.yourcompany.ems.controllers;

import com.yourcompany.ems.models.Payroll;
import com.yourcompany.ems.services.PayrollService;
import com.yourcompany.ems.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PayrollController {

    @FXML private TableView<Payroll> payrollTable;
    @FXML private TableColumn<Payroll, Integer> colId;
    @FXML private TableColumn<Payroll, String> colEmployeeName;
    @FXML private TableColumn<Payroll, Double> colBasicSalary;
    @FXML private TableColumn<Payroll, Double> colBonuses;
    @FXML private TableColumn<Payroll, Double> colDeductions;
    @FXML private TableColumn<Payroll, Double> colNetSalary;
    @FXML private TableColumn<Payroll, LocalDate> colPaymentDate;

    @FXML private TextField txtEmployeeId;
    @FXML private TextField txtBasicSalary;
    @FXML private TextField txtBonuses;
    @FXML private TextField txtDeductions;
    @FXML private DatePicker datePayment;

    private final PayrollService payrollService = new PayrollService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colEmployeeName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmployeeName()));
        colBasicSalary.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getBasicSalary()).asObject());
        colBonuses.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getBonuses()).asObject());
        colDeductions.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getDeductions()).asObject());
        colNetSalary.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getNetSalary()).asObject());
        colPaymentDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPaymentDate()));

        loadPayrolls();
    }

    private void loadPayrolls() {
        try {
            List<Payroll> payrolls = payrollService.getAllPayrolls();
            ObservableList<Payroll> data = FXCollections.observableArrayList(payrolls);
            payrollTable.setItems(data);
        } catch (SQLException e) {
            AlertUtils.showErrorAlert("Database Error", "Failed to load payroll data.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddPayroll() {
        try {
            int employeeId = Integer.parseInt(txtEmployeeId.getText().trim());
            double basicSalary = Double.parseDouble(txtBasicSalary.getText().trim());
            double bonuses = txtBonuses.getText().isEmpty() ? 0 : Double.parseDouble(txtBonuses.getText().trim());
            double deductions = txtDeductions.getText().isEmpty() ? 0 : Double.parseDouble(txtDeductions.getText().trim());
            LocalDate paymentDateValue = datePayment.getValue();

            if (paymentDateValue == null) {
                AlertUtils.showWarningAlert("Validation Error", "Please select a payment date.");
                return;
            }

            double netSalary = basicSalary + bonuses - deductions;

            Payroll payroll = new Payroll();
            payroll.setEmployeeId(employeeId);
            payroll.setBasicSalary(basicSalary);
            payroll.setBonuses(bonuses);
            payroll.setDeductions(deductions);
            payroll.setNetSalary(netSalary);
            payroll.setPaymentDate(paymentDateValue);

            boolean success = payrollService.addPayroll(payroll);
            if (success) {
                AlertUtils.showInfoAlert("Success", "Payroll record added successfully.");
                clearForm();
                loadPayrolls();
            } else {
                AlertUtils.showErrorAlert("Error", "Failed to add payroll record.");
            }

        } catch (NumberFormatException e) {
            AlertUtils.showWarningAlert("Input Error", "Please enter valid numeric values.");
        } catch (SQLException e) {
            AlertUtils.showErrorAlert("Database Error", "Failed to add payroll record.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadPayrolls();
    }

    @FXML
    private void handleDeletePayroll() {
        Payroll selected = payrollTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarningAlert("No Selection", "Please select a payroll record to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the payroll record for employee: " + selected.getEmployeeName() + "?");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.isPresent() && option.get() == ButtonType.OK) {
            try {
                boolean deleted = payrollService.deletePayroll(selected.getId());
                if (deleted) {
                    AlertUtils.showInfoAlert("Deleted", "Payroll record deleted successfully.");
                    loadPayrolls();
                } else {
                    AlertUtils.showErrorAlert("Error", "Failed to delete payroll record.");
                }
            } catch (SQLException e) {
                AlertUtils.showErrorAlert("Database Error", "Failed to delete payroll record.");
                e.printStackTrace();
            }
        }
    }

    private void clearForm() {
        txtEmployeeId.clear();
        txtBasicSalary.clear();
        txtBonuses.clear();
        txtDeductions.clear();
        datePayment.setValue(null);
    }
}




