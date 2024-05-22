package org.example.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.entity.Role;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY;


    public String extractUserName(String token) {
        return extractClaim(token,Claims::getSubject);
    }
    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .setSigningKey(getSignInkey()).
                parseClaimsJws(token).
                getBody();
    }

    private Key getSignInkey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String generateToken( UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username=extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        List<?> roles= (List<Role>) userDetails.getAuthorities();

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .claim("role", roles.get(0).toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000L *60*24*60*60))
                .signWith(SignatureAlgorithm.HS256, getSignInkey())
                .compact();
    }
}
