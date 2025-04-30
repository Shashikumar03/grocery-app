package org.example.grocery_app.serviceImplementation;

import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.dto.UserDto;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("UserDto : {}",userDto.toString());
        User user = this.modelMapper.map(userDto, User.class);
        user.setName(userDto.getName());
        log.info("UserDto Mapped to user{}",user);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        User save = this.userRepository.save(user);
        log.info("new created user :{}",save);
        return this.modelMapper.map(save, UserDto.class);

    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setName(userDto.getName());
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
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

    @Override
    public List<UserDto> getUserByRole(String role) {
        List<User> users = this.userRepository.findByRole(role).orElseThrow(() -> new ResourceNotFoundException("", "", 0));

        return users.stream().map((user -> this.modelMapper.map(user, UserDto.class))).toList();
    }
}
