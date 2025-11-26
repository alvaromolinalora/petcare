package com.petcare.servicio;

import com.petcare.modelo.Usuario;
import com.petcare.repositorio.UsuarioRepositorio;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ServicioUsuarios implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;
    // Encoder interno (evita el ciclo con SecurityConfig)
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ServicioUsuarios(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    // REGISTRO (para /registro)
    public boolean registrar(String nombreUsuario, String contrasenaPlano, String nombreCompleto) {

        if (usuarioRepositorio.findByNombreUsuario(nombreUsuario) != null) {
            return false;
        }

        Usuario u = new Usuario();
        u.setNombreUsuario(nombreUsuario);
        u.setContrasenaHash(encoder.encode(contrasenaPlano));
        u.setNombreCompleto(nombreCompleto);
        usuarioRepositorio.save(u);
        return true;
    }

    // LOGIN manual (si lo sigues usando en alguna parte)
    public Usuario validarLogin(String nombreUsuario, String contrasenaPlano) {
        Usuario u = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        if (u == null) return null;
        return encoder.matches(contrasenaPlano, u.getContrasenaHash()) ? u : null;
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepositorio.findById(id).orElse(null);
    }

    // *** NUEVO: para que SecurityConfig pueda recuperar el Usuario ***
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepositorio.findByNombreUsuario(nombreUsuario);
    }

    // ***** MÉTODO QUE USA SPRING SECURITY *****
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = usuarioRepositorio.findByNombreUsuario(username);
        if (u == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        return User.withUsername(u.getNombreUsuario())
                .password(u.getContrasenaHash())
                .roles("USER")
                .build();
    }
}
