package cl.duoc.foro.moderacion.strategy;

import cl.duoc.foro.moderacion.model.TipoContenido;

/**
 * Patrón Strategy: cada tipo de contenido sabe cómo revisarse y banearse en ms-publicaciones.
 * Los servicios piden la estrategia por tipo y no necesitan saber qué ruta llamar.
 */
public interface EstrategiaModeracion {

    TipoContenido tipo();

    void verificarExiste(Long contenidoId);

    void cambiarBaneo(Long contenidoId, boolean baneado, String token);
}
