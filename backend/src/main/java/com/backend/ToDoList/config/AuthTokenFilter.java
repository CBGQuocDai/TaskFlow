package com.backend.ToDoList.config;

import com.backend.ToDoList.errors.AppException;
import com.backend.ToDoList.enums.ErrorCode;
import com.backend.ToDoList.service.impl.UserServiceImpl;
import com.backend.ToDoList.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserServiceImpl userDetailsService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = request.getHeader("Authorization");
            if(jwt != null && jwt.startsWith("Bearer ")) {
                jwt = jwt.substring(7);
                if(jwtUtils.validateToken(jwt)) {
                    Claims body =jwtUtils.getContent(jwt);
                    String email = String.valueOf(jwtUtils.getEmailFormToken(jwt));
                    if (redisTemplate.hasKey(body.getId())) {
                        throw new AppException(ErrorCode.UNAUTHORIZED);
                    }
                    UserDetails u =  userDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken
                            (u, null, u.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authenticated user: " + u.getUsername());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        filterChain.doFilter(request, response);
    }
}
