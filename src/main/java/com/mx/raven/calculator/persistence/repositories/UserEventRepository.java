package com.mx.raven.calculator.persistence.repositories;

import com.mx.raven.calculator.model.dto.UserDTO;

import java.util.Optional;

public interface UserEventRepository {

    UserDTO storeSaveUser(UserDTO dto);

    Optional<UserDTO> findByUsername(String username);

}
