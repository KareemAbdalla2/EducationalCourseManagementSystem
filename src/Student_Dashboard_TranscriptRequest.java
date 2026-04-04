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

public class Student_Dashboard_TranscriptRequest {
    private Stage stage;
    private User currentUser;

    // Form fields
    private ComboBox<String> typeBox     = new ComboBox<>();
    private ComboBox<String> purposeBox  = new ComboBox<>();
    private TextField notesField         = new TextField();

    // Past requests table
    private TableView<Transcript> historyTable  = new TableView<>();
    private ObservableList<Transcript> historyList = FXCollections.observableArrayList();

    public Student_Dashboard_TranscriptRequest(Stage primaryStage, User user) {
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

        // ── Form ─────────────────────────────────────────────────────
        Label title = new Label("Transcript Request");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        typeBox.getItems().addAll("Official (sealed)", "Unofficial (student copy)");
        typeBox.setValue("Official (sealed)");

        purposeBox.getItems().addAll(
                "Graduate school application",
                "Employment",
                "Transfer",
                "Personal record"
        );
        purposeBox.setValue("Graduate school application");

        notesField.setPromptText("Additional notes or required date...");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("Transcript Type:"), typeBox);
        form.addRow(1, new Label("Purpose:"),         purposeBox);
        form.addRow(2, new Label("Notes:"),           notesField);

        Button btnSubmit = new Button("Submit Request");
        btnSubmit.setOnAction(e -> submitRequest());

        // ── History table ────────────────────────────────────────────
        Label histLabel = new Label("Your Past Requests");
        histLabel.setStyle("-fx-font-weight: bold;");

        TableColumn<Transcript, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Transcript, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("student"));

        TableColumn<Transcript, String> transcriptCol = new TableColumn<>("Type / Notes");
        transcriptCol.setCellValueFactory(new PropertyValueFactory<>("transcript"));
        transcriptCol.setPrefWidth(250);

        TableColumn<Transcript, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        historyTable.getColumns().addAll(idCol, studentCol, transcriptCol, statusCol);
        historyTable.setItems(historyList);
        loadHistory();

        // ── Layout ───────────────────────────────────────────────────
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(
                btnBack,
                title,
                new Separator(),
                form,
                btnSubmit,
                new Separator(),
                histLabel,
                historyTable
        );

        stage.setTitle("Transcript Request");
        stage.setScene(new Scene(layout, 650, 550));
        stage.show();
    }

    // ── Load this student's past transcript requests ─────────────────
    private void loadHistory() {
        historyList.clear();
        try {
            Connection con = DBUtils.establishConnection();
            // Fetch rows where the student column matches the logged-in user
            String query = "SELECT id, student, transcript, status FROM transcript WHERE student = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, currentUser.getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                historyList.add(new Transcript(
                        rs.getInt("id"),
                        rs.getString("student"),
                        rs.getString("transcript"),
                        rs.getString("status")
                ));
            }
            DBUtils.closeConnection(con, stmt);
        } catch (SQLException e) {
            System.out.println("Error loading transcript history: " + e.getMessage());
        }
    }

    // ── Insert a new transcript request (status = "pending") ─────────
    private void submitRequest() {
        String type    = typeBox.getValue();
        String purpose = purposeBox.getValue();
        String notes   = notesField.getText().trim();

        // Build a combined description stored in the "transcript" column
        String description = type + " | " + purpose + (notes.isEmpty() ? "" : " | " + notes);

        try {
            Connection con = DBUtils.establishConnection();
            String sql = "INSERT INTO transcript (student, transcript, status) VALUES (?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, currentUser.getUsername());
            stmt.setString(2, description);
            stmt.setString(3, "pending");
            stmt.executeUpdate();
            DBUtils.closeConnection(con, stmt);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Request Submitted");
            alert.setHeaderText(null);
            alert.setContentText("Your transcript request has been submitted and is pending approval.");
            alert.showAndWait();

            notesField.clear();
            loadHistory();
        } catch (SQLException e) {
            System.out.println("Error submitting transcript request: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Submission failed: " + e.getMessage());
            alert.showAndWait();
        }
    }
}