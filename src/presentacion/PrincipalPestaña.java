package presentacion;

import modelo.Usuario;
import modelo.Producto;
import modelo.Compra;
import modelo.DetalleCompra;
import servicios.ServicioProducto;
import servicios.ServicioCompra;
import servicios.ServicioAdmin;
import datos.UsuarioDAO;

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
    
    // Componentes principales
    private JTabbedPane tabbedPane;
    private Compra carritoActual;

    // Pestaña de Productos
    private JTable tablaProductos;
    private DefaultTableModel modeloTablaProductos;
    
    // Pestaña de Carrito
    private JTable tablaCarrito;
    private DefaultTableModel modeloTablaCarrito;
    private JLabel lblTotal;
    
    // Pestaña de Mis Compras
    private JTable tablaMisCompras;
    private DefaultTableModel modeloTablaMisCompras;
    
    // Pestaña de Administración (solo para admin)
    private JTable tablaAdminProductos;
    private DefaultTableModel modeloTablaAdminProductos;

    public PrincipalPestaña(Usuario usuario) {
        this.usuarioActual = usuario;
        this.servicioProducto = new ServicioProducto();
        this.servicioCompra = new ServicioCompra();
        this.servicioAdmin = new ServicioAdmin();
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
            tabbedPane.addTab("Mis Compras", crearPanelMisCompras());
        }
        
        // Solo mostrar administración si es usuario "admin"
        if (usuarioActual.getUsuario().equalsIgnoreCase("admin")) {
            tabbedPane.addTab("Administración", crearPanelAdministracion());
        }

        panelPrincipal.add(tabbedPane, BorderLayout.CENTER);
        add(panelPrincipal);
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
        
        panelContenido.add(panelTitulo, BorderLayout.NORTH);

        // Tabla de productos
        String[] columnas = {"Código", "Producto", "Stock", "Precio"};
        modeloTablaProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloTablaProductos);
        estilizarTabla(tablaProductos);
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

        JButton btnAgregar = crearBotonPrimario(" Agregar al Carrito");
        btnAgregar.addActionListener(e -> agregarProductoAlCarrito());
        panelBotones.add(btnAgregar);

        panelContenido.add(panelBotones, BorderLayout.SOUTH);
        panel.add(panelContenido, BorderLayout.CENTER);

        return panel;
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
        
        // Total con estilo
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.setBackground(COLOR_CARD);
        panelTotal.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotal.setForeground(TECH_GREEN);
        panelTotal.add(lblTotal);
        panelInferior.add(panelTotal, BorderLayout.NORTH);

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
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
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
        
        // Solo cargar carrito y compras si no es invitado
        if (!usuarioActual.getUsuario().equalsIgnoreCase("invitado")) {
            actualizarCarrito();
            cargarMisCompras();
        }
        
        if (usuarioActual.getUsuario().equalsIgnoreCase("admin")) {
            cargarProductosAdmin();
        }
    }

    private void cargarProductos() {
        modeloTablaProductos.setRowCount(0);
        List<Producto> productos = servicioProducto.listarProductosDisponibles();
        
        for (Producto p : productos) {
            modeloTablaProductos.addRow(new Object[]{
                p.getCodigo(),
                p.getDescripcion(),
                p.getCantidad(),
                String.format("$%.2f", p.getPrecio())
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
        
        lblTotal.setText(String.format("Total: $%.2f", carritoActual.calcularTotal()));
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

        double totalCompra = carritoActual.calcularTotal();
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

        int opcion = JOptionPane.showConfirmDialog(this,
                String.format("Total a pagar: $%.2f\nPresupuesto actual: $%.2f\nPresupuesto restante: $%.2f\n\n¿Confirmar compra?", 
                    totalCompra, presupuestoActual, presupuestoRestante),
                "Confirmar Compra",
                JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            if (servicioCompra.registrarCompra(carritoActual)) {
                // Actualizar presupuesto del usuario
                usuarioActual.setPresupuesto(presupuestoRestante);
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                usuarioDAO.actualizarPresupuesto(usuarioActual.getCodigo(), presupuestoRestante);
                
                mostrarMensaje(this, 
                    String.format("Compra realizada exitosamente\n\nNº de Compra: %d\nTotal pagado: $%.2f\nPresupuesto restante: $%.2f",
                        carritoActual.getNumeroCompra(), totalCompra, presupuestoRestante),
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Actualizar header con nuevo presupuesto
                actualizarHeader();
                
                // Crear nuevo carrito
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
