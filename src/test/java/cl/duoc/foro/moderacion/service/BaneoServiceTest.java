package cl.duoc.foro.moderacion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.foro.moderacion.dto.BaneoRequest;
import cl.duoc.foro.moderacion.dto.BaneoResponse;
import cl.duoc.foro.moderacion.exception.ConflictoException;
import cl.duoc.foro.moderacion.model.Baneo;
import cl.duoc.foro.moderacion.model.Norma;
import cl.duoc.foro.moderacion.model.TipoContenido;
import cl.duoc.foro.moderacion.repository.BaneoRepository;
import cl.duoc.foro.moderacion.security.UsuarioAutenticado;
import cl.duoc.foro.moderacion.strategy.EstrategiaModeracion;
import cl.duoc.foro.moderacion.strategy.SelectorEstrategia;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BaneoServiceTest {

    private static final UsuarioAutenticado MODERADOR = new UsuarioAutenticado(2L, "moderador", "MODERADOR");
    private static final UsuarioAutenticado ADMIN = new UsuarioAutenticado(1L, "admin", "ADMIN");
    private static final BaneoRequest BANEO_TEMA =
            new BaneoRequest(TipoContenido.TEMA, 7L, 2L, "Publicidad que no tiene relación con el foro");

    @Mock
    private BaneoRepository baneoRepository;

    @Mock
    private NormaService normaService;

    @Mock
    private ReporteService reporteService;

    @Mock
    private SelectorEstrategia selectorEstrategia;

    @Mock
    private EstrategiaModeracion estrategiaTema;

    @InjectMocks
    private BaneoService baneoService;

    @Test
    void banearGuardaElModeradorYOcultaElContenido() {
        when(normaService.buscar(2L)).thenReturn(norma());
        when(baneoRepository.save(any(Baneo.class))).thenAnswer(llamada -> llamada.getArgument(0));
        when(selectorEstrategia.para(TipoContenido.TEMA)).thenReturn(estrategiaTema);

        BaneoResponse baneo = baneoService.banear(BANEO_TEMA, MODERADOR, "token");

        assertThat(baneo.moderadorNombre()).isEqualTo("moderador");
        assertThat(baneo.activo()).isTrue();
        verify(estrategiaTema).cambiarBaneo(7L, true, "token");
        verify(reporteService).aceptarPendientes(TipoContenido.TEMA, 7L, MODERADOR);
    }

    @Test
    void banearRechazaContenidoQueYaEstaBaneado() {
        when(baneoRepository.existsByTipoContenidoAndContenidoIdAndActivo(TipoContenido.TEMA, 7L, true)).thenReturn(true);

        assertThatThrownBy(() -> baneoService.banear(BANEO_TEMA, MODERADOR, "token"))
                .isInstanceOf(ConflictoException.class);
        verify(baneoRepository, never()).save(any());
    }

    @Test
    void levantarDevuelveElContenidoAlForo() {
        Baneo baneo = baneoActivo();
        when(baneoRepository.findById(1L)).thenReturn(Optional.of(baneo));
        when(selectorEstrategia.para(TipoContenido.TEMA)).thenReturn(estrategiaTema);

        BaneoResponse levantado = baneoService.levantar(1L, ADMIN, "token");

        assertThat(levantado.activo()).isFalse();
        assertThat(levantado.levantadoPorNombre()).isEqualTo("admin");
        verify(estrategiaTema).cambiarBaneo(7L, false, "token");
    }

    @Test
    void levantarRechazaUnBaneoYaLevantado() {
        Baneo baneo = baneoActivo();
        baneo.levantar("admin");
        when(baneoRepository.findById(1L)).thenReturn(Optional.of(baneo));

        assertThatThrownBy(() -> baneoService.levantar(1L, ADMIN, "token"))
                .isInstanceOf(ConflictoException.class);
    }

    private Norma norma() {
        Norma norma = new Norma();
        norma.setId(2L);
        norma.setTitulo("Sin spam ni publicidad");
        return norma;
    }

    private Baneo baneoActivo() {
        Baneo baneo = new Baneo();
        baneo.setId(1L);
        baneo.setTipoContenido(TipoContenido.TEMA);
        baneo.setContenidoId(7L);
        baneo.setNorma(norma());
        return baneo;
    }
}
