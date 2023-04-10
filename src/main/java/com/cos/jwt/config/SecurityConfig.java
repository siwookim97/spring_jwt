package com.cos.jwt.config;

import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.filter.MyFilter3;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // BasicAuthenticationFIlter가 작동하기 전에 MyFilter1작동
        //http.addFilterBefore(new MyFilter1(), BasicAuthenticationFilter.class);
        http.csrf().disable();
        // 세션을 사용하지 않게 개발한다.
        http
                .addFilterBefore(new MyFilter3(), LogoutFilter.class)
                //.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .addFilter(new JwtAuthenticationFilter(new AuthenticationManager() {
                    @Override
                    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                        return null;
                    }
                })) // AuthenticationManager를 던져주어야 한다
                .httpBasic().disable().authorizeHttpRequests()
                        .requestMatchers("/api/v1/user/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers("api/v1/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                .and();

        return http.build();
    }

}
