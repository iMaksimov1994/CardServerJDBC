package repository;

import model.Category;
import utill.Constants;

import java.sql.*;
import java.util.ArrayList;

public class CategoryRepository implements AutoCloseable {
    private Connection connection;

    public CategoryRepository() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(Constants.DB_URL, Constants.USERNAME, Constants.PASSWORD);
        } catch (Exception ignored) {
        }
    }

    public boolean addCategory(Category category) {
        String sql = "insert into category(name, id_u) VALUES (?,?)";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setInt(2, category.getIdUser());
            int row = preparedStatement.executeUpdate();
            if (row <= 0) {
                return false;
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException ignored) {
        }
        return false;
    }

    public ArrayList<Category> getCategories(int id_u) {
        ArrayList<Category> categories = new ArrayList<>();
        String sql = "select * from category where category.id_u = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id_u);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt(1));
                category.setName(resultSet.getString(2));
                category.setIdUser(resultSet.getInt(3));
                categories.add(category);
            }
        } catch (SQLException ignored) {
        }
        return categories;
    }

    public Category getCategory(int id) {
        String sql = "select * from category where category.id= ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            Category category = new Category();
            category.setId(resultSet.getInt(1));
            category.setName(resultSet.getString(2));
            category.setIdUser(resultSet.getInt(3));
            return category;
        } catch (SQLException ignored) {
        }
        return null;
    }

    public boolean deleteCategory(int id) {
        String sql = "delete from category where category.id=?";
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

    public boolean updateCategory(Category category) {
        String sql = "update category set category.name=?, category.id_u=? where category.id=?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setInt(2, category.getIdUser());
            preparedStatement.setInt(3, category.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException ignored) {
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
