package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("dbUserStorage")
@RequiredArgsConstructor
public class DBUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    String SELECT_BY_ID = "SELECT * FROM \"user\" WHERE ID = ?";

    String SELECT_BY_IDS = "SELECT * FROM \"user\" WHERE ID IN (?)";

    String SELECT_LIST = "SELECT * FROM \"user\"";

    String INSERT = "INSERT INTO \"user\" (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";

    String UPDATE = "UPDATE \"user\" SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE ID = ?";

    String ADD_FRIEND = "INSERT INTO \"friend\" (USER_ID_1, USER_ID_2) VALUES (?, ?)";

    String GET_FRIENDS = "SELECT * FROM \"friend\" WHERE USER_ID_1 = ?";

    String REMOVE_FRIEND = "DELETE FROM \"friend\" WHERE USER_ID_1 = ? AND USER_ID_2 = ?";

    @Override
    public void addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT, new String[]{"ID"});

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));

            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();

        user.setId(id);
    }

    @Override
    public void updateUser(User user) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(UPDATE);

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            ps.setLong(5, user.getId());

            return ps;
        });
    }

    @Override
    public User getUser(Long userId) {
        List<User> result = jdbcTemplate.query(SELECT_BY_ID, this::mapToUser, userId);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    @Override
    public List<User> getUsers() {
        List<User> result = jdbcTemplate.query(SELECT_LIST, this::mapToUser);

        if (result.isEmpty()) {
            return null;
        }

        return result;
    }

    @Override
    public void addFriend(User user, User friend) {
        final List<Friend> result = jdbcTemplate.query(GET_FRIENDS, this::mapToFriend, user.getId());

        if (!result.isEmpty()) {
            if (result.get(0).getUserId1() == user.getId()) {
                return;
            }
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_FRIEND);

            ps.setLong(1, user.getId());
            ps.setLong(2, friend.getId());

            return ps;
        });
    }

    @Override
    public void deleteFriend(User user, User friend) {
        jdbcTemplate.update(REMOVE_FRIEND, user.getId(), friend.getId());
    }

    @Override
    public ArrayList<User> getFriends(User user) {
        List<Friend> result = jdbcTemplate.query(GET_FRIENDS, this::mapToFriend, user.getId());

        if (result.isEmpty()) {
            return new ArrayList();
        }

        List<String> ids = new ArrayList<>();

        for (Friend friend : result) {
            ids.add(friend.getUserId2().toString());
        }

        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        ArrayList<User> users = (ArrayList<User>) jdbcTemplate.query(
                String.format("SELECT * FROM \"user\" WHERE ID IN (%s)", inSql),
                this::mapToUser,
                ids.toArray()
        );

        return users;
    }

    @Override
    public ArrayList<User> getFriendsOfFriends(User user, User friend) {
        List<Friend> friendsList1 = jdbcTemplate.query(GET_FRIENDS, this::mapToFriend, user.getId());
        List<Friend> friendsList2 = jdbcTemplate.query(GET_FRIENDS, this::mapToFriend, friend.getId());

        ArrayList<Friend> friendsList = new ArrayList<>();

        for (Friend friendItem : friendsList1) {
            long id = friendItem.getUserId2();
            Friend containFriend = Friend.builder()
                    .userId1(friend.getId())
                    .userId2(id)
                    .build();

            if (friendsList2.contains(containFriend)) {
                friendsList.add(friendItem);
            }
        }

        String friendsIds = "";

        for (Friend item : friendsList) {
            friendsIds += item.getUserId2() + ",";
        }

        friendsIds = friendsIds.substring(0, friendsIds.length() - 1);

        ArrayList<User> users = (ArrayList<User>) jdbcTemplate.query(SELECT_BY_IDS, this::mapToUser, friendsIds);

        return users;
    }

    private User mapToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
    }

    private Friend mapToFriend(ResultSet resultSet, int rowNum) throws SQLException {
        return Friend.builder()
                .userId1(resultSet.getLong("USER_ID_1"))
                .userId2(resultSet.getLong("USER_ID_2"))
                .build();
    }
}
