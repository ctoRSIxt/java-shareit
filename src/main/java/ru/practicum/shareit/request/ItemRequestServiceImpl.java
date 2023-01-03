package ru.practicum.shareit.request;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {

    }

    @Override
    public List<ItemRequestDto> findByRequestorId(long userId) {

    }

    @Override
    public List<ItemRequestDto> findAll(int from, int size, long userId) {

    }

    @Override
    public ItemRequestDto findById(long requestId, long userId) {

    }


}
