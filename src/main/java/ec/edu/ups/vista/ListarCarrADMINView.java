package ec.edu.ups.vista;

import ec.edu.ups.util.MensajeInternacionalizacionHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ListarCarrADMINView extends JInternalFrame {
    private JPanel panelPrincipal;
    private JTextField txtNombre;
    private JButton btnBuscar;
    private JPanel panelSecundario;
    private JTable table1;
    private JButton btnCarrito;
    private JLabel lblNombre;
    private JLabel lblTituloLista;

    private DefaultTableModel modelo;
    private MensajeInternacionalizacionHandler mih;

    public ListarCarrADMINView() {
        super("Listado Carritos Admin", true, true, false, true);
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        setSize(400, 400);

        setIconoEscalado(btnBuscar, "imagenes/imagen_iconoBuscar - Copy.png", 25, 25);
        setIconoEscalado(btnCarrito, "imagenes/icono_detalles.png", 25, 25);

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
        setTitle(mih.get("listaU.admin.titulo"));
        lblTituloLista.setText(mih.get("listaU.admin.titulo"));
        lblNombre.setText(mih.get("agregarP.txtNombre"));
        txtNombre.setToolTipText(mih.get("agregarP.txtNombre"));
        btnBuscar.setText(mih.get("listarP.btnBuscar"));
        btnCarrito.setText(mih.get("detalleC.usuario.titulo"));

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
    public JLabel getLblTituloLista() {
        return lblTituloLista;
    }

    public void setLblTituloLista(JLabel lblTituloLista) {
        this.lblTituloLista = lblTituloLista;
    }

    public JLabel getLblNombre() {
        return lblNombre;
    }

    public void setLblNombre(JLabel lblNombre) {
        this.lblNombre = lblNombre;
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    public void setPanelPrincipal(JPanel panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
    }

    public JButton getBtnCarrito() {
        return btnCarrito;
    }

    public void setBtnCarrito(JButton btnCarrito) {
        this.btnCarrito = btnCarrito;
    }

    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public void setTxtNombre(JTextField txtNombre) {
        this.txtNombre = txtNombre;
    }

    public JTable getTable1() {
        return table1;
    }

    public void setTable1(JTable table1) {
        this.table1 = table1;
    }

    public JPanel getPanelSecundario() {
        return panelSecundario;
    }

    public void setPanelSecundario(JPanel panelSecundario) {
        this.panelSecundario = panelSecundario;
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public void setBtnBuscar(JButton btnBuscar) {
        this.btnBuscar = btnBuscar;
    }
}
