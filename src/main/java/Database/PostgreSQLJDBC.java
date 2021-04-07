package Database;

import Database.Database;

import java.sql.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLJDBC {

    public static boolean insert(String token, String name, String city){

        Connection c = Database.getConection();
        try {
            Statement stmt = c.createStatement();
            String sql = "INSERT INTO telegramUsers (token, name, city) "
                    + "VALUES ('" + token +"','"+ name +"','"+ city +"');";

            System.out.println(sql);
            stmt.executeUpdate(sql);
            stmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public static List<String> find(String token){
        Connection c = Database.getConection();
        Statement stmt = null;
        List<String> user = new ArrayList<>();

        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM telegramUsers WHERE token LIKE '"+token+"';" );

            if (rs.next()) {
                String id = rs.getString("token");
                String name = rs.getString("name");
                String address = rs.getString("city");
                user.add(id);
                user.add(name);
                user.add(address);
                return user;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static boolean updateCity(String token, String city){
        Connection c = Database.getConection();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "UPDATE telegramUsers " +
                    "set city = '"+ city + "'"+
                    "where token LIKE '"+token+"';";
            stmt.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static boolean updateName(String token, String name){
        Connection c = Database.getConection();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "UPDATE telegramUsers " +
                    "set name = '"+ name + "'"+
                    "where token LIKE '"+token+"';";

            System.out.println(sql);
            stmt.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public static int getState(String token){ // get user state
        Connection c = Database.getConection();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM telegramUsers WHERE token LIKE '"+token+"';" );
            if (rs.next()) {
                return rs.getInt("state");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }

    public static Boolean getNotifications(String token){ // daily messages are enabled or disabled
        Connection c = Database.getConection();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM telegramUsers WHERE token LIKE '"+token+"';" );
            if (rs.next()) {
                return rs.getBoolean("notifications");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static boolean updatenotifications(String token){
        Connection c = Database.getConection();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "UPDATE telegramUsers " +
                    "set notifications = "
                    + (getNotifications(token)?"FALSE":"TRUE") +
                    " where token LIKE '"+token+"';";

            System.out.println(sql);
            stmt.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean setState(String token, int state){
        Connection c = Database.getConection();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "UPDATE telegramUsers " +
                    "set STATE = "+ state +
                    "where token LIKE '"+token+"';";
            stmt.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

// DATE

    public static boolean insertDate(String date, String text, String token){
        Connection c = Database.getConection();
        try {
            Statement stmt = c.createStatement();
            String sql = "INSERT INTO userstask (date, text, token) "
                    + "VALUES ('" + date +"','"+ text +"','"+ token +"');";

            System.out.println(sql);
            stmt.executeUpdate(sql);
            stmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public static List<List<String>> findDate(String date){
        Connection c = Database.getConection();
        Statement stmt = null;
        List<List<String>> allTasks = new ArrayList<>();
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM userstask WHERE date LIKE '"+date+"';" );
            while (rs.next()) {
                List<String> task = new ArrayList<>();
                String token = rs.getString("token");
                String text = rs.getString("text");
                String date_task = rs.getString("date");
                Integer id = rs.getInt("id");
                task.add(token);
                task.add(text);
                task.add(date_task);
                task.add(id.toString());
                allTasks.add(task);
            }
            return allTasks;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static List<List<String>> findTasks(String token){
        Connection c = Database.getConection();
        Statement stmt = null;
        List<List<String>> allTasks = new ArrayList<>();
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM userstask WHERE token LIKE '"+token+"';" );
            while (rs.next()) {
                List<String> task = new ArrayList<>();
                String token_token = rs.getString("token");
                String text = rs.getString("text");
                String date= rs.getString("date");
                Integer id = rs.getInt("id");
                task.add(token_token);
                task.add(text);
                task.add(date);
                task.add(id.toString());
                allTasks.add(task);
            }
            return allTasks;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void deleteTask(String id){
        Connection c = Database.getConection();
        try {
            Statement stmt = c.createStatement();
            String sql = "DELETE from userstask where ID = "+ id +";";
            stmt.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static List<String> getNotificationsChatID(){
        Connection c = Database.getConection();
        Statement stmt = null;
        List<String> users = new ArrayList<>();
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM telegramUsers WHERE notifications = TRUE;" );
            while (rs.next()) {
                users.add(rs.getString("token"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return users;
    }

}
