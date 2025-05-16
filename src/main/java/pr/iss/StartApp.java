package pr.iss;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pr.iss.controller.LoginController;
import pr.iss.repo.BugDbRepository;
import pr.iss.repo.UserDbRepository;
import pr.iss.service.BugTrackingService;
import pr.iss.repo.JdbcUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class StartApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Properties props = new Properties();
            URL propsUrl = getClass().getResource("/jdbc.properties");
            if (propsUrl == null) {
                throw new RuntimeException("jdbc.properties not found!");
            }
            props.load(propsUrl.openStream());

            JdbcUtils dbUtils = new JdbcUtils(props);
            UserDbRepository userRepo = new UserDbRepository(dbUtils);
            BugDbRepository bugRepo = new BugDbRepository(dbUtils);
            BugTrackingService service = new BugTrackingService(userRepo, bugRepo);
            System.out.println("[DEBUG] Service instance created: " + service);

            URL fxmlUrl = getClass().getResource("/views/LoginView.fxml");
            if (fxmlUrl == null) {
                throw new RuntimeException("LoginView.fxml not found!");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load());

            LoginController controller = loader.getController();
            controller.setService(service);

            primaryStage.setTitle("Bug Tracker - Login");
            primaryStage.setScene(scene);
            primaryStage.show();

            System.out.println("Login window displayed successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("Startup error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
