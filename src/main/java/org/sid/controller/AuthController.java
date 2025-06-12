package org.sid.controller;

import org.sid.entity.ResponseMessage;
import org.sid.entity.Userr;
import org.sid.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Userr user) {
        String result = authService.register(user);

        // Utiliser ResponseMessage pour répondre
        if (result.contains("faible") || result.contains("exists")) {
            return ResponseEntity.badRequest().body(new ResponseMessage(result)); // 400
        }

        return ResponseEntity.ok(new ResponseMessage(result)); // 200
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        String result = authService.login(email, password);

        // Répondre avec une structure JSON appropriée
        if (result.equals("Login successful")) {
            return ResponseEntity.ok(new ResponseMessage(result));
        } else {
            return ResponseEntity.status(401).body(new ResponseMessage(result)); // Unauthorized
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(new ResponseMessage("Logout successful"));
    }
}