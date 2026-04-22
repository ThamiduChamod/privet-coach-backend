package lk.privat_coaching_backend.controller;

import jakarta.validation.Valid;
import lk.privat_coaching_backend.entity.User;
import lk.privat_coaching_backend.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import lk.privat_coaching_backend.dto.LoginRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleAuthService googleAuthService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Logic for authentication
        System.out.println(loginRequest);
        return ResponseEntity.ok("Login successful");
    }
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {

        try {
            String idToken = payload.get("idToken");
            System.out.println(idToken);
            if (idToken == null) {
                return ResponseEntity.badRequest().body("Missing idToken");
            }

            User user = googleAuthService.authenticate(idToken);

            return ResponseEntity.ok(Map.of(
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "message", "Login successful"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Google login failed");
        }
    }
}
