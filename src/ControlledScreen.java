import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.Statement;

public interface ControlledScreen {
    void init(Stage s, Statement statement, ControlledScreen cS);
    TableView getTable();
}
