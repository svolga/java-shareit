package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentRepositoryTest {
    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private ItemJpaRepository itemRepository;
    @Autowired
    private CommentJpaRepository commentRepository;
    private Item item1;
    private Long item1Id;
    private Item item2;
    private Comment comment1ToItem1;
    private Comment comment2ToItem1;
    private Comment comment1ToItem2;

    public CommentRepositoryTest() {
    }

    @BeforeEach
    public void beforeEach() {
        User owner = User.builder()
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        owner = userRepository.save(owner);

        User booker = User.builder()
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();
        booker = userRepository.save(booker);

        item1 = Item.builder()
                .name("bike")
                .description("old")
                .available(true)
                .owner(owner)
                .build();
        item1 = itemRepository.save(item1);
        item1Id = item1.getId();
        item2 = Item.builder()
                .name("pram")
                .description("nEw")
                .available(true)
                .owner(owner)
                .build();
        item2 = itemRepository.save(item2);
        comment1ToItem1 = Comment.builder()
                .text("commentText")
                .author(booker)
                .item(item1)
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(comment1ToItem1);
        comment2ToItem1 = Comment.builder()
                .text("commentText")
                .author(booker)
                .item(item1)
                .created(LocalDateTime.now().minusMonths(1))
                .build();
        commentRepository.save(comment2ToItem1);
        comment1ToItem2 = Comment.builder()
                .text("commentText")
                .author(booker)
                .item(item2)
                .created(LocalDateTime.now().minusMonths(1))
                .build();
        commentRepository.save(comment1ToItem2);
    }

    @Test
    public void findAllByItemId() {

        List<Comment> result = commentRepository.findAllByItemId(item1Id);

        assertThat(result).asList()
                .hasSize(2)
                .contains(comment1ToItem1)
                .contains(comment2ToItem1)
                .doesNotContain(comment1ToItem2);
    }

    @Test
    public void findAllByItemIn() {

        List<Item> items = List.of(item1, item2);
        List<Comment> result = commentRepository.findAllByItemIn(items);

        assertThat(result).asList()
                .hasSize(3)
                .contains(comment1ToItem1)
                .contains(comment2ToItem1)
                .contains(comment1ToItem2);
    }
}