package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserMapper;
import ru.practicum.user.model.dto.UserDto;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto addNewUser(UserDto dto) {
        User user = mapper.userDtoToUser(dto);
        return mapper.userToUserDto(repository.saveAndFlush(user));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return repository.getUsers(ids, page).stream()
                .map(mapper::userToUserDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void deleteUser(long id) {
        checkIfUserExist(id);
        repository.deleteById(id);
        repository.flush();
    }

    private User checkIfUserExist(Long userId) {
        return repository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id=" + userId + " was not found")
        );
    }
}
