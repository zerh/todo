package com.example.todo.util;

import io.jsonwebtoken.Claims; 
import io.jsonwebtoken.Jwts; 
import io.jsonwebtoken.SignatureAlgorithm; 
import io.jsonwebtoken.io.Decoders; 
import io.jsonwebtoken.security.Keys; 
import org.springframework.security.core.userdetails.UserDetails; 

import java.security.Key; 
import java.util.Date; 
import java.util.HashMap; 
import java.util.Map; 
import java.util.function.Function; 

public class JwtUtil { 

	private static final short SECONDS = 1000;

	public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437"; 
	public static final long JWT_EXPIRATION = SECONDS * 1000;
	
	public static String generateToken(String userName) { 
		Map<String, Object> claims = new HashMap<>(); 
		return createToken(claims, userName); 
	} 

	private static String createToken(Map<String, Object> claims, String userName) { 
		return Jwts.builder() 
				.setClaims(claims) 
				.setSubject(userName) 
				.setIssuedAt(new Date(System.currentTimeMillis())) 
				.setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION)) 
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact(); 
	} 

	private static Key getSignKey() { 
		byte[] keyBytes= Decoders.BASE64.decode(SECRET); 
		return Keys.hmacShaKeyFor(keyBytes); 
	} 

	public static String extractUsername(String token) { 
		return extractClaim(token, Claims::getSubject); 
	} 

	public static Date extractExpiration(String token) { 
		return extractClaim(token, Claims::getExpiration); 
	} 

	public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { 
		final Claims claims = extractAllClaims(token); 
		return claimsResolver.apply(claims); 
	} 

	private static Claims extractAllClaims(String token) { 
		return Jwts 
				.parserBuilder() 
				.setSigningKey(getSignKey()) 
				.build() 
				.parseClaimsJws(token) 
				.getBody(); 
	} 

	public static Boolean isTokenExpired(String token) { 
		return extractExpiration(token).before(new Date()); 
	} 

	public static Boolean validateToken(String token, UserDetails userDetails) { 
		final String username = extractUsername(token); 
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); 
	} 
} 
