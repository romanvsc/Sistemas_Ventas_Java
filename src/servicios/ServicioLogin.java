package servicios;

import datos.UsuarioDAO;
import modelo.Usuario;

public class ServicioLogin {
    private UsuarioDAO usuarioDAO;

    public ServicioLogin() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Valida las credenciales del usuario
     * @param usuario Nombre de usuario
     * @param contrasena Contraseña
     * @return Usuario si las credenciales son válidas, null en caso contrario
     */
    public Usuario validarCredenciales(String usuario, String contrasena) {
        // Validar que los campos no estén vacíos
        if (usuario == null || usuario.trim().isEmpty()) {
            return null;
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            return null;
        }

        // Buscar usuario en la base de datos
        Usuario usuarioEncontrado = usuarioDAO.obtenerPorCredenciales(usuario.trim(), contrasena);
        
        return usuarioEncontrado;
    }

    /**
     * Registra un nuevo usuario en el sistema
     * @param nombre Nombre completo
     * @param usuario Nombre de usuario
     * @param contrasena Contraseña
     * @param presupuesto Presupuesto inicial
     * @return true si se registró exitosamente, false en caso contrario
     */
    public boolean registrarUsuario(String nombre, String usuario, String contrasena, double presupuesto) {
        // Validar que los campos no estén vacíos
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        if (usuario == null || usuario.trim().isEmpty()) {
            return false;
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            return false;
        }
        
        // Validar presupuesto
        if (presupuesto < 0) {
            return false;
        }

        // Verificar que el usuario no exista
        if (usuarioDAO.existeUsuario(usuario.trim())) {
            return false;
        }

        // Crear nuevo usuario
        Usuario nuevoUsuario = new Usuario(nombre.trim(), usuario.trim(), contrasena, presupuesto);
        
        return usuarioDAO.insertar(nuevoUsuario);
    }

    /**
     * Verifica si un nombre de usuario ya existe
     * @param usuario Nombre de usuario
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            return false;
        }
        return usuarioDAO.existeUsuario(usuario.trim());
    }

    /**
     * Cambia la contraseña de un usuario
     * @param codigoUsuario Código del usuario
     * @param contrasenaActual Contraseña actual
     * @param nuevaContrasena Nueva contraseña
     * @return true si se cambió exitosamente, false en caso contrario
     */
    public boolean cambiarContrasena(int codigoUsuario, String contrasenaActual, String nuevaContrasena) {
        // Obtener usuario
        Usuario usuario = usuarioDAO.obtenerPorCodigo(codigoUsuario);
        
        if (usuario == null) {
            return false;
        }

        // Verificar contraseña actual
        if (!usuario.getContrasena().equals(contrasenaActual)) {
            return false;
        }

        // Validar nueva contraseña
        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            return false;
        }

        // Actualizar contraseña
        usuario.setContrasena(nuevaContrasena.trim());
        
        return usuarioDAO.actualizar(usuario);
    }
}
