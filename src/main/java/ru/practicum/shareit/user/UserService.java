package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

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
