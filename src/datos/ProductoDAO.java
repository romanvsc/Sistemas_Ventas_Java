package datos;

import modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private ConexionMySQL conexionMySQL;

    public ProductoDAO() {
        this.conexionMySQL = ConexionMySQL.obtenerInstancia();
    }

    // CREATE - Insertar un nuevo producto
    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO Producto (Descripcion, Cantidad, Precio) VALUES (?, ?, ?)";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, producto.getDescripcion());
            stmt.setInt(2, producto.getCantidad());
            stmt.setDouble(3, producto.getPrecio());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        producto.setCodigo(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    // READ - Obtener producto por código
    public Producto obtenerPorCodigo(int codigo) {
        String sql = "SELECT * FROM Producto WHERE Codigo = ?";
        Producto producto = null;
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                producto = new Producto();
                producto.setCodigo(rs.getInt("Codigo"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setCantidad(rs.getInt("Cantidad"));
                producto.setPrecio(rs.getDouble("Precio"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener producto: " + e.getMessage());
        }
        
        return producto;
    }

    // READ - Listar todos los productos
    public List<Producto> listarTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto ORDER BY Descripcion";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigo(rs.getInt("Codigo"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setCantidad(rs.getInt("Cantidad"));
                producto.setPrecio(rs.getDouble("Precio"));
                productos.add(producto);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        
        return productos;
    }

    // READ - Listar productos disponibles (con stock)
    public List<Producto> listarDisponibles() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Cantidad > 0 ORDER BY Descripcion";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigo(rs.getInt("Codigo"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setCantidad(rs.getInt("Cantidad"));
                producto.setPrecio(rs.getDouble("Precio"));
                productos.add(producto);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar productos disponibles: " + e.getMessage());
        }
        
        return productos;
    }

    // UPDATE - Actualizar producto
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Producto SET Descripcion = ?, Cantidad = ?, Precio = ? WHERE Codigo = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getDescripcion());
            stmt.setInt(2, producto.getCantidad());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getCodigo());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    // DELETE - Eliminar producto
    public boolean eliminar(int codigo) {
        String sql = "DELETE FROM Producto WHERE Codigo = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigo);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    // Actualizar stock del producto (restar cantidad)
    public boolean actualizarStock(int codigo, int cantidadVendida) {
        String sql = "UPDATE Producto SET Cantidad = Cantidad - ? WHERE Codigo = ? AND Cantidad >= ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cantidadVendida);
            stmt.setInt(2, codigo);
            stmt.setInt(3, cantidadVendida);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    // Verificar si hay stock suficiente
    public boolean hayStockSuficiente(int codigo, int cantidadRequerida) {
        String sql = "SELECT Cantidad FROM Producto WHERE Codigo = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("Cantidad") >= cantidadRequerida;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar stock: " + e.getMessage());
        }
        
        return false;
    }
}
