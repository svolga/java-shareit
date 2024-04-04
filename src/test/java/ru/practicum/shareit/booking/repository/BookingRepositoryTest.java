package ru.practicum.shareit.booking.repository;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private BookingJpaRepository bookingRepository;
    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private ItemJpaRepository itemRepository;
    private User owner;
    private Long ownerId;
    private Long bookerId;
    private Long itemId;
    private User booker;
    private Item item;
    private Booking waiting;
    private Booking approved;
    private Booking rejected;
    private Booking current;
    private Booking past;
    private Pageable page;

    @BeforeEach
    public void beforeEach() {
        page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));

        owner = User.builder().name("CustomerName").email("CustomerName@yandex.ru").build();
        owner = userRepository.save(owner);
        ownerId = owner.getId();

        booker = User.builder().name("Alex").email("Alex@yandex.ru").build();
        booker = userRepository.save(booker);
        bookerId = booker.getId();

        item = Item.builder().id(1L).name("bike").description("new").available(true).owner(owner).build();
        item = itemRepository.save(item);
        itemId = item.getId();

        waiting = Booking.builder()
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 2, 1, 1, 1, 1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        approved = waiting.toBuilder()
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 2))
                .status(BookingStatus.APPROVED)
                .build();

        rejected = waiting.toBuilder()
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 0))
                .status(BookingStatus.REJECTED)
                .build();

        current = approved.toBuilder()
                .start(LocalDateTime.of(2023, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2024, 12, 1, 1, 1, 1))
                .build();

        past = approved.toBuilder()
                .start(LocalDateTime.of(2020, 2, 1, 1, 1, 1))
                .end(LocalDateTime.of(2020, 3, 1, 1, 1, 1))
                .build();

        bookingRepository.save(waiting);
        bookingRepository.save(approved);
        bookingRepository.save(rejected);
        bookingRepository.save(past);
        bookingRepository.save(current);
    }

    @Test
    public void findAllByItem_Owner_IdOrderByStartDesc() {

        List<Booking> result = bookingRepository
                .findAllByItem_Owner_Id(ownerId, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(5)
                .contains(waiting)
                .contains(approved)
                .contains(rejected)
                .contains(past)
                .contains(current)
                .startsWith(approved)
                .endsWith(past);
    }

    @Test
    public void findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc() {

        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = bookingRepository
                .findAllByItem_Owner_IdAndEndIsBefore(ownerId, now, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(1)
                .doesNotContain(waiting)
                .contains(past)
                .startsWith(past)
                .endsWith(past);
    }

    @Test
    public void findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = bookingRepository
                .findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(ownerId, now, now, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(1)
                .doesNotContain(past)
                .contains(current)
                .startsWith(current)
                .endsWith(current);
    }

    @Test
    public void findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc() {

        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = bookingRepository
                .findAllByItem_Owner_IdAndStartIsAfter(ownerId, now, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(3)
                .contains(waiting)
                .contains(approved)
                .contains(rejected)
                .startsWith(approved)
                .endsWith(rejected);
    }

    @Test
   public  void findAllByItem_Owner_IdAndStatusInOrderByStartDesc() {

        List<BookingStatus> notApprovedStatus = List.of(BookingStatus.REJECTED, BookingStatus.CANCELED);
        List<Booking> result = bookingRepository
                .findAllByItem_Owner_IdAndStatusIn(ownerId, notApprovedStatus, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(1)
                .contains(rejected)
                .startsWith(rejected)
                .endsWith(rejected);

    }

    @Test
   public void findAllByItem_Owner_IdAndStatusOrderByStartDesc() {
        List<Booking> result = bookingRepository
                .findAllByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(1)
                .contains(waiting)
                .doesNotContain(approved)
                .doesNotContain(rejected)
                .startsWith(waiting)
                .endsWith(waiting);

    }

    @Test
        public   void findAllByBookerIdOrderByStartDesc() {

        List<Booking> result = bookingRepository
                .findAllByBookerId(bookerId, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(5)
                .contains(waiting)
                .contains(approved)
                .contains(rejected)
                .startsWith(approved)
                .endsWith(past);
    }

    @Test
    public void findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {

        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfter(bookerId, now, now, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(1)
                .contains(current)
                .doesNotContain(approved)
                .doesNotContain(rejected)
                .startsWith(current)
                .endsWith(current);
    }

    @Test
    public void findAllByBookerIdAndStartIsAfterOrderByStartDesc() {

        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartIsAfter(bookerId, now, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(3)
                .contains(waiting)
                .contains(approved)
                .contains(rejected)
                .startsWith(approved)
                .endsWith(rejected);
    }

    @Test
    public void findAllByBookerIdAndEndIsBeforeOrderByStartDesc() {

        LocalDateTime now = LocalDateTime.now();

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndEndIsBefore(bookerId, now, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(1)
                .contains(past)
                .startsWith(past)
                .endsWith(past);
    }

    @Test
    public void findAllByBookerIdAndStatusInOrderByStartDesc() {

        List<BookingStatus> notApprovedStatus = List.of(BookingStatus.REJECTED, BookingStatus.CANCELED);
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStatusIn(bookerId, notApprovedStatus, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(1)
                .contains(rejected)
                .startsWith(rejected)
                .endsWith(rejected);
    }

    @Test
    public void findAllByBookerIdAndStatusOrderByStartDesc() {

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(1)
                .contains(waiting)
                .startsWith(waiting)
                .endsWith(waiting);
    }

    @Test
    public void findFirstByItemIdAndStatusAndStartIsBeforeOrStartEqualsOrderByEndDesc() {

        LocalDateTime now = LocalDateTime.now();

        Optional<Booking> lastBooking = bookingRepository
                .findFirstByItemIdAndStatusAndStartIsBeforeOrStartEqualsOrderByEndDesc(itemId,
                        BookingStatus.APPROVED, now, now);

        AssertionsForClassTypes.assertThat(lastBooking).hasValueSatisfying(booking -> assertThat(booking)
                .hasFieldOrPropertyWithValue("id", 5L)
                .hasFieldOrPropertyWithValue("start", current.getStart())
                .hasFieldOrPropertyWithValue("end", current.getEnd())
                .hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED)
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrPropertyWithValue("booker", booker));
    }

    @Test
    public void findFirstByItemIdAndStatusAndStartIsAfterOrStartEqualsOrderByStart() {

        LocalDateTime now = LocalDateTime.now();

        Optional<Booking> lastBooking = bookingRepository
                .findFirstByItemIdAndStatusAndStartIsAfterOrStartEqualsOrderByStart(itemId,
                        BookingStatus.APPROVED, now, now);

        AssertionsForClassTypes.assertThat(lastBooking).hasValueSatisfying(booking -> assertThat(booking)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("start", approved.getStart())
                .hasFieldOrPropertyWithValue("end", approved.getEnd())
                .hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED)
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrPropertyWithValue("booker", booker));
    }

    @Test
    public void findAllByItem_IdAndBooker_IdAndStatusAndStartIsBefore() {

        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = bookingRepository
                .findAllByItem_IdAndBooker_IdAndStatusAndStartIsBefore(itemId, bookerId, BookingStatus.APPROVED, now);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(2)
                .startsWith(past)
                .endsWith(current);
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}
