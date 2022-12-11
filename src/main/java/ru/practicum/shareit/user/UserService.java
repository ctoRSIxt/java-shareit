package ru.practicum.shareit.user;


import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    // Create user
    UserDto create(UserDto userDto);

    // Update user
    UserDto update(UserDto userDto);

    // Patch part of user with userId
    UserDto update(long userId, UserDto userDto);

    // Delete user
    UserDto deleteById(long id);

    // Find user by id
    UserDto findById(long userId);

    // List all users
    List<UserDto> findAll();
}
