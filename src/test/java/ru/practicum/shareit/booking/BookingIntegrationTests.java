package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class BookingIntegrationTests {

    @Autowired
    private BookingService bookingService;

    @Autowired
    UserService userService;

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRequestService itemRequestService;

    private UserDto userDto1;
    private UserDto userDto2;

    private BookingDto bookingDto1;
    private BookingDto bookingDto2;

    private ItemDto itemDto1;
    private ItemRequestDto itemRequestDto1;


    @BeforeEach
    void setUp() {

        userDto1 = new UserDto(1, "user1", "email1@gmail.com");
        userDto2 = new UserDto(2, "user2", "email2@gmail.com");

        User user1 = new User(1, "user1", "email1@gmail.com");
        User user2 = new User(2, "user2", "email2@gmail.com");


        ItemRequest itemRequest1 = new ItemRequest(1, "description1",
                user2, LocalDateTime.now());

        itemRequestDto1 = ItemRequestMapper.toItemRequestDto(itemRequest1);

        itemDto1 = new ItemDto(1L, "item1",
                "item1 description", true,
                1L, null, null, new ArrayList<>());

        Item item1 = ItemMapper.toItem(itemDto1);
        item1.setOwner(user2);
        item1.setRequest(itemRequest1);


        Booking booking1 = new Booking(1L,
                item1,
                user1,
                LocalDateTime.of(2023, 12, 12, 12, 12),
                LocalDateTime.of(2023, 12, 15, 12, 12),
                BookingStatus.APPROVED);

        bookingDto1 = BookingMapper.toBookingDto(booking1);

        Booking booking2 = new Booking(2L,
                item1,
                user1,
                LocalDateTime.of(2023, 12, 12, 12, 12),
                LocalDateTime.of(2023, 12, 15, 12, 12),
                BookingStatus.APPROVED);

        bookingDto2 = BookingMapper.toBookingDto(booking2);
    }


    @Test
    void findAllByBookerIdTest() {
        bookingDto1.setId(-1L);
        bookingDto2.setId(-1L);

        userDto1 = userService.create(userDto1);
        userDto2 = userService.create(userDto2);


        itemRequestDto1 = itemRequestService.create(userDto1.getId(), itemRequestDto1);
        itemDto1 = itemService.create(userDto1.getId(), itemDto1);

        bookingService.create(userDto2.getId(), bookingDto1);
        bookingService.create(userDto2.getId(), bookingDto2);

        List<BookingDto> bookingDtoList = bookingService.findAllByBookerId(userDto2.getId(),
                "FUTURE", 0, 2);

        bookingDto1.setId(1L);
        bookingDto1.setStatus(BookingStatus.WAITING);
        bookingDto1.setBooker(BookingDto.toInnerUser(UserMapper.toUser(userDto2)));

        bookingDto2.setId(2L);
        bookingDto2.setStatus(BookingStatus.WAITING);
        bookingDto2.setBooker(BookingDto.toInnerUser(UserMapper.toUser(userDto2)));

        assertThat(bookingDtoList.size(), equalTo(2));
        assertThat(bookingDtoList.get(0), equalTo(bookingDto1));
        assertThat(bookingDtoList.get(1), equalTo(bookingDto2));
    }

}
