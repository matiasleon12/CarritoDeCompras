package ec.edu.ups.dao.impl.binario;


import ec.edu.ups.dao.CarritoDAO;
import ec.edu.ups.modelo.Carrito;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del DAO para la persistencia de Carritos en un archivo binario.
 */
public class CarritoDAOArchivoBinario implements CarritoDAO {

    private String rutaArchivo;

    public CarritoDAOArchivoBinario(String rutaBase) {
        this.rutaArchivo = rutaBase + File.separator + "carritos.dat";
    }

    @Override
    public List<Carrito> listarTodos() {
        List<Carrito> carritos = new ArrayList<>();
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) return carritos;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            carritos = (List<Carrito>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al leer el archivo binario de carritos: " + e.getMessage());
        }
        return carritos;
    }

    private void guardarTodos(List<Carrito> carritos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(carritos);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo binario de carritos: " + e.getMessage());
        }
    }

    @Override
    public void crear(Carrito carrito) {
        List<Carrito> carritos = listarTodos();
        carritos.add(carrito);
        guardarTodos(carritos);
    }

    @Override
    public Carrito buscarPorCodigo(int codigo) {
        return listarTodos().stream()
                .filter(c -> c.getCodigo() == codigo)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void limpiar(Carrito carrito) {
        // En persistencia, 'limpiar' es 'actualizar'
        actualizar(carrito);
    }

    @Override
    public void actualizar(Carrito carrito) {
        List<Carrito> carritos = listarTodos();
        for (int i = 0; i < carritos.size(); i++) {
            if (carritos.get(i).getCodigo() == carrito.getCodigo()) {
                carritos.set(i, carrito);
                break;
            }
        }
        guardarTodos(carritos);
    }

    @Override
    public void eliminar(int codigo) {
        List<Carrito> carritos = listarTodos();
        carritos.removeIf(c -> c.getCodigo() == codigo);
        guardarTodos(carritos);
    }

    @Override
    public List<Carrito> listarPorUsuario(String username) {
        return listarTodos().stream()
                .filter(c -> c.getUsuario().getUsername().equals(username))
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarPorUsuario(String username) {
        List<Carrito> carritos = listarTodos();
        carritos.removeIf(c -> c.getUsuario().getUsername().equals(username));
        guardarTodos(carritos);
    }
}