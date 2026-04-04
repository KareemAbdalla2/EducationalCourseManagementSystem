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

public class Student_Dashboard_Enrollment {
    private Stage stage;
    private User currentUser;

    // ── Available courses shown in the ComboBox ──────────────────────
    private ComboBox<String> courseComboBox = new ComboBox<>();
    // Maps display string → course id for the INSERT
    private java.util.Map<String, Integer> courseIdMap = new java.util.LinkedHashMap<>();

    // Enrolled-courses table and its backing list
    private TableView<Course> enrolledTable = new TableView<>();
    private ObservableList<Course> enrolledList = FXCollections.observableArrayList();

    public Student_Dashboard_Enrollment(Stage primaryStage, User user) {
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

        // ── Enrollment form ──────────────────────────────────────────
        Label title = new Label("Course Enrollment");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        loadAvailableCourses();

        Button btnEnroll = new Button("Enroll");
        btnEnroll.setOnAction(e -> enrollInCourse());

        HBox formRow = new HBox(10, new Label("Select Course:"), courseComboBox, btnEnroll);
        formRow.setPadding(new Insets(5, 0, 5, 0));

        // ── Enrolled courses table ───────────────────────────────────
        Label enrolledLabel = new Label("Your Current Enrollments");
        enrolledLabel.setStyle("-fx-font-weight: bold;");

        TableColumn<Course, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(220);

        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        // Drop button column
        TableColumn<Course, Void> dropCol = new TableColumn<>("Action");
        dropCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Drop");
            {
                btn.setStyle("-fx-font-size: 11px;");
                btn.setOnAction(e -> {
                    Course selected = getTableView().getItems().get(getIndex());
                    dropCourse(selected);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        enrolledTable.getColumns().addAll(idCol, nameCol, codeCol, dropCol);
        enrolledTable.setItems(enrolledList);
        loadEnrolledCourses();

        // ── Layout ───────────────────────────────────────────────────
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(
                btnBack,
                title,
                new Separator(),
                formRow,
                new Separator(),
                enrolledLabel,
                enrolledTable
        );

        stage.setTitle("Enrollment");
        stage.setScene(new Scene(layout, 650, 500));
        stage.show();
    }

    // ── Load all courses into the ComboBox (exclude already enrolled) ─
    private void loadAvailableCourses() {
        courseIdMap.clear();
        courseComboBox.getItems().clear();
        try {
            Connection con = DBUtils.establishConnection();
            // Only courses the student hasn't enrolled in yet
            String query = "SELECT id, name, code FROM course " +
                    "WHERE id NOT IN " +
                    "  (SELECT course_id FROM enrollment WHERE student_username = ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, currentUser.getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String display = rs.getString("code") + " · " + rs.getString("name");
                courseIdMap.put(display, rs.getInt("id"));
                courseComboBox.getItems().add(display);
            }
            DBUtils.closeConnection(con, stmt);
        } catch (SQLException e) {
            System.out.println("Error loading courses: " + e.getMessage());
        }
    }

    // ── Load this student's enrolled courses into the table ──────────
    private void loadEnrolledCourses() {
        enrolledList.clear();
        try {
            Connection con = DBUtils.establishConnection();
            String query = "SELECT c.id, c.name, c.code " +
                    "FROM course c " +
                    "JOIN enrollment e ON e.course_id = c.id " +
                    "WHERE e.student_username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, currentUser.getUsername());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                enrolledList.add(new Course(rs.getInt("id"), rs.getString("name"), rs.getString("code")));
            }
            DBUtils.closeConnection(con, stmt);
        } catch (SQLException e) {
            System.out.println("Error loading enrolled courses: " + e.getMessage());
        }
    }

    // ── Insert into enrollment table ─────────────────────────────────
    private void enrollInCourse() {
        String selected = courseComboBox.getValue();
        if (selected == null || selected.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select a course.");
            return;
        }
        int courseId = courseIdMap.get(selected);
        try {
            Connection con = DBUtils.establishConnection();
            String sql = "INSERT INTO enrollment (student_username, course_id) VALUES (?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, currentUser.getUsername());
            stmt.setInt(2, courseId);
            stmt.executeUpdate();
            DBUtils.closeConnection(con, stmt);

            showAlert(Alert.AlertType.CONFIRMATION, "Enrolled in " + selected + " successfully!");
            // Refresh both the table and the combobox
            loadEnrolledCourses();
            loadAvailableCourses();
        } catch (SQLException e) {
            System.out.println("Error enrolling: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Enrollment failed: " + e.getMessage());
        }
    }

    // ── Delete from enrollment table ──────────────────────────────────
    private void dropCourse(Course course) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Drop " + course.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Drop");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    Connection con = DBUtils.establishConnection();
                    String sql = "DELETE FROM enrollment WHERE student_username = ? AND course_id = ?";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, currentUser.getUsername());
                    stmt.setInt(2, course.getId());
                    stmt.executeUpdate();
                    DBUtils.closeConnection(con, stmt);
                    loadEnrolledCourses();
                    loadAvailableCourses();
                } catch (SQLException e) {
                    System.out.println("Error dropping course: " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Drop failed: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}