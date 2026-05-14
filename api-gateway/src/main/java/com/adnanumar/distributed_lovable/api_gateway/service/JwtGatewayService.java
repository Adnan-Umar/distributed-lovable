package com.adnanumar.distributed_lovable.api_gateway.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtGatewayService {

    @Value("${jwt.secretKey}")
    String secretKey;

    public void validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

}
