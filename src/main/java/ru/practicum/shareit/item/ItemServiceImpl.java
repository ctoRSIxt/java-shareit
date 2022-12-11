package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotItemOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService{

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemRequestStorage requestStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage,
                           UserStorage userStorage,
                           ItemRequestStorage requestStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.requestStorage = requestStorage;
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {

        validateItemDto(itemDto);
        Item item = ItemMapper.toItem(itemDto);

        item.setOwner(userStorage.findUserById(userId));

        if (itemDto.getRequestId() != null) {
            item.setRequest(requestStorage.findById(itemDto.getRequestId()));
        }
        itemStorage.create(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemStorage.findById(itemId);
        if (item.getOwner().getId() != userId) {
            throw new UserNotItemOwnerException("The user is not the owner of the item");
        }

        item = ItemMapper.updateItem(item, itemDto);

        if (itemDto.getRequestId() != null) {
            item.setRequest(requestStorage.findById(itemDto.getRequestId()));
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findById(long itemId) {
        return ItemMapper.toItemDto(itemStorage.findById(itemId));
    }

    @Override
    public List<ItemDto> findAllItemsByOwner(long userId) {
        return itemStorage.findAllItemsByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByText(String text) {
        return itemStorage.findByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateItemDto(ItemDto itemDto) {

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Name is not specified");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Description is not specified");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Available is not specified");
        }
    }


}
