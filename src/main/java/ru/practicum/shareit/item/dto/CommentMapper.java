package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getCommentId())
                .author(UserMapper.toDto(comment.getAuthor()))
                .authorId(comment.getAuthor().getUserId())
                .authorName(comment.getAuthor().getName())
                .rating(comment.getRating())
                .text(comment.getText())
                .created(comment.getCreatedAt())
                .item(ItemMapper.toDto(comment.getItem()))
                .itemId(comment.getItem().getItemId())
                .build();
    }

    public static Comment toComment(CommentDto comment, User user, Item item) {
        return Comment.builder()
                .commentId(comment.getId())
                .author(user)
                .rating(comment.getRating())
                .text(comment.getText())
                .createdAt(comment.getCreated())
                .item(item)
                .build();
    }

    public static List<CommentDto> toDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
