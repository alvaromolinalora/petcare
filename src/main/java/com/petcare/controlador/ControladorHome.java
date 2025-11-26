package com.petcare.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorHome {

    @GetMapping("/")
    public String redirigirAlLogin() {
        return "redirect:/login";
    }
}
