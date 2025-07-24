package ec.edu.ups.dao.impl.binario;

import ec.edu.ups.dao.UsuarioDAO;
import ec.edu.ups.modelo.Rol;
import ec.edu.ups.modelo.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del DAO para gestionar la persistencia de objetos Usuario
 * en un archivo binario (usuarios.dat).
 */
public class UsuarioDAOArchivoBinario implements UsuarioDAO {

    private String rutaArchivo;

    /**
     * Constructor que define la ruta base donde se guardará el archivo de usuarios.
     * @param rutaBase La carpeta donde se almacenará "usuarios.dat".
     */
    public UsuarioDAOArchivoBinario(String rutaBase) {
        this.rutaArchivo = rutaBase + File.separator + "usuarios.dat";
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            return usuarios; // Si no hay archivo, no hay usuarios.
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            usuarios = (List<Usuario>) ois.readObject();
        } catch (FileNotFoundException e) {
            // Esto es normal si el archivo aún no se ha creado.
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al leer el archivo binario de usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    private void guardarTodos(List<Usuario> usuarios) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo, false))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo binario de usuarios: " + e.getMessage());
        }
    }

    @Override
    public Usuario autenticar(String username, String contrasenia) {
        return listarTodos().stream()
                .filter(u -> u.getUsername().equals(username) && u.getContrasenia().equals(contrasenia))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void crear(Usuario usuario) {
        List<Usuario> usuarios = listarTodos();
        usuarios.add(usuario);
        guardarTodos(usuarios);
    }

    @Override
    public Usuario buscarPorUsername(String username) {
        return listarTodos().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void eliminar(String username) {
        List<Usuario> usuarios = listarTodos();
        usuarios.removeIf(u -> u.getUsername().equals(username));
        guardarTodos(usuarios);
    }

    @Override
    public void actualizar(Usuario usuario) {
        List<Usuario> usuarios = listarTodos();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getUsername().equals(usuario.getUsername())) {
                usuarios.set(i, usuario);
                break;
            }
        }
        guardarTodos(usuarios);
    }

    @Override
    public List<Usuario> listarPorRol(Rol rol) {
        return listarTodos().stream()
                .filter(u -> u.getRol() == rol)
                .collect(Collectors.toList());
    }
}