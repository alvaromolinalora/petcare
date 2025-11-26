package com.petcare.servicio;

import com.petcare.modelo.Usuario;
import com.petcare.repositorio.UsuarioRepositorio;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ServicioUsuarios {

    private final UsuarioRepositorio usuarioRepositorio;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ServicioUsuarios(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

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

    public Usuario validarLogin(String nombreUsuario, String contrasenaPlano) {
        Usuario u = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        if (u == null) return null;
        return encoder.matches(contrasenaPlano, u.getContrasenaHash()) ? u : null;
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepositorio.findById(id).orElse(null);
    }

}
