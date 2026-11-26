package gharsa.service;

import gharsa.model.User;
import gharsa.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {

    private final UserRepository users;

    public AuthService(UserRepository users) {
        this.users = users;
    }

    public User register(String fullName, String email, String password) {
        if (fullName == null || fullName.isBlank()
                || email == null || !email.contains("@")
                || password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Nom, email valide et mot de passe (6 caractères minimum) requis");
        }
        String normalizedEmail = email.trim().toLowerCase();
        if (users.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalStateException("Cet email est déjà utilisé");
        }
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        return users.create(fullName.trim(), normalizedEmail, hash, "FARMER");
    }

    public Optional<User> login(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }
        return users.findByEmail(email.trim().toLowerCase())
                .filter(u -> BCrypt.checkpw(password, u.passwordHash()));
    }
}