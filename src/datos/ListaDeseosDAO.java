package datos;

import modelo.ItemDeseo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para manejar la lista de deseos de usuarios
 * Adaptado a la estructura existente de la tabla listadeseos:
 * - CodigoUsuario (en lugar de CodigoCliente)
 * - CodigoProducto
 * - FechaAgregado
 */
public class ListaDeseosDAO {
    
    /**
     * Agrega un producto a la lista de deseos
     */
    public boolean agregar(int codigoUsuario, int codigoProducto, boolean notificar) {
        // Verificar si ya existe
        if (existe(codigoUsuario, codigoProducto)) {
            return true; // Ya está en la lista
        }
        
        String sql = "INSERT INTO listadeseos (CodigoUsuario, CodigoProducto) VALUES (?, ?)";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoUsuario);
            stmt.setInt(2, codigoProducto);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al agregar a lista de deseos: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si un producto ya está en la lista de deseos
     */
    public boolean existe(int codigoUsuario, int codigoProducto) {
        String sql = "SELECT 1 FROM listadeseos WHERE CodigoUsuario = ? AND CodigoProducto = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoUsuario);
            stmt.setInt(2, codigoProducto);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar lista de deseos: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Lista todos los items de deseos de un usuario
     */
    public List<ItemDeseo> listarPorCliente(int codigoUsuario) {
        List<ItemDeseo> items = new ArrayList<>();
        String sql = """
            SELECT ld.CodigoUsuario, ld.CodigoProducto, ld.FechaAgregado,
                   p.Descripcion, p.Precio, p.Cantidad as Stock
            FROM listadeseos ld
            JOIN Producto p ON ld.CodigoProducto = p.Codigo
            WHERE ld.CodigoUsuario = ?
            ORDER BY ld.FechaAgregado DESC
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapearItemDeseo(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar deseos: " + e.getMessage());
        }
        return items;
    }
    
    /**
     * Elimina un producto de la lista de deseos
     */
    public boolean eliminar(int codigoUsuario, int codigoProducto) {
        String sql = "DELETE FROM listadeseos WHERE CodigoUsuario = ? AND CodigoProducto = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoUsuario);
            stmt.setInt(2, codigoProducto);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar de lista de deseos: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Vacía toda la lista de deseos de un usuario
     */
    public boolean vaciar(int codigoUsuario) {
        String sql = "DELETE FROM listadeseos WHERE CodigoUsuario = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoUsuario);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al vaciar lista de deseos: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Actualiza la preferencia de notificación (no soportado en esta estructura)
     */
    public boolean actualizarNotificacion(int codigoUsuario, int codigoProducto, boolean notificar) {
        // La tabla actual no tiene columna Notificar, retornamos true para no romper
        return true;
    }
    
    /**
     * Cuenta items en la lista de deseos
     */
    public int contar(int codigoUsuario) {
        String sql = "SELECT COUNT(*) FROM listadeseos WHERE CodigoUsuario = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar deseos: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Mueve un item de deseos al carrito
     */
    public boolean moverAlCarrito(int codigoUsuario, int codigoProducto) {
        CarritoDAO carritoDAO = new CarritoDAO();
        
        // Obtener info del producto
        ProductoDAO productoDAO = new ProductoDAO();
        var producto = productoDAO.obtenerPorCodigo(codigoProducto);
        
        if (producto == null) return false;
        
        // Agregar al carrito
        modelo.CarritoItem item = new modelo.CarritoItem();
        item.setCodigoCliente(codigoUsuario);
        item.setCodigoProducto(codigoProducto);
        item.setCantidad(1);
        
        if (carritoDAO.guardarItem(item)) {
            // Eliminar de deseos
            return eliminar(codigoUsuario, codigoProducto);
        }
        
        return false;
    }
    
    /**
     * Obtiene productos con stock disponible en la lista de deseos
     */
    public List<ItemDeseo> obtenerParaNotificar(int codigoUsuario) {
        List<ItemDeseo> items = new ArrayList<>();
        String sql = """
            SELECT ld.CodigoUsuario, ld.CodigoProducto, ld.FechaAgregado,
                   p.Descripcion, p.Precio, p.Cantidad as Stock
            FROM listadeseos ld
            JOIN Producto p ON ld.CodigoProducto = p.Codigo
            WHERE ld.CodigoUsuario = ? AND p.Cantidad > 0
            ORDER BY ld.FechaAgregado DESC
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapearItemDeseo(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener para notificar: " + e.getMessage());
        }
        return items;
    }
    
    private ItemDeseo mapearItemDeseo(ResultSet rs) throws SQLException {
        ItemDeseo item = new ItemDeseo();
        item.setId(0); // La tabla no tiene Id
        item.setCodigoCliente(rs.getInt("CodigoUsuario"));
        item.setCodigoProducto(rs.getInt("CodigoProducto"));
        item.setDescripcionProducto(rs.getString("Descripcion"));
        item.setPrecioProducto(rs.getDouble("Precio"));
        item.setStockProducto(rs.getInt("Stock"));
        
        // Manejar FechaAgregado que puede ser null
        Timestamp ts = rs.getTimestamp("FechaAgregado");
        if (ts != null) {
            item.setFechaAgregado(ts.toLocalDateTime());
        } else {
            item.setFechaAgregado(java.time.LocalDateTime.now());
        }
        
        item.setNotificar(false); // La tabla no tiene Notificar
        return item;
    }
}
