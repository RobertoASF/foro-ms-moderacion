-- Datos iniciales de moderación.
-- Recibe los id de ms-publicaciones que se usan en los reportes y baneos:
--   &1 tema con publicidad (baneado)
--   &2 comentario ofensivo (baneado)
--   &3 tema baneado por error y luego liberado
--   &4 tema con un reporte pendiente
-- Usuarios de ms-usuarios: 1 admin, 2 moderador, 3 camila, 4 diego.

SET VERIFY OFF

INSERT INTO normas (titulo, descripcion) VALUES ('Respeto entre jugadores', 'No se permiten insultos, burlas ni ataques personales.');
INSERT INTO normas (titulo, descripcion) VALUES ('Sin spam ni publicidad', 'No se permite publicidad, enlaces de venta ni mensajes repetidos.');
INSERT INTO normas (titulo, descripcion) VALUES ('Publicar en la categoría correcta', 'Cada tema debe ir en la categoría del juego del que habla.');
INSERT INTO normas (titulo, descripcion) VALUES ('Sin cartas falsificadas', 'No se permite ofrecer ni promover cartas falsas o réplicas no autorizadas.');
INSERT INTO normas (titulo, descripcion) VALUES ('Sin datos personales', 'No compartas teléfonos, direcciones ni datos de otras personas.');

INSERT INTO reportes (tipo_contenido, contenido_id, norma_id, detalle, reportado_por_id, reportado_por_nombre, estado, revisado_por_nombre, fecha_creacion, fecha_revision)
VALUES ('TEMA', &1, (SELECT id FROM normas WHERE titulo = 'Sin spam ni publicidad'), 'Ofrece seguidores pagados, no tiene nada que ver con cartas.',
        3, 'camila', 'ACEPTADO', 'moderador', TIMESTAMP '2026-10-26 09:10:00', TIMESTAMP '2026-10-26 10:00:00');
INSERT INTO reportes (tipo_contenido, contenido_id, norma_id, detalle, reportado_por_id, reportado_por_nombre, estado, revisado_por_nombre, fecha_creacion, fecha_revision)
VALUES ('COMENTARIO', &2, (SELECT id FROM normas WHERE titulo = 'Respeto entre jugadores'), 'Insulta a los demás jugadores del tema.',
        3, 'camila', 'ACEPTADO', 'admin', TIMESTAMP '2026-10-27 18:30:00', TIMESTAMP '2026-10-27 19:05:00');
INSERT INTO reportes (tipo_contenido, contenido_id, norma_id, detalle, reportado_por_id, reportado_por_nombre, estado, revisado_por_nombre, fecha_creacion, fecha_revision)
VALUES ('TEMA', &3, (SELECT id FROM normas WHERE titulo = 'Publicar en la categoría correcta'), 'Creo que este tema debería ir en otra categoría.',
        4, 'diego', 'ACEPTADO', 'moderador', TIMESTAMP '2026-10-28 11:00:00', TIMESTAMP '2026-10-28 12:15:00');
INSERT INTO reportes (tipo_contenido, contenido_id, norma_id, detalle, reportado_por_id, reportado_por_nombre, fecha_creacion)
VALUES ('TEMA', &4, (SELECT id FROM normas WHERE titulo = 'Sin spam ni publicidad'), 'Nombra varias tiendas, parece publicidad.',
        3, 'camila', TIMESTAMP '2026-11-01 16:40:00');

INSERT INTO baneos (tipo_contenido, contenido_id, norma_id, motivo, moderador_id, moderador_nombre, activo, fecha_baneo)
VALUES ('TEMA', &1, (SELECT id FROM normas WHERE titulo = 'Sin spam ni publicidad'), 'Publicidad de servicios que no tienen relación con el foro.',
        2, 'moderador', 1, TIMESTAMP '2026-10-26 10:00:00');
INSERT INTO baneos (tipo_contenido, contenido_id, norma_id, motivo, moderador_id, moderador_nombre, activo, fecha_baneo)
VALUES ('COMENTARIO', &2, (SELECT id FROM normas WHERE titulo = 'Respeto entre jugadores'), 'Insultos directos a otros jugadores.',
        1, 'admin', 1, TIMESTAMP '2026-10-27 19:05:00');
INSERT INTO baneos (tipo_contenido, contenido_id, norma_id, motivo, moderador_id, moderador_nombre, activo, fecha_baneo, levantado_por_nombre, fecha_levantamiento)
VALUES ('TEMA', &3, (SELECT id FROM normas WHERE titulo = 'Publicar en la categoría correcta'), 'El tema parecía estar fuera de su categoría.',
        2, 'moderador', 0, TIMESTAMP '2026-10-28 12:15:00', 'admin', TIMESTAMP '2026-10-28 15:30:00');

COMMIT;
