package com.mss.service.impl;

import com.mss.model.User;
import com.mss.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Custom OAuth2 user service to handle Google OAuth2 authentication.
 * Matches Google users to existing users by email and updates their profile information.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService {

    private final UserRepository userRepository;
    private final ProfilePictureSyncService profilePictureSyncService;

    /**
     * Processes OAuth2 user from Google and matches to existing user by email.
     *
     * @param oAuth2User the OAuth2 user from Google
     * @return the matched user
     * @throws RuntimeException if user not found
     */
    public User processOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String googleId = (String) attributes.get("sub");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        user.setGoogleId(googleId);
        user.setAuthProvider("GOOGLE");

        user.setEnabled(true);

        if (picture != null && !picture.isEmpty()) {
            try {
                String supabaseUrl = profilePictureSyncService.syncProfilePicture(picture, user.getId());
                if (supabaseUrl != null) {
                    user.setImageUrl(supabaseUrl);
                } else {
                    user.setImageUrl(picture);
                }
            } catch (Exception e) {
                user.setImageUrl(picture);
            }
        }

        if (name != null && !name.isEmpty()) {
            String[] nameParts = name.split(" ", 2);
            if (nameParts.length >= 1 && (user.getFirstname() == null || user.getFirstname().isEmpty())) {
                user.setFirstname(nameParts[0]);
            }
            if (nameParts.length >= 2 && (user.getLastname() == null || user.getLastname().isEmpty())) {
                user.setLastname(nameParts[1]);
            }
        }

        return userRepository.save(user);
    }
}
