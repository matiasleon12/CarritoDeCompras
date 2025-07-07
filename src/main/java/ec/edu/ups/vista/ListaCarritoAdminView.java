package ec.edu.ups.vista;

import ec.edu.ups.modelo.Carrito;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.List;

public class ListaCarritoAdminView extends JInternalFrame {
    private JPanel panelPrincipal;
    private JTable table1;
    private JLabel lblTituloListar;
    private JButton btnEliminarCarrito;
    private DefaultTableModel modelo;

    private MensajeInternacionalizacionHandler mih;

    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd/MM/yyyy");

    public ListaCarritoAdminView() {
        super("Listado Carritos", true, true, false, true);
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        setSize(500, 400);

        modelo = new DefaultTableModel();
        Object[] columnas = {"Código", "Fecha de creación", "Precio total"};
        modelo.setColumnIdentifiers(columnas);
        table1.setModel(modelo);
    }

    public void setMensajeHandler(MensajeInternacionalizacionHandler mih) {
        this.mih = mih;
        actualizarTextos();
    }

    /** Actualiza título, botón y encabezados de tabla */
    private void actualizarTextos() {
        setTitle(mih.get("detalleC.admin.titulo"));
        lblTituloListar.setText(mih.get("detalleC.admin.titulo"));

        modelo.setColumnIdentifiers(new Object[]{
                mih.get("agregarP.txtCodigo"),
                mih.get("listarC.colFecha"),
                mih.get("listarC.colPrecio")
        });

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    public void cargarDatos(List<Carrito> listaCarritos) {
        modelo.setRowCount(0);
        for (Carrito c : listaCarritos) {
            String fechaStr = SDF.format(c.getFechaCreacion().getTime());
            String totalStr = String.format("%.2f", c.calcularTotal());

            modelo.addRow(new Object[]{
                    c.getCodigo(),
                    fechaStr,
                    totalStr
            });
        }
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

    public JTable getTable1() {
        return table1;
    }

    public void setTable1(JTable table1) {
        this.table1 = table1;
    }

    public JLabel getLblTituloListar() {
        return lblTituloListar;
    }

    public void setLblTituloListar(JLabel lblTituloListar) {
        this.lblTituloListar = lblTituloListar;
    }

    public JButton getBtnEliminarCarrito() {
        return btnEliminarCarrito;
    }

    public void setBtnEliminarCarrito(JButton btnEliminarCarrito) {
        this.btnEliminarCarrito = btnEliminarCarrito;
    }
}
