package datos;

import modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private ConexionMySQL conexionMySQL;

    public ProductoDAO() {
        this.conexionMySQL = ConexionMySQL.obtenerInstancia();
    }

    // CREATE - Insertar un nuevo producto
    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO Producto (Descripcion, Cantidad, Precio) VALUES (?, ?, ?)";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, producto.getDescripcion());
            stmt.setInt(2, producto.getCantidad());
            stmt.setDouble(3, producto.getPrecio());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        producto.setCodigo(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    // READ - Obtener producto por código
    public Producto obtenerPorCodigo(int codigo) {
        String sql = "SELECT * FROM Producto WHERE Codigo = ?";
        Producto producto = null;
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                producto = new Producto();
                producto.setCodigo(rs.getInt("Codigo"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setCantidad(rs.getInt("Cantidad"));
                producto.setPrecio(rs.getDouble("Precio"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener producto: " + e.getMessage());
        }
        
        return producto;
    }

    // READ - Listar todos los productos
    public List<Producto> listarTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto ORDER BY Descripcion";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigo(rs.getInt("Codigo"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setCantidad(rs.getInt("Cantidad"));
                producto.setPrecio(rs.getDouble("Precio"));
                productos.add(producto);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        
        return productos;
    }

    // READ - Listar productos disponibles (con stock)
    public List<Producto> listarDisponibles() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Cantidad > 0 ORDER BY Descripcion";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigo(rs.getInt("Codigo"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setCantidad(rs.getInt("Cantidad"));
                producto.setPrecio(rs.getDouble("Precio"));
                productos.add(producto);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar productos disponibles: " + e.getMessage());
        }
        
        return productos;
    }

    // UPDATE - Actualizar producto
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Producto SET Descripcion = ?, Cantidad = ?, Precio = ? WHERE Codigo = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getDescripcion());
            stmt.setInt(2, producto.getCantidad());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getCodigo());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    // DELETE - Eliminar producto
    public boolean eliminar(int codigo) {
        String sql = "DELETE FROM Producto WHERE Codigo = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigo);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    // Actualizar stock del producto (restar cantidad)
    public boolean actualizarStock(int codigo, int cantidadVendida) {
        String sql = "UPDATE Producto SET Cantidad = Cantidad - ? WHERE Codigo = ? AND Cantidad >= ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cantidadVendida);
            stmt.setInt(2, codigo);
            stmt.setInt(3, cantidadVendida);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    // Verificar si hay stock suficiente
    public boolean hayStockSuficiente(int codigo, int cantidadRequerida) {
        String sql = "SELECT Cantidad FROM Producto WHERE Codigo = ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codigo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("Cantidad") >= cantidadRequerida;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar stock: " + e.getMessage());
        }
        
        return false;
    }

    // Listar productos con stock bajo (menor al umbral especificado)
    public List<Producto> listarConStockBajo(int umbral) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Cantidad <= ? ORDER BY Cantidad ASC";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, umbral);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setCodigo(rs.getInt("Codigo"));
                    producto.setDescripcion(rs.getString("Descripcion"));
                    producto.setCantidad(rs.getInt("Cantidad"));
                    producto.setPrecio(rs.getDouble("Precio"));
                    productos.add(producto);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar productos con stock bajo: " + e.getMessage());
        }
        
        return productos;
    }

    // Contar productos con stock bajo
    public int contarProductosStockBajo(int umbral) {
        String sql = "SELECT COUNT(*) as Total FROM Producto WHERE Cantidad <= ?";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, umbral);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Total");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar productos con stock bajo: " + e.getMessage());
        }
        
        return 0;
    }

    // ==================== MÉTODOS DE BÚSQUEDA Y FILTROS ====================
    
    /**
     * Busca productos por descripción (coincidencia parcial)
     */
    public List<Producto> buscarPorDescripcion(String termino) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE LOWER(Descripcion) LIKE LOWER(?) ORDER BY Descripcion";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + termino + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        }
        
        return productos;
    }
    
    /**
     * Filtra productos por categoría
     */
    public List<Producto> listarPorCategoria(String categoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Categoria = ? ORDER BY Descripcion";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar por categoría: " + e.getMessage());
        }
        
        return productos;
    }
    
    /**
     * Filtra productos por rango de precio
     */
    public List<Producto> listarPorRangoPrecio(double precioMin, double precioMax) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Precio BETWEEN ? AND ? ORDER BY Precio";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, precioMin);
            stmt.setDouble(2, precioMax);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar por rango de precio: " + e.getMessage());
        }
        
        return productos;
    }
    
    /**
     * Búsqueda avanzada con múltiples filtros
     */
    public List<Producto> buscarAvanzado(String termino, String categoria, 
                                          Double precioMin, Double precioMax, 
                                          Boolean soloConStock) {
        List<Producto> productos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Producto WHERE 1=1");
        List<Object> parametros = new ArrayList<>();
        
        if (termino != null && !termino.trim().isEmpty()) {
            sql.append(" AND LOWER(Descripcion) LIKE LOWER(?)");
            parametros.add("%" + termino.trim() + "%");
        }
        
        if (categoria != null && !categoria.trim().isEmpty() && !categoria.equals("Todas")) {
            sql.append(" AND Categoria = ?");
            parametros.add(categoria);
        }
        
        if (precioMin != null) {
            sql.append(" AND Precio >= ?");
            parametros.add(precioMin);
        }
        
        if (precioMax != null) {
            sql.append(" AND Precio <= ?");
            parametros.add(precioMax);
        }
        
        if (soloConStock != null && soloConStock) {
            sql.append(" AND Cantidad > 0");
        }
        
        sql.append(" ORDER BY Descripcion");
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parametros.size(); i++) {
                Object param = parametros.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                }
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error en búsqueda avanzada: " + e.getMessage());
        }
        
        return productos;
    }
    
    /**
     * Obtiene todas las categorías existentes
     */
    public List<String> listarCategorias() {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT DISTINCT Categoria FROM Producto WHERE Categoria IS NOT NULL ORDER BY Categoria";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String cat = rs.getString("Categoria");
                if (cat != null && !cat.trim().isEmpty()) {
                    categorias.add(cat);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar categorías: " + e.getMessage());
        }
        
        return categorias;
    }
    
    /**
     * Obtiene el rango de precios de los productos
     */
    public double[] obtenerRangoPrecios() {
        String sql = "SELECT MIN(Precio) as PrecioMin, MAX(Precio) as PrecioMax FROM Producto";
        
        try (Connection conn = conexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return new double[] { rs.getDouble("PrecioMin"), rs.getDouble("PrecioMax") };
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener rango de precios: " + e.getMessage());
        }
        
        return new double[] { 0, 0 };
    }
    
    /**
     * Mapea un ResultSet a un objeto Producto
     */
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setCodigo(rs.getInt("Codigo"));
        producto.setDescripcion(rs.getString("Descripcion"));
        producto.setCantidad(rs.getInt("Cantidad"));
        producto.setPrecio(rs.getDouble("Precio"));
        
        // Intentar obtener categoría (puede no existir en BD antigua)
        try {
            producto.setCategoria(rs.getString("Categoria"));
        } catch (SQLException e) {
            // Columna no existe, ignorar
        }
        
        return producto;
    }
}
