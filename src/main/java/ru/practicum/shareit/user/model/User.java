package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Validated
@Builder
@AllArgsConstructor
public class User {
    private long id;
    private String name;

    @Email(message = "Электронная почта должна содержать символ @")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;
}
