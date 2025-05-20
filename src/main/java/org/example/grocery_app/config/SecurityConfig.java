package org.example.grocery_app.config;




import org.example.grocery_app.security.JwtAuthenticationEntryPoint;
import org.example.grocery_app.security.JwtAuthenticationFilter;
import org.example.grocery_app.serviceImplementation.CustomLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationEntryPoint point;
    @Autowired
    private JwtAuthenticationFilter filter;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomLogoutHandler customLogoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeRequests()
                .requestMatchers(HttpMethod.GET).permitAll()
                .requestMatchers(HttpMethod.POST).permitAll()
                .requestMatchers(HttpMethod.PUT).permitAll()
                .requestMatchers(HttpMethod.DELETE).permitAll()
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/api/users/").permitAll()
                .requestMatchers("/api/webhook").permitAll()
//                .requestMatchers("/api/messages/send","/api/users/create").permitAll()
//                .requestMatchers("/api/users/").permitAll()
                .anyRequest()
                .authenticated()
                .and().exceptionHandling(ex -> ex.authenticationEntryPoint(point))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout
                        .addLogoutHandler(customLogoutHandler) // Custom logout handler to blacklist token
                        .logoutUrl("/logout") // Logout URL
                        .permitAll()
                );

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }


}