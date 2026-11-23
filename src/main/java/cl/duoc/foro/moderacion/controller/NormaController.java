package cl.duoc.foro.moderacion.controller;

import cl.duoc.foro.moderacion.dto.MensajeResponse;
import cl.duoc.foro.moderacion.dto.NormaRequest;
import cl.duoc.foro.moderacion.dto.NormaResponse;
import cl.duoc.foro.moderacion.service.NormaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/normas")
@CrossOrigin(origins = "${cors.origen}")
@RequiredArgsConstructor
public class NormaController {

    private final NormaService normaService;

    @GetMapping
    public List<NormaResponse> listar() {
        return normaService.listar();
    }

    @GetMapping("/{id}")
    public NormaResponse obtener(@PathVariable Long id) {
        return normaService.obtener(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NormaResponse> crear(@Valid @RequestBody NormaRequest datos) {
        NormaResponse creada = normaService.crear(datos);
        return ResponseEntity.created(URI.create("/api/normas/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public NormaResponse actualizar(@PathVariable Long id, @Valid @RequestBody NormaRequest datos) {
        return normaService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MensajeResponse eliminar(@PathVariable Long id) {
        normaService.eliminar(id);
        return new MensajeResponse("Norma eliminada");
    }
}
