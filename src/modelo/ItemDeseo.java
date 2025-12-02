package modelo;

import java.time.LocalDateTime;

/**
 * Representa un item en la lista de deseos del usuario
 */
public class ItemDeseo {
    private int id;
    private int codigoCliente;
    private int codigoProducto;
    private String descripcionProducto;
    private double precioProducto;
    private int stockProducto;
    private LocalDateTime fechaAgregado;
    private boolean notificar;

    public ItemDeseo() {
    }

    public ItemDeseo(int codigoCliente, int codigoProducto) {
        this.codigoCliente = codigoCliente;
        this.codigoProducto = codigoProducto;
        this.fechaAgregado = LocalDateTime.now();
        this.notificar = false;
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

    public int getStockProducto() { return stockProducto; }
    public void setStockProducto(int stockProducto) { this.stockProducto = stockProducto; }

    public LocalDateTime getFechaAgregado() { return fechaAgregado; }
    public void setFechaAgregado(LocalDateTime fechaAgregado) { this.fechaAgregado = fechaAgregado; }

    public boolean isNotificar() { return notificar; }
    public void setNotificar(boolean notificar) { this.notificar = notificar; }

    public boolean estaDisponible() {
        return stockProducto > 0;
    }

    @Override
    public String toString() {
        return "ItemDeseo{" +
                "codigoProducto=" + codigoProducto +
                ", descripcion='" + descripcionProducto + '\'' +
                ", precio=" + precioProducto +
                ", disponible=" + estaDisponible() +
                '}';
    }
}
