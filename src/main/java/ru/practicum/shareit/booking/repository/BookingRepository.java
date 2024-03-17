package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemOwnerIdAndEndIsBefore(long userId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(long userId, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(long userId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(long userId, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime start, LocalDateTime end,
                                                                 Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime start, LocalDateTime end,
                                                               Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(long userId, StatusBooking status, Sort sort);

    List<Booking> findByBooker_IdAndStatus(long userId, StatusBooking status, Sort sort);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long userId, Sort sort);

    List<Booking> findByBooker_IdOrderByStartDesc(long userId, Sort sort);

    List<Booking> findByItem_Id(long itemId);

    List<Booking> findBookingByItem_IdAndStatus(long itemId, StatusBooking statusBooking);
}
