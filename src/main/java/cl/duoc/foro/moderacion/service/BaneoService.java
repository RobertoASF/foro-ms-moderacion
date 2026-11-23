package cl.duoc.foro.moderacion.service;

import cl.duoc.foro.moderacion.dto.BaneoRequest;
import cl.duoc.foro.moderacion.dto.BaneoResponse;
import cl.duoc.foro.moderacion.dto.ResumenModeracionResponse;
import cl.duoc.foro.moderacion.exception.ConflictoException;
import cl.duoc.foro.moderacion.exception.RecursoNoEncontradoException;
import cl.duoc.foro.moderacion.model.Baneo;
import cl.duoc.foro.moderacion.model.TipoContenido;
import cl.duoc.foro.moderacion.repository.BaneoRepository;
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
public class BaneoService {

    private final BaneoRepository baneoRepository;
    private final NormaService normaService;
    private final ReporteService reporteService;
    private final SelectorEstrategia selectorEstrategia;

    @Transactional(readOnly = true)
    public List<BaneoResponse> listar(Boolean activo) {
        List<Baneo> baneos = activo == null
                ? baneoRepository.findAllByOrderByFechaBaneoDesc()
                : baneoRepository.findByActivoOrderByFechaBaneoDesc(activo);
        return baneos.stream().map(BaneoResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public BaneoResponse obtener(Long id) {
        return BaneoResponse.desde(buscar(id));
    }

    @Transactional
    public BaneoResponse banear(BaneoRequest datos, UsuarioAutenticado moderador, String token) {
        if (baneoRepository.existsByTipoContenidoAndContenidoIdAndActivo(datos.tipoContenido(), datos.contenidoId(), true)) {
            throw new ConflictoException("Este contenido ya está baneado");
        }

        Baneo baneo = new Baneo();
        baneo.setTipoContenido(datos.tipoContenido());
        baneo.setContenidoId(datos.contenidoId());
        baneo.setNorma(normaService.buscar(datos.normaId()));
        baneo.setMotivo(datos.motivo());
        baneo.setModeradorId(moderador.id());
        baneo.setModeradorNombre(moderador.nombreUsuario());

        Baneo guardado = baneoRepository.save(baneo);
        reporteService.aceptarPendientes(datos.tipoContenido(), datos.contenidoId(), moderador);

        // Se llama al final: si ms-publicaciones falla, la transacción se revierte y no queda un baneo a medias
        selectorEstrategia.para(datos.tipoContenido()).cambiarBaneo(datos.contenidoId(), true, token);
        log.info("{} {} baneado por el usuario {}", datos.tipoContenido(), datos.contenidoId(), moderador.id());
        return BaneoResponse.desde(guardado);
    }

    @Transactional
    public BaneoResponse levantar(Long id, UsuarioAutenticado administrador, String token) {
        Baneo baneo = buscar(id);
        if (!baneo.isActivo()) {
            throw new ConflictoException("El baneo ya fue levantado");
        }

        baneo.levantar(administrador.nombreUsuario());
        selectorEstrategia.para(baneo.getTipoContenido()).cambiarBaneo(baneo.getContenidoId(), false, token);
        log.info("Baneo {} levantado por el usuario {}", id, administrador.id());
        return BaneoResponse.desde(baneo);
    }

    @Transactional(readOnly = true)
    public ResumenModeracionResponse resumir() {
        return new ResumenModeracionResponse(
                reporteService.contarPendientes(),
                baneoRepository.countByActivo(true),
                baneoRepository.countByTipoContenidoAndActivo(TipoContenido.TEMA, true),
                baneoRepository.countByTipoContenidoAndActivo(TipoContenido.COMENTARIO, true));
    }

    private Baneo buscar(Long id) {
        return baneoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el baneo con id " + id));
    }
}
