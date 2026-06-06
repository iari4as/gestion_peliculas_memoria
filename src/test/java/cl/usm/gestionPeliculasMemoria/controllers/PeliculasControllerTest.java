package cl.usm.gestionPeliculasMemoria.controllers;

import cl.usm.gestionPeliculasMemoria.entities.Comentario;
import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import cl.usm.gestionPeliculasMemoria.services.PeliculasService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeliculasControllerTest {

    @Mock
    private PeliculasService peliculasService;

    @InjectMocks
    private PeliculasController peliculasController;

    @Test
    void listarTodasLasPeliculasExitosamente() {
        Pelicula p1 = new Pelicula("pel-1", "Inception", "Nolan", "TOKEN1", new Comentario[0]);
        Pelicula p2 = new Pelicula("pel-2", "Avatar", "Cameron", "TOKEN2", new Comentario[0]);
        List<Pelicula> peliculas = Arrays.asList(p1, p2);

        when(peliculasService.getAll()).thenReturn(peliculas);

        ResponseEntity<List<Pelicula>> respuesta = peliculasController.getAll(null);

        assertNotNull(respuesta);
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(2, respuesta.getBody().size());
        assertEquals("pel-1", respuesta.getBody().get(0).getId());
        assertEquals("Inception", respuesta.getBody().get(0).getTitulo());
        assertEquals("Nolan", respuesta.getBody().get(0).getDirector());
        assertEquals("TOKEN1", respuesta.getBody().get(0).getTokenDescarga());
        
        verify(peliculasService, times(1)).getAll();
        verify(peliculasService, never()).filter(anyString());
    }

    @Test
    void listarPeliculasFiltradasPorConsulta() {
        Pelicula p1 = new Pelicula("pel-1", "Inception", "Nolan", "TOKEN1", new Comentario[0]);
        List<Pelicula> filtradas = Collections.singletonList(p1);

        when(peliculasService.filter("incep")).thenReturn(filtradas);

        ResponseEntity<List<Pelicula>> respuesta = peliculasController.getAll("incep");

        assertNotNull(respuesta);
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1, respuesta.getBody().size());
        assertEquals("pel-1", respuesta.getBody().get(0).getId());
        
        verify(peliculasService, times(1)).filter("incep");
        verify(peliculasService, never()).getAll();
    }

    @Test
    void errorInternoAlFiltrarPeliculas() {
        when(peliculasService.filter("error")).thenThrow(new RuntimeException("Error en servicio"));

        ResponseEntity<List<Pelicula>> respuesta = peliculasController.getAll("error");

        assertNotNull(respuesta);
        assertEquals(500, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
    }

    @Test
    void crearPeliculaExitosamente() {
        Pelicula peliculaInput = new Pelicula("pel-new", "Interstellar", "Nolan", null, new Comentario[0]);
        Pelicula peliculaCreada = new Pelicula("pel-new", "Interstellar", "Nolan", "TOKEN_NEW", new Comentario[0]);

        when(peliculasService.createPelicula(any(Pelicula.class))).thenReturn(peliculaCreada);

        ResponseEntity<?> respuesta = peliculasController.createPelicula(peliculaInput);

        assertNotNull(respuesta);
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody() instanceof Pelicula);
        Pelicula res = (Pelicula) respuesta.getBody();
        assertEquals("pel-new", res.getId());
        assertEquals("Interstellar", res.getTitulo());
        assertEquals("TOKEN_NEW", res.getTokenDescarga());
    }

    @Test
    void errorAlCrearPeliculaRetornaNull() {
        Pelicula peliculaInput = new Pelicula("pel-new", "Interstellar", "Nolan", null, new Comentario[0]);
        when(peliculasService.createPelicula(any(Pelicula.class))).thenReturn(null);

        ResponseEntity<?> respuesta = peliculasController.createPelicula(peliculaInput);

        assertNotNull(respuesta);
        assertEquals(500, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
    }

    @Test
    void buscarPeliculaPorIdExitosamente() {
        Pelicula pelicula = new Pelicula("pel-1", "Inception", "Nolan", "TOKEN1", new Comentario[0]);

        when(peliculasService.findById("pel-1")).thenReturn(pelicula);

        ResponseEntity<Pelicula> respuesta = peliculasController.findById("pel-1");

        assertNotNull(respuesta);
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals("pel-1", respuesta.getBody().getId());
        assertEquals("Inception", respuesta.getBody().getTitulo());
    }

    @Test
    void errorPeliculaNoEncontradaPorId() {
        when(peliculasService.findById("inexistente")).thenReturn(null);

        ResponseEntity<Pelicula> respuesta = peliculasController.findById("inexistente");

        assertNotNull(respuesta);
        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
    }

    @Test
    void errorInternoAlBuscarPorId() {
        when(peliculasService.findById("error")).thenThrow(new RuntimeException("Error de servicio"));

        ResponseEntity<Pelicula> respuesta = peliculasController.findById("error");

        assertNotNull(respuesta);
        assertEquals(500, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
    }

    @Test
    void obtenerComentariosDePeliculaExitosamente() {
        Comentario c1 = new Comentario("juan", "Excelente pelicula");
        Comentario c2 = new Comentario("maria", "Recomendada");
        Pelicula pelicula = new Pelicula("pel-1", "Inception", "Nolan", "TOKEN1", new Comentario[]{c1, c2});

        when(peliculasService.findById("pel-1")).thenReturn(pelicula);

        ResponseEntity<?> respuesta = peliculasController.getComentarios("pel-1");

        assertNotNull(respuesta);
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody() instanceof Comentario[]);
        Comentario[] comentarios = (Comentario[]) respuesta.getBody();
        assertEquals(2, comentarios.length);
        assertEquals("juan", comentarios[0].getUsuario());
        assertEquals("Excelente pelicula", comentarios[0].getComentario());
    }

    @Test
    void errorAlObtenerComentariosPeliculaNoEncontrada() {
        when(peliculasService.findById("inexistente")).thenReturn(null);

        ResponseEntity<?> respuesta = peliculasController.getComentarios("inexistente");

        assertNotNull(respuesta);
        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
    }

    @Test
    void errorInternoAlObtenerComentarios() {
        when(peliculasService.findById("error")).thenThrow(new RuntimeException("Error de servicio"));

        ResponseEntity<?> respuesta = peliculasController.getComentarios("error");

        assertNotNull(respuesta);
        assertEquals(500, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
    }
}
