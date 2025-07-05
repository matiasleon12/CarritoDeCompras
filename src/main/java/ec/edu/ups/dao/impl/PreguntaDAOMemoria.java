package ec.edu.ups.dao.impl;

import ec.edu.ups.dao.PreguntaDAO;
import ec.edu.ups.modelo.PreguntaSeg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PreguntaDAOMemoria implements PreguntaDAO {
    private final List<PreguntaSeg> banco;

    public PreguntaDAOMemoria() {
        banco = List.of( //lista de preguntas
                new PreguntaSeg("pS.1"),
                new PreguntaSeg("pS.2"),
                new PreguntaSeg("pS.3"),
                new PreguntaSeg("pS.4"),
                new PreguntaSeg("pS.5"),
                new PreguntaSeg("pS.6"),
                new PreguntaSeg("pS.7"),
                new PreguntaSeg("pS.8"),
                new PreguntaSeg("pS.9"),
                new PreguntaSeg("pS.10" )
        );
    }

    @Override
    public List<PreguntaSeg> listarTodas() {
        return new ArrayList<>(banco);
    }

    @Override
    public List<PreguntaSeg> seleccionarAleatorias(int n) {
        //se realiza una copia
        List<PreguntaSeg> temp = new ArrayList<>(banco);
        List<PreguntaSeg> elegidas = new ArrayList<>();

        //se generan numeros aleatorios
        Random rnd = new Random();

        for (int i = 0; i < n && !temp.isEmpty(); i++) {
            //se elige un indice al azar
            int idx = rnd.nextInt(temp.size());
            // removemos de la lista temporal y lo añadimos al resultado
            elegidas.add(temp.remove(idx));
        }
        return elegidas;
    }
}