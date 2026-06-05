# Gestión de Películas - Documentación y Suite de Pruebas Unitarias

Este proyecto es una aplicación REST desarrollada en **Spring Boot** para administrar un catálogo de películas y sus comentarios con persistencia en memoria. A continuación, se detalla la arquitectura del proyecto, la especificación de la API y la cobertura de pruebas unitarias implementada para cumplir con los estándares de calidad de software y cobertura de código (>80%).

---

## 1. Arquitectura del Proyecto

La aplicación sigue el patrón clásico de arquitectura en capas:

1. **Capa de Controladores (`controllers`)**:
   - Expone los endpoints REST para interactuar con la aplicación.
   - Maneja la validación de entrada (`@Valid`) y mapea las respuestas HTTP.
   
2. **Capa de Servicios (`services`)**:
   - Implementa la lógica de negocio.
   - Genera tokens de descarga alfanuméricos seguros usando Apache Commons Lang3 y realiza la filtración de películas.

3. **Capa de Repositorios (`repositories`)**:
   - Gestiona el acceso a datos en memoria (`ArrayList`).
   - Controla las restricciones de unicidad de ID y valores nulos.

4. **Entidades (`entities`)**:
   - `Pelicula`: Modelo principal con anotaciones de validación de Jakarta y Lombok para los métodos de acceso.
   - `Comentario`: Modelo embebido que representa las reseñas de los usuarios en una película.

---

## 2. API Endpoints

Basado en `openapi.yaml` y la implementación del controlador:

| Endpoint | Método | Descripción | Códigos HTTP |
|---|---|---|---|
| `/peliculas` | `GET` | Obtener películas. Permite búsquedas parciales usando el query parameter `q`. | `200 OK`, `500 Error` |
| `/peliculas` | `POST` | Registrar una película. Genera un token automático y valida campos requeridos. | `200 OK`, `400 Bad Request`, `500 Error` |
| `/peliculas/{id}` | `GET` | Obtener película por su identificador único. | `200 OK`, `404 Not Found`, `500 Error` |
| `/peliculas/{id}/comentarios` | `GET` | Obtener la lista de comentarios asociados a una película. | `200 OK`, `404 Not Found`, `500 Error` |

---

## 3. Suite de Pruebas Unitarias

La suite de pruebas fue diseñada de forma rigurosa y limpia. Está distribuida en las siguientes clases:

### 3.1. Pruebas de Repositorio (`PeliculasRepositoryImplTest`)
- **Ubicación:** `src/test/java/cl/usm/gestionPeliculasMemoria/repositories/PeliculasRepositoryImplTest.java`
- **Enfoque:** Evalúa las mutaciones del almacenamiento real en memoria sin simulación externa.
- **Escenarios cubiertos:**
  - Inserción exitosa de una película y validación de campos.
  - Lanzamiento de `IllegalArgumentException` si el ID de la película es nulo o está duplicado (case-insensitive).
  - Listado de registros vacíos y con datos.
  - Búsqueda por ID existente, case-insensibilidad del ID, y comportamiento ante IDs inexistentes o nulos.

### 3.2. Pruebas de Servicio (`PeliculasServiceImplTest`)
- **Ubicación:** `src/test/java/cl/usm/gestionPeliculasMemoria/services/PeliculasServiceImplTest.java`
- **Enfoque:** Aísla la capa de negocio simulando el repositorio mediante `@Mock` y `@InjectMocks`.
- **Escenarios cubiertos:**
  - Registro de película con generación de token alfanumérico seguro (longitud de 10 caracteres) y verificación de llamada.
  - Manejo de excepciones en inserciones que retornan nulo.
  - Búsqueda y listado general delegando llamadas al repositorio de forma exacta (`verify`).
  - Filtrado parcial por título e ID (incluyendo casos de error con valores nulos que disparan `NullPointerException`).

### 3.3. Pruebas de Controlador (`PeliculasControllerTest`)
- **Ubicación:** `src/test/java/cl/usm/gestionPeliculasMemoria/controllers/PeliculasControllerTest.java`
- **Enfoque:** Configuración clásica usando `@WebMvcTest` y `MockMvc` para validar la capa de presentación REST.
- **Escenarios cubiertos:**
  - Consulta general sin filtro y consulta filtrada por parámetro `q`.
  - Creación con payload válido e inválido (provocando `400 Bad Request` debido a la validación `@Valid`).
  - Respuestas HTTP apropiadas (`200 OK`, `400 Bad Request`, `404 Not Found` y `500 Internal Server Error`).
  - Extracción y aserción de campos del JSON de respuesta usando `jsonPath` y coincidencia de tipos de contenido.

---

## 4. Ejecución de las Pruebas

Para ejecutar las pruebas unitarias y verificar el porcentaje de cobertura del código, ejecuta el siguiente comando en la raíz del proyecto:

```bash
mvn test
```
