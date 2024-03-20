package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repostitory.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingMapper.toDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto create(BookingDto bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(bookingDto.getItemId()));

        if (item.getUser() != null) {
            if (item.getUser().getUserId().equals(userId)) {
                throw new BookingCreationException(userId, item.getItemId());
            }
        }

        if (item.getAvailable().equals(false)) {
            throw new ItemNotAvailableException(item.getItemId());
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new BookingStartEndTimeException(bookingDto.getStart(), bookingDto.getEnd());
        }

        Booking booking = bookingRepository.save(toBooking(bookingDto, user, item));
        log.info("Saved: " + booking);

        return toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto remove(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!booking.getUser().getUserId().equals(userId)) {
            throw new ItemNotOwnerException(userId, bookingId);
        }

        bookingRepository.deleteById(bookingId);
        log.info("Booking with id: " + bookingId + " removed");

        return toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!(booking.getItem().getUser().getUserId().equals(userId) || booking.getUser().getUserId().equals(userId))) {
            throw new BookingNotFoundException("User для userId: " + userId + " без доступа для bookingId: " + bookingId);
        }

        log.info("Найден booking: " + booking);
        return toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateApprovedStatus(Long userId, Long bookingId, String approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getStatusBooking().equals(StatusBooking.APPROVED)) {
            throw new BookingAlreadyApprovedException(bookingId);
        }

        if (!booking.getItem().getUser().getUserId().equals(userId)) {
            throw new ItemNotOwnerException(userId, booking.getItem().getItemId());
        }

        if (approved.equals("true")) {
            booking.setStatusBooking(StatusBooking.APPROVED);
        } else {
            booking.setStatusBooking(StatusBooking.REJECTED);
        }

        bookingRepository.save(booking);
        log.info("Статус изменен на --> {}, для bookingId --> {}", approved, bookingId);

        return toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingByCurrentUser(Long userId, String state) {

        StateBooking stateBooking = findStateBooking(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        switch (stateBooking) {
            case ALL:
                bookings = bookingRepository.findAllByUserUserIdOrderByEndDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByUserUserIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findAllByUserUserIdAndEndIsBeforeOrderByEndDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByUserUserIdAndStartIsAfterOrderByEndDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByUserUserIdAndStartIsAfterAndStatusBookingOrderByEndDesc(userId, now, StatusBooking.valueOf(state));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByUserUserIdAndStatusBookingOrderByEndDesc(userId, StatusBooking.valueOf(state));
                break;
            default:
                throw new NotValidStateException(state);
        }
        return toDto(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingByOwner(Long userId, String state) {
        StateBooking stateBooking = findStateBooking(state);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        switch (stateBooking) {
            case ALL:
                bookings = bookingRepository.findAllBookingByOwnerUserId(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemUserUserIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemUserUserIdAndEndIsBeforeOrderByEndDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemUserUserIdAndStartIsAfterOrderByEndDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemUserUserIdAndStartIsAfterAndStatusBookingOrderByEndDesc(userId, now, StatusBooking.valueOf(state));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemUserUserIdAndStatusBookingOrderByEndDesc(userId, StatusBooking.valueOf(state));
                break;
            default:
                throw new NotValidStateException(state);
        }
        return toDto(bookings);
    }

    private StateBooking findStateBooking(String state) {
        return Arrays.stream(StateBooking.values())
                .filter(stateBooking -> stateBooking.name().equals(state))
                .findFirst()
                .orElseThrow(() -> new UnsupportedStateException(state));
    }

}