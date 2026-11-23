package cl.duoc.foro.moderacion.repository;

import cl.duoc.foro.moderacion.model.Norma;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormaRepository extends JpaRepository<Norma, Long> {

    List<Norma> findAllByOrderByIdAsc();

    boolean existsByTituloIgnoreCase(String titulo);

    boolean existsByTituloIgnoreCaseAndIdNot(String titulo, Long id);
}
