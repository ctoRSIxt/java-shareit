package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ShareItTests {

//	@Test
//	void contextLoads() {
//	}

	@Test
	public void TestUser() {

		UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

		User user1 = new User(35, "user", "user@user.com");
		User user2 = new User(35, "user", "user@user.com");



		userController.create(user1);
		userController.create(user2);
		assertEquals(2, userController.findAll().size(), "Пользователи не добавляются");

	}

}
