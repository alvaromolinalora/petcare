package com.petcare.controlador;

import com.petcare.modelo.HistorialMedico;
import com.petcare.modelo.Mascota;
import com.petcare.modelo.Usuario;
import com.petcare.servicio.ServicioHistorialMedico;
import com.petcare.servicio.ServicioMascotas;
import com.petcare.servicio.ServicioUsuarios;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/historial")
public class ControladorHistorialMedico {

    private final ServicioHistorialMedico servicioHistorial;
    private final ServicioMascotas servicioMascotas;
    private final ServicioUsuarios servicioUsuarios;

    public ControladorHistorialMedico(ServicioHistorialMedico servicioHistorial,
                                      ServicioMascotas servicioMascotas,
                                      ServicioUsuarios servicioUsuarios) {
        this.servicioHistorial = servicioHistorial;
        this.servicioMascotas = servicioMascotas;
        this.servicioUsuarios = servicioUsuarios;
    }

    @GetMapping("/{idMascota}")
    public String verHistorial(@PathVariable Long idMascota,
                               Model modelo,
                               HttpSession sesion) {

        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/login";

        Mascota mascota = servicioMascotas.buscarPorId(idMascota);
        if (mascota == null || !mascota.getUsuario().getId().equals(usuarioId)) {
            return "redirect:/mascotas";
        }

        List<HistorialMedico> historial = servicioHistorial.listarPorMascota(mascota);

        modelo.addAttribute("mascota", mascota);
        modelo.addAttribute("historial", historial);

        return "historial_lista";
    }

    @GetMapping("/nuevo/{idMascota}")
    public String nuevoRegistro(@PathVariable Long idMascota,
                                Model modelo,
                                HttpSession sesion) {

        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/login";

        Mascota mascota = servicioMascotas.buscarPorId(idMascota);
        if (mascota == null || !mascota.getUsuario().getId().equals(usuarioId)) {
            return "redirect:/mascotas";
        }

        HistorialMedico h = new HistorialMedico();
        h.setMascota(mascota);

        modelo.addAttribute("registro", h);
        return "historial_form";
    }

    @PostMapping("/guardar")
    public String guardarRegistro(@ModelAttribute("registro") HistorialMedico registro,
                                  HttpSession sesion) {

        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/login";

        if (registro.getMascota() == null || registro.getMascota().getId() == null) {
            return "redirect:/mascotas";
        }

        Mascota mascotaBD = servicioMascotas.buscarPorId(registro.getMascota().getId());
        if (mascotaBD == null) {
            return "redirect:/mascotas";
        }

        if (!mascotaBD.getUsuario().getId().equals(usuarioId)) {
            return "redirect:/mascotas";
        }

        registro.setMascota(mascotaBD);

        servicioHistorial.guardar(registro);

        return "redirect:/historial/" + mascotaBD.getId();
    }

    @GetMapping("/editar/{id}")
    public String editarRegistro(@PathVariable Long id,
                                 Model modelo,
                                 HttpSession sesion) {

        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/login";

        HistorialMedico registro = servicioHistorial.buscarPorId(id);
        if (registro == null) return "redirect:/mascotas";

        // verificar propietario
        if (!registro.getMascota().getUsuario().getId().equals(usuarioId)) {
            return "redirect:/mascotas";
        }

        modelo.addAttribute("registro", registro);
        return "historial_form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarRegistro(@PathVariable Long id,
                                   HttpSession sesion) {

        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/login";

        HistorialMedico registro = servicioHistorial.buscarPorId(id);
        if (registro == null) return "redirect:/mascotas";

        if (!registro.getMascota().getUsuario().getId().equals(usuarioId)) {
            return "redirect:/mascotas";
        }

        Long idMascota = registro.getMascota().getId();
        servicioHistorial.eliminar(id);

        return "redirect:/historial/" + idMascota;
    }
}
