package servicios;

import datos.CarritoDAO;
import datos.ProductoDAO;
import modelo.CarritoItem;
import modelo.Producto;
import java.util.List;

/**
 * Servicio para gestionar el carrito de compras persistente
 */
public class ServicioCarrito {
    private final CarritoDAO carritoDAO;
    private final ProductoDAO productoDAO;
    
    public ServicioCarrito() {
        this.carritoDAO = new CarritoDAO();
        this.productoDAO = new ProductoDAO();
    }
    
    /**
     * Agrega un producto al carrito
     * @return true si se agregó correctamente
     */
    public boolean agregarAlCarrito(int codigoCliente, int codigoProducto, int cantidad) {
        boolean agregado = false;
        Producto producto = productoDAO.obtenerPorCodigo(codigoProducto);
        
        if (producto != null) {
            CarritoItem itemExistente = carritoDAO.obtenerItem(codigoCliente, codigoProducto);
            int cantidadActual = itemExistente != null ? itemExistente.getCantidad() : 0;
            int cantidadTotal = cantidadActual + cantidad;
            
            if (cantidadTotal <= producto.getStock()) {
                CarritoItem item = new CarritoItem();
                item.setCodigoCliente(codigoCliente);
                item.setCodigoProducto(codigoProducto);
                item.setCantidad(cantidad);
                agregado = carritoDAO.guardarItem(item);
            }
        }
        
        return agregado;
    }
    
    /**
     * Obtiene todos los items del carrito de un usuario
     */
    public List<CarritoItem> obtenerCarrito(int codigoCliente) {
        return carritoDAO.listarPorCliente(codigoCliente);
    }
    
    /**
     * Actualiza la cantidad de un item en el carrito
     */
    public boolean actualizarCantidad(int codigoCliente, int codigoProducto, int nuevaCantidad) {
        boolean actualizado = false;
        
        if (nuevaCantidad <= 0) {
            actualizado = carritoDAO.eliminarItem(codigoCliente, codigoProducto);
        } else {
            Producto producto = productoDAO.obtenerPorCodigo(codigoProducto);
            if (producto != null && nuevaCantidad <= producto.getStock()) {
                actualizado = carritoDAO.actualizarCantidad(codigoCliente, codigoProducto, nuevaCantidad);
            }
        }
        
        return actualizado;
    }
    
    /**
     * Elimina un item del carrito
     */
    public boolean eliminarDelCarrito(int codigoCliente, int codigoProducto) {
        boolean eliminado = carritoDAO.eliminarItem(codigoCliente, codigoProducto);
        return eliminado;
    }
    
    /**
     * Vacía todo el carrito
     */
    public boolean vaciarCarrito(int codigoCliente) {
        boolean vaciado = carritoDAO.vaciarCarrito(codigoCliente);
        return vaciado;
    }
    
    /**
     * Cuenta los items en el carrito
     */
    public int contarItems(int codigoCliente) {
        return carritoDAO.contarItems(codigoCliente);
    }
    
    /**
     * Calcula el subtotal del carrito (sin descuentos)
     */
    public double calcularSubtotal(int codigoCliente) {
        return carritoDAO.calcularTotal(codigoCliente);
    }
    
    /**
     * Verifica si hay stock suficiente para todos los items del carrito
     * @return Lista de productos sin stock suficiente (vacía si todo OK)
     */
    public List<CarritoItem> verificarStockCarrito(int codigoCliente) {
        List<CarritoItem> items = carritoDAO.listarPorCliente(codigoCliente);
        return items.stream()
            .filter(item -> {
                Producto p = productoDAO.obtenerPorCodigo(item.getCodigoProducto());
                return p == null || p.getStock() < item.getCantidad();
            })
            .toList();
    }
    
    /**
     * Carga el carrito guardado del usuario (al iniciar sesión)
     */
    public void cargarCarritoGuardado(int codigoCliente) {
        // Este método es para futura integración con el sistema actual de carrito en memoria
        // Por ahora, el carrito se obtiene directamente de la BD
    }
    
    /**
     * Guarda el carrito actual en la BD (antes de cerrar sesión)
     */
    public void guardarCarritoActual(int codigoCliente, List<CarritoItem> items) {
        carritoDAO.vaciarCarrito(codigoCliente);
        
        for (CarritoItem item : items) {
            item.setCodigoCliente(codigoCliente);
            carritoDAO.guardarItem(item);
        }
    }
}
