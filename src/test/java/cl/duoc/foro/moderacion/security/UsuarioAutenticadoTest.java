package cl.duoc.foro.moderacion.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class UsuarioAutenticadoTest {

    @Test
    void leeLosDatosDelToken() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject("camila")
                .claim("uid", 3)
                .claim("rol", "USUARIO")
                .build();

        UsuarioAutenticado usuario = UsuarioAutenticado.desde(jwt);

        assertThat(usuario.id()).isEqualTo(3L);
        assertThat(usuario.nombreUsuario()).isEqualTo("camila");
        assertThat(usuario.esAutor(3L)).isTrue();
    }

    @Test
    void reconoceLosRolesPermitidos() {
        UsuarioAutenticado moderador = new UsuarioAutenticado(2L, "moderador", "MODERADOR");

        assertThat(moderador.tieneRol("ADMIN", "MODERADOR")).isTrue();
        assertThat(moderador.tieneRol("ADMIN")).isFalse();
    }
}
