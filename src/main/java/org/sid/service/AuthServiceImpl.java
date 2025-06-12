package org.sid.service;

import org.sid.entity.Userr;
import org.sid.repository.UserrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserrRepository userrRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String register(Userr user) {
        if (userrRepository.findByEmail(user.getEmail()) != null) {
            return "Email already exists";
        }

        // Vérification manuelle de la force du mot de passe
        if (!isPasswordStrong(user.getPassword())) {
            return "Mot de passe faible : il doit contenir au moins 8 caractères, une majuscule, un chiffre et un caractère spécial.";
        }

        // Encoder et enregistrer
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userrRepository.save(user);

        return "User registered successfully";
    }

    @Override
    public String login(String email, String password) {
        Userr user = userrRepository.findByEmail(email);
        if (user == null) {
            return "Invalid email";
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return "Login successful";
        } else {
            return "Invalid password";
        }
    }

    // 💡 Méthode utilitaire pour forcer un mot de passe fort
    private boolean isPasswordStrong(String password) {
        // Au moins 8 caractères, une majuscule, un chiffre, et un caractère spécial (élargi)
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        return password != null && password.matches(regex);
    }
}