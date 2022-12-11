package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.Map;


@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

    @PatchMapping("/{userId}")
    public User patch(@PathVariable long userId, @RequestBody User user) {
        return userService.update(userId, user);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable long userId) {
        return userService.findById(userId);
    }

    @DeleteMapping("/{userId}")
    public User deleteById(@PathVariable long userId) {
        return userService.deleteById(userId);
    }

}
