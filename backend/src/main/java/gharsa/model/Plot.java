package gharsa.model;

public record Plot(long id, long userId, String name, Double latitude, Double longitude, Double area,
                   String cropType, String soilType, String plantingDate, String growthStage) {}