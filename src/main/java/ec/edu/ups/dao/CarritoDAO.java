package ec.edu.ups.dao;

import ec.edu.ups.modelo.Carrito;

import java.util.List;

public interface CarritoDAO {

    void crear(Carrito carrito);

    Carrito buscarPorCodigo(int codigo);

    void limpiar(Carrito carrito);

    void actualizar(Carrito carrito);

    void eliminar(int codigo);

    List<Carrito> listarTodos(); //solo para admin

    List<Carrito> listarPorUsuario(String nombreDeUsuario);

    void eliminarPorUsuario(String nombreDeUsuario);

}
