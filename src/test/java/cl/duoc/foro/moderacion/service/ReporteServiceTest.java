package cl.duoc.foro.moderacion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.foro.moderacion.dto.ReporteRequest;
import cl.duoc.foro.moderacion.dto.ReporteResponse;
import cl.duoc.foro.moderacion.exception.ConflictoException;
import cl.duoc.foro.moderacion.exception.RecursoNoEncontradoException;
import cl.duoc.foro.moderacion.model.EstadoReporte;
import cl.duoc.foro.moderacion.model.Norma;
import cl.duoc.foro.moderacion.model.Reporte;
import cl.duoc.foro.moderacion.model.TipoContenido;
import cl.duoc.foro.moderacion.repository.ReporteRepository;
import cl.duoc.foro.moderacion.security.UsuarioAutenticado;
import cl.duoc.foro.moderacion.strategy.EstrategiaModeracion;
import cl.duoc.foro.moderacion.strategy.SelectorEstrategia;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    private static final UsuarioAutenticado CAMILA = new UsuarioAutenticado(3L, "camila", "USUARIO");
    private static final UsuarioAutenticado MODERADOR = new UsuarioAutenticado(2L, "moderador", "MODERADOR");
    private static final ReporteRequest REPORTE =
            new ReporteRequest(TipoContenido.COMENTARIO, 5L, 1L, "Insulta a otros jugadores");

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private NormaService normaService;

    @Mock
    private SelectorEstrategia selectorEstrategia;

    @Mock
    private EstrategiaModeracion estrategiaComentario;

    @InjectMocks
    private ReporteService reporteService;

    @Test
    void reportarRevisaQueElContenidoExistaYGuardaAlAutor() {
        when(normaService.buscar(1L)).thenReturn(norma());
        when(selectorEstrategia.para(TipoContenido.COMENTARIO)).thenReturn(estrategiaComentario);
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(llamada -> llamada.getArgument(0));

        ReporteResponse reporte = reporteService.crear(REPORTE, CAMILA);

        verify(estrategiaComentario).verificarExiste(5L);
        assertThat(reporte.reportadoPorNombre()).isEqualTo("camila");
        assertThat(reporte.estado()).isEqualTo(EstadoReporte.PENDIENTE);
    }

    @Test
    void reportarFallaSiElComentarioNoExiste() {
        when(normaService.buscar(1L)).thenReturn(norma());
        when(selectorEstrategia.para(TipoContenido.COMENTARIO)).thenReturn(estrategiaComentario);
        Mockito.doThrow(new RecursoNoEncontradoException("No existe el comentario con id 5"))
                .when(estrategiaComentario).verificarExiste(5L);

        assertThatThrownBy(() -> reporteService.crear(REPORTE, CAMILA))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(reporteRepository, never()).save(any());
    }

    @Test
    void noSePuedeReportarDosVecesLoMismo() {
        when(reporteRepository.existsByTipoContenidoAndContenidoIdAndReportadoPorIdAndEstado(
                TipoContenido.COMENTARIO, 5L, 3L, EstadoReporte.PENDIENTE)).thenReturn(true);

        assertThatThrownBy(() -> reporteService.crear(REPORTE, CAMILA))
                .isInstanceOf(ConflictoException.class);
    }

    @Test
    void descartarMarcaQuienLoReviso() {
        Reporte reporte = new Reporte();
        reporte.setNorma(norma());
        when(reporteRepository.findById(4L)).thenReturn(Optional.of(reporte));

        ReporteResponse descartado = reporteService.descartar(4L, MODERADOR);

        assertThat(descartado.estado()).isEqualTo(EstadoReporte.DESCARTADO);
        assertThat(descartado.revisadoPorNombre()).isEqualTo("moderador");
    }

    @Test
    void descartarRechazaUnReporteYaRevisado() {
        Reporte reporte = new Reporte();
        reporte.revisar(EstadoReporte.ACEPTADO, "admin");
        when(reporteRepository.findById(4L)).thenReturn(Optional.of(reporte));

        assertThatThrownBy(() -> reporteService.descartar(4L, MODERADOR))
                .isInstanceOf(ConflictoException.class);
    }

    private Norma norma() {
        Norma norma = new Norma();
        norma.setId(1L);
        norma.setTitulo("Respeto entre jugadores");
        return norma;
    }
}
