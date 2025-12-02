package modelo;

/**
 * Clase que representa las estadísticas de venta de un producto.
 * Utilizada para mostrar reportes de productos más vendidos.
 */
public class EstadisticaProducto {
    private int codigoProducto;
    private String descripcion;
    private int cantidadVendida;
    private double montoTotal;
    private int stockActual;

    // Constructor vacío
    public EstadisticaProducto() {
    }

    // Constructor completo
    public EstadisticaProducto(int codigoProducto, String descripcion, 
                                int cantidadVendida, double montoTotal, int stockActual) {
        this.codigoProducto = codigoProducto;
        this.descripcion = descripcion;
        this.cantidadVendida = cantidadVendida;
        this.montoTotal = montoTotal;
        this.stockActual = stockActual;
    }

    // Getters y Setters
    public int getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(int codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    @Override
    public String toString() {
        return "EstadisticaProducto{" +
                "codigoProducto=" + codigoProducto +
                ", descripcion='" + descripcion + '\'' +
                ", cantidadVendida=" + cantidadVendida +
                ", montoTotal=" + montoTotal +
                ", stockActual=" + stockActual +
                '}';
    }
}
