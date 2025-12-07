package service;

import java.util.HashSet;
import java.util.Set;

public class TokenManager {
    private Set<String> validTokens = new HashSet<>();

    public void storeToken(String token) {
        validTokens.add(token);
    }

    public boolean isTokenValid(String token) {
        return validTokens.contains(token);
    }

    public void invalidateToken(String token) {
        validTokens.remove(token);
    }

    public void removeAllTokens() {
        validTokens.clear();
    }
}