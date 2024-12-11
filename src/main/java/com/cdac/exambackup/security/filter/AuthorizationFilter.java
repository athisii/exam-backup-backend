package com.cdac.exambackup.security.filter;

import com.cdac.exambackup.dto.IdentityContext;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.exception.ForbiddenException;
import com.cdac.exambackup.util.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.cdac.exambackup.constant.ApplicationConstant.PERMISSIONS;
import static com.cdac.exambackup.constant.ApplicationConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author athisii
 * @version 1.0
 * @since 5/15/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class AuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    IdentityContext identityContext;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtProvider jwtProvider;

    @Override

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("**Servlet Path => {}", request.getServletPath());
        String servletPath = request.getServletPath();
        String[] securityExemptedPaths = {"/login", "/password-reset/initiate", "/password-reset/confirm", "/refresh-token", "/api-docs", "/api-docs/.*", "/actuator/health", "/swagger.*/.*"};
        for (String path : securityExemptedPaths) {
            if (servletPath.matches(path)) {
                log.info("**Authorization not required for path: {}", path);
                filterChain.doFilter(request, response);
                return;
            }
        }
        log.info("**Authorization Filter Executing");
        // verify token and allow if valid otherwise return
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("Token not starting with 'Bearer ' or token is missing");
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            var responseDto = new ResponseDto<>("Either token not starting with 'Bearer ' or token is missing in the header", false, null);
            objectMapper.writeValue(response.getOutputStream(), responseDto);
            return;
        }
        //removes Bearer and whitespace from authorization
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        try {
            jwtProvider.checkTokenValidity(token);
            identityContext.setId(jwtProvider.findLongValueFromToken(token, "id"));
            identityContext.setUserId(jwtProvider.getSubjectFromToken(token));
            identityContext.setPermissions(jwtProvider.findListValueFromToken(token, PERMISSIONS));
            identityContext.setName(jwtProvider.findStringValueFromToken(token, "name"));
            log.info("**authorization done.");
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Invalid Token. {}", ex.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ResponseDto<?> responseDto;
            if (ex.getCause().getClass() == ForbiddenException.class) {
                responseDto = new ResponseDto<>(ex.getMessage().split(":")[2], false, null);
            } else {
                responseDto = new ResponseDto<>("Invalid token. Some error occurred while parsing token.", false, null);
            }
            objectMapper.writeValue(response.getOutputStream(), responseDto);
        }
    }
}
