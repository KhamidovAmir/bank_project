package ru.khan.bank.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.khan.bank.auth.service.TokenService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http,
                                                         TokenService tokenService) {
         return http.

                 csrf(csrf -> csrf.disable())
                 .sessionManagement(session -> session
                         .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
