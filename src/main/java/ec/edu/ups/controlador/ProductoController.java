package ec.edu.ups.controlador;

import ec.edu.ups.dao.ProductoDAO;
import ec.edu.ups.modelo.Producto;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;
import ec.edu.ups.vista.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public class ProductoController {

    private final ProductoDAO productoDAO;
    private final CarritoAnadirView carritoView;

    private final ProductoAnadirView productoAnadirView;
    private final ProductoListaView productoListaView;

    private ProductoEliminarView eliminarProductoView;
    private ProductoActualizarView productoActualizarView;

    private final MensajeInternacionalizacionHandler mih;

    public ProductoController(MensajeInternacionalizacionHandler mih, ProductoDAO productoDAO, ProductoAnadirView productoAnadirView,
                              ProductoListaView productoListaView, CarritoAnadirView carritoView) {
        this.mih = mih;
        this.productoDAO = productoDAO;
        this.productoAnadirView = productoAnadirView;
        this.productoListaView = productoListaView;
        this.carritoView = carritoView;
        this.configurarEventosEnVistas();
    }

    //para la ventana de REGISTRAR producto
    private void configurarEventosEnVistas(){
        productoAnadirView.getBtnAceptar().addActionListener(e -> guardarProducto());

        productoListaView.getBtnBuscar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProducto();
            }
        });

        productoListaView.getBtnListar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarProductos();
            }
        });

        /*productoListaView.getb.addActionListener(e -> {
            productoListaView.getTxtBuscar().setText("");
            // vacía la tabla
            productoListaView.cargarDatos(Collections.emptyList());
        });*/

        carritoView.getBtnBuscar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProductoPorCodigo();
            }
        });
    }

    // EVENTOS Eliminar Producto View
    private void configurarEventosEliminar() {
        eliminarProductoView.getBuscarButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProductoPorNombreParaEliminar();
            }
        });

        eliminarProductoView.getEliminarButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarYEliminarProducto();
            }
        });
    }

    // EVENTOS Modificar Producto
    private void configurarEventosModificar() {
        productoActualizarView.getBuscarButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProductoPorNombreParaModificar();
            }
        });

        productoActualizarView.getActualizarButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarYModificarProducto();
            }
        });
    }

    //REGISTRAR PRODUCTO
    private void guardarProducto() {
        int codigo;
        try {
            codigo = Integer.parseInt(productoAnadirView.getTxtCodigo().getText().trim());
        } catch (NumberFormatException ex) {// cuando la persona ingresa cadena de numeros no valida
            productoAnadirView.mostrarMensaje(mih.get("producto.mensajeError.codigo"));
            return;
        }
        String nombre = productoAnadirView.getTxtNombre().getText().trim();
        if (nombre.isEmpty()) {
            productoAnadirView.mostrarMensaje(mih.get("producto.mensajeError.nombre"));
            return;
        }
        double precio;
        try {
            precio = Double.parseDouble(productoAnadirView.getTxtPrecio().getText().trim());
        } catch (NumberFormatException ex) {
            productoAnadirView.mostrarMensaje(mih.get("producto.mensajeError.precio"));
            return;
        }

        //validamos que no se ingresen codigos de productos iguales
        if (productoDAO.buscarPorCodigo(codigo) != null) {
            String msg = MessageFormat.format(mih.get("producto.mensajeError.codigoExistente"), codigo);
            productoAnadirView.mostrarMensaje(msg);
            return;
        }

        for (Producto p : productoDAO.listarTodos()) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                String msg = MessageFormat.format(
                        mih.get("producto.mensajeError.nombreExistente"),
                        nombre
                );
                productoAnadirView.mostrarMensaje(msg);
                return;
            }
        }
        // creamos el producto y se actualiza la vista de productos
        productoDAO.crear(new Producto(codigo, nombre, precio));
        productoAnadirView.mostrarMensaje(mih.get("producto.mensaje.guardado"));
        productoAnadirView.limpiarCampos();
        productoAnadirView.mostrarProductos(productoDAO.listarTodos());
    }

    //usamos el metodo para buscar el producto que ingresa el usuario
    //en ventana LISTAR PRODUCTO
    private void buscarProducto() {
        // obtenemos lo ingresado por el usuario
        String nombre = productoListaView.getTxtBuscar().getText().trim();

        if (nombre.isEmpty()) {
            productoListaView.mostrarMensaje(mih.get("producto.mensajeError.campoVacio"));
            productoListaView.cargarDatos(Collections.emptyList());
            return;
        }
        // Llamo al DAO
        List<Producto> productos = productoDAO.buscarPorNombre(nombre);

        // cuando no hay el producto buscado muestro mensaje
        if (productos.isEmpty()) {
            productoListaView.mostrarMensaje(mih.get("producto.mensajeError.productoNoEncontrado"));
        }
        productoListaView.cargarDatos(productos);
    }

    //listar productos en Listar Productos View
    private void listarProductos() {
        List<Producto> productos = productoDAO.listarTodos();
        productoListaView.cargarDatos(productos);
    }

    //Eliminar View
    private void buscarProductoPorNombreParaEliminar() {
        String nombre = eliminarProductoView.getCampoNombre().getText().trim();

        if (nombre.isEmpty()) {
            eliminarProductoView.mostrarMensaje(mih.get("producto.mensajeError.campoVacio"));
            return;
        }
        List<Producto> productos = productoDAO.buscarPorNombre(nombre);

        if (productos.isEmpty()) {
            eliminarProductoView.mostrarMensaje(mih.get("producto.mensajeError.productoNoEncontrado"));
        }
        eliminarProductoView.cargarTabla(productos);
    }

    // ventana para confirmar si se desea eliminar el producto
    private void confirmarYEliminarProducto() {
        String codTxt = eliminarProductoView.getTxtCodigoEliminar().getText().trim();

        if (!codTxt.matches("\\d+")) {
            eliminarProductoView.mostrarMensaje(mih.get("producto.mensajeError"));
            return;
        }

        int codigo = Integer.parseInt(codTxt);
        int opcion = JOptionPane.showConfirmDialog(eliminarProductoView,
                MessageFormat.format(mih.get("producto.mensajeConfirmar.eliminar"),codigo),
                mih.get("producto.titulo.mensajeAccion"),
                JOptionPane.YES_NO_OPTION
        );

        if (opcion == JOptionPane.YES_OPTION) {
            boolean eliminado = eliminarProducto(codigo);
            if (eliminado) {
                eliminarProductoView.removerFila(codigo);
                eliminarProductoView.mostrarMensaje(mih.get("producto.mensajeExito.eliminar"));
            } else {
                eliminarProductoView.mostrarMensaje(mih.get("producto.mensajeError.eliminar"));
            }
        }
    }

    // Metodo de eliminación directa desde DAO
    private boolean eliminarProducto(int codigo) {
        Producto producto = productoDAO.buscarPorCodigo(codigo);
        if (producto != null) {
            productoDAO.eliminar(codigo);
            return true;
        }
        return false;
    }

    // Metodo que realiza la modificación real en el DAO
    private boolean modificarProductoPorNombre(String nombreOriginal, String nuevoNombre, double nuevoPrecio) {
        List<Producto> productos = productoDAO.buscarPorNombre(nombreOriginal);
        if (!productos.isEmpty()) {
            Producto p = productos.get(0); // toma el primero encontrado
            p.setNombre(nuevoNombre);
            p.setPrecio(nuevoPrecio);
            productoDAO.actualizar(p);
            return true;
        }
        return false;
    }

    // Metodo para buscar productos por nombre y mostrarlos en la tabla
    private void buscarProductoPorNombreParaModificar() {
        String nombre = productoActualizarView.getTextField1().getText().trim();

        if (nombre.isEmpty()) {
            productoActualizarView.mostrarMensaje(mih.get("producto.mensajeError.campoVacio"));
            return;
        }
        List<Producto> productos = productoDAO.buscarPorNombre(nombre);

        if (productos.isEmpty()) {
            productoActualizarView.mostrarMensaje(mih.get("producto.mensajeError.productoNoEncontrado"));
        }
        productoActualizarView.cargarTabla(productos);
    }

    //Metodo para confirmar y realizar la modificación del producto
    private void confirmarYModificarProducto() {
        String nombreBuscar = productoActualizarView.getTextField1().getText().trim();
        String nuevoNombre = productoActualizarView.getTextField2().getText().trim();
        String nuevoPrecioTexto = productoActualizarView.getTextField3().getText().trim();

        if (nombreBuscar.isEmpty() || nuevoNombre.isEmpty() || nuevoPrecioTexto.isEmpty()) {
            productoActualizarView.mostrarMensaje(mih.get("producto.mensajeError.campoModificar"));
            return;
        }

        //Validar formato de precio
        if (!nuevoPrecioTexto.matches("\\d+(\\.\\d+)?")) {
            productoActualizarView.mostrarMensaje(mih.get("producto.mensajeError.precio"));
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(productoActualizarView,mih.get("producto.mensajeConfirmar.modificar"),
                mih.get("producto.titulo.mensajeAccion"),JOptionPane.YES_NO_OPTION);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        if (opcion == JOptionPane.YES_OPTION) {
            double nuevoPrecio = Double.parseDouble(nuevoPrecioTexto);
            boolean modificado = modificarProductoPorNombre(nombreBuscar, nuevoNombre, nuevoPrecio);

            if (modificado) {
                productoActualizarView.mostrarMensaje(mih.get("producto.mensajeExito.modificar"));
            } else {
                productoActualizarView.mostrarMensaje(mih.get("producto.mensajeError.modificar"));
            }
        }
    }


    private void limpiarCamposModificar() {
        productoActualizarView.getTextField1().setText("");
        productoActualizarView.getTextField2().setText("");
        productoActualizarView.getTextField3().setText("");
        //productoActualizarView.get().setRowCount(0);
    }

    private void buscarProductoPorCodigo() {
        int codigo = Integer.parseInt(carritoView.getTxtCodigo().getText());
        Producto producto = productoDAO.buscarPorCodigo(codigo);
        if (producto == null) {
            carritoView.mostrarMensaje("No se encontro el producto");
            carritoView.getTxtNombre().setText("");
            carritoView.getTxtPrecio().setText("");
        } else {
            carritoView.getTxtNombre().setText(producto.getNombre());
            carritoView.getTxtPrecio().setText(String.valueOf(producto.getPrecio()));
        }
    }

    public void setProductoActualizarView(ProductoActualizarView productoActualizarView) {
        this.productoActualizarView = productoActualizarView;
        configurarEventosModificar(); // Conecta los botones
    }

    public void setEliminarProductoView(ProductoEliminarView eliminarProductoView) {
        this.eliminarProductoView = eliminarProductoView;
        configurarEventosEliminar(); // Conecta los botones
    }
}