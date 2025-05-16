package pr.iss.controller;

import pr.iss.domain.Programmer;
import pr.iss.domain.Tester;
import pr.iss.domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import pr.iss.service.BugTrackingService;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private BugTrackingService service;

    public void setService(BugTrackingService service) {
        this.service = service;
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            System.out.println("Attempting login with: " + username + " / " + password);

            User user = service.login(username, password);

            System.out.println("Logged in user: " + user);

            if (user instanceof Tester) {
                openTesterView((Tester) user);
                //((Stage) usernameField.getScene().getWindow()).close();
            } else if (user instanceof Programmer) {
                openProgrammerView((Programmer) user);
                //((Stage) usernameField.getScene().getWindow()).close();
            }

        } catch (Exception e) {
            showAlert("Login Error", e.getMessage());
            e.printStackTrace();
        }
    }



    private void openTesterView(Tester tester) throws IOException {

        System.out.println("Loading MainTesterView.fxml...");
        URL fxmlUrl = getClass().getResource("/views/MainTesterView.fxml");
        System.out.println("FXML path: " + fxmlUrl);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load());

        MainTesterController controller = loader.getController();
        controller.setService(service, tester);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Bug Tracker - Tester");
        stage.show();
    }


    private void openProgrammerView(Programmer programmer) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainProgrammerView.fxml"));
        Scene scene = new Scene(loader.load());
        MainProgrammerController controller = loader.getController();
        controller.setService(service, programmer);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Bug Tracker - Programmer");
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
