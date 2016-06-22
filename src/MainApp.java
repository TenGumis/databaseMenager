import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Optional;

public class MainApp extends Application {

    private static Connection conn;
    private static Statement statement;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        VBox singInLayout=new VBox();

        Label info=new Label("Podaj dane logowania do bazy:");

        TextField login =new TextField();
        login.setMaxWidth(200);
        login.setPromptText("login");

        TextField dbName =new TextField();
        dbName.setMaxWidth(200);
        dbName.setPromptText("nazwa bazy danych");

        PasswordField password=new PasswordField();
        password.setMaxWidth(200);
        password.setPromptText("hasło");

        Button okButton=new Button("OK");
        okButton.setMaxWidth(80);
        okButton.setOnAction(e -> {
            try
            {
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbName.getText().toString(), login.getText().toString(), password.getText().toString());
                statement = conn.createStatement();
            }
            catch(Exception ex)
            {
                statement=null;
                //ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setResizable(false);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setTitle("Błąd");
                alert.setHeaderText(null);
                alert.setContentText("Nie udało się poprawnie zalogować do bazy. Spróbuj ponownie.");
                Optional<ButtonType> result = alert.showAndWait();
            }
            if(statement!=null) {
                init(primaryStage);
            }
        });


        singInLayout.setAlignment(Pos.CENTER);
        singInLayout.setSpacing(20);
        singInLayout.getChildren().addAll(info,login,dbName,password,okButton);


        Scene singIn=new Scene(singInLayout,300,300);
        primaryStage.setScene(singIn);
        primaryStage.show();
    }

    void init(Stage primaryStage){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("FXMLs/MainScreen.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            ControlledScreen controller = loader.getController();
            controller.init(primaryStage, statement, null);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
