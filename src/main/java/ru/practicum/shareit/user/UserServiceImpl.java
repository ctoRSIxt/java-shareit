package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserStorage userStorage;

    @Override
    public UserDto create(UserDto userDto) {
        validateUser(userDto);
        return UserMapper.toUserDto(userStorage.create(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto) {
        return UserMapper.toUserDto(userStorage.update(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        User user = userStorage.findUserById(userId);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            validateUser(userDto);
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userStorage.update(user));
    }

    @Override
    public UserDto deleteById(long id) {
        return UserMapper.toUserDto(userStorage.deleteById(id));
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(long userId) {
        return UserMapper.toUserDto(userStorage.findUserById(userId));
    }

    private void validateUser(UserDto userDto) {
        if (!userStorage.findUsersByEmail(userDto.getEmail()).isEmpty()) {
            log.info("User: Validation failed: user with this email already exists");
            throw new DuplicateEmailException("Two users cannot have the same email");
        }
    }
}
