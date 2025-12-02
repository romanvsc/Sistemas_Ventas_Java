package servicios;

import datos.ListaDeseosDAO;
import modelo.ItemDeseo;
import java.util.List;

/**
 * Servicio para gestionar la lista de deseos de usuarios
 */
public class ServicioListaDeseos {
    private final ListaDeseosDAO listaDeseosDAO;
    
    public ServicioListaDeseos() {
        this.listaDeseosDAO = new ListaDeseosDAO();
    }
    
    /**
     * Agrega un producto a la lista de deseos
     */
    public boolean agregar(int codigoCliente, int codigoProducto) {
        return listaDeseosDAO.agregar(codigoCliente, codigoProducto, false);
    }
    
    /**
     * Agrega un producto con opción de notificación cuando haya stock
     */
    public boolean agregarConNotificacion(int codigoCliente, int codigoProducto, boolean notificar) {
        return listaDeseosDAO.agregar(codigoCliente, codigoProducto, notificar);
    }
    
    /**
     * Verifica si un producto está en la lista de deseos
     */
    public boolean estaEnListaDeseos(int codigoCliente, int codigoProducto) {
        return listaDeseosDAO.existe(codigoCliente, codigoProducto);
    }
    
    /**
     * Obtiene toda la lista de deseos del usuario
     */
    public List<ItemDeseo> obtenerListaDeseos(int codigoCliente) {
        return listaDeseosDAO.listarPorCliente(codigoCliente);
    }
    
    /**
     * Elimina un producto de la lista de deseos
     */
    public boolean eliminar(int codigoCliente, int codigoProducto) {
        return listaDeseosDAO.eliminar(codigoCliente, codigoProducto);
    }
    
    /**
     * Vacía la lista de deseos
     */
    public boolean vaciar(int codigoCliente) {
        return listaDeseosDAO.vaciar(codigoCliente);
    }
    
    /**
     * Alterna un producto en la lista de deseos
     * @return true si se agregó, false si se eliminó
     */
    public boolean alternar(int codigoCliente, int codigoProducto) {
        if (listaDeseosDAO.existe(codigoCliente, codigoProducto)) {
            listaDeseosDAO.eliminar(codigoCliente, codigoProducto);
            return false;
        } else {
            listaDeseosDAO.agregar(codigoCliente, codigoProducto, false);
            return true;
        }
    }
    
    /**
     * Actualiza la preferencia de notificación
     */
    public boolean actualizarNotificacion(int codigoCliente, int codigoProducto, boolean notificar) {
        return listaDeseosDAO.actualizarNotificacion(codigoCliente, codigoProducto, notificar);
    }
    
    /**
     * Mueve un item de la lista de deseos al carrito
     */
    public boolean moverAlCarrito(int codigoCliente, int codigoProducto) {
        return listaDeseosDAO.moverAlCarrito(codigoCliente, codigoProducto);
    }
    
    /**
     * Cuenta items en la lista de deseos
     */
    public int contar(int codigoCliente) {
        return listaDeseosDAO.contar(codigoCliente);
    }
    
    /**
     * Obtiene productos en la lista de deseos que ahora tienen stock
     * (para mostrar notificación al usuario)
     */
    public List<ItemDeseo> obtenerProductosDisponibles(int codigoCliente) {
        return listaDeseosDAO.obtenerParaNotificar(codigoCliente);
    }
    
    /**
     * Verifica si hay productos disponibles para notificar
     */
    public boolean hayProductosParaNotificar(int codigoCliente) {
        return !listaDeseosDAO.obtenerParaNotificar(codigoCliente).isEmpty();
    }
}
