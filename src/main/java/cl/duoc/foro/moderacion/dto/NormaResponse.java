package cl.duoc.foro.moderacion.dto;

import cl.duoc.foro.moderacion.model.Norma;

public record NormaResponse(Long id, String titulo, String descripcion) {

    public static NormaResponse desde(Norma norma) {
        return new NormaResponse(norma.getId(), norma.getTitulo(), norma.getDescripcion());
    }
}
