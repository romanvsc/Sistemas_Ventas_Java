package datos;

import modelo.Compra;
import modelo.DetalleCompra;
import modelo.EstadisticaProducto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {
    private ConexionMySQL conexionMySQL;
    private DetalleCompraDAO detalleCompraDAO;

    public CompraDAO() {
        this.conexionMySQL = ConexionMySQL.obtenerInstancia();
        this.detalleCompraDAO = new DetalleCompraDAO();
    }

    // CREATE - Insertar una nueva compra con sus detalles (transacción)
    public boolean insertar(Compra compra) {
        String sql = "INSERT INTO Compra (Fecha, CodigoCliente) VALUES (?, ?)";
        Connection conn = null;
        
        try {
            conn = conexionMySQL.obtenerConexion();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Insertar la compra
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setDate(1, Date.valueOf(compra.getFecha()));
                stmt.setInt(2, compra.getCodigoCliente());
                
                int filasAfectadas = stmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int numeroCompra = rs.getInt(1);
                            compra.setNumeroCompra(numeroCompra);
                            
                            // Insertar los detalles de la compra
                            for (DetalleCompra detalle : compra.getDetalles()) {
                                detalle.setNumeroCompra(numeroCompra);
                                if (!detalleCompraDAO.insertar(detalle, conn)) {
                                    conn.rollback();
                                    return false;
                                }
                            }
                            
                            conn.commit(); // Confirmar transacción
                            return true;
                        }
                    }
                }
            }
            
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar compra: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error al restaurar autocommit: " + e.getMessage());
                }
            }
        }
    }

    // READ - Obtener compra por número
    public Compra obtenerPorNumero(int numeroCompra) {
        String sql = "SELECT * FROM Compra WHERE NumeroCompra = ?";
        Compra compra = null;
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, numeroCompra);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    compra = new Compra();
                    compra.setNumeroCompra(rs.getInt("NumeroCompra"));
                    compra.setFecha(rs.getDate("Fecha").toLocalDate());
                    compra.setCodigoCliente(rs.getInt("CodigoCliente"));
                    
                    // Cargar los detalles
                    List<DetalleCompra> detalles = detalleCompraDAO.listarPorCompra(numeroCompra);
                    compra.setDetalles(detalles);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener compra: " + e.getMessage());
            e.printStackTrace();
        }
        
        return compra;
    }

    // READ - Listar todas las compras
    public List<Compra> listarTodas() {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM Compra ORDER BY Fecha DESC, NumeroCompra DESC";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Primero cargar todas las compras sin los detalles
            while (rs.next()) {
                Compra compra = new Compra();
                compra.setNumeroCompra(rs.getInt("NumeroCompra"));
                compra.setFecha(rs.getDate("Fecha").toLocalDate());
                compra.setCodigoCliente(rs.getInt("CodigoCliente"));
                
                compras.add(compra);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar compras: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Ahora cargar los detalles de cada compra (después de cerrar el ResultSet)
        for (Compra compra : compras) {
            List<DetalleCompra> detalles = detalleCompraDAO.listarPorCompra(compra.getNumeroCompra());
            compra.setDetalles(detalles);
        }
        
        return compras;
    }

    // READ - Listar compras por cliente
    public List<Compra> listarPorCliente(int codigoCliente) {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM Compra WHERE CodigoCliente = ? ORDER BY Fecha DESC, NumeroCompra DESC";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigoCliente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                // Primero cargar todas las compras sin los detalles
                while (rs.next()) {
                    Compra compra = new Compra();
                    compra.setNumeroCompra(rs.getInt("NumeroCompra"));
                    compra.setFecha(rs.getDate("Fecha").toLocalDate());
                    compra.setCodigoCliente(rs.getInt("CodigoCliente"));
                    
                    compras.add(compra);
                }
            }
            
            // Ahora cargar los detalles de cada compra (después de cerrar el ResultSet principal)
            for (Compra compra : compras) {
                List<DetalleCompra> detalles = detalleCompraDAO.listarPorCompra(compra.getNumeroCompra());
                compra.setDetalles(detalles);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar compras por cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return compras;
    }

    // UPDATE - Actualizar compra (solo fecha y cliente, no detalles)
    public boolean actualizar(Compra compra) {
        String sql = "UPDATE Compra SET Fecha = ?, CodigoCliente = ? WHERE NumeroCompra = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(compra.getFecha()));
            stmt.setInt(2, compra.getCodigoCliente());
            stmt.setInt(3, compra.getNumeroCompra());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar compra: " + e.getMessage());
            return false;
        }
    }

    // DELETE - Eliminar compra y sus detalles (transacción)
    public boolean eliminar(int numeroCompra) {
        Connection conn = null;
        
        try {
            conn = conexionMySQL.obtenerConexion();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Primero eliminar los detalles
            if (!detalleCompraDAO.eliminarPorCompra(numeroCompra, conn)) {
                conn.rollback();
                return false;
            }
            
            // Luego eliminar la compra
            String sql = "DELETE FROM Compra WHERE NumeroCompra = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, numeroCompra);
                int filasAfectadas = stmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    conn.commit(); // Confirmar transacción
                    return true;
                }
            }
            
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar compra: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error al restaurar autocommit: " + e.getMessage());
                }
            }
        }
    }

    // ==================== REPORTES Y ESTADÍSTICAS ====================

    /**
     * Obtiene los productos más vendidos (Top N)
     * @param limite Cantidad de productos a retornar
     * @return Lista de estadísticas de productos ordenados por cantidad vendida
     */
    public List<EstadisticaProducto> obtenerProductosMasVendidos(int limite) {
        List<EstadisticaProducto> estadisticas = new ArrayList<>();
        String sql = """
            SELECT p.Codigo, p.Descripcion, p.Cantidad as StockActual,
                   COALESCE(SUM(dc.Cantidad), 0) as CantidadVendida,
                   COALESCE(SUM(dc.Cantidad * p.Precio), 0) as MontoTotal
            FROM Producto p
            LEFT JOIN DetalleCompra dc ON p.Codigo = dc.CodigoProducto
            GROUP BY p.Codigo, p.Descripcion, p.Cantidad
            ORDER BY CantidadVendida DESC
            LIMIT ?
            """;
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EstadisticaProducto est = new EstadisticaProducto();
                    est.setCodigoProducto(rs.getInt("Codigo"));
                    est.setDescripcion(rs.getString("Descripcion"));
                    est.setStockActual(rs.getInt("StockActual"));
                    est.setCantidadVendida(rs.getInt("CantidadVendida"));
                    est.setMontoTotal(rs.getDouble("MontoTotal"));
                    estadisticas.add(est);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener productos más vendidos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return estadisticas;
    }

    /**
     * Obtiene el total de ventas realizadas
     * @return Monto total de todas las ventas
     */
    public double obtenerTotalVentas() {
        String sql = "SELECT COALESCE(SUM(dc.Cantidad * p.Precio), 0) as Total FROM DetalleCompra dc INNER JOIN Producto p ON dc.CodigoProducto = p.Codigo";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("Total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener total de ventas: " + e.getMessage());
        }
        
        return 0.0;
    }

    /**
     * Obtiene la cantidad total de productos vendidos
     * @return Cantidad total de unidades vendidas
     */
    public int obtenerCantidadProductosVendidos() {
        String sql = "SELECT COALESCE(SUM(Cantidad), 0) as Total FROM DetalleCompra";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("Total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener cantidad de productos vendidos: " + e.getMessage());
        }
        
        return 0;
    }
}
