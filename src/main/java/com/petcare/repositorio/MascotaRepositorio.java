package com.petcare.repositorio;

import com.petcare.modelo.Mascota;
import com.petcare.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MascotaRepositorio extends JpaRepository<Mascota, Long> {

    List<Mascota> findByUsuario(Usuario usuario);
}
