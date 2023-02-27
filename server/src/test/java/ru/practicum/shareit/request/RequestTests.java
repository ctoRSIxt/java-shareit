package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
public class RequestTests {


    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private User user1;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;


    @BeforeEach
    public void setUp() {
        user1 = new User(1, "user1", "email1@gmail.com");
        itemRequest1 = new ItemRequest(1, "description1",
                user1, LocalDateTime.now());

        itemRequest2 = new ItemRequest(2, "description2",
                user1, LocalDateTime.now());


        itemRequestDto1 = ItemRequestMapper.toItemRequestDto(itemRequest1);
        itemRequestDto2 = ItemRequestMapper.toItemRequestDto(itemRequest2);

    }


    @Test
    public void createTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest1);

        ItemRequestDto requestDto = requestService.create(user1.getId(), itemRequestDto1);
        itemRequestDto1.setCreated(requestDto.getCreated());
        Assertions.assertEquals(itemRequestDto1, requestDto);

    }


    @Test
    public void findByRequestorTest() {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(requestRepository.findByRequestorIdOrderByCreated(Mockito.anyLong()))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        Mockito.when(itemRepository.findAllByRequestIdIsIn(Mockito.anyList()))
                .thenReturn(new ArrayList<>());


        List<ItemRequestDto> requestDtoList = requestService.findByRequestorId(user1.getId());

        itemRequestDto1.setItems(new ArrayList<>());
        itemRequestDto2.setItems(new ArrayList<>());
        Assertions.assertEquals(2, requestDtoList.size());
        Assertions.assertEquals(itemRequestDto1, requestDtoList.get(0));
        Assertions.assertEquals(itemRequestDto2, requestDtoList.get(1));

    }


    @Test
    public void findAllTest() {

        Mockito.when(requestRepository.findAllByRequestorIdNot(Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest1, itemRequest2)));

        Mockito.when(itemRepository.findAllByRequestIdIsIn(Mockito.anyList()))
                .thenReturn(new ArrayList<>());


        List<ItemRequestDto> requestDtoList = requestService.findAll(0, 2, user1.getId());

        itemRequestDto1.setItems(new ArrayList<>());
        itemRequestDto2.setItems(new ArrayList<>());
        Assertions.assertEquals(2, requestDtoList.size());
        Assertions.assertEquals(itemRequestDto1, requestDtoList.get(0));
        Assertions.assertEquals(itemRequestDto2, requestDtoList.get(1));

    }


    @Test
    public void findByIdTest() {

        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));

        Mockito.when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest1));

        Mockito.when(itemRepository.findAllByRequestIdIsIn(Mockito.anyList()))
                .thenReturn(new ArrayList<>());


        itemRequestDto1.setItems(new ArrayList<>());
        Assertions.assertEquals(itemRequestDto1, requestService.findById(1L, user1.getId()));
    }
}