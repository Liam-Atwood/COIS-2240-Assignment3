package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import library.*;

public class LibraryGUI extends Application {
    private Library library = new Library();
    private Transaction transaction = Transaction.getTransaction();
    private VBox mainContent;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        mainContent = new VBox(10);
        mainContent.setPadding(new Insets(10));

        // Create menu buttons
        VBox menuButtons = createMenuButtons();
        root.setLeft(menuButtons);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createMenuButtons() {
        // Create menu
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #f0f0f0;");

        Button addMemberBtn = new Button("Add Member");
        Button addBookBtn = new Button("Add Book");
        Button borrowBookBtn = new Button("Borrow Book");
        Button returnBookBtn = new Button("Return Book");
        Button viewHistoryBtn = new Button("View History");

        addMemberBtn.setOnAction(e -> showAddMemberForm());
        addBookBtn.setOnAction(e -> showAddBookForm());
        borrowBookBtn.setOnAction(e -> showBorrowBookForm());
        returnBookBtn.setOnAction(e -> showReturnBookForm());
        viewHistoryBtn.setOnAction(e -> showTransactionHistory());

        menu.getChildren().addAll(addMemberBtn, addBookBtn, borrowBookBtn, 
                                returnBookBtn, viewHistoryBtn);
        return menu;
    }

    private void showAddMemberForm() {
        mainContent.getChildren().clear();
        
        TextField idField = new TextField();
        idField.setPromptText("Member ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Member Name");
        Button submitBtn = new Button("Add Member");
        Label statusLabel = new Label();

        submitBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                if (library.addMember(new Member(id, name))) {
                    statusLabel.setText("Member added successfully");
                    idField.clear();
                    nameField.clear();
                } else {
                    statusLabel.setText("Member ID already exists");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Please enter a valid ID");
            }
        });

        mainContent.getChildren().addAll(new Label("Add New Member"), 
                                       idField, nameField, submitBtn, statusLabel);
    }

    private void showAddBookForm() {
        mainContent.getChildren().clear();
        
        TextField idField = new TextField();
        idField.setPromptText("Book ID (100-999)");
        TextField titleField = new TextField();
        titleField.setPromptText("Book Title");
        Button submitBtn = new Button("Add Book");
        Label statusLabel = new Label();

        submitBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String title = titleField.getText();
                Book newBook = new Book(id, title);
                if (library.addBook(newBook)) {
                    statusLabel.setText("Book added successfully");
                    idField.clear();
                    titleField.clear();
                } else {
                    statusLabel.setText("Book ID already exists");
                }
            } catch (Exception ex) {
                statusLabel.setText(ex.getMessage());
            }
        });

        mainContent.getChildren().addAll(new Label("Add New Book"), 
                                       idField, titleField, submitBtn, statusLabel);
    }

    private void showBorrowBookForm() {
        mainContent.getChildren().clear();
        
        ComboBox<String> memberCombo = new ComboBox<>();
        ComboBox<String> bookCombo = new ComboBox<>();
        Button borrowBtn = new Button("Borrow Book");
        Label statusLabel = new Label();

        for (Member member : library.getMembers()) {
            memberCombo.getItems().add(member.getId() + " - " + member.getName());
        }

        for (Book book : library.getBooks()) {
            if (book.isAvailable()) {
                bookCombo.getItems().add(book.getId() + " - " + book.getTitle());
            }
        }

        borrowBtn.setOnAction(e -> {
            try {
                String memberSelection = memberCombo.getValue();
                String bookSelection = bookCombo.getValue();
                
                if (memberSelection == null || bookSelection == null) {
                    statusLabel.setText("Please select both member and book");
                    return;
                }

                int memberId = Integer.parseInt(memberSelection.split(" - ")[0]);
                int bookId = Integer.parseInt(bookSelection.split(" - ")[0]);

                Member member = library.findMemberById(memberId);
                Book book = library.findBookById(bookId);

                if (transaction.borrowBook(book, member)) {
                    statusLabel.setText("Book borrowed successfully");
                    bookCombo.getItems().remove(bookSelection);
                } else {
                    statusLabel.setText("Failed to borrow book");
                }
            } catch (Exception ex) {
                statusLabel.setText("Error processing request");
            }
        });

        mainContent.getChildren().addAll(new Label("Borrow Book"), 
                                       memberCombo, bookCombo, borrowBtn, statusLabel);
    }

    private void showReturnBookForm() {
        mainContent.getChildren().clear();
        
        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Member ID");
        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Book ID");
        Button returnBtn = new Button("Return Book");
        Label statusLabel = new Label();

        returnBtn.setOnAction(e -> {
            try {
                int memberId = Integer.parseInt(memberIdField.getText());
                int bookId = Integer.parseInt(bookIdField.getText());

                Member member = library.findMemberById(memberId);
                Book book = library.findBookById(bookId);

                if (member != null && book != null) {
                    if (transaction.returnBook(book, member)) {
                        statusLabel.setText("Book returned successfully");
                        memberIdField.clear();
                        bookIdField.clear();
                    } else {
                        statusLabel.setText("Book was not borrowed by this member");
                    }
                } else {
                    statusLabel.setText("Invalid member or book ID");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Please enter valid IDs");
            }
        });

        mainContent.getChildren().addAll(new Label("Return Book"), 
                                       memberIdField, bookIdField, returnBtn, statusLabel);
    }

    private void showTransactionHistory() {
        mainContent.getChildren().clear();
        
        TextArea historyArea = new TextArea();
        historyArea.setEditable(false);
        historyArea.setPrefRowCount(10);

        // Capture transaction history
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(baos);
        java.io.PrintStream old = System.out;
        System.setOut(ps);
        
        transaction.displayTransactionHistory();
        
        System.out.flush();
        System.setOut(old);
        historyArea.setText(baos.toString());

        mainContent.getChildren().addAll(new Label("Transaction History"), historyArea);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
