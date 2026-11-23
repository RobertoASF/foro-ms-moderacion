package cl.duoc.foro.moderacion.strategy;

import cl.duoc.foro.moderacion.model.TipoContenido;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SelectorEstrategia {

    private final Map<TipoContenido, EstrategiaModeracion> estrategias = new EnumMap<>(TipoContenido.class);

    // Spring entrega todas las estrategias; para un tipo nuevo basta con crear otra clase
    public SelectorEstrategia(List<EstrategiaModeracion> disponibles) {
        disponibles.forEach(estrategia -> estrategias.put(estrategia.tipo(), estrategia));
    }

    public EstrategiaModeracion para(TipoContenido tipo) {
        EstrategiaModeracion estrategia = estrategias.get(tipo);
        if (estrategia == null) {
            throw new IllegalArgumentException("No hay estrategia de moderación para " + tipo);
        }
        return estrategia;
    }
}
