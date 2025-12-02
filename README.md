# TechStore - Sistema de Ventas de Electrónica

## Descripción General

TechStore es un sistema de gestión de ventas desarrollado en Java con interfaz gráfica Swing, diseñado específicamente para la comercialización de productos electrónicos. El sistema implementa una arquitectura en capas (presentación, servicios, datos y modelo) que garantiza la separación de responsabilidades y facilita el mantenimiento del código.

## Características Principales

### Gestión de Usuarios

- **Autenticación**: Sistema de login con validación de credenciales mediante base de datos MySQL.
- **Roles de Usuario**: Tres tipos de usuarios con diferentes niveles de acceso:
  - **Administrador**: Acceso completo al sistema, incluyendo gestión de productos.
  - **Usuario Registrado**: Puede realizar compras, gestionar carrito y consultar historial.
  - **Invitado**: Acceso de solo lectura al catálogo de productos.
- **Registro de Usuarios**: Funcionalidad para crear nuevas cuentas con asignación de presupuesto inicial.
- **Control de Presupuesto**: Cada usuario registrado tiene un presupuesto asignado que se actualiza con cada compra.

### Catálogo de Productos

- Visualización completa del inventario de productos electrónicos.
- Información detallada: código, descripción, stock disponible y precio.
- Categorías de productos incluidas:
  - Laptops y Computadoras
  - Smartphones y Tablets
  - Periféricos y Accesorios
  - Componentes de PC
  - Almacenamiento y Respaldo
  - Audio y Video
  - Redes y Conectividad
  - Energía y Protección
  - Gaming y Entretenimiento
  - Dispositivos Inteligentes
- Actualización en tiempo real del stock disponible.
- Interfaz de búsqueda y navegación intuitiva.

### Carrito de Compras

- Agregar productos al carrito con cantidad personalizada.
- Visualización del carrito con:
  - Nombre del producto
  - Precio unitario
  - Cantidad seleccionada
  - Subtotal por producto
  - Total general de la compra
- Modificación del carrito:
  - Eliminar productos individuales
  - Vaciar carrito completo
- Validación de stock antes de agregar productos.
- Cálculo automático de totales.

### Proceso de Compra

- Validación de presupuesto disponible antes de finalizar la compra.
- Actualización automática de:
  - Stock de productos
  - Presupuesto del usuario
  - Registro de transacciones
- Generación de número único de compra.
- Confirmación de compra con resumen detallado.
- Control de transacciones mediante base de datos.

### Historial de Compras

- Visualización de todas las compras realizadas por el usuario.
- Información mostrada:
  - Número de compra
  - Fecha de transacción
  - Monto total
- Detalle de compra expandible que incluye:
  - Lista completa de productos adquiridos
  - Cantidad de cada producto
  - Precio unitario
  - Subtotales
  - Total de la compra

### Panel de Administración

Exclusivo para usuarios con rol de administrador:

- **Gestión Completa de Productos**:
  - Crear nuevos productos
  - Editar productos existentes (descripción, stock, precio)
  - Eliminar productos del catálogo
  - Visualización de todo el inventario
- **Control de Inventario**:
  - Monitoreo de stock disponible
  - Actualización manual de cantidades
  - Gestión de precios

## Arquitectura del Sistema

### Estructura de Capas

1. **Capa de Presentación** (`presentacion/`):
   - `LoginVentana.java`: Interfaz de autenticación y registro
   - `PrincipalPestaña.java`: Ventana principal con sistema de pestañas

2. **Capa de Servicios** (`servicios/`):
   - `ServicioLogin.java`: Lógica de autenticación y registro
   - `ServicioProducto.java`: Gestión de productos
   - `ServicioCompra.java`: Procesamiento de compras y carrito
   - `ServicioAdmin.java`: Operaciones administrativas

3. **Capa de Acceso a Datos** (`datos/`):
   - `ConexionMySQL.java`: Gestión de conexiones (patrón Singleton)
   - `UsuarioDAO.java`: Operaciones CRUD de usuarios
   - `ProductoDAO.java`: Operaciones CRUD de productos
   - `CompraDAO.java`: Gestión de compras
   - `DetalleCompraDAO.java`: Gestión de detalles de compra

4. **Capa de Modelo** (`modelo/`):
   - `Usuario.java`: Entidad de usuario
   - `Producto.java`: Entidad de producto
   - `Compra.java`: Entidad de compra
   - `DetalleCompra.java`: Entidad de detalle de compra

### Base de Datos

**Sistema Gestor**: MySQL 8.2.0

**Tablas Principales**:

- `Usuario`: Almacena información de usuarios y presupuestos
- `Producto`: Catálogo completo de productos electrónicos
- `Compra`: Registro de transacciones realizadas
- `DetalleCompra`: Productos específicos de cada compra

**Características**:
- Integridad referencial mediante claves foráneas
- Transacciones ACID para garantizar consistencia
- Índices para optimizar consultas
- Relaciones uno a muchos entre entidades

## Tecnologías Utilizadas

- **Lenguaje**: Java 8 o superior
- **Interfaz Gráfica**: Java Swing
- **Base de Datos**: MySQL 8.2.0
- **Conector**: MySQL Connector/J 8.2.0
- **Arquitectura**: MVC (Modelo-Vista-Controlador)
- **Patrones de Diseño**:
  - Singleton (gestión de conexiones)
  - DAO (Data Access Object)
  - Separación en capas

## Diseño de Interfaz

### Paleta de Colores Moderna

El sistema implementa un tema visual tecnológico y minimalista:

- **TECH_DARK** (RGB: 15, 23, 42): Headers y elementos oscuros
- **TECH_BLUE** (RGB: 59, 130, 246): Botones primarios
- **TECH_BLUE_LIGHT** (RGB: 96, 165, 250): Elementos de selección
- **TECH_CYAN** (RGB: 34, 211, 238): Acentos y logo
- **TECH_GREEN** (RGB: 34, 197, 94): Precios y confirmaciones
- **TECH_ORANGE** (RGB: 251, 146, 60): Advertencias
- **TECH_RED** (RGB: 239, 68, 68): Errores y eliminaciones

### Características de Diseño

- Interfaz con pestañas para navegación intuitiva
- Tablas con filas alternadas para mejor legibilidad
- Botones con iconografía descriptiva
- Retroalimentación visual en selecciones
- Sombras y bordes redondeados para apariencia moderna
- Tipografía Segoe UI en diferentes pesos

## Requisitos del Sistema

### Software Necesario

- Java Development Kit (JDK) 8 o superior
- MySQL Server 8.0 o superior
- MySQL Connector/J 8.2.0

### Configuración de Base de Datos

1. Crear base de datos `sistemaventas`
2. Ejecutar script de estructura de tablas
3. Ejecutar script `productos_electronica.sql` para datos iniciales
4. Configurar credenciales en `ConexionMySQL.java`

### Compilación y Ejecución

**Compilar**:
```
compilar.bat
```

**Ejecutar**:
```
ejecutar.bat
```

## Credenciales Predeterminadas

- **Administrador**:
  - Usuario: `admin`
  - Contraseña: `admin123`

- **Invitado**:
  - Usuario: `invitado`
  - Contraseña: `invitado`

## Validaciones Implementadas

### Seguridad

- Validación de credenciales en base de datos
- Protección contra inyección SQL mediante PreparedStatements
- Separación de roles y permisos

### Integridad de Datos

- Validación de stock antes de agregar al carrito
- Verificación de presupuesto disponible antes de compra
- Control de cantidades positivas
- Validación de campos obligatorios
- Verificación de duplicados de usuario

### Transacciones

- Uso de transacciones para operaciones complejas
- Rollback automático en caso de error
- Actualización atómica de múltiples tablas
- Gestión adecuada de recursos (conexiones, statements, resultsets)

## Manejo de Errores

- Mensajes descriptivos para el usuario
- Logging de errores en consola para debugging
- Recuperación elegante de fallos de conexión
- Validación de entrada de usuario
- Manejo de excepciones SQL

## Futuras Implementaciones

### Mejoras de Funcionalidad

1. **Sistema de Reportes**:
   - Generación de reportes de ventas en PDF
   - Estadísticas de productos más vendidos
   - Análisis de tendencias de compra
   - Reportes de inventario bajo

2. **Gestión Avanzada de Inventario**:
   - Alertas automáticas de stock bajo
   - Sistema de reabastecimiento
   - Historial de cambios en inventario
   - Códigos de barras para productos

3. **Mejoras en Carrito y Compras**:
   - Guardar carrito entre sesiones
   - Lista de deseos
   - Sistema de descuentos y promociones
   - Cupones de descuento
   - Compras recurrentes

4. **Gestión de Usuarios Avanzada**:
   - Recuperación de contraseña
   - Niveles de usuario adicionales (supervisor, vendedor)
   - Historial de inicio de sesión
   - Perfil de usuario personalizable

### Nuevas Características

5. **Sistema de Búsqueda y Filtros**:
   - Búsqueda por nombre, categoría o rango de precio
   - Filtros avanzados en catálogo
   - Ordenamiento por diferentes criterios
   - Búsqueda predictiva

6. **Facturación y Comprobantes**:
   - Generación de facturas electrónicas
   - Impresión de comprobantes
   - Envío de comprobantes por correo
   - Numeración fiscal

7. **Sistema de Calificaciones y Reseñas**:
   - Valoración de productos por usuarios
   - Comentarios y reseñas
   - Sistema de recomendaciones basado en historial

8. **Notificaciones**:
   - Alertas de nuevos productos
   - Notificaciones de cambios de precio
   - Recordatorios de productos en lista de deseos

### Mejoras Técnicas


9. **Interfaz de Usuario**:
    - Modo oscuro/claro alternativo
    - Personalización de temas
    - Responsive design para diferentes resoluciones
    - Accesibilidad mejorada

10. **Integración y Exportación**:
    - Exportación de datos a Excel/CSV
    - Importación masiva de productos
    - API para integraciones externas
    - Sincronización con sistemas contables

11. **Funcionalidades Web/Móvil**:
    - Versión web del sistema
    - Aplicación móvil complementaria
    - Sincronización multiplataforma
    - Compras en línea

12. **Análisis de Datos**:
    - Dashboard con métricas clave
    - Gráficos de ventas
    - Análisis de comportamiento de usuario
    - Predicción de demanda

13. **Gestión de Proveedores**:
    - Registro de proveedores
    - Órdenes de compra
    - Control de costos
    - Historial de proveedores

## Licencia

Este proyecto es desarrollado con fines educativos para la materia Programación II.


**Versión**: 1.0.0  
**Última Actualización**: Noviembre 2025  
**Estado**: Producción
**Codigos de Descuento**: TECH20, BIENVENIDO10, DESCUENTO5000
