package ru.practicum.shareit.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class UserIntegrationTests {

    @Autowired
    UserService userService;

    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        userDto1 = new UserDto(1, "user1","email1@gmail.com");
        userDto2 = new UserDto(2, "user2","email2@gmail.com");
    }

    @Test
    void createTest() {
        assertThat(userService.create(userDto1), equalTo(userDto1));
    }

    @Test
    void findAllTest() {
        userDto1.setId(-1);
        userDto2.setId(-1);

        userService.create(userDto1);
        userService.create(userDto2);

        List<UserDto> dtoList = userService.findAll();

        assertThat(dtoList.size(), equalTo(2));
        assertThat(dtoList.get(0).getId(), equalTo(1L));
        assertThat(dtoList.get(1).getId(), equalTo(2L));
    }

    @Test
    void findByIdTest() {
        userDto1.setId(-1);
        userDto1 = userService.create(userDto1);
        assertThat(userService.findById(1), equalTo(userDto1));
    }

    @Test
    void updateTest() {
        userDto1 = userService.create(userDto1);
        userDto1.setName("New Name");
        userDto1.setEmail("new@gmail.com");
        assertThat(userService.update(1, userDto1), equalTo(userDto1));
    }
}
