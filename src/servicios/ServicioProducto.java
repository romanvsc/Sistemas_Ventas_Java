package servicios;

import datos.ProductoDAO;
import modelo.Producto;
import java.util.List;

public class ServicioProducto {
    private ProductoDAO productoDAO;

    public ServicioProducto() {
        this.productoDAO = new ProductoDAO();
    }

    /**
     * Lista todos los productos disponibles (con stock)
     * @return Lista de productos con stock disponible
     */
    public List<Producto> listarProductosDisponibles() {
        return productoDAO.listarDisponibles();
    }

    /**
     * Lista todos los productos (incluyendo sin stock)
     * @return Lista de todos los productos
     */
    public List<Producto> listarTodosLosProductos() {
        return productoDAO.listarTodos();
    }

    /**
     * Busca un producto por su código
     * @param codigo Código del producto
     * @return Producto encontrado o null
     */
    public Producto buscarProducto(int codigo) {
        return productoDAO.obtenerPorCodigo(codigo);
    }

    /**
     * Agrega un nuevo producto al catálogo
     * @param descripcion Descripción del producto
     * @param cantidad Cantidad inicial en stock
     * @param precio Precio del producto
     * @return true si se agregó exitosamente, false en caso contrario
     */
    public boolean agregarProducto(String descripcion, int cantidad, double precio) {
        // Validaciones
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return false;
        }
        if (cantidad < 0) {
            return false;
        }
        if (precio <= 0) {
            return false;
        }

        Producto nuevoProducto = new Producto(descripcion.trim(), cantidad, precio);
        return productoDAO.insertar(nuevoProducto);
    }

    /**
     * Actualiza la información de un producto
     * @param producto Producto con los datos actualizados
     * @return true si se actualizó exitosamente, false en caso contrario
     */
    public boolean actualizarProducto(Producto producto) {
        if (producto == null) {
            return false;
        }
        
        // Validaciones
        if (producto.getDescripcion() == null || producto.getDescripcion().trim().isEmpty()) {
            return false;
        }
        if (producto.getCantidad() < 0) {
            return false;
        }
        if (producto.getPrecio() <= 0) {
            return false;
        }

        return productoDAO.actualizar(producto);
    }

    /**
     * Elimina un producto del catálogo
     * @param codigo Código del producto a eliminar
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public boolean eliminarProducto(int codigo) {
        return productoDAO.eliminar(codigo);
    }

    /**
     * Verifica si hay stock suficiente de un producto
     * @param codigo Código del producto
     * @param cantidadRequerida Cantidad requerida
     * @return true si hay stock suficiente, false en caso contrario
     */
    public boolean verificarStock(int codigo, int cantidadRequerida) {
        if (cantidadRequerida <= 0) {
            return false;
        }
        return productoDAO.hayStockSuficiente(codigo, cantidadRequerida);
    }

    /**
     * Actualiza el stock de un producto (incremento o decremento directo)
     * @param codigo Código del producto
     * @param nuevaCantidad Nueva cantidad en stock
     * @return true si se actualizó exitosamente, false en caso contrario
     */
    public boolean actualizarStock(int codigo, int nuevaCantidad) {
        if (nuevaCantidad < 0) {
            return false;
        }

        Producto producto = productoDAO.obtenerPorCodigo(codigo);
        if (producto == null) {
            return false;
        }

        producto.setCantidad(nuevaCantidad);
        return productoDAO.actualizar(producto);
    }

    /**
     * Incrementa el stock de un producto
     * @param codigo Código del producto
     * @param cantidad Cantidad a agregar
     * @return true si se incrementó exitosamente, false en caso contrario
     */
    public boolean incrementarStock(int codigo, int cantidad) {
        if (cantidad <= 0) {
            return false;
        }

        Producto producto = productoDAO.obtenerPorCodigo(codigo);
        if (producto == null) {
            return false;
        }

        producto.setCantidad(producto.getCantidad() + cantidad);
        return productoDAO.actualizar(producto);
    }
}
