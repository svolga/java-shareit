package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;
    private Long itemRequestId;
    private Long userId;
    private Long requestId;
    private String header;
    private MediaType jsonType;


    @BeforeEach
    @SneakyThrows
    void before() {
        userId = 1L;
        requestId = 1L;
        itemRequestId = 1L;
        header = "X-Sharer-User-Id";
        jsonType = MediaType.APPLICATION_JSON;
    }

    @Test
    @SneakyThrows
    void create_WhenItemRequestIsValid_StatusIsOk_AndInvokeService() {

        User user = User.builder().name("CustomerName").build();
        ItemDto item = ItemDto.builder().name("bike").build();
        List<ItemDto> items = List.of(item);
        ItemRequestDto validItemRequest = ItemRequestDto.builder()
                .description("I would like rent bike").build();
        String validItemRequestString = objectMapper.writeValueAsString(validItemRequest);
        ItemRequestOutDto itemRequest = ItemRequestMapper.toItemRequestOutDto(
                ItemRequestMapper.toItemRequest(validItemRequest, user), items).toBuilder().id(itemRequestId).build();
        String itemRequestString = objectMapper.writeValueAsString(itemRequest);


        when(itemRequestService.create(userId, validItemRequest)).thenReturn(itemRequest);

        String result = mockMvc.perform(post("/requests")
                        .header(header, userId)
                        .contentType(jsonType)
                        .content(validItemRequestString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.items.[0].name", is("bike")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).create(userId, validItemRequest);
        assertEquals(itemRequestString, result);

    }

    @Test
    @SneakyThrows
    void create_WhenItemRequestHasEmptyDescription_StatusIsBadRequest_DoesNotInvokeService() {

        ItemRequestDto invalidItemRequest = ItemRequestDto.builder()
                .description("").build();
        String invalidItemRequestString = objectMapper.writeValueAsString(invalidItemRequest);

        mockMvc.perform(post("/requests")
                        .header(header, userId)
                        .contentType(jsonType)
                        .content(invalidItemRequestString))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, Mockito.never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void create_WhenItemRequestHasNullDescription_StatusIsBadRequest_DoesNotInvokeService() {

        ItemRequestDto invalidItemRequest = ItemRequestDto.builder()
                .description(null).build();
        String invalidItemRequestString = objectMapper.writeValueAsString(invalidItemRequest);

        mockMvc.perform(post("/requests")
                        .header(header, userId)
                        .contentType(jsonType)
                        .content(invalidItemRequestString))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, Mockito.never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getOwnRequests_IsStatusOk_AndInvokeService() {

        User user = User.builder().name("CustomerName").build();
        ItemDto item = ItemDto.builder().name("bike").build();
        List<ItemDto> items = List.of(item);
        ItemRequestDto validItemRequest = ItemRequestDto.builder()
                .description("I would like rent bike").build();

        ItemRequestOutDto itemRequest = ItemRequestMapper.toItemRequestOutDto(
                ItemRequestMapper.toItemRequest(validItemRequest, user), items).toBuilder().id(itemRequestId).build();

        List<ItemRequestOutDto> requests = List.of(itemRequest);
        String requestsListString = objectMapper.writeValueAsString(requests);

        when(itemRequestService.getOwnRequests(userId)).thenReturn(requests);

        String result = mockMvc.perform(get("/requests")
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.[0].items.[0].name", is("bike")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getOwnRequests(userId);
        assertEquals(result, requestsListString);
    }

    @Test
    @SneakyThrows
    void getOtherUsersRequests_WhenParametersAreValid_IsStatusOk_andInvokeService() {

        User user = User.builder().name("CustomerName").build();
        ItemDto item = ItemDto.builder().name("bike").build();
        List<ItemDto> items = List.of(item);
        ItemRequestDto validItemRequest = ItemRequestDto.builder()
                .description("I would like rent bike").build();

        ItemRequestOutDto itemRequest = ItemRequestMapper.toItemRequestOutDto(
                ItemRequestMapper.toItemRequest(validItemRequest, user), items).toBuilder().id(itemRequestId).build();

        List<ItemRequestOutDto> requests = List.of(itemRequest);
        String requestsListString = objectMapper.writeValueAsString(requests);

        String paramFromName = "from";
        Integer paramFromValue = 0;
        String paramSizeName = "size";
        Integer paramSizeValue = 10;

        when(itemRequestService.getOtherUsersRequests(userId, paramFromValue, paramSizeValue)).thenReturn(requests);

        String result = mockMvc.perform(get("/requests/all")
                        .header(header, userId)
                        .param(paramFromName, String.valueOf(paramFromValue))
                        .param(paramSizeName, String.valueOf(paramSizeValue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.[0].items.[0].name", is("bike")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getOtherUsersRequests(userId, paramFromValue, paramSizeValue);
        assertEquals(result, requestsListString);
    }

    @Test
    @SneakyThrows
    void getOtherUsersRequests_WhenFromIsNegative_IsStatusBadRequest_DoesNotInvokeService() {

        String paramFromName = "from";
        Integer paramFromValue = -1;
        String paramSizeName = "size";
        Integer paramSizeValue = 10;

        mockMvc.perform(get("/requests/all")
                        .header(header, userId)
                        .param(paramFromName, String.valueOf(paramFromValue))
                        .param(paramSizeName, String.valueOf(paramSizeValue)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getOtherUsersRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getOtherUsersRequests_WhenSizeIsNegative_IsStatusBadRequest_DoesNotInvokeService() {

        String paramFromName = "from";
        Integer paramFromValue = 0;
        String paramSizeName = "size";
        Integer paramSizeValue = -1;

        mockMvc.perform(get("/requests/all")
                        .header(header, userId)
                        .param(paramFromName, String.valueOf(paramFromValue))
                        .param(paramSizeName, String.valueOf(paramSizeValue)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getOtherUsersRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getOtherUsersRequests_WhenSizeIsZero_IsStatusBadRequest_AndDoesNotInvokeService() {

        String paramFromName = "from";
        Integer paramFromValue = 0;
        String paramSizeName = "size";
        Integer paramSizeValue = 0;

        mockMvc.perform(get("/requests/all")
                        .header(header, userId)
                        .param(paramFromName, String.valueOf(paramFromValue))
                        .param(paramSizeName, String.valueOf(paramSizeValue)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getOtherUsersRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getOtherUsersRequests_WhenFromIsNotNumber_IsStatusInternalServerError_DoesNotInvokeService() {

        String paramFromName = "from";
        String paramFromValue = "from";
        String paramSizeName = "size";
        Integer paramSizeValue = 10;

        mockMvc.perform(get("/requests/all")
                        .header(header, userId)
                        .param(paramFromName, paramFromValue)
                        .param(paramSizeName, String.valueOf(paramSizeValue)))
                .andExpect(status().isInternalServerError());

        verify(itemRequestService, never()).getOtherUsersRequests(anyLong(), anyInt(), anyInt());

    }

    @Test
    @SneakyThrows
    void getOtherUsersRequests_WhenSizeIsNotNumber_IsStatusInternalServerError_DoesNotInvokeService() {

        String paramFromName = "from";
        String paramFromValue = "10";
        String paramSizeName = "size";
        String paramSizeValue = "size";

        mockMvc.perform(get("/requests/all")
                        .header(header, userId)
                        .param(paramFromName, paramFromValue)
                        .param(paramSizeName, paramSizeValue))
                .andExpect(status().isInternalServerError());

        verify(itemRequestService, never()).getOtherUsersRequests(anyLong(), anyInt(), anyInt());

    }

    @SneakyThrows
    @Test
    void getById_StatusIsOk_InvokeService() {

        User user = User.builder().name("CustomerName").build();
        ItemDto item = ItemDto.builder().name("bike").build();
        List<ItemDto> items = List.of(item);
        ItemRequestDto validItemRequest = ItemRequestDto.builder()
                .description("I would like rent bike").build();
        ItemRequestOutDto itemRequest = ItemRequestMapper.toItemRequestOutDto(
                ItemRequestMapper.toItemRequest(validItemRequest, user), items).toBuilder().id(itemRequestId).build();
        String itemRequestString = objectMapper.writeValueAsString(itemRequest);

        when(itemRequestService.getRequestById(userId, requestId)).thenReturn(itemRequest);

        String result = mockMvc.perform(get("/requests/{requestId}", itemRequestId)
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonType))
                .andExpect(content().json(itemRequestString))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getRequestById(userId, requestId);
        assertEquals(result, itemRequestString);

    }
}