package cl.duoc.foro.moderacion.controller;

import cl.duoc.foro.moderacion.dto.ReporteRequest;
import cl.duoc.foro.moderacion.dto.ReporteResponse;
import cl.duoc.foro.moderacion.model.EstadoReporte;
import cl.duoc.foro.moderacion.security.UsuarioAutenticado;
import cl.duoc.foro.moderacion.service.ReporteService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "${cors.origen}")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping
    public ResponseEntity<ReporteResponse> crear(@Valid @RequestBody ReporteRequest datos,
            @AuthenticationPrincipal Jwt jwt) {
        ReporteResponse creado = reporteService.crear(datos, UsuarioAutenticado.desde(jwt));
        return ResponseEntity.created(URI.create("/api/reportes/" + creado.id())).body(creado);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERADOR')")
    public List<ReporteResponse> listar(@RequestParam(required = false) EstadoReporte estado) {
        return reporteService.listar(estado);
    }

    @GetMapping("/mios")
    public List<ReporteResponse> listarPropios(@AuthenticationPrincipal Jwt jwt) {
        return reporteService.listarPropios(UsuarioAutenticado.desde(jwt));
    }

    @PutMapping("/{id}/descartar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERADOR')")
    public ReporteResponse descartar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return reporteService.descartar(id, UsuarioAutenticado.desde(jwt));
    }
}
