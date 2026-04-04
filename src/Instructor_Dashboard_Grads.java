import com.sun.javafx.scene.control.InputField;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Instructor_Dashboard_Grads {
    private  Course course;
    private Stage stage;
    private User currentUser;


    // Enrolled-courses table and its backing list
    private TableView<enrollment> enrolledTable = new TableView<>();

    public Instructor_Dashboard_Grads(Stage primaryStage, User user, Course id) {
        this.stage = primaryStage;
        this.currentUser = user;
        this.course = id;
    }

    public void show() {
        // ── Back button ──────────────────────────────────────────────
        Button btnBack = new Button("← Back to Dashboard");
        btnBack.setOnAction(e -> {
            Instructor_dashbroad dash = new Instructor_dashbroad(stage, currentUser);
            dash.instrutorDashbord();
        });


        Button refresh = new Button("Refresh Dashboard");
        refresh.setOnAction(e -> {
            Instructor_Dashboard_Grads n = new Instructor_Dashboard_Grads(stage,currentUser,course);
            n.show();
        });

        // ── Enrollment form ──────────────────────────────────────────
        Label title = new Label("Student Enrolled");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");


        // ── Enrolled courses table ───────────────────────────────────
        Label enrolledLabel = new Label("All studetn Enrollmented in "+course.getCode()+" "+course.getName());
        enrolledLabel.setStyle("-fx-font-weight: bold;");

        TableColumn<enrollment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<enrollment, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("student_name"));
        nameCol.setPrefWidth(220);

        TableColumn<enrollment, String> codeCol = new TableColumn<>("Grade");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("grad"));


        TableColumn<enrollment, Integer> updateGradeCol = new TableColumn<>("Grade");

        updateGradeCol.setCellFactory(col -> new TableCell<>() {
            private final Spinner<Integer> spinner = new Spinner<>(0, 100, 0);

            {
                spinner.setEditable(true); // allow typing

                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    enrollment selected = getTableView().getItems().get(getIndex());
                    if (selected != null) {
                        selected.setGrad(newVal);  // Update your model
                        // Optional: update database
                        updateGradeInDB(selected.getId(), newVal);
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    enrollment enrollment = getTableView().getItems().get(getIndex());
                    spinner.getValueFactory().setValue(enrollment.getGrad());
                    setGraphic(spinner);
                }
            }
        });




        enrolledTable.getColumns().addAll(idCol, nameCol, codeCol,updateGradeCol);

        ObservableList<enrollment> enrollmenttList = FXCollections.observableArrayList();
        // Retrieve data from DB and fill up the table
        try {
            Connection con = DBUtils.establishConnection();
            String query = "SELECT id,student_username,course_id,enrolled_at,grad FROM enrollment where course_id ="+course.getId()+"";

            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();


            while (rs.next()) {
                // Use the command model class to create a command object from each rowd
                enrollment command = new enrollment(rs.getInt("id"), rs.getString("student_username"), rs.getInt("course_id"), rs.getInt("grad"));
                // Add the command object to the observable list
                enrollmenttList.add(command);
            }

            DBUtils.closeConnection(con, stmt);
        } catch (SQLException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
        enrolledTable.setItems(enrollmenttList);

        // ── Layout ───────────────────────────────────────────────────
        HBox h  =new HBox(btnBack,refresh);
        // Create the layout (VBox that contains the table)

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(
                h,
                title,
                new Separator(),
                new Separator(),
                enrolledLabel,
                enrolledTable
        );

        stage.setTitle("Enrollment");
        stage.setScene(new Scene(layout, 650, 500));
        stage.show();
    }

    private void updateGradeInDB(int id, Integer newVal) {
        try {
            Connection con = DBUtils.establishConnection();
            String sql = "UPDATE  enrollment SET grad =? WHERE id = ? ";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, newVal);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            DBUtils.closeConnection(con, stmt);



        } catch (SQLException e) {
            System.out.println("Error Status change: " + e.getMessage());

        }
    }


    // ── Load all courses into the ComboBox (exclude already enrolled) ─





    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}