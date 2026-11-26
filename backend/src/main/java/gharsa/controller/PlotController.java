package gharsa.controller;

import gharsa.model.Plot;
import gharsa.model.PlotData;
import gharsa.service.PlotService;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;

import java.util.Map;

public class PlotController {

    private final PlotService plots;

    public PlotController(PlotService plots) {
        this.plots = plots;
    }

    public void register(Javalin app) {
        app.get("/api/v1/plots", this::list);
        app.post("/api/v1/plots", this::create);
        app.get("/api/v1/plots/{id}", this::get);
        app.put("/api/v1/plots/{id}", this::update);
        app.delete("/api/v1/plots/{id}", this::delete);
    }

    private long userId(Context ctx) {
        return ctx.<Long>attribute("userId");
    }

    private long plotId(Context ctx) {
        try {
            return Long.parseLong(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            throw new BadRequestResponse("Identifiant invalide");
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
        PlotData d = ctx.bodyAsClass(PlotData.class);
        try {
            Plot p = plots.create(userId(ctx), d);
            ctx.status(201).json(p);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private void update(Context ctx) {
        PlotData d = ctx.bodyAsClass(PlotData.class);
        long id = plotId(ctx);
        try {
            if (plots.update(id, userId(ctx), d)) {
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