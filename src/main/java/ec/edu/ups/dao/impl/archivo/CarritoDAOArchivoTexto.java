package ec.edu.ups.dao.impl.archivo;

import ec.edu.ups.dao.CarritoDAO;
import ec.edu.ups.dao.ProductoDAO;
import ec.edu.ups.dao.UsuarioDAO;
import ec.edu.ups.modelo.Carrito;
import ec.edu.ups.modelo.ItemCarrito;
import ec.edu.ups.modelo.Producto;
import ec.edu.ups.modelo.Usuario;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CarritoDAOArchivoTexto implements CarritoDAO {

    private String filePath;
    private final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final UsuarioDAO usuarioDAO;
    private final ProductoDAO productoDAO;

    public CarritoDAOArchivoTexto(String rutaBase, UsuarioDAO usuarioDAO, ProductoDAO productoDAO) {
        this.filePath = rutaBase + File.separator + "carritos.txt";
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
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    int codigo = Integer.parseInt(parts[0]);
                    Usuario usuario = usuarioDAO.buscarPorUsername(parts[1]);
                    Date fecha = SDF.parse(parts[2]);
                    if (usuario != null) {
                        Carrito carrito = new Carrito();
                        carrito.setCodigo(codigo);
                        carrito.setUsuario(usuario);
                        GregorianCalendar gc = new GregorianCalendar();
                        gc.setTime(fecha);
                        carrito.setFechaCreacion(gc);
                        if (parts.length > 3 && !parts[3].isEmpty()) {
                            String[] itemStrings = parts[3].split(",");
                            for (String itemStr : itemStrings) {
                                String[] itemParts = itemStr.split(":");
                                if (itemParts.length == 2) {
                                    Producto p = productoDAO.buscarPorCodigo(Integer.parseInt(itemParts[0]));
                                    if (p != null) {
                                        carrito.agregarProducto(p, Integer.parseInt(itemParts[1]));
                                    }
                                }
                            }
                        }
                        carritos.add(carrito);
                    }
                }
            }
        } catch (IOException | ParseException | NumberFormatException e) {
            // No es un error si está vacío
        }
        return carritos;
    }

    private void escribirTodos(List<Carrito> carritos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Carrito c : carritos) {
                String itemsStr = c.obtenerItems().stream()
                        .map(item -> item.getProducto().getCodigo() + ":" + item.getCantidad())
                        .collect(Collectors.joining(","));
                String line = c.getCodigo() + "|" + c.getUsuario().getUsername() + "|" + SDF.format(c.getFechaCreacion().getTime()) + "|" + itemsStr;
                writer.write(line);
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
        if (carritos.removeIf(c -> c.getCodigo() == codigo)) {
            escribirTodos(carritos);
        }
    }

    @Override
    public List<Carrito> listarPorUsuario(String nombreDeUsuario) {
        return leerTodos().stream()
                .filter(c -> c.getUsuario() != null && c.getUsuario().getUsername().equals(nombreDeUsuario))
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarPorUsuario(String nombreDeUsuario) {
        List<Carrito> carritos = leerTodos();
        if (carritos.removeIf(c -> c.getUsuario() != null && c.getUsuario().getUsername().equals(nombreDeUsuario))) {
            escribirTodos(carritos);
        }
    }

    @Override
    public Carrito buscarPorCodigo(int codigo) {
        return leerTodos().stream().filter(c -> c.getCodigo() == codigo).findFirst().orElse(null);
    }

    @Override
    public void limpiar(Carrito carrito) {
        actualizar(carrito);
    }

    @Override
    public List<Carrito> listarTodos() {
        return leerTodos();
    }
}