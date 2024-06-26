package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.item.model.Item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.util.exceptions.AccessIsNotAllowedException;
import ru.practicum.shareit.util.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.util.exceptions.UnavailableItemException;
import ru.practicum.shareit.util.exceptions.UnsupportedStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingJpaRepository bookingJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto) {

        try {
            Item item = getItemByIdIfExists(bookingRequestDto.getItemId());
            checkIsItemAvailable(item);
            User owner = getUserByIdIfExists(userId);
            checkAccessForOwnerNotAllowed(item, userId);

            Booking booking = BookingMapper.toBooking(bookingRequestDto, owner, item,
                    BookingStatus.WAITING);
            Booking savedBooking = bookingJpaRepository.save(booking);
            log.info("Создан booking --> {}", savedBooking);
            return BookingMapper.toBookingResponseDto(savedBooking);
        } catch (RuntimeException ex) {
            log.error("!!! Exception --> ", ex.getMessage());
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getById(Long userId, Long bookingId) {
        Booking booking = getBookingByIdIfExists(bookingId);
        checkAccessAllowedOnlyForOwnerOrBooker(booking, userId);

        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(booking);
        log.info("Получить booking --> {} для User --> {}", bookingResponseDto, userId);
        return bookingResponseDto;
    }

    @Override
    @Transactional
    public BookingResponseDto updateStatus(Long bookingId, Long userId, Boolean isApprovedBookingStatus) {
        checkIfUserExists(userId);
        Booking booking = getBookingByIdIfExists(bookingId);
        checkAccessAllowedOnlyForOwner(booking.getItem(), userId);
        checkStatusIsWaiting(booking);

        BookingStatus status = resolveStatus(isApprovedBookingStatus);
        Booking updated = booking.toBuilder().status(status).build();
        bookingJpaRepository.save(updated);

        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(updated);

        log.info("Для Booking --> {} изменен статус --> {}", bookingResponseDto, status);
        return bookingResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getListByOwner(Long ownerId, String state, Integer from, Integer size) {

        checkIfUserExists(ownerId);
        BookingState validState = findBookingState(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> listByOwner;
        int page = from / size;

        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "start"));

        switch (validState) {
            case ALL:
                listByOwner = bookingJpaRepository.findAllByItem_Owner_Id(ownerId, pageRequest);
                break;
            case CURRENT:
                listByOwner = bookingJpaRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                        ownerId, now, now, pageRequest);
                break;
            case PAST:
                listByOwner = bookingJpaRepository.findAllByItem_Owner_IdAndEndIsBefore(ownerId, now, pageRequest);
                break;
            case FUTURE:
                listByOwner = bookingJpaRepository.findAllByItem_Owner_IdAndStartIsAfter(
                        ownerId, now, pageRequest);
                break;
            case REJECTED:
                List<BookingStatus> notApprovedStatus = List.of(BookingStatus.REJECTED, BookingStatus.CANCELED);
                listByOwner = bookingJpaRepository.findAllByItem_Owner_IdAndStatusIn(ownerId,
                        notApprovedStatus, pageRequest);
                break;
            case WAITING:
                listByOwner = bookingJpaRepository.findAllByItem_Owner_IdAndStatus(ownerId,
                        BookingStatus.valueOf("WAITING"), pageRequest);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return BookingMapper.toBookingResponseDtoList(listByOwner);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getListByBooker(Long bookerId, String state, Integer from, Integer size) {
        checkIfUserExists(bookerId);
        BookingState validState = findBookingState(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> listByBooker;
        int page = from / size;
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "start"));

        switch (validState) {
            case ALL:
                listByBooker = bookingJpaRepository.findAllByBookerId(bookerId, pageRequest);
                break;
            case CURRENT:
                listByBooker = bookingJpaRepository
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfter(bookerId, now, now, pageRequest);
                break;
            case PAST:
                listByBooker = bookingJpaRepository.findAllByBookerIdAndEndIsBefore(
                        bookerId, now, pageRequest);
                break;
            case FUTURE:
                listByBooker = bookingJpaRepository.findAllByBookerIdAndStartIsAfter(
                        bookerId, now, pageRequest);
                break;
            case REJECTED:
                List<BookingStatus> notApprovedStatus = List.of(BookingStatus.REJECTED, BookingStatus.CANCELED);
                listByBooker = bookingJpaRepository
                        .findAllByBookerIdAndStatusIn(bookerId, notApprovedStatus, pageRequest);
                break;
            case WAITING:
                listByBooker = bookingJpaRepository
                        .findAllByBookerIdAndStatus(
                                bookerId, BookingStatus.valueOf("WAITING"), pageRequest);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.toBookingResponseDtoList(listByBooker);

    }

    private BookingStatus resolveStatus(Boolean isApproved) {
        return isApproved.equals(true) ? BookingStatus.APPROVED : BookingStatus.REJECTED;
    }

    private Item getItemByIdIfExists(Long itemId) {
        return itemJpaRepository.findById(itemId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Не найден Item с itemId = %d", itemId)));
    }


    private Booking getBookingByIdIfExists(Long bookingId) {
        return bookingJpaRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Не найден Booking для bookingId =  %d", bookingId)));
    }

    private User getUserByIdIfExists(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Пользователя с id %d не существует", userId)));
    }


    private void checkIfUserExists(Long userId) {
        if (!userJpaRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("Не найден User с userId: %d", userId));
        }
    }

    private void checkAccessForOwnerNotAllowed(Item item, Long userId) {
        if (isOwner(item, userId)) {
            throw new AccessIsNotAllowedException(
                    "Item не найден, owner не может бронировать свой Item");
        }
    }

    private void checkAccessAllowedOnlyForOwner(Item item, Long userId) {

        if (!isOwner(item, userId)) {
            throw new AccessIsNotAllowedException(
                    (String.format("Операция доступна только владельцу Item %s :", item)));
        }
    }

    private void checkAccessAllowedOnlyForOwnerOrBooker(Booking booking, Long userId) {
        if (!isOwner(booking.getItem(), userId) && !isBooker(booking, userId)) {
            throw new AccessIsNotAllowedException(
                    (String.format("Получить информацию о Booking для bookingId = %d. "
                            + "может только владелец Booking, либо владелец Item", booking.getId())));
        }
    }

    private boolean isOwner(Item item, Long userId) {
        return item.getOwner().getId().equals(userId);
    }

    private boolean isBooker(Booking booking, Long userId) {
        return booking.getBooker().getId().equals(userId);
    }

    private BookingState findBookingState(String bookingState) {
        try {
            return BookingState.valueOf(bookingState);
        } catch (IllegalArgumentException exception) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void checkIsItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new UnavailableItemException("В настоящий момент вещь недоступна для бронирования.");
        }
    }

    private void checkStatusIsWaiting(Booking booking) {
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new UnavailableItemException(String.format("Вы не можете изменить ранее подтвержденный статус %s",
                    booking.getStatus()));
        }
    }
}