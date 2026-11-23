package cl.duoc.foro.moderacion.repository;

import cl.duoc.foro.moderacion.model.Baneo;
import cl.duoc.foro.moderacion.model.TipoContenido;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaneoRepository extends JpaRepository<Baneo, Long> {

    @EntityGraph(attributePaths = "norma")
    List<Baneo> findAllByOrderByFechaBaneoDesc();

    @EntityGraph(attributePaths = "norma")
    List<Baneo> findByActivoOrderByFechaBaneoDesc(boolean activo);

    boolean existsByTipoContenidoAndContenidoIdAndActivo(TipoContenido tipo, Long contenidoId, boolean activo);

    long countByActivo(boolean activo);

    long countByTipoContenidoAndActivo(TipoContenido tipo, boolean activo);
}
