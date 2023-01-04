package ru.practicum.shareit.request;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntryUnknownException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {

        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new EntryUnknownException("No user with id = " + userId));

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requestor);

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findByRequestorId(long userId) {
        userService.findById(userId);

        List<ItemRequestDto> result = itemRequestRepository
                .findByRequestorIdOrderByCreated(userId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        return fillItemsList(result);

    }

    @Override
    public List<ItemRequestDto> findAll(int from, int size, long userId) {

        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByRequestorIdNot(userId,
                PageRequest.of(from / size,
                        size,
                        Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        return fillItemsList(itemRequestDtos);
    }

    @Override
    public ItemRequestDto findById(long requestId, long userId) {
        userService.findById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntryUnknownException("No item request with id = " + requestId));

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        return fillItemsList(List.of(itemRequestDto)).get(0);
    }

    private List<ItemRequestDto> fillItemsList(List<ItemRequestDto> itemRequestDtos) {

        List<Long> itemRequestIds = itemRequestDtos.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdIsIn(itemRequestIds);

        itemRequestDtos.forEach(request -> request.setItems(
                items.stream()
                        .filter(item -> item.getRequest().getId() == request.getId())
                        .map(ItemRequestDto::toInnerItem)
                        .collect(Collectors.toList())
                )
        );
        return itemRequestDtos;
    }

}
