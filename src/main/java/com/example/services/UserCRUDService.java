package com.example.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.dto.UserDto;
import com.example.entity.UserEntity;
import com.example.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Slf4j
@Service
public class UserCRUDService implements CRUDService<UserDto> {
    private final UserRepository userRepository;

    public UserCRUDService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getById(Integer id) {
        log.info("User with ID - " + id);
        UserEntity userEntity = userRepository.findById(id).orElseThrow();
        return mapToDto(userEntity);
    }

    @Override
    public Collection<UserDto> getAll() {
        log.info("Users");
        return userRepository.findAll()
                .stream()
                .map(UserCRUDService::mapToDto)
                .toList();
    }

    @Override
    public void create(UserDto userDto) {
        log.info("User created");
        String password = BCrypt.withDefaults().hashToString(10, userDto.getPassword().toCharArray());
        userDto.setPassword(password);
        UserEntity userEntity = mapToEntity(userDto);
        userRepository.save(userEntity);
    }

    @Override
    public void update(UserDto userDto) {
        log.info("User updated");

        if (!userDto.getPassword().isEmpty() || userDto.getPassword() != null) {
            String newPassword = BCrypt.withDefaults().hashToString(10, userDto.getPassword().toCharArray());
            userDto.setPassword(newPassword);
        }

        UserEntity userEntity = mapToEntity(userDto);
        userRepository.save(userEntity);
    }

    @Override
    public void delete(Integer id) {
        log.info("User deleted with ID - " + id);
        userRepository.deleteById(id);
    }

    public boolean authenticate(String email, String rawPassword) {
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);

        if (userEntity == null) {
            return false;
        }

        boolean result = BCrypt.verifyer().verify(rawPassword.toCharArray(), userEntity.getPassword()).verified;

        return result;
    }

    public static UserDto mapToDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setEmail(userEntity.getEmail());
        userDto.setPassword(userEntity.getPassword());
        return userDto;
    }

    public static UserEntity mapToEntity (UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDto.getId());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPassword(userDto.getPassword());
        return userEntity;
    }
}

