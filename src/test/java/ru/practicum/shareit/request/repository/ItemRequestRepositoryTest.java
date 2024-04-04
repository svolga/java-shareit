package ru.practicum.shareit.request.repository;

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
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    @Autowired
    private BookingJpaRepository bookingRepository;
    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private ItemJpaRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private Long ownerId;
    private Long requesterId;
    private Pageable page;
    private Pageable pageWithSize1;
    private ItemRequest item1Request;
    private ItemRequest item2Request;

    @BeforeEach
    void beforeEach() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        page = PageRequest.of(0, 10, sort);
        pageWithSize1 = PageRequest.of(0, 1, sort);
        User owner = User.builder()
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        owner = userRepository.save(owner);
        ownerId = owner.getId();
        User requester = User.builder()
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();
        requester = userRepository.save(requester);
        requesterId = requester.getId();
        item1Request = ItemRequest.builder()
                .description("I need bike")
                .requester(requester)
                .created(LocalDateTime.of(2023, 1, 1, 1, 1, 1))
                .build();
        item2Request = ItemRequest.builder()
                .description("I need pram")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
        itemRequestRepository.save(item1Request);
        itemRequestRepository.save(item2Request);
        Item item1 = Item.builder()
                .name("bike")
                .description("new")
                .available(true)
                .owner(owner)
                .request(item1Request)
                .build();
        itemRepository.save(item1);

        Item item2 = Item.builder()
                .name("pram")
                .description("new")
                .available(true)
                .owner(owner)
                .request(item2Request)
                .build();
        itemRepository.save(item2);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc() {

        List<ItemRequest> result = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(requesterId);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(2)
                .contains(item1Request)
                .contains(item2Request)
                .startsWith(item2Request)
                .endsWith(item1Request);

    }

    @Test
    void findAllByRequesterIdIsNotOrderByCreatedDesc() {

        List<ItemRequest> result = itemRequestRepository
                .findAllByRequesterIdIsNot(ownerId, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(2)
                .contains(item1Request)
                .contains(item2Request)
                .endsWith(item1Request);

    }

    @Test
    void findAllByRequesterIdIsNotOrderByCreatedDescOnlyOne() {

        List<ItemRequest> result = itemRequestRepository
                .findAllByRequesterIdIsNot(ownerId, pageWithSize1);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(1);

    }

    @Test
    void findAllByRequesterIdIsNotOrderByCreatedDesc_ReturnEmptyList() {

        List<ItemRequest> result = itemRequestRepository
                .findAllByRequesterIdIsNot(requesterId, page);

        AssertionsForClassTypes.assertThat(result).asList()
                .hasSize(0)
                .doesNotContain(item1Request)
                .doesNotContain(item2Request)
                .isEmpty();

    }
}
