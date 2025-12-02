package modelo;

public class Usuario {
    private int codigo;
    private String nombre;
    private String usuario;
    private String contrasena;
    private double presupuesto;
    private String email;
    private String telefono;
    private String direccion;
    private String preguntaSeguridad;
    private String respuestaSeguridad;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor completo
    public Usuario(int codigo, String nombre, String usuario, String contrasena, double presupuesto) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.presupuesto = presupuesto;
    }
    
    // Constructor sin presupuesto (compatibilidad)
    public Usuario(int codigo, String nombre, String usuario, String contrasena) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.presupuesto = 0.0;
    }

    // Constructor sin código (para inserción)
    public Usuario(String nombre, String usuario, String contrasena, double presupuesto) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.presupuesto = presupuesto;
    }

    // Getters y Setters
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }
    
    // Nuevos getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPreguntaSeguridad() {
        return preguntaSeguridad;
    }

    public void setPreguntaSeguridad(String preguntaSeguridad) {
        this.preguntaSeguridad = preguntaSeguridad;
    }

    public String getRespuestaSeguridad() {
        return respuestaSeguridad;
    }

    public void setRespuestaSeguridad(String respuestaSeguridad) {
        this.respuestaSeguridad = respuestaSeguridad;
    }
    
    /**
     * Verifica si el usuario tiene configurada la recuperación de contraseña
     */
    public boolean tieneRecuperacionConfigurada() {
        boolean resultado = false;
        if (preguntaSeguridad != null && !preguntaSeguridad.isEmpty() &&
            respuestaSeguridad != null && !respuestaSeguridad.isEmpty()) {
            resultado = true;
        }
        return resultado;
    }
    
    /**
     * Verifica la respuesta de seguridad
     */
    public boolean verificarRespuestaSeguridad(String respuesta) {
        boolean resultado = false;
        if (respuestaSeguridad != null && respuesta != null) {
            if (respuestaSeguridad.equalsIgnoreCase(respuesta.trim())) {
                resultado = true;
            }
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "codigo=" + codigo +
                ", nombre='" + nombre + '\'' +
                ", usuario='" + usuario + '\'' +
                ", email='" + email + '\'' +
                ", presupuesto=" + presupuesto +
                '}';
    }
}
