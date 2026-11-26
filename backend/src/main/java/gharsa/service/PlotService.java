package gharsa.service;

import gharsa.model.Plot;
import gharsa.model.PlotData;
import gharsa.repository.PlotRepository;

import java.util.List;
import java.util.Optional;

public class PlotService {

    private final PlotRepository plots;

    public PlotService(PlotRepository plots) {
        this.plots = plots;
    }

    private void validate(PlotData d) {
        if (d == null || d.name() == null || d.name().isBlank()) {
            throw new IllegalArgumentException("Le nom de la parcelle est obligatoire");
        }
        if (d.area() != null && d.area() < 0) {
            throw new IllegalArgumentException("La superficie ne peut pas être négative");
        }
        if (d.latitude() != null && (d.latitude() < -90 || d.latitude() > 90)) {
            throw new IllegalArgumentException("Latitude invalide (entre -90 et 90)");
        }
        if (d.longitude() != null && (d.longitude() < -180 || d.longitude() > 180)) {
            throw new IllegalArgumentException("Longitude invalide (entre -180 et 180)");
        }
    }

    public List<Plot> list(long userId) {
        return plots.findAllByUser(userId);
    }

    public Optional<Plot> get(long id, long userId) {
        return plots.findByIdAndUser(id, userId);
    }

    public Plot create(long userId, PlotData d) {
        validate(d);
        return plots.create(userId, d);
    }

    public boolean update(long id, long userId, PlotData d) {
        validate(d);
        return plots.update(id, userId, d);
    }

    public boolean delete(long id, long userId) {
        return plots.delete(id, userId);
    }
}