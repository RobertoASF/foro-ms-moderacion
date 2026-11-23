-- Tablas del microservicio ms-moderacion.
-- Se ejecuta conectado como FORO_MODERACION.
-- contenido_id apunta a un tema o comentario de ms-publicaciones. No lleva clave foránea
-- porque cada microservicio es dueño de sus propios datos.

CREATE TABLE normas (
    id          NUMBER(19)         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo      VARCHAR2(100 CHAR) NOT NULL UNIQUE,
    descripcion VARCHAR2(500 CHAR) NOT NULL
);

CREATE TABLE reportes (
    id                   NUMBER(19)         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_contenido       VARCHAR2(20 CHAR)  NOT NULL CHECK (tipo_contenido IN ('TEMA', 'COMENTARIO')),
    contenido_id         NUMBER(19)         NOT NULL,
    norma_id             NUMBER(19)         NOT NULL REFERENCES normas (id),
    detalle              VARCHAR2(500 CHAR),
    reportado_por_id     NUMBER(19)         NOT NULL,
    reportado_por_nombre VARCHAR2(30 CHAR)  NOT NULL,
    estado               VARCHAR2(20 CHAR)  DEFAULT 'PENDIENTE' NOT NULL
                         CHECK (estado IN ('PENDIENTE', 'ACEPTADO', 'DESCARTADO')),
    revisado_por_nombre  VARCHAR2(30 CHAR),
    fecha_creacion       TIMESTAMP          DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_revision       TIMESTAMP
);

CREATE TABLE baneos (
    id                   NUMBER(19)         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_contenido       VARCHAR2(20 CHAR)  NOT NULL CHECK (tipo_contenido IN ('TEMA', 'COMENTARIO')),
    contenido_id         NUMBER(19)         NOT NULL,
    norma_id             NUMBER(19)         NOT NULL REFERENCES normas (id),
    motivo               VARCHAR2(500 CHAR) NOT NULL,
    moderador_id         NUMBER(19)         NOT NULL,
    moderador_nombre     VARCHAR2(30 CHAR)  NOT NULL,
    activo               NUMBER(1)          DEFAULT 1 NOT NULL CHECK (activo IN (0, 1)),
    fecha_baneo          TIMESTAMP          DEFAULT CURRENT_TIMESTAMP NOT NULL,
    levantado_por_nombre VARCHAR2(30 CHAR),
    fecha_levantamiento  TIMESTAMP
);

CREATE INDEX idx_reportes_estado ON reportes (estado);
CREATE INDEX idx_reportes_contenido ON reportes (tipo_contenido, contenido_id);
CREATE INDEX idx_baneos_contenido ON baneos (tipo_contenido, contenido_id);
