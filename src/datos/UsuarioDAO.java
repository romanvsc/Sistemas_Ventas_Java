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
        // Verificar si tiene pregunta de seguridad configurada
        if (usuario.getPreguntaSeguridad() != null && !usuario.getPreguntaSeguridad().isEmpty()) {
            return insertarConSeguridad(usuario);
        }
        
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
    
    // CREATE - Insertar usuario con pregunta de seguridad
    private boolean insertarConSeguridad(Usuario usuario) {
        String sql = "INSERT INTO Cliente (Nombre, Usuario, Contrasena, presupuesto, PreguntaSeguridad, RespuestaSeguridad) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getUsuario());
            stmt.setString(3, usuario.getContrasena());
            stmt.setDouble(4, usuario.getPresupuesto());
            stmt.setString(5, usuario.getPreguntaSeguridad());
            stmt.setString(6, usuario.getRespuestaSeguridad());
            
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
            System.err.println("Error al insertar usuario con seguridad: " + e.getMessage());
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

    // ==================== MÉTODOS PARA GESTIÓN AVANZADA DE USUARIOS ====================
    
    /**
     * Obtiene un usuario por nombre de usuario (para recuperación de contraseña)
     */
    public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT * FROM Cliente WHERE Usuario = ?";
        Usuario usuario = null;
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                usuario = mapearUsuario(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por nombre: " + e.getMessage());
        }
        
        return usuario;
    }
    
    /**
     * Obtiene un usuario por email
     */
    public Usuario obtenerPorEmail(String email) {
        String sql = "SELECT * FROM Cliente WHERE Email = ?";
        Usuario usuario = null;
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                usuario = mapearUsuario(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por email: " + e.getMessage());
        }
        
        return usuario;
    }
    
    /**
     * Actualiza la contraseña de un usuario
     */
    public boolean actualizarContrasena(int codigoUsuario, String nuevaContrasena) {
        String sql = "UPDATE Cliente SET Contrasena = ? WHERE Codigo = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevaContrasena);
            stmt.setInt(2, codigoUsuario);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Actualiza el perfil del usuario (datos extendidos)
     */
    public boolean actualizarPerfil(Usuario usuario) {
        String sql = """
            UPDATE Cliente SET 
                Nombre = ?, 
                Email = ?, 
                Telefono = ?, 
                Direccion = ?,
                PreguntaSeguridad = ?,
                RespuestaSeguridad = ?
            WHERE Codigo = ?
        """;
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefono());
            stmt.setString(4, usuario.getDireccion());
            stmt.setString(5, usuario.getPreguntaSeguridad());
            stmt.setString(6, usuario.getRespuestaSeguridad());
            stmt.setInt(7, usuario.getCodigo());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar perfil: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Configura la pregunta y respuesta de seguridad
     */
    public boolean configurarRecuperacion(int codigoUsuario, String pregunta, String respuesta) {
        String sql = "UPDATE Cliente SET PreguntaSeguridad = ?, RespuestaSeguridad = ? WHERE Codigo = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, pregunta);
            stmt.setString(2, respuesta);
            stmt.setInt(3, codigoUsuario);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al configurar recuperación: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica la respuesta de seguridad
     */
    public boolean verificarRespuestaSeguridad(String nombreUsuario, String respuesta) {
        String sql = "SELECT RespuestaSeguridad FROM Cliente WHERE Usuario = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String respuestaGuardada = rs.getString("RespuestaSeguridad");
                if (respuestaGuardada != null && respuesta != null) {
                    return respuestaGuardada.equalsIgnoreCase(respuesta.trim());
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar respuesta: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Obtiene la pregunta de seguridad de un usuario
     */
    public String obtenerPreguntaSeguridad(String nombreUsuario) {
        String sql = "SELECT PreguntaSeguridad FROM Cliente WHERE Usuario = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("PreguntaSeguridad");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener pregunta: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Busca usuarios por nombre o usuario
     */
    public List<Usuario> buscar(String termino) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM Cliente WHERE LOWER(Nombre) LIKE LOWER(?) OR LOWER(Usuario) LIKE LOWER(?) ORDER BY Nombre";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String busqueda = "%" + termino + "%";
            stmt.setString(1, busqueda);
            stmt.setString(2, busqueda);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    /**
     * Cuenta el total de usuarios
     */
    public int contarUsuarios() {
        String sql = "SELECT COUNT(*) FROM Cliente";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar usuarios: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Mapea un ResultSet a un objeto Usuario
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setCodigo(rs.getInt("Codigo"));
        usuario.setNombre(rs.getString("Nombre"));
        usuario.setUsuario(rs.getString("Usuario"));
        usuario.setContrasena(rs.getString("Contrasena"));
        usuario.setPresupuesto(rs.getDouble("presupuesto"));
        
        // Intentar obtener campos extendidos (pueden no existir)
        try {
            usuario.setEmail(rs.getString("Email"));
            usuario.setTelefono(rs.getString("Telefono"));
            usuario.setDireccion(rs.getString("Direccion"));
            usuario.setPreguntaSeguridad(rs.getString("PreguntaSeguridad"));
            usuario.setRespuestaSeguridad(rs.getString("RespuestaSeguridad"));
        } catch (SQLException e) {
            // Columnas no existen, ignorar
        }
        
        return usuario;
    }
}
