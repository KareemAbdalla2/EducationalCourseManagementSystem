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

public class Admin_dashbroad_addStudent {
    private Scene Addusers;
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private TextField role = new TextField();
    private TextField Email = new TextField();
    private Stage stage;

    public Admin_dashbroad_addStudent(Stage primaryStage) {
        this.stage = primaryStage;
    }


    public void addNewuser() {
        Button btnBack = new Button("← Back to Dashboard");
        btnBack.setOnAction(e -> {
            Admin_dashbroad dash = new Admin_dashbroad(stage);
            dash.adminDashbord();
        });
        VBox adminLayout = new VBox(10);
        adminLayout.setPadding(new Insets(10));
        Button addUser = new Button("Add a new user");
        addUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               addNewUser();
            }
        });
        adminLayout.getChildren().addAll(btnBack,new Label("User name "),usernameField,
                new Label("Password:"), passwordField,
                new Label("role:"), role,
                new Label("email:"), Email,
                addUser);
        Addusers = new Scene(adminLayout, 600, 600);
        stage.setTitle("Add users Dashboard");
        stage.setScene(Addusers);
        stage.show();
    }

    private  void addNewUser(){
        String salt = BCrypt.gensalt(12);
        // concatenate the salt with the password and hash the result:
        String hashedPassword = BCrypt.hashpw(passwordField.getText(), salt);
        String name = usernameField.getText();
        String password1 = hashedPassword;
        String role1 = role.getText();
        String email =  Email.getText();




        String sql = "INSERT INTO users (username, password, role, email) VALUES (?, ?, ?, ?)";
        try {
            Connection con = DBUtils.establishConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, password1);
            statement.setString(3, role1);
            statement.setString(4, email);

            int result = statement.executeUpdate();
            System.out.println(result);
            DBUtils.closeConnection(con, statement);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("User has been add");
            alert.setContentText("User has been add");
            alert.showAndWait();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }




}
