package ru.practicum.request.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import static ru.practicum.constants.Constants.USER_EMAIL_MAX;
import static ru.practicum.constants.Constants.USER_EMAIL_MIN;
import static ru.practicum.constants.Constants.USER_NAME_MAX;
import static ru.practicum.constants.Constants.USER_NAME_MIN;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NewUserRequest {
    @Email
    @Length(min = USER_EMAIL_MIN, max = USER_EMAIL_MAX)
    String email;

    @NotBlank
    @Length(min = USER_NAME_MIN, max = USER_NAME_MAX)
    String name;
}
