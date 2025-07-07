package ec.edu.ups;

import ec.edu.ups.controlador.ProductoController;
import ec.edu.ups.controlador.CarritoController;
import ec.edu.ups.controlador.UsuarioController;
import ec.edu.ups.dao.CarritoDAO;
import ec.edu.ups.dao.ProductoDAO;
import ec.edu.ups.dao.UsuarioDAO;
import ec.edu.ups.dao.impl.CarritoDAOMemoria;
import ec.edu.ups.dao.impl.ProductoDAOMemoria;
import ec.edu.ups.dao.impl.UsuarioDAOMemoria;
import ec.edu.ups.modelo.Usuario;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;
import ec.edu.ups.vista.*;
import ec.edu.ups.modelo.Rol;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                //iniciar sesion
                UsuarioDAO usuarioDAO = new UsuarioDAOMemoria();
                CarritoDAO carritoDAO = new CarritoDAOMemoria();

                LoginView loginView = new LoginView();
                RegistrarUsuarioView registrarUsuarioView = new RegistrarUsuarioView();

                UsuarioController usuarioController = new UsuarioController(usuarioDAO,carritoDAO,loginView,registrarUsuarioView);
                loginView.setVisible(true);

                loginView.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e){
                        Usuario usuarioAuntenticado = usuarioController.getUsuarioAutenticado();
                        if(usuarioAuntenticado != null){
                            MenuPrincipalView principalView = usuarioController.getPrincipalView();  //ya tiene listeners


                            MensajeInternacionalizacionHandler mensInter = usuarioController.getMensInter();

                            CarritoAnadirView carritoView = new CarritoAnadirView();
                            carritoView.setMensajeHandler(mensInter);
                            principalView.getjDesktopPane().add(carritoView);

                            ListarCarritosView listarCarritosView = new ListarCarritosView();
                            listarCarritosView.setMensajeHandler(mensInter);
                            principalView.getjDesktopPane().add(listarCarritosView);

                            ListarCarrADMINView listarCAdmin = new ListarCarrADMINView();
                            listarCAdmin.setMensajeHandler(mensInter);
                            principalView.getjDesktopPane().add(listarCAdmin);

                            ProductoDAO productoDAO = new ProductoDAOMemoria();

                            //vista de registrar productos con el mih
                            ProductoAnadirView productoAnadirView = new ProductoAnadirView();
                            productoAnadirView.setMensajeHandler(mensInter);
                            principalView.getjDesktopPane().add(productoAnadirView);

                            //vista de listar productos
                            ProductoListaView productoListaView = new ProductoListaView();
                            productoListaView.setMensajeHandler(mensInter);
                            principalView.getjDesktopPane().add(productoListaView);

                            ProductoController productoController = new ProductoController(mensInter, productoDAO, productoAnadirView, productoListaView, carritoView);
                            CarritoController carritoController = new CarritoController(mensInter, carritoDAO, usuarioDAO,carritoView, productoDAO, usuarioAuntenticado);

                            ProductoEliminarView eliminarProductoView = new ProductoEliminarView(productoController);
                            eliminarProductoView.setMensajeHandler(mensInter);
                            productoController.setEliminarProductoView(eliminarProductoView);
                            principalView.getjDesktopPane().add(eliminarProductoView);

                            ProductoActualizarView modificarProductoView = new ProductoActualizarView(productoController);
                            modificarProductoView.setMensajeHandler(mensInter);

                            productoController.setProductoActualizarView(modificarProductoView);
                            principalView.getjDesktopPane().add(modificarProductoView);

                            carritoController.vincularListarCarritos(listarCarritosView, principalView.getjDesktopPane());
                            carritoController.configurarEventosListarCarritoAdmin(listarCAdmin, principalView.getjDesktopPane());

                            //ocultar vistas cuando ingresa un usuario
                            if(usuarioAuntenticado.getRol().equals(Rol.USUARIO)) {
                                principalView.deshabilitarMenusAdministrador();
                            } else principalView.deshabilitarMenusUsuario();

                            String texto = mensInter.get("mensaje.bienvenido") + ": " + usuarioAuntenticado.getUsername();
                            principalView.mostrarMensaje(texto);

                            // menú cuenta usuario
                            principalView.getMenuItemCuentaUsuario().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    usuarioController.abrirCuentaUsuario();
                                }
                            });

                            // menu crear producto
                            principalView.getMenuItemCrearProducto().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if(!productoAnadirView.isVisible()){
                                        productoAnadirView.setVisible(true);
                                    }
                                }
                            });

                            // menu buscar producto
                            principalView.getMenuItemBuscarProducto().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if(!productoListaView.isVisible()){
                                        productoListaView.setVisible(true);
                                    }
                                }
                            });
                            // menu eliminar produc
                            principalView.getMenuItemEliminarProducto().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (!eliminarProductoView.isVisible()) {
                                        eliminarProductoView.setVisible(true);
                                    }
                                }
                            });

                            // menu modificar producto
                            principalView.getMenuItemActualizarProducto().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (!modificarProductoView.isVisible()) {
                                        modificarProductoView.setVisible(true);
                                    }
                                }
                            });

                            principalView.getMenuItemCrearCarrito().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if(!carritoView.isVisible()){
                                        carritoView.setVisible(true);
                                    }
                                }
                            });

                            principalView.getMenuItemListarMisCarritos().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if(!listarCarritosView.isVisible()){
                                        listarCarritosView.setVisible(true);
                                    }
                                }
                            });

                            principalView.getMenuItemListarCarritosPorUsuario().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if(!listarCAdmin.isVisible()){
                                        listarCAdmin.setVisible(true);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}