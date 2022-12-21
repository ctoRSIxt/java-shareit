package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking create(long userId, Booking booking);

    Booking approveBooking(long userId, long bookingId, boolean approved);

    Booking findById(long userId, long bookingId);

    List<Booking>  findAllByUserId(long userId, String state);

    List<Booking>  findAllByOwnerId(long ownerId, String state);
}
