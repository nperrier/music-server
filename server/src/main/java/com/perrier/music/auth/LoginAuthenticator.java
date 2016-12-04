package com.perrier.music.auth;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.perrier.music.config.IConfiguration;
import com.perrier.music.config.OptionalProperty;
import com.perrier.music.config.Property;
import com.perrier.music.server.auth.UnauthorizedException;

/**
 * Handles login authentication and token creation/validation
 */
public class LoginAuthenticator {

	private static final Logger log = LoggerFactory.getLogger(LoginAuthenticator.class);

	private static final Property<String> USERNAME = new Property<>("auth.admin.username");
	private static final Property<String> PASSWORD = new Property<>("auth.admin.password");
	private static final Property<String> SECRET_KEY = new Property<>("auth.secretkey");
	private static final OptionalProperty<Boolean> AUTH_DISABLED = new OptionalProperty<>("auth.disabled", false);

	private final IConfiguration config;

	@Inject
	public LoginAuthenticator(IConfiguration config) {
		this.config = config;
	}

	/**
	 * Authenticate user credentials and issue an auth token if valid
	 *
	 * @param username
	 * @param password
	 * @return jwt token if credentials are valid
	 * @throws UnauthorizedException if credentials are not valid
	 */
	public String authenticate(final String username, final String password) throws UnauthorizedException {
		final String authUser = config.getRequiredString(USERNAME);
		final String authPass = config.getRequiredString(PASSWORD);

		// validate username and password
		// TODO: use hash of password
		if (!authUser.equals(username) || !authPass.equals(password)) {
			throw new UnauthorizedException();
		}

		String token = issueToken(username);
		return token;
	}

	/**
	 * Create an auth token for the user
	 *
	 * @param username the user to issue the token for
	 * @return auth token string
	 */
	private String issueToken(String username) {
		// Prepare JWT with claims set
		JWTClaimsSet claimsSet = new JWTClaimsSet();
		claimsSet.setSubjectClaim(username);
		final long expiration = Instant.now().plus(90, ChronoUnit.DAYS).toEpochMilli();
		claimsSet.setExpirationTimeClaim(expiration);

		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
		final byte[] secretKey = config.getRequiredString(SECRET_KEY).getBytes();
		JWSSigner signer = new MACSigner(secretKey);
		try {
			signedJWT.sign(signer);
		} catch (JOSEException e) {
			throw new RuntimeException("Unable to sign jwt token", e);
		}

		String token = signedJWT.serialize();
		return token;
	}

	/**
	 * Check if it was issued by the server and if it's not expired
	 *
	 * @param token the token to validate
	 * @throws UnauthorizedException if the token is invalid
	 */
	public ReadOnlyJWTClaimsSet validateToken(String token) throws UnauthorizedException {
		if (token == null) {
			log.debug("No token found to validate");
			throw new UnauthorizedException();
		}

		final byte[] secretKey = config.getRequiredString(SECRET_KEY).getBytes();

		SignedJWT signedJWT = null;
		try {
			signedJWT = SignedJWT.parse(token);
		} catch (ParseException e) {
			log.error("Unable to parse token", e);
			throw new UnauthorizedException();
		}

		JWSVerifier verifier = new MACVerifier(secretKey);

		boolean valid = false;
		try {
			valid = signedJWT.verify(verifier);
		} catch (JOSEException e) {
			log.error("Unable to verify token", e);
			throw new UnauthorizedException(e);
		}

		if (!valid) {
			log.warn("Token is invalid");
			throw new UnauthorizedException();
		}

		ReadOnlyJWTClaimsSet jwtClaimsSet = null;

		try {
			jwtClaimsSet = signedJWT.getJWTClaimsSet();
		} catch (ParseException e) {
			log.error("Unable to parse token claims set", e);
			throw new UnauthorizedException(e);
		}

		final String subject = jwtClaimsSet.getSubjectClaim();
		// check if subject exists
		final String username = config.getRequiredString(USERNAME);
		if (!username.equalsIgnoreCase(subject)) {
			log.debug("Invalid claims set for username: {}", username);
			throw new UnauthorizedException();
		}

		final long expiration = jwtClaimsSet.getExpirationTimeClaim();
		// check if expiration is past current date
		if (System.currentTimeMillis() >= expiration) {
			log.debug("Invalid claims set for username={}: token is expired={}", username, expiration);
			throw new UnauthorizedException();
		}

		return jwtClaimsSet;
	}

	public boolean isAuthDisabled() {
		final Boolean authDisabled = this.config.getOptionalBoolean(AUTH_DISABLED);
		return authDisabled;
	}

	// TODO
	private void hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] salt = new byte[16];
		SecureRandom random = new SecureRandom();
		random.nextBytes(salt);

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] hash = f.generateSecret(spec).getEncoded();

		Base64.Encoder enc = Base64.getEncoder();
		System.out.printf("salt: %s%n", enc.encodeToString(salt));
		System.out.printf("hash: %s%n", enc.encodeToString(hash));
	}
}

