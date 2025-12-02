package datos;

import modelo.DetalleCompra;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleCompraDAO {
    private ConexionMySQL conexionMySQL;

    public DetalleCompraDAO() {
        this.conexionMySQL = ConexionMySQL.obtenerInstancia();
    }

    // CREATE - Insertar un detalle de compra (usado internamente por CompraDAO)
    public boolean insertar(DetalleCompra detalle, Connection conn) {
        boolean resultado = false;
        String sql = "INSERT INTO DetalleCompra (NumeroCompra, CodigoProducto, Cantidad) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detalle.getNumeroCompra());
            stmt.setInt(2, detalle.getCodigoProducto());
            stmt.setInt(3, detalle.getCantidad());
            
            int filasAfectadas = stmt.executeUpdate();
            resultado = filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar detalle de compra: " + e.getMessage());
        }
        
        return resultado;
    }

    // CREATE - Insertar un detalle de compra (versión pública)
    public boolean insertar(DetalleCompra detalle) {
        boolean resultado = false;
        
        try (Connection conn = conexionMySQL.obtenerConexion()) {
            resultado = insertar(detalle, conn);
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión: " + e.getMessage());
        }
        
        return resultado;
    }

    // READ - Listar detalles por número de compra
    public List<DetalleCompra> listarPorCompra(int numeroCompra) {
        List<DetalleCompra> detalles = new ArrayList<>();
        String sql = "SELECT dc.*, p.Descripcion, p.Precio " +
                     "FROM DetalleCompra dc " +
                     "INNER JOIN Producto p ON dc.CodigoProducto = p.Codigo " +
                     "WHERE dc.NumeroCompra = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, numeroCompra);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleCompra detalle = new DetalleCompra();
                    detalle.setNumeroCompra(rs.getInt("NumeroCompra"));
                    detalle.setCodigoProducto(rs.getInt("CodigoProducto"));
                    detalle.setCantidad(rs.getInt("Cantidad"));
                    detalle.setDescripcionProducto(rs.getString("Descripcion"));
                    detalle.setPrecioUnitario(rs.getDouble("Precio"));
                    detalles.add(detalle);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar detalles de compra: " + e.getMessage());
            e.printStackTrace();
        }
        
        return detalles;
    }

    // READ - Obtener un detalle específico
    public DetalleCompra obtener(int numeroCompra, int codigoProducto) {
        String sql = "SELECT dc.*, p.Descripcion, p.Precio " +
                     "FROM DetalleCompra dc " +
                     "INNER JOIN Producto p ON dc.CodigoProducto = p.Codigo " +
                     "WHERE dc.NumeroCompra = ? AND dc.CodigoProducto = ?";
        DetalleCompra detalle = null;
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, numeroCompra);
            stmt.setInt(2, codigoProducto);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    detalle = new DetalleCompra();
                    detalle.setNumeroCompra(rs.getInt("NumeroCompra"));
                    detalle.setCodigoProducto(rs.getInt("CodigoProducto"));
                    detalle.setCantidad(rs.getInt("Cantidad"));
                    detalle.setDescripcionProducto(rs.getString("Descripcion"));
                    detalle.setPrecioUnitario(rs.getDouble("Precio"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener detalle: " + e.getMessage());
            e.printStackTrace();
        }
        
        return detalle;
    }

    // UPDATE - Actualizar cantidad de un detalle
    public boolean actualizar(DetalleCompra detalle) {
        boolean resultado = false;
        String sql = "UPDATE DetalleCompra SET Cantidad = ? WHERE NumeroCompra = ? AND CodigoProducto = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, detalle.getCantidad());
            stmt.setInt(2, detalle.getNumeroCompra());
            stmt.setInt(3, detalle.getCodigoProducto());
            
            int filasAfectadas = stmt.executeUpdate();
            resultado = filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar detalle: " + e.getMessage());
        }
        
        return resultado;
    }

    // DELETE - Eliminar un detalle específico
    public boolean eliminar(int numeroCompra, int codigoProducto) {
        boolean resultado = false;
        String sql = "DELETE FROM DetalleCompra WHERE NumeroCompra = ? AND CodigoProducto = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, numeroCompra);
            stmt.setInt(2, codigoProducto);
            
            int filasAfectadas = stmt.executeUpdate();
            resultado = filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar detalle: " + e.getMessage());
        }
        
        return resultado;
    }

    // DELETE - Eliminar todos los detalles de una compra (usado internamente por CompraDAO)
    public boolean eliminarPorCompra(int numeroCompra, Connection conn) {
        boolean resultado = false;
        String sql = "DELETE FROM DetalleCompra WHERE NumeroCompra = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroCompra);
            stmt.executeUpdate();
            resultado = true;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar detalles de compra: " + e.getMessage());
        }
        
        return resultado;
    }

    // DELETE - Eliminar todos los detalles de una compra (versión pública)
    public boolean eliminarPorCompra(int numeroCompra) {
        boolean resultado = false;
        
        try (Connection conn = conexionMySQL.obtenerConexion()) {
            resultado = eliminarPorCompra(numeroCompra, conn);
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión: " + e.getMessage());
        }
        
        return resultado;
    }
}
