package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.EntryUnknownException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class UserTests {


    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto1;
    private UserDto userDto2;

    private User user1;
    private User user2;


    @BeforeEach
    void setUp() {

        userDto1 = new UserDto(1, "user1", "email1@gmail.com");
        userDto2 = new UserDto(2, "user2", "email2@gmail.com");

        user1 = new User(1, "user1", "email1@gmail.com");
        user2 = new User(2, "user2", "email2@gmail.com");


        Mockito.lenient().when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    return invocationOnMock.getArgument(0, User.class);
                });


        Mockito.lenient().when(userRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long id = invocationOnMock.getArgument(0, Long.class);

                    if (id == 1) {
                        return Optional.of(user1);
                    } else {
                        return Optional.empty();
                    }
                });

    }


    @Test
    public void createTest() {
        Assertions.assertEquals(userDto1, userService.create(userDto1));
        Assertions.assertEquals(userDto2, userService.create(userDto2));
    }

    @Test
    public void updateUnknown() {

        final EntryUnknownException exception = Assertions.assertThrows(
                EntryUnknownException.class,
                () -> {
                    userService.update(2, userDto2);
                }
        );

        Assertions.assertEquals("No user with id = 2", exception.getMessage());
    }

    @Test
    public void updateExisting() {

        String updatedName = "updated name";
        userDto1.setName(updatedName);
        Assertions.assertEquals(updatedName, userService.update(1, userDto1).getName());

        String updatedEmail = "updated@email.com";
        userDto1.setEmail(updatedEmail);
        Assertions.assertEquals(updatedEmail, userService.update(1, userDto1).getEmail());
    }

    @Test
    public void deleteUnknown() {

        final EntryUnknownException exception = Assertions.assertThrows(
                EntryUnknownException.class,
                () -> {
                    userService.deleteById(2);
                }
        );

        Assertions.assertEquals("No user with id = 2", exception.getMessage());
    }

    @Test
    public void deleteExisting() {
        Assertions.assertEquals(userDto1, userService.deleteById(1));
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    public void findByIdTest() {
        Assertions.assertEquals(userDto1, userService.findById(1));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(1L);
    }

    @Test
    public void findAllTest() {

        Mockito.lenient().when(userRepository.findAll())
                .thenAnswer(invocationOnMock -> {
                    return List.of(user1, user2);
                });

        List<UserDto> userDtos = userService.findAll();
        Assertions.assertEquals(2, userDtos.size());
        Assertions.assertEquals(userDto1, userDtos.get(0));
        Assertions.assertEquals(userDto2, userDtos.get(1));
        Mockito.verify(userRepository, Mockito.times(1))
                .findAll();
    }

}
