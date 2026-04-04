import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Admin_dashbroad_addCourse {
    private Scene AddCourse;
    private TextField courseName = new TextField();
    private TextField courseCode = new TextField();
    private Stage stage;

    public Admin_dashbroad_addCourse(Stage primaryStage) {
        this.stage = primaryStage;
    }


    public void addNewCourse() {
        Button btnBack = new Button("← Back to Dashboard");
        btnBack.setOnAction(e -> {
            Admin_dashbroad dash = new Admin_dashbroad(stage);
            dash.adminDashbord();
        });
        VBox adminLayout = new VBox(10);
        adminLayout.setPadding(new Insets(10));
        Button addcourse = new Button("Add a new user");
        addcourse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               addNewUser();
            }
        });

        adminLayout.getChildren().addAll(btnBack ,new Label("name "),courseName,
                new Label("code:"), courseCode,
                addcourse);
        AddCourse = new Scene(adminLayout, 600, 600);
        stage.setTitle("Add Course Dashboard");
        stage.setScene(AddCourse);
        stage.show();
    }

    private  void addNewUser(){
        // concatenate the salt with the password and hash the result:
        String name = courseName.getText();
        String code = courseCode.getText();



        String sql = "INSERT INTO course (name, code) VALUES (?, ?)";
        try {
            Connection con = DBUtils.establishConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, code);
            int result = statement.executeUpdate();
            System.out.println(result);
            DBUtils.closeConnection(con, statement);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Course has been add");
            alert.setContentText("Course has been add");
            alert.showAndWait();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }




}
