package Kizuna_core_service.shared.integration;

import Kizuna_core_service.shared.exception.NotFoundException;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class IamClient {

    private final RestTemplate restTemplate;

    public IamClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserResponseDto getUserById(String id) {
        String url = "http://localhost:8083/iam/users/{id}";

        try {
            // 🔐 pega o token do contexto atual
            JwtAuthenticationToken authentication =
                    (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            String token = authentication.getToken().getTokenValue();

            // 📦 monta headers com Authorization
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // 🚀 chamada correta
            ResponseEntity<UserResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    UserResponseDto.class,
                    id
            );

            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Usuário não encontrado no IAM");

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RuntimeException("Token inválido ou não enviado para o IAM");

        } catch (Exception e) {
            throw new RuntimeException("Erro ao comunicar com IAM: " + e.getMessage());
        }
    }
}