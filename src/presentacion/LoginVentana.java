package presentacion;

import servicios.ServicioLogin;
import modelo.Usuario;
import datos.UsuarioDAO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginVentana extends JFrame {
    private ServicioLogin servicioLogin;
    private UsuarioDAO usuarioDAO;
    
    // Paleta de colores - Tema Electrónica Moderna (TechStore)
    private static final Color TECH_DARK = new Color(15, 23, 42);        // Fondo oscuro principal
    private static final Color TECH_BLUE = new Color(59, 130, 246);      // Azul tecnológico
    private static final Color TECH_BLUE_LIGHT = new Color(96, 165, 250); // Azul claro
    private static final Color TECH_CYAN = new Color(34, 211, 238);      // Cyan brillante
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_TEXTO = new Color(15, 23, 42);
    private static final Color COLOR_FONDO = new Color(248, 250, 252);
    
    // Componentes
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JButton btnRegistrar;

    public LoginVentana() {
        this.servicioLogin = new ServicioLogin();
        this.usuarioDAO = new UsuarioDAO();
        inicializarComponentes();
        configurarVentana();
    }

    private void inicializarComponentes() {
        // Panel principal con fondo
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridBagLayout());
        panelPrincipal.setBackground(COLOR_FONDO);
        
        // Panel central (tarjeta blanca con sombra)
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(COLOR_BLANCO);
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(50, 60, 50, 60)
        ));     
        // Título
        JLabel lblTitulo = new JLabel("TechStore");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(TECH_DARK);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblTitulo);
        
        JLabel lblSubtitulo = new JLabel("Sistema de Ventas de Electrónica");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(COLOR_TEXTO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblSubtitulo);
        
        panelCentral.add(Box.createVerticalStrut(40));
        
        // Campo Usuario
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsuario.setForeground(COLOR_TEXTO);
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblUsuario);
        
        panelCentral.add(Box.createVerticalStrut(8));
        
        txtUsuario = new JTextField(20);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setMaximumSize(new Dimension(320, 40));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(TECH_BLUE_LIGHT, 1, 8),
            new EmptyBorder(8, 15, 8, 15)
        ));
        txtUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(txtUsuario);
        
        panelCentral.add(Box.createVerticalStrut(20));
        
        // Campo Contraseña
        JLabel lblContrasena = new JLabel("Contraseña");
        lblContrasena.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblContrasena.setForeground(COLOR_TEXTO);
        lblContrasena.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblContrasena);
        
        panelCentral.add(Box.createVerticalStrut(8));
        
        txtContrasena = new JPasswordField(20);
        txtContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContrasena.setMaximumSize(new Dimension(320, 40));
        txtContrasena.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(TECH_BLUE_LIGHT, 1, 8),
            new EmptyBorder(8, 15, 8, 15)
        ));
        txtContrasena.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(txtContrasena);
        
        panelCentral.add(Box.createVerticalStrut(30));
        
        // Botón Ingresar
        btnIngresar = crearBotonPrimario("Ingresar");
        btnIngresar.addActionListener(e -> iniciarSesion());
        panelCentral.add(btnIngresar);
        
        panelCentral.add(Box.createVerticalStrut(12));
        
        // Botón Registrar
        btnRegistrar = crearBotonSecundario("Crear cuenta nueva");
        btnRegistrar.addActionListener(e -> mostrarDialogoRegistro());
        panelCentral.add(btnRegistrar);
        
        panelCentral.add(Box.createVerticalStrut(12));
        
        // Botón Invitado
        JButton btnInvitado = crearBotonTerciario("Ingresar como invitado");
        btnInvitado.addActionListener(e -> ingresarComoInvitado());
        panelCentral.add(btnInvitado);
        
        panelCentral.add(Box.createVerticalStrut(8));
        
        // Botón Recuperar Contraseña
        JButton btnRecuperar = crearBotonTerciario("¿Olvidaste tu contraseña?");
        btnRecuperar.addActionListener(e -> mostrarDialogoRecuperacion());
        panelCentral.add(btnRecuperar);
        
        // Enter en campos de texto
        txtUsuario.addActionListener(e -> txtContrasena.requestFocus());
        txtContrasena.addActionListener(e -> iniciarSesion());
        
        // Agregar panel central al panel principal
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelPrincipal.add(panelCentral, gbc);
        
        add(panelPrincipal);
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
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(COLOR_BLANCO);
        boton.setBackground(TECH_BLUE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setMaximumSize(new Dimension(320, 45));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
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
                if (!getModel().isPressed()) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
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
        boton.setMaximumSize(new Dimension(320, 45));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
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
    
    private JButton crearBotonTerciario(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        boton.setForeground(new Color(100, 100, 100));
        boton.setBackground(COLOR_BLANCO);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setMaximumSize(new Dimension(320, 35));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setForeground(TECH_BLUE);
            }
            public void mouseExited(MouseEvent e) {
                boton.setForeground(new Color(100, 100, 100));
            }
        });
        
        return boton;
    }
    
    // Clase para borde redondeado
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
    
    // Clase para sombra sutil
    class ShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Sombra
            for (int i = 0; i < 8; i++) {
                g2.setColor(new Color(0, 0, 0, 10 - i));
                g2.drawRoundRect(x + i, y + i, width - 1 - (i * 2), height - 1 - (i * 2), 12, 12);
            }
            
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 8, 8, 8);
        }
    }

    private void configurarVentana() {
        setTitle("Login - Sistema de Ventas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarMensaje(this, "Por favor ingrese usuario y contraseña", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuarioEncontrado = servicioLogin.validarCredenciales(usuario, contrasena);

        if (usuarioEncontrado != null) {
            // Login exitoso
            PrincipalPestaña ventanaPrincipal = new PrincipalPestaña(usuarioEncontrado);
            ventanaPrincipal.setVisible(true);
            this.dispose();
        } else {
            // Login fallido
            mostrarMensaje(this, "Usuario o contraseña incorrectos", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
            txtContrasena.setText("");
            txtUsuario.requestFocus();
        }
    }
    
    private void ingresarComoInvitado() {
        // Crear un usuario invitado temporal
        Usuario invitado = new Usuario(0, "Invitado", "invitado", "", 0.0);
        PrincipalPestaña ventanaPrincipal = new PrincipalPestaña(invitado);
        ventanaPrincipal.setVisible(true);
        this.dispose();
    }

    private void mostrarDialogoRegistro() {
        JDialog dialogoRegistro = new JDialog(this, "Crear Cuenta Nueva", true);
        dialogoRegistro.setLayout(new BorderLayout());
        
        JPanel panelFondo = new JPanel(new GridBagLayout());
        panelFondo.setBackground(COLOR_FONDO);
        panelFondo.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(COLOR_BLANCO);
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(35, 45, 35, 45)
        ));
        
        // Logo pequeño
        JLabel lblLogo = new JLabel("⚡");
        lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        lblLogo.setForeground(TECH_CYAN);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblLogo);
        
        panelCentral.add(Box.createVerticalStrut(10));
        
        JLabel lblTitulo = new JLabel("Registrar Usuario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(TECH_DARK);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblTitulo);
        
        panelCentral.add(Box.createVerticalStrut(30));
        
        JTextField txtNombre = agregarCampo(panelCentral, "Nombre completo");
        JTextField txtUsuarioReg = agregarCampo(panelCentral, "Usuario");
        JPasswordField txtContrasenaReg = (JPasswordField) agregarCampo(panelCentral, "Contraseña", true);
        JPasswordField txtConfirmar = (JPasswordField) agregarCampo(panelCentral, "Confirmar contraseña", true);
        JTextField txtPresupuesto = agregarCampo(panelCentral, "Presupuesto inicial");
        
        panelCentral.add(Box.createVerticalStrut(25));
        
        JButton btnGuardar = crearBotonPrimario("Registrar");
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String usuario = txtUsuarioReg.getText().trim();
            String contrasena = new String(txtContrasenaReg.getPassword());
            String confirmar = new String(txtConfirmar.getPassword());
            String presupuestoStr = txtPresupuesto.getText().trim();

            if (nombre.isEmpty() || usuario.isEmpty() || contrasena.isEmpty() || presupuestoStr.isEmpty()) {
                mostrarMensaje(dialogoRegistro, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!contrasena.equals(confirmar)) {
                mostrarMensaje(dialogoRegistro, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double presupuesto;
            try {
                presupuesto = Double.parseDouble(presupuestoStr);
                if (presupuesto < 0) {
                    mostrarMensaje(dialogoRegistro, "El presupuesto no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                mostrarMensaje(dialogoRegistro, "Presupuesto inválido. Ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (servicioLogin.registrarUsuario(nombre, usuario, contrasena, presupuesto)) {
                mostrarMensaje(dialogoRegistro, "Usuario registrado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialogoRegistro.dispose();
            } else {
                mostrarMensaje(dialogoRegistro, "Error al registrar usuario. El usuario ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelCentral.add(btnGuardar);
        
        panelCentral.add(Box.createVerticalStrut(12));
        
        JButton btnCancelar = crearBotonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dialogoRegistro.dispose());
        panelCentral.add(btnCancelar);
        
        panelFondo.add(panelCentral);
        dialogoRegistro.add(panelFondo);
        dialogoRegistro.pack();
        dialogoRegistro.setLocationRelativeTo(this);
        dialogoRegistro.setVisible(true);
    }
    
    /**
     * Muestra el diálogo de recuperación de contraseña
     */
    private void mostrarDialogoRecuperacion() {
        JDialog dialogoRecuperacion = new JDialog(this, "Recuperar Contraseña", true);
        dialogoRecuperacion.setLayout(new BorderLayout());
        
        JPanel panelFondo = new JPanel(new GridBagLayout());
        panelFondo.setBackground(COLOR_FONDO);
        panelFondo.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(COLOR_BLANCO);
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(35, 45, 35, 45)
        ));
        
        // Icono
        JLabel lblIcono = new JLabel("🔐");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblIcono);
        
        panelCentral.add(Box.createVerticalStrut(10));
        
        JLabel lblTitulo = new JLabel("Recuperar Contraseña");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(TECH_DARK);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblTitulo);
        
        panelCentral.add(Box.createVerticalStrut(10));
        
        JLabel lblInstruccion = new JLabel("<html><center>Ingresa tu nombre de usuario para<br>ver tu pregunta de seguridad</center></html>");
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInstruccion.setForeground(new Color(100, 100, 100));
        lblInstruccion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblInstruccion);
        
        panelCentral.add(Box.createVerticalStrut(25));
        
        // Campo usuario
        JTextField txtUsuarioRec = agregarCampo(panelCentral, "Nombre de usuario");
        
        // Panel para pregunta de seguridad (oculto inicialmente)
        JPanel panelPregunta = new JPanel();
        panelPregunta.setLayout(new BoxLayout(panelPregunta, BoxLayout.Y_AXIS));
        panelPregunta.setBackground(COLOR_BLANCO);
        panelPregunta.setVisible(false);
        
        JLabel lblPregunta = new JLabel("");
        lblPregunta.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPregunta.setForeground(TECH_BLUE);
        lblPregunta.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPregunta.add(lblPregunta);
        
        panelPregunta.add(Box.createVerticalStrut(8));
        
        JTextField txtRespuesta = new JTextField(20);
        txtRespuesta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtRespuesta.setMaximumSize(new Dimension(320, 40));
        txtRespuesta.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(TECH_BLUE_LIGHT, 1, 8),
            new EmptyBorder(8, 15, 8, 15)
        ));
        txtRespuesta.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPregunta.add(txtRespuesta);
        
        panelPregunta.add(Box.createVerticalStrut(18));
        
        // Panel para nueva contraseña (oculto inicialmente)
        JPanel panelNuevaContrasena = new JPanel();
        panelNuevaContrasena.setLayout(new BoxLayout(panelNuevaContrasena, BoxLayout.Y_AXIS));
        panelNuevaContrasena.setBackground(COLOR_BLANCO);
        panelNuevaContrasena.setVisible(false);
        
        JLabel lblNuevaPass = new JLabel("Nueva contraseña");
        lblNuevaPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNuevaPass.setForeground(COLOR_TEXTO);
        lblNuevaPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNuevaContrasena.add(lblNuevaPass);
        
        panelNuevaContrasena.add(Box.createVerticalStrut(8));
        
        JPasswordField txtNuevaContrasena = new JPasswordField(20);
        txtNuevaContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNuevaContrasena.setMaximumSize(new Dimension(320, 40));
        txtNuevaContrasena.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(TECH_BLUE_LIGHT, 1, 8),
            new EmptyBorder(8, 15, 8, 15)
        ));
        txtNuevaContrasena.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNuevaContrasena.add(txtNuevaContrasena);
        
        panelNuevaContrasena.add(Box.createVerticalStrut(15));
        
        JLabel lblConfirmarPass = new JLabel("Confirmar contraseña");
        lblConfirmarPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblConfirmarPass.setForeground(COLOR_TEXTO);
        lblConfirmarPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNuevaContrasena.add(lblConfirmarPass);
        
        panelNuevaContrasena.add(Box.createVerticalStrut(8));
        
        JPasswordField txtConfirmarContrasena = new JPasswordField(20);
        txtConfirmarContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConfirmarContrasena.setMaximumSize(new Dimension(320, 40));
        txtConfirmarContrasena.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(TECH_BLUE_LIGHT, 1, 8),
            new EmptyBorder(8, 15, 8, 15)
        ));
        txtConfirmarContrasena.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNuevaContrasena.add(txtConfirmarContrasena);
        
        panelCentral.add(panelPregunta);
        panelCentral.add(panelNuevaContrasena);
        
        panelCentral.add(Box.createVerticalStrut(20));
        
        // Estado del proceso
        final int[] paso = {1}; // 1: buscar usuario, 2: responder pregunta, 3: nueva contraseña
        final Usuario[] usuarioRecuperar = {null};
        
        JButton btnAccion = crearBotonPrimario("Buscar Usuario");
        btnAccion.addActionListener(e -> {
            switch (paso[0]) {
                case 1: // Buscar usuario
                    String nombreUsuario = txtUsuarioRec.getText().trim();
                    if (nombreUsuario.isEmpty()) {
                        mostrarMensaje(dialogoRecuperacion, "Ingrese su nombre de usuario", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    usuarioRecuperar[0] = usuarioDAO.obtenerPorNombreUsuario(nombreUsuario);
                    if (usuarioRecuperar[0] == null) {
                        mostrarMensaje(dialogoRecuperacion, "Usuario no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    String pregunta = usuarioDAO.obtenerPreguntaSeguridad(nombreUsuario);
                    if (pregunta == null || pregunta.isEmpty()) {
                        mostrarMensaje(dialogoRecuperacion, 
                            "Este usuario no tiene configurada una pregunta de seguridad.\nContacte al administrador.", 
                            "Sin recuperación", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Mostrar pregunta
                    lblPregunta.setText("Pregunta: " + pregunta);
                    panelPregunta.setVisible(true);
                    txtUsuarioRec.setEditable(false);
                    btnAccion.setText("Verificar Respuesta");
                    paso[0] = 2;
                    dialogoRecuperacion.pack();
                    break;
                    
                case 2: // Verificar respuesta
                    String respuesta = txtRespuesta.getText().trim();
                    if (respuesta.isEmpty()) {
                        mostrarMensaje(dialogoRecuperacion, "Ingrese su respuesta", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (usuarioDAO.verificarRespuestaSeguridad(usuarioRecuperar[0].getUsuario(), respuesta)) {
                        // Respuesta correcta, mostrar campos de nueva contraseña
                        panelNuevaContrasena.setVisible(true);
                        txtRespuesta.setEditable(false);
                        btnAccion.setText("Cambiar Contraseña");
                        paso[0] = 3;
                        dialogoRecuperacion.pack();
                    } else {
                        mostrarMensaje(dialogoRecuperacion, "Respuesta incorrecta", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                    
                case 3: // Cambiar contraseña
                    String nuevaPass = new String(txtNuevaContrasena.getPassword());
                    String confirmarPass = new String(txtConfirmarContrasena.getPassword());
                    
                    if (nuevaPass.isEmpty() || confirmarPass.isEmpty()) {
                        mostrarMensaje(dialogoRecuperacion, "Complete ambos campos de contraseña", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (!nuevaPass.equals(confirmarPass)) {
                        mostrarMensaje(dialogoRecuperacion, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (nuevaPass.length() < 4) {
                        mostrarMensaje(dialogoRecuperacion, "La contraseña debe tener al menos 4 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (usuarioDAO.actualizarContrasena(usuarioRecuperar[0].getCodigo(), nuevaPass)) {
                        mostrarMensaje(dialogoRecuperacion, "¡Contraseña actualizada exitosamente!\nYa puede iniciar sesión con su nueva contraseña.", 
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        dialogoRecuperacion.dispose();
                    } else {
                        mostrarMensaje(dialogoRecuperacion, "Error al actualizar la contraseña", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
            }
        });
        panelCentral.add(btnAccion);
        
        panelCentral.add(Box.createVerticalStrut(12));
        
        JButton btnCancelar = crearBotonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dialogoRecuperacion.dispose());
        panelCentral.add(btnCancelar);
        
        panelFondo.add(panelCentral);
        dialogoRecuperacion.add(panelFondo);
        dialogoRecuperacion.pack();
        dialogoRecuperacion.setLocationRelativeTo(this);
        dialogoRecuperacion.setVisible(true);
    }

    private JTextField agregarCampo(JPanel panel, String etiqueta) {
        return agregarCampo(panel, etiqueta, false);
    }

    private JTextField agregarCampo(JPanel panel, String etiqueta, boolean esPassword) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COLOR_TEXTO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
        
        panel.add(Box.createVerticalStrut(8));
        
        JTextField campo = esPassword ? new JPasswordField(20) : new JTextField(20);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setMaximumSize(new Dimension(320, 40));
        campo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(TECH_BLUE_LIGHT, 1, 8),
            new EmptyBorder(8, 15, 8, 15)
        ));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(campo);
        
        panel.add(Box.createVerticalStrut(18));
        
        return campo;
    }

    private void mostrarMensaje(Component parent, String mensaje, String titulo, int tipo) {
        UIManager.put("OptionPane.background", COLOR_BLANCO);
        UIManager.put("Panel.background", COLOR_BLANCO);
        JOptionPane.showMessageDialog(parent, mensaje, titulo, tipo);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginVentana ventana = new LoginVentana();
            ventana.setVisible(true);
        });
    }
}
