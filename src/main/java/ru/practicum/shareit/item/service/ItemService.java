package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.AdvanceInfo;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.ValidateDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository<Item> itemRepository;
    private final UserRepository<User> userRepository;

    private long itemId;

    public Item create(long userId, @Valid ItemDto itemDto) {

        User user = userRepository.findById(userId);
        ValidateDto.validate(itemDto, AdvanceInfo.class);
        Item item = ItemMapper.toItem(itemDto);

        item.setOwner(user);
        item.setId(getNewId());
        return itemRepository.create(item);
    }

    public Item findById(long itemId) {
        return itemRepository.findById(itemId);
    }

    public Item update(long userId, long itemId, @Valid ItemDto itemDto) throws ValidateException {
        Item item = itemRepository.findById(itemId);

        if (item.getOwner().getId() != userId) {
            throw new UserNotFoundException("Ошибка редактирования Item для owner с id = " + item.getOwner().getId() +
                    " от имени другого пользователя с id = " + userId);
        }

        item = ItemMapper.toItem(itemDto, item);
        return itemRepository.update(item);
    }

    public void removeById(long itemId) {
        itemRepository.remove(itemId);
    }

    public List<Item> findAll(long userId) {
        userRepository.findById(userId);
        return itemRepository.getAll(userId);
    }

    public List<Item> findByText(String text) {
        return itemRepository.findByText(text);
    }

    private long getNewId() {
        return ++itemId;
    }

}
