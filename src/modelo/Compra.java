package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Compra {
    private int numeroCompra;
    private LocalDate fecha;
    private int codigoCliente;
    private List<DetalleCompra> detalles;

    // Constructor vacío
    public Compra() {
        this.detalles = new ArrayList<>();
    }

    // Constructor completo
    public Compra(int numeroCompra, LocalDate fecha, int codigoCliente) {
        this.numeroCompra = numeroCompra;
        this.fecha = fecha;
        this.codigoCliente = codigoCliente;
        this.detalles = new ArrayList<>();
    }

    // Constructor sin número (para inserción)
    public Compra(LocalDate fecha, int codigoCliente) {
        this.fecha = fecha;
        this.codigoCliente = codigoCliente;
        this.detalles = new ArrayList<>();
    }

    // Getters y Setters
    public int getNumeroCompra() {
        return numeroCompra;
    }

    public void setNumeroCompra(int numeroCompra) {
        this.numeroCompra = numeroCompra;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompra> detalles) {
        this.detalles = detalles;
    }

    public void agregarDetalle(DetalleCompra detalle) {
        this.detalles.add(detalle);
    }

    public double calcularTotal() {
        return detalles.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();
    }

    @Override
    public String toString() {
        return "Compra{" +
                "numeroCompra=" + numeroCompra +
                ", fecha=" + fecha +
                ", codigoCliente=" + codigoCliente +
                ", total=" + calcularTotal() +
                '}';
    }
}
