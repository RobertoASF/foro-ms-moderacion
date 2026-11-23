package cl.duoc.foro.moderacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import org.hibernate.type.NumericBooleanConverter;

@Entity
@Table(name = "baneos")
@Getter
@Setter
@NoArgsConstructor
public class Baneo {

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

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(nullable = false)
    private Long moderadorId;

    @Column(nullable = false, length = 30)
    private String moderadorNombre;

    // La columna es NUMBER(1) para que funcione igual en cualquier version de Oracle
    @Convert(converter = NumericBooleanConverter.class)
    @Column(nullable = false)
    private boolean activo = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaBaneo;

    @Column(length = 30)
    private String levantadoPorNombre;

    private LocalDateTime fechaLevantamiento;

    public void levantar(String administrador) {
        activo = false;
        levantadoPorNombre = administrador;
        fechaLevantamiento = LocalDateTime.now();
    }
}
