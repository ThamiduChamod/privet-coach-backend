package lk.privat_coaching_backend.service;

import lk.privat_coaching_backend.entity.User;

public interface GoogleAuthService {
    User authenticate(String idTokenString) throws Exception;
}
