package servicios;

import datos.CompraDAO;
import datos.ProductoDAO;
import modelo.Compra;
import modelo.DetalleCompra;
import modelo.Producto;
import java.time.LocalDate;
import java.util.List;

public class ServicioCompra {
    private CompraDAO compraDAO;
    private ProductoDAO productoDAO;

    public ServicioCompra() {
        this.compraDAO = new CompraDAO();
        this.productoDAO = new ProductoDAO();
    }

    /**
     * Registra una nueva compra en el sistema
     * @param compra Objeto compra con sus detalles
     * @return true si se registró exitosamente, false en caso contrario
     */
    public boolean registrarCompra(Compra compra) {
        if (compra == null || compra.getDetalles().isEmpty()) {
            return false;
        }

        // Validar que haya stock suficiente para todos los productos
        for (DetalleCompra detalle : compra.getDetalles()) {
            if (!productoDAO.hayStockSuficiente(detalle.getCodigoProducto(), detalle.getCantidad())) {
                System.err.println("Stock insuficiente para el producto: " + detalle.getCodigoProducto());
                return false;
            }
        }

        // Registrar la compra
        boolean exito = compraDAO.insertar(compra);

        // Si se registró exitosamente, actualizar el stock
        if (exito) {
            for (DetalleCompra detalle : compra.getDetalles()) {
                productoDAO.actualizarStock(detalle.getCodigoProducto(), detalle.getCantidad());
            }
        }

        return exito;
    }

    /**
     * Crea una nueva compra para un cliente
     * @param codigoCliente Código del cliente
     * @return Nueva compra con la fecha actual
     */
    public Compra crearNuevaCompra(int codigoCliente) {
        return new Compra(LocalDate.now(), codigoCliente);
    }

    /**
     * Agrega un producto al carrito de compra
     * @param compra Compra actual
     * @param codigoProducto Código del producto
     * @param cantidad Cantidad a comprar
     * @return true si se agregó exitosamente, false en caso contrario
     */
    public boolean agregarProductoAlCarrito(Compra compra, int codigoProducto, int cantidad) {
        if (compra == null || cantidad <= 0) {
            return false;
        }

        // Verificar que el producto exista y tenga stock
        Producto producto = productoDAO.obtenerPorCodigo(codigoProducto);
        if (producto == null) {
            return false;
        }

        if (!productoDAO.hayStockSuficiente(codigoProducto, cantidad)) {
            return false;
        }

        // Verificar si el producto ya está en el carrito
        for (DetalleCompra detalle : compra.getDetalles()) {
            if (detalle.getCodigoProducto() == codigoProducto) {
                // Si ya está, actualizar la cantidad
                int nuevaCantidad = detalle.getCantidad() + cantidad;
                
                // Verificar que haya stock para la nueva cantidad
                if (!productoDAO.hayStockSuficiente(codigoProducto, nuevaCantidad)) {
                    return false;
                }
                
                detalle.setCantidad(nuevaCantidad);
                return true;
            }
        }

        // Si no está, agregarlo
        DetalleCompra nuevoDetalle = new DetalleCompra(
            codigoProducto,
            cantidad,
            producto.getPrecio()
        );
        nuevoDetalle.setDescripcionProducto(producto.getDescripcion());
        compra.agregarDetalle(nuevoDetalle);

        return true;
    }

    /**
     * Elimina un producto del carrito
     * @param compra Compra actual
     * @param codigoProducto Código del producto a eliminar
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public boolean eliminarProductoDelCarrito(Compra compra, int codigoProducto) {
        if (compra == null) {
            return false;
        }

        return compra.getDetalles().removeIf(d -> d.getCodigoProducto() == codigoProducto);
    }

    /**
     * Obtiene una compra por su número
     * @param numeroCompra Número de compra
     * @return Compra encontrada o null
     */
    public Compra obtenerCompraPorNumero(int numeroCompra) {
        return compraDAO.obtenerPorNumero(numeroCompra);
    }

    /**
     * Lista todas las compras del sistema
     * @return Lista de todas las compras
     */
    public List<Compra> listarTodasLasCompras() {
        return compraDAO.listarTodas();
    }

    /**
     * Lista las compras de un cliente específico
     * @param codigoCliente Código del cliente
     * @return Lista de compras del cliente
     */
    public List<Compra> listarComprasPorCliente(int codigoCliente) {
        return compraDAO.listarPorCliente(codigoCliente);
    }

    /**
     * Elimina una compra del sistema
     * @param numeroCompra Número de compra a eliminar
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public boolean eliminarCompra(int numeroCompra) {
        return compraDAO.eliminar(numeroCompra);
    }

    /**
     * Calcula el total de una compra
     * @param compra Compra
     * @return Total de la compra
     */
    public double calcularTotal(Compra compra) {
        if (compra == null) {
            return 0.0;
        }
        return compra.calcularTotal();
    }

    /**
     * Verifica si una compra está vacía
     * @param compra Compra
     * @return true si está vacía, false en caso contrario
     */
    public boolean estaVacia(Compra compra) {
        return compra == null || compra.getDetalles().isEmpty();
    }
}
