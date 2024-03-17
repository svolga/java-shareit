package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.exception.OwnerNotFoundException;
import ru.practicum.shareit.booking.exception.UnsupportedStatusException;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.exception.BookingDateRangeException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.dto.AdvanceInfo;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.ValidateDto;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    @Override
    public BookingOutDto create(long userId, BookingDto bookingDto) {
        ValidateDto.validate(bookingDto, AdvanceInfo.class);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Не найден пользователь с id = " + userId));

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new ItemNotFoundException("не найден Item с id = " + bookingDto.getItemId()));

        if (!item.isAvailable()){
            throw new ItemNotAvailableException("не доступен для бронирования Item с id = " + bookingDto.getItemId());
        }

        if (item.getOwner().getId() == userId) {
            throw new ItemNotFoundException("Owner не бронирует item");
        }

        if (!bookingDto.validate()) {
            throw new BookingDateRangeException("Date start должна быть < end");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setStatus(StatusBooking.WAITING);
        booking.setItem(item);
        booking.setBooker(user);

        BookingOutDto bookingOutDto = BookingMapper.toBookingOutDto(bookingRepository.save(booking));
        log.info("Запрос от пользователя --> {}, bookingDto --> {}, создан bookingOutDto --> {}", userId, bookingDto, bookingOutDto);
        return bookingOutDto;
    }

    @Transactional
    @Override
    public BookingOutDto updateApprovedStatus(long userId, long id, boolean approved) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Не найден пользователь с id = " + userId));

        Booking booking = findById(id);

        log.info("Найден booking: " + booking.toString());
        log.info("Найден Item: " + booking.getItem().toString());

        if (booking.getItem().getOwner().getId() != userId) {
            throw new OwnerNotFoundException("Id владельца вещи не совпадает с переданным userId --> " + userId);
        }
        if (booking.getStatus() != StatusBooking.WAITING) {
            throw new ValidateException("Нельзя изменить статус для booking --> " + booking.getStatus());
        }
        booking.setStatus(approved ? StatusBooking.APPROVED : StatusBooking.REJECTED);
        return BookingMapper.toBookingOutDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutDto findBooking(long userId, long bookingId) throws EntityNotFoundException {
        Booking booking = findById(bookingId);

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new EntityNotFoundException("Бронировать может только владелец или автор");
        }

        return BookingMapper.toBookingOutDto(booking);
    }

    @Override
    public Booking findById(long bookingId) throws BookingNotFoundException {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Не найден booking с id = " + bookingId));
    }

    @Override
    public List<BookingOutDto> findBookingsByOwner(long userId, String state) {
        StateBooking stateBooking = findStateBooking(state);
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Не найден пользователь с id = " + userId));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (stateBooking) {
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, now, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                        now, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, StatusBooking.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, StatusBooking.REJECTED, sort);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, sort);
                break;
        }

        if (bookings == null) {
            throw new BookingNotFoundException("Не найдены bookings для userId = " + userId);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<BookingOutDto> findBookingsByUser(long userId, String state) {
        StateBooking stateBooking = findStateBooking(state);
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Не найден пользователь с id = " + userId));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (stateBooking) {
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, now, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId,
                        now, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, StatusBooking.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, StatusBooking.REJECTED, sort);
                break;
            default:
                bookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId, sort);
                break;
        }

        if (bookings == null) {
            throw new BookingNotFoundException("Не найдены bookings для userId = " + userId);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toUnmodifiableList());

    }

    private StateBooking findStateBooking(String state) {
        return Arrays.stream(StateBooking.values())
                .filter(stateBooking -> stateBooking.name().equals(state))
                .findFirst()
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + state));
    }

}
