# Ejecucion independiente de inscription-service

## Proposito

`inscription-service` administra inscripciones, notas e historial de estados. Ejecuta su API en `8083` y usa la base `inscriptions_db`.

Para crear inscripciones necesita que estén disponibles `student-service` y `subject-service`.

## Dependencias

- JDK 21.
- Maven 3.9 o superior.
- Docker Desktop con el motor Linux activo.
- MySQL ejecutándose en Docker.
- `student-service` ejecutándose en `8081`.
- `subject-service` ejecutándose en `8082`.
- Navegador para Swagger.

## Variables de entorno

Crea `.env` desde `.env.example`:

```dotenv
SERVER_PORT=8083
DB_HOST=localhost
DB_PORT=3309
DB_NAME=inscriptions_db
DB_USER=root
DB_PASSWORD=TU_PASSWORD_MYSQL
INSCRIPTION_SERVICE_URL=http://localhost:8083
STUDENT_SERVICE_URL=http://localhost:8081
SUBJECT_SERVICE_URL=http://localhost:8082
```

- `DB_PORT=3309` es el puerto publicado hacia Windows.
- Dentro de Docker, MySQL usa `inscription-db:3306`.
- `STUDENT_SERVICE_URL` y `SUBJECT_SERVICE_URL` permiten validar estudiantes, cursos y cupos.

## Iniciar la base de datos

Desde esta carpeta:

```powershell
docker compose up -d inscription-db
docker compose ps
```

La base queda disponible desde Windows en `localhost:3309`.

## Configurar Java y Maven

```powershell
$env:JAVA_HOME="C:\Users\nicol\.jdk\jdk-21.0.8"
$env:Path="$env:JAVA_HOME\bin;C:\Users\nicol\.maven\maven-3.9.16\bin;$env:Path"
java -version
mvn -version
```

## Compilar y ejecutar

```powershell
mvn clean package
mvn spring-boot:run
```

Alternativamente:

```powershell
java -jar target\inscription-service-0.0.1-SNAPSHOT.jar
```

## Swagger

- Swagger: `http://localhost:8083/swagger-ui.html`
- OpenAPI: `http://localhost:8083/api-docs`

## Orden de prueba

Antes de crear una inscripción:

1. Inicia `student-service`.
2. Inicializa programas y estudiantes.
3. Inicia `subject-service`.
4. Crea un docente y un curso.
5. Inicia `inscription-service`.

Crea una inscripción:

```text
POST /api/inscriptions
```

```json
{
  "studentId": 1,
  "cursoId": 1,
  "period": "2026-2",
  "status": "activa"
}
```

Consultas:

```text
GET /api/inscriptions
GET /api/inscriptions/{id}
GET /api/inscriptions/{id}/grades
GET /api/inscriptions/{id}/status-history
```

CRUD de inscripción:

```text
POST                         /api/inscriptions
GET                          /api/inscriptions/{id}
PUT/PATCH                    /api/inscriptions/{id}
PATCH                        /api/inscriptions/{id}/status
DELETE                       /api/inscriptions/{id}
```

Estados permitidos:

```text
activa
completada
cancelada
```

Al cancelar se libera el cupo del curso. Al reactivar se vuelve a reservar. Una inscripción cancelada no libera el cupo nuevamente al eliminarse.

## Detener y borrar datos

```powershell
docker compose down
```

Para borrar también la base:

```powershell
docker compose down -v
```

Detén Java con `Ctrl+C`.

## Errores frecuentes

- `No se pudo contactar a student-service`: verifica que `8081` esté activo.
- `No se pudo contactar a subject-service`: verifica que `8082` esté activo.
- `Curso ya no tiene cupos`: el curso llegó a su capacidad.
- `Access denied for user root`: la contraseña no coincide con el volumen MySQL.
