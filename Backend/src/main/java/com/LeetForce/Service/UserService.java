package com.LeetForce.Service;

import com.LeetForce.Entity.UserEntity;
import com.LeetForce.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity createUser(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        String username = normalizeUsername(user.getUsername());
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalStateException("Username already exists: " + username);
        }

        user.setUsername(username);
        user.setSelfDescription(normalizeSelfDescription(user.getSelfDescription()));
        return userRepository.save(user);
    }

    public UserEntity updateUser(Long userId, String newUsername, String selfDescription) {
        if (userId == null) {
            throw new IllegalArgumentException("User id must not be null");
        }

        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (newUsername != null) {
            String normalizedUsername = normalizeUsername(newUsername);
            if (normalizedUsername.isBlank()) {
                throw new IllegalArgumentException("Username must not be blank");
            }

            if (userRepository.existsByUsernameAndIdNot(normalizedUsername, userId)) {
                throw new IllegalStateException("Username already exists: " + normalizedUsername);
            }

            existingUser.setUsername(normalizedUsername);
        }

        existingUser.setSelfDescription(normalizeSelfDescription(selfDescription));
        return userRepository.save(existingUser);
    }

    public UserEntity updateLcProfileLink(Long userId, String lcProfileUrl) {
        if (userId == null) {
            throw new IllegalArgumentException("User id must not be null");
        }

        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        existingUser.setLcProfileUrl(normalizeProfileUrl(lcProfileUrl));
        return userRepository.save(existingUser);
    }

    public UserEntity updateCfProfileLink(Long userId, String cfProfileUrl) {
        if (userId == null) {
            throw new IllegalArgumentException("User id must not be null");
        }

        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        existingUser.setCfProfileUrl(normalizeProfileUrl(cfProfileUrl));
        return userRepository.save(existingUser);
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim();
    }

    private String normalizeSelfDescription(String selfDescription) {
        if (selfDescription == null) {
            return null;
        }

        String normalizedDescription = selfDescription.trim();
        if (normalizedDescription.isEmpty()) {
            return null;
        }

        return normalizedDescription.length() > 500
                ? normalizedDescription.substring(0, 500)
                : normalizedDescription;
    }

    private String normalizeProfileUrl(String profileUrl) {
        if (profileUrl == null) {
            return null;
        }

        String normalizedUrl = profileUrl.trim();
        if (normalizedUrl.isEmpty()) {
            return null;
        }

        if (!normalizedUrl.startsWith("http://") && !normalizedUrl.startsWith("https://")) {
            normalizedUrl = "https://" + normalizedUrl;
        }

        return normalizedUrl.length() > 255
                ? normalizedUrl.substring(0, 255)
                : normalizedUrl;
    }
}
