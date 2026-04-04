import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Instructor_dashbroad {
    private Scene InstrutorScene;
    private Stage stage;
    private User currentUser;

    public Instructor_dashbroad(Stage primaryStage, User loggedInUser) {
        this.stage = primaryStage;
        this.currentUser = loggedInUser;
    }

        public void instrutorDashbord() {
        TableView<Course> table = new TableView<>();

        // Define the first column of the table, <Command, Integer> means the data type
        // of each row is a command, and the data type of values in this column is an integr (the ID)
        TableColumn<Course, Integer> idColumn = new TableColumn<>("ID");
        // PropertyValueFactory<>("id") will call the getId() method in the model class
        // which will fill the cell with the command id value for every row.
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Define the rest of the table columns in the same way
        TableColumn<Course, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> codeColumn = new TableColumn<>("Code");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Course, Void> showcolumn = new TableColumn<>("Action");
        showcolumn.setCellFactory(col -> new TableCell<>() {
                private final Button btn = new Button("Show");
                {
                    btn.setStyle("-fx-font-size: 11px;");
                    btn.setOnAction(e -> {
                        Course selected = getTableView().getItems().get(getIndex());
                        showStudent(selected);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
        });
            table.getColumns().addAll(idColumn, nameColumn, codeColumn,showcolumn);
            ObservableList<Course> commandsList = FXCollections.observableArrayList();
        // Retrieve data from DB and fill up the table
        try{
            Connection con = DBUtils.establishConnection();
            String query = "SELECT id, name, code FROM course";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();


            while (rs.next()) {
                // Use the command model class to create a command object from each row
                Course command = new Course(rs.getInt("id"), rs.getString("name"), rs.getString("code"));
                // Add the command object to the observable list

                commandsList.add(command);
            }

            DBUtils.closeConnection(con, stmt);
        }catch (SQLException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
        //Set the table to watch the observable list
        //the table will read data from it, and will also update upon any change
        table.setItems(commandsList);


        // Create the layout (VBox that contains the table)
        VBox vbox = new VBox(table);
        // Add the layout to the scene
            Label coursesLabel = new Label("All Courses");
            coursesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        VBox adminLayout = new VBox(10);
        adminLayout.setPadding(new Insets(10));

        HBox h = new HBox();
        adminLayout.getChildren().addAll(h,new Separator(),coursesLabel,vbox );
            InstrutorScene = new Scene(adminLayout, 600, 600);
        stage.setTitle("Instructor Dashboard");
        stage.setScene(InstrutorScene);
        stage.show();
    }

    private void showStudent(Course id) {
        Instructor_Dashboard_Grads n = new Instructor_Dashboard_Grads(stage,currentUser,id);
        n.show();
    }


}
