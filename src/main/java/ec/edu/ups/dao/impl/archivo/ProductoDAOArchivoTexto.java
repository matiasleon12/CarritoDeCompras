package ec.edu.ups.dao.impl.archivo;

import ec.edu.ups.dao.ProductoDAO;
import ec.edu.ups.modelo.Producto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductoDAOArchivoTexto implements ProductoDAO {

    private String filePath;

    public ProductoDAOArchivoTexto(String rutaBase) {
        this.filePath = rutaBase + File.separator + "productos.txt";
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
    }

    private List<Producto> leerTodos() {
        List<Producto> productos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    productos.add(new Producto(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2])));
                }
            }
        } catch (IOException | NumberFormatException e) {
            // No es un error si el archivo está vacío
        }
        return productos;
    }

    private void escribirTodos(List<Producto> productos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Producto p : productos) {
                writer.write(p.getCodigo() + ";" + p.getNombre() + ";" + p.getPrecio());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing products to file: " + e.getMessage());
        }
    }

    @Override
    public void crear(Producto producto) {
        List<Producto> productos = leerTodos();
        productos.add(producto);
        escribirTodos(productos);
    }

    @Override
    public Producto buscarPorCodigo(int codigo) {
        return leerTodos().stream().filter(p -> p.getCodigo() == codigo).findFirst().orElse(null);
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return leerTodos().stream().filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase())).collect(Collectors.toList());
    }

    @Override
    public void actualizar(Producto producto) {
        List<Producto> productos = leerTodos();
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getCodigo() == producto.getCodigo()) {
                productos.set(i, producto);
                escribirTodos(productos);
                return;
            }
        }
    }

    @Override
    public void eliminar(int codigo) {
        List<Producto> productos = leerTodos();
        if (productos.removeIf(p -> p.getCodigo() == codigo)) {
            escribirTodos(productos);
        }
    }

    @Override
    public List<Producto> listarTodos() {
        return leerTodos();
    }
}