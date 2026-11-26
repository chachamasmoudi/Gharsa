package gharsa.model;

public record User(long id, String fullName, String email, String passwordHash, String role) {}