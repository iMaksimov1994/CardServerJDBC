package repository;

import model.User;

import utill.Constants;

import java.sql.*;
import java.util.ArrayList;

public class UserRepository implements AutoCloseable {
    private Connection connection;

    public UserRepository() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(Constants.DB_URL,
                    Constants.USERNAME, Constants.PASSWORD);
        } catch (Exception ignored) {
        }
    }

    public boolean addUser(User user) {
        String sql = "insert into user(login, password, name, regDate) VALUES (?,?,?,?)";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setTimestamp(4, new Timestamp(user.getRegDate().getTime()));
            int row = preparedStatement.executeUpdate();
            if (row <= 0) {
                return false;
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException ignored) {
        }
        return false;
    }

    public ArrayList<User> getUsers() {
        String sql = "select * from user";
        ArrayList<User> users = new ArrayList<>();
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt(1));
                user.setLogin(resultSet.getString(2));
                user.setPassword(resultSet.getString(3));
                user.setName(resultSet.getString(4));
                user.setRegDate(resultSet.getTimestamp(5));
                users.add(user);
            }
        } catch (SQLException ignored) {
        }
        return users;
    }

    public User getUser(int id) {
        String sql = "select * from user where user.id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            User user = new User();
            user.setId(resultSet.getInt(1));
            user.setLogin(resultSet.getString(2));
            user.setPassword(resultSet.getString(3));
            user.setName(resultSet.getString(4));
            user.setRegDate(resultSet.getTimestamp(5));
            return user;
        } catch (SQLException ignored) {
        }
        return null;
    }

    public int checkUser(String login, String password) {
        String sql = "select * from user where user.login = ? and user.password=?";
        try {
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) {
                    return 0;
                }
                return resultSet.getInt(1);
            }
        } catch (SQLException ignored) {
        }
        return 0;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE from user where user.id=?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            if (preparedStatement.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() {
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (SQLException ignored) {
        }
    }
}
