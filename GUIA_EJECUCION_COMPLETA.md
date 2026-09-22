# Guia de ejecucion completa

Proyecto de Sistemas Distribuidos: `student-service`, `subject-service`, `inscription-service` y `api-gateway`.

Esta guia explica los requisitos, las variables de entorno, las extensiones recomendadas y el orden para compilar, ejecutar y probar los cuatro procesos.

## 1. Arquitectura y puertos

| Proceso | Puerto HTTP | Swagger UI | OpenAPI |
|---|---:|---|---|
| `student-service` | `8081` | `http://localhost:8081/swagger-ui.html` | `http://localhost:8081/api-docs` |
| `subject-service` | `8082` | `http://localhost:8082/swagger-ui.html` | `http://localhost:8082/api-docs` |
| `inscription-service` | `8083` | `http://localhost:8083/swagger-ui.html` | `http://localhost:8083/api-docs` |
| `api-gateway` | `8080` | `http://localhost:8080/swagger-ui.html` | `http://localhost:8080/api-docs` |

Puertos MySQL publicados hacia Windows:

| Base de datos | Puerto en Windows | Puerto dentro de Docker |
|---|---:|---:|
| Estudiantes | `3307` | `3306` |
| Materias | `3308` | `3306` |
| Inscripciones | `3309` | `3306` |

Los servicios tienen bases de datos independientes. El hecho de publicar MySQL en `3307`, `3308` y `3309` no convierte el sistema en monolitico.

## 2. Requisitos de software

### Obligatorios

- JDK 21.
- Maven 3.9 o superior.
- Docker Desktop con el motor Linux activo.
- Docker Compose v2, incluido normalmente en Docker Desktop.
- Navegador web para Swagger.

Comprueba las instalaciones desde PowerShell:

```powershell
java -version
mvn -version
docker --version
docker compose version
```

La version de Java debe ser 21. El proyecto usa Spring Boot 3.3.2 y compila con Java 21.

### Herramientas recomendadas

- Git.
- Postman o Insomnia para probar peticiones HTTP.
- Extension `REST Client` de VS Code, opcional.
- MySQL Workbench o DBeaver, opcional.

## 3. Extensiones recomendadas para VS Code

Instala desde Extensions:

1. `Extension Pack for Java` de Microsoft.
   - Java Language Support.
   - Debugger for Java.
   - Maven for Java.
   - Test Runner for Java.
2. `Spring Boot Extension Pack` de VMware.
   - Spring Boot Tools.
   - Spring Initializr.
   - Spring Boot Dashboard.
3. `Docker` de Microsoft.
4. `REST Client` de Huachao Mao, opcional.

Las extensiones ayudan con autocompletado, ejecucion y depuracion, pero no sustituyen el JDK, Maven ni Docker.

## 4. Configurar Java y Maven en PowerShell

En cada terminal nueva donde se ejecute un servicio Java, ejecuta:

```powershell
$env:JAVA_HOME="C:\Users\nicol\.jdk\jdk-21.0.8"
$env:Path="$env:JAVA_HOME\bin;C:\Users\nicol\.maven\maven-3.9.16\bin;$env:Path"

java -version
mvn -version
```

Si `mvn` no se reconoce, usa la ruta absoluta:

```powershell
& "C:\Users\nicol\.maven\maven-3.9.16\bin\mvn.cmd" test
```

## 5. Variables de entorno

Cada modulo tiene un `.env` y un `.env.example`. El archivo `.env` se usa localmente y no debe compartirse con contrasenas reales.

### 5.1 `student-service/.env`

```dotenv
SERVER_PORT=8081
DB_HOST=localhost
DB_PORT=3307
DB_NAME=students_db
DB_USER=root
DB_PASSWORD=TU_PASSWORD_MYSQL
STUDENT_SERVICE_URL=http://localhost:8081
```

| Variable | Uso |
|---|---|
| `SERVER_PORT` | Puerto HTTP del servicio de estudiantes. |
| `DB_HOST` | Host MySQL cuando se ejecuta Java localmente. |
| `DB_PORT` | Puerto MySQL publicado en Windows. |
| `DB_NAME` | Base de datos de estudiantes. |
| `DB_USER` | Usuario MySQL. |
| `DB_PASSWORD` | Contrasena del usuario MySQL. |
| `STUDENT_SERVICE_URL` | URL del servicio para otros procesos en ejecucion local. |

### 5.2 `subject-service/.env`

```dotenv
SERVER_PORT=8082
DB_HOST=localhost
DB_PORT=3308
DB_NAME=subjects_db
DB_USER=root
DB_PASSWORD=TU_PASSWORD_MYSQL
SUBJECT_SERVICE_URL=http://localhost:8082
```

### 5.3 `inscription-service/.env`

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

`inscription-service` necesita comunicarse con los otros dos servicios para:

- comprobar que existe el estudiante;
- comprobar que existe el curso;
- reservar cupos;
- liberar cupos.

### 5.4 `api-gateway/.env`

```dotenv
SERVER_PORT=8080
STUDENT_SERVICE_URL=http://localhost:8081
SUBJECT_SERVICE_URL=http://localhost:8082
INSCRIPTION_SERVICE_URL=http://localhost:8083
RETRY_MAX_ATTEMPTS=5
RETRY_INTERVAL_SECONDS=5
```

| Variable | Uso |
|---|---|
| `SERVER_PORT` | Puerto unico de entrada del Gateway. |
| `STUDENT_SERVICE_URL` | URL del servicio de estudiantes. |
| `SUBJECT_SERVICE_URL` | URL del servicio de materias. |
| `INSCRIPTION_SERVICE_URL` | URL del servicio de inscripciones. |
| `RETRY_MAX_ATTEMPTS` | Numero maximo de intentos ante errores de red o 5xx. |
| `RETRY_INTERVAL_SECONDS` | Segundos entre reintentos. |

## 6. Crear los archivos `.env`

Si aun no existen, copia cada plantilla:

```powershell
Copy-Item "student-service\.env.example" "student-service\.env"
Copy-Item "subject-service\.env.example" "subject-service\.env"
Copy-Item "inscription-service\.env.example" "inscription-service\.env"
Copy-Item "api-gateway\.env.example" "api-gateway\.env"
```

Despues cambia `DB_PASSWORD=changeme` por la contrasena real de tu MySQL en los tres modulos.

## 7. Modo recomendado: Java local y MySQL en Docker

Este es el modo recomendado para las pruebas actuales. Los `.env` usan `localhost`, por lo que Java se ejecuta en Windows y cada base de datos se ejecuta en su propio contenedor.

### Paso 1: abrir Docker Desktop

Abre Docker Desktop y espera a que el motor Linux este listo.

Comprueba:

```powershell
docker ps
```

Si aparece `dockerDesktopLinuxEngine` no disponible, Docker Desktop aun no esta iniciado.

### Paso 2: levantar las tres bases de datos

Ejecuta en la raiz del proyecto:

```powershell
docker compose -f "student-service\docker-compose.yml" up -d student-db
docker compose -f "subject-service\docker-compose.yml" up -d subject-db
docker compose -f "inscription-service\docker-compose.yml" up -d inscription-db
```

Comprueba:

```powershell
docker ps
```

Revisa la salud de una base si es necesario:

```powershell
docker compose -f "student-service\docker-compose.yml" logs -f student-db
```

### Paso 3: compilar todos los proyectos

Desde la raiz:

```powershell
$env:JAVA_HOME="C:\Users\nicol\.jdk\jdk-21.0.8"
$env:Path="$env:JAVA_HOME\bin;C:\Users\nicol\.maven\maven-3.9.16\bin;$env:Path"

$projects = @("student-service", "subject-service", "inscription-service", "api-gateway")
foreach ($project in $projects) {
    Push-Location $project
    mvn clean package
    if ($LASTEXITCODE -ne 0) { Pop-Location; exit $LASTEXITCODE }
    Pop-Location
}
```

### Paso 4: iniciar `student-service`

Abre una terminal nueva y configura Java/Maven:

```powershell
$env:JAVA_HOME="C:\Users\nicol\.jdk\jdk-21.0.8"
$env:Path="$env:JAVA_HOME\bin;C:\Users\nicol\.maven\maven-3.9.16\bin;$env:Path"
cd "C:\Users\nicol\Downloads\laboratorio2\student-service"
mvn spring-boot:run
```

No cierres esta terminal.

### Paso 5: inicializar estudiantes

Abre:

```text
http://localhost:8081/swagger-ui.html
```

Ejecuta en este orden:

```text
POST /api/initialize/programs
POST /api/initialize/students
```

Consulta los programas:

```text
GET http://localhost:8081/api/programs
```

Guarda el ID del programa `Ingenieria de Sistemas y Computacion`, porque se usara para crear materias.

### Paso 6: iniciar `subject-service`

En otra terminal:

```powershell
$env:JAVA_HOME="C:\Users\nicol\.jdk\jdk-21.0.8"
$env:Path="$env:JAVA_HOME\bin;C:\Users\nicol\.maven\maven-3.9.16\bin;$env:Path"
cd "C:\Users\nicol\Downloads\laboratorio2\subject-service"
mvn spring-boot:run
```

Abre:

```text
http://localhost:8082/swagger-ui.html
```

Inicializa materias:

```text
POST /api/initialize/subjects?systemsProgramId=ID_DEL_PROGRAMA
```

Crea un docente:

```text
POST /api/docents
```

Ejemplo:

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

Crea un curso usando un `subjectId` y el `docentId` creado:

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

### Paso 7: iniciar `inscription-service`

En otra terminal:

```powershell
$env:JAVA_HOME="C:\Users\nicol\.jdk\jdk-21.0.8"
$env:Path="$env:JAVA_HOME\bin;C:\Users\nicol\.maven\maven-3.9.16\bin;$env:Path"
cd "C:\Users\nicol\Downloads\laboratorio2\inscription-service"
mvn spring-boot:run
```

Abre:

```text
http://localhost:8083/swagger-ui.html
```

Crea una inscripcion:

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

Prueba tambien:

```text
GET /api/inscriptions
GET /api/inscriptions/1
GET /api/inscriptions/1/grades
GET /api/inscriptions/1/status-history
```

### Paso 8: iniciar el Gateway

En una cuarta terminal:

```powershell
$env:JAVA_HOME="C:\Users\nicol\.jdk\jdk-21.0.8"
$env:Path="$env:JAVA_HOME\bin;C:\Users\nicol\.maven\maven-3.9.16\bin;$env:Path"
cd "C:\Users\nicol\Downloads\laboratorio2\api-gateway"
mvn spring-boot:run
```

Abre:

```text
http://localhost:8080/swagger-ui.html
```

El Swagger del Gateway documenta principalmente el endpoint propio BFF. Las rutas proxy estan ocultas porque son reenvios transparentes.

## 8. Probar el Gateway

### Estudiantes

```text
GET http://localhost:8080/api/estudiantes/1
```

### Materias

```text
GET http://localhost:8080/api/materias/subjects/1
```

### Cursos

```text
GET http://localhost:8080/api/materias/courses/1
```

### Docentes

```text
GET http://localhost:8080/api/materias/docents/1
```

### Inscripciones

```text
GET http://localhost:8080/api/inscripciones/1
```

### Composicion BFF

```text
GET http://localhost:8080/api/estudiantes/1/detalle
```

El resultado debe reunir estudiante, inscripciones, cursos, docentes y notas.

## 9. Probar fallos

Para demostrar el manejo de fallos del Gateway:

1. Deten `subject-service` con `Ctrl+C`.
2. Ejecuta:

```text
GET http://localhost:8080/api/materias/courses/1
```

3. El Gateway debe seguir activo y responder con un error controlado `503`.
4. Vuelve a levantar `subject-service` con `mvn spring-boot:run`.

Tambien puedes probar una inscripcion con un `studentId` o `cursoId` inexistente.

## 10. Detener el sistema

Detener cada proceso Java:

```text
Ctrl+C
```

Detener las bases de datos sin borrar datos:

```powershell
docker compose -f "student-service\docker-compose.yml" down
docker compose -f "subject-service\docker-compose.yml" down
docker compose -f "inscription-service\docker-compose.yml" down
```

Detener y borrar tambien los datos locales:

```powershell
docker compose -f "student-service\docker-compose.yml" down -v
docker compose -f "subject-service\docker-compose.yml" down -v
docker compose -f "inscription-service\docker-compose.yml" down -v
```

`down -v` elimina los volumenes MySQL y borra los datos inicializados.

## 11. Docker completo: advertencia importante

Los Compose actuales estan preparados principalmente para ejecutar Java localmente y las bases de datos en Docker.

Si se ejecutan tambien las aplicaciones Java dentro de Docker, no basta con usar los `.env` actuales porque `localhost` dentro de un contenedor apunta al mismo contenedor. Ademas, los tres Compose independientes no comparten automaticamente una red comun.

Para un despliegue Docker completo se necesita:

- una red Docker compartida;
- URLs internas como `http://student-service:8081` y `http://subject-service:8082`;
- `DB_HOST=student-db`, `subject-db` o `inscription-db`;
- `DB_PORT=3306` dentro de los contenedores;
- una composicion raiz o una red externa comun.

Para la demostracion local, usa el modo recomendado de esta guia.

## 12. Errores frecuentes

### `mvn` no se reconoce

Configura Java y Maven en la terminal actual:

```powershell
$env:JAVA_HOME="C:\Users\nicol\.jdk\jdk-21.0.8"
$env:Path="$env:JAVA_HOME\bin;C:\Users\nicol\.maven\maven-3.9.16\bin;$env:Path"
```

### `release version 21 not supported`

La terminal esta usando otro Java. Comprueba:

```powershell
java -version
$env:JAVA_HOME
```

Debe ser Java 21.

### `Access denied for user root`

La contrasena del volumen MySQL no coincide con `.env`. Si no necesitas conservar datos:

```powershell
cd "student-service"
docker compose down -v
docker compose up -d student-db
```

Repite en los otros servicios si es necesario.

### `dockerDesktopLinuxEngine` no disponible

Inicia Docker Desktop y espera a que el motor Linux este listo.

### Puerto ocupado

```powershell
Test-NetConnection localhost -Port 8080
Test-NetConnection localhost -Port 8081
Test-NetConnection localhost -Port 8082
Test-NetConnection localhost -Port 8083
Test-NetConnection localhost -Port 3307
Test-NetConnection localhost -Port 3308
Test-NetConnection localhost -Port 3309
```

## 13. Orden resumido

```text
1. Instalar JDK 21, Maven y Docker Desktop.
2. Crear y revisar los cuatro archivos .env.
3. Iniciar Docker Desktop.
4. Levantar student-db, subject-db e inscription-db.
5. Compilar los cuatro proyectos.
6. Iniciar student-service en 8081.
7. Crear programas y estudiantes.
8. Iniciar subject-service en 8082.
9. Crear materias, docentes y cursos.
10. Iniciar inscription-service en 8083.
11. Crear y consultar inscripciones.
12. Iniciar api-gateway en 8080.
13. Probar los tres prefijos desde el Gateway.
14. Probar /api/estudiantes/{id}/detalle.
15. Detener un servicio y demostrar la respuesta controlada del Gateway.
```
