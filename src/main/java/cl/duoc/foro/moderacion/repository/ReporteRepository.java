package cl.duoc.foro.moderacion.repository;

import cl.duoc.foro.moderacion.model.EstadoReporte;
import cl.duoc.foro.moderacion.model.Reporte;
import cl.duoc.foro.moderacion.model.TipoContenido;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    @EntityGraph(attributePaths = "norma")
    List<Reporte> findAllByOrderByFechaCreacionDesc();

    @EntityGraph(attributePaths = "norma")
    List<Reporte> findByEstadoOrderByFechaCreacionAsc(EstadoReporte estado);

    @EntityGraph(attributePaths = "norma")
    List<Reporte> findByReportadoPorIdOrderByFechaCreacionDesc(Long reportadoPorId);

    List<Reporte> findByTipoContenidoAndContenidoIdAndEstado(TipoContenido tipo, Long contenidoId, EstadoReporte estado);

    boolean existsByTipoContenidoAndContenidoIdAndReportadoPorIdAndEstado(TipoContenido tipo, Long contenidoId,
            Long reportadoPorId, EstadoReporte estado);

    long countByEstado(EstadoReporte estado);
}
