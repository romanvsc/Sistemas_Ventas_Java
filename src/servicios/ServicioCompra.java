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

    public boolean registrarCompra(Compra compra) {
        boolean exito = false;
        if (compra != null && !compra.getDetalles().isEmpty()) {
            boolean stockValido = true;
            for (DetalleCompra detalle : compra.getDetalles()) {
                if (!productoDAO.hayStockSuficiente(detalle.getCodigoProducto(), detalle.getCantidad())) {
                    System.err.println("Stock insuficiente para el producto: " + detalle.getCodigoProducto());
                    stockValido = false;
                    break;
                }
            }
            if (stockValido) {
                exito = compraDAO.insertar(compra);
                if (exito) {
                    for (DetalleCompra detalle : compra.getDetalles()) {
                        productoDAO.actualizarStock(detalle.getCodigoProducto(), detalle.getCantidad());
                    }
                }
            }
        }
        return exito;
    }

    public Compra crearNuevaCompra(int codigoCliente) {
        return new Compra(LocalDate.now(), codigoCliente);
    }

    public boolean agregarProductoAlCarrito(Compra compra, int codigoProducto, int cantidad) {
        boolean agregado = false;
        if (compra != null && cantidad > 0) {
            Producto producto = productoDAO.obtenerPorCodigo(codigoProducto);
            if (producto != null && productoDAO.hayStockSuficiente(codigoProducto, cantidad)) {
                DetalleCompra detalleExistente = null;
                for (DetalleCompra detalle : compra.getDetalles()) {
                    if (detalle.getCodigoProducto() == codigoProducto) {
                        detalleExistente = detalle;
                        break;
                    }
                }
                if (detalleExistente != null) {
                    int nuevaCantidad = detalleExistente.getCantidad() + cantidad;
                    if (productoDAO.hayStockSuficiente(codigoProducto, nuevaCantidad)) {
                        detalleExistente.setCantidad(nuevaCantidad);
                        agregado = true;
                    }
                } else {
                    DetalleCompra nuevoDetalle = new DetalleCompra(
                        codigoProducto,
                        cantidad,
                        producto.getPrecio()
                    );
                    nuevoDetalle.setDescripcionProducto(producto.getDescripcion());
                    compra.agregarDetalle(nuevoDetalle);
                    agregado = true;
                }
            }
        }
        return agregado;
    }

    public boolean eliminarProductoDelCarrito(Compra compra, int codigoProducto) {
        boolean eliminado = false;
        if (compra != null) {
            eliminado = compra.getDetalles().removeIf(d -> d.getCodigoProducto() == codigoProducto);
        }
        return eliminado;
    }

    public Compra obtenerCompraPorNumero(int numeroCompra) {
        return compraDAO.obtenerPorNumero(numeroCompra);
    }

    public List<Compra> listarTodasLasCompras() {
        return compraDAO.listarTodas();
    }

    public List<Compra> listarComprasPorCliente(int codigoCliente) {
        return compraDAO.listarPorCliente(codigoCliente);
    }

    public boolean eliminarCompra(int numeroCompra) {
        boolean eliminado = compraDAO.eliminar(numeroCompra);
        return eliminado;
    }

    public double calcularTotal(Compra compra) {
        if (compra == null) {
            return 0.0;
        }
        return compra.calcularTotal();
    }

    public boolean estaVacia(Compra compra) {
        boolean vacia = compra == null || compra.getDetalles().isEmpty();
        return vacia;
    }
}
