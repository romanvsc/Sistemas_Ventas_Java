package presentacion;

import modelo.Usuario;
import modelo.Producto;
import modelo.Compra;
import modelo.DetalleCompra;
import modelo.EstadisticaProducto;
import modelo.ItemDeseo;
import modelo.CarritoItem;
import modelo.Descuento;
import servicios.ServicioProducto;
import servicios.ServicioCompra;
import servicios.ServicioAdmin;
import servicios.ServicioListaDeseos;
import servicios.ServicioCarrito;
import servicios.ServicioDescuento;
import datos.UsuarioDAO;
import datos.ProductoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrincipalPestaña extends JFrame {
    // Paleta de colores - Tema Electrónica Moderna
    private static final Color TECH_DARK = new Color(15, 23, 42);        // Fondo oscuro principal
    private static final Color TECH_BLUE = new Color(59, 130, 246);      // Azul tecnológico
    private static final Color TECH_BLUE_LIGHT = new Color(96, 165, 250); // Azul claro
    private static final Color TECH_CYAN = new Color(34, 211, 238);      // Cyan brillante
    private static final Color TECH_GREEN = new Color(34, 197, 94);      // Verde éxito
    private static final Color TECH_ORANGE = new Color(251, 146, 60);    // Naranja advertencia
    private static final Color TECH_RED = new Color(239, 68, 68);        // Rojo error
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_TEXTO = new Color(15, 23, 42);
    private static final Color COLOR_FONDO = new Color(248, 250, 252);
    private static final Color COLOR_CARD = Color.WHITE;
    
    private Usuario usuarioActual;
    private ServicioProducto servicioProducto;
    private ServicioCompra servicioCompra;
    private ServicioAdmin servicioAdmin;
    private ServicioListaDeseos servicioListaDeseos;
    private ServicioCarrito servicioCarritoPersistente;
    private ServicioDescuento servicioDescuento;
    
    // Componentes principales
    private JTabbedPane tabbedPane;
    private Compra carritoActual;

    // Pestaña de Productos
    private JTable tablaProductos;
    private DefaultTableModel modeloTablaProductos;
    private JTextField txtBusqueda;
    private JComboBox<String> cmbCategoria;
    private JTextField txtPrecioMin;
    private JTextField txtPrecioMax;
    private JCheckBox chkSoloConStock;
    
    // Pestaña de Carrito
    private JTable tablaCarrito;
    private DefaultTableModel modeloTablaCarrito;
    private JLabel lblTotal;
    private JLabel lblDescuento;
    private JLabel lblTotalFinal;
    private JTextField txtCodigoDescuento;
    private Descuento descuentoAplicado;
    
    // Pestaña de Lista de Deseos
    private JTable tablaDeseos;
    private DefaultTableModel modeloTablaDeseos;
    
    // Pestaña de Mis Compras
    private JTable tablaMisCompras;
    private DefaultTableModel modeloTablaMisCompras;
    
    // Pestaña de Administración (solo para admin)
    private JTable tablaAdminProductos;
    private DefaultTableModel modeloTablaAdminProductos;
    
    // Pestaña de Reportes (solo para admin)
    private JTable tablaProductosMasVendidos;
    private DefaultTableModel modeloTablaProductosMasVendidos;
    private JTable tablaStockBajo;
    private DefaultTableModel modeloTablaStockBajo;
    private JLabel lblTotalVentas;
    private JLabel lblCantidadVendida;
    private JLabel lblProductosStockBajo;

    public PrincipalPestaña(Usuario usuario) {
        this.usuarioActual = usuario;
        this.servicioProducto = new ServicioProducto();
        this.servicioCompra = new ServicioCompra();
        this.servicioAdmin = new ServicioAdmin();
        this.servicioListaDeseos = new ServicioListaDeseos();
        this.servicioCarritoPersistente = new ServicioCarrito();
        this.servicioDescuento = new ServicioDescuento();
        this.carritoActual = servicioCompra.crearNuevaCompra(usuario.getCodigo());
        
        inicializarComponentes();
        configurarVentana();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Panel principal con fondo
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);
        
        // Header
        JPanel header = crearHeader();
        panelPrincipal.add(header, BorderLayout.NORTH);
        
        // TabbedPane estilizado
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(COLOR_BLANCO);
        tabbedPane.setForeground(COLOR_TEXTO);

        // Verificar si es invitado
        boolean esInvitado = usuarioActual.getUsuario().equalsIgnoreCase("invitado");
        
        // Agregar pestañas
        tabbedPane.addTab("Productos", crearPanelProductos());
        
        if (!esInvitado) {
            tabbedPane.addTab("Carrito", crearPanelCarrito());
            tabbedPane.addTab("Lista de Deseos", crearPanelListaDeseos());
            tabbedPane.addTab("Mis Compras", crearPanelMisCompras());
            tabbedPane.addTab("Mi Perfil", crearPanelMiPerfil());
        }
        
        // Solo mostrar administración si es usuario "admin"
        if (usuarioActual.getUsuario().equalsIgnoreCase("admin")) {
            tabbedPane.addTab("Administración", crearPanelAdministracion());
            tabbedPane.addTab("Reportes", crearPanelReportes());
        }

        panelPrincipal.add(tabbedPane, BorderLayout.CENTER);
        add(panelPrincipal);
        
        // Mostrar mensajes de bienvenida después de iniciar sesión
        SwingUtilities.invokeLater(() -> mostrarMensajesBienvenida());
    }
    
    /**
     * Muestra mensajes de bienvenida personalizados según el tipo de usuario
     */
    private void mostrarMensajesBienvenida() {
        boolean esInvitado = usuarioActual.getUsuario().equalsIgnoreCase("invitado");
        boolean esAdmin = usuarioActual.getUsuario().equalsIgnoreCase("admin");
        
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("¡Bienvenido(a), ").append(usuarioActual.getNombre()).append("!\n\n");
        
        boolean hayNotificaciones = false;
        
        // Para usuarios normales (no invitados): verificar lista de deseos
        if (!esInvitado) {
            List<ItemDeseo> listaDeseos = servicioListaDeseos.obtenerListaDeseos(usuarioActual.getCodigo());
            
            if (!listaDeseos.isEmpty()) {
                // Verificar productos de la lista de deseos que ahora tienen stock
                List<ItemDeseo> conStockDisponible = new java.util.ArrayList<>();
                for (ItemDeseo item : listaDeseos) {
                    if (item.getStockProducto() > 0) {
                        conStockDisponible.add(item);
                    }
                }
                
                if (!conStockDisponible.isEmpty()) {
                    hayNotificaciones = true;
                    mensaje.append("¡Buenas noticias! Productos de tu Lista de Deseos con stock disponible:\n\n");
                    
                    int mostrar = Math.min(conStockDisponible.size(), 5);
                    for (int i = 0; i < mostrar; i++) {
                        ItemDeseo item = conStockDisponible.get(i);
                        mensaje.append("  - ").append(item.getDescripcionProducto())
                               .append(" - $").append(String.format("%.2f", item.getPrecioProducto()))
                               .append(" (").append(item.getStockProducto()).append(" disponibles)\n");
                    }
                    
                    if (conStockDisponible.size() > 5) {
                        mensaje.append("  ... y ").append(conStockDisponible.size() - 5).append(" producto(s) más.\n");
                    }
                    mensaje.append("\n");
                }
            }
            
            // Verificar carrito guardado
            List<CarritoItem> carritoGuardado = servicioCarritoPersistente.obtenerCarrito(usuarioActual.getCodigo());
            if (!carritoGuardado.isEmpty()) {
                hayNotificaciones = true;
                double totalCarrito = servicioCarritoPersistente.calcularSubtotal(usuarioActual.getCodigo());
                mensaje.append("Tienes ").append(carritoGuardado.size())
                       .append(" producto(s) guardado(s) en tu carrito (Total: $")
                       .append(String.format("%.2f", totalCarrito)).append(")\n\n");
            }
        }
        
        // Para admin: verificar stock bajo
        if (esAdmin) {
            int cantidadStockBajo = servicioAdmin.contarProductosStockBajo();
            
            if (cantidadStockBajo > 0) {
                hayNotificaciones = true;
                List<Producto> productosStockBajo = servicioAdmin.obtenerProductosConStockBajo();
                
                mensaje.append("ALERTA DE INVENTARIO - ").append(cantidadStockBajo).append(" producto(s) con stock bajo:\n\n");
                
                int mostrar = Math.min(productosStockBajo.size(), 5);
                for (int i = 0; i < mostrar; i++) {
                    Producto p = productosStockBajo.get(i);
                    String estado = p.getCantidad() == 0 ? "SIN STOCK" : p.getCantidad() + " unid.";
                    mensaje.append("  • ").append(p.getDescripcion()).append(" - ").append(estado).append("\n");
                }
                
                if (productosStockBajo.size() > 5) {
                    mensaje.append("  ... y ").append(productosStockBajo.size() - 5).append(" más.\n");
                }
                mensaje.append("\n");
            }
        }
        
        // Mostrar mensaje según el contenido
        if (hayNotificaciones) {
            if (esAdmin) {
                mensaje.append("¿Desea ir a la pestaña de Reportes para gestionar el inventario?");
                
                int respuesta = JOptionPane.showConfirmDialog(this,
                    mensaje.toString(),
                    "Bienvenido - Notificaciones",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
                
                if (respuesta == JOptionPane.YES_OPTION) {
                    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                        if (tabbedPane.getTitleAt(i).equals("Reportes")) {
                            tabbedPane.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } else if (!esInvitado) {
                mensaje.append("¿Desea ir a la Lista de Deseos?");
                
                int respuesta = JOptionPane.showConfirmDialog(this,
                    mensaje.toString(),
                    "Bienvenido - Notificaciones",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
                
                if (respuesta == JOptionPane.YES_OPTION) {
                    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                        if (tabbedPane.getTitleAt(i).equals("Lista de Deseos")) {
                            tabbedPane.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        } else {
            // Solo mostrar bienvenida simple si no hay notificaciones
            JOptionPane.showMessageDialog(this,
                mensaje.append("¡Gracias por visitarnos! Explora nuestros productos.").toString(),
                "Bienvenido",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(TECH_DARK);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Panel izquierdo con icono y título
        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelIzquierdo.setBackground(TECH_DARK);
        
        JLabel lblIcono = new JLabel("");
        lblIcono.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblIcono.setForeground(TECH_CYAN);
        panelIzquierdo.add(lblIcono);
        
        JLabel lblTitulo = new JLabel("TechStore - Sistema de Ventas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_BLANCO);
        panelIzquierdo.add(lblTitulo);
        
        header.add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel derecho con usuario y presupuesto
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(TECH_DARK);
        
        JLabel lblUsuario = new JLabel(" " + usuarioActual.getNombre());
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setForeground(TECH_BLUE_LIGHT);
        lblUsuario.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panelDerecho.add(lblUsuario);
        
        // Mostrar presupuesto solo si no es invitado
        if (!usuarioActual.getUsuario().equalsIgnoreCase("invitado")) {
            JLabel lblPresupuesto = new JLabel(String.format(" $%.2f", usuarioActual.getPresupuesto()));
            lblPresupuesto.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblPresupuesto.setForeground(TECH_GREEN);
            lblPresupuesto.setAlignmentX(Component.RIGHT_ALIGNMENT);
            panelDerecho.add(lblPresupuesto);
        }
        
        header.add(panelDerecho, BorderLayout.EAST);
        
        return header;
    }

    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de contenido con fondo blanco
        JPanel panelContenido = new JPanel(new BorderLayout(10, 10));
        panelContenido.setBackground(COLOR_CARD);
        panelContenido.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Panel superior con título y búsqueda
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setBackground(COLOR_CARD);
        
        // Título con icono
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTitulo.setBackground(COLOR_CARD);
        
        JLabel lblIcono = new JLabel("");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        panelTitulo.add(lblIcono);
        
        JLabel lblTitulo = new JLabel("Productos Electrónicos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(TECH_DARK);
        panelTitulo.add(lblTitulo);
        
        panelSuperior.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel de búsqueda y filtros
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelBusqueda.setBackground(new Color(248, 250, 252));
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(226, 232, 240), 1, 8),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        // Campo de búsqueda
        JLabel lblBuscar = new JLabel("Buscar:");
        panelBusqueda.add(lblBuscar);
        
        txtBusqueda = new JTextField(15);
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(200, 200, 200), 1, 6),
            new EmptyBorder(5, 10, 5, 10)
        ));
        txtBusqueda.setToolTipText("Buscar por nombre");
        panelBusqueda.add(txtBusqueda);
        
        // Categoría
        panelBusqueda.add(new JLabel("Categoría:"));
        cmbCategoria = new JComboBox<>();
        cmbCategoria.addItem("Todas");
        cmbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbCategoria.setPreferredSize(new Dimension(120, 28));
        panelBusqueda.add(cmbCategoria);
        
        // Rango de precio
        panelBusqueda.add(new JLabel("Precio:"));
        txtPrecioMin = new JTextField(6);
        txtPrecioMin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPrecioMin.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(200, 200, 200), 1, 6),
            new EmptyBorder(3, 8, 3, 8)
        ));
        txtPrecioMin.setToolTipText("Precio mínimo");
        panelBusqueda.add(txtPrecioMin);
        
        panelBusqueda.add(new JLabel("-"));
        
        txtPrecioMax = new JTextField(6);
        txtPrecioMax.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPrecioMax.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(200, 200, 200), 1, 6),
            new EmptyBorder(3, 8, 3, 8)
        ));
        txtPrecioMax.setToolTipText("Precio máximo");
        panelBusqueda.add(txtPrecioMax);
        
        // Solo con stock
        chkSoloConStock = new JCheckBox("Solo con stock");
        chkSoloConStock.setSelected(true);
        chkSoloConStock.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkSoloConStock.setBackground(new Color(248, 250, 252));
        panelBusqueda.add(chkSoloConStock);
        
        // Botón buscar
        JButton btnBuscar = crearBotonPrimario("Buscar");
        btnBuscar.setPreferredSize(new Dimension(90, 30));
        btnBuscar.addActionListener(e -> buscarProductos());
        panelBusqueda.add(btnBuscar);
        
        // Botón limpiar
        JButton btnLimpiar = crearBotonSecundario("Limpiar");
        btnLimpiar.setPreferredSize(new Dimension(80, 30));
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panelBusqueda.add(btnLimpiar);
        
        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);
        panelContenido.add(panelSuperior, BorderLayout.NORTH);

        // Tabla de productos
        String[] columnas = {"Código", "Producto", "Categoría", "Stock", "Precio", "Deseo"};
        modeloTablaProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloTablaProductos);
        estilizarTabla(tablaProductos);
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(250);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(60);
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(30);
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(COLOR_BLANCO);
        panelContenido.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(COLOR_CARD);
        
        JButton btnActualizar = crearBotonSecundario(" Actualizar");
        btnActualizar.addActionListener(e -> cargarProductos());
        panelBotones.add(btnActualizar);
        
        // Solo mostrar botón de deseos si no es invitado
        if (!usuarioActual.getUsuario().equalsIgnoreCase("invitado")) {
            JButton btnDeseos = crearBotonSecundario("Lista de Deseos");
            btnDeseos.addActionListener(e -> agregarAListaDeseos());
            panelBotones.add(btnDeseos);
        }

        JButton btnAgregar = crearBotonPrimario(" Agregar al Carrito");
        btnAgregar.addActionListener(e -> agregarProductoAlCarrito());
        panelBotones.add(btnAgregar);

        panelContenido.add(panelBotones, BorderLayout.SOUTH);
        panel.add(panelContenido, BorderLayout.CENTER);

        // Cargar categorías
        cargarCategorias();
        
        // Listener para búsqueda al presionar Enter
        txtBusqueda.addActionListener(e -> buscarProductos());

        return panel;
    }
    
    private void cargarCategorias() {
        ProductoDAO productoDAO = new ProductoDAO();
        List<String> categorias = productoDAO.listarCategorias();
        
        cmbCategoria.removeAllItems();
        cmbCategoria.addItem("Todas");
        for (String cat : categorias) {
            cmbCategoria.addItem(cat);
        }
    }
    
    private void buscarProductos() {
        String termino = txtBusqueda.getText().trim();
        String categoria = (String) cmbCategoria.getSelectedItem();
        
        Double precioMin = null;
        Double precioMax = null;
        
        try {
            if (!txtPrecioMin.getText().trim().isEmpty()) {
                precioMin = Double.parseDouble(txtPrecioMin.getText().trim());
            }
            if (!txtPrecioMax.getText().trim().isEmpty()) {
                precioMax = Double.parseDouble(txtPrecioMax.getText().trim());
            }
        } catch (NumberFormatException e) {
            mostrarMensaje(this, "Los valores de precio deben ser numéricos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Boolean soloConStock = chkSoloConStock.isSelected();
        
        ProductoDAO productoDAO = new ProductoDAO();
        List<Producto> productos = productoDAO.buscarAvanzado(termino, categoria, precioMin, precioMax, soloConStock);
        
        modeloTablaProductos.setRowCount(0);
        for (Producto p : productos) {
            boolean enDeseos = !usuarioActual.getUsuario().equalsIgnoreCase("invitado") && 
                              servicioListaDeseos.estaEnListaDeseos(usuarioActual.getCodigo(), p.getCodigo());
            
            modeloTablaProductos.addRow(new Object[]{
                p.getCodigo(),
                p.getDescripcion(),
                p.getCategoria() != null ? p.getCategoria() : "-",
                p.getCantidad(),
                String.format("$%.2f", p.getPrecio()),
                enDeseos ? "Si" : "No"
            });
        }
    }
    
    private void limpiarFiltros() {
        txtBusqueda.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtPrecioMin.setText("");
        txtPrecioMax.setText("");
        chkSoloConStock.setSelected(true);
        cargarProductos();
    }
    
    private void agregarAListaDeseos() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            mostrarMensaje(this, "Seleccione un producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int codigoProducto = (int) modeloTablaProductos.getValueAt(filaSeleccionada, 0);
        
        boolean agregado = servicioListaDeseos.alternar(usuarioActual.getCodigo(), codigoProducto);
        
        if (agregado) {
            modeloTablaProductos.setValueAt("Si", filaSeleccionada, 5);
            mostrarMensaje(this, "Producto agregado a la lista de deseos", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            modeloTablaProductos.setValueAt("No", filaSeleccionada, 5);
            mostrarMensaje(this, "Producto eliminado de la lista de deseos", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Actualizar pestaña de deseos si existe
        if (modeloTablaDeseos != null) {
            cargarListaDeseos();
        }
    }

    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de contenido con fondo blanco
        JPanel panelContenido = new JPanel(new BorderLayout(10, 10));
        panelContenido.setBackground(COLOR_CARD);
        panelContenido.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Título con icono
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTitulo.setBackground(COLOR_CARD);
        
        JLabel lblIcono = new JLabel("");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        panelTitulo.add(lblIcono);
        
        JLabel lblTitulo = new JLabel("Mi Carrito de Compras");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(TECH_DARK);
        panelTitulo.add(lblTitulo);
        
        panelContenido.add(panelTitulo, BorderLayout.NORTH);

        // Tabla de carrito
        String[] columnas = {"Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        modeloTablaCarrito = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCarrito = new JTable(modeloTablaCarrito);
        estilizarTabla(tablaCarrito);
        JScrollPane scrollPane = new JScrollPane(tablaCarrito);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(COLOR_BLANCO);
        panelContenido.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout(0, 15));
        panelInferior.setBackground(COLOR_CARD);
        
        // Panel de cupón de descuento
        JPanel panelDescuento = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelDescuento.setBackground(new Color(248, 250, 252));
        panelDescuento.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(226, 232, 240), 1, 8),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        JLabel lblCupon = new JLabel("Codigo de descuento:");
        lblCupon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelDescuento.add(lblCupon);
        
        txtCodigoDescuento = new JTextField(12);
        txtCodigoDescuento.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCodigoDescuento.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(200, 200, 200), 1, 6),
            new EmptyBorder(5, 10, 5, 10)
        ));
        panelDescuento.add(txtCodigoDescuento);
        
        JButton btnAplicarCupon = crearBotonSecundario("Aplicar");
        btnAplicarCupon.setPreferredSize(new Dimension(80, 30));
        btnAplicarCupon.addActionListener(e -> aplicarCuponDescuento());
        panelDescuento.add(btnAplicarCupon);
        
        JButton btnQuitarCupon = crearBotonSecundario("Quitar");
        btnQuitarCupon.setPreferredSize(new Dimension(70, 30));
        btnQuitarCupon.addActionListener(e -> quitarCuponDescuento());
        panelDescuento.add(btnQuitarCupon);
        
        panelInferior.add(panelDescuento, BorderLayout.NORTH);
        
        // Panel de totales
        JPanel panelTotales = new JPanel();
        panelTotales.setLayout(new BoxLayout(panelTotales, BoxLayout.Y_AXIS));
        panelTotales.setBackground(COLOR_CARD);
        panelTotales.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JPanel panelSubtotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSubtotal.setBackground(COLOR_CARD);
        lblTotal = new JLabel("Subtotal: $0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTotal.setForeground(COLOR_TEXTO);
        panelSubtotal.add(lblTotal);
        panelTotales.add(panelSubtotal);
        
        JPanel panelDescuentoAplicado = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelDescuentoAplicado.setBackground(COLOR_CARD);
        lblDescuento = new JLabel("");
        lblDescuento.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDescuento.setForeground(TECH_RED);
        panelDescuentoAplicado.add(lblDescuento);
        panelTotales.add(panelDescuentoAplicado);
        
        JPanel panelTotalFinal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotalFinal.setBackground(COLOR_CARD);
        lblTotalFinal = new JLabel("Total: $0.00");
        lblTotalFinal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotalFinal.setForeground(TECH_GREEN);
        panelTotalFinal.add(lblTotalFinal);
        panelTotales.add(panelTotalFinal);
        
        panelInferior.add(panelTotales, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(COLOR_CARD);
        
        JButton btnEliminar = crearBotonSecundario(" Eliminar");
        btnEliminar.addActionListener(e -> eliminarDelCarrito());
        panelBotones.add(btnEliminar);

        JButton btnVaciar = crearBotonAdvertencia(" Vaciar Carrito");
        btnVaciar.addActionListener(e -> vaciarCarrito());
        panelBotones.add(btnVaciar);

        JButton btnComprar = crearBotonExito(" Finalizar Compra");
        btnComprar.addActionListener(e -> finalizarCompra());
        panelBotones.add(btnComprar);

        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        panelContenido.add(panelInferior, BorderLayout.SOUTH);
        panel.add(panelContenido, BorderLayout.CENTER);

        return panel;
    }
    
    private void aplicarCuponDescuento() {
        String codigo = txtCodigoDescuento.getText().trim();
        
        if (codigo.isEmpty()) {
            mostrarMensaje(this, "Ingrese un código de descuento", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (servicioCompra.estaVacia(carritoActual)) {
            mostrarMensaje(this, "El carrito está vacío", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Descuento descuento = servicioDescuento.validarCupon(codigo);
        
        if (descuento == null) {
            mostrarMensaje(this, servicioDescuento.obtenerDescripcionCupon(codigo), "Cupón inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double subtotal = carritoActual.calcularTotal();
        
        if (!descuento.aplicaParaMonto(subtotal)) {
            mostrarMensaje(this, 
                String.format("Este cupón requiere un monto mínimo de $%.2f", descuento.getMontoMinimo()),
                "Cupón no aplicable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        descuentoAplicado = descuento;
        actualizarTotalesCarrito();
        mostrarMensaje(this, "¡Cupón aplicado! " + descuento.getDescuentoFormateado() + " de descuento", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void quitarCuponDescuento() {
        descuentoAplicado = null;
        txtCodigoDescuento.setText("");
        actualizarTotalesCarrito();
    }
    
    private void actualizarTotalesCarrito() {
        double subtotal = carritoActual.calcularTotal();
        lblTotal.setText(String.format("Subtotal: $%.2f", subtotal));
        
        if (descuentoAplicado != null && subtotal > 0) {
            double montoDescuento = descuentoAplicado.calcularDescuento(subtotal);
            double total = subtotal - montoDescuento;
            
            lblDescuento.setText(String.format("Descuento (%s): -$%.2f", 
                descuentoAplicado.getDescuentoFormateado(), montoDescuento));
            lblTotalFinal.setText(String.format("Total: $%.2f", total));
        } else {
            lblDescuento.setText("");
            lblTotalFinal.setText(String.format("Total: $%.2f", subtotal));
        }
    }
    
    private JPanel crearPanelListaDeseos() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de contenido con fondo blanco
        JPanel panelContenido = new JPanel(new BorderLayout(10, 10));
        panelContenido.setBackground(COLOR_CARD);
        panelContenido.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Título con icono
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTitulo.setBackground(COLOR_CARD);
        
        JLabel lblIcono = new JLabel("");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        panelTitulo.add(lblIcono);
        
        JLabel lblTitulo = new JLabel("Mi Lista de Deseos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(TECH_DARK);
        panelTitulo.add(lblTitulo);
        
        panelContenido.add(panelTitulo, BorderLayout.NORTH);

        // Tabla de deseos
        String[] columnas = {"Código", "Producto", "Precio", "Stock", "Agregado"};
        modeloTablaDeseos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDeseos = new JTable(modeloTablaDeseos);
        estilizarTabla(tablaDeseos);
        tablaDeseos.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaDeseos.getColumnModel().getColumn(1).setPreferredWidth(280);
        tablaDeseos.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaDeseos.getColumnModel().getColumn(3).setPreferredWidth(60);
        tablaDeseos.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(tablaDeseos);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(COLOR_BLANCO);
        panelContenido.add(scrollPane, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(COLOR_CARD);
        
        JButton btnActualizar = crearBotonSecundario("Actualizar");
        btnActualizar.addActionListener(e -> cargarListaDeseos());
        panelBotones.add(btnActualizar);
        
        JButton btnEliminar = crearBotonSecundario("Eliminar");
        btnEliminar.addActionListener(e -> eliminarDeListaDeseos());
        panelBotones.add(btnEliminar);
        
        JButton btnMoverCarrito = crearBotonExito("Mover al Carrito");
        btnMoverCarrito.addActionListener(e -> moverDeDeseoACarrito());
        panelBotones.add(btnMoverCarrito);

        panelContenido.add(panelBotones, BorderLayout.SOUTH);
        panel.add(panelContenido, BorderLayout.CENTER);

        return panel;
    }
    
    private void cargarListaDeseos() {
        if (modeloTablaDeseos == null) return;
        
        modeloTablaDeseos.setRowCount(0);
        List<ItemDeseo> deseos = servicioListaDeseos.obtenerListaDeseos(usuarioActual.getCodigo());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (ItemDeseo item : deseos) {
            modeloTablaDeseos.addRow(new Object[]{
                item.getCodigoProducto(),
                item.getDescripcionProducto(),
                String.format("$%.2f", item.getPrecioProducto()),
                item.getStockProducto() > 0 ? item.getStockProducto() : "Sin stock",
                item.getFechaAgregado().format(formatter)
            });
        }
    }
    
    private void eliminarDeListaDeseos() {
        int filaSeleccionada = tablaDeseos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            mostrarMensaje(this, "Seleccione un producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int codigoProducto = (int) modeloTablaDeseos.getValueAt(filaSeleccionada, 0);
        
        if (servicioListaDeseos.eliminar(usuarioActual.getCodigo(), codigoProducto)) {
            cargarListaDeseos();
            cargarProductos(); // Actualizar corazones
            mostrarMensaje(this, "Producto eliminado de la lista de deseos", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void moverDeDeseoACarrito() {
        int filaSeleccionada = tablaDeseos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            mostrarMensaje(this, "Seleccione un producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int codigoProducto = (int) modeloTablaDeseos.getValueAt(filaSeleccionada, 0);
        Object stockObj = modeloTablaDeseos.getValueAt(filaSeleccionada, 3);
        
        if (stockObj instanceof String && stockObj.equals("Sin stock")) {
            mostrarMensaje(this, "Este producto no tiene stock disponible", "Sin stock", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Agregar al carrito
        if (servicioCompra.agregarProductoAlCarrito(carritoActual, codigoProducto, 1)) {
            // Eliminar de la lista de deseos
            servicioListaDeseos.eliminar(usuarioActual.getCodigo(), codigoProducto);
            
            cargarListaDeseos();
            cargarProductos();
            actualizarCarrito();
            
            mostrarMensaje(this, "Producto movido al carrito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            // Ir al carrito
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (tabbedPane.getTitleAt(i).equals("Carrito")) {
                    tabbedPane.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            mostrarMensaje(this, "No se pudo agregar al carrito. Verifique el stock.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ==================== PANEL MI PERFIL ====================
    
    private JPanel crearPanelMiPerfil() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel de contenido con fondo blanco
        JPanel panelContenido = new JPanel(new BorderLayout(10, 15));
        panelContenido.setBackground(COLOR_CARD);
        panelContenido.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(25, 30, 25, 30)
        ));
        
        // Título con icono
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTitulo.setBackground(COLOR_CARD);
        
        JLabel lblIcono = new JLabel("");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        panelTitulo.add(lblIcono);
        
        JLabel lblTitulo = new JLabel("Mi Perfil");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(TECH_DARK);
        panelTitulo.add(lblTitulo);
        
        panelContenido.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con formulario
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(COLOR_CARD);
        panelFormulario.setBorder(new EmptyBorder(20, 50, 20, 50));
        
        // Cargar datos actuales del usuario
        Usuario usuarioActualizado = new UsuarioDAO().obtenerPorCodigo(usuarioActual.getCodigo());
        if (usuarioActualizado != null) {
            usuarioActual = usuarioActualizado;
        }
        
        // Campos del formulario
        JTextField txtNombre = crearCampoPerfil(panelFormulario, "Nombre completo", usuarioActual.getNombre());
        JTextField txtUsuario = crearCampoPerfil(panelFormulario, "Usuario", usuarioActual.getUsuario());
        txtUsuario.setEditable(false);
        txtUsuario.setBackground(new Color(240, 240, 240));
        
        JTextField txtEmail = crearCampoPerfil(panelFormulario, "Email", 
            usuarioActual.getEmail() != null ? usuarioActual.getEmail() : "");
        JTextField txtTelefono = crearCampoPerfil(panelFormulario, "Teléfono", 
            usuarioActual.getTelefono() != null ? usuarioActual.getTelefono() : "");
        JTextField txtDireccion = crearCampoPerfil(panelFormulario, "Dirección", 
            usuarioActual.getDireccion() != null ? usuarioActual.getDireccion() : "");
        
        // Separador
        panelFormulario.add(Box.createVerticalStrut(15));
        JSeparator separador = new JSeparator();
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panelFormulario.add(separador);
        panelFormulario.add(Box.createVerticalStrut(15));
        
        // Sección de seguridad
        JLabel lblSeguridad = new JLabel("Seguridad y Recuperacion");
        lblSeguridad.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSeguridad.setForeground(TECH_BLUE);
        lblSeguridad.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelFormulario.add(lblSeguridad);
        panelFormulario.add(Box.createVerticalStrut(15));
        
        // Pregunta de seguridad
        String[] preguntas = {
            "Seleccione una pregunta de seguridad...",
            "¿Cuál es el nombre de tu primera mascota?",
            "¿En qué ciudad naciste?",
            "¿Cuál es tu película favorita?",
            "¿Cuál es el nombre de tu mejor amigo de la infancia?",
            "¿Cuál fue tu primer auto?",
            "¿Cuál es el nombre de tu escuela primaria?"
        };
        
        JLabel lblPregunta = new JLabel("Pregunta de seguridad");
        lblPregunta.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPregunta.setForeground(COLOR_TEXTO);
        lblPregunta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelFormulario.add(lblPregunta);
        panelFormulario.add(Box.createVerticalStrut(5));
        
        JComboBox<String> cmbPregunta = new JComboBox<>(preguntas);
        cmbPregunta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbPregunta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cmbPregunta.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Seleccionar pregunta actual si existe
        if (usuarioActual.getPreguntaSeguridad() != null && !usuarioActual.getPreguntaSeguridad().isEmpty()) {
            for (int i = 0; i < preguntas.length; i++) {
                if (preguntas[i].equals(usuarioActual.getPreguntaSeguridad())) {
                    cmbPregunta.setSelectedIndex(i);
                    break;
                }
            }
        }
        panelFormulario.add(cmbPregunta);
        panelFormulario.add(Box.createVerticalStrut(15));
        
        JTextField txtRespuesta = crearCampoPerfil(panelFormulario, "Respuesta de seguridad", 
            usuarioActual.getRespuestaSeguridad() != null ? usuarioActual.getRespuestaSeguridad() : "");
        
        // Separador para cambio de contraseña
        panelFormulario.add(Box.createVerticalStrut(10));
        JSeparator separador2 = new JSeparator();
        separador2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panelFormulario.add(separador2);
        panelFormulario.add(Box.createVerticalStrut(15));
        
        JLabel lblCambioPass = new JLabel("Cambiar Contraseña (opcional)");
        lblCambioPass.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCambioPass.setForeground(TECH_BLUE);
        lblCambioPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelFormulario.add(lblCambioPass);
        panelFormulario.add(Box.createVerticalStrut(15));
        
        JPasswordField txtContrasenaActual = (JPasswordField) crearCampoPerfil(panelFormulario, "Contraseña actual", "", true);
        JPasswordField txtNuevaContrasena = (JPasswordField) crearCampoPerfil(panelFormulario, "Nueva contraseña", "", true);
        JPasswordField txtConfirmarContrasena = (JPasswordField) crearCampoPerfil(panelFormulario, "Confirmar nueva contraseña", "", true);
        
        // Scroll para el formulario
        JScrollPane scrollFormulario = new JScrollPane(panelFormulario);
        scrollFormulario.setBorder(null);
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(16);
        panelContenido.add(scrollFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBotones.setBackground(COLOR_CARD);
        
        JButton btnGuardar = crearBotonExito("Guardar Cambios");
        btnGuardar.addActionListener(e -> {
            // Validar campos obligatorios
            if (txtNombre.getText().trim().isEmpty()) {
                mostrarMensaje(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar cambio de contraseña si se intenta
            String passActual = new String(txtContrasenaActual.getPassword());
            String nuevaPass = new String(txtNuevaContrasena.getPassword());
            String confirmarPass = new String(txtConfirmarContrasena.getPassword());
            
            if (!nuevaPass.isEmpty() || !confirmarPass.isEmpty()) {
                if (passActual.isEmpty()) {
                    mostrarMensaje(this, "Debe ingresar su contraseña actual para cambiarla", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!passActual.equals(usuarioActual.getContrasena())) {
                    mostrarMensaje(this, "La contraseña actual es incorrecta", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!nuevaPass.equals(confirmarPass)) {
                    mostrarMensaje(this, "Las contraseñas nuevas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (nuevaPass.length() < 4) {
                    mostrarMensaje(this, "La nueva contraseña debe tener al menos 4 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Actualizar datos del usuario
            usuarioActual.setNombre(txtNombre.getText().trim());
            usuarioActual.setEmail(txtEmail.getText().trim());
            usuarioActual.setTelefono(txtTelefono.getText().trim());
            usuarioActual.setDireccion(txtDireccion.getText().trim());
            
            // Pregunta y respuesta de seguridad
            if (cmbPregunta.getSelectedIndex() > 0) {
                usuarioActual.setPreguntaSeguridad((String) cmbPregunta.getSelectedItem());
                usuarioActual.setRespuestaSeguridad(txtRespuesta.getText().trim());
            }
            
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            boolean exito = usuarioDAO.actualizarPerfil(usuarioActual);
            
            // Cambiar contraseña si corresponde
            if (!nuevaPass.isEmpty() && exito) {
                exito = usuarioDAO.actualizarContrasena(usuarioActual.getCodigo(), nuevaPass);
                if (exito) {
                    usuarioActual.setContrasena(nuevaPass);
                    txtContrasenaActual.setText("");
                    txtNuevaContrasena.setText("");
                    txtConfirmarContrasena.setText("");
                }
            }
            
            if (exito) {
                mostrarMensaje(this, "¡Perfil actualizado correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                mostrarMensaje(this, "Error al actualizar el perfil", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelBotones.add(btnGuardar);
        
        panelContenido.add(panelBotones, BorderLayout.SOUTH);
        panel.add(panelContenido, BorderLayout.CENTER);
        
        // Panel lateral con información
        JPanel panelLateral = new JPanel();
        panelLateral.setLayout(new BoxLayout(panelLateral, BoxLayout.Y_AXIS));
        panelLateral.setBackground(COLOR_CARD);
        panelLateral.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(20, 20, 20, 20)
        ));
        panelLateral.setPreferredSize(new Dimension(250, 0));
        
        // Avatar
        JLabel lblAvatar = new JLabel("");
        lblAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLateral.add(lblAvatar);
        
        panelLateral.add(Box.createVerticalStrut(10));
        
        JLabel lblNombreUsuario = new JLabel(usuarioActual.getNombre());
        lblNombreUsuario.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombreUsuario.setForeground(TECH_DARK);
        lblNombreUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLateral.add(lblNombreUsuario);
        
        JLabel lblTipoUsuario = new JLabel("@" + usuarioActual.getUsuario());
        lblTipoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTipoUsuario.setForeground(new Color(100, 100, 100));
        lblTipoUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLateral.add(lblTipoUsuario);
        
        panelLateral.add(Box.createVerticalStrut(20));
        
        // Información de cuenta
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(new Color(248, 250, 252));
        panelInfo.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblPresupuesto = new JLabel("Presupuesto");
        lblPresupuesto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPresupuesto.setForeground(new Color(100, 100, 100));
        panelInfo.add(lblPresupuesto);
        
        JLabel lblPresupuestoValor = new JLabel(String.format("$%.2f", usuarioActual.getPresupuesto()));
        lblPresupuestoValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblPresupuestoValor.setForeground(TECH_GREEN);
        panelInfo.add(lblPresupuestoValor);
        
        panelInfo.add(Box.createVerticalStrut(10));
        
        // Estado de seguridad
        JLabel lblEstadoSeg = new JLabel("Recuperación");
        lblEstadoSeg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEstadoSeg.setForeground(new Color(100, 100, 100));
        panelInfo.add(lblEstadoSeg);
        
        boolean tieneRecuperacion = usuarioActual.getPreguntaSeguridad() != null 
            && !usuarioActual.getPreguntaSeguridad().isEmpty();
        JLabel lblEstadoSegValor = new JLabel(tieneRecuperacion ? "Configurada" : "Sin configurar");
        lblEstadoSegValor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstadoSegValor.setForeground(tieneRecuperacion ? TECH_GREEN : TECH_ORANGE);
        panelInfo.add(lblEstadoSegValor);
        
        panelLateral.add(panelInfo);
        
        panel.add(panelLateral, BorderLayout.EAST);
        
        return panel;
    }
    
    private JTextField crearCampoPerfil(JPanel panel, String etiqueta, String valor) {
        return crearCampoPerfil(panel, etiqueta, valor, false);
    }
    
    private JTextField crearCampoPerfil(JPanel panel, String etiqueta, String valor, boolean esPassword) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COLOR_TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        
        panel.add(Box.createVerticalStrut(5));
        
        JTextField campo = esPassword ? new JPasswordField(25) : new JTextField(25);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setText(valor);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(campo);
        
        panel.add(Box.createVerticalStrut(12));
        
        return campo;
    }

    private JPanel crearPanelMisCompras() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de contenido con fondo blanco
        JPanel panelContenido = new JPanel(new BorderLayout(10, 10));
        panelContenido.setBackground(COLOR_CARD);
        panelContenido.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Título con icono
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTitulo.setBackground(COLOR_CARD);
        
        JLabel lblIcono = new JLabel("");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        panelTitulo.add(lblIcono);
        
        JLabel lblTitulo = new JLabel("Historial de Compras");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(TECH_DARK);
        panelTitulo.add(lblTitulo);
        
        panelContenido.add(panelTitulo, BorderLayout.NORTH);

        // Tabla de compras
        String[] columnas = {"Nº Compra", "Fecha", "Total"};
        modeloTablaMisCompras = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaMisCompras = new JTable(modeloTablaMisCompras);
        estilizarTabla(tablaMisCompras);
        JScrollPane scrollPane = new JScrollPane(tablaMisCompras);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(COLOR_BLANCO);
        panelContenido.add(scrollPane, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(COLOR_CARD);
        
        JButton btnActualizar = crearBotonSecundario(" Actualizar");
        btnActualizar.addActionListener(e -> cargarMisCompras());
        panelBotones.add(btnActualizar);
        
        JButton btnDetalle = crearBotonPrimario(" Ver Detalle");
        btnDetalle.addActionListener(e -> verDetalleCompra());
        panelBotones.add(btnDetalle);

        panelContenido.add(panelBotones, BorderLayout.SOUTH);
        panel.add(panelContenido, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelAdministracion() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de contenido con fondo blanco
        JPanel panelContenido = new JPanel(new BorderLayout(10, 10));
        panelContenido.setBackground(COLOR_CARD);
        panelContenido.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Título con icono
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTitulo.setBackground(COLOR_CARD);
        
        JLabel lblIcono = new JLabel("");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        panelTitulo.add(lblIcono);
        
        JLabel lblTitulo = new JLabel("Administración de Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(TECH_DARK);
        panelTitulo.add(lblTitulo);
        
        panelContenido.add(panelTitulo, BorderLayout.NORTH);

        // Tabla de productos
        String[] columnas = {"Código", "Producto", "Stock", "Precio"};
        modeloTablaAdminProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaAdminProductos = new JTable(modeloTablaAdminProductos);
        estilizarTabla(tablaAdminProductos);
        JScrollPane scrollPane = new JScrollPane(tablaAdminProductos);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(COLOR_BLANCO);
        panelContenido.add(scrollPane, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(COLOR_CARD);
        
        JButton btnActualizar = crearBotonSecundario(" Actualizar");
        btnActualizar.addActionListener(e -> cargarProductosAdmin());
        panelBotones.add(btnActualizar);

        JButton btnEliminar = crearBotonError(" Eliminar");
        btnEliminar.addActionListener(e -> eliminarProducto());
        panelBotones.add(btnEliminar);

        JButton btnEditar = crearBotonSecundario(" Editar");
        btnEditar.addActionListener(e -> editarProducto());
        panelBotones.add(btnEditar);
        
        JButton btnNuevo = crearBotonExito(" Nuevo Producto");
        btnNuevo.addActionListener(e -> nuevoProducto());
        panelBotones.add(btnNuevo);

        panelContenido.add(panelBotones, BorderLayout.SOUTH);
        panel.add(panelContenido, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==================== PANEL SUPERIOR: Cards de Estadísticas ====================
        JPanel panelEstadisticas = new JPanel(new BorderLayout(10, 10));
        panelEstadisticas.setBackground(COLOR_CARD);
        panelEstadisticas.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Título
        JPanel panelTituloEst = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTituloEst.setBackground(COLOR_CARD);
        JLabel lblIconoEst = new JLabel("");
        lblIconoEst.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panelTituloEst.add(lblIconoEst);
        JLabel lblTituloEst = new JLabel("Resumen de Ventas");
        lblTituloEst.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloEst.setForeground(TECH_DARK);
        panelTituloEst.add(lblTituloEst);
        panelEstadisticas.add(panelTituloEst, BorderLayout.NORTH);

        // Cards
        JPanel panelCards = new JPanel(new GridLayout(1, 3, 15, 0));
        panelCards.setBackground(COLOR_CARD);
        panelCards.setBorder(new EmptyBorder(10, 0, 5, 0));

        lblTotalVentas = crearCardEstadistica("", "Total Ventas", "$0.00", TECH_GREEN);
        panelCards.add(lblTotalVentas.getParent());
        lblCantidadVendida = crearCardEstadistica("", "Unidades Vendidas", "0", TECH_BLUE);
        panelCards.add(lblCantidadVendida.getParent());
        lblProductosStockBajo = crearCardEstadistica("", "Productos Stock Bajo", "0", TECH_ORANGE);
        panelCards.add(lblProductosStockBajo.getParent());

        panelEstadisticas.add(panelCards, BorderLayout.CENTER);

        // ==================== PANEL CENTRAL: Tablas lado a lado ====================
        JPanel panelTablas = new JPanel(new GridLayout(1, 2, 15, 0));
        panelTablas.setBackground(COLOR_FONDO);

        // --- Panel Productos Más Vendidos ---
        JPanel panelVendidos = new JPanel(new BorderLayout(10, 10));
        panelVendidos.setBackground(COLOR_CARD);
        panelVendidos.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel panelTituloVend = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelTituloVend.setBackground(COLOR_CARD);
        JLabel lblIconoVend = new JLabel("");
        lblIconoVend.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panelTituloVend.add(lblIconoVend);
        JLabel lblTituloVend = new JLabel("Top 10 Productos Más Vendidos");
        lblTituloVend.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTituloVend.setForeground(TECH_DARK);
        panelTituloVend.add(lblTituloVend);
        panelVendidos.add(panelTituloVend, BorderLayout.NORTH);

        String[] columnasVendidos = {"Producto", "Cant.", "Total"};
        modeloTablaProductosMasVendidos = new DefaultTableModel(columnasVendidos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductosMasVendidos = new JTable(modeloTablaProductosMasVendidos);
        estilizarTabla(tablaProductosMasVendidos);
        tablaProductosMasVendidos.getColumnModel().getColumn(0).setPreferredWidth(280);
        tablaProductosMasVendidos.getColumnModel().getColumn(1).setPreferredWidth(50);
        tablaProductosMasVendidos.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaProductosMasVendidos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollVendidos = new JScrollPane(tablaProductosMasVendidos);
        scrollVendidos.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollVendidos.getViewport().setBackground(COLOR_BLANCO);
        panelVendidos.add(scrollVendidos, BorderLayout.CENTER);

        // --- Panel Stock Bajo ---
        JPanel panelStock = new JPanel(new BorderLayout(10, 10));
        panelStock.setBackground(COLOR_CARD);
        panelStock.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel panelTituloStock = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelTituloStock.setBackground(COLOR_CARD);
        JLabel lblIconoStock = new JLabel("");
        lblIconoStock.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panelTituloStock.add(lblIconoStock);
        JLabel lblTituloStock = new JLabel("Productos con Stock Bajo");
        lblTituloStock.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTituloStock.setForeground(TECH_DARK);
        panelTituloStock.add(lblTituloStock);
        JLabel lblUmbral = new JLabel("  (≤ " + servicioAdmin.getUmbralStockBajoDefault() + " unid.)");
        lblUmbral.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUmbral.setForeground(new Color(120, 120, 120));
        panelTituloStock.add(lblUmbral);
        panelStock.add(panelTituloStock, BorderLayout.NORTH);

        String[] columnasStock = {"Cód.", "Producto", "Stock", "Precio"};
        modeloTablaStockBajo = new DefaultTableModel(columnasStock, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaStockBajo = new JTable(modeloTablaStockBajo);
        estilizarTablaStockBajo(tablaStockBajo);
        tablaStockBajo.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaStockBajo.getColumnModel().getColumn(1).setPreferredWidth(250);
        tablaStockBajo.getColumnModel().getColumn(2).setPreferredWidth(50);
        tablaStockBajo.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaStockBajo.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollStock = new JScrollPane(tablaStockBajo);
        scrollStock.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollStock.getViewport().setBackground(COLOR_BLANCO);
        panelStock.add(scrollStock, BorderLayout.CENTER);

        panelTablas.add(panelVendidos);
        panelTablas.add(panelStock);

        // ==================== PANEL INFERIOR: Botón Actualizar ====================
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        panelBoton.setBackground(COLOR_FONDO);
        JButton btnActualizarReportes = crearBotonSecundario("Actualizar Reportes");
        btnActualizarReportes.addActionListener(e -> cargarDatosReportes());
        panelBoton.add(btnActualizarReportes);

        // Agregar todo al panel principal
        panel.add(panelEstadisticas, BorderLayout.NORTH);
        panel.add(panelTablas, BorderLayout.CENTER);
        panel.add(panelBoton, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel crearCardEstadistica(String icono, String titulo, String valor, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(226, 232, 240), 1, 8),
            new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblIcono = new JLabel(icono + " " + titulo);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblIcono.setForeground(new Color(100, 100, 100));
        lblIcono.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblIcono);

        card.add(Box.createVerticalStrut(5));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblValor);

        return lblValor;
    }

    private void estilizarTablaStockBajo(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(35);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(TECH_DARK);
        tabla.setSelectionForeground(COLOR_BLANCO);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setBackground(COLOR_BLANCO);
        
        // Estilizar header
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(TECH_ORANGE);
        header.setForeground(COLOR_BLANCO);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setOpaque(true);
        
        // Renderizador personalizado para el header
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setForeground(COLOR_TEXTO);
                label.setBackground(TECH_ORANGE);
                label.setOpaque(true);
                label.setBorder(new EmptyBorder(10, 10, 10, 10));
                label.setHorizontalAlignment(JLabel.LEFT);
                return label;
            }
        });
        
        // Renderizador para resaltar filas con stock crítico
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    // Obtener el stock de la fila
                    Object stockObj = table.getValueAt(row, 2);
                    int stock = 0;
                    if (stockObj instanceof Integer) {
                        stock = (Integer) stockObj;
                    }
                    
                    // Color de fondo según nivel de stock
                    if (stock == 0) {
                        setBackground(new Color(254, 226, 226)); // Rojo claro - sin stock
                    } else if (stock <= 5) {
                        setBackground(new Color(254, 243, 199)); // Amarillo claro - crítico
                    } else {
                        setBackground(row % 2 == 0 ? COLOR_BLANCO : new Color(248, 249, 252));
                    }
                } else {
                    setBackground(TECH_DARK);
                }
                
                // Color de texto
                if (isSelected) {
                    setForeground(COLOR_BLANCO);
                } else if (value != null && value.toString().startsWith("$")) {
                    setForeground(TECH_GREEN);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else {
                    setForeground(COLOR_TEXTO);
                    setFont(new Font("Segoe UI", Font.PLAIN, 13));
                }
                
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }

    private void verificarAlertaStockBajo() {
        int cantidadStockBajo = servicioAdmin.contarProductosStockBajo();
        
        if (cantidadStockBajo > 0) {
            List<Producto> productosStockBajo = servicioAdmin.obtenerProductosConStockBajo();
            
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("ALERTA DE INVENTARIO\n\n");
            mensaje.append("Hay ").append(cantidadStockBajo).append(" producto(s) con stock bajo:\n\n");
            
            int mostrar = Math.min(productosStockBajo.size(), 5);
            for (int i = 0; i < mostrar; i++) {
                Producto p = productosStockBajo.get(i);
                String estado = p.getCantidad() == 0 ? "SIN STOCK" : p.getCantidad() + " unid.";
                mensaje.append("• ").append(p.getDescripcion()).append(" - ").append(estado).append("\n");
            }
            
            if (productosStockBajo.size() > 5) {
                mensaje.append("\n... y ").append(productosStockBajo.size() - 5).append(" más.\n");
            }
            
            mensaje.append("\n¿Desea ir a la pestaña de Reportes para ver el detalle?");
            
            int respuesta = JOptionPane.showConfirmDialog(this,
                mensaje.toString(),
                "Alerta de Stock Bajo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                // Buscar el índice de la pestaña de Reportes
                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    if (tabbedPane.getTitleAt(i).equals("Reportes")) {
                        tabbedPane.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void cargarDatosReportes() {
        // Cargar estadísticas generales
        double totalVentas = servicioAdmin.obtenerTotalVentas();
        int cantidadVendida = servicioAdmin.obtenerCantidadProductosVendidos();
        int stockBajo = servicioAdmin.contarProductosStockBajo();
        
        lblTotalVentas.setText(String.format("$%.2f", totalVentas));
        lblCantidadVendida.setText(String.valueOf(cantidadVendida));
        lblProductosStockBajo.setText(String.valueOf(stockBajo));
        
        // Cargar productos más vendidos
        modeloTablaProductosMasVendidos.setRowCount(0);
        List<EstadisticaProducto> masVendidos = servicioAdmin.obtenerProductosMasVendidos(10);
        
        for (EstadisticaProducto est : masVendidos) {
            if (est.getCantidadVendida() > 0) {
                modeloTablaProductosMasVendidos.addRow(new Object[]{
                    est.getDescripcion(),
                    est.getCantidadVendida(),
                    String.format("$%.2f", est.getMontoTotal())
                });
            }
        }
        
        // Cargar productos con stock bajo
        modeloTablaStockBajo.setRowCount(0);
        List<Producto> productosStockBajo = servicioAdmin.obtenerProductosConStockBajo();
        
        for (Producto p : productosStockBajo) {
            modeloTablaStockBajo.addRow(new Object[]{
                p.getCodigo(),
                p.getDescripcion(),
                p.getCantidad(),
                String.format("$%.2f", p.getPrecio())
            });
        }
    }
    
    // Métodos para estilizar componentes
    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(35);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(TECH_DARK);
        tabla.setSelectionForeground(COLOR_BLANCO);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setBackground(COLOR_BLANCO);
        
        // Estilizar header
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(TECH_DARK);
        header.setForeground(COLOR_BLANCO);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setOpaque(true);
        
        // Renderizador personalizado para el header
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setForeground(COLOR_BLANCO);
                label.setBackground(TECH_DARK);
                label.setOpaque(true);
                label.setBorder(new EmptyBorder(10, 10, 10, 10));
                label.setHorizontalAlignment(JLabel.LEFT);
                return label;
            }
        });
        
        // Renderizador personalizado para alternar colores de filas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Establecer color de fondo
                if (!isSelected) {
                    if (row % 2 == 0) {
                        setBackground(COLOR_BLANCO);
                    } else {
                        setBackground(new Color(248, 249, 252));
                    }
                } else {
                    // Fondo de selección - mismo color que el header
                    setBackground(TECH_DARK);
                }
                
                // Establecer color de texto y fuente
                if (isSelected) {
                    // Cuando está seleccionado, todo el texto en blanco
                    setForeground(COLOR_BLANCO);
                    if (value != null && value.toString().startsWith("$")) {
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                } else {
                    // Cuando no está seleccionado
                    if (value != null && value.toString().startsWith("$")) {
                        // Columnas de precio en verde
                        setForeground(TECH_GREEN);
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        // Texto normal - oscuro para legibilidad
                        setForeground(COLOR_TEXTO);
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                }
                
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }
    
    private JButton crearBotonPrimario(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(COLOR_BLANCO);
        boton.setBackground(TECH_BLUE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setPreferredSize(new Dimension(boton.getPreferredSize().width + 20, 38));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(TECH_DARK);
            }
            public void mouseExited(MouseEvent e) {
                boton.setBackground(TECH_BLUE);
            }
        });
        
        return boton;
    }
    
    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setForeground(TECH_BLUE);
        boton.setBackground(COLOR_BLANCO);
        boton.setFocusPainted(false);
        boton.setBorder(new RoundedBorder(TECH_BLUE, 2, 8));
        boton.setContentAreaFilled(false);
        boton.setPreferredSize(new Dimension(boton.getPreferredSize().width + 20, 38));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(TECH_BLUE_LIGHT);
            }
            public void mouseExited(MouseEvent e) {
                boton.setBackground(COLOR_BLANCO);
            }
        });
        
        return boton;
    }
    
    private JButton crearBotonExito(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(COLOR_BLANCO);
        boton.setBackground(TECH_GREEN);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setPreferredSize(new Dimension(boton.getPreferredSize().width + 20, 38));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(35, 200, 70));
            }
            public void mouseExited(MouseEvent e) {
                boton.setBackground(TECH_GREEN);
            }
        });
        
        return boton;
    }
    
    private JButton crearBotonAdvertencia(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(COLOR_TEXTO);
        boton.setBackground(TECH_ORANGE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setPreferredSize(new Dimension(boton.getPreferredSize().width + 20, 38));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(220, 220, 40));
            }
            public void mouseExited(MouseEvent e) {
                boton.setBackground(TECH_ORANGE);
            }
        });
        
        return boton;
    }
    
    private JButton crearBotonError(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(COLOR_BLANCO);
        boton.setBackground(TECH_RED);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setPreferredSize(new Dimension(boton.getPreferredSize().width + 20, 38));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(230, 50, 65));
            }
            public void mouseExited(MouseEvent e) {
                boton.setBackground(TECH_RED);
            }
        });
        
        return boton;
    }
    
    // Clases auxiliares para bordes
    class RoundedBorder extends AbstractBorder {
        private Color color;
        private int thickness;
        private int radius;
        
        RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }
    
    class ShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Sombra sutil
            for (int i = 0; i < 6; i++) {
                g2.setColor(new Color(0, 0, 0, 8 - i));
                g2.drawRoundRect(x + i, y + i, width - 1 - (i * 2), height - 1 - (i * 2), 8, 8);
            }
            
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 6, 6, 6);
        }
    }

    private void configurarVentana() {
        setTitle("Sistema de Ventas - " + usuarioActual.getNombre());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new java.awt.Dimension(1000, 700));
        
        // Menú estilizado
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COLOR_BLANCO);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        menuArchivo.setForeground(COLOR_TEXTO);
        
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar Sesión");
        itemCerrarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemCerrarSesion.addActionListener(e -> cerrarSesion());
        menuArchivo.add(itemCerrarSesion);
        
        menuArchivo.addSeparator();
        
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);
        
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);
    }

    private void cargarDatos() {
        cargarProductos();
        
        // Solo cargar carrito, deseos y compras si no es invitado
        if (!usuarioActual.getUsuario().equalsIgnoreCase("invitado")) {
            actualizarCarrito();
            cargarListaDeseos();
            cargarMisCompras();
            
            // Verificar si hay productos en lista de deseos con stock disponible
            verificarProductosDeseosDisponibles();
        }
        
        if (usuarioActual.getUsuario().equalsIgnoreCase("admin")) {
            cargarProductosAdmin();
            cargarDatosReportes();
        }
    }
    
    private void verificarProductosDeseosDisponibles() {
        List<ItemDeseo> disponibles = servicioListaDeseos.obtenerProductosDisponibles(usuarioActual.getCodigo());
        
        if (!disponibles.isEmpty()) {
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("¡Buenas noticias!\n\n");
            mensaje.append("Algunos productos de tu lista de deseos ya están disponibles:\n\n");
            
            int mostrar = Math.min(disponibles.size(), 5);
            for (int i = 0; i < mostrar; i++) {
                ItemDeseo item = disponibles.get(i);
                mensaje.append("• ").append(item.getDescripcionProducto())
                       .append(" (").append(item.getStockProducto()).append(" en stock)\n");
            }
            
            if (disponibles.size() > 5) {
                mensaje.append("\n... y ").append(disponibles.size() - 5).append(" más.\n");
            }
            
            mensaje.append("\n¿Deseas ir a tu Lista de Deseos?");
            
            int respuesta = JOptionPane.showConfirmDialog(this,
                mensaje.toString(),
                "Productos Disponibles",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    if (tabbedPane.getTitleAt(i).equals("Lista de Deseos")) {
                        tabbedPane.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void cargarProductos() {
        modeloTablaProductos.setRowCount(0);
        List<Producto> productos = servicioProducto.listarProductosDisponibles();
        
        for (Producto p : productos) {
            boolean enDeseos = !usuarioActual.getUsuario().equalsIgnoreCase("invitado") && 
                              servicioListaDeseos.estaEnListaDeseos(usuarioActual.getCodigo(), p.getCodigo());
            
            modeloTablaProductos.addRow(new Object[]{
                p.getCodigo(),
                p.getDescripcion(),
                p.getCategoria() != null ? p.getCategoria() : "-",
                p.getCantidad(),
                String.format("$%.2f", p.getPrecio()),
                enDeseos ? "Si" : "No"
            });
        }
    }

    private void agregarProductoAlCarrito() {
        // Verificar si es invitado
        if (usuarioActual.getUsuario().equalsIgnoreCase("invitado")) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                "Debes registrarte para agregar productos al carrito.\n¿Deseas registrarte ahora?",
                "Registro requerido",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                // Cerrar ventana actual y volver al login
                this.dispose();
                LoginVentana loginVentana = new LoginVentana();
                loginVentana.setVisible(true);
            }
            return;
        }
        
        int filaSeleccionada = tablaProductos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            mostrarMensaje(this, "Seleccione un producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigoProducto = (int) modeloTablaProductos.getValueAt(filaSeleccionada, 0);
        
        String cantidadStr = JOptionPane.showInputDialog(this, 
                "Ingrese la cantidad:", 
                "Cantidad", 
                JOptionPane.QUESTION_MESSAGE);
        
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            
            if (cantidad <= 0) {
                mostrarMensaje(this, "La cantidad debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (servicioCompra.agregarProductoAlCarrito(carritoActual, codigoProducto, cantidad)) {
                mostrarMensaje(this, "Producto agregado al carrito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarCarrito();
                tabbedPane.setSelectedIndex(1); // Ir a pestaña de carrito
            } else {
                mostrarMensaje(this, "No hay stock suficiente", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            mostrarMensaje(this, "Cantidad inválida", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCarrito() {
        // Verificar si no es invitado
        if (usuarioActual.getUsuario().equalsIgnoreCase("invitado") || modeloTablaCarrito == null) {
            return;
        }
        
        modeloTablaCarrito.setRowCount(0);
        
        for (DetalleCompra detalle : carritoActual.getDetalles()) {
            modeloTablaCarrito.addRow(new Object[]{
                detalle.getDescripcionProducto(),
                String.format("$%.2f", detalle.getPrecioUnitario()),
                detalle.getCantidad(),
                String.format("$%.2f", detalle.getSubtotal())
            });
        }
        
        actualizarTotalesCarrito();
    }

    private void eliminarDelCarrito() {
        int filaSeleccionada = tablaCarrito.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            mostrarMensaje(this, "Seleccione un producto del carrito", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigoProducto = carritoActual.getDetalles().get(filaSeleccionada).getCodigoProducto();
        
        if (servicioCompra.eliminarProductoDelCarrito(carritoActual, codigoProducto)) {
            actualizarCarrito();
        }
    }

    private void vaciarCarrito() {
        if (carritoActual.getDetalles().isEmpty()) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de vaciar el carrito?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            carritoActual.getDetalles().clear();
            actualizarCarrito();
        }
    }

    private void finalizarCompra() {
        if (servicioCompra.estaVacia(carritoActual)) {
            mostrarMensaje(this, "El carrito está vacío", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double subtotalCompra = carritoActual.calcularTotal();
        double montoDescuento = 0;
        double totalCompra = subtotalCompra;
        
        // Aplicar descuento si hay cupón
        if (descuentoAplicado != null) {
            montoDescuento = descuentoAplicado.calcularDescuento(subtotalCompra);
            totalCompra = subtotalCompra - montoDescuento;
        }
        
        double presupuestoActual = usuarioActual.getPresupuesto();
        
        // Validar que tenga presupuesto suficiente
        if (presupuestoActual < totalCompra) {
            mostrarMensaje(this, 
                String.format("Presupuesto insuficiente\n\nTotal a pagar: $%.2f\nPresupuesto disponible: $%.2f\nFaltante: $%.2f", 
                    totalCompra, presupuestoActual, (totalCompra - presupuestoActual)),
                "Presupuesto insuficiente", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double presupuestoRestante = presupuestoActual - totalCompra;
        
        // Construir mensaje de confirmación
        StringBuilder msgConfirmacion = new StringBuilder();
        msgConfirmacion.append(String.format("Subtotal: $%.2f\n", subtotalCompra));
        
        if (descuentoAplicado != null) {
            msgConfirmacion.append(String.format("Descuento (%s): -$%.2f\n", 
                descuentoAplicado.getCodigo(), montoDescuento));
        }
        
        msgConfirmacion.append(String.format("Total a pagar: $%.2f\n\n", totalCompra));
        msgConfirmacion.append(String.format("Presupuesto actual: $%.2f\n", presupuestoActual));
        msgConfirmacion.append(String.format("Presupuesto restante: $%.2f\n\n", presupuestoRestante));
        msgConfirmacion.append("¿Confirmar compra?");

        int opcion = JOptionPane.showConfirmDialog(this,
                msgConfirmacion.toString(),
                "Confirmar Compra",
                JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            if (servicioCompra.registrarCompra(carritoActual)) {
                // Registrar uso del cupón si aplica
                if (descuentoAplicado != null) {
                    servicioDescuento.registrarUso(descuentoAplicado.getCodigo());
                }
                
                // Actualizar presupuesto del usuario
                usuarioActual.setPresupuesto(presupuestoRestante);
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                usuarioDAO.actualizarPresupuesto(usuarioActual.getCodigo(), presupuestoRestante);
                
                // Construir mensaje de éxito
                StringBuilder msgExito = new StringBuilder();
                msgExito.append(String.format("Compra realizada exitosamente\n\n"));
                msgExito.append(String.format("Nº de Compra: %d\n", carritoActual.getNumeroCompra()));
                
                if (descuentoAplicado != null) {
                    msgExito.append(String.format("Descuento aplicado: $%.2f\n", montoDescuento));
                }
                
                msgExito.append(String.format("Total pagado: $%.2f\n", totalCompra));
                msgExito.append(String.format("Presupuesto restante: $%.2f", presupuestoRestante));
                
                mostrarMensaje(this, msgExito.toString(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                // Actualizar header con nuevo presupuesto
                actualizarHeader();
                
                // Limpiar descuento y crear nuevo carrito
                descuentoAplicado = null;
                txtCodigoDescuento.setText("");
                carritoActual = servicioCompra.crearNuevaCompra(usuarioActual.getCodigo());
                actualizarCarrito();
                cargarProductos();
                cargarMisCompras();
            } else {
                mostrarMensaje(this, "Error al procesar la compra", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void actualizarHeader() {
        // Remover componentes actuales
        getContentPane().removeAll();
        
        // Reinicializar componentes
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);
        
        JPanel header = crearHeader();
        panelPrincipal.add(header, BorderLayout.NORTH);
        panelPrincipal.add(tabbedPane, BorderLayout.CENTER);
        
        add(panelPrincipal);
        revalidate();
        repaint();
    }

    private void cargarMisCompras() {
        modeloTablaMisCompras.setRowCount(0);
        List<Compra> compras = servicioCompra.listarComprasPorCliente(usuarioActual.getCodigo());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Compra c : compras) {
            modeloTablaMisCompras.addRow(new Object[]{
                c.getNumeroCompra(),
                c.getFecha().format(formatter),
                String.format("$%.2f", c.calcularTotal())
            });
        }
    }

    private void verDetalleCompra() {
        int filaSeleccionada = tablaMisCompras.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            mostrarMensaje(this, "Seleccione una compra", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int numeroCompra = (int) modeloTablaMisCompras.getValueAt(filaSeleccionada, 0);
        Compra compra = servicioCompra.obtenerCompraPorNumero(numeroCompra);
        
        if (compra != null) {
            mostrarDialogoDetalle(compra);
        }
    }
    
    private void mostrarDialogoDetalle(Compra compra) {
        JDialog dialogo = new JDialog(this, "Detalle de Compra", true);
        dialogo.setLayout(new BorderLayout());
        
        JPanel panelFondo = new JPanel(new GridBagLayout());
        panelFondo.setBackground(COLOR_FONDO);
        panelFondo.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(COLOR_BLANCO);
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(30, 40, 30, 40)
        ));
        
        JLabel lblTitulo = new JLabel("Compra Nº " + compra.getNumeroCompra());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(TECH_DARK);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(lblTitulo);
        
        panelCentral.add(Box.createVerticalStrut(5));
        
        JLabel lblFecha = new JLabel("Fecha: " + compra.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFecha.setForeground(COLOR_TEXTO);
        lblFecha.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(lblFecha);
        
        panelCentral.add(Box.createVerticalStrut(20));
        
        JLabel lblProductos = new JLabel("Productos:");
        lblProductos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblProductos.setForeground(COLOR_TEXTO);
        lblProductos.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(lblProductos);
        
        panelCentral.add(Box.createVerticalStrut(10));
        
        for (DetalleCompra d : compra.getDetalles()) {
            JPanel panelItem = new JPanel(new BorderLayout());
            panelItem.setBackground(new Color(248, 249, 252));
            panelItem.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(230, 230, 230), 1, 6),
                new EmptyBorder(10, 15, 10, 15)
            ));
            panelItem.setMaximumSize(new Dimension(450, 60));
            panelItem.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JPanel panelInfo = new JPanel();
            panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
            panelInfo.setBackground(new Color(248, 249, 252));
            
            JLabel lblNombre = new JLabel(d.getDescripcionProducto());
            lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblNombre.setForeground(COLOR_TEXTO);
            panelInfo.add(lblNombre);
            
            JLabel lblCantidad = new JLabel("Cantidad: " + d.getCantidad());
            lblCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblCantidad.setForeground(new Color(120, 120, 120));
            panelInfo.add(lblCantidad);
            
            panelItem.add(panelInfo, BorderLayout.WEST);
            
            JLabel lblPrecio = new JLabel(String.format("$%.2f", d.getSubtotal()));
            lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblPrecio.setForeground(TECH_GREEN);
            panelItem.add(lblPrecio, BorderLayout.EAST);
            
            panelCentral.add(panelItem);
            panelCentral.add(Box.createVerticalStrut(8));
        }
        
        panelCentral.add(Box.createVerticalStrut(10));
        
        JSeparator separador = new JSeparator();
        separador.setMaximumSize(new Dimension(450, 1));
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(separador);
        
        panelCentral.add(Box.createVerticalStrut(15));
        
        JLabel lblTotal = new JLabel(String.format("Total: $%.2f", compra.calcularTotal()));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(TECH_GREEN);
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(lblTotal);
        
        panelCentral.add(Box.createVerticalStrut(20));
        
        JButton btnCerrar = crearBotonPrimario("Cerrar");
        btnCerrar.addActionListener(e -> dialogo.dispose());
        btnCerrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(btnCerrar);
        
        panelFondo.add(panelCentral);
        dialogo.add(panelFondo);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    private void cargarProductosAdmin() {
        modeloTablaAdminProductos.setRowCount(0);
        List<Producto> productos = servicioAdmin.listarProductos();
        
        for (Producto p : productos) {
            modeloTablaAdminProductos.addRow(new Object[]{
                p.getCodigo(),
                p.getDescripcion(),
                p.getCantidad(),
                String.format("$%.2f", p.getPrecio())
            });
        }
    }

    private void nuevoProducto() {
        mostrarFormularioProducto(null);
    }

    private void editarProducto() {
        int filaSeleccionada = tablaAdminProductos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            mostrarMensaje(this, "Seleccione un producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = (int) modeloTablaAdminProductos.getValueAt(filaSeleccionada, 0);
        Producto producto = servicioAdmin.buscarProducto(codigo);
        
        mostrarFormularioProducto(producto);
    }

    private void mostrarFormularioProducto(Producto producto) {
        boolean esNuevo = (producto == null);
        
        JDialog dialogo = new JDialog(this, esNuevo ? "Nuevo Producto" : "Editar Producto", true);
        dialogo.setLayout(new BorderLayout());
        
        JPanel panelFondo = new JPanel(new GridBagLayout());
        panelFondo.setBackground(COLOR_FONDO);
        panelFondo.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(COLOR_BLANCO);
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(30, 40, 30, 40)
        ));
        
        JLabel lblTitulo = new JLabel(esNuevo ? "Nuevo Producto" : "Editar Producto");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(TECH_DARK);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(lblTitulo);
        
        panelCentral.add(Box.createVerticalStrut(25));

        // Descripción
        JLabel lblDescripcion = new JLabel("Descripción");
        lblDescripcion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDescripcion.setForeground(COLOR_TEXTO);
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(lblDescripcion);
        
        panelCentral.add(Box.createVerticalStrut(8));
        
        JTextField txtDescripcion = new JTextField(20);
        if (!esNuevo) txtDescripcion.setText(producto.getDescripcion());
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescripcion.setMaximumSize(new Dimension(350, 40));
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(TECH_BLUE_LIGHT, 1, 8),
            new EmptyBorder(8, 15, 8, 15)
        ));
        txtDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(txtDescripcion);
        
        panelCentral.add(Box.createVerticalStrut(18));

        // Cantidad
        JLabel lblCantidad = new JLabel("Cantidad (Stock)");
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCantidad.setForeground(COLOR_TEXTO);
        lblCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(lblCantidad);
        
        panelCentral.add(Box.createVerticalStrut(8));
        
        JTextField txtCantidad = new JTextField(20);
        if (!esNuevo) txtCantidad.setText(String.valueOf(producto.getCantidad()));
        txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCantidad.setMaximumSize(new Dimension(350, 40));
        txtCantidad.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(TECH_BLUE_LIGHT, 1, 8),
            new EmptyBorder(8, 15, 8, 15)
        ));
        txtCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(txtCantidad);
        
        panelCentral.add(Box.createVerticalStrut(18));

        // Precio
        JLabel lblPrecio = new JLabel("Precio");
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPrecio.setForeground(COLOR_TEXTO);
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(lblPrecio);
        
        panelCentral.add(Box.createVerticalStrut(8));
        
        JTextField txtPrecio = new JTextField(20);
        if (!esNuevo) txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtPrecio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPrecio.setMaximumSize(new Dimension(350, 40));
        txtPrecio.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(TECH_BLUE_LIGHT, 1, 8),
            new EmptyBorder(8, 15, 8, 15)
        ));
        txtPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(txtPrecio);
        
        panelCentral.add(Box.createVerticalStrut(25));

        // Botones
        JButton btnGuardar = crearBotonExito("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                String descripcion = txtDescripcion.getText();
                int cantidad = Integer.parseInt(txtCantidad.getText());
                double precio = Double.parseDouble(txtPrecio.getText());

                boolean exito;
                if (esNuevo) {
                    exito = servicioAdmin.agregarProducto(descripcion, cantidad, precio);
                } else {
                    producto.setDescripcion(descripcion);
                    producto.setCantidad(cantidad);
                    producto.setPrecio(precio);
                    exito = servicioAdmin.actualizarProducto(producto);
                }

                if (exito) {
                    mostrarMensaje(dialogo, "Guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarProductosAdmin();
                    cargarProductos();
                    dialogo.dispose();
                } else {
                    mostrarMensaje(dialogo, "Error al guardar", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                mostrarMensaje(dialogo, "Datos inválidos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnGuardar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(btnGuardar);
        
        panelCentral.add(Box.createVerticalStrut(12));

        JButton btnCancelar = crearBotonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dialogo.dispose());
        btnCancelar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(btnCancelar);
        
        panelFondo.add(panelCentral);
        dialogo.add(panelFondo);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    private void eliminarProducto() {
        int filaSeleccionada = tablaAdminProductos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            mostrarMensaje(this, "Seleccione un producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = (int) modeloTablaAdminProductos.getValueAt(filaSeleccionada, 0);
        
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este producto?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            if (servicioAdmin.eliminarProducto(codigo)) {
                mostrarMensaje(this, "Producto eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductosAdmin();
                cargarProductos();
            } else {
                mostrarMensaje(this, "Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de cerrar sesión?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            LoginVentana loginVentana = new LoginVentana();
            loginVentana.setVisible(true);
            this.dispose();
        }
    }
    
    private void mostrarMensaje(Component parent, String mensaje, String titulo, int tipo) {
        UIManager.put("OptionPane.background", COLOR_BLANCO);
        UIManager.put("Panel.background", COLOR_BLANCO);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXTO);
        JOptionPane.showMessageDialog(parent, mensaje, titulo, tipo);
    }
}
