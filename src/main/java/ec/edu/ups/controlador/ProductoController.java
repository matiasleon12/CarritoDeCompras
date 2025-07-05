package ec.edu.ups.controlador;

import ec.edu.ups.dao.ProductoDAO;
import ec.edu.ups.modelo.Producto;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;
import ec.edu.ups.vista.CarritoAnadirView;
import ec.edu.ups.vista.ProductoAnadirView;
import ec.edu.ups.vista.ProductoListaView;
import ec.edu.ups.vista.ProductoActualizarView;
import ec.edu.ups.vista.ProductoEliminarView;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ProductoController {

    private final ProductoAnadirView productoAnadirView;
    private final ProductoListaView productoListaView;
    private final CarritoAnadirView carritoAnadirView;
    private final ProductoActualizarView productoActualizarView;
    private final ProductoEliminarView productoEliminarView;

    private final ProductoDAO productoDAO;

    public ProductoController(ProductoDAO productoDAO,
                              ProductoAnadirView productoAnadirView,
                              ProductoListaView productoListaView,
                              CarritoAnadirView carritoAnadirView, ProductoActualizarView productoActualizarView, ProductoEliminarView productoEliminarView) {

        this.productoDAO = productoDAO;
        this.productoAnadirView = productoAnadirView;
        this.productoListaView = productoListaView;
        this.carritoAnadirView = carritoAnadirView;
        this.productoActualizarView = productoActualizarView;
        this.productoEliminarView = productoEliminarView;
        this.configurarEventosEnVistas();
    }

    private void configurarEventosEnVistas() {
        productoAnadirView.getBtnAceptar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarProducto();
            }
        });

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

        carritoAnadirView.getBtnBuscar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProductoPorCodigo();
            }
        });

        productoEliminarView.getEliminarButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){ /*eliminarProducto();*/}
        });

    }

    private void guardarProducto() {
        int codigo = Integer.parseInt(productoAnadirView.getTxtCodigo().getText());
        String nombre = productoAnadirView.getTxtNombre().getText();
        double precio = Double.parseDouble(productoAnadirView.getTxtPrecio().getText());

        productoDAO.crear(new Producto(codigo, nombre, precio));
        productoAnadirView.mostrarMensaje("Producto guardado correctamente");
        productoAnadirView.limpiarCampos();
        productoAnadirView.mostrarProductos(productoDAO.listarTodos());
    }

    private void buscarProducto() {
        String nombre = productoListaView.getTxtBuscar().getText();

        List<Producto> productosEncontrados = productoDAO.buscarPorNombre(nombre);
        productoListaView.cargarDatos(productosEncontrados);
    }

    private void listarProductos() {
        List<Producto> productos = productoDAO.listarTodos();
        productoListaView.cargarDatos(productos);
    }

    private void buscarProductoPorCodigo() {
        int codigo = Integer.parseInt(carritoAnadirView.getTxtCodigo().getText());
        Producto producto = productoDAO.buscarPorCodigo(codigo);
        if (producto == null) {
            carritoAnadirView.mostrarMensaje("No se encontro el producto");
            carritoAnadirView.getTxtNombre().setText("");
            carritoAnadirView.getTxtPrecio().setText("");
        } else {
            carritoAnadirView.getTxtNombre().setText(producto.getNombre());
            carritoAnadirView.getTxtPrecio().setText(String.valueOf(producto.getPrecio()));
        }

    }

    /*private  void eliminarProducto(){
            String textCodigo = productoEliminarView.getTextField2().getText().trim();
            if (textCodigo.isEmpty()) {
                productoEliminarView.mostrarMensaje(mensInter.get("producto.mensaje.ingresar.codigo"));
                return;
            }
            if (!textCodigo.matches("[1-9]\\d*|0")) {
                productoEliminarView.mostrarMensaje(mi.get("producto.mensaje.codigo.invalido"));
                return;
            }
            int codigo = Integer.parseInt(textCodigo);
            Producto producto = productoDAO.buscarPorCodigo(codigo);
            if (producto == null) {
                productoEliminarView.mostrarMensaje(mi.get("producto.mensaje.no.encontrado"));
                return;
            }
            boolean confirmado = productoEliminarView.configurarEliminacion(mensInter.get("producto.mensaje.eliminar.pregunta"));
            if (!confirmado) {
                productoEliminarView.mostrarMensaje(mi.get("producto.mensaje.eliminacion.cancelada"));
                return;
            }
            productoDAO.eliminar(codigo);
            productoEliminarView.mostrarMensaje(mi.get("producto.mensaje.eliminado.correctamente"));
            productoEliminarView.limpiarCampos();

    }*/
}