package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerId(Long id, Pageable pageable);

    Page<Booking> findByBookerIdAndEndBefore(Long id, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndStartAfter(Long id, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long id,
                                                          LocalDateTime start,
                                                          LocalDateTime end,
                                                          Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(long id, BookingStatus status, Pageable pageable);


    Page<Booking> findByItemIdInOrderByStartDesc(List<Long> itemIds, Pageable pageable);


    Page<Booking> findByItemIdInAndEndBefore(List<Long> itemIds,
                                             LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemIdInAndStartAfter(List<Long> itemIds,
                                                              LocalDateTime start, Pageable pageable);

    Page<Booking> findByItemIdInAndStartBeforeAndEndAfter(List<Long> itemIds,
                                                          LocalDateTime start,
                                                          LocalDateTime end,
                                                          Pageable pageable);

    Page<Booking> findByItemIdInAndStatus(List<Long> itemIds,
                                          BookingStatus status,
                                          Pageable pageable);


    List<Booking> findByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime moment);

    Optional<Booking> findFirstByItemIdAndStartBeforeOrderByStartDesc(long itemId, LocalDateTime moment);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime moment);
}

