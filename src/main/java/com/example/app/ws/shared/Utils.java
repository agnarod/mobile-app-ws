package com.example.app.ws.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.app.ws.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class Utils {

	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public String generateUserId(int length) {
		return generateRandomString(length);
	}

	public String generateAddressId(int length) {
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {
		StringBuilder returnValue = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}

		return new String(returnValue);
	}

	public boolean hasTokenExpired(String token) {
		boolean returnValue = false;
		try {
			Claims claims  = Jwts.parser()
			.setSigningKey( SecurityConstants.getToken() )
			.parseClaimsJws(token).getBody();
		
			Date tokenExpiratDate = claims.getExpiration();
			Date todayDate = new Date();
			returnValue = tokenExpiratDate.before(todayDate);
		}
		catch (ExpiredJwtException e) {
			returnValue = true;
			// TODO: handle exception
		}
		return returnValue;
		
	}

	public String generateToken(String userId, String type) {

		long expiration_time = 0;
		switch (type) {
		case SecurityConstants.PASSWORD_TYPE_TOKEN_STRING:
			expiration_time = SecurityConstants.PASSWORD_EXPIRATION_TIME;
			break;
		default:
			expiration_time = SecurityConstants.DEFAULT_EXPIRATION_TIME;
			break;
		}

		String token = Jwts.builder().setSubject(userId)
				.setExpiration(new Date(System.currentTimeMillis() + expiration_time))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getToken()).compact();
		return token;
	}
}
