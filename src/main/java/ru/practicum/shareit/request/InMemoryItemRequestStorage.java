package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.EntryUnknownException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class InMemoryItemRequestStorage implements ItemRequestStorage {

    private static long idCounter = 0;
    private final Map<Long, ItemRequest> requests = new HashMap<>();


    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        itemRequest.setId(++idCounter);
        requests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest update(ItemRequest itemRequest) {

        if (!requests.containsKey(itemRequest.getId())) {
            throw new EntryUnknownException("No itemRequest with id = " + itemRequest.getId());
        }

        requests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest findById(long requestId) {
        ItemRequest itemRequest = requests.get(requestId);

        if (itemRequest == null) {
            throw new EntryUnknownException("No itemRequest with id = " + requestId);
        }

        return itemRequest;
    }

    @Override
    public List<ItemRequest> findAll() {
        return new ArrayList<>(requests.values());
    }

}