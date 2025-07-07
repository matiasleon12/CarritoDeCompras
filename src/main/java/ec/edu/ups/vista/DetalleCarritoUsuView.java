package ec.edu.ups.vista;

import ec.edu.ups.modelo.ItemCarrito;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DetalleCarritoUsuView extends JInternalFrame {
    private JPanel panelPrincipal;
    private JTable tablaDetalles;
    private JButton btnEliminar;
    private JButton btnModificar;
    private JLabel tituloDetalle;

    private DefaultTableModel modelo;
    private MensajeInternacionalizacionHandler mih;

    public DetalleCarritoUsuView() {
        super("Detalle del carrito", true, true, false, true);
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        setSize(500, 400);

        setIconoEscalado(btnEliminar, "imagenes/icono_eliminar.png", 25, 25);
        setIconoEscalado(btnModificar, "imagenes/modificarDatos.png", 25, 25);

        modelo = new DefaultTableModel();
        Object[] columnas = {"Codigo", "Nombre", "Precio", "Cantidad", "Precio Total"};
        modelo.setColumnIdentifiers(columnas);
        tablaDetalles.setModel(modelo);
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
        setTitle(mih.get("detalleC.usuario.titulo"));
        tituloDetalle.setText(mih.get("detalleC.usuario.titulo"));
        btnEliminar.setText(mih.get("eliminarP.btnEliminar"));
        btnModificar.setText(mih.get("modificarP.btnModificar"));

        modelo.setColumnIdentifiers(new Object[]{
                mih.get("agregarP.txtCodigo"),
                mih.get("agregarP.txtNombre"),
                mih.get("agregarP.txtPrecio"),
                mih.get("carrito.colCantidad"),
                mih.get("listarC.colPrecio")
        });
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    public void cargarDatos(List<ItemCarrito> items) {
        modelo.setRowCount(0);
        for (ItemCarrito it : items) {
            modelo.addRow(new Object[]{
                    it.getProducto().getCodigo(),
                    it.getProducto().getNombre(),
                    String.format("%.2f", it.getProducto().getPrecio()),
                    it.getCantidad(),
                    String.format("%.2f", it.getCantidad() * it.getProducto().getPrecio())
            });
        }
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    //getters y setters
    public JLabel getTituloDetalle() {
        return tituloDetalle;
    }

    public void setTituloDetalle(JLabel tituloDetalle) {
        this.tituloDetalle = tituloDetalle;
    }

    public JButton getBtnModificar() {
        return btnModificar;
    }

    public void setBtnModificar(JButton btnModificar) {
        this.btnModificar = btnModificar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public void setBtnEliminar(JButton btnEliminar) {
        this.btnEliminar = btnEliminar;
    }

    public JTable getTablaDetalles() {
        return tablaDetalles;
    }

    public void setTablaDetalles(JTable tablaDetalles) {
        this.tablaDetalles = tablaDetalles;
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    public void setPanelPrincipal(JPanel panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
    }
}
