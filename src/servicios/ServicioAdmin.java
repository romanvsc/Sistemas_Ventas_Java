package servicios;

import datos.UsuarioDAO;
import datos.ProductoDAO;
import datos.CompraDAO;
import modelo.Usuario;
import modelo.Producto;
import modelo.Compra;
import java.util.List;

public class ServicioAdmin {
    private UsuarioDAO usuarioDAO;
    private ProductoDAO productoDAO;
    private CompraDAO compraDAO;

    public ServicioAdmin() {
        this.usuarioDAO = new UsuarioDAO();
        this.productoDAO = new ProductoDAO();
        this.compraDAO = new CompraDAO();
    }

    // ==================== GESTIÓN DE USUARIOS ====================

    /**
     * Lista todos los usuarios del sistema
     * @return Lista de usuarios
     */
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listarTodos();
    }

    /**
     * Busca un usuario por código
     * @param codigo Código del usuario
     * @return Usuario encontrado o null
     */
    public Usuario buscarUsuario(int codigo) {
        return usuarioDAO.obtenerPorCodigo(codigo);
    }

    /**
     * Agrega un nuevo usuario
     * @param nombre Nombre del usuario
     * @param usuario Nombre de usuario
     * @param contrasena Contraseña
     * @return true si se agregó exitosamente, false en caso contrario
     */
    public boolean agregarUsuario(String nombre, String usuario, String contrasena) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        if (usuario == null || usuario.trim().isEmpty()) {
            return false;
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            return false;
        }

        // Verificar que el usuario no exista
        if (usuarioDAO.existeUsuario(usuario.trim())) {
            return false;
        }

        Usuario nuevoUsuario = new Usuario(nombre.trim(), usuario.trim(), contrasena, 0.0);
        return usuarioDAO.insertar(nuevoUsuario);
    }

    /**
     * Actualiza un usuario
     * @param usuario Usuario con los datos actualizados
     * @return true si se actualizó exitosamente, false en caso contrario
     */
    public boolean actualizarUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        return usuarioDAO.actualizar(usuario);
    }

    /**
     * Elimina un usuario
     * @param codigo Código del usuario
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public boolean eliminarUsuario(int codigo) {
        return usuarioDAO.eliminar(codigo);
    }

    // ==================== GESTIÓN DE PRODUCTOS ====================

    /**
     * Lista todos los productos
     * @return Lista de productos
     */
    public List<Producto> listarProductos() {
        return productoDAO.listarTodos();
    }

    /**
     * Busca un producto por código
     * @param codigo Código del producto
     * @return Producto encontrado o null
     */
    public Producto buscarProducto(int codigo) {
        return productoDAO.obtenerPorCodigo(codigo);
    }

    /**
     * Agrega un nuevo producto
     * @param descripcion Descripción del producto
     * @param cantidad Cantidad inicial
     * @param precio Precio
     * @return true si se agregó exitosamente, false en caso contrario
     */
    public boolean agregarProducto(String descripcion, int cantidad, double precio) {
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
     * Actualiza un producto
     * @param producto Producto con los datos actualizados
     * @return true si se actualizó exitosamente, false en caso contrario
     */
    public boolean actualizarProducto(Producto producto) {
        if (producto == null) {
            return false;
        }
        return productoDAO.actualizar(producto);
    }

    /**
     * Elimina un producto
     * @param codigo Código del producto
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public boolean eliminarProducto(int codigo) {
        return productoDAO.eliminar(codigo);
    }

    // ==================== GESTIÓN DE COMPRAS ====================

    /**
     * Lista todas las compras
     * @return Lista de compras
     */
    public List<Compra> listarCompras() {
        return compraDAO.listarTodas();
    }

    /**
     * Busca una compra por número
     * @param numeroCompra Número de compra
     * @return Compra encontrada o null
     */
    public Compra buscarCompra(int numeroCompra) {
        return compraDAO.obtenerPorNumero(numeroCompra);
    }

    /**
     * Lista compras por cliente
     * @param codigoCliente Código del cliente
     * @return Lista de compras del cliente
     */
    public List<Compra> listarComprasPorCliente(int codigoCliente) {
        return compraDAO.listarPorCliente(codigoCliente);
    }

    /**
     * Elimina una compra
     * @param numeroCompra Número de compra
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public boolean eliminarCompra(int numeroCompra) {
        return compraDAO.eliminar(numeroCompra);
    }

    // ==================== REPORTES Y ESTADÍSTICAS ====================

    /**
     * Obtiene la cantidad total de usuarios
     * @return Cantidad de usuarios
     */
    public int obtenerTotalUsuarios() {
        return usuarioDAO.listarTodos().size();
    }

    /**
     * Obtiene la cantidad total de productos
     * @return Cantidad de productos
     */
    public int obtenerTotalProductos() {
        return productoDAO.listarTodos().size();
    }

    /**
     * Obtiene la cantidad total de compras
     * @return Cantidad de compras
     */
    public int obtenerTotalCompras() {
        return compraDAO.listarTodas().size();
    }

    /**
     * Obtiene productos con stock bajo (menos de 5 unidades)
     * @return Lista de productos con stock bajo
     */
    public List<Producto> obtenerProductosConStockBajo() {
        return productoDAO.listarTodos().stream()
                .filter(p -> p.getCantidad() < 5)
                .toList();
    }
}
