package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "item_requests")
public class ItemRequest {

    @Id
    @GeneratedValue
    private Long itemRequestId;

    @Column
    private String itemName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Column
    private LocalDateTime createdAt;
}
