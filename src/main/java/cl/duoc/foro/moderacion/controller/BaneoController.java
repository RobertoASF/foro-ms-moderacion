package cl.duoc.foro.moderacion.controller;

import cl.duoc.foro.moderacion.dto.BaneoRequest;
import cl.duoc.foro.moderacion.dto.BaneoResponse;
import cl.duoc.foro.moderacion.dto.ResumenModeracionResponse;
import cl.duoc.foro.moderacion.security.UsuarioAutenticado;
import cl.duoc.foro.moderacion.service.BaneoService;
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
@RequestMapping("/api/baneos")
@CrossOrigin(origins = "${cors.origen}")
@RequiredArgsConstructor
public class BaneoController {

    private final BaneoService baneoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERADOR')")
    public List<BaneoResponse> listar(@RequestParam(required = false) Boolean activo) {
        return baneoService.listar(activo);
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERADOR')")
    public ResumenModeracionResponse resumir() {
        return baneoService.resumir();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERADOR')")
    public BaneoResponse obtener(@PathVariable Long id) {
        return baneoService.obtener(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERADOR')")
    public ResponseEntity<BaneoResponse> banear(@Valid @RequestBody BaneoRequest datos,
            @AuthenticationPrincipal Jwt jwt) {
        BaneoResponse creado = baneoService.banear(datos, UsuarioAutenticado.desde(jwt), jwt.getTokenValue());
        return ResponseEntity.created(URI.create("/api/baneos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}/levantar")
    @PreAuthorize("hasRole('ADMIN')")
    public BaneoResponse levantar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return baneoService.levantar(id, UsuarioAutenticado.desde(jwt), jwt.getTokenValue());
    }
}
