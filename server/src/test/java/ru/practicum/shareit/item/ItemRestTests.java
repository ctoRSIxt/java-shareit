package ru.practicum.shareit.item;

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
import ru.practicum.shareit.exceptions.EntryUnknownException;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRestTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ItemServiceImpl itemService;
    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ItemDto itemDto1;
    private ItemDto itemDto2;

    private CommentDto commentDto1;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        itemDto1 = new ItemDto(1L, "item1",
                "item1 description", true,
                1L, null, null, new ArrayList<>());

        itemDto2 = new ItemDto(2L, "item2",
                "item2 description", true,
                1L, null, null, new ArrayList<>());


        commentDto1 = new CommentDto(1L, "text",
                "author name", LocalDateTime.now());

        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void postTest() throws Exception {
        Mockito.when(itemService.create(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(itemDto1);


        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())));
    }

    @Test
    void postBlankDescriptionTest() throws Exception {
        itemDto1.setDescription("");

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void patchExistingTest() throws Exception {

        Mockito.when(itemService.update(Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.any(ItemDto.class)))
                .thenAnswer(invocationOnMock -> {

                    Long itemId = invocationOnMock.getArgument(1, Long.class);

                    if (itemId == 1) {
                        return invocationOnMock.getArgument(2, ItemDto.class);
                    } else {
                        throw new EntryUnknownException("No item with id = " + itemId);
                    }
                });

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())));
    }

    @Test
    void patchUnknownTest() throws Exception {

        Mockito.when(itemService.update(Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.any(ItemDto.class)))
                .thenAnswer(invocationOnMock -> {

                    Long itemId = invocationOnMock.getArgument(1, Long.class);

                    if (itemId == 1) {
                        return invocationOnMock.getArgument(2, ItemDto.class);
                    } else {
                        throw new EntryUnknownException("No item with id = " + itemId);
                    }
                });

        mockMvc.perform(patch("/items/{itemId}", 2)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void getAllTest() throws Exception {
        Mockito.when(itemService.findAllItemsByOwner(Mockito.anyLong(),
                        Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDto1, itemDto2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())));
    }


    @Test
    void findByIdTest() throws Exception {
        Mockito.when(itemService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemDto1);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())));
    }


    @Test
    void searchTest() throws Exception {
        Mockito.when(itemService.findByText(Mockito.anyString(),
                        Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDto1, itemDto2));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "find")
                        .param("from", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())));
    }


    @Test
    void createCommentTest() throws Exception {
        Mockito.when(itemService.createComment(Mockito.any(CommentDto.class),
                        Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(commentDto1);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto1.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto1.getAuthorName())));
    }

}
