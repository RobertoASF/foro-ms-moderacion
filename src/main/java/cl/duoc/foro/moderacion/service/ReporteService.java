package cl.duoc.foro.moderacion.service;

import cl.duoc.foro.moderacion.dto.ReporteRequest;
import cl.duoc.foro.moderacion.dto.ReporteResponse;
import cl.duoc.foro.moderacion.exception.ConflictoException;
import cl.duoc.foro.moderacion.exception.RecursoNoEncontradoException;
import cl.duoc.foro.moderacion.model.EstadoReporte;
import cl.duoc.foro.moderacion.model.Reporte;
import cl.duoc.foro.moderacion.model.TipoContenido;
import cl.duoc.foro.moderacion.repository.ReporteRepository;
import cl.duoc.foro.moderacion.security.UsuarioAutenticado;
import cl.duoc.foro.moderacion.strategy.SelectorEstrategia;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final NormaService normaService;
    private final SelectorEstrategia selectorEstrategia;

    @Transactional(readOnly = true)
    public List<ReporteResponse> listar(EstadoReporte estado) {
        List<Reporte> reportes = estado == null
                ? reporteRepository.findAllByOrderByFechaCreacionDesc()
                : reporteRepository.findByEstadoOrderByFechaCreacionAsc(estado);
        return reportes.stream().map(ReporteResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public List<ReporteResponse> listarPropios(UsuarioAutenticado usuario) {
        return reporteRepository.findByReportadoPorIdOrderByFechaCreacionDesc(usuario.id()).stream()
                .map(ReporteResponse::desde)
                .toList();
    }

    @Transactional
    public ReporteResponse crear(ReporteRequest datos, UsuarioAutenticado usuario) {
        if (reporteRepository.existsByTipoContenidoAndContenidoIdAndReportadoPorIdAndEstado(
                datos.tipoContenido(), datos.contenidoId(), usuario.id(), EstadoReporte.PENDIENTE)) {
            throw new ConflictoException("Ya reportaste este contenido y está pendiente de revisión");
        }

        Reporte reporte = new Reporte();
        reporte.setNorma(normaService.buscar(datos.normaId()));
        selectorEstrategia.para(datos.tipoContenido()).verificarExiste(datos.contenidoId());
        reporte.setTipoContenido(datos.tipoContenido());
        reporte.setContenidoId(datos.contenidoId());
        reporte.setDetalle(datos.detalle());
        reporte.setReportadoPorId(usuario.id());
        reporte.setReportadoPorNombre(usuario.nombreUsuario());

        Reporte guardado = reporteRepository.save(reporte);
        log.info("Reporte {} creado por el usuario {}", guardado.getId(), usuario.id());
        return ReporteResponse.desde(guardado);
    }

    @Transactional
    public ReporteResponse descartar(Long id, UsuarioAutenticado moderador) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el reporte con id " + id));
        if (!reporte.estaPendiente()) {
            throw new ConflictoException("El reporte ya fue revisado");
        }

        reporte.revisar(EstadoReporte.DESCARTADO, moderador.nombreUsuario());
        log.info("Reporte {} descartado por el usuario {}", id, moderador.id());
        return ReporteResponse.desde(reporte);
    }

    // Al banear un contenido, todos sus reportes pendientes quedan aceptados
    @Transactional
    public void aceptarPendientes(TipoContenido tipo, Long contenidoId, UsuarioAutenticado moderador) {
        reporteRepository.findByTipoContenidoAndContenidoIdAndEstado(tipo, contenidoId, EstadoReporte.PENDIENTE)
                .forEach(reporte -> reporte.revisar(EstadoReporte.ACEPTADO, moderador.nombreUsuario()));
    }

    @Transactional(readOnly = true)
    public long contarPendientes() {
        return reporteRepository.countByEstado(EstadoReporte.PENDIENTE);
    }
}
