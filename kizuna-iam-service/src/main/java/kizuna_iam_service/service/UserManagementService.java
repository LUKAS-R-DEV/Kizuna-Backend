package kizuna_iam_service.service;

import jakarta.ws.rs.core.Response;
import kizuna_iam_service.dto.ApiResponseGeneric;
import kizuna_iam_service.dto.UserRequestDto;
import kizuna_iam_service.user.domain.User;
import kizuna_iam_service.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserManagementService {
    private final Keycloak keycloak;
    private final String realmName="Kizuna";
    private final UserRepository userRepository;

    public void createUserKizuna(UserRequestDto userRequestDto){
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userRequestDto.username());
        user.setEmail(userRequestDto.email());
        user.setFirstName(userRequestDto.firstName());
        user.setEmailVerified(true);

        CredentialRepresentation password = new CredentialRepresentation();
        password.setTemporary(false);
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(userRequestDto.password());
        user.setCredentials(Collections.singletonList(password));

        Response response = keycloak.realm(realmName).users().create(user);

        if(response.getStatus()==201){
            String userId= CreatedResponseUtil.getCreatedId(response);
            assignRoleToUser(userId,userRequestDto.role());

        }else{
            throw new RuntimeException("Failed to create user");
        }

    }

    public User getMe(Jwt jwt){
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


    public List<UserRepresentation> findAll(){
        return keycloak.realm(realmName).users().list();
    }
    public UserRepresentation findById(String id){
        return keycloak.realm(realmName).users().get(id).toRepresentation();
    }
    public List<UserRepresentation> search(String query){
        return keycloak.realm(realmName).users().search(query);
    }

    public ApiResponseGeneric update(String userId,UserRequestDto requestDto){
        UserResource userResource = keycloak.realm(realmName).users().get(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();

        userRepresentation.setUsername(requestDto.username());
        userRepresentation.setFirstName(requestDto.firstName());
        userRepresentation.setLastName(requestDto.lastName());
        userRepresentation.setEmail(requestDto.email());
        userResource.update(userRepresentation);
        return apiResponseGeneric("User updated successfully");
    }


    public ApiResponseGeneric deleteById(String id){
        keycloak.realm(realmName).users().get(id).remove();
        return apiResponseGeneric("User deleted successfully");
    }



    private void assignRoleToUser(String userId, String roleName) {
        RoleRepresentation role = keycloak.realm(realmName).roles().get(roleName).toRepresentation();

        keycloak.realm(realmName).users().get(userId).roles().realmLevel().add(Collections.singletonList(role));
    }

    private ApiResponseGeneric apiResponseGeneric(String message) {
        return new ApiResponseGeneric(message, LocalDateTime.now());
    }

}
