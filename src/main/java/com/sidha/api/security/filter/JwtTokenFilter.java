package com.sidha.api.security.filter;


import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sidha.api.service.JwtService;
import com.sidha.api.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

      @Autowired
      private final JwtService jwtService;
      @Autowired
      private final UserService userService;

      @Override
      protected void doFilterInternal(HttpServletRequest request,
                  HttpServletResponse response,
                  FilterChain filterChain)
                  throws ServletException, IOException {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userName;
            if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
                  filterChain.doFilter(request, response);
                  return;
            }
            jwt = authHeader.substring(7);
            log.debug("JWT - {}", jwt.toString());
            userName = jwtService.extractUserName(jwt);
            if (StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
                  UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userName);
                  if (jwtService.isTokenValid(jwt, userDetails)) {
                        log.debug("User - {}", userDetails);
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        context.setAuthentication(authToken);
                        SecurityContextHolder.setContext(context);
                  }
            }
            filterChain.doFilter(request, response);
      }
}