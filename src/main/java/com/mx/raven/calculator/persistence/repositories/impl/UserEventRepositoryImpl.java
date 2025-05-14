package com.mx.raven.calculator.persistence.repositories.impl;

import com.mx.raven.calculator.mappers.UsersMapperService;
import com.mx.raven.calculator.model.dto.UserDTO;
import com.mx.raven.calculator.persistence.repositories.UserEventRepository;
import com.mx.raven.calculator.persistence.stores.UserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserEventRepositoryImpl implements UserEventRepository {

    private final UserStore userStore;
    private final UsersMapperService mapper;

    @Override
    public UserDTO storeSaveUser(UserDTO dto) {
        var entity = userStore.save(mapper.userDTOToUser(dto));
        return mapper.userToUserDTO(entity);
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        var entities = userStore.findByUsername(username);
        return entities.map(mapper::userToUserDTO);
    }
}
