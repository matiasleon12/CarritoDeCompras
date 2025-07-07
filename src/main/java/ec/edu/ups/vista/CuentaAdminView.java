package ec.edu.ups.vista;

import ec.edu.ups.util.MensajeInternacionalizacionHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CuentaAdminView extends JInternalFrame {
    private JPanel panelPrincipal;
    private JTable table1;
    private JButton btnListar;
    private JButton btnEliminar;
    private JTextField textField1;
    private JButton btnBuscar;
    private JButton btnModificarContra;
    private JButton btnModificarNom;
    private JButton btnCerrarSesion;
    private JLabel lblNombre;
    private JLabel tituloVentana;
    private JButton crearUsuarioButton;

    private DefaultTableModel modelo;

    private MensajeInternacionalizacionHandler mih;

    public CuentaAdminView() {
        super("Cuenta administrador", true, true, false, true);
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        setSize(850, 500);

        setIconoEscalado(btnEliminar, "imagenes/icono_eliminar.png", 25, 25);
        setIconoEscalado(btnBuscar, "imagenes/imagen_iconoBuscar - Copy.png", 25, 25);
        setIconoEscalado(btnModificarContra, "imagenes/modificarDatos.png", 25, 25);
        setIconoEscalado(btnModificarNom, "imagenes/modificarDatos.png", 25, 25);
        setIconoEscalado(btnCerrarSesion, "imagenes/cerrarSesion.png", 25, 25);
        setIconoEscalado(btnListar, "imagenes/icono_listar.png", 25, 25);

        modelo = new DefaultTableModel();
        Object[] columnas = {"Usuario"};
        modelo.setColumnIdentifiers(columnas);
        table1.setModel(modelo);
    }

    private void setIconoEscalado(JButton boton, String ruta, int ancho, int alto) {
        try {
            java.net.URL url = getClass().getClassLoader().getResource(ruta);
            if (url != null) {
                Image imagen = new ImageIcon(url).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                boton.setIcon(new ImageIcon(imagen));
                boton.setHorizontalTextPosition(SwingConstants.RIGHT);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen" + ruta + " → " + e.getMessage());
        }
    }

    public void setMensajeHandler(MensajeInternacionalizacionHandler mih) {
        this.mih = mih;
        actualizarTextos();
    }

    private void actualizarTextos() {
        setTitle(mih.get("sesionAdmin.titulo"));
        tituloVentana.setText(mih.get("sesionAdmin.titulo"));
        lblNombre.setText(mih.get("agregarP.txtNombre"));
        btnListar.setText(mih.get("listarP.btnListar"));
        btnBuscar.setText(mih.get("listarP.btnBuscar"));
        btnEliminar.setText(mih.get("eliminarP.btnEliminar"));
        btnModificarNom.setText(mih.get("sesionAdmin.btn.modifNom"));
        btnModificarContra.setText(mih.get("sesionAdmin.btn.modifContra"));
        btnCerrarSesion.setText(mih.get("sesionAdmin.btn.cerrarS"));

        DefaultTableModel modelo = (DefaultTableModel) table1.getModel();
        modelo.setColumnIdentifiers(new Object[]{
                mih.get("login.txtUsuario")
        });

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    public void cargarUsuarios(java.util.List<String> usuarios) {
        modelo.setRowCount(0);
        for (String nombre : usuarios) {
            modelo.addRow(new Object[]{ nombre });
        }
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    //getters y setters
    public JLabel getLblNombre() {
        return lblNombre;
    }

    public void setLblNombre(JLabel lblNombre) {
        this.lblNombre = lblNombre;
    }

    public JLabel getTituloVentana() {
        return tituloVentana;
    }

    public void setTituloVentana(JLabel tituloVentana) {
        this.tituloVentana = tituloVentana;
    }

    public JButton getBtnCerrarSesion() {
        return btnCerrarSesion;
    }

    public void setBtnCerrarSesion(JButton btnCerrarSesion) {
        this.btnCerrarSesion = btnCerrarSesion;
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    public void setPanelPrincipal(JPanel panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
    }

    public JTable getTable1() {
        return table1;
    }

    public void setTable1(JTable table1) {
        this.table1 = table1;
    }

    public JButton getBtnListar() {
        return btnListar;
    }

    public void setBtnListar(JButton btnListar) {
        this.btnListar = btnListar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public void setBtnEliminar(JButton btnEliminar) {
        this.btnEliminar = btnEliminar;
    }

    public JTextField getTextField1() {
        return textField1;
    }

    public void setTextField1(JTextField textField1) {
        this.textField1 = textField1;
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public void setBtnBuscar(JButton btnBuscar) {
        this.btnBuscar = btnBuscar;
    }

    public JButton getBtnModificarContra() {
        return btnModificarContra;
    }

    public void setBtnModificarContra(JButton btnModificarContra) {
        this.btnModificarContra = btnModificarContra;
    }

    public JButton getBtnModificarNom() {
        return btnModificarNom;
    }

    public void setBtnModificarNom(JButton btnModificarNom) {
        this.btnModificarNom = btnModificarNom;
    }
}
