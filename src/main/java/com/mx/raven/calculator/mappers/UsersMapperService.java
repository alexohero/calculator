package com.mx.raven.calculator.mappers;

import com.mx.raven.calculator.model.dto.UserDTO;
import com.mx.raven.calculator.persistence.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsersMapperService {

    private final UsersMapper usersMapper;

    public UserDTO userToUserDTO(User user) {
        return usersMapper.userToUserDTO(user);
    }

    public User userDTOToUser(UserDTO userDTO) {
        return usersMapper.userDTOToUser(userDTO);
    }

}
