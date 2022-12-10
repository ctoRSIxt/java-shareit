package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.DuplicateEmailException;
import ru.practicum.shareit.ValidationException;
import java.util.List;


@Service
@Slf4j
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long userId) {
        return userStorage.findUserById(userId);
    }

    public User create(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User update(long userId, User user) {
        User userToUpdate =  userStorage.findUserById(userId);

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }

        if (user.getEmail() != null) {
            validateUser(user);
            userToUpdate.setEmail(user.getEmail());
        }
        return userStorage.update(userToUpdate);
    }

    public User deleteById(long id) {
        return userStorage.deleteById(id);
    }


    private void validateUser(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("User: Валидация не пройдена: email пуст или не содержит @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать @");
        }

        if (!userStorage.findUsersByEmail(user.getEmail()).isEmpty()) {
            log.info("User: Валидация не пройдена: пользователь с таким email уже существует");
            throw new DuplicateEmailException("Два пользователя не могут иметь одинаковый адрес электронной почты");
        }
    }
}
