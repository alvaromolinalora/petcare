package com.petcare.config;

import com.petcare.modelo.Usuario;
import com.petcare.servicio.ServicioUsuarios;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ServicioUsuarios servicioUsuarios;

    public SecurityConfig(ServicioUsuarios servicioUsuarios) {
        this.servicioUsuarios = servicioUsuarios;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(servicioUsuarios);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/registro",
                                "/estilos.css",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/img/**",
                                "/static/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("usuario")
                        .passwordParameter("contrasena")

                        .successHandler((request, response, authentication) -> {
                            String username = authentication.getName();
                            Usuario u = servicioUsuarios.buscarPorNombreUsuario(username);
                            if (u != null) {
                                request.getSession().setAttribute("usuarioId", u.getId());
                                request.getSession().setAttribute("usuarioNombre", u.getNombreUsuario());
                            }
                            response.sendRedirect("/inicio");
                        })
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}
