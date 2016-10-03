package DelphiToCs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main extends Application {
    public static final ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("formDemo.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Scene scene = new Scene(root, 726, 564);
            primaryStage.setTitle("Sales Info");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
