package com.petcare.servicio;

import com.petcare.modelo.Mascota;
import com.petcare.modelo.Usuario;
import com.petcare.repositorio.MascotaRepositorio;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicioMascotas {

    private final MascotaRepositorio mascotaRepositorio;

    public ServicioMascotas(MascotaRepositorio mascotaRepositorio) {
        this.mascotaRepositorio = mascotaRepositorio;
    }

    public void guardarMascota(Mascota mascota) {
        mascotaRepositorio.save(mascota);
    }

    public List<Mascota> listarPorUsuario(Usuario usuario) {
        return mascotaRepositorio.findByUsuario(usuario);
    }

    public Mascota buscarPorId(Long id) {
        return mascotaRepositorio.findById(id).orElse(null);
    }

    public void eliminarMascota(Long id) {
        mascotaRepositorio.deleteById(id);
    }
}
