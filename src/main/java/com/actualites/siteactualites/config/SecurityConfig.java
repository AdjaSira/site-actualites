package com.actualites.siteactualites.config;

import com.actualites.siteactualites.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    @Autowired
    public SecurityConfig(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/article/**", "/categorie/**", "/recherche", "/login", "/after-login", "/css/**", "/js/**", "/h2-console/**", "/ws/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers("/editeur/**").hasAnyRole("EDITEUR", "ADMINISTRATEUR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("login")
                        .passwordParameter("motDePasse")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/login?error=Identifiants incorrects")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/ws/**")
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            System.out.println("Authentification réussie pour : " + authentication.getName());
            authentication.getAuthorities().forEach(authority -> {
                System.out.println("Rôle détecté : " + authority.getAuthority());
            });
            System.out.println("Redirection vers /after-login");
            response.sendRedirect("/after-login");
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            System.out.println("Chargement de l'utilisateur : " + username);
            return userService.trouverParLogin(username)
                    .map(user -> {
                        String role = user.getRole();
                        System.out.println("Utilisateur chargé : " + user.getLogin() + ", Rôle : " + role);
                        return org.springframework.security.core.userdetails.User
                                .withUsername(user.getLogin())
                                .password(user.getMotDePasse())
                                .roles(role)
                                .build();
                    })
                    .orElseThrow(() -> {
                        System.out.println("Utilisateur non trouvé : " + username);
                        return new UsernameNotFoundException("Utilisateur non trouvé : " + username);
                    });
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}