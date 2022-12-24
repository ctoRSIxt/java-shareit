package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {

    // Create a new itemDto
    ItemDto create(long userId, ItemDto itemDto);

    // Update an existing itemDto
    ItemDto update(long userId, long itemId, ItemDto itemDto);

    // Find an itemDto by id
    ItemDto findById(long itemId);

    // Find all items belonging to owner
    List<ItemDto> findAllItemsByOwner(long userId);

    // Find an itemDto containing text in description or name
    List<ItemDto> findByText(String text);

    Comment createComment(Comment comment, long itemId, long userId);
}
