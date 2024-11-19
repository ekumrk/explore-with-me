package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.user.model.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constants.Constants.FROM;
import static ru.practicum.constants.Constants.SIZE;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService service;

    @RequestMapping(value = "/admin/users", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid UserDto dto) {
        return service.addNewUser(dto);
    }

    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                  @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        return service.getUsers(ids, from, size);
    }

    @RequestMapping(value = "/admin/users/{userId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Positive long userId) {
        service.deleteUser(userId);
    }
}
