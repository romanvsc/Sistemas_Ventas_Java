package datos;

import modelo.CarritoItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para manejar el carrito persistente de usuarios
 */
public class CarritoDAO {
    
    /**
     * Guarda o actualiza un item en el carrito del usuario
     */
    public boolean guardarItem(CarritoItem item) {
        boolean resultado = false;
        
        // Primero verificamos si ya existe
        CarritoItem existente = obtenerItem(item.getCodigoCliente(), item.getCodigoProducto());
        
        if (existente != null) {
            // Actualizar cantidad
            resultado = actualizarCantidad(item.getCodigoCliente(), item.getCodigoProducto(), 
                                      existente.getCantidad() + item.getCantidad());
        } else {
            String sql = "INSERT INTO CarritoGuardado (CodigoUsuario, CodigoProducto, Cantidad) VALUES (?, ?, ?)";
            
            try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, item.getCodigoCliente());
                stmt.setInt(2, item.getCodigoProducto());
                stmt.setInt(3, item.getCantidad());
                
                resultado = stmt.executeUpdate() > 0;
                
            } catch (SQLException e) {
                System.err.println("Error al guardar item en carrito: " + e.getMessage());
                resultado = false;
            }
        }
        
        return resultado;
    }
    
    /**
     * Obtiene un item específico del carrito
     */
    public CarritoItem obtenerItem(int CodigoUsuario, int codigoProducto) {
        CarritoItem item = null;
        String sql = """
            SELECT c.CodigoUsuario, c.CodigoProducto, c.Cantidad, c.FechaAgregado,
                   p.Descripcion, p.Precio
            FROM CarritoGuardado c
            JOIN Producto p ON c.CodigoProducto = p.Codigo
            WHERE c.CodigoUsuario = ? AND c.CodigoProducto = ?
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, CodigoUsuario);
            stmt.setInt(2, codigoProducto);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    item = mapearCarritoItem(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener item del carrito: " + e.getMessage());
        }
        
        return item;
    }
    
    /**
     * Lista todos los items del carrito de un usuario
     */
    public List<CarritoItem> listarPorCliente(int CodigoUsuario) {
        List<CarritoItem> items = new ArrayList<>();
        String sql = """
            SELECT c.CodigoUsuario, c.CodigoProducto, c.Cantidad, c.FechaAgregado,
                   p.Descripcion, p.Precio
            FROM CarritoGuardado c
            JOIN Producto p ON c.CodigoProducto = p.Codigo
            WHERE c.CodigoUsuario = ?
            ORDER BY c.FechaAgregado DESC
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, CodigoUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapearCarritoItem(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar carrito: " + e.getMessage());
        }
        
        return items;
    }
    
    /**
     * Actualiza la cantidad de un item
     */
    public boolean actualizarCantidad(int CodigoUsuario, int codigoProducto, int nuevaCantidad) {
        boolean resultado = false;
        
        if (nuevaCantidad <= 0) {
            resultado = eliminarItem(CodigoUsuario, codigoProducto);
        } else {
            String sql = "UPDATE CarritoGuardado SET Cantidad = ? WHERE CodigoUsuario = ? AND CodigoProducto = ?";
            
            try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, nuevaCantidad);
                stmt.setInt(2, CodigoUsuario);
                stmt.setInt(3, codigoProducto);
                
                resultado = stmt.executeUpdate() > 0;
                
            } catch (SQLException e) {
                System.err.println("Error al actualizar cantidad: " + e.getMessage());
                resultado = false;
            }
        }
        
        return resultado;
    }
    
    /**
     * Elimina un item del carrito
     */
    public boolean eliminarItem(int CodigoUsuario, int codigoProducto) {
        boolean resultado = false;
        String sql = "DELETE FROM CarritoGuardado WHERE CodigoUsuario = ? AND CodigoProducto = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, CodigoUsuario);
            stmt.setInt(2, codigoProducto);
            
            resultado = stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar item del carrito: " + e.getMessage());
            resultado = false;
        }
        
        return resultado;
    }
    
    /**
     * Vacía todo el carrito de un usuario
     */
    public boolean vaciarCarrito(int CodigoUsuario) {
        boolean resultado = false;
        String sql = "DELETE FROM CarritoGuardado WHERE CodigoUsuario = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, CodigoUsuario);
            stmt.executeUpdate();
            resultado = true;
            
        } catch (SQLException e) {
            System.err.println("Error al vaciar carrito: " + e.getMessage());
            resultado = false;
        }
        
        return resultado;
    }
    
    /**
     * Cuenta items en el carrito
     */
    public int contarItems(int CodigoUsuario) {
        int cantidad = 0;
        String sql = "SELECT COUNT(*) FROM CarritoGuardado WHERE CodigoUsuario = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, CodigoUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cantidad = rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar items: " + e.getMessage());
        }
        
        return cantidad;
    }
    
    /**
     * Calcula el total del carrito
     */
    public double calcularTotal(int CodigoUsuario) {
        double total = 0;
        String sql = """
            SELECT SUM(c.Cantidad * p.Precio) as Total
            FROM CarritoGuardado c
            JOIN Producto p ON c.CodigoProducto = p.Codigo
            WHERE c.CodigoUsuario = ?
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, CodigoUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("Total");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al calcular total: " + e.getMessage());
        }
        
        return total;
    }
    
    private CarritoItem mapearCarritoItem(ResultSet rs) throws SQLException {
        CarritoItem item = new CarritoItem();
        item.setId(0); // La tabla no tiene Id
        item.setCodigoCliente(rs.getInt("CodigoUsuario"));
        item.setCodigoProducto(rs.getInt("CodigoProducto"));
        item.setDescripcionProducto(rs.getString("Descripcion"));
        item.setPrecioProducto(rs.getDouble("Precio"));
        item.setCantidad(rs.getInt("Cantidad"));
        
        // Manejar FechaAgregado que puede ser null
        Timestamp ts = rs.getTimestamp("FechaAgregado");
        if (ts != null) {
            item.setFechaAgregado(ts.toLocalDateTime());
        } else {
            item.setFechaAgregado(java.time.LocalDateTime.now());
        }
        
        return item;
    }
}
