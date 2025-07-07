package ec.edu.ups.vista;

import ec.edu.ups.modelo.Carrito;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ListarCarritosView extends JInternalFrame {
    private JPanel panelPrincipal;
    private JTable table1;
    private JButton btnListar;
    private JButton btnDetalle;
    private JButton btnEliminar;
    private JLabel tituloListaC;
    private DefaultTableModel modelo;

    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd/MM/yyyy");

    private MensajeInternacionalizacionHandler mih;

    public ListarCarritosView() {
        super("Listado Carritos", true, true, false, true);
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        setSize(500, 400);

        setIconoEscalado(btnListar, "imagenes/icono_listar.png", 25, 25);
        setIconoEscalado(btnDetalle, "imagenes/icono_detalles.png", 25, 25);
        setIconoEscalado(btnEliminar, "imagenes/icono_eliminar.png", 25, 25);

        modelo = new DefaultTableModel();
        Object[] columnas = {"Código", "Fecha de creación", "Precio total"};
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
        setTitle(mih.get("listarC.usuario.titulo"));
        tituloListaC.setText(mih.get("listarC.usuario.titulo"));
        btnListar.setText(mih.get("listarP.btnListar"));
        btnDetalle.setText(mih.get("listarC.usuario.detalle"));
        btnEliminar.setText(mih.get("eliminarP.btnEliminar"));

        modelo.setColumnIdentifiers(new Object[]{
                mih.get("agregarP.txtCodigo"),
                mih.get("listarC.colFecha"),
                mih.get("listarC.colPrecio")
        });
        //panelPrincipal.revalidate();
        //panelPrincipal.repaint();
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
    public JLabel getTituloListaC() {
        return tituloListaC;
    }

    public void setTituloListaC(JLabel tituloListaC) {
        this.tituloListaC = tituloListaC;
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

    public JButton getBtnDetalle() {
        return btnDetalle;
    }

    public void setBtnDetalle(JButton btnDetalle) {
        this.btnDetalle = btnDetalle;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public void setBtnEliminar(JButton btnEliminar) {
        this.btnEliminar = btnEliminar;
    }
}