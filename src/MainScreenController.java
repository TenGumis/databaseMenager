import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

public class MainScreenController implements ControlledScreen {

    Stage currentStage;
    Statement statement;
    ControlledScreen parentController;
    static String currentTable;
    ObservableList<ObservableList> data;


    @Override
    public void init(Stage primaryStage, Statement statement, ControlledScreen cS)
    {
        currentStage = primaryStage;
        this.statement = statement;
        parentController = cS;
        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ArrayList<String> tableList = new ArrayList<>();
        try {
            ResultSet rs = statement.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';");
            while(rs.next()) {
                tableList.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (String tableName : tableList) {
            MenuItem i = new MenuItem(tableName);
            i.setOnAction(event -> {
                displayTable(i.getText(),"");
            });
            tableSelectionMenu.getItems().add(i);
        }
        displayTable(tableSelectionMenu.getItems().get(0).getText(),"");
    }

    void displayTable(String tableName,String whereConstraint)
    {
        dataTable.getColumns().clear();
        currentTable = tableName;
        currentStage.setTitle(tableName);
        data = FXCollections.observableArrayList();
        try {
            ResultSet rs = statement.executeQuery("select * from " + tableName +" "+whereConstraint+ ";");
            for(int i = 1 ; i <= rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn column = new TableColumn(rs.getMetaData().getColumnName(i));
                column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> p) {
                    return new SimpleStringProperty(p.getValue().get(j - 1).toString());
                }
            });

            dataTable.getColumns().addAll(column);
        }

        while(rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for(int i = 1 ; i <= rs.getMetaData().getColumnCount(); i++)
                row.add(rs.getString(i));
            data.add(row);
        }
        dataTable.setItems(data);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TableView dataTable;

    @FXML
    private MenuButton tableSelectionMenu;

    @FXML
    private Button searchButton;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button exitButton;

    @FXML
    void deleteFromTable(MouseEvent event) {
        if(dataTable.getSelectionModel().getSelectedItem() != null) {
            ObservableList L = (ObservableList) dataTable.getSelectionModel().getSelectedItem();
            data.remove(L);
            try {
                ResultSet rs = statement.executeQuery("select * from " + currentTable + ";");
                StringBuilder sb = new StringBuilder();
                int i = 1;
                for (Object o : L) {
                    if (i == 1) sb.append("WHERE ");
                    else sb.append(" AND ");
                    sb.append(rs.getMetaData().getColumnName(i) + "=\'" + o.toString() + "\' ");
                    i++;
                }
                if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
                //System.out.println("DELETE FROM "+currentTable+" "+sb.toString()+";");
                statement.executeUpdate("DELETE FROM " + currentTable + " " + sb.toString() + ";");
                System.out.println("Deleted successfully");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void editTableScreen(MouseEvent event) {
        if(dataTable.getSelectionModel().getSelectedItem() != null)
            try {
                Stage s = new Stage();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("FXMLs/EditScreen.fxml"));
                Scene scene = new Scene(loader.load());
                s.setScene(scene);
                ControlledScreen controller = loader.getController();
                controller.init(s, statement,this);
                s.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @FXML
    void displaySearchScreen(MouseEvent event) {
        try {
            Stage s = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("FXMLs/SelectionScreen.fxml"));
            Scene scene = new Scene(loader.load());
            s.setScene(scene);
            ControlledScreen controller = loader.getController();
            controller.init(s, statement, this);
            s.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setResizable(false);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Błąd");
            alert.setHeaderText(null);
            alert.setContentText("Dane nie mogły zostać usunięte. Spróbuj ponownie.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    @FXML
    void exit(MouseEvent event) {
        currentStage.close();
    }

    @FXML
    void insertIntoTable(MouseEvent event) {
        try {
            Stage s = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("FXMLs/InsertionScreen.fxml"));
            Scene scene = new Scene(loader.load());
            s.setScene(scene);
            ControlledScreen controller = loader.getController();
            controller.init(s, statement,this);
            s.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TableView getTable(){
        return dataTable;
    }

}