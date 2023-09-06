package com.supportportal.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import static com.supportportal.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.supportportal.domain.UserPrincipal;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Generate a JWT token for a given UserPrincipal.
     *
     * @param userPrincipal The UserPrincipal for whom the token is generated.
     * @return The generated JWT token.
     */
    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(GET_ARRAYS_LLC)
                .withAudience(GET_ARRAYS_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    /**
     * Get a list of GrantedAuthority objects from a JWT token.
     *
     * @param token The JWT token.
     * @return A list of GrantedAuthority objects.
     */
    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimFromToken(token);
        return stream(claims)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Get Authentication object from a username, list of authorities, and an HTTP request.
     *
     * @param username    The username.
     * @param authorities List of authorities.
     * @param request     The HTTP request.
     * @return The Authentication object.
     */
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPasswordAuthToken
                = new UsernamePasswordAuthenticationToken(username, null, authorities);

        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    /**
     * Check if a JWT token is valid for a given username.
     *
     * @param username The username.
     * @param token    The JWT token.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String username, String token) {
        JWTVerifier verifier = getJWTVerifier();
        boolean isValid = StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);

        if (!isValid) {
            log.error("JWT token for user {} is invalid or expired.", username);
        }

        return isValid;
    }

    /**
     * Get the subject (username) from a JWT token.
     *
     * @param token The JWT token.
     * @return The subject (username).
     */
    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    // Private Methods
    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private String[] getClaimFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(GET_ARRAYS_LLC).build();
        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

    private String[] getClaimFromUser(UserPrincipal user) {
        List<String> authorities = new ArrayList<>();
        user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));
        return authorities.toArray(new String[0]);
    }

}
