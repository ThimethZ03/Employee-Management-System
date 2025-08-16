package com.yourcompany.ems.controllers;

import com.yourcompany.ems.database.DatabaseConnection;
import com.yourcompany.ems.database.EmployeeDAO;
import com.yourcompany.ems.models.Employee;
import com.yourcompany.ems.utils.AlertUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.Connection;

public class EmployeeController {

    @FXML private TextField nameField, emailField, phoneField, positionField, departmentField, salaryField;
    @FXML private DatePicker hireDatePicker;
    @FXML private ImageView avatarImageView;
    @FXML private TextField idField;


    private String selectedAvatarPath;
    private EmployeeDAO employeeDAO;
    private Employee editingEmployee = null;

    @FXML
    public void initialize() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            employeeDAO = new EmployeeDAO(connection);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Database Error", "Failed to connect to database.");
        }
    }

    public void setEmployeeForEdit(Employee emp) {
        if (emp != null) {
            this.editingEmployee = emp;

            idField.setText(String.valueOf(emp.getId()));  // set ID field (read-only)
            nameField.setText(emp.getName());
            emailField.setText(emp.getEmail());
            phoneField.setText(emp.getPhone());
            positionField.setText(emp.getPosition());
            departmentField.setText(emp.getDepartment());
            salaryField.setText(emp.getSalary().toString());
            hireDatePicker.setValue(emp.getHireDate());

            selectedAvatarPath = emp.getAvatarPath();
            if (selectedAvatarPath != null && !selectedAvatarPath.isBlank()) {
                File file = new File(selectedAvatarPath);
                if (file.exists()) {
                    avatarImageView.setImage(new Image(file.toURI().toString()));
                }
            }
        }
    }

    @FXML
    private void handleChooseAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Avatar Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                Path source = selectedFile.toPath();
                Path targetDir = Paths.get("avatars");
                if (Files.notExists(targetDir)) {
                    Files.createDirectories(targetDir);
                }

                Path target = targetDir.resolve(source.getFileName());
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

                selectedAvatarPath = target.toString();
                avatarImageView.setImage(new Image(target.toUri().toString()));
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.showErrorAlert("File Error", "Failed to copy avatar file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            if (editingEmployee == null) {
                editingEmployee = new Employee(); // create new
            }

            editingEmployee.setName(nameField.getText());
            editingEmployee.setEmail(emailField.getText());
            editingEmployee.setPhone(phoneField.getText());
            editingEmployee.setPosition(positionField.getText());
            editingEmployee.setDepartment(departmentField.getText());
            editingEmployee.setHireDate(hireDatePicker.getValue());
            editingEmployee.setSalary(new BigDecimal(salaryField.getText()));
            editingEmployee.setAvatarPath(selectedAvatarPath);

            if (editingEmployee.getId() > 0) {
                employeeDAO.updateEmployee(editingEmployee);
                AlertUtils.showSuccessAlert("Updated", "Employee updated successfully!");
            } else {
                employeeDAO.addEmployee(editingEmployee);
                AlertUtils.showSuccessAlert("Added", "Employee added successfully!");
            }

            closeForm();
        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("Invalid Input", "Salary must be a valid number.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Failed to save employee: " + e.getMessage());
        }
    }

    private void closeForm() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}