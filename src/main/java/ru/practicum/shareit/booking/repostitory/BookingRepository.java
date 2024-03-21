package ru.practicum.shareit.booking.repostitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemItemId(Long itemId);

    List<Booking> findAllByUserUserIdOrderByEndDesc(Long userId);

    List<Booking> findAllByUserUserIdAndStartIsAfterOrderByEndDesc(Long userId, LocalDateTime time);

    List<Booking> findAllByUserUserIdAndStatusBookingOrderByEndDesc(Long userId, StatusBooking status);

    List<Booking> findAllByItemUserUserIdAndEndIsBeforeOrderByEndDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByItemUserUserIdAndStartIsAfterOrderByEndDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByItemUserUserIdAndStatusBookingOrderByEndDesc(Long userId, StatusBooking status);

    List<Booking> findAllByUserUserIdAndEndIsBeforeOrderByEndDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByUserUserIdAndStartIsAfterAndStatusBookingOrderByEndDesc(Long userId, LocalDateTime dateTime, StatusBooking status);

    List<Booking> findAllByItemUserUserIdAndStartIsAfterAndStatusBookingOrderByEndDesc(Long userId, LocalDateTime dateTime, StatusBooking status);

    List<Booking> findAllByUserUserIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long userId, LocalDateTime dateTime1, LocalDateTime dateTime2);

    List<Booking> findAllByItemUserUserIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long userId, LocalDateTime dateTime1, LocalDateTime dateTime2);

    @Query("SELECT b " +
            "FROM Booking as b " +
            "JOIN b.item AS i " +
            "JOIN i.user AS u " +
            "WHERE u.userId = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllBookingByOwnerUserId(Long ownerId);
}