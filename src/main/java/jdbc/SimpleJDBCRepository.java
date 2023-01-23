package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private static final String createUserSQL = "INSERT INTO users (firstName, lastName, age) VALUES (?, ?, ?);";
    private static final String updateUserSQL = "UPDATE users SET firstname = ?, lastname = ?, age= ? WHERE id=?;";
    private static final String deleteUser = "DELETE FROM users WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM users WHERE id = ?;";
    private static final String findUserByNameSQL = "SELECT * FROM users WHERE firstName =?;";
    private static final String findAllUserSQL = "SELECT * FROM users;";
    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    public Long createUser(User user) throws SQLException, IOException {
        Long id = 0L;
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, user.getFirstName());
        ps.setString(2, user.getLastName());
        ps.setInt(3, user.getAge());
        int affectedRows = ps.executeUpdate();

        if (affectedRows > 0) {
            ResultSet resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getLong(1);
            }
        }
        return id;
    }

    public User findUserById(Long userId) throws SQLException, IOException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(findUserByIdSQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ps.setLong(1, userId);
        ResultSet resultSet = ps.executeQuery();
        String firstName = null;
        String lastName = null;
        int age = 0;
        while (resultSet.next()) {
            firstName = resultSet.getString(2);
            lastName = resultSet.getString(3);
            age = resultSet.getInt(4);
        }
        connection.close();
        resultSet.close();
        return new User(userId, firstName, lastName, age);
    }

    public User findUserByName(String userName) throws SQLException, IOException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(findUserByNameSQL);
        ps.setString(1, userName);
        ResultSet resultSet = ps.executeQuery();
        long userId = 0L;
        String lastName = null;
        int age = 0;
        while (resultSet.next()) {
            userId = resultSet.getLong(1);
            lastName = resultSet.getString(3);
            age = resultSet.getInt(4);
        }
        connection.close();
        resultSet.close();
        return new User(userId, userName, lastName, age);
    }

    public List<User> findAllUser() throws SQLException, IOException {
        List<User> users = new ArrayList<>();
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(findAllUserSQL);
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            User user = new User(resultSet.getLong("id"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getInt("age"));
            users.add(user);
        }
        connection.close();
        resultSet.close();
        return users;
    }

    public User updateUser(User user) throws SQLException, IOException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(updateUserSQL);
        ps.setLong(4, user.getId());
        ps.setString(1, user.getFirstName());
        ps.setString(2, user.getLastName());
        ps.setInt(3, user.getAge());
        ps.executeUpdate();

        User updatedUser = findUserById(user.getId());
        connection.close();
        return updatedUser;
    }

    public void deleteUser(Long userId) throws SQLException, IOException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(deleteUser);
        ps.setLong(1, userId);
        ps.executeUpdate();
        connection.close();
    }
}
