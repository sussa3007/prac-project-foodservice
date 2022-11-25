package com.example.foodserviceapp.auth;

import com.example.foodserviceapp.auth.utils.JwtAuthorityUtils;
import com.example.foodserviceapp.auth.utils.Token;
import com.example.foodserviceapp.exception.ErrorCode;
import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenizer {

    @Getter
    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    @Getter
    @Value("${ACCESS_TOKEN_EXPIRATION_MINUTES}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${REFRESH_TOKEN_EXPIRATION_MINUTES}")
    private int refreshTokenExpirationMinutes;

    private final JwtAuthorityUtils jwtAuthorityUtils;

    public String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Token generateToken(
            Map<String, Object> claims,
            String subject,
            String base64EncodedSecretKey
    ) {
        Key key = getKeyFromBase64EncodedSecretKey(base64EncodedSecretKey);

        return new Token(
                Jwts.builder()
                        .setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(Calendar.getInstance().getTime())
                        .setExpiration(getTokenExpiration(accessTokenExpirationMinutes))
                        .signWith(key)
                        .compact(),
                Jwts.builder()
                        .setSubject(subject)
                        .setIssuedAt(Calendar.getInstance().getTime())
                        .setExpiration(getTokenExpiration(refreshTokenExpirationMinutes))
                        .signWith(key)
                        .compact());

    }

    public Token delegateToken(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", member.getEmail());
        claims.put("roles", member.getRoles());

        String subject = member.getEmail();

        String base64SecretKey = encodeBase64SecretKey(getSecretKey());

        return generateToken(claims, subject, base64SecretKey);
    }

    private void reIssueToken(
            String refreshToken,
            String base64SecretKey,
            HttpServletResponse response
    ) throws IOException {
        Map<String, Object> claims = new HashMap<>();
        String subject = getEmail(refreshToken);
        claims.put("username", subject);
        claims.put("roles", jwtAuthorityUtils.createRoles(subject));

        Token token = generateToken(claims, subject, base64SecretKey);
        String newAccessToken = token.getAccessToken();
        String newRefreshToken = token.getRefreshToken();

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.setHeader("Refresh", newRefreshToken);
    }

    public void verifyRefreshToken(
            String refreshToken,
            HttpServletResponse response
    ) throws IOException {
        String base64SecretKey = encodeBase64SecretKey(getSecretKey());
        try {
            verifySignature(refreshToken, base64SecretKey);
            // todo refreshToken 검증되면, 토큰 재발급
            reIssueToken(refreshToken, base64SecretKey, response);
        } catch (ExpiredJwtException ee) {
            throw new ServiceLogicException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (Exception e) {
            throw e;
        }
    }

    private Key getKeyFromBase64EncodedSecretKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void verifySignature(String jws, String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedSecretKey(base64EncodedSecretKey);

        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
    }

    public Jws<Claims> getClaims(String jws, String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedSecretKey(base64EncodedSecretKey);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
    }

    public Date getTokenExpiration(int expirationMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationMinutes);
        return calendar.getTime();
    }

    // todo refactoring중 활용할 곳이 없다면 제거 예정
    public String getEmail(String token) {
        Key key = getKeyFromBase64EncodedSecretKey(encodeBase64SecretKey(secretKey));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
