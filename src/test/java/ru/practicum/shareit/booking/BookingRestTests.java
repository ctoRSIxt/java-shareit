package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

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
public class BookingRestTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper objectMapper = new ObjectMapper();


    private BookingDto bookingDto1;
    private BookingDto bookingDto2;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ErrorHandler.class)
                .build();


        User user1 = new User(1, "user1", "email1@gmail.com");
        User user2 = new User(2, "user2", "email2@gmail.com");


        ItemRequest itemRequest1 = new ItemRequest(1, "description1",
                user1, LocalDateTime.now());


        ItemDto itemDto1 = new ItemDto(1L, "item1",
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

        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void postTest() throws Exception {
        Mockito.when(bookingService.create(Mockito.anyLong(), Mockito.any(BookingDto.class)))
                .thenReturn(bookingDto1);


        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().toString())));
    }

    @Test
    void patchToApproveTest() throws Exception {

        Mockito.when(bookingService.approveBooking(Mockito.anyLong(),
                        Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingDto1);


        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().toString())));
    }


    @Test
    void findByIdTest() throws Exception {
        Mockito.when(bookingService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingDto1);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().toString())));
    }

    @Test
    void findByUserTest() throws Exception {
        Mockito.when(bookingService.findAllByBookerId(Mockito.anyLong(),
                        Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())));
    }


    @Test
    void findByOwnerTest() throws Exception {
        Mockito.when(bookingService.findAllByOwnerId(Mockito.anyLong(),
                        Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "2")
                        .param("state", "FUTURE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())));
    }
}
