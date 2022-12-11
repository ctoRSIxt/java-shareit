package ru.practicum.shareit.request;

import ru.practicum.shareit.EntryUnknownException;

import java.util.ArrayList;
import java.util.List;

public interface ItemRequestStorage {

    ItemRequest create(ItemRequest itemRequest);

    ItemRequest update(ItemRequest itemRequest);

    ItemRequest findById(long requestId);

    List<ItemRequest> findAll();
}
