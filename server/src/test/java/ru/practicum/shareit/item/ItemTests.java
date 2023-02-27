package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.EntryUnknownException;
import ru.practicum.shareit.exceptions.UserNotItemOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class ItemTests {


    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user1;
    private ItemRequest itemRequest1;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Booking booking1;

    @BeforeEach
    public void setUp() {
        user1 = new User(1, "user1", "email1@gmail.com");
        itemRequest1 = new ItemRequest(1, "iReq description",
                user1, LocalDateTime.now());

        itemDto1 = new ItemDto(1L, "item1",
                "item1 description", true,
                1L, null, null, new ArrayList<>());

        item1 = ItemMapper.toItem(itemDto1);
        item1.setOwner(user1);
        item1.setRequest(itemRequest1);

        itemDto2 = new ItemDto();
        item2 = ItemMapper.toItem(itemDto2);
        itemDto2 = ItemMapper.toItemDto(item2);

        itemDto2 = new ItemDto(2L, "item2",
                "item2 description", true,
                1L, null, null, new ArrayList<>());

        item2 = ItemMapper.toItem(itemDto2);
        item2.setOwner(user1);
        item2.setRequest(itemRequest1);

        comment1 = new Comment(1L, "comment text",
                1L, user1, LocalDateTime.now());


        booking1 = new Booking(1L,
                item1,
                user1,
                LocalDateTime.of(2022, 12, 12, 12, 12),
                LocalDateTime.of(2022, 12, 15, 12, 12),
                BookingStatus.APPROVED);

    }


    @Test
    public void createTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest1));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocationOnMock -> {
                    return invocationOnMock.getArgument(0, Item.class);
                });

        Assertions.assertEquals(itemDto1, itemService.create(user1.getId(), itemDto1));

    }

    @Test
    public void createWithUnknownUserTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(new EntryUnknownException("No user with id = 2"));

        final EntryUnknownException exception = Assertions.assertThrows(
                EntryUnknownException.class,
                () -> {
                    itemService.create(2, itemDto1);
                }
        );

        Assertions.assertEquals("No user with id = 2", exception.getMessage());

    }


    @Test
    public void updateTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocationOnMock -> {
                    return invocationOnMock.getArgument(0, Item.class);
                });

        Mockito.when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest1));

        itemDto1.setName("new name");
        itemDto1.setDescription("new description");

        Assertions.assertEquals(itemDto1,
                itemService.update(user1.getId(), itemDto1.getId(), itemDto1));
    }

    @Test
    public void updateByNotOwnerTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        itemDto1.setName("new name");
        itemDto1.setDescription("new description");

        final UserNotItemOwnerException exception = Assertions.assertThrows(
                UserNotItemOwnerException.class,
                () -> {
                    itemService.update(3L, itemDto1.getId(), itemDto1);
                }
        );
    }

    @Test
    public void findByIdTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));

        Mockito.when(bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking1));

        itemDto1.setLastBooking(ItemDto.toBookingDtoForItem(booking1));

        Assertions.assertEquals(itemDto1,
                itemService.findById(user1.getId(), itemDto1.getId()));
    }


    @Test
    public void findByOwnerTest() {
        Mockito.when(itemRepository.findAllByOwnerId(Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item1, item2)));

        Mockito.when(commentRepository.findByItemIdOrderByCreatedDesc(Mockito.anyLong()))
                .thenReturn(List.of(comment1));

        List<ItemDto> itemDtoList = itemService.findAllItemsByOwner(1L, 0, 2);
        Assertions.assertEquals(2, itemDtoList.size());

        itemDto1.setComments(List.of(ItemDto.toInnerComment(comment1)));
        Assertions.assertEquals(itemDto1, itemDtoList.get(0));
    }

    @Test
    public void findByTextTest() {
        Mockito.when(itemRepository.searchItemsByText(Mockito.anyString(),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item1, item2)));

        List<ItemDto> itemDtoList = itemService.findByText("find text", 0, 2);
        Assertions.assertEquals(2, itemDtoList.size());
        Assertions.assertEquals(itemDto1, itemDtoList.get(0));
    }

    @Test
    public void findByTextEmptyTest() {
        List<ItemDto> itemDtoList = itemService.findByText("", 0, 2);
        Assertions.assertEquals(0, itemDtoList.size());
    }

    @Test
    public void createCommentTest() {
        Mockito.when(bookingRepository
                        .findByBookerIdAndItemIdAndEndBefore(Mockito.anyLong(),
                                Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(booking1));


        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenAnswer(invocationOnMock -> {
                    Comment comment = invocationOnMock.getArgument(0, Comment.class);
                    comment.setId(1L);
                    return comment;
                });


        CommentDto commentDto = CommentMapper.toCommentDto(comment1);
        itemService.createComment(commentDto, 1L, 1L);
    }

}
