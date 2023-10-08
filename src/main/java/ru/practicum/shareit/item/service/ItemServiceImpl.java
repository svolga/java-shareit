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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository<Item> itemRepository;
    private final UserRepository<User> userRepository;

    private long itemId;

    @Override
    public ItemDto create(long userId, @Valid ItemDto itemDto) {

        User user = userRepository.findById(userId);
        ValidateDto.validate(itemDto, AdvanceInfo.class);
        Item item = ItemMapper.toItem(itemDto);

        item.setOwner(user);
        item.setId(getNewId());
        return ItemMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public Item findById(long itemId) {
        return itemRepository.findById(itemId);
    }

    @Override
    public ItemDto findItemById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId));
    }

    @Override
    public ItemDto update(long userId, @Valid ItemDto itemDto) throws ValidateException {
        userRepository.findById(userId);
        Item item = itemRepository.findById(itemDto.getId());
        item = ItemMapper.toItem(itemDto, item);

        if (item.getOwner().getId() != userId) {
            throw new UserNotFoundException("Ошибка редактирования Item для owner с id = " + item.getOwner().getId() +
                    " от имени другого пользователя с id = " + userId);
        }

        return ItemMapper.toItemDto(itemRepository.update(item));
    }

    @Override
    public void removeById(long itemId) {
        itemRepository.remove(itemId);
    }

    @Override
    public List<ItemDto> findAll(long userId) {
        userRepository.findById(userId);
        return itemRepository.getAll(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ItemDto> findByText(String text) {
        return itemRepository.findByText(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toUnmodifiableList());
    }

    private long getNewId() {
        return ++itemId;
    }
}
