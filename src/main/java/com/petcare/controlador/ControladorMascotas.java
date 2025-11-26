package com.petcare.controlador;

import com.petcare.modelo.Mascota;
import com.petcare.modelo.Usuario;
import com.petcare.servicio.ServicioMascotas;
import com.petcare.servicio.ServicioUsuarios;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mascotas")
public class ControladorMascotas {

    private final ServicioMascotas servicioMascotas;
    private final ServicioUsuarios servicioUsuarios;

    public ControladorMascotas(ServicioMascotas servicioMascotas, ServicioUsuarios servicioUsuarios) {
        this.servicioMascotas = servicioMascotas;
        this.servicioUsuarios = servicioUsuarios;
    }

    @GetMapping
    public String listarMascotas(Model modelo, HttpSession sesion) {
        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Usuario usuario = servicioUsuarios.buscarPorId(usuarioId);
        List<Mascota> lista = servicioMascotas.listarPorUsuario(usuario);

        // *** CLAVE: nunca mandar null a la vista ***
        if (lista == null) {
            lista = new ArrayList<>();
        }

        modelo.addAttribute("mascotas", lista);
        return "mascotas";
    }

    @GetMapping("/nueva")
    public String nuevaMascota(Model modelo) {
        modelo.addAttribute("mascota", new Mascota());
        return "mascota_form";
    }

    @PostMapping("/guardar")
    public String guardarMascota(@ModelAttribute Mascota mascota,
                                 @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo,
                                 HttpSession sesion) {

        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Usuario usuario = servicioUsuarios.buscarPorId(usuarioId);
        mascota.setUsuario(usuario);

        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            try {
                String carpeta = "src/main/resources/static/uploads/mascotas";
                Path rutaCarpeta = Paths.get(carpeta);
                Files.createDirectories(rutaCarpeta);

                String nombreOriginal = fotoArchivo.getOriginalFilename();
                String nombreLimpio = nombreOriginal != null ? nombreOriginal.replace(" ", "_") : "mascota";
                String nombreArchivo = System.currentTimeMillis() + "_" + nombreLimpio;

                Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);
                Files.copy(fotoArchivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

                String urlFoto = "/uploads/mascotas/" + nombreArchivo;
                mascota.setFoto(urlFoto);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        servicioMascotas.guardarMascota(mascota);
        return "redirect:/mascotas";
    }

    @GetMapping("/editar/{id}")
    public String editarMascota(@PathVariable Long id,
                                Model modelo,
                                HttpSession sesion) {
        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/login";

        Mascota m = servicioMascotas.buscarPorId(id);
        if (m == null || !m.getUsuario().getId().equals(usuarioId)) {
            return "redirect:/mascotas";
        }

        modelo.addAttribute("mascota", m);
        return "mascota_editar";
    }

    @PostMapping("/actualizar")
    public String actualizarMascota(@ModelAttribute Mascota mascota, HttpSession sesion) {
        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/login";

        Usuario usuario = servicioUsuarios.buscarPorId(usuarioId);
        mascota.setUsuario(usuario);

        servicioMascotas.guardarMascota(mascota);
        return "redirect:/mascotas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id,
                                  HttpSession sesion) {
        Long usuarioId = (Long) sesion.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/login";

        Mascota m = servicioMascotas.buscarPorId(id);
        if (m != null && m.getUsuario().getId().equals(usuarioId)) {
            servicioMascotas.eliminarMascota(id);
        }
        return "redirect:/mascotas";
    }
}
