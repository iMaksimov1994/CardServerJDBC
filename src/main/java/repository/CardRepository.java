package repository;


import model.Card;
import utill.Constants;

import java.sql.*;
import java.util.ArrayList;

public class CardRepository implements AutoCloseable {
    private Connection connection;

    public CardRepository() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(Constants.DB_URL, Constants.USERNAME, Constants.PASSWORD);
        } catch (Exception ignored) {
        }
    }

    public boolean addCard(Card card) {
        String sql = "insert into card(question, answer,id_c,creationDate) VALUES (?,?,?,?)";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, card.getQuestion());
            preparedStatement.setString(2, card.getAnswer());
            preparedStatement.setInt(3, card.getIdC());
            preparedStatement.setTimestamp(4, new Timestamp(card.getCreationDate().getTime()));
            int row = preparedStatement.executeUpdate();
            if (row <= 0) {
                return false;
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    card.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException ignored) {
        }
        return false;
    }

    public ArrayList<Card> getCards(int id_c) {
        ArrayList<Card> cards = new ArrayList<>();
        String sql = "select * from card where card.id_c = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id_c);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Card card = new Card();
                card.setId(resultSet.getInt(1));
                card.setQuestion(resultSet.getString(2));
                card.setAnswer(resultSet.getString(3));
                card.setIdC(resultSet.getInt(4));
                card.setCreationDate(resultSet.getTimestamp(5));
                cards.add(card);
            }

        } catch (SQLException ignored) {
        }
        return cards;
    }

    public ArrayList<Card> getCardByUser(int id_u) {
        ArrayList<Card> cards = new ArrayList<>();
        String sql = "select * from card, category, user where user.id = category.id_u and card.id_c = category.id ";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id_u);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Card card = new Card();
                card.setId(resultSet.getInt(1));
                card.setQuestion(resultSet.getString(2));
                card.setAnswer(resultSet.getString(3));
                card.setIdC(resultSet.getInt(4));
                card.setCreationDate(resultSet.getTimestamp(5));
                cards.add(card);
            }
        } catch (SQLException ignored) {
        }
        return cards;
    }

    public Card getCard(int id) {
        String sql = "select * from card where card.id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            Card card = new Card();
            card.setId(resultSet.getInt(1));
            card.setQuestion(resultSet.getString(2));
            card.setAnswer(resultSet.getString(3));
            card.setIdC(resultSet.getInt(4));
            card.setCreationDate(resultSet.getTimestamp(5));
            return card;
        } catch (SQLException ignored) {
        }
        return null;
    }

    public boolean deleteCard(int id) {
        String sql = "delete from card where card.id=?";
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

    public boolean updateCard(Card card) {
        String sql = "update card set card.question=?, card.answer=?,card.id_c=?,card.creationDate=? where card.id=?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setString(1, card.getQuestion());
            preparedStatement.setString(2, card.getAnswer());
            preparedStatement.setInt(3, card.getIdC());
            preparedStatement.setTimestamp(4, new Timestamp(card.getCreationDate().getTime()));
            preparedStatement.setInt(5, card.getId());
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
