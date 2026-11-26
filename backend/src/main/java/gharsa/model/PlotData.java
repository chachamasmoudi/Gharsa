package gharsa.model;

public record PlotData(String name, Double latitude, Double longitude, Double area,
                       String cropType, String soilType, String plantingDate, String growthStage) {}