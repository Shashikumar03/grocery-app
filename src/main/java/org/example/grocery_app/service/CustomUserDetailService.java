package org.example.grocery_app.service;

import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private StudentRepository studentRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Admin admin = this.adminRepository.findByAdminEmail(username).orElse(null);
        org.example.grocery_app.entities.User user = this.userRepository.findByEmail(username).orElse(null);
         if (user != null) {
            return User.withUsername(user.getEmail())
                    .password(user.getPassword())
//                    .id(user.getId())
                    .build();
        } else {
            throw new ApiException("User not found with username: " + username);
        }

    }
}