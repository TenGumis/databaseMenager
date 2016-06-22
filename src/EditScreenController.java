import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class EditScreenController implements ControlledScreen{

    Stage currentStage;
    Statement statement;
    String currentTable;
    MainScreenController parentController;
    ObservableList<LabelledTextField> data;
    ObservableList originalData;

    @Override
    public void init(Stage s, Statement statement,ControlledScreen parentController) {
        currentStage = s;
        this.statement = statement;
        this.parentController=(MainScreenController) parentController;
        currentTable = MainScreenController.currentTable;
        data = FXCollections.observableArrayList();
        dataTable.setEditable(true);
        columnNameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().value);
        valueColumn.setEditable(true);
        valueColumn.setOnEditCommit(t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).value = new SimpleStringProperty(t.getNewValue()));
        try {
            ResultSet rs = statement.executeQuery("select column_name from information_schema.columns where table_name = '" + currentTable + "';");
            originalData = (ObservableList) parentController.getTable().getSelectionModel().getSelectedItem();
            int i=0;
            while(rs.next()) {
                data.add( new LabelledTextField(rs.getString(1),originalData.get(i).toString()) );
                i++;
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
    void attemptUpdate(MouseEvent event) {
        try {
            StringBuilder sb=new StringBuilder();
            StringBuilder sb2=new StringBuilder();
            sb2.append(" WHERE true ");
            int i=0;
            for (LabelledTextField l : data) {
                if(valueColumn.getCellData(l)!=null && valueColumn.getCellData(l).length()>0) {
                    sb.append(columnNameColumn.getCellData(l)+"=\'"+valueColumn.getCellData(l) + "\',");
                }
                if(originalData.get(i)!=null){
                    sb2.append(" AND "+columnNameColumn.getCellData(l)+"=\'"+originalData.get(i)+"\'");
                }
                i++;
            }
            if(sb.length()>0) sb.deleteCharAt(sb.length()-1);
            //System.out.println("UPDATE "+currentTable+" SET "+sb.toString()+" "+sb2.toString()+";");
            statement.executeUpdate("UPDATE "+currentTable+" SET "+sb.toString()+" "+sb2.toString()+";");
            System.out.println("Updated successfully");
            parentController.displayTable(parentController.currentTable,"");
            currentStage.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setResizable(false);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Niepoprawne dane.");
            alert.setHeaderText(null);
            alert.setContentText("Dane nie mogły być edytowane. Spróbuj ponownie.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    @Override
    public TableView getTable(){
        return null;
    }
}
