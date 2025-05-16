package pr.iss.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import pr.iss.domain.Bug;
import pr.iss.domain.Programmer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pr.iss.observer.BugObserver;
import pr.iss.service.BugTrackingService;

import java.util.List;

public class MainProgrammerController implements BugObserver {

    @FXML
    private TableView<Bug> bugTable;
    @FXML
    private TableColumn<Bug, String> nameColumn;
    @FXML
    private TableColumn<Bug, String> descriptionColumn;
    @FXML
    private TableColumn<Bug, String> reporterColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label welcomeLabel;

    private BugTrackingService service;
    private Programmer loggedInProgrammer;
    private ObservableList<Bug> bugs = FXCollections.observableArrayList();

    public void setService(BugTrackingService service, Programmer programmer) {
        this.service = service;
        this.loggedInProgrammer = programmer;
        service.addObserver(this);
        welcomeLabel.setText("Welcome back " + programmer.getUsername() + "!");
        initTable();
    }

    @Override
    public void bugsUpdated() {
        System.out.println("[DEBUG] ProgrammerController -> bugsUpdated()");
        Platform.runLater(() -> {
            List<Bug> updated = service.getAllBugs();
            System.out.println("[DEBUG] ProgrammerController -> bugs.size = " + updated.size());
            bugs.setAll(updated);
        });
    }


    private void initTable() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        reporterColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getReportedBy().getUsername()));
        bugs.setAll(service.getAllBugs());
        bugTable.setItems(bugs);
        bugTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    @FXML
    public void handleSearch() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            bugs.setAll(service.getAllBugs());
            return;
        }

        List<Bug> result = service.searchBugsByName(query);

        if (result.isEmpty()) {
            showError("Search", "No bugs found matching your query.");
            bugs.setAll(service.getAllBugs());
        } else {
            bugs.setAll(result);
        }
    }

    @FXML
    public void handleDeleteBug() {
        List<Bug> selected = bugTable.getSelectionModel().getSelectedItems();

        if (selected == null || selected.isEmpty()) {
            showError("No bug selected", "Please select one or more bugs to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete Bug(s)?");
        confirm.setContentText("Are you sure you want to delete the selected bug(s)?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    for (Bug bug : selected) {
                        service.deleteBug(loggedInProgrammer, bug);
                    }
                    showInfo("Success", "Bug(s) deleted.");
                    bugs.setAll(service.getAllBugs());
                } catch (Exception e) {
                    showError("Error", e.getMessage());
                }
            }
        });
    }


    @FXML
    public void handleLogout() {
        ((Stage) bugTable.getScene().getWindow()).close();
        service.removeObserver(this);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
