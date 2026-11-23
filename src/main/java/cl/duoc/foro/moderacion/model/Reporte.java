package cl.duoc.foro.moderacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "reportes")
@Getter
@Setter
@NoArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoContenido tipoContenido;

    @Column(nullable = false)
    private Long contenidoId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "norma_id", nullable = false)
    private Norma norma;

    @Column(length = 500)
    private String detalle;

    @Column(nullable = false)
    private Long reportadoPorId;

    @Column(nullable = false, length = 30)
    private String reportadoPorNombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReporte estado = EstadoReporte.PENDIENTE;

    @Column(length = 30)
    private String revisadoPorNombre;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaRevision;

    public boolean estaPendiente() {
        return estado == EstadoReporte.PENDIENTE;
    }

    public void revisar(EstadoReporte resultado, String moderador) {
        estado = resultado;
        revisadoPorNombre = moderador;
        fechaRevision = LocalDateTime.now();
    }
}
