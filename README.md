# ms-moderacion

Microservicio de moderación del foro de TCG. Guarda las normas del foro, los reportes que hacen los usuarios y los baneos que aplican moderadores y administradores. Cuando se banea un tema o un comentario, le pide a `ms-publicaciones` que lo oculte.

Creado con el arquetipo `foro-microservicio-archetype` (repositorio `foro-arquetipo`). Hecho con Java 21, Spring Boot 4.1, Spring Data JPA, Spring Security y Oracle.

## Endpoints

### Normas

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/normas` | Público | Lista las normas del foro |
| GET | `/api/normas/{id}` | Público | Muestra una norma |
| POST | `/api/normas` | ADMIN | Crea una norma |
| PUT | `/api/normas/{id}` | ADMIN | Actualiza una norma |
| DELETE | `/api/normas/{id}` | ADMIN | Elimina una norma que no se ha usado |

### Reportes

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/reportes` | Con sesión | Reporta un tema o comentario |
| GET | `/api/reportes/mios` | Con sesión | Reportes hechos por quien consulta |
| GET | `/api/reportes?estado=` | MODERADOR o ADMIN | Lista reportes, opcionalmente por estado |
| PUT | `/api/reportes/{id}/descartar` | MODERADOR o ADMIN | Descarta un reporte pendiente |

### Baneos

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/baneos?activo=` | MODERADOR o ADMIN | Lista baneos, activos o levantados |
| GET | `/api/baneos/resumen` | MODERADOR o ADMIN | Reportes pendientes y baneos activos por tipo |
| GET | `/api/baneos/{id}` | MODERADOR o ADMIN | Muestra un baneo |
| POST | `/api/baneos` | MODERADOR o ADMIN | Banea un tema o comentario |
| PUT | `/api/baneos/{id}/levantar` | ADMIN | Levanta un baneo y el contenido vuelve al foro |

Ejemplo para banear un tema:

```json
{
  "tipoContenido": "TEMA",
  "contenidoId": 7,
  "normaId": 2,
  "motivo": "Publicidad que no tiene relación con el foro"
}
```

Al banear, todos los reportes pendientes de ese contenido quedan aceptados.

## Patrón Strategy

Cada tipo de contenido tiene su propia estrategia para revisar que existe y para banearse en `ms-publicaciones`:

```
EstrategiaModeracion (interfaz)
├── ModeracionTema         /api/temas/{id} y /api/temas/{id}/baneo
└── ModeracionComentario   /api/comentarios/{id} y /api/comentarios/{id}/baneo
```

`SelectorEstrategia` recibe todas las estrategias y entrega la que corresponde al tipo. Así `ReporteService` y `BaneoService` no saben qué ruta llamar, y para moderar un tipo nuevo basta con agregar otra clase.

## Comunicación con ms-publicaciones

`PublicacionesClient` usa `RestClient` y reenvía el token del moderador. Así `ms-publicaciones` vuelve a revisar el rol antes de ocultar el contenido. Si ese servicio responde 404, se devuelve 404; si no responde, se devuelve 503.

El baneo se guarda dentro de una transacción y la llamada a `ms-publicaciones` va al final: si falla, la transacción se revierte y no queda un baneo a medias.

## Estructura

```
src/main/java/cl/duoc/foro/moderacion
├── client       Cliente REST hacia ms-publicaciones
├── config       Seguridad y validación del token
├── controller   Endpoints REST
├── dto          Datos de entrada y salida, con sus validaciones
├── exception    Errores propios y manejo global
├── model        Entidades JPA y enums
├── repository   Acceso a Oracle
├── security     Datos del usuario autenticado y respuestas 401 y 403
├── service      Reglas de negocio
└── strategy     Estrategias de moderación por tipo de contenido
```

## Base de datos

Los scripts están en la carpeta `db` y se ejecutan conectado como `FORO_MODERACION`:

1. `01_tablas.sql` crea `normas`, `reportes` y `baneos`.
2. `02_datos.sql` carga 5 normas, 4 reportes y 3 baneos. Recibe los id de los temas y comentarios de `ms-publicaciones`, porque cada esquema genera sus propios id. El script `foro-infra/db/comun.sh` los busca y se los pasa.

## Configuración

Está en `src/main/resources/application.properties`. Las claves se entregan con variables de entorno:

| Variable | Descripción |
|---|---|
| `DB_PASSWORD` | Clave del esquema `FORO_MODERACION` |
| `JWT_SECRETO` | El mismo secreto que usa `ms-usuarios` |
| `PUBLICACIONES_URL` | Dirección de `ms-publicaciones`, por defecto `http://localhost:8082` |
| `CORS_ORIGEN` | Origen web permitido |
| `LOG_NIVEL` | Nivel de log del proyecto, por defecto `INFO` |

## Cómo ejecutarlo

El servicio se levanta junto a los demás desde el repositorio `foro-infra`.

Las pruebas no necesitan base de datos:

```bash
mvn test
```
