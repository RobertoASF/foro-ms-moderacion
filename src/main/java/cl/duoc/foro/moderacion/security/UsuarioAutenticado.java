package cl.duoc.foro.moderacion.security;

import java.util.Arrays;
import org.springframework.security.oauth2.jwt.Jwt;

public record UsuarioAutenticado(Long id, String nombreUsuario, String rol) {

    public static UsuarioAutenticado desde(Jwt jwt) {
        Number id = jwt.getClaim("uid");
        return new UsuarioAutenticado(id.longValue(), jwt.getSubject(), jwt.getClaimAsString("rol"));
    }

    public boolean esAutor(Long autorId) {
        return id.equals(autorId);
    }

    public boolean tieneRol(String... roles) {
        return Arrays.asList(roles).contains(rol);
    }
}
