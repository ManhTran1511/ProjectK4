package com.fpt.edu.security.services;

import com.fpt.edu.models.User;
import com.fpt.edu.models.UserDto;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
}
