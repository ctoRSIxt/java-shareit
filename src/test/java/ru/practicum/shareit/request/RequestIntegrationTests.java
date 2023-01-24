package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
public class RequestIntegrationTests {


    @Autowired
    ItemRequestService itemRequestService;

    @Autowired
    UserService userService;

    private UserDto userDto1;
    private UserDto userDto2;

    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;

    @BeforeEach
    void setUp() {

        userDto1 = new UserDto(1, "user1","email1@gmail.com");
        userDto2 = new UserDto(2, "user2","email2@gmail.com");

        User user1 = new User(1, "user1", "email1@gmail.com");
        ItemRequest itemRequest1 = new ItemRequest(1, "description1",
                user1, LocalDateTime.now());

        ItemRequest itemRequest2 = new ItemRequest(2, "description2",
                user1, LocalDateTime.now());

        itemRequestDto1 = ItemRequestMapper.toItemRequestDto(itemRequest1);
        itemRequestDto2 = ItemRequestMapper.toItemRequestDto(itemRequest2);
    }


    @Test
    void findAllTest() {
        itemRequestDto1.setId(-1L);
        itemRequestDto2.setId(-1L);

        userDto1 = userService.create(userDto1);
        userDto2 = userService.create(userDto2);

        itemRequestService.create(userDto1.getId(), itemRequestDto1);
        itemRequestService.create(userDto1.getId(), itemRequestDto2);

        List<ItemRequestDto> requestDtoList = itemRequestService.findByRequestorId(userDto1.getId());

        itemRequestDto1.setId(1L);
        itemRequestDto1.setCreated(requestDtoList.get(0).getCreated());
        itemRequestDto1.setItems(new ArrayList<>());
        itemRequestDto2.setId(2L);
        itemRequestDto2.setCreated(requestDtoList.get(1).getCreated());
        itemRequestDto2.setItems(new ArrayList<>());

        assertThat(requestDtoList.size(), equalTo(2));
        assertThat(requestDtoList.get(0), equalTo(itemRequestDto1));
        assertThat(requestDtoList.get(1), equalTo(itemRequestDto2));
    }


}
