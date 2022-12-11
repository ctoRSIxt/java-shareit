package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto findById(long userId) {
        return UserMapper.toUserDto(userStorage.findUserById(userId));
    }

    public UserDto create(UserDto userDto) {
        validateUser(userDto);
        return UserMapper.toUserDto(userStorage.create(UserMapper.toUser(userDto)));
    }

    public UserDto update(UserDto userDto) {
        return UserMapper.toUserDto(userStorage.update(UserMapper.toUser(userDto)));
    }

    public UserDto update(long userId, UserDto userDto) {
        User user =  userStorage.findUserById(userId);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            validateUser(userDto);
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userStorage.update(user));
    }

    public UserDto deleteById(long id) {
        return UserMapper.toUserDto(userStorage.deleteById(id));
    }


    private void validateUser(UserDto userDto) {

        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            log.info("User: Валидация не пройдена: email пуст или не содержит @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать @");
        }

        if (!userStorage.findUsersByEmail(userDto.getEmail()).isEmpty()) {
            log.info("User: Валидация не пройдена: пользователь с таким email уже существует");
            throw new DuplicateEmailException("Два пользователя не могут иметь одинаковый адрес электронной почты");
        }
    }
}
