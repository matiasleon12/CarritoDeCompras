package ec.edu.ups.vista;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import ec.edu.ups.util.*;

public class LoginView extends JFrame {

    // (El enum TipoAlmacenamiento y los componentes del panel no cambian)
    public enum TipoAlmacenamiento {
        MEMORIA, TEXTO, BINARIO
    }

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
    private JComboBox<TipoAlmacenamiento> cbxTipoAlmacenamiento;
    private JLabel lblTipoAlmacenamiento;
    private JButton btnSeleccionarRuta;
    private JLabel lblRutaArchivos;
    private String rutaAlmacenamiento = ".";
    private JPanel panelAlmacenamiento;
    private JTextField txtRutaArchivos;




    private MensajeInternacionalizacionHandler mensInter;

    public LoginView() {
        setContentPane(panelPrincipal);
        setTitle("Iniciar Sesión");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(550, 450);



        setIconoEscalado(btnIniciarSesion, "imagenes/imagen_inicioSesion.png", 25, 25);
        setIconoEscalado(btnRegistrarse, "imagenes/imagen_registrarse.png", 25, 25);


        idiomaComboBox();
        almacenamientoComboBox();



        btnSeleccionarRuta.addActionListener(e -> seleccionarRuta());

        cbxTipoAlmacenamiento.addActionListener(e -> {
            boolean habilitar = getTipoAlmacenamientoSeleccionado() != TipoAlmacenamiento.MEMORIA;

            btnSeleccionarRuta.setEnabled(habilitar);
            lblRutaArchivos.setEnabled(habilitar);


            btnSeleccionarRuta.setVisible(habilitar);
            lblRutaArchivos.setVisible(habilitar);
            txtRutaArchivos.setVisible(habilitar);
        });
    }


    /**
     * Permite que el controlador establezca el manejador de internacionalización.
     * @param mensInter El manejador de idioma que usará la vista.
     */
    public void setMensajeInternacionalizacionHandler(MensajeInternacionalizacionHandler mensInter) {
        this.mensInter = mensInter;
        actualizarTextos();
    }

    public void actualizarTextos() {

        if (mensInter == null) return;

        setTitle(mensInter.get("login.titulo"));
        lblIniciarSesion.setText(mensInter.get("login.titulo"));
        lblIdioma.setText(mensInter.get("login.txtIdioma"));
        lblUsuario.setText(mensInter.get("login.txtUsuario"));
        lblContrasenia.setText(mensInter.get("login.txtContrasenia"));
        btnIniciarSesion.setText(mensInter.get("button.login"));
        btnRegistrarse.setText(mensInter.get("button.registrar"));
        btnRecuperarContra.setText(mensInter.get("button.olvido.contrasenia"));
        lblTipoAlmacenamiento.setText(mensInter.get("Tipo de almacenamiento")); // Añadir esta clave a los properties
        lblRutaArchivos.setText(mensInter.get("Elegir ruta")); // Añadir esta clave a los properties
        btnSeleccionarRuta.setText(mensInter.get("Seleccionar carpeta")); // Añadir esta clave a los properties
    }


    private void seleccionarRuta() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Seleccione la carpeta de almacenamiento");
        int opcion = fileChooser.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            rutaAlmacenamiento = fileChooser.getSelectedFile().getPath();
            btnSeleccionarRuta.setToolTipText("Ruta actual: " + rutaAlmacenamiento);
        }
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

    private void almacenamientoComboBox() {
        cbxTipoAlmacenamiento.setModel(new DefaultComboBoxModel<>(TipoAlmacenamiento.values()));
        cbxTipoAlmacenamiento.setSelectedItem(TipoAlmacenamiento.MEMORIA);
        btnSeleccionarRuta.setEnabled(false);
        lblRutaArchivos.setEnabled(false);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public TipoAlmacenamiento getTipoAlmacenamientoSeleccionado() {
        return (TipoAlmacenamiento) cbxTipoAlmacenamiento.getSelectedItem();
    }

    public String getRutaAlmacenamiento() {
        return rutaAlmacenamiento;
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    public JTextField getTxtUsuario() {
        return txtUsuario;
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