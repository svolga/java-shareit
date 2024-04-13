package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItem_Owner_Id(Long ownerId, Pageable page);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(
            Long ownerId, LocalDateTime dateTime, Pageable page);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
            Long ownerId, LocalDateTime dateTime1, LocalDateTime dateTime2, Pageable page);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(
            Long ownerId, LocalDateTime dateTime, Pageable page);

    List<Booking> findAllByItem_Owner_IdAndStatusIn(
            Long ownerId, List<BookingStatus> notApprovedStatus, Pageable page);

    List<Booking> findAllByItem_Owner_IdAndStatus(
            Long ownerId, BookingStatus waiting, Pageable page);

    List<Booking> findAllByBookerId(Long bookerId, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
            Long bookerId, LocalDateTime dateTime, LocalDateTime dateTime2, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfter(
            Long bookerId, LocalDateTime dateTime, Pageable page);

    List<Booking> findAllByBookerIdAndEndIsBefore(
            Long bookerId, LocalDateTime dateTime, Pageable page);

    List<Booking> findAllByBookerIdAndStatusIn(
            Long bookerId, List<BookingStatus> bookingStatuses, Pageable page);

    List<Booking> findAllByBookerIdAndStatus(
            Long bookerId, BookingStatus bookingStatus, Pageable page);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsBeforeOrStartEqualsOrderByEndDesc(
            Long itemId, BookingStatus bookingStatus, LocalDateTime dateTime1, LocalDateTime dateTime2);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsAfterOrStartEqualsOrderByStart(
            Long itemId, BookingStatus bookingStatus, LocalDateTime dateTime1, LocalDateTime dateTime2);

    List<Booking> findAllByItem_IdAndBooker_IdAndStatusAndStartIsBefore(
            Long itemId, Long bookerId, BookingStatus bookingStatus, LocalDateTime dateTime);
}