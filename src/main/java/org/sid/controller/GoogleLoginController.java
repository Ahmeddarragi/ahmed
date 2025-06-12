package org.sid.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class GoogleLoginController {

    // ✅ Récupérer les infos de l'utilisateur connecté via Google OAuth2
    @GetMapping("/user-info")
    public Map<String, Object> getUserinfo(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            return ((OAuth2User) authentication.getPrincipal()).getAttributes();
        }
        return Map.of("message", "Utilisateur non connecté");
    }

    // ✅ Déconnexion + renvoi d'une URL de logout Google
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // URL vers la page de logout Google (à ouvrir côté front)
        String googleLogoutUrl = "https://accounts.google.com/logout";

        return ResponseEntity.ok(
            Map.of(
                "message", "Logout successful",
                "redirectUrl", googleLogoutUrl
            )
        );
    }
}