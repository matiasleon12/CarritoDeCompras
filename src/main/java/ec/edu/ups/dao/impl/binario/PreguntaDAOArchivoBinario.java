package ec.edu.ups.dao.impl.binario;


import ec.edu.ups.dao.PreguntaDAO;
import ec.edu.ups.modelo.PreguntaSeg;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implementación del DAO para la persistencia de Preguntas de Seguridad en un archivo binario.
 * Por simplicidad, esta clase puede leer un archivo de texto con las claves
 * y tener un método para generar el archivo binario si no existe.
 */
public class PreguntaDAOArchivoBinario implements PreguntaDAO {

    private String rutaArchivo;

    public PreguntaDAOArchivoBinario(String rutaBase) {
        this.rutaArchivo = rutaBase + File.separator + "preguntas.dat";
        inicializarBancoDePreguntasSiNoExiste();
    }

    private void inicializarBancoDePreguntasSiNoExiste() {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            System.out.println("Creando archivo binario de preguntas por primera vez...");
            List<PreguntaSeg> bancoInicial = List.of(
                    new PreguntaSeg("pS.1"), new PreguntaSeg("pS.2"),
                    new PreguntaSeg("pS.3"), new PreguntaSeg("pS.4"),
                    new PreguntaSeg("pS.5"), new PreguntaSeg("pS.6"),
                    new PreguntaSeg("pS.7"), new PreguntaSeg("pS.8"),
                    new PreguntaSeg("pS.9"), new PreguntaSeg("pS.10")
            );
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
                oos.writeObject(bancoInicial);
            } catch (IOException e) {
                System.err.println("Error al inicializar el archivo binario de preguntas: " + e.getMessage());
            }
        }
    }

    @Override
    public List<PreguntaSeg> listarTodas() {
        List<PreguntaSeg> preguntas = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            preguntas = (List<PreguntaSeg>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al leer el archivo binario de preguntas: " + e.getMessage());
        }
        return preguntas;
    }

    @Override
    public List<PreguntaSeg> seleccionarAleatorias(int n) {
        List<PreguntaSeg> todas = listarTodas();
        if (n >= todas.size()) {
            return todas;
        }
        Collections.shuffle(todas, new Random());
        return todas.subList(0, n);
    }
}