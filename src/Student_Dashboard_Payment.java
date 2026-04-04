import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Student_Dashboard_Payment {
    private Stage stage;
    private User currentUser;

    // Form fields
    private TextField amountField  = new TextField();
    private ComboBox<String> methodBox = new ComboBox<>();
    private TextField descField    = new TextField();

    // Payment history table
    private TableView<Payment> historyTable = new TableView<>();
    private ObservableList<Payment> historyList = FXCollections.observableArrayList();

    // Balance labels (updated after each payment)
    private Label balanceLabel = new Label();
    private Label paidLabel    = new Label();

    public Student_Dashboard_Payment(Stage primaryStage, User user) {
        this.stage = primaryStage;
        this.currentUser = user;
    }

    public void show() {
        // ── Back button ──────────────────────────────────────────────
        Button btnBack = new Button("← Back to Dashboard");
        btnBack.setOnAction(e -> {
            Student_Dashboard dash = new Student_Dashboard(stage, currentUser);
            dash.studentDashbord();
        });

        Label title = new Label("Payments");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // ── Summary labels ───────────────────────────────────────────
        balanceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #c0392b;");
        paidLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
        refreshSummary();

        HBox summaryBox = new HBox(20,
                new VBox(2, new Label("Outstanding Balance:"), balanceLabel),
                new VBox(2, new Label("Total Paid:"), paidLabel)
        );
        summaryBox.setPadding(new Insets(8, 0, 8, 0));

        // ── Payment form ─────────────────────────────────────────────
        Label formLabel = new Label("Record a Payment");
        formLabel.setStyle("-fx-font-weight: bold;");

        amountField.setPromptText("Amount (e.g. 1200)");
        methodBox.getItems().addAll("Credit / Debit Card", "Bank Transfer", "Cash at Finance Office");
        methodBox.setValue("Credit / Debit Card");
        descField.setPromptText("Description (e.g. Spring 2025 tuition)");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("Amount:"),      amountField);
        form.addRow(1, new Label("Method:"),      methodBox);
        form.addRow(2, new Label("Description:"), descField);

        Button btnPay = new Button("Submit Payment");
        btnPay.setOnAction(e -> submitPayment());

        // ── History table ────────────────────────────────────────────
        Label histLabel = new Label("Payment History");
        histLabel.setStyle("-fx-font-weight: bold;");

        TableColumn<Payment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Payment, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);

        TableColumn<Payment, Double> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Payment, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("method"));

        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        historyTable.getColumns().addAll(idCol, descCol, amtCol, methodCol, statusCol);
        historyTable.setItems(historyList);
        loadHistory();

        // ── Layout ───────────────────────────────────────────────────
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(
                btnBack,
                title,
                new Separator(),
                summaryBox,
                new Separator(),
                formLabel,
                form,
                btnPay,
                new Separator(),
                histLabel,
                historyTable
        );

        stage.setTitle("Payments");
        stage.setScene(new Scene(layout, 650, 600));
        stage.show();
    }

    // ── Calculate and display balance summary ─────────────────────────
    private void refreshSummary() {
        try {
            Connection con = DBUtils.establishConnection();

            // Total paid by this student
            String paidQuery = "SELECT COALESCE(SUM(amount), 0) AS total FROM payment WHERE student_username = ?";
            PreparedStatement paidStmt = con.prepareStatement(paidQuery);
            paidStmt.setString(1, currentUser.getUsername());
            ResultSet paidRs = paidStmt.executeQuery();
            double totalPaid = 0;
            if (paidRs.next()) totalPaid = paidRs.getDouble("total");

            // Total fees = number of enrolled courses × 900 (adjust to match your fee structure)
            String feeQuery = "SELECT COUNT(*) AS cnt FROM enrollment WHERE student_username = ?";
            PreparedStatement feeStmt = con.prepareStatement(feeQuery);
            feeStmt.setString(1, currentUser.getUsername());
            ResultSet feeRs = feeStmt.executeQuery();
            int enrolledCount = 0;
            if (feeRs.next()) enrolledCount = feeRs.getInt("cnt");

            double totalFees    = enrolledCount * 900.0;
            double outstanding  = Math.max(0, totalFees - totalPaid);

            balanceLabel.setText(String.format("%.2f", outstanding));
            paidLabel.setText(String.format("%.2f", totalPaid));

            DBUtils.closeConnection(con, paidStmt);
        } catch (SQLException e) {
            System.out.println("Error refreshing summary: " + e.getMessage());
            balanceLabel.setText("N/A");
            paidLabel.setText("N/A");
        }
    }

    // ── Load payment history from DB ─────────────────────────────────
    private void loadHistory() {
        historyList.clear();
        try {
            Connection con = DBUtils.establishConnection();
            String query = "SELECT id, description, amount, method, status FROM payment WHERE student_username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, currentUser.getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                historyList.add(new Payment(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("method"),
                        rs.getString("status")
                ));
            }
            DBUtils.closeConnection(con, stmt);
        } catch (SQLException e) {
            System.out.println("Error loading payment history: " + e.getMessage());
        }
    }

    // ── Insert a payment record ───────────────────────────────────────
    private void submitPayment() {
        String amountText = amountField.getText().trim();
        String method     = methodBox.getValue();
        String desc       = descField.getText().trim();

        if (amountText.isEmpty() || desc.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please fill in Amount and Description.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "Please enter a valid positive amount.");
            return;
        }

        try {
            Connection con = DBUtils.establishConnection();
            String sql = "INSERT INTO payment (student_username, description, amount, method, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, currentUser.getUsername());
            stmt.setString(2, desc);
            stmt.setDouble(3, amount);
            stmt.setString(4, method);
            stmt.setString(5, "paid");
            stmt.executeUpdate();
            DBUtils.closeConnection(con, stmt);

            showAlert(Alert.AlertType.CONFIRMATION, "Payment of " + amount + " recorded successfully!");
            amountField.clear();
            descField.clear();
            loadHistory();
            refreshSummary();
        } catch (SQLException e) {
            System.out.println("Error recording payment: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Payment failed: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}