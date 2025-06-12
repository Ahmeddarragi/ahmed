package org.sid.controller;

import java.util.Optional;
import java.util.regex.Pattern;

import org.sid.entity.Admin;
import org.sid.repository.AdminRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v2")
public class AdminController {
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Enregistrement d'un nouvel admin
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        Optional<Admin> existingUser = adminRepository.findByUsername(admin.getUsername());
        Optional<Admin> existingEmail = adminRepository.findByEmail(admin.getEmail());

        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (existingEmail.isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // ✅ Vérification du format de l'email
        if (!isValidEmail(admin.getEmail())) {
            return ResponseEntity.badRequest().body("Email must be from @vermeg.com domain.");
        }

        // ✅ Vérification de la sécurité du mot de passe
        if (!isValidPassword(admin.getPassword())) {
            return ResponseEntity.badRequest().body(
                "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character (@#$%^&+=!?), and must not contain spaces."
            );
        }

        // 🔐 Hashage du mot de passe avant enregistrement
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);

        return ResponseEntity.ok("Admin registered successfully");
    }

    // ✅ Connexion de l'admin
    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody Admin loginRequest) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(loginRequest.getEmail());

        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Admin admin = adminOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        return ResponseEntity.ok("Login successful");
    }

    // ✅ Vérification de la force du mot de passe
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?])(?=\\S+$).{8,}$";
        return Pattern.matches(passwordPattern, password);
    }

    // ✅ Vérification du domaine de l'email
    private boolean isValidEmail(String email) {
        return email.toLowerCase().endsWith("@vermeg.com");
    }

    // ✅ Déconnexion de l'admin
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false); // Récupérer la session si elle existe
        if (session != null) {
            session.invalidate(); // Invalidation de la session
        }
        
        // Empêcher le cache après la déconnexion
        response.setHeader("Cache-Control", "no-store");

        return ResponseEntity.ok("Logout successful");
    }
}