package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyInt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    private String header;
    private MediaType jsonType;
    private Long userId;

    @BeforeEach
    void before() {
        userId = 1L;
        header = "X-Sharer-User-Id";
        jsonType = MediaType.APPLICATION_JSON;
    }

    @Test
    @SneakyThrows
    public void create_WhenBookingIsValid_StatusIsOk_AndInvokeService() {

        // create valid start and end time
        LocalDateTime futureStart = LocalDateTime.of(2024, 5, 1, 1, 1, 1);
        LocalDateTime futureEnd = LocalDateTime.of(2024, 10, 1, 1, 1, 1);

        // create User
        User user = User.builder()
                .id(userId)
                .name("CustomerName")
                .build();

        // create Item
        Item item = Item.builder()
                .name("bike")
                .build();


        // create valid input booking: BookingDto
        BookingRequestDto bookingIn = BookingRequestDto.builder()
                .start(futureStart)
                .end(futureEnd)
                .build();

        // create expected out booking: BookingOutDto
        BookingResponseDto bookingOut = BookingMapper.toBookingResponseDto(
                BookingMapper.toBooking(bookingIn, user, item, BookingStatus.WAITING));

        // map input and out booking into strings
        String bookingInString = objectMapper.writeValueAsString(bookingIn);
        String expectedBookingString = objectMapper.writeValueAsString(bookingOut);

        //mock service answer
        when(bookingService.create(userId, bookingIn)).thenReturn(bookingOut);

        //perform tested request and check status and content
        String result = mockMvc.perform(post("/bookings")
                .header(header, userId)
                .contentType(jsonType)
                .content(bookingInString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", is(bookingOut.getStart().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingOut.getEnd().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.item", is(bookingOut.getItem()), Item.class))
                .andExpect(jsonPath("$.booker", is(bookingOut.getBooker()), User.class))
                .andExpect(jsonPath("$.status", is(bookingOut.getStatus().toString()), BookingStatus.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // verify invokes
        verify(bookingService).create(userId, bookingIn);

        //check result
        assertEquals(expectedBookingString, result);
    }

    @SneakyThrows
    @Test
    public void getById_StatusIsOk_InvokeService() {

        // create User
        User user = User.builder()
                .id(userId)
                .name("CustomerName")
                .build();

        // create Item
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .name("bike")
                .build();

        // create valid start and end time
        LocalDateTime futureStart = LocalDateTime.of(2024, 1, 1, 1, 1, 1);
        LocalDateTime futureEnd = LocalDateTime.of(2024, 2, 1, 1, 1, 1);

        // create valid input booking: BookingDto
        Long bookingId = 1L;
        BookingRequestDto bookingIn = BookingRequestDto.builder()
                .start(futureStart)
                .end(futureEnd)
                .itemId(itemId)
                .build();

        // create expected out booking: BookingOutDto
        BookingResponseDto bookingOut = BookingMapper.toBookingResponseDto(
                BookingMapper.toBooking(bookingIn, user, item, BookingStatus.WAITING));

        // map out booking into string
        String expectedBookingString = objectMapper.writeValueAsString(bookingOut);

        //mock service answer
        when(bookingService.getById(userId, bookingId)).thenReturn(bookingOut);

        //perform tested request and check status and content
        String result = mockMvc.perform(get("/bookings/{id}", bookingId)
                .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonType))
                .andExpect(content().json(expectedBookingString))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // verify invokes
        verify(bookingService).getById(userId, bookingId);

        // check result
        assertEquals(result, expectedBookingString);
    }

    @Test
    @SneakyThrows
    public void update_StatusIsOk_AndInvokeService() {

        // create valid parameter name and value
        String paramName = "approved";
        Boolean paramValue = true;

        // create User
        User user = User.builder()
                .id(userId)
                .name("Name")
                .build();

        // create Item
        Item item = Item.builder()
                .name("Item")
                .build();

        // create valid start and end time
        LocalDateTime futureStart = LocalDateTime.of(2024, 1, 1, 1, 1, 1);
        LocalDateTime futureEnd = LocalDateTime.of(2024, 2, 1, 1, 1, 1);

        // create valid input booking: BookingDto
        Long bookingId = 1L;
        BookingRequestDto bookingIn = BookingRequestDto.builder()
                .start(futureStart)
                .end(futureEnd)
                .build();

        // create expected out booking: BookingOutDto
        BookingResponseDto bookingOut = BookingMapper.toBookingResponseDto(
                BookingMapper.toBooking(bookingIn, user, item, BookingStatus.WAITING));

        // map input and out booking into strings
        String bookingInString = objectMapper.writeValueAsString(bookingIn);
        String expectedBookingString = objectMapper.writeValueAsString(bookingOut);


        // mock service answer
        when(bookingService.updateStatus(bookingId, userId, true)).thenReturn(bookingOut);

        //perform tested request and check status and content
        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                .header(header, userId)
                .param(paramName, String.valueOf(paramValue))
                .contentType(jsonType)
                .content(bookingInString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingOut.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingOut.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingOut.getStatus().toString())))
                .andExpect(jsonPath("$.item.name", is("Item")))
                .andExpect(jsonPath("$.booker.name", is("Name")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // verify invokes
        verify(bookingService).updateStatus(bookingId, userId, true);

        // check result
        assertEquals(result, expectedBookingString);
    }

    @Test
    @SneakyThrows
    public void update_WhenParamHasInvalidName_StatusIsInternalServerError_DoesNotInvokeService() {

        // create invalid parameter name
        String paramName = "notValidName";

        // create valid parameter value
        Boolean paramValue = true;

        // create userId
        Long userId = 1L;

        // create valid input booking: BookingDto
        Long bookingId = 1L;
        BookingRequestDto bookingIn = BookingRequestDto.builder()
                .id(bookingId)
                .build();

        // map input booking into string
        String bookingInString = objectMapper.writeValueAsString(bookingIn);

        //perform tested request and check status
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                .header(header, userId)
                .param(paramName, String.valueOf(paramValue))
                .contentType(jsonType)
                .content(bookingInString))
                .andExpect(status().isInternalServerError());

        // verify invokes
        verify(bookingService, never()).updateStatus(anyLong(), anyLong(), anyBoolean());

    }

    @Test
    @SneakyThrows
    public void update_WhenParamHasInvalidValue_StatusIsInternalServerError_DoesNotInvokeService() {

        // create valid parameter name
        String paramName = "approved";

        // create invalid parameter value
        String paramValue = "notValidValue";

        // create userId
        Long userId = 1L;

        // create valid input booking: BookingDto
        Long bookingId = 1L;
        BookingRequestDto bookingIn = BookingRequestDto.builder()
                .id(bookingId)
                .build();

        // map input booking into string
        String bookingInString = objectMapper.writeValueAsString(bookingIn);

        //perform tested request and check status

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                .header(header, userId)
                .param(paramName, paramValue)
                .contentType(jsonType)
                .content(bookingInString))
                .andExpect(status().isInternalServerError());
        // verify invokes

        verify(bookingService, never()).updateStatus(anyLong(), anyLong(), anyBoolean());

    }

    @Test
    @SneakyThrows
    public void getListByOwner_WhenParametersAreValid_IsStatusOk_AndInvokeService() {

        //create valid parameters
        String paramStateName = "state";
        String paramStateValue = "APPROVED";
        String paramFromName = "from";
        Integer paramFromValue = 0;
        String paramSizeName = "size";
        Integer paramSizeValue = 10;

        //create BookingOutDto objects
        Long booking1Id = 1L;
        Long booking2Id = 2L;

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2024, 2, 1, 1, 1, 1);

        BookingResponseDto booking1 = BookingResponseDto.builder()
                .id(booking1Id)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .build();
        BookingResponseDto booking2 = BookingResponseDto.builder()
                .id(booking2Id)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();

        // create list of bookings
        List<BookingResponseDto> bookings = List.of(booking1, booking2);

        // map bookings list into string
        String expectedBookingsListString = objectMapper.writeValueAsString(bookings);

        // mock service answer
        when(bookingService.getListByOwner(userId, "APPROVED", 0, 10)).thenReturn(bookings);

        //perform tested request and check status and content
        String result = mockMvc.perform(get("/bookings/owner")
                .header(header, userId)
                .param(paramStateName, paramStateValue)
                .param(paramFromName, String.valueOf(paramFromValue))
                .param(paramSizeName, String.valueOf(paramSizeValue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(booking1.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(booking1.getEnd().toString())))
                .andExpect(jsonPath("$.[0].status", is(booking1.getStatus().toString())))
                .andExpect(jsonPath("$.[1].id", is(booking2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].start", is(booking2.getStart().toString())))
                .andExpect(jsonPath("$.[1].end", is(booking2.getEnd().toString())))
                .andExpect(jsonPath("$.[1].status", is(booking2.getStatus().toString())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // verify invokes
        verify(bookingService).getListByOwner(userId, paramStateValue, paramFromValue, paramSizeValue);

        // check result
        assertEquals(result, expectedBookingsListString);
    }


    @Test
    @SneakyThrows
    public void getListByOwner_WhenFromIsString_IsStatusInternalServerError_DoesNotInvokeService() {

        // create invalid parameter
        String paramFromValue = "from";

        //create valid parameters
        String paramStateName = "state";
        String paramStateValue = "APPROVED";
        String paramFromName = "from";
        String paramSizeName = "size";
        Integer paramSizeValue = 10;

        //perform tested request and check status
        mockMvc.perform(get("/bookings/owner")
                .header(header, userId)
                .param(paramStateName, paramStateValue)
                .param(paramFromName, paramFromValue)
                .param(paramSizeName, String.valueOf(paramSizeValue)))
                .andExpect(status().isInternalServerError());

        // verify invokes
        verify(bookingService, never()).getListByOwner(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    public void getListByOwner_WhenSizeIsString_IsStatusInternalServerError_DoesNotInvokeService() {

        // create invalid parameter
        String paramSizeValue = "size";

        //create valid parameters
        String paramStateName = "state";
        String paramStateValue = "APPROVED";
        String paramFromName = "from";
        Integer paramFromValue = 10;
        String paramSizeName = "size";

        //perform tested request and check status
        mockMvc.perform(get("/bookings/owner")
                .header(header, userId)
                .param(paramStateName, paramStateValue)
                .param(paramFromName, String.valueOf(paramFromValue))
                .param(paramSizeName, paramSizeValue))
                .andExpect(status().isInternalServerError());

        // verify invokes
        verify(bookingService, never()).getListByOwner(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    public void getListByBooker_WhenParametersAreValid_IsStatusOk_AndInvokeService() {

        //create valid parameters
        String paramStateName = "state";
        String paramStateValue = "APPROVED";
        String paramFromName = "from";
        Integer paramFromValue = 0;
        String paramSizeName = "size";
        Integer paramSizeValue = 10;


        //create BookingOutDto objects
        Long booking1Id = 1L;
        Long booking2Id = 2L;

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2024, 2, 1, 1, 1, 1);

        BookingResponseDto booking1 = BookingResponseDto.builder()
                .id(booking1Id)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .build();
        BookingResponseDto booking2 = BookingResponseDto.builder()
                .id(booking2Id)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();

        // create list of bookings
        List<BookingResponseDto> bookings = List.of(booking1, booking2);

        // map bookings list into string
        String expectedBookingsListString = objectMapper.writeValueAsString(bookings);

        //mock service answer
        when(bookingService.getListByBooker(userId, "APPROVED", 0, 10)).thenReturn(bookings);

        //perform tested request and check status and content
        String result = mockMvc.perform(get("/bookings")
                .header(header, userId)
                .param(paramStateName, paramStateValue)
                .param(paramFromName, String.valueOf(paramFromValue))
                .param(paramSizeName, String.valueOf(paramSizeValue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(booking1.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(booking1.getEnd().toString())))
                .andExpect(jsonPath("$.[0].status", is(booking1.getStatus().toString())))
                .andExpect(jsonPath("$.[1].id", is(booking2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].start", is(booking2.getStart().toString())))
                .andExpect(jsonPath("$.[1].end", is(booking2.getEnd().toString())))
                .andExpect(jsonPath("$.[1].status", is(booking2.getStatus().toString())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        // verify invokes

        verify(bookingService).getListByBooker(userId, paramStateValue, paramFromValue, paramSizeValue);
        assertEquals(result, expectedBookingsListString);
    }


    @Test
    @SneakyThrows
    public void getListByBooker_WhenFromIsNotNumber_IsStatusInternalServerError_DoesNotInvokeService() {

        // create invalid parameter
        String paramFromValue = "from";

        //create valid parameters
        String paramStateName = "state";
        String paramStateValue = "APPROVED";
        String paramFromName = "from";
        String paramSizeName = "size";
        Integer paramSizeValue = 10;

        //perform tested request and check status
        mockMvc.perform(get("/bookings")
                .header(header, userId)
                .param(paramStateName, paramStateValue)
                .param(paramFromName, paramFromValue)
                .param(paramSizeName, String.valueOf(paramSizeValue)))
                .andExpect(status().isInternalServerError());

        // verify invokes
        verify(bookingService, never()).getListByBooker(anyLong(), anyString(), anyInt(), anyInt());

    }


    @Test
    @SneakyThrows
    public void getListByBooker_WhenSizeIsNotNumber_IsStatusInternalServerError_DoesNotInvokeService() {

        // create invalid parameter
        String paramSizeValue = "size";

        //create valid parameters
        String paramStateName = "state";
        String paramStateValue = "APPROVED";
        String paramFromName = "from";
        Integer paramFromValue = 10;
        String paramSizeName = "size";

        //perform tested request and check status
        mockMvc.perform(get("/bookings")
                .header(header, userId)
                .param(paramStateName, paramStateValue)
                .param(paramFromName, String.valueOf(paramFromValue))
                .param(paramSizeName, paramSizeValue))
                .andExpect(status().isInternalServerError());

        // verify invokes
        verify(bookingService, never()).getListByBooker(anyLong(), anyString(), anyInt(), anyInt());

    }

}
