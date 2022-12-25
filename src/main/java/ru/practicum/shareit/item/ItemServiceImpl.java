package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.EntryUnknownException;
import ru.practicum.shareit.exceptions.UserNotItemOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new EntryUnknownException("No user with id = " + userId));

//        item.setOwnerId(owner.getId());
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            item.setRequest(requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new EntryUnknownException("No itemRequest with id = " + itemDto.getRequestId())));
        }

        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntryUnknownException("No item with id = " + itemId));

        if (item.getOwner().getId() != userId) {
            throw new UserNotItemOwnerException("The user is not the owner of the item");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.getRequestId() != null) {
            item.setRequest(requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new EntryUnknownException("No itemRequest with id = " + itemDto.getRequestId())));
        }

        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findById(long userId, long itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntryUnknownException("No item with id = " + itemId));


        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().getId() == userId) {
            itemDto = setBookingInfo(itemDto);
        }

        itemDto.setComments(getCommentDtoByItemId(itemDto.getId()));
        return itemDto;
    }

    @Override
    public List<ItemDto> findAllItemsByOwner(long userId) {
        return itemRepository.findAllByOwnerIdOrderById(userId).stream()
                .map(ItemMapper::toItemDto)
                .map(itemDto -> {
                    return setBookingInfo(itemDto);
                })
                .map(itemDto -> {
                    itemDto.setComments(getCommentDtoByItemId(itemDto.getId()));
                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, long itemId, long userId) {

        if (bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())
                .isEmpty()) {
            throw new ValidationException("No completed bookings of item " + itemId + " by user" + userId);
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntryUnknownException("No user with id = " + userId));

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItemId(itemId);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }



    private List<CommentDto> getCommentDtoByItemId(long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }


    private ItemDto setBookingInfo(ItemDto itemDto) {

        if (itemDto == null) {
            return null;
        }

        Booking lastBooking = bookingRepository
                .findFirstByItemIdAndStartBeforeOrderByStartDesc(itemDto.getId(), LocalDateTime.now())
                .orElse(null);

        Booking nextBooking = bookingRepository
                .findFirstByItemIdAndStartAfterOrderByStartAsc(itemDto.getId(), LocalDateTime.now())
                .orElse(null);

        itemDto.setLastBooking(BookingMapper.toBookingDtoForItem(setItemAndBooker(lastBooking)));
        itemDto.setNextBooking(BookingMapper.toBookingDtoForItem(setItemAndBooker(nextBooking)));
        return itemDto;
    }

    private Booking setItemAndBooker(Booking booking) {

        if (booking == null) {
            return null;
        }

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new EntryUnknownException("No item with id = " + booking.getItemId()));
        booking.setItem(item);

        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new EntryUnknownException("No user with id = " + booking.getBookerId()));
        booking.setBooker(booker);

        return booking;
    }

}
