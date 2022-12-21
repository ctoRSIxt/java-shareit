package ru.practicum.shareit.booking;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.EntryUnknownException;
import ru.practicum.shareit.exceptions.UserNotItemOwnerException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;


    @Override
    public Booking create(long userId, Booking booking) {
        booking.setBookerId(userId);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);

    }

    @Override
    public Booking approveBooking(long userId, long bookingId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntryUnknownException("No booking with id = " + bookingId));

        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new UserNotItemOwnerException("The user is not the owner of the item");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return bookingRepository.save(booking);
    }

    @Override
    public Booking findById(long userId, long bookingId) {

    }

    @Override
    public List<Booking> findAllByUserId(long userId, String state) {

    }

    @Override
    public List<Booking>  findAllByOwnerId(long ownerId, String state) {

    }
}
