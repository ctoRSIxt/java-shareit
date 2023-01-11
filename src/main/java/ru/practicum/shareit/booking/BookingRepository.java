package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //        itemRepository.findAllByOwnerIdOrderById(ownerId,
//                PageRequest.of(from / size, size,
//                        Sort.by(Sort.Direction.ASC, "ownerId")))

//                case PAST:
//    result = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
//                break;
//            case FUTURE:
//    result = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
//                break;
//            case CURRENT:
//    result = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now());
//                break;
//            case REJECTED:
//    result = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
//                break;
//            case WAITING:
//    result = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
//                break;
//    default:
//    result = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);

//    Page<Item> findAllByOwnerIdOrderById(Long ownerId, Pageable pageable);


    Page<Booking> findByBookerId(Long id, Pageable pageable);

//    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end);

    Page<Booking> findByBookerIdAndEndBefore(Long id, LocalDateTime end, Pageable pageable);

//    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start);

    Page<Booking> findByBookerIdAndStartAfter(Long id, LocalDateTime start, Pageable pageable);

//    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long id,
//                                                                          LocalDateTime start,
//                                                                          LocalDateTime end);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long id,
                                                          LocalDateTime start,
                                                          LocalDateTime end,
                                                          Pageable pageable);

//    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long id, BookingStatus status);

    Page<Booking> findByBookerIdAndStatus(long id, BookingStatus status, Pageable pageable);


    List<Booking> findByItemIdInOrderByStartDesc(List<Long> itemIds);

    List<Booking> findByItemIdInAndEndBeforeOrderByStartDesc(List<Long> itemIds, LocalDateTime end);

    List<Booking> findByItemIdInAndStartAfterOrderByStartDesc(List<Long> itemIds, LocalDateTime start);

    List<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List<Long> itemIds,
                                                                          LocalDateTime start,
                                                                          LocalDateTime end);

    List<Booking> findByItemIdInAndStatusOrderByStartDesc(List<Long> itemIds, BookingStatus status);


    List<Booking> findByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime moment);

    Optional<Booking> findFirstByItemIdAndStartBeforeOrderByStartDesc(long itemId, LocalDateTime moment);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime moment);
}

