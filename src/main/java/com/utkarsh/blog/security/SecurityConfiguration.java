package com.utkarsh.blog.security;

import com.utkarsh.blog.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login-page","/signup","/posts","/admin-signup").permitAll()
                        .requestMatchers("/posts/new-post","/posts/add")
                                                   .hasAnyRole("USER","ADMIN")
                        .requestMatchers("/posts/*/edit").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/posts/*/delete").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/posts/**").permitAll()
                        .requestMatchers("/comments/post/*").permitAll()
                        .requestMatchers("/comments/*/edit").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/comments/*/delete").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/tags/new").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login-page")
                        .loginProcessingUrl("/authenticateUser")
                        .defaultSuccessUrl("/posts",true)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}