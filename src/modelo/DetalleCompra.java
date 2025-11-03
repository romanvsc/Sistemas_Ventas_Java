package modelo;

public class DetalleCompra {
    private int numeroCompra;
    private int codigoProducto;
    private int cantidad;
    private double precioUnitario; // Para mantener el precio al momento de la compra
    private String descripcionProducto; // Para mostrar en la interfaz

    // Constructor vacío
    public DetalleCompra() {
    }

    // Constructor completo
    public DetalleCompra(int numeroCompra, int codigoProducto, int cantidad, 
                         double precioUnitario, String descripcionProducto) {
        this.numeroCompra = numeroCompra;
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descripcionProducto = descripcionProducto;
    }

    // Constructor sin número de compra (para creación antes de guardar)
    public DetalleCompra(int codigoProducto, int cantidad, double precioUnitario) {
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Getters y Setters
    public int getNumeroCompra() {
        return numeroCompra;
    }

    public void setNumeroCompra(int numeroCompra) {
        this.numeroCompra = numeroCompra;
    }

    public int getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(int codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public double getSubtotal() {
        return cantidad * precioUnitario;
    }

    @Override
    public String toString() {
        return "DetalleCompra{" +
                "numeroCompra=" + numeroCompra +
                ", codigoProducto=" + codigoProducto +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
