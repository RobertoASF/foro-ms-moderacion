package cl.duoc.foro.moderacion.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.foro.moderacion.exception.ManejadorErrores;
import cl.duoc.foro.moderacion.security.RespuestasSeguridad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

// Sin filtros por defecto no se cargan los controladores: solo se prueba la seguridad
@WebMvcTest(useDefaultFilters = false,
        properties = "jwt.secreto=clave-solo-para-pruebas-de-mas-de-32-caracteres")
@Import({SeguridadConfig.class, RespuestasSeguridad.class, ManejadorErrores.class})
class SeguridadConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sinTokenResponde401EnJson() throws Exception {
        mockMvc.perform(get("/api/cualquier-ruta"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.estado").value(401));
    }

    @Test
    void conTokenUnaRutaInexistenteResponde404EnJson() throws Exception {
        mockMvc.perform(get("/api/no-existe")
                        .with(jwt().jwt(datos -> datos.subject("camila").claim("uid", 3L).claim("rol", "USUARIO"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El recurso solicitado no existe"));
    }
}
