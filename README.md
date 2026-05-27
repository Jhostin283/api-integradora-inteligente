# API Integradora Inteligente

API REST desarrollada con Spring Boot que consume la API externa **API Ninjas Exercises API** para recomendar ejercicios, clasificar intensidad y sugerir actividades segun objetivos fisicos.

## Tecnologias

- Java 17
- Spring Boot 3
- Maven
- Spring Web
- RestClient
- Validation
- Lombok
- Swagger/OpenAPI

## API externa usada

```txt
https://api.api-ninjas.com/v1/exercises
```

La API externa devuelve datos como:

- `name`
- `type`
- `muscle`
- `equipment`
- `equipments`
- `difficulty`
- `instructions`

## Configuracion

Archivo:

```txt
src/main/resources/application.properties
```

Configuracion principal:

```properties
server.port=8080

api.externa.base-url=https://api.api-ninjas.com/v1/exercises
api.externa.key=tu_api_key_aqui
```

## Instalacion y ejecucion

Compilar:

```bash
mvn clean install
```

Ejecutar:

```bash
mvn spring-boot:run
```

## Swagger

Cuando la aplicacion este ejecutandose, abrir:

```txt
http://localhost:8080/swagger-ui/index.html
```

## Endpoints

### 1. Recomendacion por musculo

```http
GET /api/fitness/recomendacion?musculo=chest
```

Respuesta ejemplo:

```json
{
  "musculo": "Pecho",
  "musculoTrabajado": "Pecho",
  "cantidad": 3,
  "mensaje": "Se encontraron ejercicios recomendados para trabajar pecho.",
  "ejercicios": [
    {
      "ejercicio": "Press de banca con mancuernas",
      "dificultad": "Intermedio",
      "tipo": "Fuerza",
      "equipo": "Mancuernas",
      "instruccionesResumen": "Ejercicio de fuerza enfocado en pecho. Requiere mancuernas y tiene nivel intermedio. Ejecutalo con control, buena postura y respiracion constante.",
      "recomendacion": "Recomendado para personas con experiencia moderada."
    },
    {
      "ejercicio": "Flexiones de pecho",
      "dificultad": "Principiante",
      "tipo": "Fuerza",
      "equipo": "Peso corporal",
      "instruccionesResumen": "Ejercicio de fuerza enfocado en pecho. Requiere peso corporal y tiene nivel principiante. Ejecutalo con control, buena postura y respiracion constante.",
      "recomendacion": "Ideal para principiantes."
    }
  ]
}
```

### 2. Clasificacion de intensidad

```http
GET /api/fitness/intensidad?musculo=legs
```

Respuesta ejemplo:

```json
{
  "musculo": "Cuadriceps",
  "ejercicio": "Squat",
  "tipo": "Fuerza",
  "equipo": "Peso corporal",
  "dificultad": "Intermedio",
  "nivelIntensidad": "Media",
  "mensaje": "Requiere experiencia moderada.",
  "recomendacionSeguridad": "Haz calentamiento previo y controla la carga o velocidad."
}
```

### 3. Recomendacion segun objetivo fisico

```http
GET /api/fitness/objetivo?objetivo=fuerza
```

Respuesta ejemplo:

```json
{
  "objetivo": "fuerza",
  "ejercicio": "Press de banca con mancuernas",
  "tipo": "Fuerza",
  "musculoTrabajado": "Pecho",
  "equipo": "Mancuernas",
  "dificultad": "Intermedio",
  "categoria": "Entrenamiento funcional",
  "instruccionesResumen": "Ejercicio de fuerza enfocado en pecho. Requiere mancuernas y tiene nivel intermedio. Ejecutalo con control, buena postura y respiracion constante.",
  "recomendacion": "Ideal para ganar fuerza muscular."
}
```

## Manejo de errores

Respuesta estandar:

```json
{
  "error": "Musculo no encontrado",
  "status": 404,
  "timestamp": "2026-05-27T10:00:00"
}
```

Errores contemplados:

| Caso | Codigo |
|---|---|
| Parametro vacio | 400 |
| API Key invalida | 401 |
| Recurso inexistente | 404 |
| Error interno | 500 |

## Coleccion Postman

Importar el archivo:

```txt
api-integradora-inteligente.postman_collection.json
```

La coleccion incluye consultas para los tres casos de uso y pruebas de error.
