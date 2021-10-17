package com.labs.authentication.security.JWT;

import com.labs.authentication.dto.JSONWebTokenDTO;
import com.labs.authentication.entity.UserPrincipal;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import io.jsonwebtoken.*;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JWTProvider {

  private static final Logger logger = LoggerFactory.getLogger(
    JWTProvider.class
  );

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private int expiration;

  public String generateToken(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    List<String> roles = userPrincipal
      .getAuthorities()
      .stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.toList());
    return Jwts
      .builder()
      .setSubject(userPrincipal.getUsername())
      .claim("roles", roles)
      .setIssuedAt(new Date())
      .setExpiration(new Date(new Date().getTime() + expiration * 180))
      .signWith(SignatureAlgorithm.HS512, secret.getBytes())
      .compact();
  }

  public String getUsernameFromToken(String token) {
    return Jwts
      .parser()
      .setSigningKey(secret.getBytes())
      .parseClaimsJws(token)
      .getBody()
      .getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("token mal formado");
    } catch (UnsupportedJwtException e) {
      logger.error("token no soportado");
    } catch (ExpiredJwtException e) {
      logger.error("token expirado");
    } catch (IllegalArgumentException e) {
      logger.error("token vacío");
    } catch (SignatureException e) {
      logger.error("fail en la firma");
    }
    return false;
  }

  public String refreshToken(JSONWebTokenDTO jwtDto) throws ParseException {
    try {
      Jwts
        .parser()
        .setSigningKey(secret.getBytes())
        .parseClaimsJws(jwtDto.getToken());
    } catch (ExpiredJwtException e) {
      JWT jwt = JWTParser.parse(jwtDto.getToken());
      JWTClaimsSet claims = jwt.getJWTClaimsSet();
      String username = claims.getSubject();
      List<String> roles = (List<String>) claims.getClaim("roles");

      return Jwts
        .builder()
        .setSubject(username)
        .claim("roles", roles)
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + expiration))
        .signWith(SignatureAlgorithm.HS512, secret.getBytes())
        .compact();
    }
    return null;
  }
}
