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
        // Primero verificamos si ya existe
        CarritoItem existente = obtenerItem(item.getCodigoCliente(), item.getCodigoProducto());
        
        if (existente != null) {
            // Actualizar cantidad
            return actualizarCantidad(item.getCodigoCliente(), item.getCodigoProducto(), 
                                      existente.getCantidad() + item.getCantidad());
        }
        
        String sql = "INSERT INTO CarritoGuardado (CodigoCliente, CodigoProducto, Cantidad) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, item.getCodigoCliente());
            stmt.setInt(2, item.getCodigoProducto());
            stmt.setInt(3, item.getCantidad());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar item en carrito: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene un item específico del carrito
     */
    public CarritoItem obtenerItem(int codigoCliente, int codigoProducto) {
        String sql = """
            SELECT c.Id, c.CodigoCliente, c.CodigoProducto, c.Cantidad, c.FechaAgregado,
                   p.Descripcion, p.Precio
            FROM CarritoGuardado c
            JOIN Producto p ON c.CodigoProducto = p.Codigo
            WHERE c.CodigoCliente = ? AND c.CodigoProducto = ?
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoCliente);
            stmt.setInt(2, codigoProducto);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCarritoItem(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener item del carrito: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Lista todos los items del carrito de un usuario
     */
    public List<CarritoItem> listarPorCliente(int codigoCliente) {
        List<CarritoItem> items = new ArrayList<>();
        String sql = """
            SELECT c.Id, c.CodigoCliente, c.CodigoProducto, c.Cantidad, c.FechaAgregado,
                   p.Descripcion, p.Precio
            FROM CarritoGuardado c
            JOIN Producto p ON c.CodigoProducto = p.Codigo
            WHERE c.CodigoCliente = ?
            ORDER BY c.FechaAgregado DESC
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoCliente);
            
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
    public boolean actualizarCantidad(int codigoCliente, int codigoProducto, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            return eliminarItem(codigoCliente, codigoProducto);
        }
        
        String sql = "UPDATE CarritoGuardado SET Cantidad = ? WHERE CodigoCliente = ? AND CodigoProducto = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nuevaCantidad);
            stmt.setInt(2, codigoCliente);
            stmt.setInt(3, codigoProducto);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cantidad: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un item del carrito
     */
    public boolean eliminarItem(int codigoCliente, int codigoProducto) {
        String sql = "DELETE FROM CarritoGuardado WHERE CodigoCliente = ? AND CodigoProducto = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoCliente);
            stmt.setInt(2, codigoProducto);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar item del carrito: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Vacía todo el carrito de un usuario
     */
    public boolean vaciarCarrito(int codigoCliente) {
        String sql = "DELETE FROM CarritoGuardado WHERE CodigoCliente = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoCliente);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al vaciar carrito: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cuenta items en el carrito
     */
    public int contarItems(int codigoCliente) {
        String sql = "SELECT COUNT(*) FROM CarritoGuardado WHERE CodigoCliente = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoCliente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar items: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Calcula el total del carrito
     */
    public double calcularTotal(int codigoCliente) {
        String sql = """
            SELECT SUM(c.Cantidad * p.Precio) as Total
            FROM CarritoGuardado c
            JOIN Producto p ON c.CodigoProducto = p.Codigo
            WHERE c.CodigoCliente = ?
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoCliente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("Total");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al calcular total: " + e.getMessage());
        }
        return 0;
    }
    
    private CarritoItem mapearCarritoItem(ResultSet rs) throws SQLException {
        CarritoItem item = new CarritoItem();
        item.setId(rs.getInt("Id"));
        item.setCodigoCliente(rs.getInt("CodigoCliente"));
        item.setCodigoProducto(rs.getInt("CodigoProducto"));
        item.setDescripcionProducto(rs.getString("Descripcion"));
        item.setPrecioProducto(rs.getDouble("Precio"));
        item.setCantidad(rs.getInt("Cantidad"));
        item.setFechaAgregado(rs.getTimestamp("FechaAgregado").toLocalDateTime());
        return item;
    }
}
