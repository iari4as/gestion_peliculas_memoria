package cl.usm.gestionPeliculasMemoria.services;

import cl.usm.gestionPeliculasMemoria.entities.Comentario;
import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import cl.usm.gestionPeliculasMemoria.repositories.PeliculasRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeliculasServiceImplTest {

    @Mock
    private PeliculasRepository peliculasRepository;

    @InjectMocks
    private PeliculasServiceImpl peliculasService;

    @Test
    void guardarPeliculaExitosamente() {
        Pelicula peliculaInput = new Pelicula("pel-123", "Matrix", "Lana Wachowski", null, new Comentario[0]);
        
        when(peliculasRepository.insert(any(Pelicula.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pelicula guardada = peliculasService.createPelicula(peliculaInput);

        assertNotNull(guardada);
        assertEquals("pel-123", guardada.getId());
        assertNotNull(guardada.getTokenDescarga());
        assertEquals(10, guardada.getTokenDescarga().length());
        
        verify(peliculasRepository, times(1)).insert(peliculaInput);
    }

    @Test
    void errorAlRegistrarRetornaNulo() {
        Pelicula peliculaInput = new Pelicula("pel-123", "Matrix", "Lana Wachowski", null, new Comentario[0]);
        
        when(peliculasRepository.insert(any(Pelicula.class))).thenThrow(new IllegalArgumentException("Error al insertar"));

        Pelicula guardada = peliculasService.createPelicula(peliculaInput);

        assertNull(guardada);
        verify(peliculasRepository, times(1)).insert(peliculaInput);
    }

    @Test
    void buscarTodasLasPeliculasCorrectamente() {
        List<Pelicula> listaSimulada = new ArrayList<>();
        listaSimulada.add(new Pelicula("pel-1", "Matrix", "Wachowski", "TOKEN1", new Comentario[0]));
        listaSimulada.add(new Pelicula("pel-2", "Avatar", "Cameron", "TOKEN2", new Comentario[0]));
        
        when(peliculasRepository.findAll()).thenReturn(listaSimulada);

        List<Pelicula> resultado = peliculasService.getAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Matrix", resultado.get(0).getTitulo());
        
        verify(peliculasRepository, times(1)).findAll();
    }

    @Test
    void buscarPorIdEspecificoCorrectamente() {
        Pelicula peliculaSimulada = new Pelicula("pel-1", "Matrix", "Wachowski", "TOKEN1", new Comentario[0]);
        
        when(peliculasRepository.findById("pel-1")).thenReturn(peliculaSimulada);

        Pelicula resultado = peliculasService.findById("pel-1");

        assertNotNull(resultado);
        assertEquals("pel-1", resultado.getId());
        assertEquals("Matrix", resultado.getTitulo());

        verify(peliculasRepository, times(1)).findById("pel-1");
    }

    @Test
    void filtrarPeliculasPorCoincidencias() {
        List<Pelicula> listaSimulada = new ArrayList<>();
        listaSimulada.add(new Pelicula("pel-matrix", "The Matrix", "Wachowski", "T1", new Comentario[0]));
        listaSimulada.add(new Pelicula("pel-avatar", "Avatar", "Cameron", "T2", new Comentario[0]));
        listaSimulada.add(new Pelicula("pel-3", "Matrix Resurrections", "Wachowski", "T3", new Comentario[0]));

        when(peliculasRepository.findAll()).thenReturn(listaSimulada);

        List<Pelicula> filtradasPorTitulo = peliculasService.filter("matrix");
        assertEquals(2, filtradasPorTitulo.size());
        assertTrue(filtradasPorTitulo.stream().anyMatch(p -> p.getId().equals("pel-matrix")));
        assertTrue(filtradasPorTitulo.stream().anyMatch(p -> p.getId().equals("pel-3")));

        List<Pelicula> filtradasPorId = peliculasService.filter("avatar");
        assertEquals(1, filtradasPorId.size());
        assertEquals("pel-avatar", filtradasPorId.get(0).getId());

        verify(peliculasRepository, times(2)).findAll();
    }

    @Test
    void errorAlRegistrarConCamposNulosLanzaNullPointerException() {
        List<Pelicula> listaSimulada = new ArrayList<>();
        listaSimulada.add(new Pelicula(null, "The Matrix", "Wachowski", "T1", new Comentario[0]));

        when(peliculasRepository.findAll()).thenReturn(listaSimulada);

        assertThrows(NullPointerException.class, () -> peliculasService.filter("matrix"));
    }
}
