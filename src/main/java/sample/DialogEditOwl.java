
package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class DialogEditOwl {
    public static Stage window = null;
    public static Scene scene;
    public static Owl currentOwl = null;

    public static void display(Owl owl) throws IOException {
        currentOwl = owl;
        Parent root = FXMLLoader.load(Main.class.getResource("editowl.fxml"));
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Редагувати сову: " + owl.name);
        scene = new Scene(root, 500, 450);
        window.setScene(scene);
        window.showAndWait();
    }
}