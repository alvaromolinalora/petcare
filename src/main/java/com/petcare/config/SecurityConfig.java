package com.petcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (se pueden ver sin iniciar sesión)
                        .requestMatchers(
                                "/",              // raíz -> se redirige a /login por tu ControladorHome
                                "/login",         // página de login
                                "/registro",      // si tienes página de registro pública
                                "/estilos.css",   // tu CSS principal
                                "/css/**",        // por si en algún momento usas /css/...
                                "/js/**",         // scripts
                                "/img/**",        // imágenes en /static/img
                                "/uploads.mascotas/**",
                                "/FotoPerfilMascotas/**",
                                "/favicon.ico"
                        ).permitAll()
                        // El resto de rutas necesitan estar autenticadas
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")          // usa tu plantilla login.html
                        .defaultSuccessUrl("/inicio", true)  // al iniciar sesión, ve a /inicio
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // Para simplificar, desactivamos CSRF (aceptable para tu proyecto)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
