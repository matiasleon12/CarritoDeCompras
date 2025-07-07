package ec.edu.ups.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Locale;
import ec.edu.ups.util.*;

public class LoginView extends JFrame {
    private JPanel panelPrincipal;
    private JTextField txtUsuario;
    private JButton btnIniciarSesion;
    private JButton btnRegistrarse;
    private JPasswordField txtContrasenia;
    private JComboBox<Idioma> comboBoxIdioma;
    private JLabel lblUsuario;
    private JLabel lblContrasenia;
    private JLabel lblIdioma;
    private JLabel lblIniciarSesion;
    private JButton btnRecuperarContra;

    private MensajeInternacionalizacionHandler mensInter;

    public LoginView() {
        setContentPane(panelPrincipal);
        setTitle("Iniciar Sesión");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(500, 400);



        mensInter = new MensajeInternacionalizacionHandler("es", "EC");

        setIconoEscalado(btnIniciarSesion, "imagenes/imagen_inicioSesion.png", 25, 25);
        setIconoEscalado(btnRegistrarse, "imagenes/imagen_registrarse.png", 25, 25);
        setIconoEscalado(btnRecuperarContra, "imagenes/imagen_guardarDatos.png", 25, 25);

        idiomaComboBox(); //inicializamos el combo box con los idiomas que deseamos

        actualizarTextos(); //asigna textos segun el idioma elegido

        //cada vez que se seleccione otro idioma del combo
        comboBoxIdioma.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Idioma seleccionado = (Idioma) comboBoxIdioma.getSelectedItem();
                mensInter.setLenguaje(seleccionado.getLocale().getLanguage(), seleccionado.getLocale().getCountry());
                actualizarTextos();
            }
        });
    }

    private void setIconoEscalado(JButton boton, String ruta, int ancho, int alto) {
        try {
            java.net.URL url = getClass().getClassLoader().getResource(ruta);
            if (url != null) {
                Image imagen = new ImageIcon(url).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                boton.setIcon(new ImageIcon(imagen));
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen " + ruta + " → " + e.getMessage());
        }
    }

    private void idiomaComboBox() {
        comboBoxIdioma.removeAllItems();
        comboBoxIdioma.addItem(new Idioma(new Locale("es","EC"), "Español"));
        comboBoxIdioma.addItem(new Idioma(new Locale("en","US"), "English"));
        comboBoxIdioma.addItem(new Idioma(new Locale("fr","FR"), "Français"));
        comboBoxIdioma.setSelectedIndex(0);
    }

    private void actualizarTextos() {
        setTitle(mensInter.get("login.titulo"));
        lblIniciarSesion.setText(mensInter.get("login.titulo"));
        lblIdioma.setText(mensInter.get("login.txtIdioma"));
        lblUsuario.setText(mensInter.get("login.txtUsuario"));
        lblContrasenia.setText(mensInter.get("login.txtContrasenia"));
        btnIniciarSesion.setText(mensInter.get("button.login"));
        btnRegistrarse.setText(mensInter.get("button.registrar"));
        btnRecuperarContra.setText(mensInter.get("button.olvido.contrasenia"));
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    //getters y setters
    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    public void setPanelPrincipal(JPanel panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
    }

    public JTextField getTxtUsuario() {
        return txtUsuario;
    }

    public void setTxtUsuario(JTextField txtUsuario) {
        this.txtUsuario = txtUsuario;
    }

    public JButton getBtnIniciarSesion() {
        return btnIniciarSesion;
    }

    public JButton getBtnRegistrarse() {
        return btnRegistrarse;
    }

    public JPasswordField getTxtContrasenia() {
        return txtContrasenia;
    }

    public JComboBox<Idioma> getComboBoxIdioma() {
        return comboBoxIdioma;
    }

    public JButton getBtnRecuperarContra() {
        return btnRecuperarContra;
    }
}
