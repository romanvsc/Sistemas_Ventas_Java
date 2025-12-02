package servicios;

import datos.UsuarioDAO;
import datos.ProductoDAO;
import datos.CompraDAO;
import modelo.Usuario;
import modelo.Producto;
import modelo.Compra;
import modelo.EstadisticaProducto;
import java.util.List;

public class ServicioAdmin {
    private UsuarioDAO usuarioDAO;
    private ProductoDAO productoDAO;
    private CompraDAO compraDAO;
    
    private static final int UMBRAL_STOCK_BAJO_DEFAULT = 10;

    public ServicioAdmin() {
        this.usuarioDAO = new UsuarioDAO();
        this.productoDAO = new ProductoDAO();
        this.compraDAO = new CompraDAO();
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listarTodos();
    }

    public Usuario buscarUsuario(int codigo) {
        return usuarioDAO.obtenerPorCodigo(codigo);
    }

    public boolean agregarUsuario(String nombre, String usuario, String contrasena) {
        boolean resultado = false;

        String nombreLimpio = nombre != null ? nombre.trim() : null;
        String usuarioLimpio = usuario != null ? usuario.trim() : null;
        String contrasenaLimpia = contrasena != null ? contrasena.trim() : null;

        boolean datosValidos = nombreLimpio != null && !nombreLimpio.isEmpty()
                && usuarioLimpio != null && !usuarioLimpio.isEmpty()
                && contrasenaLimpia != null && !contrasenaLimpia.isEmpty();

        if (datosValidos && !usuarioDAO.existeUsuario(usuarioLimpio)) {
            Usuario nuevoUsuario = new Usuario(nombreLimpio, usuarioLimpio, contrasena, 0.0);
            resultado = usuarioDAO.insertar(nuevoUsuario);
        }

        return resultado;
    }

    public boolean actualizarUsuario(Usuario usuario) {
        boolean resultado = false;

        if (usuario != null) {
            resultado = usuarioDAO.actualizar(usuario);
        }

        return resultado;
    }

    public boolean eliminarUsuario(int codigo) {
        boolean resultado = usuarioDAO.eliminar(codigo);
        return resultado;
    }

    public List<Producto> listarProductos() {
        return productoDAO.listarTodos();
    }

    public Producto buscarProducto(int codigo) {
        return productoDAO.obtenerPorCodigo(codigo);
    }

    public boolean agregarProducto(String descripcion, int cantidad, double precio) {
        boolean resultado = false;

        String descripcionLimpia = descripcion != null ? descripcion.trim() : null;
        boolean datosValidos = descripcionLimpia != null && !descripcionLimpia.isEmpty()
                && cantidad >= 0
                && precio > 0;

        if (datosValidos) {
            Producto nuevoProducto = new Producto(descripcionLimpia, cantidad, precio);
            resultado = productoDAO.insertar(nuevoProducto);
        }

        return resultado;
    }

    public boolean actualizarProducto(Producto producto) {
        boolean resultado = false;

        if (producto != null) {
            resultado = productoDAO.actualizar(producto);
        }

        return resultado;
    }

    public boolean eliminarProducto(int codigo) {
        boolean resultado = productoDAO.eliminar(codigo);
        return resultado;
    }

    public List<Compra> listarCompras() {
        return compraDAO.listarTodas();
    }

    public Compra buscarCompra(int numeroCompra) {
        return compraDAO.obtenerPorNumero(numeroCompra);
    }

    public List<Compra> listarComprasPorCliente(int codigoCliente) {
        return compraDAO.listarPorCliente(codigoCliente);
    }

    public boolean eliminarCompra(int numeroCompra) {
        boolean resultado = compraDAO.eliminar(numeroCompra);
        return resultado;
    }

    public int obtenerTotalUsuarios() {
        return usuarioDAO.listarTodos().size();
    }

    public int obtenerTotalProductos() {
        return productoDAO.listarTodos().size();
    }

    public int obtenerTotalCompras() {
        return compraDAO.listarTodas().size();
    }

    public List<Producto> obtenerProductosConStockBajo() {
        return productoDAO.listarConStockBajo(UMBRAL_STOCK_BAJO_DEFAULT);
    }

    public List<Producto> obtenerProductosConStockBajo(int umbral) {
        return productoDAO.listarConStockBajo(umbral);
    }

    public int contarProductosStockBajo() {
        return productoDAO.contarProductosStockBajo(UMBRAL_STOCK_BAJO_DEFAULT);
    }

    public int contarProductosStockBajo(int umbral) {
        return productoDAO.contarProductosStockBajo(umbral);
    }

    public List<EstadisticaProducto> obtenerProductosMasVendidos(int limite) {
        return compraDAO.obtenerProductosMasVendidos(limite);
    }

    public double obtenerTotalVentas() {
        return compraDAO.obtenerTotalVentas();
    }

    public int obtenerCantidadProductosVendidos() {
        return compraDAO.obtenerCantidadProductosVendidos();
    }

    public int getUmbralStockBajoDefault() {
        return UMBRAL_STOCK_BAJO_DEFAULT;
    }
}
