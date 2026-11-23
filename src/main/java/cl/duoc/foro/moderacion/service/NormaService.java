package cl.duoc.foro.moderacion.service;

import cl.duoc.foro.moderacion.dto.NormaRequest;
import cl.duoc.foro.moderacion.dto.NormaResponse;
import cl.duoc.foro.moderacion.exception.ConflictoException;
import cl.duoc.foro.moderacion.exception.RecursoNoEncontradoException;
import cl.duoc.foro.moderacion.model.Norma;
import cl.duoc.foro.moderacion.repository.NormaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NormaService {

    private final NormaRepository normaRepository;

    @Transactional(readOnly = true)
    public List<NormaResponse> listar() {
        return normaRepository.findAllByOrderByIdAsc().stream().map(NormaResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public NormaResponse obtener(Long id) {
        return NormaResponse.desde(buscar(id));
    }

    @Transactional
    public NormaResponse crear(NormaRequest datos) {
        if (normaRepository.existsByTituloIgnoreCase(datos.titulo())) {
            throw new ConflictoException("Ya existe una norma con ese título");
        }

        Norma norma = new Norma();
        norma.setTitulo(datos.titulo());
        norma.setDescripcion(datos.descripcion());

        Norma guardada = normaRepository.save(norma);
        log.info("Norma creada con id {}", guardada.getId());
        return NormaResponse.desde(guardada);
    }

    @Transactional
    public NormaResponse actualizar(Long id, NormaRequest datos) {
        Norma norma = buscar(id);
        if (normaRepository.existsByTituloIgnoreCaseAndIdNot(datos.titulo(), id)) {
            throw new ConflictoException("Ya existe una norma con ese título");
        }

        norma.setTitulo(datos.titulo());
        norma.setDescripcion(datos.descripcion());
        log.info("Norma {} actualizada", id);
        return NormaResponse.desde(norma);
    }

    // Si la norma ya se usó en reportes o baneos, Oracle rechaza el borrado y responde 409
    @Transactional
    public void eliminar(Long id) {
        normaRepository.delete(buscar(id));
        normaRepository.flush();
        log.info("Norma {} eliminada", id);
    }

    public Norma buscar(Long id) {
        return normaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la norma con id " + id));
    }
}
