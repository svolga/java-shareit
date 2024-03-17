package ru.practicum.shareit.booking.model;

public enum StateBooking {
    CURRENT, // текущие
    PAST, // завершённые
    FUTURE, // будущие
    WAITING, // ожидающие подтверждения
    REJECTED, // отклонённые
    ALL // все
}
