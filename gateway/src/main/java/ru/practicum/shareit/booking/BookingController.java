package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.validation.Validation;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping()
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
										 @RequestBody @Valid BookingRequestDto bookingRequestDto) {
		Validation.checkDates(bookingRequestDto);
		log.info("GATE POST-request: создание Booking c userId --> {}, bookingRequestDto --> {}", userId, bookingRequestDto);
		return bookingClient.create(userId, bookingRequestDto);
	}

	@GetMapping("{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
		log.info("Get-request: получение Booking по id бронирования --> {}, владелец вещи --> {}", userId, bookingId);
		return bookingClient.getById(userId, bookingId);
	}

	@PatchMapping("{bookingId}")
	public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
										   @PathVariable("bookingId") Long bookingId, @RequestParam("approved") Boolean approved) {
		log.info("PATCH-request: подтверждение бронирования для bookingId --> {}, userId --> {}, статус --> {}",
				bookingId, userId, approved);
		return bookingClient.updateStatus(bookingId, userId, approved);
	}

	@GetMapping("owner")
	public ResponseEntity<Object> getListByOwner(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(value = "state", defaultValue = "ALL") String state,
			@PositiveOrZero @RequestParam(
					name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(
					name = "size", defaultValue = "10") Integer size
	) {
		Validation.checkBookingState(state);
		log.info("GET-запрос: список Bookings для userId владельца: id --> {}, статус Booking --> {}, " +
						"from --> {}, size --> {} ",
				userId, state, from, size);
		return bookingClient.getListByOwner(userId, state, from, size);
	}

	@GetMapping()
	public ResponseEntity<Object> getListByBooker(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(value = "state", defaultValue = "ALL") String state,
			@PositiveOrZero @RequestParam(
					name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(
					name = "size", defaultValue = "10") Integer size
	) {
		Validation.checkBookingState(state);
		log.info("GET-запрос: получить список Bookings для userId --> {}, статус брони --> {}, " +
						"from --> {}, size --> {}",
				userId, state, from, size);
		return bookingClient.getListByBooker(userId, state, from, size);
	}
}