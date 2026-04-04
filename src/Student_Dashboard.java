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

public class Student_Dashboard {
    private Scene studentScene;
    private Stage stage;
    private User currentUser;

    public Student_Dashboard(Stage primaryStage, User user) {
        this.stage = primaryStage;
        this.currentUser = user;
    }

    public void studentDashbord() {
        // ── Top nav buttons ──────────────────────────────────────────
        Button btnEnrollment       = new Button("Enrollment");
        Button btnTranscriptReq    = new Button("Transcript Request");
        Button btnPayment          = new Button("Payment");

        btnEnrollment.setOnAction(e -> {
            Student_Dashboard_Enrollment s = new Student_Dashboard_Enrollment(stage, currentUser);
            s.show();
        });
        btnTranscriptReq.setOnAction(e -> {
            Student_Dashboard_TranscriptRequest s = new Student_Dashboard_TranscriptRequest(stage, currentUser);
            s.show();
        });
        btnPayment.setOnAction(e -> {
            Student_Dashboard_Payment s = new Student_Dashboard_Payment(stage, currentUser);
            s.show();
        });

        HBox navBar = new HBox(10, btnEnrollment, btnTranscriptReq, btnPayment);
        navBar.setPadding(new Insets(0, 0, 10, 0));

        // ── Courses table ────────────────────────────────────────────
        Label coursesLabel = new Label("Your Enrolled Courses");
        coursesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TableView<Course> table = new TableView<>();

        TableColumn<Course, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        table.getColumns().addAll(idCol, nameCol, codeCol);

        ObservableList<Course> courseList = FXCollections.observableArrayList();
        try {
            Connection con = DBUtils.establishConnection();
            // Show only courses this student is enrolled in
            String query = "SELECT c.id, c.name, c.code " +
                    "FROM course c " +
                    "JOIN enrollment e ON e.course_id = c.id " +
                    "WHERE e.student_username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, currentUser.getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courseList.add(new Course(rs.getInt("id"), rs.getString("name"), rs.getString("code")));
            }
            DBUtils.closeConnection(con, stmt);
        } catch (SQLException e) {
            System.out.println("Error fetching enrolled courses: " + e.getMessage());
        }
        table.setItems(courseList);

        // ── Layout ───────────────────────────────────────────────────
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(
                new Label("Welcome, " + currentUser.getUsername()),
                navBar,
                new Separator(),
                coursesLabel,
                table
        );

        studentScene = new Scene(layout, 650, 500);
        stage.setTitle("Student Dashboard");
        stage.setScene(studentScene);
        stage.show();
    }
}