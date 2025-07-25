package org.example.grocery_app.serviceImplementation;

import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.dto.PasswordResetTokenDto;
import org.example.grocery_app.entities.PasswordResetToken;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.payload.JwtResponse;
import org.example.grocery_app.repository.PasswordResetTokenRepository;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.security.JwtHelper;
import org.example.grocery_app.service.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class AuthServiceImplementation implements AuthService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository; // ✅ Add this

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtHelper helper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public JwtResponse doLogin(String email, String password) {
        try {
            Optional<User> userByPhone = userRepository.findByPhoneNumber(email);
            if(userByPhone.isPresent()){
                this.doAuthenticate(email, password);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                String token = this.helper.generateToken(userByPhone.get());

                return JwtResponse.builder()
                        .jwtToken(token)
                        .user(userByPhone.get())
                        .build();
            } else  {
                Optional<User> userByEmail = userRepository.findByEmail(email);
                if(userByEmail.isPresent()){

                    this.doAuthenticate(email, password);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    String token = this.helper.generateToken(userByEmail.get());

                    return JwtResponse.builder()
                            .jwtToken(token)
                            .user(userByEmail.get())
                            .build();
                }else{
                    throw  new ApiException("कृपया सही email or phone दर्ज करें।:" + email);
                }
            }

//            User user = userRepository.findByEmail(email).orElseThrow(
//                    () -> new ApiException("कृपया सही email ID दर्ज करें।: " + email)
//            );
//            email=user.getEmail();


        } catch (BadCredentialsException ex) {
            throw new ApiException("Wrong Password !!");
        }
    }



//    @Override
//    public JwtResponse doLogin(String email, String password) {
//        User user = userRepository.findByEmail(email).orElseThrow(
//                () -> new ApiException("कृपया सही email ID दर्ज करें।: " + email)
//        );
//
//        this.doAuthenticate(email, password);
//
//        // Load UserDetails
//        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//
//
//        String token = this.helper.generateToken(user);
//        Long idFromToken = this.helper.getIdFromToken(token);
//        System.out.println("id from the token: "+ idFromToken);
//
//
//        return JwtResponse.builder()
//                .jwtToken(token)
////                .username(user.getUsername())   // user's username/email
////                .role(user.getRole())            // user's role
////                .id(user.getId())                // user's id
//                .user(user)
//                .build();
//    }

    @Override
    public PasswordResetTokenDto requestResetPassword(String email) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email: " + email, 0));
        String otp = String.format("%06d", new Random().nextInt(999999));
        Optional<PasswordResetToken> passwordReset = passwordResetTokenRepository.findByUser(user);
        PasswordResetToken resetPassword;
//        int attemptCount=0;
        PasswordResetToken save;
        if(passwordReset.isEmpty()){
            resetPassword= new PasswordResetToken();
            resetPassword.setAttemptCount(1);
//            passwordResetToken.
            resetPassword.setUser(user);

            resetPassword.setOtp(otp);
            resetPassword.setCreatedTime(LocalDateTime.now());
//        token.setAttemptCount(0);
            save = passwordResetTokenRepository.save(resetPassword);

        }else{
            PasswordResetToken passwordResetToken = passwordReset.get();


            Duration duration = Duration.between(passwordResetToken.getCreatedTime(), LocalDateTime.now());
            long minutes = duration.toMinutes();


            int attemptCount = passwordResetToken.getAttemptCount();
            if(minutes>15){
                attemptCount=0;
            }
            log.info("get retry count: {}",attemptCount);
            if(attemptCount>3){
                throw  new ApiException("Max try Exceeded!! please try after 15 Min");
            }else{
                passwordResetToken.setAttemptCount(attemptCount+1);
            }
            passwordResetToken.setUser(user);

            passwordResetToken.setOtp(otp);
            passwordResetToken.setEmail(email);
            passwordResetToken.setCreatedTime(LocalDateTime.now());
            save = passwordResetTokenRepository.save(passwordResetToken);
        }

        Long userId = save.getUser().getId();
        PasswordResetTokenDto map = this.modelMapper.map(save, PasswordResetTokenDto.class);
        map.setUserId(userId);
        return map;
    }

    @Override
    public void verifyResetOtp(String email, String otp, String password) {
        Optional<PasswordResetToken>  resetTokenOP= passwordResetTokenRepository.findByEmail(email);
        if(resetTokenOP.isEmpty()){
            throw  new ApiException("Please send the otp first");
        }
        PasswordResetToken resetToken = resetTokenOP.get();
        Duration duration = Duration.between(resetToken.getCreatedTime(), LocalDateTime.now());
        long minutes = duration.toMinutes();

        if(minutes>5){
            throw  new ApiException("Otp is expire !! valid for 5Min Only");
        }
        if (!otp.equals(resetToken.getOtp())){
            throw  new ApiException("Invalid Otp !!");

        }else{
            passwordResetTokenRepository.delete(resetToken);
            User user = this.userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("email", "emailId: " + email, 0));
            user.setPassword(passwordEncoder.encode(password));
            this.userRepository.save(user);

        }
    }

    @Override
    public void resetPassword(String email, String password) {
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("email", "emailId: " + email, 0));
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);

    }


    private void doAuthenticate(String email, String password) {

//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        UsernamePasswordAuthenticationToken authentication =   new UsernamePasswordAuthenticationToken(email, null);
        log.info("authenticate email: {}, password: {}", authentication, password);
        try {
//            Authentication authenticate = manager.authenticate(authentication);
            log.info("authenticate checking: {}", "bypass security");


        } catch (BadCredentialsException e) {
            log.info("Bad Credentials: {}", e.getMessage());
            throw new BadCredentialsException("Wrong Password !!");
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }
}
