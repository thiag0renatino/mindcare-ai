package com.fiap.mindcare.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fiap.mindcare.dto.TokenDTO;
import com.fiap.mindcare.service.exception.InvalidJwtAuthenticationException;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private Long validityInMilliseconds;

    private final UserDetailsService userDetailsService;

    private Algorithm algorithm;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private static final String TOKEN_TYPE = "token_type";
    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    // 1 Cria AccessToken + RefreshToken
    public TokenDTO createAccessToken(String email, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String accessToken = getAccessToken(email, roles, now, validity);
        String refreshToken = getRefreshToken(email, roles, now);

        return new TokenDTO(email, refreshToken, accessToken, validity, now, true);
    }

    private String getAccessToken(String email, List<String> roles, Date now, Date validity) {
        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return JWT.create()
                .withSubject(email)
                .withIssuer(issuerUrl)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("roles", roles)
                .withClaim(TOKEN_TYPE, TOKEN_TYPE_ACCESS)
                .sign(algorithm);
    }

    private String getRefreshToken(String email, List<String> roles, Date now) {
        Date refreshValidity = new Date(now.getTime() + (validityInMilliseconds * 3));
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(now)
                .withExpiresAt(refreshValidity)
                .withClaim("roles", roles)
                .withClaim(TOKEN_TYPE, TOKEN_TYPE_REFRESH)
                .sign(algorithm);
    }

    // 2 Refresh
    public TokenDTO refreshToken(String refreshToken, String expectedEmail) {
        String token = refreshToken;
        if (tokenContainsBearer(token)) {
            token = token.substring("Bearer ".length());
        }

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decoded = verifier.verify(token);

        String username = decoded.getSubject();

        List<String> roles = decoded.getClaim("roles").asList(String.class);

        String tokenType = decoded.getClaim(TOKEN_TYPE).asString();
        if (!TOKEN_TYPE_REFRESH.equals(tokenType)) {
            throw new InvalidJwtAuthenticationException("Invalid token type for refresh operation");
        }

        if (StringUtils.isNotBlank(expectedEmail) && !username.equalsIgnoreCase(expectedEmail)) {
            throw new InvalidJwtAuthenticationException("Refresh token does not belong to the provided user");
        }

        return createAccessToken(username, roles);
    }

    // 3 Cria um Authentication para o SecurityContext a partir do JWT
    public Authentication getAuthentication(String token) {
        DecodedJWT decoded = decodedToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(decoded.getSubject());
        return new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities()
        );
    }

    private DecodedJWT decodedToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    // 4 Lê token do header Authorization
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (tokenContainsBearer(bearerToken)) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    private static boolean tokenContainsBearer(String token) {
        return StringUtils.isNotBlank(token) && token.startsWith("Bearer ");
    }

    // 5 Extrair data de expiração do token
    public Date getExpirationDate(String token) {
        DecodedJWT decoded = decodedToken(token);
        return decoded.getExpiresAt();
    }

    // 6 Remove prefixo "Bearer " de um token
    public String resolveRawToken(String bearerToken) {
        if (tokenContainsBearer(bearerToken)) {
            return bearerToken.substring("Bearer ".length());
        }
        return bearerToken;
    }

    // 7 Validar token
    public boolean validateToken(String token) {
        try {
            DecodedJWT decoded = decodedToken(token);
            String tokenType = decoded.getClaim(TOKEN_TYPE).asString();
            boolean notExpired = decoded.getExpiresAt() == null || decoded.getExpiresAt().after(new Date());
            return TOKEN_TYPE_ACCESS.equals(tokenType) && notExpired;
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or Invalid JWT Token!");
        }
    }
}
