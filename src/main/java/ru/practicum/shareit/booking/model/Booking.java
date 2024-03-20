package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Table(name = "bookings")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking implements Comparable<Booking> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "start_time")
    @NotNull
    @Future
    private LocalDateTime start;

    @Column(name = "end_time")
    @NotNull
    @Future
    private LocalDateTime end;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private StatusBooking statusBooking;

    @Override
    public int compareTo(Booking booking) {
        return booking.getEnd().compareTo(this.end);
    }
}
