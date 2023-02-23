package maps.liked;

import javafx.scene.control.Button;
import maps.Connector;
import maps.Constants;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Класс опції вподобаного
 */
public class liked {
    public static String mapName = null;
    static boolean isLikedF = false;

    public static void likedMap() throws SQLException {
        isLikedF=isLiked();
        PreparedStatement prepareStatement;
        if(!isLikedF){
            prepareStatement = Connector.connection.prepareStatement("INSERT INTO liked (user, map) values (?,?);");
        }
       else {
            prepareStatement = Connector.connection.prepareStatement("DELETE FROM liked WHERE user = ? AND map = ?;");
        }
        prepareStatement.setString(1, Constants.username);
        prepareStatement.setString(2, mapName);
        var set = prepareStatement.executeUpdate();
    }

    public static boolean isLiked() throws SQLException {
        PreparedStatement prepareStatement = Connector.connection.prepareStatement("SELECT COUNT(1) FROM liked WHERE user = ? AND map = ?;");
        prepareStatement.setString(1, Constants.username);
        prepareStatement.setString(2, mapName);
        var set = prepareStatement.executeQuery();
       // while (set.next()) {
            if (set.getInt(1) == 1) {
                isLikedF = true;
                return true;
            } else {
                isLikedF = false;
                return false;
            }
       // }
       // return isLikedF;
    }
    /*public static boolean isButtonLiked(Button btn, ) throws SQLException {
        PreparedStatement prepareStatement = Connector.connection.prepareStatement("SELECT COUNT(1) FROM liked WHERE user = ? AND map = ?;");
        prepareStatement.setString(1, Constants.username);
        prepareStatement.setString(2, mapName);
        var set = prepareStatement.executeQuery();
        if (set.getInt(1) == 1) {
            isLikedF = true;
            return true;
        } else {
            isLikedF = false;
            return false;
        }
    }*/
}

