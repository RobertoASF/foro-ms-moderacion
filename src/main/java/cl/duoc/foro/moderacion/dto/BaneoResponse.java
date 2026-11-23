package cl.duoc.foro.moderacion.dto;

import cl.duoc.foro.moderacion.model.Baneo;
import cl.duoc.foro.moderacion.model.TipoContenido;
import java.time.LocalDateTime;

public record BaneoResponse(
        Long id,
        TipoContenido tipoContenido,
        Long contenidoId,
        Long normaId,
        String norma,
        String motivo,
        String moderadorNombre,
        boolean activo,
        LocalDateTime fechaBaneo,
        String levantadoPorNombre,
        LocalDateTime fechaLevantamiento) {

    public static BaneoResponse desde(Baneo baneo) {
        return new BaneoResponse(
                baneo.getId(),
                baneo.getTipoContenido(),
                baneo.getContenidoId(),
                baneo.getNorma().getId(),
                baneo.getNorma().getTitulo(),
                baneo.getMotivo(),
                baneo.getModeradorNombre(),
                baneo.isActivo(),
                baneo.getFechaBaneo(),
                baneo.getLevantadoPorNombre(),
                baneo.getFechaLevantamiento());
    }
}
