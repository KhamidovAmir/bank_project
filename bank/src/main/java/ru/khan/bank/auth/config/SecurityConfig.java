package ru.khan.bank.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.khan.bank.auth.service.TokenService;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain springSecurityFilterChain(HttpSecurity http,
                                                         TokenService tokenService) throws Exception {
         return http.

                 csrf(csrf -> csrf.disable())
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers("/auth/register", "/auth/login").permitAll()
                         .anyRequest().authenticated()
                 )
                 .addFilterBefore(
                         new JwtAuthenticationFilter(tokenService),
                         UsernamePasswordAuthenticationFilter.class
                 )
                 .build();
    }

    @Bean
    public PasswordEncoder   passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
