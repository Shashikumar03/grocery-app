package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.dto.UserDto;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User User = this.modelMapper.map(userDto, User.class);
        User save = this.userRepository.save(User);
        return this.modelMapper.map(save, UserDto.class);

    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setRole(userDto.getRole());
        user.setPhoneNumber(userDto.getPhoneNumber());
        User saveUser = this.userRepository.save(user);

       return  this.modelMapper.map(saveUser, UserDto.class);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "UserId", userId));
        return this.modelMapper.map(user, UserDto.class);
        
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = this.userRepository.findAll();
        List<UserDto> allUserDto = allUsers.stream().map(user -> this.modelMapper.map(user, UserDto.class)).collect(Collectors.toList());

        return allUserDto;
    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public UserDto getUserByEmail(String email) {
        return null;
    }
}
