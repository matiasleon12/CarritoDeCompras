package ec.edu.ups.dao.impl.archivo;

import ec.edu.ups.dao.PreguntaDAO;
import ec.edu.ups.modelo.PreguntaSeg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PreguntaDAOArchivoTexto implements PreguntaDAO {

    private final String RUTA_ARCHIVO = "preguntas.txt";

    public PreguntaDAOArchivoTexto() {

    }

    @Override
    public List<PreguntaSeg> listarTodas() {
        List<PreguntaSeg> banco = new ArrayList<>();
        File archivo = new File(RUTA_ARCHIVO);

        if (!archivo.exists()) {
            System.err.println("Archivo de preguntas no encontrado en: " + RUTA_ARCHIVO);
            return banco; // Devuelve lista vacía
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String clave;
            while ((clave = reader.readLine()) != null) {
                if (!clave.trim().isEmpty()) {
                    banco.add(new PreguntaSeg(clave.trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de preguntas: " + e.getMessage());
        }
        return banco;
    }

    @Override
    public List<PreguntaSeg> seleccionarAleatorias(int n) {
        List<PreguntaSeg> todas = listarTodas();
        if (n >= todas.size()) {
            return todas;
        }


        List<PreguntaSeg> temp = new ArrayList<>(todas);
        Collections.shuffle(temp, new Random());

        // Devolvemos una sublista con los primeros 'n' elementos
        return temp.subList(0, n);
    }
}