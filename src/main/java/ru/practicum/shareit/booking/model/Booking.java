package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class Booking {

    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private StatusBooking status;


}
