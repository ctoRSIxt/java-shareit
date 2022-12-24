package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long id);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long id
            , LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long id, BookingStatus status);


    List<Booking> findByItemIdInOrderByStartDesc(List<Long> itemIds);

    List<Booking> findByItemIdInAndEndBeforeOrderByStartDesc(List<Long> itemIds, LocalDateTime end);

    List<Booking> findByItemIdInAndStartAfterOrderByStartDesc(List<Long> itemIds, LocalDateTime start);

    List<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List<Long> itemIds
            , LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemIdInAndStatusOrderByStartDesc(List<Long> itemIds, BookingStatus status);


    List<Booking> findByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime moment);

    Optional<Booking> findFirstByItemIdAndStartBeforeOrderByStartDesc(long itemId, LocalDateTime moment);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime moment);
}

