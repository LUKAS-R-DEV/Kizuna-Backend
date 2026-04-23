package kizuna_iam_service.user.controller;

import kizuna_iam_service.dto.UserResponseDto;
import kizuna_iam_service.exception.NotFoundException;
import kizuna_iam_service.user.domain.User;
import kizuna_iam_service.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController

@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String fullname = jwt.getClaimAsString("name");
        String email = jwt.getClaimAsString("email");

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        List<String> roles = realmAccess != null
                ? (List<String>) realmAccess.get("roles")
                : List.of();

        User user = userRepository.findById(keycloakId)
                .map(existing -> {
                    existing.setUsername(username);
                    existing.setFullName(fullname);
                    existing.setEmail(email);
                    existing.setRoles(roles);
                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setKeycloakId(keycloakId);
                    newUser.setUsername(username);
                    newUser.setFullName(fullname);
                    newUser.setEmail(email);
                    newUser.setRoles(roles);
                    return userRepository.save(newUser);
                });

        return user;
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return ResponseEntity.ok(userResponseDto(user));
    }
    @GetMapping("/allOperators")
    public ResponseEntity<List<UserResponseDto>> getAllOperators() {
        List<User> operators = userRepository.findAllByRolesContaining("OPERATOR");
        return ResponseEntity.ok(operators.stream().map(this::userResponseDto).toList());
    }


    private final UserResponseDto userResponseDto(User user) {
        return new UserResponseDto(user.getKeycloakId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getRoles());
    }


}