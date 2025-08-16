package com.yourcompany.ems.controllers;

import com.yourcompany.ems.database.DatabaseConnection;
import com.yourcompany.ems.database.EmployeeDAO;
import com.yourcompany.ems.models.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.util.List;

public class EmployeeManagementController {

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Integer> idCol;
    @FXML private TableColumn<Employee, String> avatarCol;
    @FXML private TableColumn<Employee, String> nameCol;
    @FXML private TableColumn<Employee, String> emailCol;
    @FXML private TableColumn<Employee, String> phoneCol;
    @FXML private TableColumn<Employee, String> departmentCol;
    @FXML private TableColumn<Employee, String> positionCol;
    @FXML private TableColumn<Employee, String> hireDateCol;
    @FXML private TableColumn<Employee, Void> editColumn;
    @FXML private TableColumn<Employee, Void> deleteColumn;

    private EmployeeDAO employeeDAO;

    @FXML
    public void initialize() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            employeeDAO = new EmployeeDAO(conn);
            setupColumns();
            addEditAndDeleteButtonsToTable(); // Add Edit/Delete button columns
            loadEmployeeData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupColumns() {
        avatarCol.setCellValueFactory(new PropertyValueFactory<>("avatarPath"));
        avatarCol.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(60);
                imageView.setFitHeight(600);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null || path.isBlank()) {
                    setGraphic(null);
                } else {
                    File file = new File(path);
                    if (file.exists()) {
                        imageView.setImage(new Image(file.toURI().toString()));
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        hireDateCol.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
    }

    private void loadEmployeeData() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        ObservableList<Employee> list = FXCollections.observableArrayList(employees);
        employeeTable.setItems(list);
    }

    @FXML
    private void handleAddEmployeeClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/yourcompany/ems/views/employee-form.fxml"));
            Parent formRoot = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Employee");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(formRoot));
            stage.showAndWait();

            loadEmployeeData(); // Refresh table after adding
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openEditForm(Employee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/yourcompany/ems/views/employee-form.fxml"));
            Parent formRoot = loader.load();

            EmployeeController controller = loader.getController();
            controller.setEmployeeForEdit(employee); // Populate form with data

            Stage stage = new Stage();
            stage.setTitle("Edit Employee");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(formRoot));
            stage.showAndWait();

            loadEmployeeData(); // Refresh after edit
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteEmployee(Employee employee) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete employee: " + employee.getName() + "?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                employeeDAO.deleteEmployee(employee.getId());
                loadEmployeeData(); // Refresh table
            }
        });
    }

    private void addEditAndDeleteButtonsToTable() {
        editColumn.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("Edit");

            {
                btn.setOnAction(event -> {
                    Employee emp = getTableView().getItems().get(getIndex());
                    openEditForm(emp);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        deleteColumn.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("Delete");

            {
                btn.setOnAction(event -> {
                    Employee emp = getTableView().getItems().get(getIndex());
                    deleteEmployee(emp);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }
}



