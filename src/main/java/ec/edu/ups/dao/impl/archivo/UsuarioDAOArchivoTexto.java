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

    private final String RUTA_ARCHIVO = "usuarios.txt";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    private static final String SEPARADOR_CAMPO = ";";
    private static final String SEPARADOR_RESPUESTA = "\\|";
    private static final String SEPARADOR_PREGUNTA_RESPUESTA = ":";

    public UsuarioDAOArchivoTexto() {
        // Asegurarse de que el archivo exista al iniciar
        try {
            File archivo = new File(RUTA_ARCHIVO);
            if (!archivo.exists()) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error al crear el archivo de usuarios: " + e.getMessage());
        }
    }

    @Override
    public Usuario autenticar(String username, String contrasenia) {
        List<Usuario> usuarios = listarTodos();
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equals(username) && usuario.getContrasenia().equals(contrasenia)) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public void crear(Usuario usuario) {
        List<Usuario> usuarios = listarTodos();
        usuarios.add(usuario);
        guardarTodos(usuarios);
    }

    @Override
    public Usuario buscarPorUsername(String username) {
        List<Usuario> usuarios = listarTodos();
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equals(username)) {
                return usuario;
            }
        }
        return null;
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
        try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                Usuario usuario = parsearLinea(linea);
                if (usuario != null) {
                    usuarios.add(usuario);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de usuarios: " + e.getMessage());
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) { // false para sobrescribir
            for (Usuario usuario : usuarios) {
                writer.write(formatearLinea(usuario));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de usuarios: " + e.getMessage());
        }
    }

    /**
     * Convierte un objeto Usuario a una línea de texto para el archivo.
     * Formato: username;contrasenia;rol;nombre;apellido;fecha;email;telefono;preg1:resp1|preg2:resp2
     */
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

    /**
     * Parsea una línea de texto del archivo a un objeto Usuario.
     */
    private Usuario parsearLinea(String linea) {
        String[] partes = linea.split(SEPARADOR_CAMPO);
        if (partes.length < 8) return null;

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

        // Parsear respuestas de seguridad si existen (a partir de la novena parte)
        if (partes.length > 8 && !partes[8].isEmpty()) {
            List<RespuestaSeg> respuestas = new ArrayList<>();
            String[] respuestasPares = partes[8].split(SEPARADOR_RESPUESTA);
            for (String par : respuestasPares) {
                String[] preguntaRespuesta = par.split(SEPARADOR_PREGUNTA_RESPUESTA, 2);
                if (preguntaRespuesta.length == 2) {
                    PreguntaSeg pregunta = new PreguntaSeg(preguntaRespuesta[0]);
                    String respuesta = preguntaRespuesta[1];
                    respuestas.add(new RespuestaSeg(pregunta, respuesta));
                }
            }
            usuario.setRespuestasSeguridad(respuestas);
        }

        return usuario;
    }
}