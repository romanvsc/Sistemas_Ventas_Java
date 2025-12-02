package servicios;

import datos.DescuentoDAO;
import modelo.Descuento;
import java.util.List;

/**
 * Servicio para gestionar cupones y códigos de descuento
 */
public class ServicioDescuento {
    private final DescuentoDAO descuentoDAO;
    
    public ServicioDescuento() {
        this.descuentoDAO = new DescuentoDAO();
    }
    
    /**
     * Valida y obtiene un cupón de descuento
     * @return El descuento si es válido, null si no existe o no es válido
     */
    public Descuento validarCupon(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        
        Descuento descuento = descuentoDAO.buscarPorCodigo(codigo.trim());
        
        if (descuento != null && descuento.esValido()) {
            return descuento;
        }
        
        return null;
    }
    
    /**
     * Aplica un cupón de descuento a un monto
     * @return El monto del descuento, o 0 si no aplica
     */
    public double aplicarDescuento(String codigo, double montoTotal) {
        Descuento descuento = validarCupon(codigo);
        
        if (descuento == null) {
            return 0;
        }
        
        return descuento.calcularDescuento(montoTotal);
    }
    
    /**
     * Calcula el total final después de aplicar descuento
     */
    public double calcularTotalConDescuento(String codigo, double montoSubtotal) {
        double descuento = aplicarDescuento(codigo, montoSubtotal);
        return Math.max(0, montoSubtotal - descuento);
    }
    
    /**
     * Registra el uso de un cupón (después de completar la compra)
     */
    public boolean registrarUso(String codigo) {
        boolean registrado = descuentoDAO.incrementarUsos(codigo);
        return registrado;
    }
    
    /**
     * Obtiene información de un cupón para mostrar al usuario
     */
    public String obtenerDescripcionCupon(String codigo) {
        Descuento descuento = descuentoDAO.buscarPorCodigo(codigo);
        
        if (descuento == null) {
            return "Cupón no encontrado";
        }
        
        if (!descuento.esValido()) {
            if (!descuento.isActivo()) {
                return "Este cupón ya no está activo";
            }
            if (descuento.getUsosMaximos() != null && 
                descuento.getUsosActuales() >= descuento.getUsosMaximos()) {
                return "Este cupón ha alcanzado su límite de usos";
            }
            return "Este cupón ha expirado";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(descuento.getDescripcion());
        sb.append(" - ");
        sb.append(descuento.getDescuentoFormateado());
        sb.append(" de descuento");
        
        if (descuento.getMontoMinimo() > 0) {
            sb.append(String.format(" (mín. $%.2f)", descuento.getMontoMinimo()));
        }
        
        return sb.toString();
    }
    
    /**
     * Lista todos los cupones (para administradores)
     */
    public List<Descuento> listarTodos() {
        return descuentoDAO.listarTodos();
    }
    
    /**
     * Lista solo cupones activos
     */
    public List<Descuento> listarActivos() {
        return descuentoDAO.listarActivos();
    }
    
    /**
     * Crea un nuevo cupón (para administradores)
     */
    public boolean crearCupon(Descuento descuento) {
        boolean creado = false;
        if (descuento.getCodigo() != null && !descuento.getCodigo().trim().isEmpty()) {
            if (descuentoDAO.buscarPorCodigo(descuento.getCodigo()) == null) {
                creado = descuentoDAO.crear(descuento);
            }
        }
        return creado;
    }
    
    /**
     * Actualiza un cupón existente (para administradores)
     */
    public boolean actualizarCupon(Descuento descuento) {
        boolean actualizado = descuentoDAO.actualizar(descuento);
        return actualizado;
    }
    
    /**
     * Desactiva un cupón (para administradores)
     */
    public boolean desactivarCupon(String codigo) {
        boolean desactivado = descuentoDAO.desactivar(codigo);
        return desactivado;
    }
    
    /**
     * Elimina un cupón (para administradores)
     */
    public boolean eliminarCupon(String codigo) {
        boolean eliminado = descuentoDAO.eliminar(codigo);
        return eliminado;
    }
}
