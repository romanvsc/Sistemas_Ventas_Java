package datos;

import modelo.Descuento;
import modelo.Descuento.TipoDescuento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para manejar cupones y códigos de descuento
 */
public class DescuentoDAO {
    
    /**
     * Busca un descuento por su código
     */
    public Descuento buscarPorCodigo(String codigo) {
        String sql = """
            SELECT Codigo, Descripcion, TipoDescuento, Valor, FechaInicio, FechaFin,
                   Activo, UsosMaximos, UsosActuales, MontoMinimo
            FROM Descuento
            WHERE Codigo = ?
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigo.toUpperCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearDescuento(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar descuento: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Lista todos los descuentos
     */
    public List<Descuento> listarTodos() {
        List<Descuento> descuentos = new ArrayList<>();
        String sql = """
            SELECT Codigo, Descripcion, TipoDescuento, Valor, FechaInicio, FechaFin,
                   Activo, UsosMaximos, UsosActuales, MontoMinimo
            FROM Descuento
            ORDER BY FechaFin DESC
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                descuentos.add(mapearDescuento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar descuentos: " + e.getMessage());
        }
        return descuentos;
    }
    
    /**
     * Lista solo los descuentos activos y vigentes
     */
    public List<Descuento> listarActivos() {
        List<Descuento> descuentos = new ArrayList<>();
        String sql = """
            SELECT Codigo, Descripcion, TipoDescuento, Valor, FechaInicio, FechaFin,
                   Activo, UsosMaximos, UsosActuales, MontoMinimo
            FROM Descuento
            WHERE Activo = TRUE
              AND (FechaInicio IS NULL OR FechaInicio <= CURDATE())
              AND (FechaFin IS NULL OR FechaFin >= CURDATE())
              AND (UsosMaximos IS NULL OR UsosActuales < UsosMaximos)
            ORDER BY FechaFin ASC
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                descuentos.add(mapearDescuento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar descuentos activos: " + e.getMessage());
        }
        return descuentos;
    }
    
    /**
     * Crea un nuevo descuento
     */
    public boolean crear(Descuento descuento) {
        String sql = """
            INSERT INTO Descuento (Codigo, Descripcion, TipoDescuento, Valor, FechaInicio, 
                                   FechaFin, Activo, UsosMaximos, MontoMinimo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, descuento.getCodigo().toUpperCase());
            stmt.setString(2, descuento.getDescripcion());
            stmt.setString(3, descuento.getTipoDescuento().name());
            stmt.setDouble(4, descuento.getValor());
            
            if (descuento.getFechaInicio() != null) {
                stmt.setDate(5, Date.valueOf(descuento.getFechaInicio()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            if (descuento.getFechaFin() != null) {
                stmt.setDate(6, Date.valueOf(descuento.getFechaFin()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            
            stmt.setBoolean(7, descuento.isActivo());
            
            if (descuento.getUsosMaximos() != null) {
                stmt.setInt(8, descuento.getUsosMaximos());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            
            stmt.setDouble(9, descuento.getMontoMinimo());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear descuento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Actualiza un descuento existente
     */
    public boolean actualizar(Descuento descuento) {
        String sql = """
            UPDATE Descuento SET
                Descripcion = ?,
                TipoDescuento = ?,
                Valor = ?,
                FechaInicio = ?,
                FechaFin = ?,
                Activo = ?,
                UsosMaximos = ?,
                MontoMinimo = ?
            WHERE Codigo = ?
        """;
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, descuento.getDescripcion());
            stmt.setString(2, descuento.getTipoDescuento().name());
            stmt.setDouble(3, descuento.getValor());
            
            if (descuento.getFechaInicio() != null) {
                stmt.setDate(4, Date.valueOf(descuento.getFechaInicio()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            
            if (descuento.getFechaFin() != null) {
                stmt.setDate(5, Date.valueOf(descuento.getFechaFin()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setBoolean(6, descuento.isActivo());
            
            if (descuento.getUsosMaximos() != null) {
                stmt.setInt(7, descuento.getUsosMaximos());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            stmt.setDouble(8, descuento.getMontoMinimo());
            stmt.setString(9, descuento.getCodigo().toUpperCase());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar descuento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Incrementa el contador de usos de un descuento
     */
    public boolean incrementarUsos(String codigo) {
        String sql = "UPDATE Descuento SET UsosActuales = UsosActuales + 1 WHERE Codigo = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigo.toUpperCase());
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al incrementar usos: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Desactiva un descuento
     */
    public boolean desactivar(String codigo) {
        String sql = "UPDATE Descuento SET Activo = FALSE WHERE Codigo = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigo.toUpperCase());
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al desactivar descuento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un descuento
     */
    public boolean eliminar(String codigo) {
        String sql = "DELETE FROM Descuento WHERE Codigo = ?";
        
        try (Connection conn = ConexionMySQL.obtenerInstancia().obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigo.toUpperCase());
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar descuento: " + e.getMessage());
            return false;
        }
    }
    
    private Descuento mapearDescuento(ResultSet rs) throws SQLException {
        Descuento d = new Descuento();
        d.setCodigo(rs.getString("Codigo"));
        d.setDescripcion(rs.getString("Descripcion"));
        d.setTipoDescuento(TipoDescuento.valueOf(rs.getString("TipoDescuento")));
        d.setValor(rs.getDouble("Valor"));
        
        Date fechaInicio = rs.getDate("FechaInicio");
        if (fechaInicio != null) {
            d.setFechaInicio(fechaInicio.toLocalDate());
        }
        
        Date fechaFin = rs.getDate("FechaFin");
        if (fechaFin != null) {
            d.setFechaFin(fechaFin.toLocalDate());
        }
        
        d.setActivo(rs.getBoolean("Activo"));
        
        int usosMaximos = rs.getInt("UsosMaximos");
        if (!rs.wasNull()) {
            d.setUsosMaximos(usosMaximos);
        }
        
        d.setUsosActuales(rs.getInt("UsosActuales"));
        d.setMontoMinimo(rs.getDouble("MontoMinimo"));
        
        return d;
    }
}
