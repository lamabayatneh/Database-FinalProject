package application;

import dao.CategoryDAO;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import model.Book;
import model.Category;

import java.io.File;
import java.time.LocalDate;

public class BookFormDialog extends Dialog<Book> {

    private final TextField titleField = new TextField();
    private final TextField authorField = new TextField();
    private final TextField priceField = new TextField();
    private final TextField quantityField = new TextField();
    private final DatePicker datePicker = new DatePicker();
    private final ComboBox<Category> categoryBox = new ComboBox<>();
    private final TextField imagePathField = new TextField();

    public BookFormDialog(Book book) {
        setTitle(book == null ? "Add Book" : "Edit Book");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        categoryBox.getItems().addAll(CategoryDAO.getAllCategories());

        Button browseBtn = new Button("Browse");
        browseBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            File file = fc.showOpenDialog(getOwner());
            if (file != null) {
                imagePathField.setText(file.getAbsolutePath());
            }
        });

        grid.addRow(0, new Label("Title:"), titleField);
        grid.addRow(1, new Label("Author:"), authorField);
        grid.addRow(2, new Label("Price:"), priceField);
        grid.addRow(3, new Label("Quantity:"), quantityField);
        grid.addRow(4, new Label("Added Date:"), datePicker);
        grid.addRow(5, new Label("Category:"), categoryBox);
        grid.addRow(6, new Label("Image Path:"), imagePathField, browseBtn);

        getDialogPane().setContent(grid);

        // EDIT MODE
        if (book != null) {
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            priceField.setText(String.valueOf(book.getPrice()));
            quantityField.setText(String.valueOf(book.getQuantity()));
            datePicker.setValue(book.getAddedDate());
            categoryBox.setValue(book.getCategory());
            imagePathField.setText(book.getImagePath());
        } else {
            datePicker.setValue(LocalDate.now());
        }

        setResultConverter(btn -> {
            if (btn == saveBtn) {
                return new Book(
                        0,
                        titleField.getText(),
                        authorField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(quantityField.getText()),
                        datePicker.getValue(),
                        categoryBox.getValue(),
                        imagePathField.getText()
                );
            }
            return null;
        });
    }
}