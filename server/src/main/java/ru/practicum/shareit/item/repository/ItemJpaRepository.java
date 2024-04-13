package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(Long userId);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.available = true AND (upper(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    List<Item> searchItemsBySubstring(String text);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequestIn(List<ItemRequest> itemRequests);
}
