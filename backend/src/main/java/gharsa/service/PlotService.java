package gharsa.service;

import gharsa.model.Plot;
import gharsa.repository.PlotRepository;

import java.util.List;
import java.util.Optional;

public class PlotService {

    private final PlotRepository plots;

    public PlotService(PlotRepository plots) {
        this.plots = plots;
    }

    private void validate(String name, Double areaHa) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom de la parcelle est obligatoire");
        }
        if (areaHa != null && areaHa < 0) {
            throw new IllegalArgumentException("La surface ne peut pas être négative");
        }
    }

    public List<Plot> list(long userId) {
        return plots.findAllByUser(userId);
    }

    public Optional<Plot> get(long id, long userId) {
        return plots.findByIdAndUser(id, userId);
    }

    public Plot create(long userId, String name, Double areaHa, String crop, String location) {
        validate(name, areaHa);
        return plots.create(userId, name.trim(), areaHa, crop, location);
    }

    public boolean update(long id, long userId, String name, Double areaHa, String crop, String location) {
        validate(name, areaHa);
        return plots.update(id, userId, name.trim(), areaHa, crop, location);
    }

    public boolean delete(long id, long userId) {
        return plots.delete(id, userId);
    }
}