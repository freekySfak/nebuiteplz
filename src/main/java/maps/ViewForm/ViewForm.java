package maps.ViewForm;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import maps.liked.liked;
import maps.Connector;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Клас форми відображення карти
 */
public class ViewForm implements Initializable {

    public Label nameLabel;
    public Label categoryLabel;
    public Label authorLabel;
    public Label mapLinkLabel;
    public ImageView imageBox;
    public VBox vBox;
    public Label mapLinkLabelUrl;
    public static String name;
    public static Stage currentStage;
    public Button likedButton;

    /**
     * Метод додавання карти до вподобаного
     */
    public void likeMap() throws SQLException {
        if (liked.isLiked()){
            likedButton.setStyle("-fx-text-fill: Gray; -fx-background-color: #FF99FF; -fx-cursor: hand;");
        }
        else {

            likedButton.setStyle("-fx-text-fill: Yellow; -fx-background-color: #FF99FF; -fx-cursor: hand;");
        }
        liked.likedMap();
    }

    public void checkIsLiked() throws SQLException {
        if (liked.isLiked()){
            likedButton.setStyle("-fx-text-fill: Yellow; -fx-background-color: #FF99FF; -fx-cursor: hand;");
        }
        else {
            likedButton.setStyle("-fx-text-fill: Gray; -fx-background-color: #FF99FF; -fx-cursor: hand;");
        }
    }
    /**
     * Метод ініціалізації форми та підвантаження даних на форму
     */
    public void openInBrowser() throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(new URL(mapLinkLabelUrl.getText()).toURI());
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            checkIsLiked();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            imageBox.fitWidthProperty().bind(vBox.widthProperty());
            imageBox.fitHeightProperty().bind(vBox.heightProperty());
        });
        try {
            PreparedStatement statement = Connector.connection.prepareStatement("SELECT * FROM maps WHERE name = ?");
            statement.setString(1, name);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                nameLabel.setText(nameLabel.getText() + set.getString("name"));
                categoryLabel.setText(categoryLabel.getText() + set.getString("category"));
                mapLinkLabelUrl.setText(set.getString("link"));
                authorLabel.setText(authorLabel.getText() + set.getString("author"));
                imageBox.setImage(new Image(new File(set.getString("imagePath")).toURI().toString()));
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }
}
