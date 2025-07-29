package ec.edu.ups;

import ec.edu.ups.controlador.CarritoController;
import ec.edu.ups.controlador.ProductoController;
import ec.edu.ups.controlador.UsuarioController;
import ec.edu.ups.dao.CarritoDAO;
import ec.edu.ups.dao.ProductoDAO;
import ec.edu.ups.dao.UsuarioDAO;
import ec.edu.ups.dao.impl.*;
import ec.edu.ups.dao.impl.archivo.CarritoDAOArchivoTexto;
import ec.edu.ups.dao.impl.archivo.ProductoDAOArchivoTexto;
import ec.edu.ups.dao.impl.archivo.UsuarioDAOArchivoTexto;
import ec.edu.ups.dao.impl.binario.CarritoDAOArchivoBinario;
import ec.edu.ups.dao.impl.binario.ProductoDAOArchivoBinario;
import ec.edu.ups.dao.impl.binario.UsuarioDAOArchivoBinario;
import ec.edu.ups.modelo.Rol;
import ec.edu.ups.modelo.Usuario;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;
import ec.edu.ups.vista.*;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            // 1. Instanciar todas las vistas y el controlador de usuario inicial.
            LoginView loginView = new LoginView();
            RegistrarUsuarioView registrarUsuarioView = new RegistrarUsuarioView();

            // Se necesitan DAOs temporales en memoria solo para el proceso de login y registro.
            // La persistencia real se decidirá después.
            UsuarioDAO usuarioDAOLogin = new UsuarioDAOMemoria(); // DAO temporal para login
            CarritoDAO carritoDAOLogin = new CarritoDAOMemoria(); // DAO temporal para login

            UsuarioController usuarioController = new UsuarioController(usuarioDAOLogin, carritoDAOLogin, loginView, registrarUsuarioView);

            // 2. Añadir un listener a LoginView para que se ejecute cuando se cierre.
            loginView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    Usuario usuarioAutenticado = usuarioController.getUsuarioAutenticado();

                    // 3. Solo si el login fue exitoso, continuamos.
                    if (usuarioAutenticado != null) {
                        // 4. Leer la selección de almacenamiento del usuario desde LoginView.
                        LoginView.TipoAlmacenamiento tipo = loginView.getTipoAlmacenamientoSeleccionado();
                        String ruta = loginView.getRutaAlmacenamiento();
                        System.out.println("Almacenamiento seleccionado: " + tipo + " en ruta: " + ruta);


                        // 5. Instanciar los DAOs DEFINITIVOS según la selección.
                        UsuarioDAO usuarioDAO;
                        ProductoDAO productoDAO;
                        CarritoDAO carritoDAO;

                        switch (tipo) {
                            case TEXTO:
                                usuarioDAO = new ec.edu.ups.dao.impl.archivo.UsuarioDAOArchivoTexto(ruta);
                                productoDAO = new ec.edu.ups.dao.impl.archivo.ProductoDAOArchivoTexto(ruta);
                                carritoDAO = new ec.edu.ups.dao.impl.archivo.CarritoDAOArchivoTexto(ruta, usuarioDAO, productoDAO);
                                System.out.println("Usando DAOs de Archivo de Texto");
                                break;
                            case BINARIO:
                                usuarioDAO = new UsuarioDAOArchivoBinario(ruta);
                                productoDAO = new ProductoDAOArchivoBinario(ruta);
                                carritoDAO = new CarritoDAOArchivoBinario(ruta);
                                System.out.println("Usando DAOs de Archivo Binario");
                                break;
                            default: // MEMORIA
                                usuarioDAO = new UsuarioDAOMemoria();
                                productoDAO = new ProductoDAOMemoria();
                                carritoDAO = new CarritoDAOMemoria();
                                System.out.println("Usando DAOs de Memoria");
                                break;
                        }

                        // 6. Ahora, con los DAOs correctos, iniciamos la aplicación principal.
                        lanzarAplicacionPrincipal(usuarioAutenticado, usuarioDAO, productoDAO, carritoDAO, usuarioController.getMensInter());
                    }
                }
            });

            // 7. Finalmente, mostrar la vista de login para iniciar el proceso.
            loginView.setVisible(true);
        });
    }

    /**
     * Este método centraliza la creación de la ventana principal y sus controladores
     * una vez que el usuario ha iniciado sesión y los DAOs han sido seleccionados.
     */
    private static void lanzarAplicacionPrincipal(Usuario usuarioAutenticado, UsuarioDAO usuarioDAO, ProductoDAO productoDAO, CarritoDAO carritoDAO, MensajeInternacionalizacionHandler mensInter) {

        // --- Crear Vistas del Menú Principal ---
        MenuPrincipalView principalView = new MenuPrincipalView(mensInter);

        CarritoAnadirView carritoView = new CarritoAnadirView();
        carritoView.setMensajeHandler(mensInter);
        principalView.getjDesktopPane().add(carritoView);

        ListarCarritosView listarCarritosView = new ListarCarritosView();
        listarCarritosView.setMensajeHandler(mensInter);
        principalView.getjDesktopPane().add(listarCarritosView);

        ListarCarrADMINView listarCAdmin = new ListarCarrADMINView();
        listarCAdmin.setMensajeHandler(mensInter);
        principalView.getjDesktopPane().add(listarCAdmin);

        ProductoAnadirView productoAnadirView = new ProductoAnadirView();
        productoAnadirView.setMensajeHandler(mensInter);
        principalView.getjDesktopPane().add(productoAnadirView);

        ProductoListaView productoListaView = new ProductoListaView();
        productoListaView.setMensajeHandler(mensInter);
        principalView.getjDesktopPane().add(productoListaView);

        // --- Crear Controladores con los DAOs seleccionados ---
        ProductoController productoController = new ProductoController(mensInter, productoDAO, productoAnadirView, productoListaView, carritoView);
        CarritoController carritoController = new CarritoController(mensInter, carritoDAO, usuarioDAO, carritoView, productoDAO, usuarioAutenticado);

        // --- Vistas de Actualizar y Eliminar que dependen del controlador ---
        ProductoEliminarView eliminarProductoView = new ProductoEliminarView(productoController);
        eliminarProductoView.setMensajeHandler(mensInter);
        productoController.setEliminarProductoView(eliminarProductoView);
        principalView.getjDesktopPane().add(eliminarProductoView);

        ProductoActualizarView modificarProductoView = new ProductoActualizarView(productoController);
        modificarProductoView.setMensajeHandler(mensInter);
        productoController.setProductoActualizarView(modificarProductoView);
        principalView.getjDesktopPane().add(modificarProductoView);

        // --- Vincular eventos y controladores ---
        carritoController.vincularListarCarritos(listarCarritosView, principalView.getjDesktopPane());
        carritoController.configurarEventosListarCarritoAdmin(listarCAdmin, principalView.getjDesktopPane());

        // Habilitar/deshabilitar menús según el rol
        if (usuarioAutenticado.getRol().equals(Rol.USUARIO)) {
            principalView.deshabilitarMenusAdministrador();
        } else {
            principalView.deshabilitarMenusUsuario();
        }

        // --- Configurar los Action Listeners del Menú Principal ---

        configurarMenuListeners(principalView, productoAnadirView, productoListaView, eliminarProductoView, modificarProductoView, carritoView, listarCarritosView, listarCAdmin);

        // Mensaje de bienvenida
        String texto = mensInter.get("mensaje.bienvenido") + ": " + usuarioAutenticado.getUsername();
        principalView.mostrarMensaje(texto);

        // Mostrar la ventana principal
        principalView.setVisible(true);
    }

    private static void configurarMenuListeners(MenuPrincipalView principalView, ProductoAnadirView productoAnadirView, ProductoListaView productoListaView, ProductoEliminarView eliminarProductoView, ProductoActualizarView modificarProductoView, CarritoAnadirView carritoView, ListarCarritosView listarCarritosView, ListarCarrADMINView listarCAdmin) {
        principalView.getMenuItemCrearProducto().addActionListener(e -> {
            if (!productoAnadirView.isVisible()) productoAnadirView.setVisible(true);
        });
        principalView.getMenuItemBuscarProducto().addActionListener(e -> {
            if (!productoListaView.isVisible()) productoListaView.setVisible(true);
        });
        principalView.getMenuItemEliminarProducto().addActionListener(e -> {
            if (!eliminarProductoView.isVisible()) eliminarProductoView.setVisible(true);
        });
        principalView.getMenuItemActualizarProducto().addActionListener(e -> {
            if (!modificarProductoView.isVisible()) modificarProductoView.setVisible(true);
        });
        principalView.getMenuItemCrearCarrito().addActionListener(e -> {
            if (!carritoView.isVisible()) carritoView.setVisible(true);
        });
        principalView.getMenuItemListarMisCarritos().addActionListener(e -> {
            if (!listarCarritosView.isVisible()) listarCarritosView.setVisible(true);
        });
        principalView.getMenuItemListarCarritosPorUsuario().addActionListener(e -> {
            if (!listarCAdmin.isVisible()) listarCAdmin.setVisible(true);
        });
        principalView.getMenuItemSalirALogin().addActionListener(e -> {
            principalView.dispose();


        });


        principalView.getMenuItemSalir().addActionListener(e -> {
            System.exit(0);
        });
    }
}