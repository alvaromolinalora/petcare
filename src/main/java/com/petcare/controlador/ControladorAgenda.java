package com.petcare.controlador;

import com.petcare.modelo.HistorialMedico;
import com.petcare.modelo.Usuario;
import com.petcare.servicio.ServicioHistorialMedico;
import com.petcare.servicio.ServicioUsuarios;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

@Controller
@RequestMapping("/agenda")
public class ControladorAgenda {

    private final ServicioHistorialMedico servicioHistorial;
    private final ServicioUsuarios servicioUsuarios;

    public ControladorAgenda(ServicioHistorialMedico servicioHistorial,
                             ServicioUsuarios servicioUsuarios) {
        this.servicioHistorial = servicioHistorial;
        this.servicioUsuarios = servicioUsuarios;
    }

    @GetMapping
    public String verAgenda(Model modelo, HttpSession sesion) {

        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Usuario usuario = servicioUsuarios.buscarPorId(usuarioId);

        List<HistorialMedico> citas = servicioHistorial.listarProximasCitas(usuario);
        // >>> CLAVE para Render: nunca iterar sobre null
        if (citas == null) {
            citas = new ArrayList<>();
        }

        Map<String, List<HistorialMedico>> agendaPorMascota = new LinkedHashMap<>();

        for (HistorialMedico cita : citas) {
            String nombreMascota = cita.getMascota().getNombre();
            agendaPorMascota
                    .computeIfAbsent(nombreMascota, k -> new ArrayList<>())
                    .add(cita);
        }

        modelo.addAttribute("agendaPorMascota", agendaPorMascota);
        return "agenda";
    }
}
