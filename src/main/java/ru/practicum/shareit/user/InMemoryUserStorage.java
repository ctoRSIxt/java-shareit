package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntryUnknownException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private static long idCounter = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new EntryUnknownException("No user with id = " + id);
        }
        return user;
    }

    @Override
    public List<User> findUsersByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        user.setId(++idCounter);

        log.info("Create (post) a record for the user with id = {}", user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Edit (put) a record for the user with id = {}", user.getId());

        if (!users.containsKey(user.getId())) {
            throw new EntryUnknownException("User with id = " + user.getId() + " is unknown");
        }

        users.put(user.getId(), user);
        return user;
    }


    @Override
    public User deleteById(long id) {
        log.info("Remove (delete) a record for the user with id = {}", id);

        if (!users.containsKey(id)) {
            throw new EntryUnknownException("User with id = " + id + " is unknown");
        }

        User user = users.get(id);
        users.remove(id);
        return user;
    }

}
