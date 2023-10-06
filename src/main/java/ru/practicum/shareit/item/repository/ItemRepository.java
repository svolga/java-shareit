package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository<T extends Item> {

    T create(T t);

    T update(T t) throws ValidateException;

    void remove(long id);

    List<T> getAll(long userId);

    List<T> findByText(String text);

    T findById(long id);
}
