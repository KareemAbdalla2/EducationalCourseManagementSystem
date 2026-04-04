import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class Admin_dashbroad_Transcript {
    private Stage stage;

    public Admin_dashbroad_Transcript(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        stage.setTitle("Transcript Viewer");
        // To display data in a table, use the JavaFX TableView
        // <Command> means the data type of each row in the table is a Command object
        TableView<Transcript> table = new TableView<>();

        // Define the first column of the table, <Command, Integer> means the data type
        // of each row is a command, and the data type of values in this column is an integr (the ID)
        TableColumn<Transcript, Integer> idColumn = new TableColumn<>("ID");
        // PropertyValueFactory<>("id") will call the getId() method in the model class
        // which will fill the cell with the command id value for every row.
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Define the rest of the table columns in the same way
        TableColumn<Transcript, String> studentColumn = new TableColumn<>("Student");
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("student"));

        TableColumn<Transcript, String> transcriptColumn = new TableColumn<>("Transcript");
        transcriptColumn.setCellValueFactory(new PropertyValueFactory<>("transcript"));

        TableColumn<Transcript, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Drop button column
        TableColumn<Transcript, Void> statusActionColumn = new TableColumn<>("Change Status");

        statusActionColumn.setCellFactory(col -> new TableCell<>() {

            private final ComboBox<String> comboBox = new ComboBox<>();

            {
                comboBox.getItems().addAll("Pending", "Approved", "Rejected");

                comboBox.setOnAction(e -> {
                    Transcript selected = getTableView().getItems().get(getIndex());

                    if (selected != null) {
                        String newStatus = comboBox.getValue();

                        selected.setStatus(newStatus);

                        // Optional: update database
                        changeStatus(selected.getId(), newStatus);

                        table.viewOrderProperty();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Transcript transcript = getTableView().getItems().get(getIndex());
                    comboBox.setValue(transcript.getStatus());
                    setGraphic(comboBox);
                }
            }
        });
        // Add all columns to the table
        table.getColumns().addAll(idColumn, studentColumn, transcriptColumn, statusColumn, statusActionColumn);

        // We will use Observable List to hold the data retrieved from the database
        // then pass it to the table so that cells are filled with the obtained data
        // observable lists are useful in some situations since the table will be
        // auto updated upon any change in the observable list (e.g. new row added, deleted, etc.)
        ObservableList<Transcript> transcriptList = FXCollections.observableArrayList();

        // Retrieve data from DB and fill up the table
        try {
            Connection con = DBUtils.establishConnection();
            String query = "SELECT id, student, transcript,status FROM transcript";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();


            while (rs.next()) {
                // Use the command model class to create a command object from each row
                Transcript command = new Transcript(rs.getInt("id"), rs.getString("student"), rs.getString("transcript"), rs.getString("status"));
                // Add the command object to the observable list
                transcriptList.add(command);
            }

            DBUtils.closeConnection(con, stmt);
        } catch (SQLException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
        //Set the table to watch the observable list
        //the table will read data from it, and will also update upon any change
        table.setItems(transcriptList);

        Button btnBack = new Button("← Back to Dashboard");
        btnBack.setOnAction(e -> {
            Admin_dashbroad dash = new Admin_dashbroad(stage);
            dash.adminDashbord();
        });
        Button refresh = new Button("Refresh Dashboard");
        refresh.setOnAction(e -> {
            Admin_dashbroad_Transcript n = new Admin_dashbroad_Transcript(stage);
                    n.initializeComponents();
        });

        HBox h  =new HBox(btnBack,refresh);
        // Create the layout (VBox that contains the table)
        VBox vbox = new VBox(h, table);
        // Add the layout to the scene
        Scene scene = new Scene(vbox, 750, 500);

        //Add the scene to stage
        stage.setScene(scene);
        stage.show();
        return;
    }

    public void changeStatus(int id, String newStatus) {
        try {
            Connection con = DBUtils.establishConnection();
            String sql = "UPDATE  transcript SET status =? WHERE id = ? ";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            DBUtils.closeConnection(con, stmt);



        } catch (SQLException e) {
            System.out.println("Error Status change: " + e.getMessage());

        }

    }


}
