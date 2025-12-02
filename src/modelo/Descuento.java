package modelo;

import java.time.LocalDate;

/**
 * Representa un cupón o código de descuento
 */
public class Descuento {
    private String codigo;
    private String descripcion;
    private TipoDescuento tipoDescuento;
    private double valor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activo;
    private Integer usosMaximos;
    private int usosActuales;
    private double montoMinimo;

    public enum TipoDescuento {
        PORCENTAJE,
        MONTO_FIJO
    }

    public Descuento() {
    }

    public Descuento(String codigo, String descripcion, TipoDescuento tipo, double valor) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tipoDescuento = tipo;
        this.valor = valor;
        this.activo = true;
        this.usosActuales = 0;
        this.montoMinimo = 0;
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoDescuento getTipoDescuento() { return tipoDescuento; }
    public void setTipoDescuento(TipoDescuento tipoDescuento) { this.tipoDescuento = tipoDescuento; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Integer getUsosMaximos() { return usosMaximos; }
    public void setUsosMaximos(Integer usosMaximos) { this.usosMaximos = usosMaximos; }

    public int getUsosActuales() { return usosActuales; }
    public void setUsosActuales(int usosActuales) { this.usosActuales = usosActuales; }

    public double getMontoMinimo() { return montoMinimo; }
    public void setMontoMinimo(double montoMinimo) { this.montoMinimo = montoMinimo; }

    /**
     * Verifica si el cupón es válido para usar
     */
    public boolean esValido() {
        if (!activo) return false;
        
        LocalDate hoy = LocalDate.now();
        
        if (fechaInicio != null && hoy.isBefore(fechaInicio)) return false;
        if (fechaFin != null && hoy.isAfter(fechaFin)) return false;
        if (usosMaximos != null && usosActuales >= usosMaximos) return false;
        
        return true;
    }

    /**
     * Verifica si el cupón aplica para un monto dado
     */
    public boolean aplicaParaMonto(double monto) {
        return esValido() && monto >= montoMinimo;
    }

    /**
     * Calcula el descuento para un monto dado
     */
    public double calcularDescuento(double monto) {
        if (!aplicaParaMonto(monto)) return 0;
        
        if (tipoDescuento == TipoDescuento.PORCENTAJE) {
            return monto * (valor / 100.0);
        } else {
            return Math.min(valor, monto); // No puede ser mayor al monto
        }
    }

    /**
     * Retorna el descuento formateado para mostrar
     */
    public String getDescuentoFormateado() {
        if (tipoDescuento == TipoDescuento.PORCENTAJE) {
            return String.format("%.0f%%", valor);
        } else {
            return String.format("$%.2f", valor);
        }
    }

    @Override
    public String toString() {
        return "Descuento{" +
                "codigo='" + codigo + '\'' +
                ", tipo=" + tipoDescuento +
                ", valor=" + getDescuentoFormateado() +
                ", activo=" + activo +
                '}';
    }
}
