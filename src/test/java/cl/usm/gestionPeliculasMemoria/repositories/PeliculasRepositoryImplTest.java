package cl.usm.gestionPeliculasMemoria.repositories;

import cl.usm.gestionPeliculasMemoria.entities.Comentario;
import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PeliculasRepositoryImplTest {

    private PeliculasRepositoryImpl repository;

    @BeforeEach
    void inicializarRepositorio() {
        repository = new PeliculasRepositoryImpl();
    }

    @Test
    void registrarPeliculaExitosamente() {
        Pelicula pelicula = new Pelicula("pel-1", "Inception", "Christopher Nolan", "TOKEN123", new Comentario[0]);
        Pelicula guardada = repository.insert(pelicula);

        assertNotNull(guardada);
        assertEquals("pel-1", guardada.getId());
        assertEquals("Inception", guardada.getTitulo());
        assertEquals("Christopher Nolan", guardada.getDirector());
        assertEquals("TOKEN123", guardada.getTokenDescarga());
        assertEquals(0, guardada.getComentarios().length);
    }

    @Test
    void errorAlRegistrarCuandoIdEsNulo() {
        Pelicula pelicula = new Pelicula(null, "Inception", "Christopher Nolan", "TOKEN123", new Comentario[0]);
        
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> repository.insert(pelicula));
        assertEquals("El ID de la pelicula no puede ser nulo", excepcion.getMessage());
    }

    @Test
    void errorAlRegistrarCuandoIdYaExiste() {
        Pelicula pelicula1 = new Pelicula("PEL-1", "Inception", "Christopher Nolan", "TOKEN123", new Comentario[0]);
        repository.insert(pelicula1);

        Pelicula pelicula2 = new Pelicula("pel-1", "Interstellar", "Christopher Nolan", "TOKEN456", new Comentario[0]);
        
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> repository.insert(pelicula2));
        assertEquals("La pelicula con ID pel-1 ya existe", excepcion.getMessage());
    }

    @Test
    void obtenerListaVaciaSinRegistros() {
        List<Pelicula> resultado = repository.findAll();
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerTodasLasPeliculasCorrectamente() {
        Pelicula p1 = new Pelicula("pel-1", "Inception", "Nolan", "T1", new Comentario[0]);
        Pelicula p2 = new Pelicula("pel-2", "Avatar", "Cameron", "T2", new Comentario[0]);
        
        repository.insert(p1);
        repository.insert(p2);

        List<Pelicula> resultado = repository.findAll();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("pel-1", resultado.get(0).getId());
        assertEquals("pel-2", resultado.get(1).getId());
    }

    @Test
    void buscarPorIdExistente() {
        Pelicula pelicula = new Pelicula("pel-1", "Inception", "Nolan", "TOKEN", new Comentario[0]);
        repository.insert(pelicula);

        Pelicula encontrada = repository.findById("pel-1");
        assertNotNull(encontrada);
        assertEquals("pel-1", encontrada.getId());
    }

    @Test
    void buscarPorIdExistenteSinImportarMayusculas() {
        Pelicula pelicula = new Pelicula("PeL-1", "Inception", "Nolan", "TOKEN", new Comentario[0]);
        repository.insert(pelicula);

        Pelicula encontrada = repository.findById("pel-1");
        assertNotNull(encontrada);
        assertEquals("PeL-1", encontrada.getId());
    }

    @Test
    void buscarPorIdNoEncontrado() {
        Pelicula encontrada = repository.findById("inexistente");
        assertNull(encontrada);
    }

    @Test
    void buscarPorIdNuloRetornaNulo() {
        Pelicula encontrada = repository.findById(null);
        assertNull(encontrada);
    }
}
