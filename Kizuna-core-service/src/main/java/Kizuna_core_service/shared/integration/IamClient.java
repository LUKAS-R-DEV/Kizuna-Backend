package Kizuna_core_service.shared.integration;

import Kizuna_core_service.shared.exception.NotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class IamClient {

    private final RestTemplate restTemplate;

    public IamClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "iamService", fallbackMethod = "fallbackGetUserById")
    public UserResponseDto getUserById(String id) {

        String url = "http://localhost:8083/iam/users/{id}";

        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String token = authentication.getToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UserResponseDto.class,
                id
        );

        return response.getBody();
    }

    // 🔥 FALLBACK (quando IAM falhar)
    public UserResponseDto fallbackGetUserById(String id, Throwable ex) {

        System.out.println("⚠️ IAM indisponível: " + ex.getMessage());

        // pode retornar um usuário fake ou lançar exceção controlada
        return new UserResponseDto(
                id,
                "unknown",
                "unknown@system",
                "Usuário indisponível",
                List.of()
        );
    }
}