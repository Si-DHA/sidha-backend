package com.sidha.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.sidha.api.security.jwt.JwtTokenFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig  {
        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private JwtTokenFilter jwtTokenFilter;

        @Bean
        @Order(1)
        public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
                http.securityMatcher("/api/**")
                        .csrf(AbstractHttpConfigurer::disable)
                        .headers(headers -> headers
                                .frameOptions(frameOptions -> frameOptions.disable()))
                        .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                                .requestMatchers("/api/**").permitAll()
                                .anyRequest().permitAll())
                        .sessionManagement(
                                sessionManagement -> sessionManagement
                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
                http
                        .csrf(Customizer.withDefaults())
                        .authorizeHttpRequests(requests -> requests
                                .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                                .anyRequest().permitAll())
                        .formLogin(form -> form
                                .loginPage("/login")
                                .permitAll()
                                .defaultSuccessUrl("/"))
                        .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/login"));

                return http.build();
        }



        @Bean
        public BCryptPasswordEncoder encoder() {
                return new BCryptPasswordEncoder();
        }

        @Autowired
        public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
                auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
        }
}
