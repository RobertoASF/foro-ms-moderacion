package cl.duoc.foro.moderacion.exception;

public class AccionNoPermitidaException extends RuntimeException {

    public AccionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
