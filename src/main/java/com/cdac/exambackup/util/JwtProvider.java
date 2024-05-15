package com.cdac.exambackup.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.enums.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.cdac.exambackup.constant.ApplicationConstant.*;


/**
 * @author athisii
 * @version 1.0
 * @since 5/15/24
 */

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.token.validity}")
    private Long accessTokenValidity;

    @Value("${jwt.refresh.token.validity}")
    private Long refreshTokenValidity;

    private JwtProvider() {
    }

    public String generateTokenFromAppUser(AppUser appUser, TokenType type) {
        Algorithm algorithm = Algorithm.HMAC512(secret.getBytes());
        return JWT.create()
                .withSubject(appUser.getUserId())
                .withIssuer(TOKEN_ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ((TokenType.ACCESS_TOKEN.equals(type) ? accessTokenValidity : refreshTokenValidity) * 24 * 60 * 60 * 1000)))
                .withClaim(PERMISSIONS, getPermissionsFromAppUser(appUser))
                .withClaim("id", appUser.getId())
                .withClaim("name", appUser.getName()) // place holder
                .withClaim("key", (String) null) // place holder
                .sign(algorithm);
    }

    public List<Integer> getPermissionsFromAppUser(AppUser appUser) {
        return List.of(appUser.getRole().getCode());
    }

    public void checkTokenValidity(String token) {
        JWTVerifier verifier = getJWTVerifier();
        Date expiration = verifier.verify(token).getExpiresAt();
        if (expiration.before(new Date())) {
            throw new JWTVerificationException("The Token has expired on " + expiration);
        }
    }

    public String generateTokenFromRefreshToken(String refreshToken) {
        checkTokenValidity(refreshToken);
        Algorithm algorithm = Algorithm.HMAC512(secret.getBytes());
        JWTVerifier verifier = getJWTVerifier();

        return JWT.create()
                .withSubject(getSubjectFromToken(refreshToken))
                .withIssuer(verifier.verify(refreshToken).getIssuer())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidity))
                .withClaim(PERMISSIONS, findListValueFromToken(refreshToken, PERMISSIONS))
                .withClaim("id", findLongValueFromToken(refreshToken, "id"))
                .withClaim("name", findStringValueFromToken(refreshToken, "name"))
                .withClaim("key", (String) null) // place holder
                .sign(algorithm);
    }


    public String getSubjectFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    public String findStringValueFromToken(String token, String name) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(name).asString();
    }

    public Integer findIntValueFromToken(String token, String name) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(name).asInt();

    }

    public Long findLongValueFromToken(String token, String name) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(name).asLong();
    }


    public List<Long> findListValueFromToken(String token, String name) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(name).asList(Long.class);

    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(TOKEN_ISSUER).build();
        } catch (JWTVerificationException ignored) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

}