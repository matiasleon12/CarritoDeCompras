package ec.edu.ups.controlador;

import ec.edu.ups.dao.CarritoDAO;
import ec.edu.ups.dao.ProductoDAO;
import ec.edu.ups.dao.UsuarioDAO;
import ec.edu.ups.modelo.Carrito;
import ec.edu.ups.modelo.ItemCarrito;
import ec.edu.ups.modelo.Producto;
import ec.edu.ups.modelo.Usuario;
import ec.edu.ups.util.Formatear;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;
import ec.edu.ups.vista.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarritoController {

    private final CarritoDAO carritoDAO;
    private final UsuarioDAO usuarioDAO;
    private final CarritoAnadirView carritoView;
    private final ProductoDAO productoDAO;
    private final Usuario usuarioActual;
    private Carrito carrito;

    private final MensajeInternacionalizacionHandler mensInter;

    public CarritoController(MensajeInternacionalizacionHandler mensInter, CarritoDAO carritoDAO, UsuarioDAO usuarioDAO, CarritoAnadirView carritoView, ProductoDAO productoDAO,
                             Usuario usuarioActual) {
        this.mensInter = mensInter;
        this.carritoDAO = carritoDAO;
        this.usuarioDAO = usuarioDAO;
        this.carritoView = carritoView;
        this.productoDAO = productoDAO;
        this.usuarioActual = usuarioActual;
        this.carrito = new Carrito();
        configurarEventosEnVista();
    }

    private void configurarEventosEnVista() {
        carritoView.getBtnAnadir().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                anadirProducto();
            }
        });

        carritoView.getBtnGuardar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                guardarCarrito();
            }
        });

        carritoView.getBtnLimpiar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarCarrito();
            }
        });

        carritoView.getBtnLimpiar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        carritoView.getBtnLimpiar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarItemDelCarrito();
            }
        });

        /*carritoView.getBtnActualizar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarVentanaModificarCantidad();
            }
        });*/
    }

    private void guardarCarrito() {
        //asignar el usuario al carrito
        carrito.setUsuario(usuarioActual);
        carritoDAO.crear(carrito);
        carritoView.mostrarMensaje(mensInter.get("carrito.mensajeConfirmar"));
        // limpiamos
        DefaultTableModel m = (DefaultTableModel) carritoView.getTblProductos().getModel();
        m.setRowCount(0);
        carrito = new Carrito();
    }

    //se agregan los productos y su cantidad
    private void anadirProducto() {
        int codigo = Integer.parseInt(carritoView.getTxtCodigo().getText());
        Producto producto = productoDAO.buscarPorCodigo(codigo);
        int cantidad = Integer.parseInt(carritoView.getCbxCantidad().getSelectedItem().toString());
        carrito.agregarProducto(producto, cantidad);

        cargarProductos();
        mostrarTotales();
    }

    private void cargarProductos() {
        List<ItemCarrito> items = carrito.obtenerItems();
        DefaultTableModel modelo = (DefaultTableModel) carritoView.getTblProductos().getModel();
        modelo.setRowCount(0);  // limpiamos antes de recargar

        Locale locale = mensInter.getLocale();

        for (ItemCarrito item : items) {
            double precio = item.getProducto().getPrecio();
            int cantidad = item.getCantidad();
            double subtotal = precio * cantidad;

            modelo.addRow(new Object[]{
                    item.getProducto().getCodigo(),
                    item.getProducto().getNombre(),
                    Formatear.formatearMoneda(precio, locale),cantidad,
                    Formatear.formatearMoneda(subtotal, locale)
            });
        }
    }

    private void mostrarTotales() {
        Locale locale = mensInter.getLocale();
        carritoView.getTxtSubtotal().setText(Formatear.formatearMoneda(carrito.calcularSubtotal(), locale));
        carritoView.getTxtIva().setText(Formatear.formatearMoneda(carrito.calcularIVA(), locale));
        carritoView.getTxtTotal().setText(Formatear.formatearMoneda(carrito.calcularTotal(), locale));
    }

    private void cancelarCarrito() {
        carrito = new Carrito(); //nuevo carrito vacio
        limpiarCampos();
        DefaultTableModel modelo = (DefaultTableModel) carritoView.getTblProductos().getModel();
        modelo.setRowCount(0); //Limpia tabla
    }

    private void limpiarCampos() {
        carritoView.getTxtCodigo().setText("");
        carritoView.getTxtNombre().setText("");
        carritoView.getTxtPrecio().setText("");
        carritoView.getTxtSubtotal().setText("");
        carritoView.getTxtIva().setText("");
        carritoView.getTxtTotal().setText("");
        carritoView.getCbxCantidad().setSelectedIndex(0);
    }

    private void eliminarItemDelCarrito() {
        JTable tabla = carritoView.getTblProductos();
        int fila = tabla.getSelectedRow();

        if (fila >= 0) {
            int confirmacion = JOptionPane.showConfirmDialog(carritoView,
                    mensInter.get("carrito.mensajeConfirmar.eliminar"), mensInter.get("carrito.titulo"),
                    JOptionPane.YES_NO_OPTION
            );
            if (confirmacion == JOptionPane.YES_OPTION) {
                int codigo = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
                carrito.eliminarItem(codigo);
                actualizarTabla();
                mostrarTotales();
                carritoView.mostrarMensaje(
                        mensInter.get("carrito.mensajeItemEliminado")
                );
            }
        } else {
            carritoView.mostrarMensaje(mensInter.get("carrito.mensaje.ItemNOSeleccionado"));
        }
    }

    private void actualizarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) carritoView.getTblProductos().getModel();
        modelo.setRowCount(0); //Limpiar tabla antes de volver a cargar

        List<ItemCarrito> items = carrito.obtenerItems();
        for (ItemCarrito item : items) {
            modelo.addRow(new Object[]{
                    item.getProducto().getCodigo(),
                    item.getProducto().getNombre(),
                    item.getProducto().getPrecio(),
                    item.getCantidad(),
                    item.getCantidad() * item.getProducto().getPrecio()
            });
        }
    }

    private void mostrarVentanaModificarCantidad() {
        JTable tabla = carritoView.getTblProductos();
        int fila = tabla.getSelectedRow();

        if (fila >= 0) {
            int codigoProducto = Integer.parseInt(tabla.getValueAt(fila, 0).toString());

            //Crear ventana para modificar cantidad del producto
            JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(carritoView),
                    mensInter.get("carrito.ventanaModificar.titulo"),true);

            dialogo.setSize(250, 150);
            dialogo.setLocationRelativeTo(carritoView);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel label = new JLabel(mensInter.get("carrito.txtCantidad.modificar"));

            //ComboBox con num del 1 al 20
            JComboBox<Integer> comboCantidad = new JComboBox<>();
            for (int i = 1; i <= 20; i++) {
                comboCantidad.addItem(i);
            }

            JButton btnAceptar = new JButton(mensInter.get("carrito.modificar.btnActualizar"));

            btnAceptar.addActionListener(ev -> {
                int nuevaCantidad = (int) comboCantidad.getSelectedItem();
                carrito.actualizarCantidad(codigoProducto, nuevaCantidad);
                actualizarTabla();
                mostrarTotales();
                dialogo.dispose(); //cerrar ventana
                carritoView.mostrarMensaje(mensInter.get("carrito.mensajeExito.modificar"));
            });
            panel.add(label);
            panel.add(comboCantidad);
            panel.add(Box.createVerticalStrut(10));
            panel.add(btnAceptar);

            dialogo.add(panel);
            dialogo.setVisible(true);
        } else {
            carritoView.mostrarMensaje(mensInter.get("carrito.mensaje.ItemNOSeleccionado"));
        }
    }

    //LISTAR CARRITOS DEL USUARIO,
    public void vincularListarCarritos(ListarCarritosView view, JDesktopPane desktop) {
        configurarEventoListar(view);
        configurarEventoDetalle(view, desktop);
        configurarEventoEliminar(view);
    }

    private void configurarEventoListar(ListarCarritosView view) {
        view.getBtnListar().setText(mensInter.get("listarP.btnListar"));
        view.getBtnListar().addActionListener(evt -> {
            List<Carrito> lista = carritoDAO.listarTodos();
            if (lista.isEmpty()) {
                view.mostrarMensaje(mensInter.get("listarC.mensajeError.vacio"));
            } else {
                view.cargarDatos(lista);
            }
        });
    }

    private void configurarEventoDetalle(ListarCarritosView view, JDesktopPane desktop) {
        view.getBtnDetalle().setText(mensInter.get("listarC.usuario.detalle"));
        view.getBtnDetalle().addActionListener(evt -> {
            int fila = view.getTable1().getSelectedRow();
            if (fila < 0) {
                view.mostrarMensaje(mensInter.get("listarC.mensajeSelecc"));
                return;
            }
            int codigo = (int) view.getTable1().getValueAt(fila, 0);
            Carrito c = carritoDAO.buscarPorCodigo(codigo);
            if (c == null) {
                view.mostrarMensaje(mensInter.get("listarC.mensajeError.noEnc"));
                return;
            }
            DetalleCarritoUsuView detView = new DetalleCarritoUsuView();
            detView.setMensajeHandler(mensInter);
            desktop.add(detView);
            detView.setVisible(true);
            detView.cargarDatos(c.obtenerItems());
            vincularDetalle(detView, c);
        });
    }
    //ELIMINAR CARRITO
    private void configurarEventoEliminar(ListarCarritosView view) {
        view.getBtnEliminar().setText(mensInter.get("eliminarP.btnEliminar"));
        view.getBtnEliminar().addActionListener(evt -> {
            int fila = view.getTable1().getSelectedRow();
            if (fila < 0) {
                view.mostrarMensaje(mensInter.get("listarC.mensajeEliminar"));
                return;
            }
            int codigo = (int) view.getTable1().getValueAt(fila, 0);
            carritoDAO.eliminar(codigo);
            view.mostrarMensaje(mensInter.get("listarC.mensajeExito"));
            //actualiza  la liusta
            List<Carrito> lista = carritoDAO.listarPorUsuario(usuarioActual.getUsername());
            view.cargarDatos(lista);
        });
    }

    public void vincularDetalle(DetalleCarritoUsuView view, Carrito carrito) {
        configurarEventoModificarDetalle(view, carrito);
        configurarEventoEliminarDetalle(view, carrito);
    }

    private void configurarEventoModificarDetalle(DetalleCarritoUsuView view, Carrito carrito) {
        view.getBtnModificar().addActionListener(evt -> {
            int fila = view.getTablaDetalles().getSelectedRow();
            if (fila < 0) {
                view.mostrarMensaje(mensInter.get("detalleC.mensaje.Selecc"));
                return;
            }
            int codigoProd = obtenerCodigoDeDetalle(view, fila);
            String prompt = mensInter.get("carrito.txtCantidad.modificar");
            String input = JOptionPane.showInputDialog(view, prompt);
            if (input != null) {
                procesarNuevaCantidad(input, carrito, view);
            }
        });
    }

    private void configurarEventoEliminarDetalle(DetalleCarritoUsuView view, Carrito carrito) {
        view.getBtnEliminar().addActionListener(evt -> {
            int fila = view.getTablaDetalles().getSelectedRow();
            if (fila < 0) {
                view.mostrarMensaje(mensInter.get("detalleC.mensaje.Selecc"));
                return;
            }
            int codigoProd = obtenerCodigoDeDetalle(view, fila);
            carrito.eliminarItem(codigoProd);
            carritoDAO.limpiar(carrito);
            view.cargarDatos(carrito.obtenerItems());
        });
    }

    private int obtenerCodigoDeDetalle(DetalleCarritoUsuView view, int fila) {
        return (int) view.getTablaDetalles().getValueAt(fila, 0);
    }

    private void procesarNuevaCantidad(String cantidadColocada, Carrito carrito, DetalleCarritoUsuView view) {
        try {
            int nuevaCant = Integer.parseInt(cantidadColocada);
            carrito.actualizarCantidad(obtenerCodigoDeDetalle(view, view.getTablaDetalles().getSelectedRow()), nuevaCant);
            carritoDAO.limpiar(carrito);
            view.cargarDatos(carrito.obtenerItems());
        } catch (NumberFormatException ex) {
            view.mostrarMensaje(mensInter.get("detalleC.mensajeError.cantidad"));
        }
    }

    //CARRITOS DEL ADMINISTRADOR
    public void configurarEventosListarCarritoAdmin(ListarCarrADMINView view, JDesktopPane desktop) {
        cargarUsuariosInicial(view);
        configurarEventoBuscarUsuario(view);
        configurarEventoListarCarrito(view, desktop);
    }

    private void cargarUsuariosInicial(ListarCarrADMINView view) {
        List<Usuario> lista = usuarioDAO.listarTodos();
        List<String> nombres = new ArrayList<>();
        for (Usuario u : lista) {
            nombres.add(u.getUsername());
        }
        view.cargarUsuarios(nombres);
    }

    private void configurarEventoBuscarUsuario(ListarCarrADMINView view) {
        view.getBtnBuscar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = view.getTxtNombre().getText().trim();
                if (nombre.isEmpty()) {
                    view.mostrarMensaje(mensInter.get("listarU.admin.mensajeError.vacio"));
                    return;
                }
                Usuario encontrado = usuarioDAO.buscarPorUsername(nombre);
                if (encontrado == null) {
                    view.mostrarMensaje(mensInter.get("listarU.admin.mensajeError.usuario"));
                    view.cargarUsuarios(new ArrayList<>());
                } else {
                    List<String> uno = new ArrayList<>();
                    uno.add(encontrado.getUsername());
                    view.cargarUsuarios(uno);
                }
            }
        });
    }

    private void configurarEventoListarCarrito(ListarCarrADMINView view, JDesktopPane desktop) {
        view.getBtnCarrito().addActionListener(e -> {
            int fila = view.getTable1().getSelectedRow();
            if (fila < 0) {
                view.mostrarMensaje(mensInter.get("listarU.admin.mensajeSelecc"));
                return;
            }
            String username = view.getTable1().getValueAt(fila, 0).toString();
            List<Carrito> carritos = carritoDAO.listarPorUsuario(username);
            if (carritos.isEmpty()) {
                view.mostrarMensaje(mensInter.get("listarU.admin.mensajeError.noCarrito"));
                return;
            }
            //Se crea la ventana de listar carritos
            ListaCarritoAdminView listaCarriAdmin = new ListaCarritoAdminView();
            listaCarriAdmin.setMensajeHandler(mensInter);
            desktop.add(listaCarriAdmin);
            listaCarriAdmin.cargarDatos(carritos);
            listaCarriAdmin.setVisible(true);
        });
    }
}


