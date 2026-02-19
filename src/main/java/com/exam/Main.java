package com.exam;

import com.exam.util.DBInit;
import com.exam.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DBInit.initialize();   // Creates tables
        new LoginView().show(); // Launches JavaFX UI
    }

    public static void main(String[] args) {
        launch(args);
    }
}