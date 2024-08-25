package com.hyunwns.demoweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    // Spring Security 버전 5.7.0-M2 이후 부터는 WebSecurityConfigurerAdapter가 Deprecated 됨
    // 기존에 security 예외 url을 설정하던 antMatchers 는 아예 삭제되었다.
    // 빈으로 등록해서 사용할 것으로 권장됨

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/signup").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/").permitAll())

                .csrf( csrf -> csrf.disable() );

        // 인가 처리


        return http.build();

    }
}
