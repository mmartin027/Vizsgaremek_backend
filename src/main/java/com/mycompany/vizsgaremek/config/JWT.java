package com.mycompany.vizsgaremek.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

public class JWT {
    
    private static final String JWT_SECRET = " 71mJh&ADZ#rs6x:pIeT)JModkG/z^fWo*cFVFbP>Ankj29}A4SX/uKWko]/pR3Bg";
    
    public static String createJWT(User u){
        
        Instant now = Instant.now();
        
        String token = Jwts.builder()
                .setIssuer("IAKK")
                .setSubject("Backend_alapok")
                .claim("id", u.getId())
                .claim("phone", u.getPhone())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.DAYS)))
                .signWith(
                        SignatureAlgorithm.HS256,
                        TextCodec.BASE64.decode(JWT_SECRET)
                        )
                .compact();
        return token;
    }
    public static Boolean validateJwt (String token) {
        
        try{
            
            byte[] secretArray = Base64.getDecoder().decode(JWT_SECRET);
            Jws<Claims> tokenResult = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(secretArray)).parseClaimsJws(token);
            
            Integer userIdClaim = tokenResult.getBody().get("id", Integer.class);
            
            if (userIdClaim == null){
                return false;
            } else {
                return true;
            }
            
        }catch(SignatureException | MalformedJwtException | IllegalArgumentException | UnsupportedJwtException ex){
            return false;
        }catch (ExpiredJwtException ex){
            return null;
        }
        
    }
}
