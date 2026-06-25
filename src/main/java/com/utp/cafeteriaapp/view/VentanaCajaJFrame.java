package com.utp.cafeteriaapp.view;

import com.utp.cafeteriaapp.model.Pedido;
import com.utp.cafeteriaapp.model.Producto;
import com.utp.cafeteriaapp.service.RestauranteFacade;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class VentanaCajaJFrame extends JFrame {

    private RestauranteFacade fachada;
    private Pedido pedidoActual;
    private List<Producto> baseDatosProductos;
    private String categoriaActiva;

    private JTable tablaPedido;
    private DefaultTableModel modeloTabla;
    private JLabel lblSubtotalValor;
    private JLabel lblIgvValor;
    private JLabel lblTotal;
    private JPanel panelGridProductos;
    private JTextField txtBuscar;

    public VentanaCajaJFrame(RestauranteFacade fachada) {
        this.fachada = fachada;
        this.pedidoActual = fachada.crearNuevoPedido();
        this.categoriaActiva = "CAFÉS";
        inicializarBaseDatosMock();
        configurarVentana();
        inicializarComponentes();
        filtrarYBuscarProductos();
    }

    private void configurarVentana() {
        setTitle("CAFETERÍA - PUNTO DE VENTA");
        setSize(1024, 728);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(0xF4, 0xF1, 0xEA));
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        Border bordeGuia = BorderFactory.createLineBorder(Color.red);

        // ########## BARRA SUPERIOR ##########
        JPanel panelHeader = new JPanel(new BorderLayout()); // BorderLayout usa distribución de bordes (Norte, Sur, Este, Oeste)
        panelHeader.setBackground(new Color(0x4A, 0x35, 0x25));
        panelHeader.setPreferredSize(new Dimension(1024, 60));
        panelHeader.setBorder(new EmptyBorder(0, 10, 0, 20)); // Solo márgenes internos laterales

        // Contenedor del botón menú
        JPanel panelMenu = new JPanel(new GridBagLayout()); // GridBagLayout para centrar en ambos ejes
        panelMenu.setOpaque(false); // Quitar color de fondo

        // Botón de menú - izquierda
        JButton btnMenu = new JButton("☰");
        btnMenu.setFont(new Font("SansSerif", Font.BOLD, 22)); // Tipografía, estilo y tamaño
        btnMenu.setForeground(Color.WHITE); // Color del texto
        btnMenu.setContentAreaFilled(false); // Fondo transparende
        btnMenu.setBorderPainted(false); // Sin bordes
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMenu.addActionListener(e -> JOptionPane.showMessageDialog(this, "Módulo de administración en desarrollo.", "Historial", JOptionPane.INFORMATION_MESSAGE));
        // Efecto hover
        btnMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnMenu.setContentAreaFilled(true);
                btnMenu.setForeground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnMenu.setContentAreaFilled(false);
                btnMenu.setForeground(Color.WHITE);
            }
        });
        panelMenu.add(btnMenu);
        panelHeader.add(panelMenu, BorderLayout.WEST);

        // Título del sistema - centro
        JLabel lblTitulo = new JLabel("CAFETERÍA", JLabel.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        panelHeader.add(lblTitulo, BorderLayout.CENTER);

        // Botones de utilidades - derecha
        JPanel panelUtilidades = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18)); // Alineacion en x e y
        panelUtilidades.setOpaque(false); // Fondo transparente

        // Instancia el botón de arqueo usando un emoticón de taza de café caliente
        JButton btnCaja = new JButton("☕");
        // Configura el puntero del mouse como mano para indicar que es un elemento cliqueable
        btnCaja.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Añade el escuchador para reaccionar al movimiento dinámico del cursor encima del componente
        btnCaja.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Cambia el texto corto por una frase explicativa al posicionar el cursor encima
                btnCaja.setText("Reporte de hoy");
                // Modifica el color del texto a gris para dar una respuesta visual interactiva
                btnCaja.setForeground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Regresa el texto al icono por defecto cuando el mouse se retira
                btnCaja.setText("☕");
                // Restablece el color del texto a negro puro (Variante: podrías usar Color.WHITE si el fondo es oscuro)
                btnCaja.setForeground(Color.BLACK);
            }
        });

        // Instancia el botón para ver inventarios generales usando el emoticón de la tabla portapapeles
        JButton btnReporte = new JButton("📋");
        // Modifica el puntero básico del sistema operativo por la mano interactiva de selección
        btnReporte.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Agrega las respuestas automáticas para los eventos de entrada y salida del mouse
        btnReporte.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Muestra un texto largo descriptivo que reemplaza temporalmente al dibujo
                btnReporte.setText("Reporte general");
                // Pinta las letras de color gris (Variante: usar un tono personalizado como new Color(0x99,0x99,0x99))
                btnReporte.setForeground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Devuelve el carácter original del portapapeles a la etiqueta del botón
                btnReporte.setText("📋");
                // Retorna el color base del elemento a negro absoluto
                btnReporte.setForeground(Color.BLACK);
            }
        });

        // Instancia el botón de asistencia técnica usando un signo de interrogación blanco
        JButton btnInfo = new JButton("❓");
        // Cambia la flecha común del mouse por la mano de enlace digital activa
        btnInfo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Inyecta un listener directo para abrir un cuadro de diálogo informativo al hacer clic
        btnInfo.addActionListener(e -> JOptionPane.showMessageDialog(this, "App Cafetería POS\nDesarrollado por: Rodrigo Sihues Yanqui", "Información", JOptionPane.INFORMATION_MESSAGE));
        // Agrega el escuchador de posición para controlar los cambios de texto reactivos
        btnInfo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Al poner el mouse encima despliega la palabra completa "Información"
                btnInfo.setText("Información");
                // Opaca el color a un gris tenue
                btnInfo.setForeground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Regresa el signo de interrogación al salir de los límites rectangulares del elemento
                btnInfo.setText("❓");
                // Reestablece el tono negro de origen
                btnInfo.setForeground(Color.BLACK);
            }
        });

        // Agrega de forma consecutiva el botón de caja al panel de utilidades derecho
        panelUtilidades.add(btnCaja);
        // Incorpora el botón de reportes generales a la derecha del anterior elemento
        panelUtilidades.add(btnReporte);
        // Suma el botón de ayuda técnica cerrando el bloque horizontal de control administrativo
        panelUtilidades.add(btnInfo);
        // Monta el grupo de utilidades completo en el extremo derecho del encabezado
        panelHeader.add(panelUtilidades, BorderLayout.EAST);
        // Fija la barra superior terminada en el límite norte de la ventana de la aplicación
        add(panelHeader, BorderLayout.NORTH);

        // -----------------------------------------------------------------------------------------------------
        // SUB-BLOQUE 2: PANEL DE CONTROL GLOBAL (ZONA OPERATIVA DE DOBLE ENTRADA HORIZONTAL)
        // -----------------------------------------------------------------------------------------------------
        // Inicializa el lienzo intermedio que recibirá los bloques de contenido usando un BorderLayout base
        JPanel panelContenedorGlobal = new JPanel(new BorderLayout(0, 0));
        // Aplica un tono crema claro uniforme a todo el fondo del contenedor operativo principal
        panelContenedorGlobal.setBackground(new Color(0xF4, 0xF1, 0xEA));

        // ############################################ BLOCK 1: CUERPO CENTRAL ############################################
        // Instancia un subpanel que usará un GridBagLayout para estructurar columnas proporcionales y elásticas
        JPanel panelCuerpo = new JPanel(new GridBagLayout());
        // Aplica el mismo fondo crema para que no se noten cortes visuales de color en el fondo
        panelCuerpo.setBackground(new Color(0xF4, 0xF1, 0xEA));
        // Genera márgenes de separación perimetral (Superior: 15px, Izquierdo: 15px, Inferior: 10px, Derecho: 15px)
        panelCuerpo.setBorder(new EmptyBorder(15, 15, 10, 15));

        // -----------------------------------------------------------------------------------------------------
        // SUB-BLOQUE 2.1: COLUMNA IZQUIERDA - CATÁLOGO DE PRODUCTOS (58% DE ANCHO GENERAL)
        // -----------------------------------------------------------------------------------------------------
        // Panel vertical que estructurará el catálogo usando separación de elementos de 12px en el eje vertical
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 12));
        // Copia el color de fondo crema de la aplicación para unificar la sección
        panelIzquierdo.setBackground(new Color(0xF4, 0xF1, 0xEA));

        // Subcontenedor superior que apilará verticalmente la barra de búsqueda y las pestañas con un espacio de 8px
        JPanel panelControlesIzquierda = new JPanel(new BorderLayout(0, 8));
        // Hace transparente este panel auxiliar para que herede el color crema base del elemento de abajo
        panelControlesIzquierda.setOpaque(false);

        // Instancia el campo de ingreso de texto interactivo para la búsqueda de ítems
        txtBuscar = new JTextField();
        // Propiedad avanzada de FlatLaf que inyecta un texto de ayuda gris transparente dentro del input vacío
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar producto por nombre...");
        // Añade un escuchador al documento interno para capturar cambios en el texto al instante sin usar botones
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarYBuscarProductos();
            } // Se ejecuta al escribir una letra nueva

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarYBuscarProductos();
            } // Se ejecuta al borrar un carácter con el teclado

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarYBuscarProductos();
            } // Se ejecuta si cambia el estilo o formato del texto interno
        });
        // Ubica la barra de búsqueda en el extremo superior (Norte) del subcontenedor de controles
        panelControlesIzquierda.add(txtBuscar, BorderLayout.NORTH);

        // Crea una fila horizontal alineada al centro con espacios de 5px entre las pestañas del menú
        JPanel panelCategorias = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        // Desactiva la capa opaca grisácea nativa para mantener limpio el diseño crema de fondo
        panelCategorias.setOpaque(false);
        // Define un arreglo de cadenas de texto con los nombres de las agrupaciones comerciales de la cafetería
        String[] cats = {"CAFÉS", "BEBIDAS FRÍAS", "POSTRES", "SÁNDWICHES"};
        // Bucle estructurado For-Each que recorre las categorías para construir un botón por cada una de ellas
        for (String cat : cats) {
            // Crea un botón asignándole el nombre del elemento actual del bucle
            JButton btnCat = new JButton(cat);
            // Pinta la caja del botón con un tono marrón oscuro consistente con la paleta de la marca
            btnCat.setBackground(new Color(0x4A, 0x35, 0x25));
            // Cambia el color del texto de la categoría a blanco puro para asegurar una lectura correcta
            btnCat.setForeground(Color.WHITE);
            // Activa la mano interactiva del cursor para indicar un elemento cliqueable al operario
            btnCat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Agrega el evento click que actualiza el estado de la categoría activa y refresca el grid
            btnCat.addActionListener(e -> {
                categoriaActiva = cat; // Modifica la variable global por la pestaña seleccionada actualmente
                filtrarYBuscarProductos(); // Redibuja de inmediato la rejilla de tarjetas en pantalla
            });
            // Añade listeners para controlar las transiciones de color dinámicas del mouse en cada categoría
            btnCat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    // Invierte los colores al entrar: Caja blanca con texto marrón oscuro (Efecto Invertido)
                    btnCat.setBackground(Color.WHITE);
                    btnCat.setForeground(new Color(0x4A, 0x35, 0x25));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // Restablece los colores originales al salir: Caja marrón con letras blancas
                    btnCat.setBackground(new Color(0x4A, 0x35, 0x25));
                    btnCat.setForeground(Color.WHITE);
                }
            });
            // Incorpora el botón terminado a la fila horizontal de categorías
            panelCategorias.add(btnCat);
        }
        // Coloca la barra horizontal completa de pestañas en la base (Sur) del contenedor de controles
        panelControlesIzquierda.add(panelCategorias, BorderLayout.SOUTH);
        // Fija el conjunto unificado de búsqueda y pestañas en la parte superior de la columna izquierda
        panelIzquierdo.add(panelControlesIzquierda, BorderLayout.NORTH);

        // Crea el panel blanco que servirá de fondo limpio para aislar las tarjetas de productos
        JPanel panelLienzoProductos = new JPanel(new BorderLayout());
        // Aplica color blanco puro al fondo de este lienzo contenedor principal
        panelLienzoProductos.setBackground(Color.WHITE);
        // Genera un borde compuesto: Contorno gris redondeado sutil de 1px + Acolchado interno de 16px en las 4 esquinas
        panelLienzoProductos.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xD2, 0xCF, 0xC7), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Instancia la rejilla dinámica de dos columnas con espacios de separación de 16px en ambos ejes
        panelGridProductos = new JPanel(new GridLayout(0, 2, 16, 16));
        // Fija el fondo en blanco puro para heredar uniformidad a las tarjetas que reciba
        panelGridProductos.setBackground(Color.WHITE);

        // Panel auxiliar de anclaje que bloquea el crecimiento vertical de las tarjetas si hay pocos productos
        JPanel panelAnclaNorte = new JPanel(new BorderLayout());
        // Aplica color blanco puro al fondo del panel de anclaje técnico
        panelAnclaNorte.setBackground(Color.WHITE);
        // Introduce el grid de productos alineado rígidamente al norte para empujar el espacio sobrante hacia abajo
        panelAnclaNorte.add(panelGridProductos, BorderLayout.NORTH);

        // Envuelve el panel de anclaje dentro de un componente de desplazamiento con barras automáticas
        JScrollPane scrollProductos = new JScrollPane(panelAnclaNorte);
        // Elimina el borde gris nativo del JScrollPane para evitar dobles líneas toscas en el diseño
        scrollProductos.setBorder(BorderFactory.createEmptyBorder());
        // Asegura que el área de fondo visible del scroll mantenga el color blanco puro unificado
        scrollProductos.getViewport().setBackground(Color.WHITE);

        // Agrega el scroll central terminado directamente en el corazón del lienzo blanco
        panelLienzoProductos.add(scrollProductos, BorderLayout.CENTER);
        // Monta el lienzo completo en el centro operativo de la sección de catálogo
        panelIzquierdo.add(panelLienzoProductos, BorderLayout.CENTER);

        // Configura las reglas estructurales de la columna izquierda para la cuadrícula maestra del cuerpo
        GridBagConstraints gbc = new GridBagConstraints();
        // Fuerza al componente a estirarse en ambos sentidos para cubrir toda su celda asignada
        gbc.fill = GridBagConstraints.BOTH;
        // Asigna la coordenada X inicial (Fila 0, Columna 0)
        gbc.gridx = 0;
        // Asigna la coordenada Y inicial
        gbc.gridy = 0;
        // Configura el peso proporcional del ancho asignándole un 58% estricto del espacio libre horizontal
        gbc.weightx = 0.58;
        // Permite que la sección absorba el 100% del crecimiento vertical disponible de la ventana
        gbc.weighty = 1.0;
        // Genera un canal de aire o brecha de 10px a la derecha para separarse de la columna de facturación
        gbc.insets = new Insets(0, 0, 0, 10);
        // Inyecta la columna izquierda de catálogo en el panel maestro del cuerpo central
        panelCuerpo.add(panelIzquierdo, gbc);

        // -----------------------------------------------------------------------------------------------------
        // SUB-BLOQUE 2.2: COLUMNA DERECHA - DETALLES DEL PEDIDO (42% DE ANCHO GENERAL)
        // -----------------------------------------------------------------------------------------------------
        // Panel vertical continuo que contendrá el resumen de compras y desglose de comprobante
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 0));
        // Fija el color de fondo en blanco puro para estructurar la boleta limpia
        panelDerecho.setBackground(Color.WHITE);
        // Construye un contorno lineal gris plano de 1px a su alrededor para delimitar la zona de cobro
        panelDerecho.setBorder(BorderFactory.createLineBorder(new Color(0xDB, 0xD9, 0xD3), 1));

        // Crea una matriz de textos con los nombres que encabezarán las columnas de la tabla de ventas
        String[] columnas = {"Nombre", "Cantidad", "Precio"};
        // Inicializa el modelo lógico de la tabla anulando la edición por teclado en las celdas por seguridad
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            } // Celdas bloqueadas para edición directa
        };
        // Vincula el modelo lógico al componente visual JTable
        tablaPedido = new JTable(modeloTabla);
        // Configura el control operativo restringiendo la selección de la tabla a una sola fila a la vez
        tablaPedido.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Define una altura cómoda de 25px por fila para facilitar la lectura del operador
        tablaPedido.setRowHeight(25);

        // Envuelve la tabla dentro de un contenedor scrollable para manejar listas largas de compras de forma segura
        JScrollPane scrollProductosSeleccionados = new JScrollPane(tablaPedido);
        // Remueve el contorno gris tosco por defecto del scroll de la tabla
        scrollProductosSeleccionados.setBorder(BorderFactory.createEmptyBorder());
        // Fija el color de fondo interior del viewport del scroll en blanco puro
        scrollProductosSeleccionados.getViewport().setBackground(Color.WHITE);
        // Añade la tabla scrollable ocupando la zona central de la columna derecha de facturación
        panelDerecho.add(scrollProductosSeleccionados, BorderLayout.CENTER);

        // Panel vertical inferior que unificará la línea de separación y los cálculos matemáticos
        JPanel panelConsolidadoCobro = new JPanel(new BorderLayout());
        // Configura el color de fondo en blanco puro
        panelConsolidadoCobro.setBackground(Color.WHITE);

        // Instancia un componente separador horizontal gris para actuar como la línea de corte del ticket
        JSeparator lineaSeparadora = new JSeparator(JSeparator.HORIZONTAL);
        // Define un tono gris claro suave para pintar la línea divisoria
        lineaSeparadora.setForeground(new Color(0xDB, 0xD9, 0xD3));
        // Coloca la línea divisoria al inicio (Norte) del panel de consolidación financiera
        panelConsolidadoCobro.add(lineaSeparadora, BorderLayout.NORTH);

        // Rejilla interna de 3 filas y 2 columnas con separaciones de 5px para acomodar Subtotal, IGV y Total
        JPanel panelTextosBoleta = new JPanel(new GridLayout(3, 2, 5, 5));
        // Fija el fondo en blanco puro
        panelTextosBoleta.setBackground(Color.WHITE);
        // Inyecta un acolchado interno uniforme de 15px en los bordes para separar los textos del contorno
        panelTextosBoleta.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Inserta la etiqueta literal del Subtotal neto en la fila 1, columna 1
        panelTextosBoleta.add(new JLabel("Subtotal (S/.):"));
        // Inicializa el componente que mostrará el valor cambiante del subtotal alineado a la derecha
        lblSubtotalValor = new JLabel("0.00", JLabel.RIGHT);
        // Monta el valor cambiante en la fila 1, columna 2
        panelTextosBoleta.add(lblSubtotalValor);

        // Inserta la etiqueta literal del impuesto peruano IGV en la fila 2, columna 1
        panelTextosBoleta.add(new JLabel("IGV (18%):"));
        // Inicializa el componente que mostrará el valor dinámico del impuesto alineado a la derecha
        lblIgvValor = new JLabel("0.00", JLabel.RIGHT);
        // Monta el valor dinámico en la fila 2, columna 2
        panelTextosBoleta.add(lblIgvValor);

        // Inserta la etiqueta del Total General de la boleta en la fila 3, columna 1
        panelTextosBoleta.add(new JLabel("Total General:"));
        // Inicializa el componente maestro que desplegará el monto final a transaccionar
        lblTotal = new JLabel("S/. 0.00", JLabel.RIGHT);
        // Cambia la tipografía del total a SansSerif, Negrita y un tamaño resaltado de 18px para su fácil lectura
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 18));
        // Pinta el texto de un color gris oscuro casi negro para darle jerarquía tipográfica premium
        lblTotal.setForeground(new Color(0x21, 0x21, 0x21));
        // Monta el visor del total en la fila 3, columna 2 cerrando la rejilla de textos de la boleta
        panelTextosBoleta.add(lblTotal);

        // Agrega el bloque de textos completo al centro del panel de consolidación de cobros
        panelConsolidadoCobro.add(panelTextosBoleta, BorderLayout.CENTER);
        // Coloca el bloque financiero terminado en la base (Sur) de la columna derecha de facturación
        panelDerecho.add(panelConsolidadoCobro, BorderLayout.SOUTH);

        // Configura las reglas estructurales de la columna derecha para la rejilla maestra central
        gbc.gridx = 1; // Avanza a la coordenada X (Fila 0, Columna 1)
        gbc.gridy = 0; // Se mantiene en la fila inicial superior
        gbc.weightx = 0.42; // Configura el ancho proporcional asignándole un 42% estricto del espacio del sistema
        gbc.weighty = 1.0; // Sincroniza la altura para que crezca al mismo ritmo exacto que el catálogo izquierdo
        gbc.insets = new Insets(0, 10, 0, 0); // Genera la otra mitad de la brecha central (10px a la izquierda)
        // Inyecta la columna derecha de facturación en el panel de la rejilla central del sistema
        panelCuerpo.add(panelDerecho, gbc);
        // Agrega el cuerpo central completo terminado al centro del panel contenedor operativo principal
        panelContenedorGlobal.add(panelCuerpo, BorderLayout.CENTER);

        // ############################################ BLOCK 2: FOOTER DE BOTONES UNIFICADO ############################################
        // Instancia un subpanel inferior que usará GridBagLayout para sincronizar el ancho de los botones con las columnas superiores
        JPanel panelFooterSincronizado = new JPanel(new GridBagLayout());
        // Aplica el color crema base de fondo para mimetizarse con el entorno operativo exterior
        panelFooterSincronizado.setBackground(new Color(0xF4, 0xF1, 0xEA));
        // Aplica márgenes perimetrales de separación (Superior: 5px, Izquierdo: 15px, Inferior: 15px, Derecho: 15px)
        panelFooterSincronizado.setBorder(new EmptyBorder(5, 15, 15, 15));

        // Subpanel operativo izquierdo que distribuirá internamente las funciones de cancelación y edición
        JPanel panelAccionesIzquierda = new JPanel(new BorderLayout());
        // Hace transparente el subpanel para heredar limpiamente el color crema del fondo general
        panelAccionesIzquierda.setOpaque(false);

        // Instancia el botón de borrado total de la comanda en caja
        JButton btnCancelar = new JButton("CANCELAR PEDIDO");
        // Aplica un color terracota/ladrillo llamativo que alerte de una acción destructiva de limpieza
        btnCancelar.setBackground(new Color(0xA6, 0x4B, 0x2A));
        // Pinta las letras del botón de color blanco para asegurar legibilidad
        btnCancelar.setForeground(Color.WHITE);
        // Define un tamaño preferido robusto para el botón (Ancho: 150px, Alto: 45px) para facilitar el click rápido
        btnCancelar.setPreferredSize(new Dimension(150, 45));
        // Inyecta el método operativo que limpia el pedido actual y resetea la pantalla al hacer clic
        btnCancelar.addActionListener(e -> resetearVenta());
        // Ubica este botón de alerta en el extremo izquierdo (Oeste) de su subpanel operacional
        panelAccionesIzquierda.add(btnCancelar, BorderLayout.WEST);

        // Panel de flujo horizontal alineado a la derecha que agrupará los dos botones grises de edición consecutiva
        JPanel panelEdicionDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        // Hace transparente este panel secundario de flujo para evitar parches de color gris
        panelEdicionDerecha.setOpaque(false);

        // Instancia el botón encargado de alterar el conteo de unidades de las filas
        JButton btnModificarCant = new JButton("MODIFICAR CANTIDAD");
        // Aplica un color gris neutro que denote una acción operativa estándar de control interno
        btnModificarCant.setBackground(new Color(0x7A, 0x7A, 0x7A));
        // Pinta el texto del botón de color blanco puro
        btnModificarCant.setForeground(Color.WHITE);
        // Fija dimensiones cómodas y amplias para recibir el texto extenso (Ancho: 160px, Alto: 45px)
        btnModificarCant.setPreferredSize(new Dimension(160, 45));
        // Vincula la lógica que lanza un diálogo numérico flotante para editar la fila de la tabla
        btnModificarCant.addActionListener(e -> modificarCantidadSeleccionada());

        // Instancia el botón encargado de remover productos individuales del carrito de compras
        JButton btnEliminarProd = new JButton("ELIMINAR PRODUCTO");
        // Aplica el mismo color gris neutro consistente con el bloque de herramientas de edición de ítems
        btnEliminarProd.setBackground(new Color(0x7A, 0x7A, 0x7A));
        // Pinta el texto del botón de color blanco puro
        btnEliminarProd.setForeground(Color.WHITE);
        // Fija el tamaño del elemento (Ancho: 150px, Alto: 45px)
        btnEliminarProd.setPreferredSize(new Dimension(150, 45));
        // Vincula la lógica de borrado que remueve la fila seleccionada por click de la tabla de la boleta
        btnEliminarProd.addActionListener(e -> eliminarProductoSeleccionado());

        // Agrega de forma consecutiva el botón de modificar cantidad en el grupo de edición derecho
        panelEdicionDerecha.add(btnModificarCant);
        // Incorpora el botón de eliminar producto pegado a la derecha del anterior elemento con un espacio de 10px
        panelEdicionDerecha.add(btnEliminarProd);
        // Monta el subgrupo de edición completo en el extremo derecho (Este) del subpanel operativo izquierdo
        panelAccionesIzquierda.add(panelEdicionDerecha, BorderLayout.EAST);

        // Subpanel operativo derecho que encapsulará exclusivamente el botón definitivo de pago del terminal
        JPanel panelCobroDerecha = new JPanel(new BorderLayout());
        // Hace transparente el subpanel para heredar el crema exterior plano de fondo
        panelCobroDerecha.setOpaque(false);

        // Instancia el gran botón transaccional de fin de ciclo de venta
        JButton btnCobrar = new JButton("COBRAR PEDIDO");
        // Aplica un color verde esmeralda corporativo elegante que resalte el éxito de la operación comercial
        btnCobrar.setBackground(new Color(0x1E, 0x39, 0x32));
        // Pinta el texto en blanco puro
        btnCobrar.setForeground(Color.WHITE);
        // Modifica la fuente a SansSerif, Negrita y un tamaño intermedio de 15px para su rápida lectura
        btnCobrar.setFont(new Font("SansSerif", Font.BOLD, 15));
        // Define la altura en 45px e inicializa el ancho libre para ser controlado por la celda de la rejilla
        btnCobrar.setPreferredSize(new Dimension(0, 45));
        // Propiedad avanzada de FlatLaf que anula los bordes curvos dándole esquinas cuadradas limpias de acoplamiento
        btnCobrar.putClientProperty("JButton.buttonType", "square");
        // Vincula la acción atómica que escribe el ticket de texto físico y envía la orden por red al monitor de la cocina
        btnCobrar.addActionListener(e -> procesarCobro());
        // Inserta el botón verde ocupando de extremo a extremo todo el espacio del panel de cobro derecho
        panelCobroDerecha.add(btnCobrar, BorderLayout.CENTER);

        // Configura los parámetros de inyección del footer izquierdo dentro del panel footer sincronizado general
        GridBagConstraints gbcFooter = new GridBagConstraints();
        // Fuerza el estiramiento completo en ambos ejes dentro de la coordenada asignada
        gbcFooter.fill = GridBagConstraints.BOTH;
        // Mantiene una altura fija para la botonera inferior bloqueando el crecimiento vertical reactivo
        gbcFooter.weighty = 1.0;
        // Asigna la coordenada de la celda (Fila 1, Columna 0)
        gbcFooter.gridx = 0;
        gbcFooter.gridy = 1;
        // Sincroniza de forma matemática compartiendo el mismo peso exacto de ancho que el catálogo de arriba (58%)
        gbcFooter.weightx = 0.58;
        // Agrega una separación idéntica de 10px a la derecha para mantener la línea vertical del canal central de aire
        gbcFooter.insets = new Insets(0, 0, 0, 10);
        // Inyecta el grupo de 3 botones en el panel footer sincronizado maestro
        panelFooterSincronizado.add(panelAccionesIzquierda, gbcFooter);

        // Configura los parámetros de inyección para acoplar el botón cobrar en el extremo derecho inferior del sistema
        gbcFooter.gridx = 1; // Avanza a la coordenada de celda (Fila 1, Columna 1)
        gbcFooter.gridy = 1; // Se mantiene en la línea inferior de la botonera
        // Sincroniza de forma exacta compartiendo el mismo peso de ancho que el panel blanco de la boleta de arriba (42%)
        gbcFooter.weightx = 0.42;
        // Completa la brecha de aire inyectando un margen de separación de 10px hacia el lado izquierdo
        gbcFooter.insets = new Insets(0, 10, 0, 0);
        // Inyecta el botón verde de cobro definitivo cerrando la rejilla sincronizada del footer global
        panelFooterSincronizado.add(panelCobroDerecha, gbcFooter);

        // Une de forma permanente el footer sincronizado en el extremo inferior (Sur) del contenedor global operativo
        panelContenedorGlobal.add(panelFooterSincronizado, BorderLayout.SOUTH);
        // Monta el ecosistema de control completo terminado en el centro de la ventana principal de la aplicación
        add(panelContenedorGlobal, BorderLayout.CENTER);
    }

    private void filtrarYBuscarProductos() {
        panelGridProductos.removeAll();
        String textoBusqueda = txtBuscar != null ? txtBuscar.getText().toLowerCase().trim() : "";

        for (Producto prod : baseDatosProductos) {
            boolean coincideCategoria = prod.getCategoria().equals(categoriaActiva);
            boolean coincideTexto = textoBusqueda.isEmpty() || prod.getNombre().toLowerCase().contains(textoBusqueda);

            if (coincideCategoria && coincideTexto) {
                JPanel tarjeta = new JPanel(new BorderLayout(5, 5));
                tarjeta.setBackground(Color.WHITE);
                tarjeta.setBorder(new LineBorder(new Color(0xE0, 0xDE, 0xD8), 1, true));
                tarjeta.setPreferredSize(new Dimension(260, 140));

                JPanel panelTextoInterno = new JPanel(new GridLayout(2, 1, 0, 2));
                panelTextoInterno.setOpaque(false);
                panelTextoInterno.setBorder(new EmptyBorder(20, 10, 10, 10));

                JLabel lblNombre = new JLabel(prod.getNombre(), JLabel.CENTER);
                lblNombre.setFont(new Font("SansSerif", Font.BOLD, 15));
                lblNombre.setForeground(new Color(0x21, 0x21, 0x21));

                JLabel lblPrecio = new JLabel("S/. " + String.format("%.2f", prod.getPrecio()), JLabel.CENTER);
                lblPrecio.setFont(new Font("SansSerif", Font.PLAIN, 14));
                lblPrecio.setForeground(new Color(0x7A, 0x7A, 0x7A));

                panelTextoInterno.add(lblNombre);
                panelTextoInterno.add(lblPrecio);
                tarjeta.add(panelTextoInterno, BorderLayout.CENTER);

                JButton btnAgregar = new JButton("AGREGAR");
                btnAgregar.setBackground(new Color(0x8B, 0x7E, 0x74));
                btnAgregar.setForeground(Color.WHITE);
                btnAgregar.setFont(new Font("SansSerif", Font.BOLD, 12));
                btnAgregar.setPreferredSize(new Dimension(240, 35));
                btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnAgregar.addActionListener(e -> {
                    pedidoActual.agregarProducto(prod, 1);
                    actualizarTablaVisual();
                });

                JPanel panelBtnWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
                panelBtnWrapper.setOpaque(false);
                panelBtnWrapper.add(btnAgregar);
                tarjeta.add(panelBtnWrapper, BorderLayout.SOUTH);

                panelGridProductos.add(tarjeta);
            }
        }
        panelGridProductos.revalidate();
        panelGridProductos.repaint();
    }

    private void actualizarTablaVisual() {
        modeloTabla.setRowCount(0);
        List<Producto> prods = pedidoActual.getListaProductos();
        List<Integer> cants = pedidoActual.getListaCantidades();

        for (int i = 0; i < prods.size(); i++) {
            modeloTabla.addRow(new Object[]{
                prods.get(i).getNombre(),
                cants.get(i),
                "S/. " + String.format("%.2f", prods.get(i).getPrecio() * cants.get(i))
            });
        }

        double total = pedidoActual.calcularTotal();
        double subtotal = total / 1.18;
        double igv = total - subtotal;

        lblSubtotalValor.setText(String.format("%.2f", subtotal));
        lblIgvValor.setText(String.format("%.2f", igv));
        lblTotal.setText("Total: S/. " + String.format("%.2f", total));
    }

    private void eliminarProductoSeleccionado() {
        int fila = tablaPedido.getSelectedRow();
        if (fila != -1) {
            pedidoActual.eliminarProducto(fila);
            actualizarTablaVisual();
        } else {
            JOptionPane.showMessageDialog(this, "Debe hacer clic sobre un producto de la tabla para poder eliminarlo.");
        }
    }

    private void modificarCantidadSeleccionada() {
        int fila = tablaPedido.getSelectedRow();
        if (fila != -1) {
            String input = JOptionPane.showInputDialog(this, "Ingrese la nueva cantidad para el producto:");
            try {
                if (input != null) {
                    int nuevaCant = Integer.parseInt(input);
                    pedidoActual.modificarCantidad(fila, nuevaCant);
                    actualizarTablaVisual();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un número entero válido.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe hacer clic sobre un producto de la tabla para modificar su cantidad.");
        }
    }

    private void resetearVenta() {
        pedidoActual = fachada.crearNuevoPedido();
        actualizarTablaVisual();
    }

    private void procesarCobro() {
        if (pedidoActual.getListaProductos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos en la orden actual.");
            return;
        }
        fachada.procesarCobroYDespacho(pedidoActual);
        JOptionPane.showMessageDialog(this, "Comprobante emitido. Orden despachada a barra.");
        resetearVenta();
    }

//    private void inicializarBaseDatosMock() {
//        baseDatosProductos = new ArrayList<>();
//        baseDatosProductos.add(new Producto("Espresso Doble", 4.50, "CAFÉS"));
//        baseDatosProductos.add(new Producto("Latte Mediano", 4.00, "CAFÉS"));
//        baseDatosProductos.add(new Producto("Capuccino Grande", 6.50, "CAFÉS"));
//        baseDatosProductos.add(new Producto("Chicha Helada", 3.50, "BEBIDAS FRÍAS"));
//        baseDatosProductos.add(new Producto("Iced Tea Limón", 4.00, "BEBIDAS FRÍAS"));
//        baseDatosProductos.add(new Producto("Muffin Arándanos", 4.00, "POSTRES"));
//        baseDatosProductos.add(new Producto("Croissant Manjar", 4.50, "POSTRES"));
//        baseDatosProductos.add(new Producto("Sándwich Pollo", 6.00, "SÁNDWICHES"));
//        baseDatosProductos.add(new Producto("Sándwich Triple", 5.50, "SÁNDWICHES"));
//    }
    private void inicializarBaseDatosMock() {
        baseDatosProductos = new ArrayList<>();

        // --- CATEGORÍA: CAFÉS ---
        baseDatosProductos.add(new Producto("Espresso Doble", 4.50, "CAFÉS"));
        baseDatosProductos.add(new Producto("Latte Mediano", 5.50, "CAFÉS"));
        baseDatosProductos.add(new Producto("Capuccino Grande", 7.50, "CAFÉS"));
        baseDatosProductos.add(new Producto("Americano Regular", 4.00, "CAFÉS"));
        baseDatosProductos.add(new Producto("Mocaccino Especial", 8.00, "CAFÉS"));
        baseDatosProductos.add(new Producto("Frappé de Vainilla", 9.50, "CAFÉS"));

        // --- CATEGORÍA: BEBIDAS FRÍAS ---
        baseDatosProductos.add(new Producto("Chicha Helada Jarra", 11.00, "BEBIDAS FRÍAS"));
        baseDatosProductos.add(new Producto("Chicha Helada Vaso", 4.00, "BEBIDAS FRÍAS"));
        baseDatosProductos.add(new Producto("Iced Tea Limón", 4.50, "BEBIDAS FRÍAS"));
        baseDatosProductos.add(new Producto("Jugo de Naranja", 6.00, "BEBIDAS FRÍAS"));
        baseDatosProductos.add(new Producto("Jugo de Fresa y Leche", 7.50, "BEBIDAS FRÍAS"));
        baseDatosProductos.add(new Producto("Limonada Frozen", 6.50, "BEBIDAS FRÍAS"));

        // --- CATEGORÍA: POSTRES ---
        baseDatosProductos.add(new Producto("Muffin Arándanos", 4.50, "POSTRES"));
        baseDatosProductos.add(new Producto("Croissant Manjar", 4.00, "POSTRES"));
        baseDatosProductos.add(new Producto("Porción Torta Chocolate", 8.50, "POSTRES"));
        baseDatosProductos.add(new Producto("Pie de Limón", 7.50, "POSTRES"));
        baseDatosProductos.add(new Producto("Cheesecake de Fresa", 8.50, "POSTRES"));
        baseDatosProductos.add(new Producto("Alfajor de la Casa", 3.00, "POSTRES"));

        // --- CATEGORÍA: SÁNDWICHES ---
        baseDatosProductos.add(new Producto("Sándwich de Pollo", 6.50, "SÁNDWICHES"));
        baseDatosProductos.add(new Producto("Sándwich Triple", 5.50, "SÁNDWICHES"));
        baseDatosProductos.add(new Producto("Sándwich de Asado", 9.00, "SÁNDWICHES"));
        baseDatosProductos.add(new Producto("Butifarra Tradicional", 8.00, "SÁNDWICHES"));
        baseDatosProductos.add(new Producto("Empanada de Carne", 4.50, "SÁNDWICHES"));
        baseDatosProductos.add(new Producto("Croissant de Jamón y Queso", 6.00, "SÁNDWICHES"));
    }
}
