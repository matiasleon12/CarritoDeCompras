package ec.edu.ups.dao;

import ec.edu.ups.modelo.PreguntaSeg;

import java.util.List;

public interface PreguntaDAO {

    List<PreguntaSeg> listarTodas(); //lista de preguntas

    //permite seleccionar preguntas aleatorias del banco
    List<PreguntaSeg> seleccionarAleatorias(int n);
}
