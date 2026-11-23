package cl.duoc.foro.moderacion.client;

import cl.duoc.foro.moderacion.exception.AccionNoPermitidaException;
import cl.duoc.foro.moderacion.exception.RecursoNoEncontradoException;
import cl.duoc.foro.moderacion.exception.ServicioNoDisponibleException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// Único punto del servicio que habla con ms-publicaciones
@Slf4j
@Component
public class PublicacionesClient {

    private final RestClient restClient;

    public PublicacionesClient(RestClient.Builder builder, @Value("${publicaciones.url}") String url) {
        this.restClient = builder.baseUrl(url).build();
    }

    public void verificarExiste(String ruta, Long id, String nombre) {
        llamar(nombre, id, () -> restClient.get().uri(ruta, id).retrieve().toBodilessEntity());
    }

    // Reenvía el token del moderador: ms-publicaciones vuelve a revisar su rol
    public void cambiarBaneo(String ruta, Long id, boolean baneado, String token, String nombre) {
        llamar(nombre, id, () -> restClient.put()
                .uri(ruta, id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("baneado", baneado))
                .retrieve()
                .toBodilessEntity());
    }

    private void llamar(String nombre, Long id, Runnable llamada) {
        try {
            llamada.run();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("No existe " + nombre + " con id " + id);
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden ex) {
            throw new AccionNoPermitidaException("ms-publicaciones rechazó la acción por falta de permisos");
        } catch (RestClientException ex) {
            log.error("No se pudo comunicar con ms-publicaciones: {}", ex.getMessage());
            throw new ServicioNoDisponibleException("El servicio de publicaciones no está disponible");
        }
    }
}
