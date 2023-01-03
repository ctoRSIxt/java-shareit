package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findByRequestorId(long userId);

    List<ItemRequestDto> findAll(int from, int size, long userId);

    ItemRequestDto findById(long requestId, long userId);

}
