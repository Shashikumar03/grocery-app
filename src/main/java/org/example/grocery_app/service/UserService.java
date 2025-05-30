package org.example.grocery_app.service;


import org.example.grocery_app.dto.UserDto;


import java.util.List;

public interface UserService {


    UserDto createUser(UserDto userDto);


    UserDto updateUser(Long userId, UserDto userDto);


    UserDto getUserById(Long userId);



    List<UserDto> getAllUsers();


    void deleteUser(Long userId);

    UserDto getUserByEmail(String email);

    List<UserDto> getUserByRole(String role);

    void  deleteMyAccount(String  email);

}
