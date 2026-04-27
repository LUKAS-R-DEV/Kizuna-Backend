package kizuna_iam_service.user.controller;

import kizuna_iam_service.dto.ApiResponseGeneric;
import kizuna_iam_service.dto.UserRequestDto;
import kizuna_iam_service.dto.UserResponseDto;
import kizuna_iam_service.exception.NotFoundException;
import kizuna_iam_service.service.UserManagementService;
import kizuna_iam_service.user.domain.User;
import kizuna_iam_service.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserManagementService userService;


    @GetMapping("/me")
   public ResponseEntity<User> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getMe(jwt));
    }

    @GetMapping()
    public ResponseEntity<List<UserRepresentation>> getAllUsers() {
       return ResponseEntity.ok(userService.findAll());
    }
    @GetMapping("{id}")
    public ResponseEntity<UserRepresentation> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }
    @PutMapping("{id}")
    public ResponseEntity<ApiResponseGeneric> updateUser(@PathVariable String id, @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.update(id, userRequestDto));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponseGeneric> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.deleteById(id));
    }
    @GetMapping("/allOperators")
    public ResponseEntity<List<UserResponseDto>> getAllOperators() {
        List<User> operators = userRepository.findAllByRolesContaining("OPERATOR");
        return ResponseEntity.ok(operators.stream().map(this::userResponseDto).toList());
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDto requestDto){
        try{
            userService.createUserKizuna(requestDto);
            return ResponseEntity.ok("User created successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Failed to create user");
        }

    }
    
    private final UserResponseDto userResponseDto(User user) {
        return new UserResponseDto(user.getKeycloakId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getRoles());
    }


}