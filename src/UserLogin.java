import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserLogin {
    private Scene loginScene;
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Stage stage;

    public UserLogin(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(10));
        Button loginButton = new Button("Sign In");
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                validateLogin();
            }
        });
        loginLayout.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                loginButton
        );

        loginScene = new Scene(loginLayout, 600, 600);
        stage.setTitle("User Login");
        stage.setScene(loginScene);
        stage.show();
    }

    private void validateLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User loggedInUser = AuthenticationService.authenticate(username, password);
        if (loggedInUser != null) {
            if (AuthorizationService.isAdmin(loggedInUser)) {
                Admin_dashbroad addUsersPage = new Admin_dashbroad(stage);
                addUsersPage.adminDashbord();
                System.out.println("You are authorized as admin");
            } else if (AuthorizationService.isStudent(loggedInUser)) {
                // Pass the logged-in User so all student screens know who is logged in
                Student_Dashboard studentPage = new Student_Dashboard(stage, loggedInUser);
                studentPage.studentDashbord();
                System.out.println("You are authorized as student");
            }else if (AuthorizationService.isInstructor(loggedInUser)) {
                // Pass the logged-in User so all student screens know who is logged in
                Instructor_dashbroad instructorPage = new Instructor_dashbroad(stage, loggedInUser);
                instructorPage.instrutorDashbord();
                System.out.println("You are authorized as student");
            }

        } else {
            showAlert("Authentication Failed", "Invalid username or password.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}