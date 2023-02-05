package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class RequestRestTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user1;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        user1 = new User(1, "user1", "email1@gmail.com");
        itemRequest1 = new ItemRequest(1, "description1",
                user1, LocalDateTime.now());

        itemRequest2 = new ItemRequest(2, "description2",
                user1, LocalDateTime.now());

        itemRequestDto1 = ItemRequestMapper.toItemRequestDto(itemRequest1);
        itemRequestDto2 = ItemRequestMapper.toItemRequestDto(itemRequest2);

        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void postTest() throws Exception {
        Mockito.when(itemRequestService.create(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto1);


        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.requestorId", is(itemRequestDto1.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())));
    }

    @Test
    void postBlankDescriptionTest() throws Exception {
        itemRequestDto1.setDescription("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByRequestorTest() throws Exception {
        Mockito.when(itemRequestService.findByRequestorId(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestDto1, itemRequestDto2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].requestorId", is(itemRequestDto1.getRequestorId()),
                        Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }


    @Test
    void findAllTest() throws Exception {
        Mockito.when(itemRequestService.findAll(Mockito.anyInt(),
                        Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(List.of(itemRequestDto1, itemRequestDto2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].requestorId", is(itemRequestDto1.getRequestorId()),
                        Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }


    @Test
    void findByIdTest() throws Exception {
        Mockito.when(itemRequestService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestDto1);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())));
    }

}
