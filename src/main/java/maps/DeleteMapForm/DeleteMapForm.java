package maps.DeleteMapForm;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import maps.Constants;
import maps.Connector;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

/**
 * Клас форми видалення карт
 */
public class DeleteMapForm implements Initializable {

    public ChoiceBox<String> mapBox;
    public static Stage currentStage;

    /**
     * Метод видалення карт
     */
    public void deleteButtonClick() {
        try {
            if (mapBox.getValue() != null) {
                String imagePath = null;
                PreparedStatement statementForImage = Connector.connection.prepareStatement("SELECT * FROM maps WHERE name = ?");
                statementForImage.setString(1, mapBox.getValue());
                ResultSet set = statementForImage.executeQuery();
                while (set.next()) {
                    imagePath = set.getString("imagePath");
                }


                PreparedStatement statement = Connector.connection.prepareStatement("DELETE FROM maps WHERE name = ?");
                statement.setString(1, mapBox.getValue());
                statement.executeUpdate();
                if (!imagePath.equals(""))
                    Files.delete(new File(imagePath).toPath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Успіх");
                alert.setContentText("Карту успішно видалено");
                alert.showAndWait();
                currentStage.close();
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Метод ініціалізації форми
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateMaps();
    }

    /**
     * Метод оновлення карти
     */
    public void updateMaps() {
        mapBox.getItems().clear();
        try {
            PreparedStatement statement = Connector.connection.prepareStatement("SELECT * FROM maps WHERE createdByUser = ?");
            statement.setString(1, Constants.username);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                mapBox.getItems().add(set.getString("name"));
            }
        }
        catch (Exception ignored) { }
    }
}
