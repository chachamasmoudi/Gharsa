package gharsa;

import io.javalin.Javalin;
import java.util.Map;
import gharsa.config.Database;
import gharsa.controller.AuthController;
import gharsa.repository.UserRepository;
import gharsa.service.AuthService;

public class App {

    public static void main(String[] args) {
        Database.init();
        Javalin app = Javalin.create(config -> {
            // Le frontend sera servi depuis une autre origine : on autorise CORS
            config.bundledPlugins.enableCors(cors ->
                    cors.addRule(rule -> rule.anyHost())
            );
        });

        app.get("/api/health", ctx -> ctx.json(Map.of("status", "ok", "service", "gharsa-backend")));
        new AuthController(new AuthService(new UserRepository())).register(app);

        app.start(7070);
    }
}