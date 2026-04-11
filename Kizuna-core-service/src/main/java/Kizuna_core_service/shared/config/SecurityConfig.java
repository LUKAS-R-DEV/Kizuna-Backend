package Kizuna_core_service.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 🔹 Endpoints públicos (agora relativos ao /core)
                        .requestMatchers("/public/**").permitAll()

                        // 🔹 Acesso por Roles - Remova o prefixo "/core" de todos
                        .requestMatchers("/auth/**", "/users/**").hasRole("ACCESS_MANAGER")
                        .requestMatchers("/recipes/**").hasRole("PLANNER")
                        .requestMatchers("/qualityInspection/**").hasRole("INSPECTOR")

                        // AGORA O EXECUTIVO VAI SER BARRADO AQUI:
                        .requestMatchers("/inventory/**", "/inventoryMovement/**").hasRole("INVENTORY_MANAGER")

                        .requestMatchers("/dashboard/**", "/report/**").hasRole("EXECUTIVE")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // 1. Extração de Roles do Realm (Onde está o seu "EXECUTIVE")
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.get("roles") instanceof List<?>) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                roles.forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                });
            }

            // 2. Extração de Roles de Clientes específicos (ex: "core")
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null && resourceAccess.get("core") instanceof Map<?, ?>) {
                Map<String, Object> coreClient = (Map<String, Object>) resourceAccess.get("core");
                if (coreClient.get("roles") instanceof List<?>) {
                    List<String> clientRoles = (List<String>) coreClient.get("roles");
                    clientRoles.forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    });
                }
            }

            return authorities;
        });

        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withJwkSetUri("http://127.0.0.1:8081/realms/Kizuna/protocol/openid-connect/certs")
                .build();
    }
}