package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.Min;
import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }


    @PatchMapping("/{bookingId}")
    public BookingDto patchToApprove(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long bookingId, @RequestParam boolean approved) {

        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(value = "from", defaultValue = "0")
                                            @Min(value = 0) int from,
                                            @RequestParam(value = "size", defaultValue = "10")
                                            @Min(value = 1) int size) {
        return bookingService.findAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(value = "from", defaultValue = "0")
                                             @Min(value = 0) int from,
                                             @RequestParam(value = "size", defaultValue = "10")
                                             @Min(value = 1) int size) {
        return bookingService.findAllByOwnerId(ownerId, state, from, size);
    }

}
