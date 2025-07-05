package ec.edu.ups.modelo;

//guarda la respuesta que el usuario responde a una pregunta concreta
public class RespuestaSeg {
    private final PreguntaSeg pregunta;
    private final String respuesta;

    public RespuestaSeg(PreguntaSeg pregunta, String respuesta) {
        this.pregunta  = pregunta;
        this.respuesta = respuesta;
    }

    //getters
    public PreguntaSeg getPregunta() {
        return pregunta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    @Override
    public String toString() {
        return "RespuestaSeguridad{" +
                "pregunta=" + pregunta.getClave() +
                ", respuesta='" + respuesta + '\'' +
                '}';
    }
}
