package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingDtoForItem {
        private long id;
        private long bookerId;
    }

}
