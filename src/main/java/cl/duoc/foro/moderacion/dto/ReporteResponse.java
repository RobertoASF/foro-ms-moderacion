package cl.duoc.foro.moderacion.dto;

import cl.duoc.foro.moderacion.model.EstadoReporte;
import cl.duoc.foro.moderacion.model.Reporte;
import cl.duoc.foro.moderacion.model.TipoContenido;
import java.time.LocalDateTime;

public record ReporteResponse(
        Long id,
        TipoContenido tipoContenido,
        Long contenidoId,
        Long normaId,
        String norma,
        String detalle,
        String reportadoPorNombre,
        EstadoReporte estado,
        String revisadoPorNombre,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaRevision) {

    public static ReporteResponse desde(Reporte reporte) {
        return new ReporteResponse(
                reporte.getId(),
                reporte.getTipoContenido(),
                reporte.getContenidoId(),
                reporte.getNorma().getId(),
                reporte.getNorma().getTitulo(),
                reporte.getDetalle(),
                reporte.getReportadoPorNombre(),
                reporte.getEstado(),
                reporte.getRevisadoPorNombre(),
                reporte.getFechaCreacion(),
                reporte.getFechaRevision());
    }
}
