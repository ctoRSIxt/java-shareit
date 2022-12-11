package ru.practicum.shareit.item;

import ru.practicum.shareit.EntryUnknownException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Item findById(long itemId);

    List<Item> findAll();

    List<Item> findAllItemsByOwnerId(long ownerId);

    List<Item> findByText(String text);
}
