package com.petcare.repositorio;

import com.petcare.modelo.HistorialMedico;
import com.petcare.modelo.Mascota;
import com.petcare.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HistorialMedicoRepositorio extends JpaRepository<HistorialMedico, Long> {

    List<HistorialMedico> findByMascotaOrderByFechaDesc(Mascota mascota);

    List<HistorialMedico> findByMascotaUsuarioAndFechaGreaterThanEqualOrderByFechaAsc(
            Usuario usuario,
            LocalDate fechaDesde
    );
}
