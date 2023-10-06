package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository<Item> {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Item item) throws ValidateException {
        findById(item.getId());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public void remove(long id) {
        findById(id);
        items.remove(id);
    }

    @Override
    public List<Item> getAll(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Item findById(long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new ItemNotFoundException("Не найден Item с id = " + id);
        }
        return item;
    }

    @Override
    public List<Item> findByText(String text) {
        if (text == null || text.trim().length() == 0) {
            return Collections.emptyList();
        }

        Predicate<Item> isFind = item -> item.isAvailable() && (
                item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()));

        return items.values().stream()
                .filter(isFind)
                .collect(Collectors.toUnmodifiableList());
    }

}
