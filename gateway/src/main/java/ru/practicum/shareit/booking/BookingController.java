package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody BookingDto bookingDto) {
        return bookingClient.create(userId, bookingDto);
    }


    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchToApprove(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId, @RequestParam boolean approved) {

        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long bookingId) {
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String stateString,
                                                  @PositiveOrZero @RequestParam(value = "from", defaultValue = "0")
                                                  int from,
                                                  @Positive @RequestParam(value = "size", defaultValue = "10")
                                                  int size) {
        State state = State.validateState(stateString);
        return bookingClient.findAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                   @RequestParam(defaultValue = "ALL") String stateString,
                                                   @PositiveOrZero @RequestParam(value = "from", defaultValue = "0")
                                                   int from,
                                                   @Positive @RequestParam(value = "size", defaultValue = "10")
                                                   int size) {

        State state = State.validateState(stateString);
        return bookingClient.findAllByOwnerId(ownerId, state, from, size);
    }
}
