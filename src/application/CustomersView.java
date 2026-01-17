package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CustomersView {

    public void show(Stage stage, Runnable backAction) {

        Label title = new Label("Customers");
        title.getStyleClass().add("sb-title");

        Button back = new Button("Back");
        back.getStyleClass().addAll("sb-pill", "sb-accent");
        back.setOnAction(e -> backAction.run());

        BorderPane root = new BorderPane();
        root.setTop(new ToolBar(title, back));
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Admin Panel");
    }
}