package ec.edu.ups.dao.impl.archivo;

import ec.edu.ups.dao.ProductoDAO;
import ec.edu.ups.modelo.Producto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.stream.Collectors;

public class ProductoDAOArchivoTexto implements ProductoDAO {

    private String filePath;

    public ProductoDAOArchivoTexto(String filePath) {
        this.filePath = filePath;
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
                String[] parts = line.split(";"); // Example delimiter
                if (parts.length == 3) {
                    try {
                        int codigo = Integer.parseInt(parts[0]);
                        String nombre = parts[1];
                        double precio = Double.parseDouble(parts[2]);
                        productos.add(new Producto(codigo, nombre, precio));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing product data: " + line + " -> " + e.getMessage());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, return empty list
        } catch (IOException e) {
            System.err.println("Error reading products from file: " + e.getMessage());
        }
        return productos;
    }

    private void escribirTodos(List<Producto> productos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
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
        return listarTodos().stream()
                .filter(p -> p.getCodigo() == codigo)
                .findFirst().orElse(null);
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return listarTodos().stream()
                .filter(p -> p.getNombre().startsWith(nombre))
                .collect(Collectors.toList());
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
        boolean removed = productos.removeIf(p -> p.getCodigo() == codigo);
        if (removed) {
            escribirTodos(productos);
        }
    }

    @Override
    public List<Producto> listarTodos() {
        return leerTodos();
    }
}