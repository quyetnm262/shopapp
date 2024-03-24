package com.ecommerce.shopapp.components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {



    @Value("${jwt.expiration}")
    private int expiration; //save to environment variable

    @Value("${jwt.secretKey}")
    private String secretKey;
    public String generateToken(com.ecommerce.shopapp.models.User user) {

        Map<String, Object> claim = new HashMap<>();
        claim.put("phoneNumber", user.getPhoneNumber());
        claim.put("userId", user.getId());
        this.generateSecretKey();

        try{

            String token = Jwts.builder()
                    .setClaims(claim)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;

        }catch (Exception e){

            //Co the dung logger
            System.out.println("Cannot create jwt token, error "+e.getMessage());
            return null;
        }
    }

    private Key getSignInKey(){

        byte[] bytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private void generateSecretKey(){
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        this.secretKey = Encoders.BASE64.encode(keyBytes);
    }

    public Claims extractAllClaim(String token){

        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){

        final Claims claims = this.extractAllClaim(token);
        return claimResolver.apply(claims);
    }

    public boolean isTokenExpired(String token){

        Date expirationDate = this.extractClaim(token, Claims::getExpiration);

        return expirationDate.before(new Date());
    }

    public String extractPhoneNumber(String token){

        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token,
                                 UserDetails userDetails){

        String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

}
