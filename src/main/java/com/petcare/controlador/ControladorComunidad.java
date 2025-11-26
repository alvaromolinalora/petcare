package com.petcare.controlador;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comunidad")
public class ControladorComunidad {

    @GetMapping
    public String mostrarComunidad(HttpSession sesion) {
        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        return "comunidad";
    }
}
