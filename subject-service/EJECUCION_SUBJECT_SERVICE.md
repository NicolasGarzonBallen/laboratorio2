# Ejecucion independiente de subject-service

## Proposito

`subject-service` administra el catálogo de materias, docentes y cursos. Ejecuta su API en `8082` y usa la base `subjects_db`.

## Dependencias

- JDK 21.
- Maven 3.9 o superior.
- Docker Desktop con el motor Linux activo.
- MySQL ejecutándose en Docker o una instancia compatible.
- Navegador para Swagger.

## Variables de entorno

Crea `.env` desde `.env.example`:

```dotenv
SERVER_PORT=8082
DB_HOST=localhost
DB_PORT=3308
DB_NAME=subjects_db
DB_USER=root
DB_PASSWORD=TU_PASSWORD_MYSQL
SUBJECT_SERVICE_URL=http://localhost:8082
```

El puerto `3308` es el puerto publicado hacia Windows. Dentro de Docker, el servicio usa `subject-db:3306`.

## Iniciar la base de datos

Desde esta carpeta:

```powershell
docker compose up -d subject-db
docker compose ps
```

La base queda disponible desde Windows en `localhost:3308`.

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
java -jar target\subject-service-0.0.1-SNAPSHOT.jar
```

## Swagger

- Swagger: `http://localhost:8082/swagger-ui.html`
- OpenAPI: `http://localhost:8082/api-docs`

## Inicialización y pruebas

Inicializa las 50 materias:

```text
POST /api/initialize/subjects?systemsProgramId=ID_DEL_PROGRAMA
```

El `systemsProgramId` se obtiene desde `student-service`, consultando `GET /api/programs`.

Crea un docente:

```text
POST /api/docents
```

```json
{
  "name": "Profesor de Sistemas",
  "certifications": "Ingeniero de Sistemas",
  "typeContract": "PLANTA",
  "salary": 5000000,
  "academy": "Ingenieria de Sistemas",
  "email": "profesor@uptc.edu.co"
}
```

Crea un curso:

```text
POST /api/courses
```

```json
{
  "name": "Sistemas Distribuidos",
  "period": "2026-2",
  "schedule": "Lunes-Miercoles 8:00-10:00",
  "capacity": 30,
  "subjectId": 1,
  "docentId": 1
}
```

Prueba cupos:

```text
POST /api/courses/{id}/enroll
POST /api/courses/{id}/release
```

Rutas principales:

```text
GET/POST/PUT/PATCH/DELETE /api/subjects
GET/POST/PUT/DELETE         /api/courses
GET/POST/PUT/DELETE         /api/docents
```

Los filtros de cursos aceptan conjuntamente `period`, `subjectId` y `docentId`.

## Detener y borrar datos

```powershell
docker compose down
```

Para borrar también la base:

```powershell
docker compose down -v
```

Detén Java con `Ctrl+C`.
