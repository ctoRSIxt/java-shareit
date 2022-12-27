package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Available should not be empty")
    private Boolean available;
    private Long requestId;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private List<Comment> comments = new ArrayList<>();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Comment {
        private long id;

        private String text;

        private String authorName;

        private LocalDateTime created;
    }

    public static Comment toInnerComment(ru.practicum.shareit.item.model.Comment comment) {
        return new Comment(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingDtoForItem {
        private long id;
        private long bookerId;
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
}
