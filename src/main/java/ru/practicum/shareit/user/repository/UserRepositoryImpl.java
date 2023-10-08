package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository<User> {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(User user) throws ValidateException {
        findById(user.getId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void remove(long id) {
        findById(id);
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User findById(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("Не найден пользователь с id = " + id);
        }
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null)
            return Optional.empty();
        else {
            return users.values().stream()
                    .filter(user -> user.getEmail().equals(email))
                    .findFirst();
        }
    }

    @Override
    public Optional<User> findByEmailAndExcludeId(String email, long id) {
        if (email == null)
            return Optional.empty();
        else {
            return users.values().stream()
                    .filter(user -> user.getId() != id && user.getEmail().equals(email))
                    .findFirst();
        }
    }

}
