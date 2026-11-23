package cl.duoc.foro.moderacion.dto;

public record ResumenModeracionResponse(
        long reportesPendientes,
        long baneosActivos,
        long temasBaneados,
        long comentariosBaneados) {
}
