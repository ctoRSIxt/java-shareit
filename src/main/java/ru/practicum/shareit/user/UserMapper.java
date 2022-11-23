package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getDescription(),
                user.isAvailable(),
                user.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getName(),
                userDto.getDescription(),
                userDto.isAvailable(),
                userDto.getRequest() != null ? item.getRequest().getId() : null
        );
    }
}
