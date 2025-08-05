package com.example.uberauthservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService implements CommandLineRunner {

    @Value("${jwt.expiry}")
    private int expiry;

    @Value("${jwt.secret}")
    private String SECRET;

    public String createToken(Map<String,Object> payload,String email){
        Date now=new Date();
        Date expiryDate=new Date(now.getTime() + expiry*1000L);
//        SecretKey key= Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claims(payload)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiryDate)
                .subject(email)
                .signWith(getSignKey())
                .compact();

    }
    public Claims extractAllPayloads(String token){
//        SecretKey key= Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
      return Jwts
              .parser()
              .setSigningKey(getSignKey())
              .build()
              .parseClaimsJws(token)
              .getBody();
    }
    public <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims=extractAllPayloads(token);
        return claimResolver.apply(claims);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());

    }
    public Key getSignKey(){
        return  Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
    public Boolean validateToken(String token,String email){
        final String userEmailFetchFromToken=extractEmail(token);
        return (userEmailFetchFromToken.equals(email)&& !isTokenExpired(token));
    }

    public String createToken(String email){
        return createToken(new HashMap<>(),email);
    }
    public String extractPayload(String token,String payloadKey){
       Claims claim=extractAllPayloads(token);
      return claim.get(payloadKey).toString();
    }
    @Override
    public void run(String... args) throws Exception {
        Map<String,Object> payload=new HashMap<>();
        payload.put("email","viv@gmail.com");
        payload.put("phoneNumber","8218007147");
String result=createToken(payload,"vivek");
System.out.println("Generted token ishello "+result);
System.out.println(extractPayload(result,"email"));
    }
}
