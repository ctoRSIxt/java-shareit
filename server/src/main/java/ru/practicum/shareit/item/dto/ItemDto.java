package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private List<Comment> comments = new ArrayList<>();

    public static Comment toInnerComment(ru.practicum.shareit.item.model.Comment comment) {
        return new Comment(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static BookingDtoForItem toBookingDtoForItem(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new BookingDtoForItem(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Comment {
        private long id;

        private String text;

        private String authorName;

        private LocalDateTime created;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingDtoForItem {
        private long id;
        private long bookerId;
    }
}
