package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository<T extends User> {
    T create(T t);

    T update(T t) throws ValidateException;

    void remove(long id);

    List<T> getAll();

    Optional<T> findByEmailAndExcludeId(String email, long id);

    Optional<T> findByEmail(String email);

    T findById(long id);

}
