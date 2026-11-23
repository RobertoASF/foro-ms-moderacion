package cl.duoc.foro.moderacion.dto;

import cl.duoc.foro.moderacion.model.TipoContenido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ReporteRequest(
        @NotNull(message = "El tipo de contenido es obligatorio: TEMA o COMENTARIO")
        TipoContenido tipoContenido,

        @NotNull(message = "El id del contenido es obligatorio")
        @Positive(message = "El id del contenido debe ser positivo")
        Long contenidoId,

        @NotNull(message = "La norma incumplida es obligatoria")
        @Positive(message = "El id de la norma debe ser positivo")
        Long normaId,

        @Size(max = 500, message = "El detalle no puede superar los 500 caracteres")
        String detalle) {
}
