package cl.duoc.foro.moderacion.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import cl.duoc.foro.moderacion.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class PublicacionesClientTest {

    private MockRestServiceServer servidor;
    private PublicacionesClient cliente;

    @BeforeEach
    void preparar() {
        RestClient.Builder builder = RestClient.builder();
        servidor = MockRestServiceServer.bindTo(builder).build();
        cliente = new PublicacionesClient(builder, "http://publicaciones");
    }

    @Test
    void banearEnviaElTokenDelModerador() {
        servidor.expect(requestTo("http://publicaciones/api/temas/7/baneo"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(header("Authorization", "Bearer token-moderador"))
                .andExpect(content().json("{\"baneado\": true}"))
                .andRespond(withSuccess());

        cliente.cambiarBaneo("/api/temas/{id}/baneo", 7L, true, "token-moderador", "el tema");

        servidor.verify();
    }

    @Test
    void un404DePublicacionesSeTraduceAContenidoInexistente() {
        servidor.expect(requestTo("http://publicaciones/api/comentarios/99")).andRespond(withResourceNotFound());

        assertThatThrownBy(() -> cliente.verificarExiste("/api/comentarios/{id}", 99L, "el comentario"))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No existe el comentario con id 99");
    }
}
