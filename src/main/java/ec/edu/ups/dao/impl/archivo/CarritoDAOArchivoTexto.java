package ec.edu.ups.dao.impl.archivo;

import ec.edu.ups.dao.CarritoDAO;
import ec.edu.ups.modelo.Carrito;
import ec.edu.ups.modelo.ItemCarrito;
import ec.edu.ups.modelo.Producto;
import ec.edu.ups.modelo.Usuario;
import ec.edu.ups.dao.UsuarioDAO; // Needed to link Carrito to Usuario
import ec.edu.ups.dao.ProductoDAO; // Needed to link ItemCarrito to Producto

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Iterator;
import java.util.stream.Collectors;

public class CarritoDAOArchivoTexto implements CarritoDAO {

    private String filePath;
    private final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Format for date
    private final UsuarioDAO usuarioDAO; // To find user by username
    private final ProductoDAO productoDAO; // To find product by code

    // Constructor now takes DAOs to resolve relationships
    public CarritoDAOArchivoTexto(String filePath, UsuarioDAO usuarioDAO, ProductoDAO productoDAO) {
        this.filePath = filePath;
        this.usuarioDAO = usuarioDAO;
        this.productoDAO = productoDAO;
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

    private List<Carrito> leerTodos() {
        List<Carrito> carritos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|"); // Use a delimiter not common in other fields, e.g., '|'

                if (parts.length >= 3) { // Minimum parts: code, user, date, then items
                    try {
                        int codigo = Integer.parseInt(parts[0]);
                        String username = parts[1];
                        Date fechaCreacion = SDF.parse(parts[2]);

                        Usuario usuario = usuarioDAO.buscarPorUsername(username); // Retrieve user object

                        Carrito carrito = new Carrito();
                        carrito.setCodigo(codigo);
                        carrito.setUsuario(usuario);
                        // Reconstruct GregorianCalendar
                        GregorianCalendar gc = new GregorianCalendar();
                        gc.setTime(fechaCreacion);
                        carrito.setFechaCreacion(gc);

                        // Parse items if they exist
                        if (parts.length > 3 && !parts[3].isEmpty()) {
                            String[] itemStrings = parts[3].split(","); // Items separated by comma
                            for (String itemStr : itemStrings) {
                                String[] itemParts = itemStr.split(":"); // ProductCode:Quantity
                                if (itemParts.length == 2) {
                                    int productCode = Integer.parseInt(itemParts[0]);
                                    int quantity = Integer.parseInt(itemParts[1]);
                                    Producto producto = productoDAO.buscarPorCodigo(productCode); // Retrieve product object
                                    if (producto != null) {
                                        // Directly add ItemCarrito to the list managed by Carrito
                                        // (This avoids re-adding products and handles quantities correctly)
                                        carrito.agregarProducto(producto, quantity);
                                    }
                                }
                            }
                        }
                        carritos.add(carrito);
                    } catch (NumberFormatException | ParseException e) {
                        System.err.println("Error parsing carrito data: " + line + " -> " + e.getMessage());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, return empty list
        } catch (IOException e) {
            System.err.println("Error reading carritos from file: " + e.getMessage());
        }
        return carritos;
    }

    private void escribirTodos(List<Carrito> carritos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Carrito c : carritos) {
                StringBuilder line = new StringBuilder();
                line.append(c.getCodigo()).append("|");
                line.append(c.getUsuario() != null ? c.getUsuario().getUsername() : "null").append("|");
                line.append(SDF.format(c.getFechaCreacion().getTime()));

                // Serialize items
                List<String> itemStrings = new ArrayList<>();
                for (ItemCarrito item : c.obtenerItems()) {
                    itemStrings.add(item.getProducto().getCodigo() + ":" + item.getCantidad());
                }
                line.append("|").append(String.join(",", itemStrings));

                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing carritos to file: " + e.getMessage());
        }
    }

    @Override
    public void crear(Carrito carrito) {
        List<Carrito> carritos = leerTodos();
        carritos.add(carrito);
        escribirTodos(carritos);
    }

    @Override
    public Carrito buscarPorCodigo(int codigo) {
        return listarTodos().stream()
                .filter(c -> c.getCodigo() == codigo)
                .findFirst().orElse(null);
    }

    @Override
    public void limpiar(Carrito carrito) {
        // This method seems to be used internally by CarritoController
        // to reset a carrito object, not necessarily persist a 'cleaned' state to storage.
        // If it means removing all items from a stored carrito, you'd modify the carrito
        // and then call actualizar.
        // For now, assuming it's for in-memory object manipulation handled by controller.
        // If it was for persistence, it would involve reading, modifying, and writing back.
    }

    @Override
    public void actualizar(Carrito carrito) {
        List<Carrito> carritos = leerTodos();
        for (int i = 0; i < carritos.size(); i++) {
            if (carritos.get(i).getCodigo() == carrito.getCodigo()) {
                carritos.set(i, carrito);
                escribirTodos(carritos);
                return;
            }
        }
    }

    @Override
    public void eliminar(int codigo) {
        List<Carrito> carritos = leerTodos();
        boolean removed = carritos.removeIf(c -> c.getCodigo() == codigo);
        if (removed) {
            escribirTodos(carritos);
        }
    }

    @Override
    public List<Carrito> listarTodos() {
        return leerTodos();
    }

    @Override
    public List<Carrito> listarPorUsuario(String nombreDeUsuario) {
        return listarTodos().stream()
                .filter(c -> c.getUsuario() != null && c.getUsuario().getUsername().equals(nombreDeUsuario))
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarPorUsuario(String nombreDeUsuario) {
        List<Carrito> carritos = leerTodos();
        boolean removed = carritos.removeIf(c -> c.getUsuario() != null && c.getUsuario().getUsername().equals(nombreDeUsuario));
        if (removed) {
            escribirTodos(carritos);
        }
    }
}