package org.example.grocery_app.serviceImplementation;

import jakarta.servlet.http.HttpServletRequest;
import org.example.grocery_app.dto.AddProductRequestDto;
import org.example.grocery_app.dto.DeliveryAddressDto;
import org.example.grocery_app.dto.UserDto;
import org.example.grocery_app.entities.DeliveryAddress;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.DeliveryAddressRepository;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.security.SecurityUtils;
import org.example.grocery_app.service.DeliveryAddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryAddressServiceImp implements DeliveryAddressService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public DeliveryAddressDto createNewDeliveryAddress(Long userId, DeliveryAddressDto deliveryAddressDto) {

        this.securityUtils.validateUserAccess(userId, request);

        User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "userId" ,userId));

        DeliveryAddress deliveryAddress = this.modelMapper.map(deliveryAddressDto, DeliveryAddress.class);
        deliveryAddress.setUser(user);
        DeliveryAddress saveDeliveryAddress = this.deliveryAddressRepository.save(deliveryAddress);
        UserDto userDto = this.modelMapper.map(user, UserDto.class);
        // Map saved entity back to DTO
        DeliveryAddressDto savedDto = this.modelMapper.map(saveDeliveryAddress, DeliveryAddressDto.class);

        // Also set userId manually if needed
        savedDto.setUserId(userId);

        return savedDto;
    }

    @Override
    public List<DeliveryAddressDto> getUserAllDeliveryAddresses(Long userId) {
        this.securityUtils.validateUserAccess(userId, request);
        List<DeliveryAddress> listOfUserAddress = this.deliveryAddressRepository.findByUserId(userId);
        List<DeliveryAddressDto> list = listOfUserAddress.stream()
                .map(address -> this.modelMapper.map(address, DeliveryAddressDto.class))
                .toList();

        return list;
    }
}
