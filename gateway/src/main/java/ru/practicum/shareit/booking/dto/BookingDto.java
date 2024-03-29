package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.validators.EndTimeAfterStartTime;
import ru.practicum.shareit.booking.validators.StartTimeBeforeNow;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EndTimeAfterStartTime(startTime = "start", endTime = "end")
public class BookingDto {
    private long id;
    private long itemId;

    @StartTimeBeforeNow
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private Item item;
    private User booker;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private long id;
        private String name;
        private String email;
    }

}