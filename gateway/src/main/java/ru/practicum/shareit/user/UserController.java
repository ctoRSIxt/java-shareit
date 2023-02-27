package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.create(userDto);
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestBody UserDto userDto) {
        log.info("Updating user {}", userDto);
        return userClient.update(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patch(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Patching user {} with userId={}", userDto, userId);
        return userClient.patch(userId, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Getting all users");
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable long userId) {
        log.info("Getting user with userId={}", userId);
        return userClient.findById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteById(@PathVariable long userId) {
        return userClient.deleteById(userId);
    }

}
