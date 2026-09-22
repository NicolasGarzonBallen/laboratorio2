# Ejecucion independiente de student-service

## Proposito

`student-service` administra estudiantes, programas, contactos y documentos. Ejecuta su API en `8081` y usa la base `students_db`.

## Dependencias

- JDK 21.
- Maven 3.9 o superior.
- Docker Desktop con el motor Linux activo.
- MySQL ejecutandose en Docker o una instancia MySQL compatible.
- Navegador para Swagger.

## Variables de entorno

Crea `.env` desde `.env.example`:

```dotenv
SERVER_PORT=8081
DB_HOST=localhost
DB_PORT=3307
DB_NAME=students_db
DB_USER=root
DB_PASSWORD=TU_PASSWORD_MYSQL
STUDENT_SERVICE_URL=http://localhost:8081
```

- `SERVER_PORT`: puerto HTTP del servicio.
- `DB_HOST`: `localhost` cuando Java corre en Windows.
- `DB_PORT`: puerto MySQL publicado hacia Windows.
- `DB_NAME`, `DB_USER`, `DB_PASSWORD`: credenciales de la base.
- `STUDENT_SERVICE_URL`: URL del servicio para clientes locales.

Dentro del Compose, el servicio Java usa `student-db:3306`; `3307` solo es el puerto publicado para el host.

## Iniciar la base de datos

Desde esta carpeta:

```powershell
docker compose up -d student-db
docker compose ps
```

La base queda disponible desde Windows en `localhost:3307`.

## Configurar Java y Maven

```powershell
$env:JAVA_HOME="C:\Users\nicol\.jdk\jdk-21.0.8"
$env:Path="$env:JAVA_HOME\bin;C:\Users\nicol\.maven\maven-3.9.16\bin;$env:Path"
java -version
mvn -version
```

## Compilar

```powershell
mvn clean package
```

## Ejecutar

```powershell
mvn spring-boot:run
```

O con el jar:

```powershell
java -jar target\student-service-0.0.1-SNAPSHOT.jar
```

## Swagger y datos iniciales

- Swagger: `http://localhost:8081/swagger-ui.html`
- OpenAPI: `http://localhost:8081/api-docs`

Ejecuta en este orden:

```text
POST /api/initialize/programs
POST /api/initialize/students
```

La segunda operación completa la base hasta alcanzar 1000 estudiantes. Si ya existen algunos, no los duplica.

Comprueba el resultado:

```text
GET /api/students?pageNumber=0&pageSize=20
```

La respuesta debe incluir `totalElements: 1000`.

Rutas principales:

```text
GET/POST      /api/students
GET/PUT/PATCH/DELETE /api/students/{id}
GET/POST      /api/programs
GET/PUT/DELETE /api/programs/{id}
GET/POST/PUT/DELETE /api/students/{studentId}/contact
GET/POST       /api/students/{studentId}/documents
PUT/DELETE     /api/documents/{documentId}
```

## Detener y borrar datos

```powershell
docker compose down
```

Para borrar también los datos MySQL:

```powershell
docker compose down -v
```

Detén Java con `Ctrl+C`.

## Errores frecuentes

- `Access denied for user root`: la contraseña no coincide con la usada al crear el volumen.
- `Connection refused`: MySQL no está iniciado o el puerto no es `3307`.
- `totalElements` menor que 1000: vuelve a ejecutar `POST /api/initialize/students`; ahora completa los faltantes.
