package ec.edu.ups.modelo;

import java.util.Date;
import java.util.List;

public class Usuario {
    private String username;
    private String contrasenia;
    private Rol rol;
    private String nombre;
    private String apellido;
    private Date fechaNacimiento;
    private String email;
    private String telefono;


    private List<RespuestaSeg> respuestasSeguridad;

    public Usuario() {
    }

    public Usuario(String usuario, String contrasenia, Rol rol, String nombre, String apellido, Date fechaNacimiento, String email, String telefono) {
        this.username = usuario;
        this.contrasenia = contrasenia;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.telefono = telefono;
    }

    //getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public Rol getRol() {
        return rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<RespuestaSeg> getRespuestasSeguridad() {
        return respuestasSeguridad;
    }

    public void setRespuestasSeguridad(List<RespuestaSeg> respuestasSeguridad) {
        this.respuestasSeguridad = respuestasSeguridad;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombreDeUsuario='" + username + '\'' +
                ", contrasenia='" + contrasenia + '\'' +
                ", rol=" + rol +
                '}';
    }
}
