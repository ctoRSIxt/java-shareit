package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {

    /**
     * Return list of all users
     */
    List<User> findAll();

    /**
     * Return user by id
     */
    User findUserById(long id);

    /**
     * Create a new user
     */
    User create(User user);

    /**
     * Update an existing user
     */
    User update(User user);

    /**
     * Delete an existing user
     */
    User deleteById(long id);

    /**
     * Find users by email
     */
    List<User> findUsersByEmail(String email);
}
