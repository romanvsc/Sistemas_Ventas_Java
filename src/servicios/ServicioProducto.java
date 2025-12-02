package servicios;

import datos.ProductoDAO;
import modelo.Producto;
import java.util.List;

public class ServicioProducto {
    private ProductoDAO productoDAO;

    public ServicioProducto() {
        this.productoDAO = new ProductoDAO();
    }

    public List<Producto> listarProductosDisponibles() {
        return productoDAO.listarDisponibles();
    }

    public List<Producto> listarTodosLosProductos() {
        return productoDAO.listarTodos();
    }

    public Producto buscarProducto(int codigo) {
        return productoDAO.obtenerPorCodigo(codigo);
    }

    public boolean agregarProducto(String descripcion, int cantidad, double precio) {
        boolean resultado = false;
        if (descripcion != null && !descripcion.trim().isEmpty()
                && cantidad >= 0 && precio > 0) {
            Producto nuevoProducto = new Producto(descripcion.trim(), cantidad, precio);
            resultado = productoDAO.insertar(nuevoProducto);
        }
        return resultado;
    }

    public boolean actualizarProducto(Producto producto) {
        boolean resultado = false;
        if (producto != null
                && producto.getDescripcion() != null && !producto.getDescripcion().trim().isEmpty()
                && producto.getCantidad() >= 0
                && producto.getPrecio() > 0) {
            resultado = productoDAO.actualizar(producto);
        }
        return resultado;
    }

    public boolean eliminarProducto(int codigo) {
        boolean resultado = productoDAO.eliminar(codigo);
        return resultado;
    }

    public boolean verificarStock(int codigo, int cantidadRequerida) {
        boolean resultado = false;
        if (cantidadRequerida > 0) {
            resultado = productoDAO.hayStockSuficiente(codigo, cantidadRequerida);
        }
        return resultado;
    }

    public boolean actualizarStock(int codigo, int nuevaCantidad) {
        boolean resultado = false;
        if (nuevaCantidad >= 0) {
            Producto producto = productoDAO.obtenerPorCodigo(codigo);
            if (producto != null) {
                producto.setCantidad(nuevaCantidad);
                resultado = productoDAO.actualizar(producto);
            }
        }
        return resultado;
    }

    public boolean incrementarStock(int codigo, int cantidad) {
        boolean resultado = false;
        if (cantidad > 0) {
            Producto producto = productoDAO.obtenerPorCodigo(codigo);
            if (producto != null) {
                producto.setCantidad(producto.getCantidad() + cantidad);
                resultado = productoDAO.actualizar(producto);
            }
        }
        return resultado;
    }
}
