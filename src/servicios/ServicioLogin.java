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
        if (usuario == null || usuario.trim().isEmpty()) {
            return null;
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            return null;
        }

        return usuarioDAO.obtenerPorCredenciales(usuario.trim(), contrasena);
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
        boolean registroExitoso = registrarUsuario(nombre, usuario, contrasena, presupuesto, null, null);
        return registroExitoso;
    }
    
    /**
     * Registra un nuevo usuario en el sistema con pregunta de seguridad
     * @param nombre Nombre completo
     * @param usuario Nombre de usuario
     * @param contrasena Contraseña
     * @param presupuesto Presupuesto inicial
     * @param preguntaSeguridad Pregunta de seguridad para recuperación
     * @param respuestaSeguridad Respuesta a la pregunta de seguridad
     * @return true si se registró exitosamente, false en caso contrario
     */
    public boolean registrarUsuario(String nombre, String usuario, String contrasena, double presupuesto,
                                    String preguntaSeguridad, String respuestaSeguridad) {
        boolean registroExitoso = false;

        String nombreTrim = nombre != null ? nombre.trim() : null;
        String usuarioTrim = usuario != null ? usuario.trim() : null;
        String contrasenaTrim = contrasena != null ? contrasena.trim() : null;

        boolean datosValidos = nombreTrim != null && !nombreTrim.isEmpty()
                && usuarioTrim != null && !usuarioTrim.isEmpty()
                && contrasenaTrim != null && !contrasenaTrim.isEmpty()
                && presupuesto >= 0;

        if (datosValidos) {
            boolean usuarioDisponible = !usuarioDAO.existeUsuario(usuarioTrim);
            if (usuarioDisponible) {
                Usuario nuevoUsuario = new Usuario(nombreTrim, usuarioTrim, contrasena, presupuesto);

                boolean tieneSeguridad = preguntaSeguridad != null && !preguntaSeguridad.trim().isEmpty()
                        && respuestaSeguridad != null && !respuestaSeguridad.trim().isEmpty();
                if (tieneSeguridad) {
                    nuevoUsuario.setPreguntaSeguridad(preguntaSeguridad.trim());
                    nuevoUsuario.setRespuestaSeguridad(respuestaSeguridad.trim());
                }

                registroExitoso = usuarioDAO.insertar(nuevoUsuario);
            }
        }

        return registroExitoso;
    }

    /**
     * Verifica si un nombre de usuario ya existe
     * @param usuario Nombre de usuario
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String usuario) {
        boolean existe = false;
        if (usuario != null && !usuario.trim().isEmpty()) {
            existe = usuarioDAO.existeUsuario(usuario.trim());
        }
        return existe;
    }

    /**
     * Cambia la contraseña de un usuario
     * @param codigoUsuario Código del usuario
     * @param contrasenaActual Contraseña actual
     * @param nuevaContrasena Nueva contraseña
     * @return true si se cambió exitosamente, false en caso contrario
     */
    public boolean cambiarContrasena(int codigoUsuario, String contrasenaActual, String nuevaContrasena) {
        boolean contrasenaCambiada = false;

        Usuario usuario = usuarioDAO.obtenerPorCodigo(codigoUsuario);
        if (usuario != null && usuario.getContrasena().equals(contrasenaActual)) {
            String nuevaContrasenaTrim = nuevaContrasena != null ? nuevaContrasena.trim() : null;
            boolean nuevaValida = nuevaContrasenaTrim != null && !nuevaContrasenaTrim.isEmpty();
            if (nuevaValida) {
                usuario.setContrasena(nuevaContrasenaTrim);
                contrasenaCambiada = usuarioDAO.actualizar(usuario);
            }
        }

        return contrasenaCambiada;
    }
}
