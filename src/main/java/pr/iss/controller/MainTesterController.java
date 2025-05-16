package pr.iss.controller;

import javafx.application.Platform;
import javafx.stage.Stage;
import pr.iss.domain.Bug;
import pr.iss.domain.Tester;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pr.iss.observer.BugObserver;
import pr.iss.service.BugTrackingService;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class MainTesterController implements BugObserver {

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
    private TextField bugNameField;
    @FXML
    private TextArea bugDescriptionField;
    @FXML
    private Label welcomeLabel;

    private BugTrackingService service;
    private Tester loggedInTester;
    private ObservableList<Bug> bugs = FXCollections.observableArrayList();

    public void setService(BugTrackingService service, Tester tester) {
        this.service = service;
        this.loggedInTester = tester;
        service.addObserver(this);
        welcomeLabel.setText("Welcome back " + tester.getUsername() + "!");
        initTable();
    }

    @Override
    public void bugsUpdated() {
        System.out.println("[DEBUG] TesterController -> bugsUpdated()");
        Platform.runLater(() -> {
            List<Bug> updated = service.getAllBugs();
            System.out.println("[DEBUG] TesterController -> bugs.size = " + updated.size());
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
    public void handleReportBug() {
        String name = bugNameField.getText();
        String description = bugDescriptionField.getText();

        if (name == null || name.isBlank() || description == null || description.isBlank()) {
            showError("Input Error", "Please fill in both name and description.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Bug Report");
        confirm.setHeaderText("Are you sure you want to report this bug?");
        confirm.setContentText("Name: " + name + "\nDescription: " + description);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    service.reportBug(loggedInTester, name, description);
                    showInfo("Success", "Bug reported successfully!");
                    bugs.setAll(service.getAllBugs());
                    bugNameField.clear();
                    bugDescriptionField.clear();
                } catch (Exception e) {
                    showError("Error reporting bug", e.getMessage());
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
