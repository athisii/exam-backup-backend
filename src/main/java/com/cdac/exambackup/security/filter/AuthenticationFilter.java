package com.cdac.exambackup.security.filter;

import com.cdac.exambackup.dao.AppUserDao;
import com.cdac.exambackup.dto.LoginFormDto;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.dto.TokenResDto;
import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.entity.AppUserDetails;
import com.cdac.exambackup.enums.TokenType;
import com.cdac.exambackup.util.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author athisii
 * @version 1.0
 * @since 5/14/24
 */


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    final AuthenticationManager authenticationManager;
    final JwtProvider jwtProvider;
    final AppUserDao appUserDao;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("**attempting to authenticate");
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        LoginFormDto loginFormDto;
        try {
            loginFormDto = objectMapper.readValue(request.getInputStream(), LoginFormDto.class);
        } catch (IOException ex) {
            log.error("Error parsing request form value. ", ex);
            throw new AuthenticationCredentialsNotFoundException("Unable to parse login form");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginFormDto.username(), loginFormDto.password());
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //logic to create token
        log.info("**successfully authenticated");
        AppUserDetails appUserDetails = (AppUserDetails) authResult.getPrincipal();
        String token = jwtProvider.generateTokenFromAppUser(appUserDetails.getAppUser(), TokenType.ACCESS_TOKEN);
        String refreshToken = jwtProvider.generateTokenFromAppUser(appUserDetails.getAppUser(), TokenType.REFRESH_TOKEN);
        boolean isFirstLogin = appUserDetails.getAppUser().isFirstLogin();
        // update db.
        if (isFirstLogin) {
            AppUser appUser = appUserDetails.getAppUser();
            appUser.setFirstLogin(false);
            appUserDao.save(appUser);
        }
        var responseDto = new ResponseDto<>("Token fetched successfully.", new TokenResDto(token, refreshToken, isFirstLogin));
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), responseDto);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        //logic to limit failure attempts
        log.info("**failed to authenticate");
        var responseDto = new ResponseDto<>("Bad credentials", false, null);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        objectMapper.writeValue(response.getWriter(), responseDto);
    }
}
