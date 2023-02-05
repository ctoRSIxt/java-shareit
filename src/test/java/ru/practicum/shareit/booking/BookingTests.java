package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.EntryUnknownException;
import ru.practicum.shareit.exceptions.StateValidationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class BookingTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;


    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private ItemDto itemDto1;
    private Item item1;

    private Booking booking1;
    private Booking booking2;
    private BookingDto bookingDto1;
    private BookingDto bookingDto2;


    @BeforeEach
    public void setUp() {

        user1 = new User(1, "user1", "email1@gmail.com");
        user2 = new User(2, "user2", "email2@gmail.com");


        itemRequest1 = new ItemRequest(1, "description1",
                user1, LocalDateTime.now());


        itemDto1 = new ItemDto(1L, "item1",
                "item1 description", true,
                1L, null, null, new ArrayList<>());

        item1 = ItemMapper.toItem(itemDto1);
        item1.setOwner(user2);
        item1.setRequest(itemRequest1);


        booking1 = new Booking(1L,
                item1,
                user1,
                LocalDateTime.of(2023, 12, 12, 12, 12),
                LocalDateTime.of(2023, 12, 15, 12, 12),
                BookingStatus.APPROVED);

        bookingDto1 = BookingMapper.toBookingDto(booking1);

        booking2 = new Booking(2L,
                item1,
                user1,
                LocalDateTime.of(2023, 12, 12, 12, 12),
                LocalDateTime.of(2023, 12, 15, 12, 12),
                BookingStatus.APPROVED);

        bookingDto2 = BookingMapper.toBookingDto(booking2);

    }


    @Test
    public void createTest() {

        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);

        Assertions.assertEquals(bookingDto1, bookingService.create(user1.getId(), bookingDto1));
    }

    @Test
    public void createNoBookingTest() {

        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final EntryUnknownException exception = Assertions.assertThrows(
                EntryUnknownException.class,
                () -> {
                    bookingService.create(user1.getId(), bookingDto1);
                }
        );
    }

    @Test
    public void createNoAvailableTest() {

        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        item1.setAvailable(false);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> {
                    bookingService.create(user1.getId(), bookingDto1);
                }
        );
    }

    @Test
    public void createWithUserOwnerTest() {

        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        item1.setOwner(user1);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        final EntryUnknownException exception = Assertions.assertThrows(
                EntryUnknownException.class,
                () -> {
                    bookingService.create(user1.getId(), bookingDto1);
                }
        );
    }


    @Test
    public void findByIdTest() {

        booking1.setStatus(BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);


        BookingDto bookingDto = bookingService.findById(2L, 1L);
        Assertions.assertEquals(BookingMapper.toBookingDto(booking1), bookingDto);
    }


    @Test
    public void approveBookingTest() {

        booking1.setStatus(BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);


        BookingDto bookingDto = bookingService.approveBooking(2L, 1L, true);

        booking1.setStatus(BookingStatus.APPROVED);
        Assertions.assertEquals(BookingMapper.toBookingDto(booking1), bookingDto);
    }

    @Test
    public void rejectBookingTest() {

        booking1.setStatus(BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);


        BookingDto bookingDto = bookingService.approveBooking(2L, 1L, false);

        booking1.setStatus(BookingStatus.REJECTED);
        Assertions.assertEquals(BookingMapper.toBookingDto(booking1), bookingDto);
    }


    @Test
    public void findByBookerWrongStateTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        final StateValidationException exception = Assertions.assertThrows(
                StateValidationException.class,
                () -> {
                    bookingService.findAllByBookerId(2L,
                            "WRONGSTATE", 0, 2);
                }
        );

    }


    @Test
    public void findByBookerPastTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByBookerIdAndEndBefore(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));


        List<BookingDto> bookingDtos = bookingService.findAllByBookerId(2L,
                "PAST", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }

    @Test
    public void findByBookerFutureTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByBookerIdAndStartAfter(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));


        List<BookingDto> bookingDtos = bookingService.findAllByBookerId(2L,
                "FUTURE", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }

    @Test
    public void findByBookerCurrentTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));


        List<BookingDto> bookingDtos = bookingService.findAllByBookerId(2L,
                "CURRENT", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }

    @Test
    public void findByBookerRejectedTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByBookerIdAndStatus(Mockito.anyLong(),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));


        List<BookingDto> bookingDtos = bookingService.findAllByBookerId(2L,
                "REJECTED", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }

    @Test
    public void findByBookerWaitingTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByBookerIdAndStatus(Mockito.anyLong(),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));


        List<BookingDto> bookingDtos = bookingService.findAllByBookerId(2L,
                "WAITING", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }

    @Test
    public void findByBookerAllTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByBookerId(Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));


        List<BookingDto> bookingDtos = bookingService.findAllByBookerId(2L,
                "ALL", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }


    @Test
    public void findByOwnerPastTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByItemIdInAndEndBefore(Mockito.anyList(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));

        List<BookingDto> bookingDtos = bookingService.findAllByOwnerId(2L,
                "PAST", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }


    @Test
    public void findByOwnerFutureTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByItemIdInAndStartAfter(Mockito.anyList(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));

        List<BookingDto> bookingDtos = bookingService.findAllByOwnerId(2L,
                "FUTURE", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }

    @Test
    public void findByOwnerCurrentTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByItemIdInAndStartBeforeAndEndAfter(Mockito.anyList(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));

        List<BookingDto> bookingDtos = bookingService.findAllByOwnerId(2L,
                "CURRENT", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }

    @Test
    public void findByOwnerRejectedTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByItemIdInAndStatus(Mockito.anyList(),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));

        List<BookingDto> bookingDtos = bookingService.findAllByOwnerId(2L,
                "REJECTED", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }

    @Test
    public void findByOwnerWaitingTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByItemIdInAndStatus(Mockito.anyList(),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));

        List<BookingDto> bookingDtos = bookingService.findAllByOwnerId(2L,
                "WAITING", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }

    @Test
    public void findByOwnerAllTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(bookingRepository.findByItemIdInOrderByStartDesc(Mockito.anyList(),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));

        List<BookingDto> bookingDtos = bookingService.findAllByOwnerId(2L,
                "ALL", 0, 2);

        Assertions.assertEquals(2, bookingDtos.size());
        Assertions.assertEquals(bookingDto1, bookingDtos.get(0));
        Assertions.assertEquals(bookingDto2, bookingDtos.get(1));
    }

}