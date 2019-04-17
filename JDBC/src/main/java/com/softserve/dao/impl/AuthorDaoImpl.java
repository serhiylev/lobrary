package com.softserve.dao.impl;

import com.softserve.dao.AuthorDao;
import com.softserve.entity.Author;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class AuthorDaoImpl implements AuthorDao {
 public static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/library?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&user=root&password=admin";
    Connection connection;

    public void getConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (connection == null)
                connection = DriverManager.getConnection(CONNECTION_STRING);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createAuthor(Author author) {
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO authors (ID,FIRSTNAME,LASTNAME,AGE) VALUES (NULL,?,?,?)");
            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());
            preparedStatement.setInt(3, author.getAge());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("Element added");
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Connection error!");
            }
        }
    }

    @Override
    public List<Author> retrieveAllAuthors() {
        List<Author> authors = new LinkedList<>();
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM authors");

            Author author;
            while (resultSet.next()) {
                author = new Author();
                author.setId(resultSet.getInt("ID"));
                author.setFirstName(resultSet.getString("FIRSTNAME"));
                author.setLastName(resultSet.getString("LASTNAME"));
                author.setAge(resultSet.getInt("AGE"));
                authors.add(author);
            }
            resultSet.close();
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Connection error!");
            }
        }

        return authors;
    }

    @Override
    public Author retrieveAuthor(int id) {
        List<Author> authors = retrieveAllAuthors();
        Author author = null;
        for (Author b : authors) {
            if (b.getId() == id) {
                author = b;
                break;
            }
        }
        return author;
    }

    @Override
    public void updateAuthor(Author author) {
        String sql = "update authors set FIRSTNAME = ?, LASTNAME = ?, AGE = ? where ID = ?";
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());
            preparedStatement.setInt(3, author.getAge());
            preparedStatement.setInt(4, author.getId());
            preparedStatement.executeUpdate();
            System.out.println("Database updated successfully");
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Connection error!");
            }
        }
    }

    @Override
    public void deleteAuthor(int id) {
        String sql1 = "delete book from book join list_of_author on book.ID = list_of_author.book_id where id_author = ?;";
        String sql2 = "delete orders from orders right join BOOK on BOOK.ID = orders.ID_BOOK where BOOK.ID is null;";
        String sql3 = "delete list_of_author from list_of_author where id_author = ?;";
        String sql4 = "delete authors from authors where ID = ?;";
        try {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(sql2);
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql3);
            preparedStatement2.setInt(1, id);
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            preparedStatement1.setInt(1, id);
            PreparedStatement preparedStatement3 = connection.prepareStatement(sql4);
            preparedStatement3.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement1.executeUpdate();
            preparedStatement2.executeUpdate();
            preparedStatement3.executeUpdate();
            System.out.println("Record deleted successfully");
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Connection error!");
            }
            e.printStackTrace();
        }
    }
}
