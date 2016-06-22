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

public class SelectionScreenController implements ControlledScreen {

    Stage currentStage;
    Statement statement;
    String currentTable;
    MainScreenController parentController;
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
    void select(MouseEvent event) {
            StringBuilder sb=new StringBuilder();
            sb.append(" WHERE true");
            for (LabelledTextField l : data) {
                if(valueColumn.getCellData(l)!=null && valueColumn.getCellData(l).length()>0) {
                    if (valueColumn.getCellData(l).equals("null")){
                        sb.append(" AND "+columnNameColumn.getCellData(l)+" IS NULL,");
                    }
                    else if (valueColumn.getCellData(l).equals("!null")){
                        sb.append(" AND "+columnNameColumn.getCellData(l)+" IS NOT NULL,");
                    }
                    else sb.append(" AND "+columnNameColumn.getCellData(l)+"=\'"+ valueColumn.getCellData(l) + "\',");
                }
            }
            if (sb.length()>11) {
                sb.deleteCharAt(sb.length() - 1);
                parentController.displayTable(parentController.currentTable, sb.toString());
            }
            currentStage.close();
    }

    @Override
    public TableView getTable(){
        return null;
    }
}