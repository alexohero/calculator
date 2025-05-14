package com.mx.raven.calculator.mappers;

import com.mx.raven.calculator.model.dto.UserDTO;
import com.mx.raven.calculator.persistence.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UsersMapper {

    UsersMapper INSTANCE = Mappers.getMapper(UsersMapper.class);

    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);
}
