package com.petcare.controlador;

import com.petcare.modelo.Usuario;
import com.petcare.servicio.ServicioUsuarios;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControladorAutenticacion {

    private final ServicioUsuarios servicioUsuarios;

    public ControladorAutenticacion(ServicioUsuarios servicioUsuarios) {
        this.servicioUsuarios = servicioUsuarios;
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String usuario,
                                   @RequestParam(required = false) String nombreCompleto,
                                   @RequestParam String contrasena) {

        boolean creado = servicioUsuarios.registrar(usuario, contrasena, nombreCompleto);

        if (!creado) {
            return "redirect:/registro?existe=1";
        }

        return "redirect:/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario,
                                @RequestParam String contrasena,
                                HttpSession sesion) {

        Usuario u = servicioUsuarios.validarLogin(usuario, contrasena);
        if (u == null) {
            return "redirect:/login?error=1";
        }

        sesion.setAttribute("usuarioId", u.getId());
        sesion.setAttribute("usuarioNombre", u.getNombreUsuario());

        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/salir")
    public String salir(HttpSession sesion) {
        sesion.invalidate();
        return "redirect:/login";
    }
}
