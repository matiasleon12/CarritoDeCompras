package ec.edu.ups.vista;

import ec.edu.ups.modelo.PreguntaSeg;
import ec.edu.ups.modelo.RespuestaSeg;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntasSeguridad extends JDialog {
    private final List<PreguntaSeg> preguntas;
    private final List<JTextField> campos = new ArrayList<>();
    private boolean submitted;
    private final int minimoRespuestasRequeridas;

    public PreguntasSeguridad(List<PreguntaSeg> preguntas, MensajeInternacionalizacionHandler mih, int minimoRespuestas) {
        super();
        this.preguntas = preguntas;
        this.minimoRespuestasRequeridas = minimoRespuestas;

        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle(mih.get("preguntaS.titulo"));

        buildUI(mih);
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI(MensajeInternacionalizacionHandler mih) {
        JPanel form = new JPanel(new GridLayout(preguntas.size(), 2, 5, 5));
        for (PreguntaSeg p : preguntas) {
            //etiqueta traducida
            JLabel lbl = new JLabel(mih.get(p.getClave()) + ":");
            JTextField tf = new JTextField(20);
            campos.add(tf);
            form.add(lbl);
            form.add(tf);
        }

        //usamos el bundle
        JButton btnCancelar = new JButton(mih.get("carrito.btnCancelar"));
        JButton btnAceptar  = new JButton(mih.get("agregar.btnAceptar"));
        btnCancelar.addActionListener(e -> dispose());
        btnAceptar.addActionListener(e -> onAccept(mih));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancelar);
        botones.add(btnAceptar);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(botones, BorderLayout.SOUTH);
    }
    //el usuario tiene que responder unicamente 5 de las 10 preguntas mostradas
    private void onAccept(MensajeInternacionalizacionHandler mih) {
        long contestadas = campos.stream().filter(tf -> !tf.getText().trim().isEmpty()).count();

        if (contestadas < minimoRespuestasRequeridas) {
            JOptionPane.showMessageDialog(
                    this,
                    mih.get("preguntaS.error.Responder"),
                    mih.get("preguntaS.tituloCompleto"),
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        submitted = true;
        dispose();
    }
    //me retorna true si el usuario coloco aceptar y completo todas las preguntas
    public boolean isSubmitted() {
        return submitted;
    }

    //me ayuda a listar las respuestas en el mismo orden que las pregunats
    public List<RespuestaSeg> getRespuestas() {
        List<RespuestaSeg> respuestas = new ArrayList<>();
        if (!submitted) return respuestas;
        for (int i = 0; i < preguntas.size(); i++) {
            String resp = campos.get(i).getText().trim();
            respuestas.add(new RespuestaSeg(preguntas.get(i), resp));
        }
        return respuestas;
    }
}
