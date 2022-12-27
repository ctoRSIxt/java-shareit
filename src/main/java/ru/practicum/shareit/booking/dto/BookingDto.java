package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private long id;
    private long itemId;
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

    public static Item toInnerItem(ru.practicum.shareit.item.model.Item item0) {
        return new Item(item0.getId(),
                item0.getName(),
                item0.getDescription(),
                item0.getAvailable());
    }

    @Data
    @AllArgsConstructor
    public static class User {
        private long id;
        private String name;
        private String email;
    }

    public static User toInnerUser(ru.practicum.shareit.user.model.User user0) {
        return new User(user0.getId(),
                user0.getName(),
                user0.getEmail());
    }

}