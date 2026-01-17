package application;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Category;

public class CategoryFormDialog extends Dialog<Category> {

    public CategoryFormDialog() {

        setTitle("Add Category");
        setHeaderText(null);

        // ===== Buttons =====
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        // ===== Fields =====
        TextField nameField = new TextField();
        nameField.setPromptText("Category name");

        TextField descField = new TextField();
        descField.setPromptText("Description (optional)");

        // ===== Layout =====
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Category Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);

        getDialogPane().setContent(grid);

        // ===== Validation + Result =====
        setResultConverter(button -> {
            if (button != saveBtn) return null;

            String name = nameField.getText().trim();
            String desc = descField.getText().trim();

            if (name.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Category name is required");
                a.setHeaderText(null);
                a.showAndWait();
                return null;
            }

            return new Category(0, name, desc);
        });
    }
}