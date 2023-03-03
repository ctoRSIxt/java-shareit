package ru.practicum.shareit.request.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private long id;

    private String description;
    private long requestorId;
    private LocalDateTime created;
    private List<Item> items;

    public ItemRequestDto(long id, String description, long requestorId, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestorId = requestorId;
        this.created = created;
    }

    public static Item toInnerItem(ru.practicum.shareit.item.model.Item item) {
        return new Item(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getRequest().getId(), item.getOwner().getId());
    }

    @Data
    @AllArgsConstructor
    public static class Item {
        private long id;
        private String name;
        private String description;
        private boolean available;
        private long requestId;
        private long ownerId;
    }
}
