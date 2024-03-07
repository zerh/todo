package com.example.todo.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.Optional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity; 
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.security.web.SecurityFilterChain; 
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import com.example.todo.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig { 

	@Bean
	OncePerRequestFilter requestFilter(UserDetailsService userDetailsService) {
		return new OncePerRequestFilter(){
			
			private String getTokenFromCookie(HttpServletRequest request) {
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						if (cookie.getName().equals("token")) {
							return cookie.getValue();
						}
					}
				}
				return null;
			}

			private String getToken(HttpServletRequest request) {
				String token = getTokenFromCookie(request);
				if(token==null){
					String authHeader = request.getHeader("Authorization");
					if (authHeader != null && authHeader.startsWith("Bearer ")) { 
						token = authHeader.substring(7); 
					} 
				}
				return token;
			}

			@SuppressWarnings("null")
			@Override
			protected void doFilterInternal(
					HttpServletRequest request, 
					HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {

				String token = getToken(request); 
				String username = null;
				
				if(token!=null){
					try{
						username = JwtUtil.extractUsername(token);
					} catch(ExpiredJwtException ex) { }	
				}
				
				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { 
					UserDetails userDetails = userDetailsService.loadUserByUsername(username); 
					
					if (JwtUtil.validateToken(token, userDetails)) { 
						UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); 
						SecurityContextHolder.getContext().setAuthentication(authToken); 
					} 
				} 

				filterChain.doFilter(request, response);
			}
		};
	}

	@Bean
	SecurityFilterChain securityFilterChain(
			HttpSecurity http, 
			OncePerRequestFilter requestFilter,
			AuthenticationProvider authenticationProvider,
			AuthenticationManager authenticationManager) throws Exception { 

		return http
			.authorizeHttpRequests(request->request.requestMatchers(
					"/logout",
					"/auth/**",
					"/login", 
					"/**" , 
					"/h2-console/**").permitAll()
				.requestMatchers("/todo/**").hasRole("USER")
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()	
			)
			.csrf(AbstractHttpConfigurer::disable)
			.headers(headers->headers.frameOptions(f->f.disable()))
			.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class)
			.authenticationManager(authenticationManager)
			.authenticationProvider(authenticationProvider)
			.build();
	}

	@Bean
	UserDetailsService userDetailsService(UserRepository userRepository) {
		return username->{
			Optional<User> userInfo = userRepository.findByUsername(username);
			if(userInfo.isEmpty()) {
				throw new UsernameNotFoundException("User not found");
			}
			UserDetailsImpl userInfoDetails = new UserDetailsImpl(userInfo.get());
			return userInfoDetails;
		};
	}


	@Bean
	PasswordEncoder passwordEncoder() {
		return new PasswordEncoder() {
			@Override
			public String encode(CharSequence rawPassword) {
				try {
					MessageDigest digest = MessageDigest.getInstance("SHA-256");
					byte[] encodedHash = digest.digest(rawPassword.toString().getBytes(StandardCharsets.ISO_8859_1));
					return bytesToHex(encodedHash);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				String encodedRawPassword = encode(rawPassword);
        		return encodedRawPassword.equals(encodedPassword);
			}

			private static String bytesToHex(byte[] hash) {
				StringBuilder hexString = new StringBuilder(2 * hash.length);
				for (byte b : hash) {
					
					String hex = Integer.toHexString(0xff & b);
					if (hex.length() == 1) {
						hexString.append('0');
					}
					hexString.append(hex);
				}
				return hexString.toString();
			}
		};
	}

	@Bean
	AuthenticationProvider authenticationProvider(
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder){

		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(/*AuthenticationProvider authenticationProvider*/ AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

} 

