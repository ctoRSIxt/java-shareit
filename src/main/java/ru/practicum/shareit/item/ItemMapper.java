package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

//private long id;
//private String name;
//private String description;
//private boolean available;
//private User owner;
//private ItemRequest request;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner(),
                item.getRequest() != null ? item.getRequest() : null
//                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.isAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
    }
}
