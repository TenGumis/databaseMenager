import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LabelledTextField {
    StringProperty name;
    StringProperty value;
    LabelledTextField(String name) {
        this.name = new SimpleStringProperty(name);
        value = null;
    }
    LabelledTextField(String name,String value) {
        this.name = new SimpleStringProperty(name);
        this.value= new SimpleStringProperty(value);
    }
}
