package com.mx.raven.calculator.service;

import com.mx.raven.calculator.model.AuthenticationRequest;
import com.mx.raven.calculator.model.AuthenticationResponse;
import com.mx.raven.calculator.model.dto.UserDTO;

public interface UserService {

    UserDTO registerUser(UserDTO userDTO);

    AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest);
}
