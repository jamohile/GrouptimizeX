package ca.mohile;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Grouptimize extends Application{
    static Iteration iteration;

    public static void main(String[] args) {
        iteration = new Iteration();
        //iteration.configureFromJSONFile("anj's.json");
        //iteration.optimize(3, 2);
        launch();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Grouptimize.fxml"));
        Parent root = loader.load();
        loader.<GrouptimizeUIController>getController().config(root, iteration);

        primaryStage.setTitle("Grouptimize");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }
}
