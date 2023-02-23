package maps.MainForm;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import maps.AddMapsForm.AddMapsForm;
import maps.liked.liked;
import maps.AuthForm.AuthForm;
import maps.Constants;
import maps.DeleteMapForm.DeleteMapForm;
import maps.RegisterForm.RegisterForm;
import maps.ViewForm.ViewForm;
import maps.utils.FormUtils;
import maps.Connector;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Клас головної форми
 */
public class MainForm implements Initializable {

    public static Stage currentStage;
    public MenuItem authButton;
    public MenuItem regButton;
    public MenuItem logoffButton;
    public Menu editButton;
    public VBox vBox;
    public ChoiceBox<String> categoryBox;
    public Button updateButton;

    /**
     * Метод відкриття форми авторизації
     */
    public void authButtonClick() {
        AuthForm.currentStage = FormUtils.createWindow("/fxml/AuthForm.fxml", "Авторизація", currentStage);
        AuthForm.currentStage.setResizable(false);
        AuthForm.currentStage.showAndWait();
        if (!Constants.username.equals("")) {
            editButton.setVisible(true);
        }
    }

    /**
     * Метод відкриття форми реєстрації
     */
    public void regButtonClick() {
        RegisterForm.currentStage = FormUtils.createWindow("/fxml/RegisterForm.fxml", "Реєстрація", currentStage);
        RegisterForm.currentStage.setResizable(false);
        RegisterForm.currentStage.showAndWait();
    }

    /**
     * Метод виходу з акаунту
     */
    public void logoffButtonClick() {
        Constants.username = "";
        editButton.setVisible(false);
    }

    /**
     * Метод відкриття форми додавання карти
     */
    public void addMapClick() {
        AddMapsForm.currentStage = FormUtils.createWindow("/fxml/AddMapForm.fxml", "Додати карту", currentStage);
        AddMapsForm.currentStage.setMinWidth(800);
        AddMapsForm.currentStage.setMinHeight(600);
        AddMapsForm.currentStage.showAndWait();
        updateButtonClick();
    }

    /**
     * Метод відкриття форми видалення карти
     */
    public void deleteMapClick() {
        DeleteMapForm.currentStage = FormUtils.createWindow("/fxml/DeleteMapForm.fxml", "Видалити карту", currentStage);
        DeleteMapForm.currentStage.setResizable(false);
        DeleteMapForm.currentStage.showAndWait();
        updateButtonClick();
    }

    /**
     * Метод відкриття форми "Про програму"
     */
    public void aboutProgramClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Про програму");
        alert.setHeaderText("Довідник по картам OSU!");
        alert.show();
    }

    /**
     * Метод відкриття форми "Про автора"
     */
    public void aboutAuthorClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Про автора");
        alert.setHeaderText("Виконав студент групи КН-31 Мікла Владислав");
        alert.show();
    }

    /**
     * Метод оновлення кнопок
     */
    public void updateButtonClick() {
        vBox.getChildren().clear();
        try {
            PreparedStatement statement;
            if (categoryBox.getValue().equals("Всі")) {
                statement = Connector.connection.prepareStatement("SELECT * FROM maps");
            }
            else if (categoryBox.getValue().equals("Вподобані")) {
                statement = Connector.connection.prepareStatement("SELECT map FROM liked WHERE user = ?");
                statement.setString(1, Constants.username);
                System.out.println(Constants.username);

                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    System.out.println(set.getString("map"));
                    Button button = new Button(set.getString("map"));
                    button.setStyle("-fx-background-color: #FF99FF; -fx-background-radius: 20;");
                    button.setCursor(Cursor.HAND);
                    button.setMaxWidth(Double.MAX_VALUE);
                    button.setOnAction(this::mapButtonClick);
                    vBox.getChildren().add(button);
                }
                return;
            }
            else {
                statement = Connector.connection.prepareStatement("SELECT * FROM maps WHERE category = ?");
                statement.setString(1, categoryBox.getValue());
            }
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                Button button = new Button(set.getString("name"));
                button.setStyle("-fx-background-color: #FF99FF; -fx-background-radius: 20;");
                button.setCursor(Cursor.HAND);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setOnAction(this::mapButtonClick);
                vBox.getChildren().add(button);
            }
        }
        catch (Exception ignored) { }
    }

    /**
     * Метод оновлення карт
     */
    public void mapButtonClick(ActionEvent event) {
        Button b = (Button) event.getSource();
        ViewForm.name = b.getText();
        liked.mapName=b.getText();
        ViewForm.currentStage = FormUtils.createWindow("/fxml/ViewForm.fxml", b.getText(), currentStage);
        ViewForm.currentStage.show();
        ViewForm.currentStage.setMinHeight(600);
        ViewForm.currentStage.setMinWidth(800);
    }

    /**
     * Метод додавання категорій
     */
    public void addCategories() {
        categoryBox.getItems().add("Всі");
        categoryBox.getItems().add("Вподобані");
        try {
            PreparedStatement statement = Connector.connection.prepareStatement("SELECT * FROM categories");
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                categoryBox.getItems().add(set.getString("categoryName"));
            }
        }
        catch (Exception ignored) { }
    }

    public void showLiked(){
        try {
            PreparedStatement statement = Connector.connection.prepareStatement("SELECT map FROM liked WHERE user = '123'");
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                categoryBox.getItems().add(set.getString("categoryName"));
            }
        }
        catch (Exception ignored) { }
    }

    /**
     * Метод оновлення карти при зміні категорії
     */
    public void onValueChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        updateButtonClick();
    }

    /**
     * Метод ініціалізації форми
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addCategories();
        Platform.runLater(() -> {
            categoryBox.valueProperty().addListener(this::onValueChanged);
            categoryBox.setValue("Всі");
        });
        updateButtonClick();
    }
}
