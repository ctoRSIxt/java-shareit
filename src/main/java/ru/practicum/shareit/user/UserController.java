package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PutMapping
    public UserDto update(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patch(@PathVariable long userId, @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable long userId) {
        return userService.findById(userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteById(@PathVariable long userId) {
        return userService.deleteById(userId);
    }

}
