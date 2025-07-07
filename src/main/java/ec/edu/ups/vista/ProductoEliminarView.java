package ec.edu.ups.vista;

import ec.edu.ups.controlador.ProductoController;
import ec.edu.ups.modelo.Producto;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductoEliminarView extends JInternalFrame {
    private JPanel panelPrincipal;
    private JTable tblProductos;
    private JTextField txtCodigoEliminar;
    private JButton eliminarButton;
    private JButton buscarButton;
    private DefaultTableModel modelo;
    private JLabel txtTitulo;
    private JTextField campoNombre;
    private JLabel lblNombre;
    private JLabel lblCodigo;

    private final ProductoController productoC;
    private MensajeInternacionalizacionHandler mensInter;

    public ProductoEliminarView(ProductoController productoC) {
        this.productoC = productoC;
        setContentPane(panelPrincipal);
        setTitle("Eliminar Productos");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(500, 400);
        setClosable(true);
        setIconifiable(true);
        setResizable(true);

        setIconoEscalado(buscarButton, "imagenes/imagen_iconoBuscar - Copy.png", 25, 25);
        setIconoEscalado(eliminarButton, "imagenes/icono_eliminar.png", 25, 25);

        modelo = new DefaultTableModel();
        Object[] columnas = {"Codigo", "Nombre", "Precio"};
        modelo.setColumnIdentifiers(columnas);
        tblProductos.setModel(modelo);
    }

    public void setMensajeHandler(MensajeInternacionalizacionHandler mensInter) {
        this.mensInter = mensInter;
        actualizarTextos();
    }

    private void actualizarTextos() {
        setTitle(mensInter.get("eliminarP.titulo"));
        txtTitulo.setText(mensInter.get("eliminarP.titulo"));
        lblNombre.setText(mensInter.get("eliminarP.txtNombre"));
        lblCodigo.setText(mensInter.get("eliminarP.txtCodigo"));
        buscarButton.setText(mensInter.get("eliminarP.btnBuscar"));
        eliminarButton.setText(mensInter.get("eliminarP.btnEliminar"));

        modelo.setColumnIdentifiers(new Object[]{
                mensInter.get("eliminarP.colCodigo"),
                mensInter.get("eliminarP.colNombre"),
                mensInter.get("eliminarP.colPrecio")
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

    public void cargarTabla(List<Producto> lista) {
        modelo.setRowCount(0);
        for (Producto p : lista) {
            modelo.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getPrecio()});
        }
    }

    public void removerFila(int codigo) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if (modelo.getValueAt(i, 0).toString().equals(String.valueOf(codigo))) {
                modelo.removeRow(i);
                break;
            }
        }
    }

    //Getters y setters
    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public JTextField getCampoNombre() {
        return campoNombre;
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    public void setPanelPrincipal(JPanel panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
    }

    public JTextField getTxtCodigoEliminar() {
        return txtCodigoEliminar;
    }

    public JButton getEliminarButton() {
        return eliminarButton;
    }

    public JButton getBuscarButton() {
        return buscarButton;
    }

}