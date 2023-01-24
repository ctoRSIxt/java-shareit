package ru.practicum.shareit.item;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class ItemIntegrationTests {

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    ItemRequestService requestService;

    private UserDto userDto1;
    private UserDto userDto2;

    private ItemDto itemDto1;
    private ItemDto itemDto2;

    private ItemRequestDto itemRequestDto1;

    @BeforeEach
    void setUp() {

        userDto1 = new UserDto(1, "user1", "email1@gmail.com");
        userDto2 = new UserDto(2, "user2", "email2@gmail.com");

        itemDto1 = new ItemDto(1L, "item1",
                "item1 description", true,
                1L, null, null, new ArrayList<>());

        itemDto2 = new ItemDto(2L, "item2",
                "item2 description", true,
                1L, null, null, new ArrayList<>());

        itemRequestDto1 = new ItemRequestDto(1, "iReq description",
                1L, LocalDateTime.now());
    }

    @Test
    void findAllTest() {
        itemDto1.setId(-1L);
        itemDto2.setId(-1L);

        userDto1 = userService.create(userDto1);
        userDto2 = userService.create(userDto2);

        requestService.create(userDto1.getId(), itemRequestDto1);

        itemService.create(userDto1.getId(), itemDto1);
        itemService.create(userDto1.getId(), itemDto2);

        List<ItemDto> dtoList = itemService.findAllItemsByOwner(userDto1.getId(), 0, 2);

        itemDto1.setId(1L);
        itemDto2.setId(2L);

        assertThat(dtoList.size(), equalTo(2));
        assertThat(dtoList.get(0), equalTo(itemDto1));
        assertThat(dtoList.get(1), equalTo(itemDto2));
    }


}
