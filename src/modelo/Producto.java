package modelo;

public class Producto {
    private int codigo;
    private String descripcion;
    private int cantidad;
    private double precio;
    private String categoria;  // Nuevo campo para búsqueda y filtros

    // Constructor vacío
    public Producto() {
    }

    // Constructor completo
    public Producto(int codigo, String descripcion, int cantidad, double precio) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
    }
    
    // Constructor con categoría
    public Producto(int codigo, String descripcion, int cantidad, double precio, String categoria) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.categoria = categoria;
    }

    // Constructor sin código (para inserción)
    public Producto(String descripcion, int cantidad, double precio) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    // Getters y Setters
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    // Alias para compatibilidad con Stock
    public int getStock() {
        return cantidad;
    }
    
    public void setStock(int stock) {
        this.cantidad = stock;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    /**
     * Verifica si el producto tiene stock disponible
     */
    public boolean tieneStock() {
        return cantidad > 0;
    }
    
    /**
     * Verifica si hay suficiente stock para una cantidad dada
     */
    public boolean tieneStockSuficiente(int cantidadRequerida) {
        return cantidad >= cantidadRequerida;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "codigo=" + codigo +
                ", descripcion='" + descripcion + '\'' +
                ", cantidad=" + cantidad +
                ", precio=" + precio +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}
