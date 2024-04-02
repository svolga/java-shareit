package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingJpaRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime dateTime);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId,
                                                                                      LocalDateTime dateTime1,
                                                                                      LocalDateTime dateTime2);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime);

    List<Booking> findAllByItem_Owner_IdAndStatusInOrderByStartDesc(Long ownerId,
                                                                    List<BookingStatus> notApprovedStatus);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus waiting);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId,
                                                                                 LocalDateTime dateTime, LocalDateTime dateTime2);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStatusInOrderByStartDesc(Long bookerId, List<BookingStatus> bookingStatuses);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus bookingStatus);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsBeforeOrStartEqualsOrderByEndDesc(Long itemId, BookingStatus bookingStatus,
                                                                                            LocalDateTime dateTime1, LocalDateTime dateTime2);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsAfterOrStartEqualsOrderByStart(Long itemId,
                                                                                         BookingStatus bookingStatus,
                                                                                         LocalDateTime dateTime1,
                                                                                         LocalDateTime dateTime2);

    List<Booking> findAllByItem_IdAndBooker_IdAndStatusAndStartIsBefore(Long itemId, Long bookerId,
                                                                        BookingStatus bookingStatus, LocalDateTime dateTime);
}
