package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId() != null ? itemDto.getId().longValue() : 0,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable().booleanValue() : false,
                null,
                null
        );
    }

}
