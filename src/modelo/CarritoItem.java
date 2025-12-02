package modelo;

import java.time.LocalDateTime;

/**
 * Representa un item en el carrito guardado (persistente entre sesiones)
 */
public class CarritoItem {
    private int id;
    private int codigoCliente;
    private int codigoProducto;
    private String descripcionProducto;
    private double precioProducto;
    private int cantidad;
    private LocalDateTime fechaAgregado;

    public CarritoItem() {
    }

    public CarritoItem(int codigoCliente, int codigoProducto, int cantidad) {
        this.codigoCliente = codigoCliente;
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.fechaAgregado = LocalDateTime.now();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(int codigoCliente) { this.codigoCliente = codigoCliente; }

    public int getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(int codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getDescripcionProducto() { return descripcionProducto; }
    public void setDescripcionProducto(String descripcionProducto) { this.descripcionProducto = descripcionProducto; }

    public double getPrecioProducto() { return precioProducto; }
    public void setPrecioProducto(double precioProducto) { this.precioProducto = precioProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public LocalDateTime getFechaAgregado() { return fechaAgregado; }
    public void setFechaAgregado(LocalDateTime fechaAgregado) { this.fechaAgregado = fechaAgregado; }

    public double getSubtotal() {
        return precioProducto * cantidad;
    }

    @Override
    public String toString() {
        return "CarritoItem{" +
                "codigoProducto=" + codigoProducto +
                ", descripcion='" + descripcionProducto + '\'' +
                ", cantidad=" + cantidad +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
