package ru.practicum.shareit.booking.service;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.util.exceptions.AccessIsNotAllowedException;
import ru.practicum.shareit.util.exceptions.DateTimeException;
import ru.practicum.shareit.util.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.util.exceptions.UnavailableItemException;
import ru.practicum.shareit.util.exceptions.UnsupportedStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private UserJpaRepository userRepository;
    @Mock
    private ItemJpaRepository itemRepository;
    @Mock
    private BookingJpaRepository bookingRepository;

    @Test
    public void create_whenStartEndAreValid_ItemExists_ItemIsAvailable_UserExists_UserIsNotOwner_invokeSave_returnResult() {

        //  create Owner
        Long ownerId = 1L;
        User owner = User.builder()
                .id(ownerId)
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();

        // create Booker
        Long bookerId = 2L;
        User booker = User.builder()
                .id(bookerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        // create Item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .name("bike")
                .description("new")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        // create input BookingDto with valid start and end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime endAfterStart = start.plusWeeks(1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(endAfterStart)
                .itemId(itemId)
                .build();

        // map input BookingDto to Booking
        Booking booking = BookingMapper.toBooking(bookingDto, booker, item, BookingStatus.WAITING);

        // create expected after saving Booking and BookingOutDto to return
        Long bookingId = 1L;
        Booking savedBooking = booking.toBuilder().id(bookingId).build();

        BookingResponseDto expectedBooking = BookingMapper.toBookingResponseDto(savedBooking);

        //mock repository answers
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(booking)).thenReturn(savedBooking);

        //invoke tested method
        BookingResponseDto result = bookingService.create(bookerId, bookingDto);

        //verify repositories' invokes and their order

        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository);
        inOrder.verify(itemRepository).findById(itemId);
        inOrder.verify(userRepository).findById(bookerId);
        inOrder.verify(bookingRepository).save(booking);

        //test result
        assertEquals(result, expectedBooking);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", bookingId)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", endAfterStart)
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrPropertyWithValue("item.id", itemId)
                .hasFieldOrPropertyWithValue("item.name", "bike")
                .hasFieldOrPropertyWithValue("item.description", "new")
                .hasFieldOrPropertyWithValue("item.available", true)
                .hasFieldOrPropertyWithValue("item.request", null)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("booker.id", bookerId)
                .hasFieldOrPropertyWithValue("booker.name", "CustomerName")
                .hasFieldOrPropertyWithValue("booker.email", "CustomerName@yandex.ru")
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING);

    }

    @Test
    public void create_whenStartEqualsEnd_thenThrowsIncorrectTimeException_doesNotInvokeAnyMore() {

        // create bookerId;
        Long bookerId = 2L;

        // create input BookingDto with invalid start equals end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(start)
                .build();

        //invoke tested method to check throws
        assertThrows(DateTimeException.class,
                () -> bookingService.create(bookerId, bookingDto),
                "Указаны некорректные даты начала и/или конца бронирования");

        //verify repositories' do not invoke
        verifyNoInteractions(itemRepository, userRepository, bookingRepository);

    }

    @Test
    public void create_whenEndIsBeforeStart_thenThrowsIncorrectTimeException_doesNotInvokeAnyMore() {

        // create bookerId;
        Long bookerId = 2L;

        // create input BookingDto with invalid start equals end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime endBeforeStart = start.minusYears(1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(endBeforeStart)
                .build();

        //invoke tested method to check throws
        assertThrows(DateTimeException.class,
                () -> bookingService.create(bookerId, bookingDto),
                "Указаны некорректные даты начала и/или конца бронирования");

        //verify repositories' do not invoke
        verifyNoInteractions(itemRepository, userRepository, bookingRepository);

    }

    @Test
    public void create_whenStartEndAreValid_ItemDoesNotExist_thenThrowsObjectNotFound_doesNotInvokeAnyMore() {

        // create bookerId;
        Long bookerId = 2L;

        // create itemId
        Long itemId = 1L;

        // create input BookingDto with valid start and end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime endAfterStart = start.plusWeeks(1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(endAfterStart)
                .itemId(itemId)
                .build();

        //mock repository answers
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        //invoke tested method to check throws
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(bookerId, bookingDto),
                String.format("Вещи с id %d не существует", itemId));

        //verify repositories' invokes
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(userRepository, bookingRepository);
    }

    @Test
    public void create_whenStartEndAreValid_ItemExistButNotAvailable_thenThrowsUnavailableItem_DoesNotInvokeAnyMore() {

        // create bookerId;
        Long bookerId = 2L;

        // create item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .available(false)
                .build();

        //mock repository answers
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // create input BookingDto with valid start and end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime endAfterStart = start.plusWeeks(1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(endAfterStart)
                .itemId(itemId)
                .build();

        //invoke tested method to check throws
        assertThrows(UnavailableItemException.class,
                () -> bookingService.create(bookerId, bookingDto),
                "В настоящий момент вещь недоступна для бронирования.");

        //verify repositories' invokes
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(userRepository, bookingRepository);

    }

    @Test
    public void create_whenStartEndAreValid_ItemExistAndAvailable_UserDoesNotExists_thenThrowsObjectNotFound_doesNotInvoke() {

        // create bookerId;
        Long bookerId = 2L;

        // create item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .available(true)
                .build();

        //mock repository answers
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        // create input BookingDto with valid start and end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime endAfterStart = start.plusWeeks(1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(endAfterStart)
                .itemId(itemId)
                .build();

        //invoke tested method to check throws
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(bookerId, bookingDto),
                String.format("Пользователя с id %d не существует", bookerId));

        //verify repositories' invokes
        verify(itemRepository).findById(itemId);
        verify(userRepository).findById(bookerId);
        verifyNoInteractions(bookingRepository);

    }

    @Test
    public void create_whenStartEndAreValid_ItemExistsAndAvailable_UserExistsButIsOwner_thenThrowsAccessIsNotAllowed() {

        //  create Owner
        Long ownerId = 1L;
        User owner = User.builder()
                .id(ownerId)
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();

        // create item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .owner(owner)
                .available(true)
                .build();

        //mock repository answers
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        // create input BookingDto with valid start and end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime endAfterStart = start.plusWeeks(1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(endAfterStart)
                .itemId(itemId)
                .build();

        //invoke tested method to check throws
        assertThrows(AccessIsNotAllowedException.class,
                () -> bookingService.create(ownerId, bookingDto),
                "Объект не найден среди доступных для бронирования: "
                        + "владелец не может забронировать свою вещь.");

        //verify repositories' invokes
        verify(itemRepository).findById(itemId);
        verify(userRepository).findById(ownerId);
        verifyNoInteractions(bookingRepository);

    }

    @Test
    public void getById_whenBookingExists_whenUserIsOwner_thenReturnBooking() {

        //  create Owner
        Long ownerId = 1L;
        User owner = User.builder()
                .id(ownerId)
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();

        // create Booker
        Long bookerId = 2L;
        User booker = User.builder()
                .id(bookerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        // create Item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .name("bike")
                .description("new")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        // create Booking
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime end = start.plusWeeks(1);

        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();


        // create expected BookingOutDto to return
        BookingResponseDto expectedBooking = BookingMapper.toBookingResponseDto(booking);

        //mock repository answers
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        //invoke tested method
        BookingResponseDto result = bookingService.getById(ownerId, bookingId);

        //verify repository's invoke
        verify(bookingRepository).findById(bookingId);

        //test result
        assertEquals(result, expectedBooking);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", bookingId)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrPropertyWithValue("item.id", itemId)
                .hasFieldOrPropertyWithValue("item.name", "bike")
                .hasFieldOrPropertyWithValue("item.description", "new")
                .hasFieldOrPropertyWithValue("item.available", true)
                .hasFieldOrPropertyWithValue("item.request", null)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("booker.id", bookerId)
                .hasFieldOrPropertyWithValue("booker.name", "CustomerName")
                .hasFieldOrPropertyWithValue("booker.email", "CustomerName@yandex.ru")
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING);

    }

    @Test
    public void getById_whenBookingExists_whenUserIsBooker_thenReturnBooking() {

        //  create Owner
        Long ownerId = 1L;
        User owner = User.builder()
                .id(ownerId)
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();

        // create Booker
        Long bookerId = 2L;
        User booker = User.builder()
                .id(bookerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        // create Item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .name("bike")
                .description("new")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        // create Booking
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime end = start.plusWeeks(1);

        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();


        // create expected BookingOutDto to return
        BookingResponseDto expectedBooking = BookingMapper.toBookingResponseDto(booking);

        //mock repository answers
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        //invoke tested method
        BookingResponseDto result = bookingService.getById(bookerId, bookingId);

        //verify repository's invoke
        verify(bookingRepository).findById(bookingId);

        //test result
        assertEquals(result, expectedBooking);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", bookingId)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrPropertyWithValue("item.id", itemId)
                .hasFieldOrPropertyWithValue("item.name", "bike")
                .hasFieldOrPropertyWithValue("item.description", "new")
                .hasFieldOrPropertyWithValue("item.available", true)
                .hasFieldOrPropertyWithValue("item.request", null)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("booker.id", bookerId)
                .hasFieldOrPropertyWithValue("booker.name", "CustomerName")
                .hasFieldOrPropertyWithValue("booker.email", "CustomerName@yandex.ru")
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING);

    }

    @Test
    public void getById_whenBookingExists_whenUserIsNotBookerOrOwner_thenThrowsAccessIsNotAllowed() {

        //  create Owner
        Long ownerId = 1L;
        User owner = User.builder()
                .id(ownerId)
                .build();

        // create Booker
        Long bookerId = 2L;
        User booker = User.builder()
                .id(bookerId)
                .build();

        // create notAllowedUserId
        Long notAllowedUserId = 3L;

        // create Item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .owner(owner)
                .build();

        // create Booking
        Long bookingId = 1L;

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .build();

        //mock repository answers
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        //invoke tested method to check throws
        assertThrows(AccessIsNotAllowedException.class,
                () -> bookingService.getById(notAllowedUserId, bookingId),
                String.format("У вас нет доступа к операции получения информации о брони с id %d. "
                        + "Доступ возможен только для инициатора брони, либо владельца вещи", booking.getId()));

        //verify repository's invoke
        verify(bookingRepository).findById(bookingId);

    }

    @Test
    public void updateStatus_whenUserExists_bookingFound_userIsOwner_statusIsWaiting_saveAndReturnApproved() {

        //  create Owner
        Long ownerId = 1L;
        User owner = User.builder()
                .id(ownerId)
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();

        // create Booker
        Long bookerId = 2L;
        User booker = User.builder()
                .id(bookerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        // create Item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .name("bike")
                .description("new")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        // create Booking
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime end = start.plusWeeks(1);

        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        // create expected BookingOutDto to return
        Boolean approved = true;
        Booking approvedBooking = booking.toBuilder()
                .status(BookingStatus.APPROVED)
                .build();
        BookingResponseDto expectedBooking = BookingMapper.toBookingResponseDto(approvedBooking);

        //mock repository answers
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        //invoke tested method
        BookingResponseDto result = bookingService.updateStatus(bookingId, ownerId, approved);

        //verify repositories' invokes and their order

        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository).existsById(ownerId);
        inOrder.verify(bookingRepository).findById(bookingId);
        inOrder.verify(bookingRepository).save(approvedBooking);

        //test result
        assertEquals(result, expectedBooking);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", bookingId)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrPropertyWithValue("item.id", itemId)
                .hasFieldOrPropertyWithValue("item.name", "bike")
                .hasFieldOrPropertyWithValue("item.description", "new")
                .hasFieldOrPropertyWithValue("item.available", true)
                .hasFieldOrPropertyWithValue("item.request", null)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("booker.id", bookerId)
                .hasFieldOrPropertyWithValue("booker.name", "CustomerName")
                .hasFieldOrPropertyWithValue("booker.email", "CustomerName@yandex.ru")
                .hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);

    }

    @Test
    public void updateStatus_whenUserExists_bookingFound_userIsOwner_statusIsWaiting_saveAndReturnRejected() {

        //  create Owner
        Long ownerId = 1L;
        User owner = User.builder()
                .id(ownerId)
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();

        // create Booker
        Long bookerId = 2L;
        User booker = User.builder()
                .id(bookerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        // create Item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .name("bike")
                .description("new")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        // create Booking
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime end = start.plusWeeks(1);

        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        // create expected BookingOutDto to return
        Boolean approved = false;
        Booking rejectedBooking = booking.toBuilder()
                .status(BookingStatus.REJECTED)
                .build();
        BookingResponseDto expectedBooking = BookingMapper.toBookingResponseDto(rejectedBooking);

        //mock repository answers
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        //invoke tested method
        BookingResponseDto result = bookingService.updateStatus(bookingId, ownerId, approved);

        //verify repositories' invokes and their order

        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository).existsById(ownerId);
        inOrder.verify(bookingRepository).findById(bookingId);
        inOrder.verify(bookingRepository).save(rejectedBooking);

        //test result
        assertEquals(result, expectedBooking);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", bookingId)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrPropertyWithValue("item.id", itemId)
                .hasFieldOrPropertyWithValue("item.name", "bike")
                .hasFieldOrPropertyWithValue("item.description", "new")
                .hasFieldOrPropertyWithValue("item.available", true)
                .hasFieldOrPropertyWithValue("item.request", null)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("booker.id", bookerId)
                .hasFieldOrPropertyWithValue("booker.name", "CustomerName")
                .hasFieldOrPropertyWithValue("booker.email", "CustomerName@yandex.ru")
                .hasFieldOrPropertyWithValue("status", BookingStatus.REJECTED);

    }

    @Test
    public void updateStatus_whenUserDoesNotExists_thenThrowsObjectNotFound_DoesNotInvokeAnyMore() {

        //  create userId
        Long userId = 1L;

        // create bookingId
        Long bookingId = 1L;

        // create approved
        Boolean approved = false;

        //mock repository answers
        when(userRepository.existsById(userId)).thenReturn(false);

        //invoke tested method to check throws
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.updateStatus(bookingId, userId, approved),
                String.format("Пользователя с id %d не существует", userId));


        //verify repositories' invokes and their order
        verify(userRepository).existsById(userId);
        verifyNoInteractions(bookingRepository);

    }

    @Test
    public void updateStatus_whenUserExists_BookingDoesNotExist_thenThrowsObjectNotFound_DoesNotInvokeAnyMore() {

        //  create userId
        Long userId = 1L;

        // create bookingId
        Long bookingId = 1L;

        // create approved
        Boolean approved = false;

        //mock repository answers
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        //invoke tested method to check throws
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.updateStatus(bookingId, userId, approved),
                String.format("Бронирования с id %d не существует", bookingId));

        //verify repositories' invokes
        verify(userRepository).existsById(userId);
        verify(bookingRepository).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void updateStatus_whenUserExists_BookingExists_UserIsNotOwner_thenThrowsAccessIsNotAllowed() {

        //  create Owner
        Long ownerId = 1L;
        User owner = User.builder()
                .id(ownerId)
                .build();

        // create Booker
        Long bookerId = 2L;
        User booker = User.builder()
                .id(bookerId)
                .build();

        // create Item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .owner(owner)
                .build();

        // create Booking
        Long bookingId = 1L;
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        // create approved
        Boolean approved = true;

        //mock repository answers
        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of((booking)));

        //invoke tested method to check throws
        assertThrows(AccessIsNotAllowedException.class,
                () -> bookingService.updateStatus(bookingId, bookerId, approved),
                String.format("Операция доступна только владельцу вещи %s :", item));

        //verify repositories' invokes
        verify(userRepository).existsById(bookerId);
        verify(bookingRepository).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void updateStatus_whenUserExists_BookingExists_UserIsOwner_StatusIsNotWaiting_thenThrowsUnavailableItem() {

        //  create Owner
        Long ownerId = 1L;
        User owner = User.builder()
                .id(ownerId)
                .build();

        // create Booker
        Long bookerId = 2L;
        User booker = User.builder()
                .id(bookerId)
                .build();

        // create Item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .owner(owner)
                .build();

        // create Booking
        Long bookingId = 1L;
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        // create approved
        Boolean approved = false;

        //mock repository answers
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of((booking)));

        //invoke tested method to check throws
        assertThrows(UnavailableItemException.class,
                () -> bookingService.updateStatus(bookingId, ownerId, approved),
                String.format("Вы не можете изменить ранее подтвержденный статус %s", booking.getStatus()));

        //verify repositories' invokes
        verify(userRepository).existsById(ownerId);
        verify(bookingRepository).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void getListByOwner_whenUserExists_whenStateIsAll_invokeAppropriateMethod_andReturnResult() {

        //create ownerId
        Long ownerId = 1L;

        //create state
        String state = "ALL";

        //create parameters of page
        int from = 1;
        int size = 1;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.APPROVED)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_Id(ownerId, page)).thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByOwner(ownerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(ownerId);
        inOrder.verify(bookingRepository).findAllByItem_Owner_Id(ownerId, page);

        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.REJECTED);

    }

    @Test
    public void getListByOwner_whenUserExists_whenStateIsCurrent_invokeAppropriateMethod_andReturnResult() {

        //create ownerId
        Long ownerId = 1L;

        //create state
        String state = "CURRENT";

        //create parameters of page
        int from = 10;
        int size = 2;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.APPROVED)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(eq(ownerId),
                any(), any(), eq(page))).thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByOwner(ownerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(ownerId);
        inOrder.verify(bookingRepository).findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                eq(ownerId), any(), any(), eq(page));
        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.REJECTED);

    }

    @Test
    public void getListByOwner_whenUserExists_whenStateIsPast_invokeAppropriateMethod_andReturnResult() {

        //create ownerId
        Long ownerId = 1L;

        //create state
        String state = "PAST";

        //create parameters of page
        int from = 10;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.APPROVED)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(eq(ownerId),
                any(), eq(page))).thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByOwner(ownerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(ownerId);
        inOrder.verify(bookingRepository).findAllByItem_Owner_IdAndEndIsBefore(
                eq(ownerId), any(), eq(page));
        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.REJECTED);

    }

    @Test
    public void getListByOwner_whenUserExists_whenStateIsFuture_invokeAppropriateMethod_andReturnResult() {

        //create ownerId
        Long ownerId = 1L;

        //create state
        String state = "FUTURE";

        //create parameters of page
        int from = 10;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.APPROVED)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(eq(ownerId),
                any(), eq(page))).thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByOwner(ownerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(ownerId);
        inOrder.verify(bookingRepository).findAllByItem_Owner_IdAndStartIsAfter(
                eq(ownerId), any(), eq(page));
        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.REJECTED);

    }

    @Test
    public void getListByOwner_whenUserExists_whenStateIsRejected_invokeAppropriateMethod_andReturnResult() {

        //create ownerId
        Long ownerId = 1L;

        //create state
        String state = "REJECTED";

        //create status
        List<BookingStatus> notApprovedStatus = List.of(BookingStatus.REJECTED, BookingStatus.CANCELED);

        //create parameters of page
        int from = 10;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.REJECTED)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStatusIn(ownerId, notApprovedStatus, page))
                .thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByOwner(ownerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(ownerId);
        inOrder.verify(bookingRepository)
                .findAllByItem_Owner_IdAndStatusIn(ownerId, notApprovedStatus, page);
        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.REJECTED);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.REJECTED);

    }

    @Test
    public void getListByOwner_whenUserExists_whenStateIsWaiting_invokeAppropriateMethod_andReturnResult() {

        //create ownerId
        Long ownerId = 1L;

        //create state
        String state = "WAITING";

        //create status
        BookingStatus status = BookingStatus.WAITING;

        //create parameters of page
        int from = 10;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.WAITING)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.WAITING)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStatus(ownerId, status, page))
                .thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByOwner(ownerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(ownerId);
        inOrder.verify(bookingRepository)
                .findAllByItem_Owner_IdAndStatus(ownerId, status, page);
        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.WAITING);

    }

    @Test
    public void getListByOwner_whenUserDoesNotExist_thenThrowsObjectNotFound_doesNotInvokeAnyMore() {

        //create ownerId
        Long ownerId = 1L;

        //create state
        String state = "WAITING";

        //create parameters of page
        int from = 0;
        int size = 10;

        //mock repository answer
        when(userRepository.existsById(ownerId)).thenReturn(false);

        //invoke tested method to check throws
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getListByOwner(ownerId, state, from, size),
                String.format("Пользователя с id %d не существует", ownerId));

        //verify repositories' invokes

        verify(userRepository).existsById(ownerId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    public void getListByOwner_whenUserExist_butStateIsNotValid_doesNotInvokeAnyMore() {

        //create ownerId
        Long ownerId = 1L;

        //create state
        String state = "INVALID";

        //create parameters of page
        int from = 0;
        int size = 10;

        //mock repository answer
        when(userRepository.existsById(ownerId)).thenReturn(true);

        //invoke tested method to check throws
        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getListByOwner(ownerId, state, from, size),
                "Unknown state: UNSUPPORTED_STATUS");

        //verify repositories' invokes
        verify(userRepository).existsById(ownerId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    public void getListByBooker_whenUserExists_whenStateIsAll_invokeAppropriateMethod_andReturnResult() {

        //create bookerId
        Long bookerId = 1L;

        //create state
        String state = "ALL";

        //create parameters of page
        int from = 10;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.APPROVED)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findAllByBookerId(bookerId, page)).thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByBooker(bookerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(bookerId);
        inOrder.verify(bookingRepository).findAllByBookerId(bookerId, page);

        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.REJECTED);

    }

    @Test
    public void getListByBooker_whenUserExists_whenStateIsCurrent_invokeAppropriateMethod_andReturnResult() {

        //create bookerId
        Long bookerId = 1L;

        //create state
        String state = "CURRENT";

        //create parameters of page
        int from = 10;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.APPROVED)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(eq(bookerId),
                any(), any(), eq(page))).thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByBooker(bookerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(bookerId);
        inOrder.verify(bookingRepository).findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                eq(bookerId), any(), any(), eq(page));
        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.REJECTED);

    }

    @Test
    public void getListByBooker_whenUserExists_whenStateIsPast_invokeAppropriateMethod_andReturnResult() {

        //create bookerId
        Long bookerId = 1L;

        //create state
        String state = "PAST";

        //create parameters of page
        int from = 10;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.APPROVED)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndEndIsBefore(eq(bookerId),
                any(), eq(page))).thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByBooker(bookerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(bookerId);
        inOrder.verify(bookingRepository).findAllByBookerIdAndEndIsBefore(
                eq(bookerId), any(), eq(page));
        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.REJECTED);

    }

    @Test
    public void getListByBooker_whenUserExists_whenStateIsFuture_invokeAppropriateMethod_andReturnResult() {

        //create bookerId
        Long bookerId = 1L;

        //create state
        String state = "FUTURE";

        //create parameters of page
        int from = 10;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.APPROVED)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStartIsAfter(eq(bookerId),
                any(), eq(page))).thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByBooker(bookerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(bookerId);
        inOrder.verify(bookingRepository).findAllByBookerIdAndStartIsAfter(
                eq(bookerId), any(), eq(page));
        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.REJECTED);

    }

    @Test
    public void getListByBooker_whenUserExists_whenStateIsRejected_invokeAppropriateMethod_andReturnResult() {

        //create bookerId
        Long bookerId = 1L;

        //create state
        String state = "REJECTED";

        //create status
        List<BookingStatus> notApprovedStatus = List.of(BookingStatus.REJECTED, BookingStatus.CANCELED);

        //create parameters of page
        int from = 10;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.REJECTED)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatusIn(bookerId, notApprovedStatus, page))
                .thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByBooker(bookerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(bookerId);
        inOrder.verify(bookingRepository)
                .findAllByBookerIdAndStatusIn(bookerId, notApprovedStatus, page);
        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.REJECTED);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.REJECTED);

    }

    @Test
    public void getListByBooker_whenUserExists_whenStateIsWaiting_invokeAppropriateMethod_andReturnResult() {

        //create bookerId
        Long bookerId = 1L;

        //create state
        String state = "WAITING";

        //create status
        BookingStatus status = BookingStatus.WAITING;

        //create parameters of page
        int from = 10;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        // create list of bookings
        Long booking1Id = 1L;
        Booking booking1 = Booking.builder()
                .id(booking1Id)
                .status(BookingStatus.WAITING)
                .build();
        Long booking2Id = 2L;
        Booking booking2 = Booking.builder()
                .id(booking2Id)
                .status(BookingStatus.WAITING)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        //create expected list of BookingOutDto
        List<BookingResponseDto> expectedList = BookingMapper.toBookingResponseDtoList(bookings);

        //mock repository answer
        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatus(bookerId, status, page))
                .thenReturn(bookings);

        //invoke tested method
        List<BookingResponseDto> result = bookingService.getListByBooker(bookerId, state, from, size);

        //verify repositories' invokes and their order
        InOrder inOrder = inOrder(userRepository, bookingRepository);

        inOrder.verify(userRepository).existsById(bookerId);
        inOrder.verify(bookingRepository)
                .findAllByBookerIdAndStatus(bookerId, status, page);
        //check result
        assertEquals(result, expectedList);

        assertThat(result).asList()
                .hasSize(2)
                .hasOnlyElementsOfType(BookingResponseDto.class);

        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking1)));
        MatcherAssert.assertThat(result, hasItem(BookingMapper.toBookingResponseDto(booking2)));

        assertEquals(result.get(0).getId(), booking1Id);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(result.get(1).getId(), booking2Id);
        assertEquals(result.get(1).getStatus(), BookingStatus.WAITING);

    }

    @Test
    public void getListByBooker_whenUserDoesNotExist_thenThrowsObjectNotFound_doesNotInvokeAnyMore() {

        //create bookerId
        Long bookerId = 1L;

        //create state
        String state = "WAITING";

        //create parameters of page
        int from = 0;
        int size = 10;

        //mock repository answer
        when(userRepository.existsById(bookerId)).thenReturn(false);

        //invoke tested method to check throws
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getListByBooker(bookerId, state, from, size),
                String.format("Пользователя с id %d не существует", bookerId));

        //verify repositories' invokes

        verify(userRepository).existsById(bookerId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    public void getListByBooker_whenUserExist_butStateIsNotValid_doesNotInvokeAnyMore() {

        //create bookerId
        Long bookerId = 1L;

        //create state
        String state = "INVALID";

        //create parameters of page
        int from = 0;
        int size = 10;

        //mock repository answer
        when(userRepository.existsById(bookerId)).thenReturn(true);

        //invoke tested method to check throws
        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getListByBooker(bookerId, state, from, size),
                "Unknown state: UNSUPPORTED_STATUS");

        //verify repositories' invokes
        verify(userRepository).existsById(bookerId);
        verifyNoInteractions(bookingRepository);
    }
}
