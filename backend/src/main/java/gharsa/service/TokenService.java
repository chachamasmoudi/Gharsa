package gharsa.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import gharsa.model.User;

import java.util.Date;
import java.util.Optional;

public class TokenService {

    private static final long VALIDITY_MS = 24L * 60 * 60 * 1000; // 24 h
    private final Algorithm algorithm;

    public TokenService() {
        String secret = System.getenv("GHARSA_JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            secret = "dev-only-secret-change-me-please-32chars";
            System.out.println("ATTENTION : GHARSA_JWT_SECRET non défini, secret de développement utilisé");
        }
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String create(User u) {
        return JWT.create()
                .withSubject(String.valueOf(u.id()))
                .withClaim("role", u.role())
                .withExpiresAt(new Date(System.currentTimeMillis() + VALIDITY_MS))
                .sign(algorithm);
    }

    public Optional<DecodedJWT> verify(String token) {
        try {
            return Optional.of(JWT.require(algorithm).build().verify(token));
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }
}