package ru.practicum.shareit.booking;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exceptions.EntryUnknownException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;


    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {

        validateBookingDto(userId, bookingDto);

        Booking booking = new Booking();

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntryUnknownException("No item with id = " + bookingDto.getItemId()));
        booking.setItem(item);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new EntryUnknownException("No user with id = " + userId));
        booking.setBooker(booker);

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));

    }

    @Override
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntryUnknownException("No booking with id = " + bookingId));

        if (!(booking.getItem().getOwner().getId() == userId)) {
            throw new EntryUnknownException("The user is not the owner of the item");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("You cannot change status of approved booking");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntryUnknownException("No booking with id = " + bookingId));

        if (!(booking.getItem().getOwner().getId() == userId || booking.getBooker().getId() == userId)) {
            throw new EntryUnknownException("The user is not the owner or booker of the item");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> findAllByBookerId(long bookerId, String stateString) {

        validateUserId(bookerId);
        State state = State.validateState(stateString);
        List<Booking> result;

        switch (state) {
            case PAST:
                result = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
                break;
            case CURRENT:
                result = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case REJECTED:
                result = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
                break;
            case WAITING:
                result = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
                break;
            default:
                result = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
        }

        return result.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwnerId(long ownerId, String stateString) {
        validateUserId(ownerId);
        State state = State.validateState(stateString);
        List<Booking> result;

        List<Long> itemIds = itemRepository
                .findAllByOwnerIdOrderById(ownerId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());


        switch (state) {
            case PAST:
                result = bookingRepository
                        .findByItemIdInAndEndBeforeOrderByStartDesc(itemIds, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository
                        .findByItemIdInAndStartAfterOrderByStartDesc(itemIds, LocalDateTime.now());
                break;
            case CURRENT:
                result = bookingRepository
                        .findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemIds,
                                LocalDateTime.now(), LocalDateTime.now());
                break;
            case REJECTED:
                result = bookingRepository
                        .findByItemIdInAndStatusOrderByStartDesc(itemIds, BookingStatus.REJECTED);
                break;
            case WAITING:
                result = bookingRepository
                        .findByItemIdInAndStatusOrderByStartDesc(itemIds, BookingStatus.WAITING);
                break;
            default:
                result = bookingRepository
                        .findByItemIdInOrderByStartDesc(itemIds);
        }

        return result.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    private void validateUserId(long userId) {
        userService.findById(userId);
    }


    private void validateBookingDto(long bookerId, BookingDto bookingDto) {

        validateUserId(bookerId);

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntryUnknownException("No item with id = " + bookingDto.getItemId()));

        if (!item.getAvailable()) {
            throw new ValidationException("Item " +
                    bookingDto.getItemId() + " is not available");
        }

        if (item.getOwner().getId() == bookerId) {
            throw new EntryUnknownException("Owner cannot book his/her own items");
        }


        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("End date " +
                    bookingDto.getEnd() + " cannot be before start date " + bookingDto.getStart());
        }


        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date " +
                    bookingDto.getStart() + " is in a past, now is " + LocalDateTime.now());
        }

    }

}
