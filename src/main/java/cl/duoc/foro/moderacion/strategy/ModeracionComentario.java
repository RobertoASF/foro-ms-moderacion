package cl.duoc.foro.moderacion.strategy;

import cl.duoc.foro.moderacion.client.PublicacionesClient;
import cl.duoc.foro.moderacion.model.TipoContenido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModeracionComentario implements EstrategiaModeracion {

    private final PublicacionesClient publicaciones;

    @Override
    public TipoContenido tipo() {
        return TipoContenido.COMENTARIO;
    }

    @Override
    public void verificarExiste(Long contenidoId) {
        publicaciones.verificarExiste("/api/comentarios/{id}", contenidoId, "el comentario");
    }

    @Override
    public void cambiarBaneo(Long contenidoId, boolean baneado, String token) {
        publicaciones.cambiarBaneo("/api/comentarios/{id}/baneo", contenidoId, baneado, token, "el comentario");
    }
}
