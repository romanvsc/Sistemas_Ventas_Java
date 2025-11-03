package datos;

import modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private ConexionMySQL conexionMySQL;

    public UsuarioDAO() {
        this.conexionMySQL = ConexionMySQL.obtenerInstancia();
    }

    // CREATE - Insertar un nuevo usuario
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO Cliente (Nombre, Usuario, Contrasena, presupuesto) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getUsuario());
            stmt.setString(3, usuario.getContrasena());
            stmt.setDouble(4, usuario.getPresupuesto());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setCodigo(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    // READ - Obtener usuario por código
    public Usuario obtenerPorCodigo(int codigo) {
        String sql = "SELECT * FROM Cliente WHERE Codigo = ?";
        Usuario usuario = null;
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setCodigo(rs.getInt("Codigo"));
                usuario.setNombre(rs.getString("Nombre"));
                usuario.setUsuario(rs.getString("Usuario"));
                usuario.setContrasena(rs.getString("Contrasena"));
                usuario.setPresupuesto(rs.getDouble("presupuesto"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
        }
        
        return usuario;
    }

    // READ - Obtener usuario por nombre de usuario y contraseña (login)
    public Usuario obtenerPorCredenciales(String usuario, String contrasena) {
        String sql = "SELECT * FROM Cliente WHERE Usuario = ? AND Contrasena = ?";
        Usuario usuarioEncontrado = null;
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario);
            stmt.setString(2, contrasena);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                usuarioEncontrado = new Usuario();
                usuarioEncontrado.setCodigo(rs.getInt("Codigo"));
                usuarioEncontrado.setNombre(rs.getString("Nombre"));
                usuarioEncontrado.setUsuario(rs.getString("Usuario"));
                usuarioEncontrado.setContrasena(rs.getString("Contrasena"));
                usuarioEncontrado.setPresupuesto(rs.getDouble("presupuesto"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
        }
        
        return usuarioEncontrado;
    }

    // READ - Listar todos los usuarios
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM Cliente ORDER BY Nombre";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setCodigo(rs.getInt("Codigo"));
                usuario.setNombre(rs.getString("Nombre"));
                usuario.setUsuario(rs.getString("Usuario"));
                usuario.setContrasena(rs.getString("Contrasena"));
                usuario.setPresupuesto(rs.getDouble("presupuesto"));
                usuarios.add(usuario);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }

    // UPDATE - Actualizar usuario
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE Cliente SET Nombre = ?, Usuario = ?, Contrasena = ?, presupuesto = ? WHERE Codigo = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getUsuario());
            stmt.setString(3, usuario.getContrasena());
            stmt.setDouble(4, usuario.getPresupuesto());
            stmt.setInt(5, usuario.getCodigo());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
    
    // UPDATE - Actualizar solo presupuesto
    public boolean actualizarPresupuesto(int codigoUsuario, double nuevoPresupuesto) {
        String sql = "UPDATE Cliente SET presupuesto = ? WHERE Codigo = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, nuevoPresupuesto);
            stmt.setInt(2, codigoUsuario);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar presupuesto: " + e.getMessage());
            return false;
        }
    }

    // DELETE - Eliminar usuario
    public boolean eliminar(int codigo) {
        String sql = "DELETE FROM Cliente WHERE Codigo = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigo);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    // Verificar si existe un usuario con ese nombre de usuario
    public boolean existeUsuario(String usuario) {
        String sql = "SELECT COUNT(*) FROM Cliente WHERE Usuario = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de usuario: " + e.getMessage());
        }
        
        return false;
    }
}
