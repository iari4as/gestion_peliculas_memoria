package cl.usm.gestionPeliculasMemoria.controllers;

import cl.usm.gestionPeliculasMemoria.entities.Comentario;
import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import cl.usm.gestionPeliculasMemoria.services.PeliculasService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PeliculasController.class)
class PeliculasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PeliculasService peliculasService;

    @Test
    void obtenerCatalogoCompletoDePeliculas() throws Exception {
        Pelicula pelicula = new Pelicula("pel-1", "Inception", "Nolan", "TOKEN", new Comentario[0]);
        when(peliculasService.getAll()).thenReturn(Collections.singletonList(pelicula));

        mockMvc.perform(get("/peliculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("pel-1"))
                .andExpect(jsonPath("$[0].titulo").value("Inception"));
    }

    @Test
    void filtrarPeliculasPorParametroBusqueda() throws Exception {
        Pelicula pelicula = new Pelicula("pel-1", "Inception", "Nolan", "TOKEN", new Comentario[0]);
        when(peliculasService.filter("Inception")).thenReturn(Collections.singletonList(pelicula));

        mockMvc.perform(get("/peliculas").param("q", "Inception"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("pel-1"));
    }

    @Test
    void errorInternoAlObtenerCatalogo() throws Exception {
        when(peliculasService.getAll()).thenThrow(new RuntimeException("Error simulado"));

        mockMvc.perform(get("/peliculas"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void registrarNuevaPeliculaExitosamente() throws Exception {
        Pelicula pelicula = new Pelicula("pel-1", "Inception", "Nolan", "TOKEN", new Comentario[0]);
        when(peliculasService.createPelicula(any(Pelicula.class))).thenReturn(pelicula);

        String jsonPayload = """
                {
                    "id": "pel-1",
                    "titulo": "Inception",
                    "director": "Nolan"
                }
                """;

        mockMvc.perform(post("/peliculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pel-1"));
    }

    @Test
    void errorAlCrearPeliculaRetornaNull() throws Exception {
        when(peliculasService.createPelicula(any(Pelicula.class))).thenReturn(null);

        String jsonPayload = """
                {
                    "id": "pel-1",
                    "titulo": "Inception",
                    "director": "Nolan"
                }
                """;

        mockMvc.perform(post("/peliculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void errorDeValidacionAlCrearPeliculaInvalida() throws Exception {
        String jsonPayload = """
                {
                    "id": "",
                    "titulo": "",
                    "director": ""
                }
                """;

        mockMvc.perform(post("/peliculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPeliculaExistentePorSuId() throws Exception {
        Pelicula pelicula = new Pelicula("pel-1", "Inception", "Nolan", "TOKEN", new Comentario[0]);
        when(peliculasService.findById("pel-1")).thenReturn(pelicula);

        mockMvc.perform(get("/peliculas/pel-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pel-1"));
    }

    @Test
    void errorPeliculaNoEncontradaPorId() throws Exception {
        when(peliculasService.findById("inexistente")).thenReturn(null);

        mockMvc.perform(get("/peliculas/inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void errorInternoAlBuscarPorId() throws Exception {
        when(peliculasService.findById("error")).thenThrow(new RuntimeException("Error simulado"));

        mockMvc.perform(get("/peliculas/error"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void obtenerComentariosDeUnaPelicula() throws Exception {
        Comentario comentario = new Comentario("usuario", "buena");
        Pelicula pelicula = new Pelicula("pel-1", "Inception", "Nolan", "TOKEN", new Comentario[]{comentario});
        when(peliculasService.findById("pel-1")).thenReturn(pelicula);

        mockMvc.perform(get("/peliculas/pel-1/comentarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuario").value("usuario"));
    }

    @Test
    void errorPeliculaNoEncontradaAlObtenerComentarios() throws Exception {
        when(peliculasService.findById("inexistente")).thenReturn(null);

        mockMvc.perform(get("/peliculas/inexistente/comentarios"))
                .andExpect(status().isNotFound());
    }

    @Test
    void errorInternoAlObtenerComentarios() throws Exception {
        when(peliculasService.findById("error")).thenThrow(new RuntimeException("Error simulado"));

        mockMvc.perform(get("/peliculas/error/comentarios"))
                .andExpect(status().isInternalServerError());
    }
}
