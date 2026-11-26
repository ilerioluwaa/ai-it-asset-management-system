
package com.cmsc495.assetmanagement.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.security.admin.username:admin}")
    private String adminUsername;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    // Password encryption

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // Administrator authentication

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        if (adminUsername == null
                || adminUsername.isBlank()) {

            throw new IllegalStateException(
                    "Administrator username is required");
        }

        if (adminPassword == null
                || adminPassword.length() < 12
                || adminPassword.getBytes(
                    StandardCharsets.UTF_8).length > 72) {

            throw new IllegalStateException(
                    "Administrator password must contain at least "
                    + "12 characters and no more than 72 UTF-8 bytes");
        }

        UserDetails administrator = User
                .withUsername(adminUsername)
                .password(
                    passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(
                administrator);
    }

    // HTTP security configuration

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // Public and protected endpoints

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/login",
                    "/login.html",
                    "/login.js",
                    "/login/csrf",
                    "/style.css",
                    "/error",
                    "/favicon.ico",
                    "/actuator/health"
                ).permitAll()

                .requestMatchers(
                    "/api/**",
                    "/actuator/**"
                ).hasRole("ADMIN")

                .anyRequest().hasRole("ADMIN")
            )

            // Custom administrator login

            .formLogin(form -> form

                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )

            // Administrator logout

            .logout(logout -> logout

                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // Cross-site request forgery protection

            .csrf(csrf -> csrf

                .csrfTokenRepository(
                    new HttpSessionCsrfTokenRepository())
            )

            // Session fixation protection

            .sessionManagement(session -> session

                .sessionFixation(fixation ->
                    fixation.changeSessionId())
            )

            // Authentication and authorization errors

            .exceptionHandling(exceptions -> exceptions

                .authenticationEntryPoint(
                    (request, response, exception) -> {

                        String path = request
                            .getRequestURI()
                            .substring(
                                request.getContextPath().length());

                        if (path.equals("/api")
                                || path.startsWith("/api/")) {

                            new HttpStatusEntryPoint(
                                HttpStatus.UNAUTHORIZED)
                                .commence(
                                    request,
                                    response,
                                    exception);

                        } else {

                            new LoginUrlAuthenticationEntryPoint(
                                "/login")
                                .commence(
                                    request,
                                    response,
                                    exception);
                        }
                    }
                )

                .accessDeniedHandler(
                    (request, response, exception) ->
                        writeAccessDeniedResponse(response))
            )

            // Browser security headers

            .headers(headers -> headers

                .frameOptions(frame -> frame.deny())

                .contentTypeOptions(options -> {})

                .contentSecurityPolicy(csp ->
                    csp.policyDirectives(
                        "default-src 'self'; "
                        + "script-src 'self'; "
                        + "style-src 'self' "
                        + "https://cdn.jsdelivr.net; "
                        + "img-src 'self' data:; "
                        + "font-src 'self' "
                        + "https://cdn.jsdelivr.net; "
                        + "connect-src 'self'; "
                        + "object-src 'none'; "
                        + "base-uri 'self'; "
                        + "form-action 'self'; "
                        + "frame-ancestors 'none';"
                    )
                )
            );

        return http.build();
    }

    // Safe access-denied response

    private void writeAccessDeniedResponse(
            HttpServletResponse response)
            throws IOException {

        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN);

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE);

        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
            {
                "status": 403,
                "error": "Forbidden",
                "message": "Access denied or invalid security token"
            }
            """);
    }

    // Login page and CSRF endpoints

    @RestController
    public static class CsrfController {

        @GetMapping(
            value = "/login",
            produces = MediaType.TEXT_HTML_VALUE
        )
        public ResponseEntity<Resource> getLoginPage() {

            Resource loginPage =
                new ClassPathResource("static/login.html");

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .contentType(MediaType.TEXT_HTML)
                    .body(loginPage);
        }

        @GetMapping("/login/csrf")
        public ResponseEntity<Map<String, String>>
                getLoginCsrfToken(CsrfToken csrfToken) {

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .body(Map.of(
                        "token", csrfToken.getToken(),
                        "headerName", csrfToken.getHeaderName(),
                        "parameterName",
                            csrfToken.getParameterName()
                    ));
        }

        @GetMapping("/api/csrf")
        public ResponseEntity<CsrfToken> getCsrfToken(
                CsrfToken csrfToken) {

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .body(csrfToken);
        }
    }
}
