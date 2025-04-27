package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.dto.DeliveryAddressDto;
import org.example.grocery_app.dto.UserDto;
import org.example.grocery_app.entities.DeliveryAddress;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.DeliveryAddressRepository;
import org.example.grocery_app.repository.UserRepository;
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
    @Override
    public DeliveryAddressDto createNewDeliveryAddress(Long userId, DeliveryAddressDto deliveryAddressDto) {
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
        return List.of();
    }
}
