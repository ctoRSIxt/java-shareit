package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
public class ItemDto {
    long id;
    String name;
    String description;
    boolean available;
    User owner;
    ItemRequest request;
}
