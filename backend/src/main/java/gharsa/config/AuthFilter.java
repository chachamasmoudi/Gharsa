package gharsa.config;

import gharsa.service.TokenService;
import io.javalin.Javalin;
import io.javalin.http.HandlerType;
import io.javalin.http.UnauthorizedResponse;

public class AuthFilter {

    public static void register(Javalin app, TokenService tokens) {
        app.before("/api/*", ctx -> {
            String path = ctx.path();
            if (ctx.method() == HandlerType.OPTIONS
                    || path.startsWith("/api/auth/")
                    || path.equals("/api/health")) {
                return;
            }
            String header = ctx.header("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new UnauthorizedResponse("Token manquant");
            }
            var decoded = tokens.verify(header.substring(7))
                    .orElseThrow(() -> new UnauthorizedResponse("Token invalide ou expiré"));
            ctx.attribute("userId", Long.parseLong(decoded.getSubject()));
            ctx.attribute("role", decoded.getClaim("role").asString());
        });
    }
}