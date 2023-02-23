package maps.AddMapsForm;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import maps.Constants;
import maps.Connector;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

/**
 * Клас форми додавання карт
 */
public class AddMapsForm implements Initializable {

    public TextField mapNameTextBox;
    public TextField mapAuthorTextBox;
    public Button addMapButton;
    public ChoiceBox<String> mapComboBox;
    public Button mapImageButton;
    public TextField mapLinkTextBox;

    public String pathToImage;

    public static Stage currentStage;

    /**
     * Метод додавання карти в базу даних
     */
    public void addMapButtonClick() {
        if (mapNameTextBox.getText().equals("") || mapLinkTextBox.getText().equals("") || mapAuthorTextBox.getText().equals("") || mapComboBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Помилка");
            alert.setContentText("Ви не ввели всі необхідні дані");
            alert.show();
        }
        else {
            try {
                PreparedStatement preparedStatement = Connector.connection.prepareStatement("INSERT INTO maps (name, link, imagePath, category, author, createdByUser) VALUES (?,?,?,?,?,?)");
                preparedStatement.setString(1, mapNameTextBox.getText());
                preparedStatement.setString(2, mapLinkTextBox.getText());
                if (pathToImage != null)
                    preparedStatement.setString(3, "images/" + mapNameTextBox.getText() + ".png");
                else
                    preparedStatement.setString(3, "");
                preparedStatement.setString(4, mapComboBox.getValue());
                preparedStatement.setString(5, mapAuthorTextBox.getText());
                preparedStatement.setString(6, Constants.username);
                preparedStatement.executeUpdate();
                String pathToFinalImage = "images/" + mapNameTextBox.getText() + ".png";
                Path path = new File(pathToFinalImage).toPath();
                if (pathToImage != null)
                    Files.copy(Paths.get(pathToImage), path, StandardCopyOption.REPLACE_EXISTING);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Успіх");
                alert.setContentText("Карту успішно додано");
                alert.showAndWait();
                currentStage.close();
            }
            catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Помилка");
                alert.setContentText("Перевірте введені дані\nКарта з таким іменем, скоріше за все, вже існує");
                alert.show();
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод вибору зображення
     */
    public void pickImageClick() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(currentStage);
        if (file != null) {
            pathToImage = file.getAbsolutePath();
            mapImageButton.setText(pathToImage.substring(pathToImage.lastIndexOf("\\") + 1));
        }
    }

    /**
     * Метод ініціалізації форми
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            PreparedStatement statement = Connector.connection.prepareStatement("SELECT * FROM categories");
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                mapComboBox.getItems().add(set.getString("categoryName"));
            }
        }
        catch (Exception ignored) { }
    }
}
