package application;

import dao.BookDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Book;

public class AdminBooksView extends BorderPane {

    private final TableView<Book> table = new TableView<>();
    private final Stage stage;

    public AdminBooksView(Stage stage) {
        this.stage = stage;
        setPadding(new Insets(15));

        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(
                d.getValue().getBookID()).asObject());

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getTitle()));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getAuthor()));

        TableColumn<Book, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleDoubleProperty(d.getValue().getPrice()).asObject());

        TableColumn<Book, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(d.getValue().getQuantity()).asObject());

        table.getColumns().addAll(idCol, titleCol, authorCol, priceCol, qtyCol);
        refresh();

        Button addBtn = new Button("Add Book");
        Button editBtn = new Button("Edit Book");
        Button deleteBtn = new Button("Delete Book");
        Button refreshBtn = new Button("Refresh");

        addBtn.setOnAction(e -> {
            BookFormDialog dialog = new BookFormDialog(null);
            dialog.initOwner(stage);

            dialog.showAndWait().ifPresent(book -> {
                BookDAO.insertBook(book);
                refresh();
            });
        });

        editBtn.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Select a book first").show();
                return;
            }

            BookFormDialog dialog = new BookFormDialog(selected);
            dialog.initOwner(stage);

            dialog.showAndWait().ifPresent(updated -> {
                updated.setBookID(selected.getBookID());
                BookDAO.updateBook(updated);
                refresh();
            });
        });

        deleteBtn.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                BookDAO.deleteBook(selected.getBookID());
                refresh();
            }
        });

        refreshBtn.setOnAction(e -> refresh());

        HBox top = new HBox(10, addBtn, editBtn, deleteBtn, refreshBtn);
        setTop(top);
        setCenter(table);
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(BookDAO.getAllBooks()));
    }
}