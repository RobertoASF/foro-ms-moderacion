package cl.duoc.foro.moderacion.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.foro.moderacion.config.SeguridadConfig;
import cl.duoc.foro.moderacion.security.RespuestasSeguridad;
import cl.duoc.foro.moderacion.service.BaneoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BaneoController.class,
        properties = "jwt.secreto=clave-solo-para-pruebas-de-mas-de-32-caracteres")
@Import({SeguridadConfig.class, RespuestasSeguridad.class})
class BaneoControllerTest {

    private static final String BANEO_VALIDO = """
            {"tipoContenido": "TEMA", "contenidoId": 7, "normaId": 2, "motivo": "Publicidad fuera de lugar"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BaneoService baneoService;

    @Test
    void banearSinTokenResponde401() throws Exception {
        mockMvc.perform(post("/api/baneos").contentType(MediaType.APPLICATION_JSON).content(BANEO_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unUsuarioComunNoPuedeBanear() throws Exception {
        mockMvc.perform(post("/api/baneos").with(token(3L, "camila", "USUARIO"))
                        .contentType(MediaType.APPLICATION_JSON).content(BANEO_VALIDO))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.estado").value(403));
    }

    @Test
    void banearConMotivoCortoIndicaElCampo() throws Exception {
        mockMvc.perform(post("/api/baneos").with(token(2L, "moderador", "MODERADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipoContenido": "TEMA", "contenidoId": 7, "normaId": 2, "motivo": "spam"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.motivo").exists());
    }

    @Test
    void soloElAdminPuedeLevantarUnBaneo() throws Exception {
        mockMvc.perform(put("/api/baneos/1/levantar").with(token(2L, "moderador", "MODERADOR")))
                .andExpect(status().isForbidden());
    }

    private JwtRequestPostProcessor token(Long id, String nombre, String rol) {
        return jwt().jwt(datos -> datos.subject(nombre).claim("uid", id).claim("rol", rol))
                .authorities(new SimpleGrantedAuthority("ROLE_" + rol));
    }
}
