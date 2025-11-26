package com.petcare.servicio;

import com.petcare.modelo.HistorialMedico;
import com.petcare.modelo.Mascota;
import com.petcare.modelo.Usuario;
import com.petcare.repositorio.HistorialMedicoRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ServicioHistorialMedico {

    private final HistorialMedicoRepositorio repo;

    public ServicioHistorialMedico(HistorialMedicoRepositorio repo) {
        this.repo = repo;
    }

    public List<HistorialMedico> listarPorMascota(Mascota mascota) {
        return repo.findByMascotaOrderByFechaDesc(mascota);
    }

    public HistorialMedico buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void guardar(HistorialMedico h) {
        repo.save(h);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public List<HistorialMedico> listarProximasCitas(Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        return repo.findByMascotaUsuarioAndFechaGreaterThanEqualOrderByFechaAsc(usuario, hoy);
    }
}
