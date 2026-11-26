package gharsa;

import gharsa.config.AuthFilter;
import gharsa.config.Database;
import gharsa.controller.AuthController;
import gharsa.repository.UserRepository;
import gharsa.service.AuthService;
import gharsa.service.TokenService;
import io.javalin.Javalin;

import java.util.Map;

import gharsa.controller.PlotController;
import gharsa.repository.PlotRepository;
import gharsa.service.PlotService;
public class App {

    public static void main(String[] args) {
        Database.init();

        Javalin app = Javalin.create(config -> {
            // Le frontend sera servi depuis une autre origine : on autorise CORS
            config.bundledPlugins.enableCors(cors ->
                    cors.addRule(rule -> rule.anyHost())
            );
        });

        TokenService tokens = new TokenService();
        AuthFilter.register(app, tokens);

        app.get("/api/v1/health", ctx -> ctx.json(Map.of("status", "ok", "service", "gharsa-backend")));
        new AuthController(new AuthService(new UserRepository()), tokens).register(app);
        new PlotController(new PlotService(new PlotRepository())).register(app);
        app.start(7070);
    }
}