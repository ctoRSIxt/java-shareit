package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Item findById(long itemId);

    List<Item> findAll();

    List<Item> findAllItemsByOwnerId(long ownerId);

    List<Item> findByText(String text);
}
