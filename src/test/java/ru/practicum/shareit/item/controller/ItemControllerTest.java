package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    private Long itemId;
    private Long userId;
    private Long requestId;
    private String header;
    private MediaType jsonType;

    @BeforeEach
    @SneakyThrows
    void before() {
        itemId = 1L;
        userId = 1L;
        requestId = 1L;
        header = "X-Sharer-User-Id";
        jsonType = MediaType.APPLICATION_JSON;
    }

    @Test
    @SneakyThrows
    public void create_WhenItemIsValid_StatusIsOk_andInvokeService() {

        //create Item with valid fields
        ItemDto validItem = ItemDto.builder()
                .id(itemId)
                .name("bike")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();

        // map item into String
        String expectedItemString = objectMapper.writeValueAsString(validItem);

        //mock service answer
        when(itemService.create(userId, validItem)).thenReturn(validItem);

        //perform request and check status and content
        String result = mockMvc.perform(post("/items")
                        .header(header, userId)
                        .contentType(jsonType)
                        .content(expectedItemString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(validItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(validItem.getName())))
                .andExpect(jsonPath("$.description", is(validItem.getDescription())))
                .andExpect(jsonPath("$.available", is(validItem.getAvailable())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // verify invokes
        verify(itemService).create(userId, validItem);

        //check result
        assertEquals(expectedItemString, result);

    }

    @Test
    @SneakyThrows
    public void create_whenItemHasEmptyName_statusIsBadRequest_doesNotInvokeService() {

        //create invalid field
        String emptyName = "";

        //create Item with invalid fields
        ItemDto invalidItem = ItemDto.builder()
                .id(itemId)
                .name(emptyName)
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        // map item into String
        String invalidItemString = objectMapper.writeValueAsString(invalidItem);

        //perform request and check status
        mockMvc.perform(post("/items")
                        .header(header, userId)
                        .contentType(jsonType)
                        .content(invalidItemString))
                .andExpect(status().isBadRequest());

        // verify invokes
        verify(itemService, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    public void create_whenItemHasEmptyDescription_statusIsBadRequest_doesNotInvokeService() {

        //create invalid field
        String emptyDescription = "";

        //create Item with invalid fields
        ItemDto invalidItem = ItemDto.builder()
                .id(itemId)
                .name("bike")
                .description(emptyDescription)
                .available(true)
                .requestId(1L)
                .build();

        // map item into String
        String invalidItemString = objectMapper.writeValueAsString(invalidItem);

        //perform request and check status
        mockMvc.perform(post("/items")
                        .header(header, userId)
                        .contentType(jsonType)
                        .content(invalidItemString))
                .andExpect(status().isBadRequest());

        // verify invokes
        verify(itemService, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    public void create_whenItemHasNullName_statusIsBadRequest_doesNotInvokeService() {

        //create Item with null name
        ItemDto invalidItem = ItemDto.builder()
                .id(itemId)
                .name(null)
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        // map item into String
        String invalidItemString = objectMapper.writeValueAsString(invalidItem);

        //perform request and check status
        mockMvc.perform(post("/items")
                        .header(header, userId)
                        .contentType(jsonType)
                        .content(invalidItemString))
                .andExpect(status().isBadRequest());

        // verify invokes
        verify(itemService, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    public void create_whenItemHasNullDescription_statusIsBadRequest_doesNotInvokeService() {

        //create Item with null description
        ItemDto invalidItem = ItemDto.builder()
                .id(itemId)
                .name("bike")
                .description(null)
                .available(true)
                .requestId(1L)
                .build();

        // map item into String
        String invalidItemString = objectMapper.writeValueAsString(invalidItem);

        //perform request and check status
        mockMvc.perform(post("/items")
                        .header(header, userId)
                        .contentType(jsonType)
                        .content(invalidItemString))
                .andExpect(status().isBadRequest());

        // verify invokes
        verify(itemService, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    public void create_whenItemHasNullAvailable_statusIsBadRequest_doesNotInvokeService() {

        //create Item with null available
        ItemDto invalidItem = ItemDto.builder()
                .id(itemId)
                .name("bike")
                .description("description")
                .available(null)
                .requestId(1L)
                .build();

        // map item into String
        String invalidItemString = objectMapper.writeValueAsString(invalidItem);

        //perform request and check status
        mockMvc.perform(post("/items")
                        .header(header, userId)
                        .contentType(jsonType)
                        .content(invalidItemString))
                .andExpect(status().isBadRequest());

        // verify invokes
        verify(itemService, never()).create(anyLong(), any());
    }


    @SneakyThrows
    @Test
    public void getById_statusIsOk_andInvokeService() {

        //create Item with valid fields
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name("Item")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();
        ItemResponseDto item = ItemMapper.toItemResponseDto(itemDto);

        // map item into String
        String expectedItemString = objectMapper.writeValueAsString(item);

        //mock service answer
        when(itemService.getById(userId, itemId)).thenReturn(item);

        //perform request and check status and content
        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedItemString))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // verify invokes
        verify(itemService).getById(userId, itemId);

        //check result
        assertEquals(result, expectedItemString);
    }


    @Test
    @SneakyThrows
    public void update_statusIsOk_andInvokeService() {
        //create ItemDto with valid fields to update
        ItemDto validItem = ItemDto.builder()
                .id(itemId)
                .name("bike")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();

        // map item into String
        String expectedItemString = objectMapper.writeValueAsString(validItem);

        //mock service answer
        when(itemService.update(userId, validItem, itemId)).thenReturn(validItem);

        //perform request and check status and content
        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(header, userId)
                        .contentType(jsonType)
                        .content(expectedItemString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // verify invokes
        verify(itemService).update(userId, validItem, itemId);

        //check result
        assertEquals(expectedItemString, result);
    }

    @Test
    @SneakyThrows
    public void delete_statusIsOk_andInvokeService() {

        //perform request and check status
        mockMvc.perform(delete("/items/{itemId}", itemId))
                .andExpect(status().isOk());

        // verify invokes
        verify(itemService).deleteById(itemId);

    }

    @Test
    @SneakyThrows
    public void getListByUser_isStatusOk_andInvokeService() {

        //create ItemDto objects
        ItemDto validItem1 = ItemDto.builder()
                .id(itemId)
                .name("bike")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        ItemDto validItem2 = ItemDto.builder()
                .id(itemId)
                .name("bike2")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();

        //create list of items
        List<ItemDto> list = List.of(validItem1, validItem2);
        List<ItemResponseDto> items = list.stream().map(ItemMapper::toItemResponseDto).collect(Collectors.toList());

        // map ItemOutDto list into String
        String expectedItemsListString = objectMapper.writeValueAsString(items);

        //mock service answer
        when(itemService.getListByUser(userId)).thenReturn(items);

        //perform request and check status and content
        String result = mockMvc.perform(get("/items")
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(validItem1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(validItem1.getName()), String.class))
                .andExpect(jsonPath("$.[1].name", is(validItem2.getName()), String.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // verify invokes
        verify(itemService).getListByUser(userId);

        //check result
        assertEquals(result, expectedItemsListString);
    }

    @Test
    @SneakyThrows
    public void searchItemsBySubstring_isStatusOk_andInvokeService() {

        //create valid parameters for search
        String parameterName = "text";
        String parameterValue = "substring";

        //create ItemDto objects
        ItemDto validItem1 = ItemDto.builder()
                .id(itemId)
                .name("bike")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        ItemDto validItem2 = ItemDto.builder()
                .id(itemId)
                .name("bike2")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();

        //create list of items
        List<ItemDto> list = List.of(validItem1, validItem2);
        List<ItemResponseDto> items = list.stream().map(ItemMapper::toItemResponseDto).collect(Collectors.toList());

        // map list into String
        String itemsString = objectMapper.writeValueAsString(items);

        //mock service answer
        when(itemService.searchItemsBySubstring(parameterValue)).thenReturn(items);

        //perform request and check status and content
        String result = mockMvc.perform(get("/items/search")
                        .param(parameterName, parameterValue))
                .andExpect(status().isOk())
                .andExpect(content().json(itemsString))
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // verify invokes
        verify(itemService).searchItemsBySubstring(parameterValue);

        //check result
        assertEquals(result, itemsString);
    }

    @Test
    @SneakyThrows
    public void searchItemsBySubstring_whenParameterNameIsInvalid_isStatusInternalServerError_AndInvokeService() {

        //create invalid parameter
        String parameterName = "invalid";

        //create valid parameter fo search
        String parameterValue = "substring";

        //create ItemDto objects
        ItemDto validItem1 = ItemDto.builder()
                .id(itemId)
                .name("bike")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        ItemDto validItem2 = ItemDto.builder()
                .id(itemId)
                .name("bike2")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();

        //create list of items
        List<ItemDto> list = List.of(validItem1, validItem2);
        List<ItemResponseDto> items = list.stream().map(ItemMapper::toItemResponseDto).collect(Collectors.toList());

        //mock service answer
        when(itemService.searchItemsBySubstring(parameterValue)).thenReturn(items);

        //perform request and check status
        mockMvc.perform(get("/items/search")
                        .param(parameterName, parameterValue))
                .andExpect(status().isInternalServerError());

        // verify invokes
        verify(itemService, never()).searchItemsBySubstring(anyString());
    }

    @Test
    @SneakyThrows
    public void addComment_whenValidComment_isStatusOk_andInvokeService() {

        //create comment with valid fields
        CommentRequestDto comment = CommentRequestDto.builder()
                .text("commentText")
                .authorName("CustomerName")
                .itemId(itemId)
                .build();

        CommentResponseDto commentOut = CommentMapper.toCommentResponseDto(comment);

        // map comment into String
        String expectedCommentString = objectMapper.writeValueAsString(commentOut);

        //mock service answer
        when(itemService.addComment(comment, userId, itemId)).thenReturn(commentOut);

        //perform request and check status and content
        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(header, userId)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(jsonType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentOut.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentOut.getText())))
                .andExpect(jsonPath("$.authorName", is(commentOut.getAuthorName())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // verify invokes
        verify(itemService).addComment(comment, userId, itemId);

        //check result
        assertEquals(result, expectedCommentString);
    }

    @Test
    @SneakyThrows
    public void addComment_whenInvalidCommentEmptyText_isStatusBadRequest_andDoesNotInvokeService() {

        String emptyText = "";
        CommentRequestDto invalidComment = CommentRequestDto.builder()
                .text(emptyText)
                .authorName("CustomerName")
                .itemId(itemId)
                .build();

        CommentResponseDto commentOut = CommentMapper.toCommentResponseDto(invalidComment);

        //mock service answer
        when(itemService.addComment(invalidComment, userId, itemId)).thenReturn(commentOut);

        //perform request and check status
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(header, userId)
                        .content(objectMapper.writeValueAsString(invalidComment))
                        .contentType(jsonType))
                .andExpect(status().isBadRequest());

        // verify invokes
        verify(itemService, never()).addComment(invalidComment, userId, itemId);

    }

    @Test
    @SneakyThrows
    public void addComment_whenInvalidCommentNullText_isStatusBadRequest_andDoesNotInvokeService() {

        //create invalidCommentDto object with null text
        CommentRequestDto invalidComment = CommentRequestDto.builder()
                .text(null)
                .authorName("CustomerName")
                .itemId(itemId)
                .build();

        CommentResponseDto commentOut = CommentMapper.toCommentResponseDto(invalidComment);

        //mock service answer
        when(itemService.addComment(invalidComment, userId, itemId)).thenReturn(commentOut);

        //perform request and check status
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(header, userId)
                        .content(objectMapper.writeValueAsString(invalidComment))
                        .contentType(jsonType))
                .andExpect(status().isBadRequest());

        // verify invokes
        verify(itemService, never()).addComment(any(), anyLong(), anyLong());

    }
}
