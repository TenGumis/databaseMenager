import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import sun.applet.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class InsertionScreenController implements ControlledScreen {

    Stage currentStage;
    Statement statement;
    MainScreenController parentController;
    String currentTable;
    ObservableList<LabelledTextField> data;

    @Override
    public void init(Stage s, Statement statement, ControlledScreen cS) {
        currentStage = s;
        parentController = (MainScreenController) cS;
        this.statement = statement;
        currentTable = MainScreenController.currentTable;
        data = FXCollections.observableArrayList();
        dataTable.setEditable(true);
        columnNameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().value);
        valueColumn.setEditable(true);
        valueColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<LabelledTextField, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<LabelledTextField, String> t) {
                (t.getTableView().getItems().get(t.getTablePosition().getRow())).value = new SimpleStringProperty(t.getNewValue());
            }
        });
        try {
            ResultSet rs = statement.executeQuery("select column_name from information_schema.columns where table_name = '" + currentTable + "';");
            while(rs.next()) {
                data.add(new LabelledTextField(rs.getString(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataTable.setItems(data);
    }

    @FXML
    private TableView<LabelledTextField> dataTable;

    @FXML
    private TableColumn<LabelledTextField, String> columnNameColumn;

    @FXML
    private TableColumn<LabelledTextField, String> valueColumn;

    @FXML
    private Button exitButton;


    @FXML
    void attemptInsert(MouseEvent event) {
        try {
            StringBuilder sb=new StringBuilder();
            StringBuilder sb2=new StringBuilder();
            sb2.append(" (");
            for (LabelledTextField l : data) {
                if(valueColumn.getCellData(l)!=null && valueColumn.getCellData(l).length()>0) {
                    sb.append("\'" + valueColumn.getCellData(l) + "\',");
                    sb2.append(columnNameColumn.getCellData(l)+",");
                }
            }
            System.out.println(sb2.toString());
            if(sb.length()>0) sb.deleteCharAt(sb.length()-1);
            if(sb2.length()>0) sb2.deleteCharAt(sb2.length()-1);
            sb.append(");");
            sb2.append(") ");
            System.out.println("INSERT INTO "+currentTable+" "+sb2.toString()+" VALUES ("+sb.toString());
            statement.executeUpdate("INSERT INTO "+currentTable+" "+sb2.toString()+" VALUES ("+sb.toString());
            System.out.println("Inserted successfully");
            parentController.displayTable(parentController.currentTable,"");
            currentStage.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setResizable(false);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Niepoprawne dane.");
            alert.setHeaderText(null);
            alert.setContentText("Dane nie mogły być wprowadzone do tabeli. Spróbuj ponownie.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    @Override
    public TableView getTable(){
        return null;
    }
}
