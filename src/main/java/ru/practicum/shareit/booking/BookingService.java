package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(long userId, BookingDto bookingDto);

    BookingDto approveBooking(long userId, long bookingId, boolean approved);

    BookingDto findById(long userId, long bookingId);

    List<BookingDto> findAllByBookerId(long userId, String state, int from, int size);

    List<BookingDto> findAllByOwnerId(long ownerId, String state, int from, int size);
}
