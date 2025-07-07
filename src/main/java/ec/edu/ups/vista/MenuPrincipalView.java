package ec.edu.ups.vista;

import ec.edu.ups.util.MensajeInternacionalizacionHandler;

import javax.swing.*;

public class MenuPrincipalView extends JFrame {

    private MensajeInternacionalizacionHandler mensInter;
    private JDesktopPane jDesktopPane = new MiJDesktopPane();

    private JMenuBar menuBar;
    private JMenu menuProducto;
    private JMenu menuCarrito;
    private JMenu menuSesion;
    private JMenu menuSalirLogin;

    private JMenuItem menuItemCrearProducto;
    private JMenuItem menuItemEliminarProducto;
    private JMenuItem menuItemActualizarProducto;
    private JMenuItem menuItemBuscarProducto;

    private JMenuItem menuItemCrearCarrito;
    private JMenuItem menuItemListarMisCarritos;
    private JMenuItem menuItemlistarCarritosPorUsuario;



    private JMenuItem menuItemCuentaUsuario;
    private JMenuItem menuItemListarUsuarios; //solo para admin


    private JMenuItem menuItemSalir;
    private JMenuItem menuItemSalirALogin;

    public MenuPrincipalView() {
        jDesktopPane = new MiJDesktopPane();
        menuBar = new JMenuBar();

        menuProducto = new JMenu("Producto");
        menuCarrito = new JMenu("Carrito");
        menuSesion = new JMenu("Cuenta");
        menuSalirLogin = new JMenu("Opciones Salida");


        menuItemCrearProducto = new JMenuItem("Crear Producto");
        menuItemEliminarProducto = new JMenuItem("Eliminar Producto");
        menuItemActualizarProducto = new JMenuItem("Modificar/Actualizar Producto");
        menuItemBuscarProducto = new JMenuItem("Buscar/Listar Productos");


        menuItemCrearCarrito = new JMenuItem("Crear Carrito");
        menuItemListarMisCarritos = new JMenuItem("Mis Carritos");
        menuItemlistarCarritosPorUsuario = new JMenuItem("Listar Carritos por Usuario");


        menuItemCuentaUsuario = new JMenuItem("Información de Cuenta");
        menuItemListarUsuarios = new JMenuItem("Listar Usuarios"); //para admin


        menuItemSalir = new JMenuItem("Salir");
        menuItemSalirALogin = new JMenuItem("Cerrar sesión");

        menuBar.add(menuProducto);
        menuBar.add(menuCarrito);
        menuBar.add(menuSesion);
        menuBar.add(menuSalirLogin);

        menuProducto.add(menuItemCrearProducto);
        menuProducto.add(menuItemEliminarProducto);
        menuProducto.add(menuItemActualizarProducto);
        menuProducto.add(menuItemBuscarProducto);

        menuCarrito.add(menuItemCrearCarrito);
        menuCarrito.add(menuItemListarMisCarritos);
        menuCarrito.addSeparator();
        menuCarrito.add(menuItemlistarCarritosPorUsuario);

        menuSesion.add(menuItemCuentaUsuario);
        menuSesion.add(menuItemListarUsuarios);

        menuSalirLogin.add(menuItemSalir);
        menuSalirLogin.add(menuItemSalirALogin);

        //idioma por defecto
        mensInter = new MensajeInternacionalizacionHandler("es", "EC");
        actualizarTextos();

        setJMenuBar(menuBar);
        setContentPane(jDesktopPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sistema de Carrito de Compras En Línea");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    //Nuevo constructor que recibe el handler ya inicializado
    public MenuPrincipalView(MensajeInternacionalizacionHandler mih) {
        this();               //llama al constructor por defecto
        this.mensInter = mih;       //asigna el handler
        actualizarTextos();   //pinta los textos con el idioma adecuado
    }

    public void actualizarTextos() {
        setTitle(mensInter.get("app.titulo"));
        menuProducto.setText(mensInter.get("menu.producto"));
        menuCarrito.setText(mensInter.get("menu.carrito"));
        menuSesion.setText(mensInter.get("menu.sesion"));
        menuSalirLogin .setText(mensInter.get("menu.salir.opciones"));

        //items del producto
        menuItemCrearProducto.setText(mensInter.get("menu.producto.crear"));
        menuItemEliminarProducto.setText(mensInter.get("menu.producto.eliminar"));
        menuItemActualizarProducto.setText(mensInter.get("menu.producto.actualizar"));
        menuItemBuscarProducto .setText(mensInter.get("menu.producto.buscar"));

        //items del carrito
        menuItemCrearCarrito.setText(mensInter.get("menu.carrito.crear"));
        menuItemListarMisCarritos.setText(mensInter.get("menu.carrito.listarMisCarritos"));
        menuItemlistarCarritosPorUsuario.setText(mensInter.get("menu.carrito.listarPorUsuario"));

        //items de Cuenta
        menuItemCuentaUsuario .setText(mensInter.get("menu.cuenta.usuario"));
        menuItemListarUsuarios.setText(mensInter.get("menu.cuenta.admin"));

        menuItemSalir.setText(mensInter.get("menu.salir.todos"));
        menuItemSalirALogin .setText(mensInter.get("menu.salir.login"));
    }

    public void deshabilitarMenusAdministrador() {
        getMenuItemCrearProducto().setEnabled(false);
        getMenuItemBuscarProducto().setEnabled(false);
        getMenuItemActualizarProducto().setEnabled(false);
        getMenuItemEliminarProducto().setEnabled(false);
        getMenuItemListarCarritosPorUsuario().setEnabled(false); //usuario no puede listar los carritos de demas usuarios
        getMenuItemListarUsuarios().setEnabled(false);
    }

    public void deshabilitarMenusUsuario(){
        getMenuItemCrearCarrito().setEnabled(false);
        getMenuItemListarMisCarritos().setEnabled(false);
        getMenuItemCuentaUsuario().setEnabled(false);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    //GETTERS Y SETTERS
    public void setMenuBar(JMenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public JMenu getMenuSalirALogin() {
        return menuSalirLogin;
    }

    public void setMenuSalirALogin(JMenu menuSalirALogin) {
        this.menuSalirLogin = menuSalirALogin;
    }

    public JMenuItem getMenuItemSalir() {
        return menuItemSalir;
    }

    public void setMenuItemSalir(JMenuItem menuItemSalir) {
        this.menuItemSalir = menuItemSalir;
    }

    public JMenuItem getMenuItemSalirALogin() {
        return menuItemSalirALogin;
    }

    public void setMenuItemSalirALogin(JMenuItem menuItemSalirALogin) {
        this.menuItemSalirALogin = menuItemSalirALogin;
    }

    public MensajeInternacionalizacionHandler getMensInter() {
        return mensInter;
    }

    public void setMensInter(MensajeInternacionalizacionHandler mensInter) {
        this.mensInter = mensInter;
    }

    public JMenuItem getMenuItemCrearProducto() {
        return menuItemCrearProducto;
    }

    public JMenu getMenuProducto() {
        return menuProducto;
    }

    public void setMenuProducto(JMenu menuProducto) {
        this.menuProducto = menuProducto;
    }

    public JMenu getMenuCarrito() {
        return menuCarrito;
    }

    public void setMenuCarrito(JMenu menuCarrito) {
        this.menuCarrito = menuCarrito;
    }

    public JMenu getMenuSesion() {
        return menuSesion;
    }

    public void setMenuSesion(JMenu menuSesion) {
        this.menuSesion = menuSesion;
    }

    public void setMenuItemCrearProducto(JMenuItem menuItemCrearProducto) {
        this.menuItemCrearProducto = menuItemCrearProducto;
    }

    public JMenuItem getMenuItemEliminarProducto() {
        return menuItemEliminarProducto;
    }

    public void setMenuItemEliminarProducto(JMenuItem menuItemEliminarProducto) {
        this.menuItemEliminarProducto = menuItemEliminarProducto;
    }

    public JMenuItem getMenuItemActualizarProducto() {
        return menuItemActualizarProducto;
    }

    public void setMenuItemActualizarProducto(JMenuItem menuItemActualizarProducto) {
        this.menuItemActualizarProducto = menuItemActualizarProducto;
    }

    public JMenuItem getMenuItemBuscarProducto() {
        return menuItemBuscarProducto;
    }

    public void setMenuItemBuscarProducto(JMenuItem menuItemBuscarProducto) {
        this.menuItemBuscarProducto = menuItemBuscarProducto;
    }

    public JMenuItem getMenuItemCrearCarrito() {
        return menuItemCrearCarrito;
    }

    public void setMenuItemCrearCarrito(JMenuItem menuItemCrearCarrito) {
        this.menuItemCrearCarrito = menuItemCrearCarrito;
    }

    public JMenuItem getMenuItemListarMisCarritos() {
        return menuItemListarMisCarritos;
    }

    public void setMenuItemListarMisCarritos(JMenuItem menuItemListarMisCarritos) {
        this.menuItemListarMisCarritos = menuItemListarMisCarritos;
    }

    public JMenuItem getMenuItemListarCarritosPorUsuario() {
        return menuItemlistarCarritosPorUsuario;
    }

    public void setMenuItemListarCarritosPorUsuario(JMenuItem menuItemListarCarritosPorUsuario) {
        this.menuItemlistarCarritosPorUsuario = menuItemListarCarritosPorUsuario;
    }

    public JMenuItem getMenuItemCuentaUsuario() {
        return menuItemCuentaUsuario;
    }

    public void setMenuItemCuentaUsuario(JMenuItem menuItemCuentaUsuario) {
        this.menuItemCuentaUsuario = menuItemCuentaUsuario;
    }

    public JMenuItem getMenuItemListarUsuarios() {
        return menuItemListarUsuarios;
    }

    public void setMenuItemListarUsuarios(JMenuItem menuItemListarUsuarios) {
        this.menuItemListarUsuarios = menuItemListarUsuarios;
    }

    public JDesktopPane getjDesktopPane() {
        return jDesktopPane;
    }

    public void setjDesktopPane(JDesktopPane jDesktopPane) {
        this.jDesktopPane = jDesktopPane;
    }
}