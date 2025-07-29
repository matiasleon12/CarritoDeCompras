package ec.edu.ups.dao.impl.archivo;

import ec.edu.ups.dao.UsuarioDAO;
import ec.edu.ups.modelo.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioDAOArchivoTexto implements UsuarioDAO {

    private final String rutaArchivo; // Variable para la ruta completa
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    private static final String SEPARADOR_CAMPO = ";";
    private static final String SEPARADOR_RESPUESTA = "\\|";
    private static final String SEPARADOR_PREGUNTA_RESPUESTA = ":";

    // Constructor que SÍ usa la ruta seleccionada
    public UsuarioDAOArchivoTexto(String rutaBase) {
        this.rutaArchivo = rutaBase + File.separator + "usuarios.txt";
        try {
            File archivo = new File(this.rutaArchivo);
            if (!archivo.getParentFile().exists()) {
                archivo.getParentFile().mkdirs();
            }
            if (!archivo.exists()) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error al crear el archivo de usuarios: " + e.getMessage());
        }
    }

    @Override
    public Usuario autenticar(String username, String contrasenia) {
        return listarTodos().stream()
                .filter(usuario -> usuario.getUsername().equals(username) && usuario.getContrasenia().equals(contrasenia))
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
                .filter(usuario -> usuario.getUsername().equals(username))
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
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(this.rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                Usuario usuario = parsearLinea(linea);
                if (usuario != null) {
                    usuarios.add(usuario);
                }
            }
        } catch (IOException e) {
            // No es un error si el archivo está vacío la primera vez
        }
        return usuarios;
    }

    @Override
    public List<Usuario> listarPorRol(Rol rol) {
        return listarTodos().stream()
                .filter(u -> u.getRol() == rol)
                .collect(Collectors.toList());
    }

    private void guardarTodos(List<Usuario> usuarios) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.rutaArchivo, false))) { // false para sobrescribir
            for (Usuario usuario : usuarios) {
                writer.write(formatearLinea(usuario));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de usuarios: " + e.getMessage());
        }
    }

    private String formatearLinea(Usuario usuario) {
        String fechaStr = (usuario.getFechaNacimiento() != null) ? SDF.format(usuario.getFechaNacimiento()) : "";
        String respuestasStr = "";
        if (usuario.getRespuestasSeguridad() != null && !usuario.getRespuestasSeguridad().isEmpty()) {
            respuestasStr = usuario.getRespuestasSeguridad().stream()
                    .map(r -> r.getPregunta().getClave() + SEPARADOR_PREGUNTA_RESPUESTA + r.getRespuesta())
                    .collect(Collectors.joining("|"));
        }
        return String.join(SEPARADOR_CAMPO,
                usuario.getUsername(),
                usuario.getContrasenia(),
                usuario.getRol().name(),
                usuario.getNombre(),
                usuario.getApellido(),
                fechaStr,
                usuario.getEmail(),
                usuario.getTelefono(),
                respuestasStr
        );
    }

    private Usuario parsearLinea(String linea) {
        String[] partes = linea.split(SEPARADOR_CAMPO, -1); // -1 para incluir campos vacíos
        if (partes.length < 9) return null;
        String username = partes[0];
        String contrasenia = partes[1];
        Rol rol = Rol.valueOf(partes[2]);
        String nombre = partes[3];
        String apellido = partes[4];
        Date fechaNacimiento = null;
        try {
            if (!partes[5].isEmpty()) {
                fechaNacimiento = SDF.parse(partes[5]);
            }
        } catch (ParseException e) {
            System.err.println("Formato de fecha incorrecto para el usuario " + username);
        }
        String email = partes[6];
        String telefono = partes[7];
        Usuario usuario = new Usuario(username, contrasenia, rol, nombre, apellido, fechaNacimiento, email, telefono);
        if (!partes[8].isEmpty()) {
            List<RespuestaSeg> respuestas = new ArrayList<>();
            String[] respuestasPares = partes[8].split(SEPARADOR_RESPUESTA);
            for (String par : respuestasPares) {
                String[] preguntaRespuesta = par.split(SEPARADOR_PREGUNTA_RESPUESTA, 2);
                if (preguntaRespuesta.length == 2) {
                    respuestas.add(new RespuestaSeg(new PreguntaSeg(preguntaRespuesta[0]), preguntaRespuesta[1]));
                }
            }
            usuario.setRespuestasSeguridad(respuestas);
        }
        return usuario;
    }
}