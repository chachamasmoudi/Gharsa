package gharsa.controller;

import gharsa.model.Plot;
import gharsa.service.PlotService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class PlotController {

    public record PlotRequest(String name, Double areaHa, String crop, String location) {}

    private final PlotService plots;

    public PlotController(PlotService plots) {
        this.plots = plots;
    }

    public void register(Javalin app) {
        app.get("/api/plots", this::list);
        app.post("/api/plots", this::create);
        app.get("/api/plots/{id}", this::get);
        app.put("/api/plots/{id}", this::update);
        app.delete("/api/plots/{id}", this::delete);
    }

    private long userId(Context ctx) {
        return ctx.<Long>attribute("userId");
    }

    private long plotId(Context ctx) {
        try {
            return Long.parseLong(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            throw new io.javalin.http.BadRequestResponse("Identifiant invalide");
        }
    }

    private void notFound(Context ctx) {
        ctx.status(404).json(Map.of("error", "Parcelle introuvable"));
    }

    private void list(Context ctx) {
        ctx.json(plots.list(userId(ctx)));
    }

    private void get(Context ctx) {
        plots.get(plotId(ctx), userId(ctx)).ifPresentOrElse(ctx::json, () -> notFound(ctx));
    }

    private void create(Context ctx) {
        PlotRequest r = ctx.bodyAsClass(PlotRequest.class);
        try {
            Plot p = plots.create(userId(ctx), r.name(), r.areaHa(), r.crop(), r.location());
            ctx.status(201).json(p);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void update(Context ctx) {
        PlotRequest r = ctx.bodyAsClass(PlotRequest.class);
        long id = plotId(ctx);
        try {
            if (plots.update(id, userId(ctx), r.name(), r.areaHa(), r.crop(), r.location())) {
                plots.get(id, userId(ctx)).ifPresent(ctx::json);
            } else {
                notFound(ctx);
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void delete(Context ctx) {
        if (plots.delete(plotId(ctx), userId(ctx))) {
            ctx.status(204);
        } else {
            notFound(ctx);
        }
    }
}