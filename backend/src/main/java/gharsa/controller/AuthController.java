package gharsa.controller;

import gharsa.model.User;
import gharsa.service.AuthService;
import gharsa.service.TokenService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class AuthController {

    public record RegisterRequest(String fullName, String email, String password) {}
    public record LoginRequest(String email, String password) {}

    private final AuthService auth;
    private final TokenService tokens;

    public AuthController(AuthService auth, TokenService tokens) {
        this.auth = auth;
        this.tokens = tokens;
    }

    public void register(Javalin app) {
        app.post("/api/auth/register", this::handleRegister);
        app.post("/api/auth/login", this::handleLogin);
        app.get("/api/me", this::handleMe);
    }

    private void handleRegister(Context ctx) {
        RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);
        try {
            User u = auth.register(req.fullName(), req.email(), req.password());
            ctx.status(201).json(toPublic(u));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            ctx.status(409).json(Map.of("error", e.getMessage()));
        }
    }

    private void handleLogin(Context ctx) {
        LoginRequest req = ctx.bodyAsClass(LoginRequest.class);
        auth.login(req.email(), req.password()).ifPresentOrElse(
                u -> {
                    Map<String, Object> body = new HashMap<>(toPublic(u));
                    body.put("token", tokens.create(u));
                    ctx.json(body);
                },
                () -> ctx.status(401).json(Map.of("error", "Email ou mot de passe incorrect")));
    }

    private void handleMe(Context ctx) {
        ctx.json(Map.of(
                "userId", ctx.<Long>attribute("userId"),
                "role", ctx.<String>attribute("role")));
    }

    private Map<String, Object> toPublic(User u) {
        return Map.of("id", u.id(), "fullName", u.fullName(),
                "email", u.email(), "role", u.role());
    }
}