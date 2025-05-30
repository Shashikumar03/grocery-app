package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.dto.ShopkeeperDto;
import org.example.grocery_app.entities.Shopkeeper;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.ShopkeeperRepository;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.service.ShopkeeperService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopkeeperServiceImplementation implements ShopkeeperService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopkeeperRepository shopkeeperRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public ShopkeeperDto createShopkeeper(ShopkeeperDto shopkeeperDto) {
        Long userId = shopkeeperDto.getUserId();
        User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "userid", userId));
        Shopkeeper shopkeeper = this.modelMapper.map(shopkeeperDto, Shopkeeper.class);
        shopkeeper.setUser(user);
        Shopkeeper saveShopkeeper = this.shopkeeperRepository.save(shopkeeper);
        return  this.modelMapper.map(saveShopkeeper, ShopkeeperDto.class);

    }

    @Override
    public Optional<ShopkeeperDto> getShopkeeperById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<ShopkeeperDto> getAllShopkeepers() {
        return List.of();
    }

    @Override
    public void deleteShopkeeper(Long id) {

    }
}
