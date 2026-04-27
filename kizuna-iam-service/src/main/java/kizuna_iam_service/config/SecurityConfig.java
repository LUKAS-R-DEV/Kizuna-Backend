package kizuna_iam_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/users/**").hasRole("manage-users")
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()) // Configuração padrão inicial
                );

        return http.build();
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.get("roles") instanceof List<?>) {
                ((List<String>) realmAccess.get("roles")).forEach(role ->
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                );
            }

            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null && resourceAccess.containsKey("realm-management")) {
                Map<String, Object> realmMgmt = (Map<String, Object>) resourceAccess.get("realm-management");
                if (realmMgmt.get("roles") instanceof List<?>) {
                    ((List<String>) realmMgmt.get("roles")).forEach(role ->
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                }
            }

            return authorities;
        });

        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://127.0.0.1:8081/realms/Kizuna/protocol/openid-connect/certs").build();
    }
}