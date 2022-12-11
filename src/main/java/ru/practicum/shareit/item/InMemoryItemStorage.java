package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.EntryUnknownException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private static long idCounter = 0;
    private final Map<Long, Item> items = new HashMap<>();


    @Override
    public Item create(Item item) {
        item.setId(++idCounter);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {

        if (!items.containsKey(item.getId())) {
            throw new EntryUnknownException("No item with id = " + item.getId());
        }

        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(long itemId) {
        Item item = items.get(itemId);

        if (item == null) {
            throw new EntryUnknownException("No item with id = " + itemId);
        }

        return item;
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findAllItemsByOwnerId(long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        String textLower = text.toLowerCase();
        return items.values().stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(textLower)
                        || item.getName().toLowerCase().contains(textLower)))
                .filter(item -> item.isAvailable())
                .collect(Collectors.toList());
    }
}
