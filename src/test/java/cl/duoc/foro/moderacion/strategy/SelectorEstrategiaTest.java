package cl.duoc.foro.moderacion.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import cl.duoc.foro.moderacion.client.PublicacionesClient;
import cl.duoc.foro.moderacion.model.TipoContenido;
import java.util.List;
import org.junit.jupiter.api.Test;

class SelectorEstrategiaTest {

    private final PublicacionesClient cliente = mock(PublicacionesClient.class);
    private final SelectorEstrategia selector = new SelectorEstrategia(
            List.of(new ModeracionTema(cliente), new ModeracionComentario(cliente)));

    @Test
    void entregaLaEstrategiaSegunElTipo() {
        assertThat(selector.para(TipoContenido.TEMA)).isInstanceOf(ModeracionTema.class);
        assertThat(selector.para(TipoContenido.COMENTARIO)).isInstanceOf(ModeracionComentario.class);
    }
}
