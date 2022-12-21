package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") long userId
                         ,@RequestBody Booking booking) {
        return bookingService.create(userId, booking);
    }


    @PatchMapping("/{bookingId}")
    public Booking patchToApprove(@RequestHeader("X-Sharer-User-Id") long userId
                                 ,@PathVariable long bookingId
                                 ,@RequestParam boolean approved) {

        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking findById(@RequestHeader("X-Sharer-User-Id") long userId
                           ,@PathVariable long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<Booking> findAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId
                                        ,@RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId
                                         ,@RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByOwnerId(ownerId, state);
    }

}
