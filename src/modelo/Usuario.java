package modelo;

public class Usuario {
    private int codigo;
    private String nombre;
    private String usuario;
    private String contrasena;
    private double presupuesto;

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

    @Override
    public String toString() {
        return "Usuario{" +
                "codigo=" + codigo +
                ", nombre='" + nombre + '\'' +
                ", usuario='" + usuario + '\'' +
                ", presupuesto=" + presupuesto +
                '}';
    }
}
