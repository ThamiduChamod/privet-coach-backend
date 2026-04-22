package lk.privat_coaching_backend.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lk.privat_coaching_backend.entity.User;
import lk.privat_coaching_backend.repository.UserRepository;
import lk.privat_coaching_backend.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    public User authenticate(String idTokenString) throws Exception {
        System.out.println("service "+ idTokenString);
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);

        System.out.println("verified " + idToken);
        if (idToken == null) {
            throw new Exception("Invalid Google ID Token");
        }
        System.out.println("id token payload "+idToken.getPayload());
        GoogleIdToken.Payload payload = idToken.getPayload();

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        // 🔍 check user exists
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return existingUser.get(); // login
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setImageUrl(picture);

        return userRepository.save(newUser);
    }


}