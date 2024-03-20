package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.dto.UserDto;


import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommentDto {

    private Long id;
    private UserDto author;
    private ItemDto item;

    private String authorName;
    private Long authorId;
    private int rating;
    @NotBlank
    private String text;
    private LocalDateTime created;
    private Long itemId;
}
