package ru.practicum.shareit.booking;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exceptions.EntryUnknownException;
import ru.practicum.shareit.exceptions.StateValidationException;
import ru.practicum.shareit.exceptions.UserNotItemOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
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
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;


    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {

        validateBookingDto(userId, bookingDto);
        return BookingMapper.toBookingDto(
                bookingRepository.save(
                        mapToBooking(userId, bookingDto)
                )
        );

    }

    @Override
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {

        Booking booking = getBooking(bookingId);

        if (!booking.getItem().getOwnerId().equals(userId)) {
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
        Booking booking = getBooking(bookingId);

        if (!(booking.getItem().getOwnerId().equals(userId) || booking.getBookerId().equals(userId))) {
            throw new EntryUnknownException("The user is not the owner or booker of the item");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> findAllByBookerId(long bookerId, String stateString) {

        validateUserId(bookerId);
        State state = validateState(stateString);
        List<Booking> result;

        switch (state) {
            case PAST:
                result = bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository
                        .findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
                break;
            case CURRENT:
                result = bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId
                                , LocalDateTime.now(), LocalDateTime.now());
                break;
            case REJECTED:
                result = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
                break;
            case WAITING:
                result = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
                break;
            default:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
        }

        return result.stream()
                .map(booking -> {return setItemAndBooker(booking);})
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto>  findAllByOwnerId(long ownerId, String stateString) {
        validateUserId(ownerId);
        State state = validateState(stateString);
        List<Booking> result;

        List<Long> itemIds = itemRepository.findAllByOwnerIdOrderById(ownerId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());


        switch (state) {
            case PAST:
                result = bookingRepository
                        .findAllByItemIdInAndEndBeforeOrderByStartDesc(itemIds, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository
                        .findAllByItemIdInAndStartAfterOrderByStartDesc(itemIds, LocalDateTime.now());
                break;
            case CURRENT:
                result = bookingRepository
                        .findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemIds
                                , LocalDateTime.now(), LocalDateTime.now());
                break;
            case REJECTED:
                result = bookingRepository
                        .findAllByItemIdInAndStatusOrderByStartDesc(itemIds, BookingStatus.REJECTED);
                break;
            case WAITING:
                result = bookingRepository
                        .findAllByItemIdInAndStatusOrderByStartDesc(itemIds, BookingStatus.WAITING);
                break;
            default:
                result = bookingRepository.findAllByItemIdInOrderByStartDesc(itemIds);
        }

        return result.stream()
                .map(booking -> {return setItemAndBooker(booking);})
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    private void validateUserId(long userId) {
        if (userService.findById(userId) == null) {
            throw new ValidationException("User with id" + userId + " doesn't exist");
        }
    }

    private State validateState(String stateString) {
        State state = null;
        try {
            state = State.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new StateValidationException("Unknown state: " + stateString);
        }
        return state;
    }

    private void validateBookingDto(long bookerId, BookingDto bookingDto) {

        validateUserId(bookerId);

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntryUnknownException("No item with id = " + bookingDto.getItemId()));

        if (!item.getAvailable()) {
            throw new ValidationException("Item " + bookingDto.getItemId() + " is not available");
        }

        if (item.getOwnerId() == bookerId) {
            throw new ValidationException("Owner cannot book his/her own items");
        }

        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        LocalDateTime now = LocalDateTime.now();


        if (end.isBefore(start)) {
            throw new ValidationException("End date " + end + " cannot be before start date " + start);
        }


        if (start.isBefore(now)) {
            throw new ValidationException("Start date " + start + " is in a past, now is " + now);
        }

    }

    private Booking setItemAndBooker(Booking booking) {

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new EntryUnknownException("No item with id = " + booking.getItemId()));
        booking.setItem(item);

        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new EntryUnknownException("No user with id = " + booking.getBookerId()));
        booking.setBooker(booker);

        return booking;
    }

    private Booking mapToBooking(long bookerId, BookingDto bookingDto) {

        Booking booking = new Booking();

        booking.setItemId(bookingDto.getItemId());
        booking.setBookerId(bookerId);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        return setItemAndBooker(booking);
    }

    private Booking getBooking(long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntryUnknownException("No booking with id = " + bookingId));

        return setItemAndBooker(booking);
    }

}
