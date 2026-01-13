package org.alexpakh.diplomBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Основная цепочка безопасности для API
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // Применяется только к API путям
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // Отключаем CSRF для REST API
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless для REST
                )
                .authorizeHttpRequests(auth -> auth
                        // Публичные API эндпоинты (без аутентификации)
                        .requestMatchers("/api/auth/**", "/api/public/**").permitAll()

                        // Защищенные эндпоинты с проверкой ролей
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/manager/**", "/api/users-list").hasRole("MANAGER")

                        // Все остальные API требуют аутентификации
                        .requestMatchers("/api/**").authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    // 2. Цепочка для публичных путей (Angular статика, favicon, ошибки)
    @Bean
    @Order(2)
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/", "/error", "/favicon.ico", "/index.html", "/assets/**", "/*.js", "/*.css")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    // 3. Конвертер JWT токена (адаптирован под ваш токен)
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setPrincipalClaimName("preferred_username"); // Используем username из токена

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Set<GrantedAuthority> authorities = new HashSet<>();

            // Вариант A: Берем роли из realm_access.roles (реалм роли)
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.get("roles") != null) {
                List<String> realmRoles = (List<String>) realmAccess.get("roles");

                authorities.addAll(realmRoles.stream()
                        .filter(role -> !role.startsWith("default-roles-") &&
                                !role.equals("offline_access") &&
                                !role.equals("uma_authorization"))
                        .map(role -> {
                            // Добавляем префикс ROLE_ для Spring Security
                            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                            return new SimpleGrantedAuthority(authority);
                        })
                        .collect(Collectors.toSet()));
            }

            // Вариант B: Берем роли из resource_access.pet.roles (клиентские роли)
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                // Для клиента "pet"
                Map<String, Object> petClient = (Map<String, Object>) resourceAccess.get("pet");
                if (petClient != null && petClient.get("roles") != null) {
                    List<String> clientRoles = (List<String>) petClient.get("roles");

                    authorities.addAll(clientRoles.stream()
                            .map(role -> {
                                String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                                return new SimpleGrantedAuthority(authority);
                            })
                            .collect(Collectors.toSet()));
                }

                // Для клиента "account" (если нужно)
                Map<String, Object> accountClient = (Map<String, Object>) resourceAccess.get("account");
                if (accountClient != null && accountClient.get("roles") != null) {
                    List<String> accountRoles = (List<String>) accountClient.get("roles");
                    // Можете добавить обработку account roles если нужно
                }
            }

            // Если роли не найдены, создаем пустой список
            if (authorities.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }

            return new ArrayList<>(authorities);
        });

        return converter;
    }

    // 4. Конфигурация CORS для Angular приложения
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",  // Angular dev server
                "http://localhost:8081"   // Ваш Spring Boot
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With",
                "Accept", "Origin", "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 час кеширования CORS префлайта

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}