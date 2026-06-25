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
        inicializarDatos();
        configurarVentana();
        inicializarComponentes();
        filtrarYBuscarProductos();
    }

    private void configurarVentana() {
        setTitle("CAFETERÍA - PUNTO DE VENTA");
        setSize(1024, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(0xF4, 0xF1, 0xEA));
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
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

        // Contenedor de botones de utilidades - derecha
        JPanel panelUtilidades = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18)); // Alineacion en X e Y
        panelUtilidades.setOpaque(false); // Fondo transparente

        // Botón de arqueo
        JButton btnCaja = new JButton("☕");
        btnCaja.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Efecto hover
        btnCaja.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCaja.setText("Reporte de hoy");
                btnCaja.setForeground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCaja.setText("☕");
                btnCaja.setForeground(Color.BLACK);
            }
        });

        // Botón para exportar el reporte general
        JButton btnReporte = new JButton("📋");
        btnReporte.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Efecto hover
        btnReporte.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnReporte.setText("Reporte general");
                btnReporte.setForeground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnReporte.setText("📋");
                btnReporte.setForeground(Color.BLACK);
            }
        });

        // Botón de información
        JButton btnInfo = new JButton("❓");
        btnInfo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnInfo.addActionListener(e -> JOptionPane.showMessageDialog(this, "App Cafetería POS\nDesarrollado por: Rodrigo Sihues Yanqui", "Información", JOptionPane.INFORMATION_MESSAGE));
        // Efecto hover
        btnInfo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnInfo.setText("Información");
                btnInfo.setForeground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnInfo.setText("❓");
                btnInfo.setForeground(Color.BLACK);
            }
        });

        // Agregar de forma consecutiva los botones al panel de utilidades
        panelUtilidades.add(btnCaja);
        panelUtilidades.add(btnReporte);
        panelUtilidades.add(btnInfo);
        // Agregar el grupo de utilidades en el extremo derecho de la barra
        panelHeader.add(panelUtilidades, BorderLayout.EAST);
        // Fijar la barra superior terminada en el límite norte de la ventana de la aplicación
        add(panelHeader, BorderLayout.NORTH);

        // ########## PANEL DE CONTROL GLOBAL ##########
        JPanel panelContenedorGlobal = new JPanel(new BorderLayout(0, 0));
        panelContenedorGlobal.setBackground(new Color(0xF4, 0xF1, 0xEA)); // Color crema claro

        // CUERPO CENTRAL
        JPanel panelCuerpo = new JPanel(new GridBagLayout());
        panelCuerpo.setBackground(new Color(0xF4, 0xF1, 0xEA));
        // Márgenes (Superior: 15px, Izquierda: 15px, Inferior: 10px, Derecha: 15px
        panelCuerpo.setBorder(new EmptyBorder(15, 15, 10, 15));

        // ##### COLUMNA IZQUIERDA - CATÁLOGO DE PRODUCTOS
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 12)); // Panel vertical que separa 12px en el eje vertical
        panelIzquierdo.setBackground(new Color(0xF4, 0xF1, 0xEA));

        // Contenedor superior para la barra de búsqueda y las pestañas con un espacio de 8px
        JPanel panelControlesIzquierda = new JPanel(new BorderLayout(0, 8));
        panelControlesIzquierda.setOpaque(false);

        // Campo de ingreso de texto para la búsqueda de productos
        txtBuscar = new JTextField();
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar producto por nombre..."); // Placeholder
        // Escucha para capturar cambios en el texto al instante
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarYBuscarProductos();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarYBuscarProductos();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarYBuscarProductos();
            }
        });
        panelControlesIzquierda.add(txtBuscar, BorderLayout.NORTH);

        // Contenedor de categorias en fila horizontal alineada al centro con espacios de 5px en X
        JPanel panelCategorias = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panelCategorias.setOpaque(false);
        String[] cats = {"CAFÉS", "BEBIDAS FRÍAS", "POSTRES", "SÁNDWICHES"};

        for (String cat : cats) {
            JButton btnCat = new JButton(cat);
            btnCat.setBackground(new Color(0x4A, 0x35, 0x25));
            btnCat.setForeground(Color.WHITE);
            btnCat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Agrega el evento click que actualiza el estado de la categoría activa y refresca el grid
            btnCat.addActionListener(e -> {
                categoriaActiva = cat; // Modifica la variable global por la pestaña seleccionada actualmente
                filtrarYBuscarProductos(); // Redibuja de inmediato
            });
            // Efect hover
            btnCat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btnCat.setBackground(Color.WHITE);
                    btnCat.setForeground(new Color(0x4A, 0x35, 0x25));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btnCat.setBackground(new Color(0x4A, 0x35, 0x25));
                    btnCat.setForeground(Color.WHITE);
                }
            });
            panelCategorias.add(btnCat);
        }
        panelControlesIzquierda.add(panelCategorias, BorderLayout.SOUTH);
        // Fija la búsqueda y categorías en la parte superior de la columna izquierda
        panelIzquierdo.add(panelControlesIzquierda, BorderLayout.NORTH);

        // Contenedor para aislar las tarjetas de productos
        JPanel panelLienzoProductos = new JPanel(new BorderLayout());
        panelLienzoProductos.setBackground(Color.WHITE);
        // Borde compuesto, contorno gris redondeado 1px con espaciado interno de 16px en las 4 esquinas
        panelLienzoProductos.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xD2, 0xCF, 0xC7), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Instancia de dos columnas con espacios de separación de 16px en ambos ejes
        panelGridProductos = new JPanel(new GridLayout(0, 2, 16, 16));
        panelGridProductos.setBackground(Color.WHITE);

        // Panel que bloquea el crecimiento vertical de las tarjetas si hay pocos productos
        JPanel panelAnclaNorte = new JPanel(new BorderLayout());
        panelAnclaNorte.setBackground(Color.WHITE);
        panelAnclaNorte.add(panelGridProductos, BorderLayout.NORTH);

        // Para envolver el panel de anclaje dentro de un componente scroll
        JScrollPane scrollProductos = new JScrollPane(panelAnclaNorte);
        // Elimina el borde gris nativo del JScrollPane
        scrollProductos.setBorder(BorderFactory.createEmptyBorder());
        scrollProductos.getViewport().setBackground(Color.WHITE);

        panelLienzoProductos.add(scrollProductos, BorderLayout.CENTER);
        panelIzquierdo.add(panelLienzoProductos, BorderLayout.CENTER);

        // Configura las reglas estructurales de la columna izquierda
        GridBagConstraints gbc = new GridBagConstraints();
        // Fuerza al componente a estirarse en ambos sentidos para cubrir toda su celda asignada
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0; // Coordenada X inicial (Fila 0, Columna 0)
        gbc.gridy = 0; // Coordenada Y inicial        
        gbc.weightx = 0.58; // Ancho 58%
        // Permite que la sección absorba el 100% del crecimiento vertical disponible de la ventana
        gbc.weighty = 1.0;
        // Genera un canal de aire o brecha de 10px a la derecha para separarse de la columna de facturación
        gbc.insets = new Insets(0, 0, 0, 10);
        panelCuerpo.add(panelIzquierdo, gbc);

        // ##### COLUMNA DERECHA - DETALLES DEL PEDIDO
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 0));
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setBorder(BorderFactory.createLineBorder(new Color(0xDB, 0xD9, 0xD3), 1));

        String[] columnas = {"Nombre", "Cantidad", "Precio"};
        // Inicializa el modelo de la tabla anulando la edición por teclado en las celdas por seguridad
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Vincula el modelo lógico al componente visual JTable
        tablaPedido = new JTable(modeloTabla);
        // Restringe la selección de la tabla a una sola fila a la vez
        tablaPedido.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPedido.setRowHeight(25); // Altura por fila

        // Envuelve la tabla dentro de un contenedor scrollable
        JScrollPane scrollProductosSeleccionados = new JScrollPane(tablaPedido);
        // Remueve el contorno gris del scroll
        scrollProductosSeleccionados.setBorder(BorderFactory.createEmptyBorder());
        scrollProductosSeleccionados.getViewport().setBackground(Color.WHITE); // Color de fondo
        panelDerecho.add(scrollProductosSeleccionados, BorderLayout.CENTER);

        // Panel vertical inferior
        JPanel panelConsolidadoCobro = new JPanel(new BorderLayout());
        panelConsolidadoCobro.setBackground(Color.WHITE);

        // Instancia un componente separador horizontal gris
        JSeparator lineaSeparadora = new JSeparator(JSeparator.HORIZONTAL);
        lineaSeparadora.setForeground(new Color(0xDB, 0xD9, 0xD3));
        panelConsolidadoCobro.add(lineaSeparadora, BorderLayout.NORTH);

        // Grid de 3 filas y 2 columnas con separaciones de 5px
        JPanel panelTextosBoleta = new JPanel(new GridLayout(3, 2, 5, 5));
        panelTextosBoleta.setBackground(Color.WHITE);
        // Espaciado interno de 15px en los bordes
        panelTextosBoleta.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Inserta la etiqueta Subtotal en la fila 1, columna 1
        panelTextosBoleta.add(new JLabel("Subtotal (S/.):"));
        // Componente que muestra el valor cambiante del subtotal
        lblSubtotalValor = new JLabel("0.00", JLabel.RIGHT);
        // Inserta el valor cambiante en la fila 1, columna 2
        panelTextosBoleta.add(lblSubtotalValor);

        // Inserta la etiqueta IGV en la fila 2, columna 1
        panelTextosBoleta.add(new JLabel("IGV (18%):"));
        // Componente que muestra el valor cambiante del IGV
        lblIgvValor = new JLabel("0.00", JLabel.RIGHT);
        // Inserta el valor cambiante en la fila 2, columna 2
        panelTextosBoleta.add(lblIgvValor);

        // Inserta la etiqueta Total en la fila 3, columna 1
        panelTextosBoleta.add(new JLabel("Total General:"));
        // Componente que muestra el valor cambiante del Total
        lblTotal = new JLabel("S/. 0.00", JLabel.RIGHT);
        // Cambia la tipografía, Negrita y tamaño
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTotal.setForeground(new Color(0x21, 0x21, 0x21));
        // Inserta el valor cambiante en la fila 3, columna 2
        panelTextosBoleta.add(lblTotal);

        panelConsolidadoCobro.add(panelTextosBoleta, BorderLayout.CENTER);
        panelDerecho.add(panelConsolidadoCobro, BorderLayout.SOUTH);

        // Configura las reglas estructurales de la columna derecha
        gbc.gridx = 1; // Avanza a la coordenada X (Fila 0, Columna 1)
        gbc.gridy = 0; // Mantiene la fila inicial
        gbc.weightx = 0.42; // Ancho 42%
        gbc.weighty = 1.0; // Altura al mismo tamaño que la columna izquierda
        gbc.insets = new Insets(0, 10, 0, 0); // 10px a la izquierda
        panelCuerpo.add(panelDerecho, gbc);

        // Agrega el cuerpo central completo terminado al centro del panel contenedor operativo principal
        panelContenedorGlobal.add(panelCuerpo, BorderLayout.CENTER);

        // ########## FOOTER DE BOTONES ##########
        // Contenedor izquierdo para los 3 primeros botones
        JPanel panelAccionesIzquierda = new JPanel(new BorderLayout());
        panelAccionesIzquierda.setOpaque(false);

        // Botón de CANCELAR PEDIDO
        JButton btnCancelar = new JButton("CANCELAR PEDIDO");
        btnCancelar.setBackground(new Color(0xA6, 0x4B, 0x2A));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setPreferredSize(new Dimension(150, 45));
        btnCancelar.addActionListener(e -> resetearVenta());
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelAccionesIzquierda.add(btnCancelar, BorderLayout.WEST);

        // Contenedor horizontal para agrupar los dos botones grises de edición
        JPanel panelEdicionDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelEdicionDerecha.setOpaque(false);

        // Botón MODIFICAR CANTIDAD
        JButton btnModificarCant = new JButton("MODIFICAR CANTIDAD");
        btnModificarCant.setBackground(new Color(0x7A, 0x7A, 0x7A));
        btnModificarCant.setForeground(Color.WHITE);
        btnModificarCant.setPreferredSize(new Dimension(160, 45));
        btnModificarCant.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModificarCant.addActionListener(e -> modificarCantidadSeleccionada());

        // Botón ELIMINAR PRODUCTO
        JButton btnEliminarProd = new JButton("ELIMINAR PRODUCTO");
        btnEliminarProd.setBackground(new Color(0x7A, 0x7A, 0x7A));
        btnEliminarProd.setForeground(Color.WHITE);
        btnEliminarProd.setPreferredSize(new Dimension(150, 45));
        btnEliminarProd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminarProd.addActionListener(e -> eliminarProductoSeleccionado());

        panelEdicionDerecha.add(btnModificarCant);
        // Espaciador invisible
        Component espacioIzquierdo = Box.createHorizontalStrut(15);
        panelEdicionDerecha.add(espacioIzquierdo);
        panelEdicionDerecha.add(btnEliminarProd);

        panelAccionesIzquierda.add(panelEdicionDerecha, BorderLayout.EAST);

        // Panel derecho para el botón COBRAR PEDIDO
        JPanel panelCobroDerecha = new JPanel(new BorderLayout());
        panelCobroDerecha.setOpaque(false);

        // Instancia el gran botón transaccional de fin de ciclo de venta
        JButton btnCobrar = new JButton("COBRAR PEDIDO");
        btnCobrar.setBackground(new Color(0x1E, 0x39, 0x32));
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnCobrar.setPreferredSize(new Dimension(0, 45)); // Altura en 45px y Ancho libre
        // Propiedad de FlatLaf que anula los bordes y establece esquinas cuadradas
        btnCobrar.putClientProperty("JButton.buttonType", "square");
        btnCobrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCobrar.addActionListener(e -> procesarCobro());
        panelCobroDerecha.add(btnCobrar, BorderLayout.CENTER);

        // Parámetros del footer izquierdo
        GridBagConstraints gbcFooter = new GridBagConstraints();
        // Fuerza el estiramiento completo en ambos ejes dentro de la coordenada asignada
        gbcFooter.fill = GridBagConstraints.BOTH;
// SOLUCIÓN: Inyectar los botones en la fila 1 de la misma rejilla central
        gbcFooter.gridx = 0; // Columna izquierda
        gbcFooter.gridy = 1; // Fila inferior
        gbcFooter.weightx = 0.58; // Mismo ancho que el catálogo (58%)
        gbcFooter.weighty = 0.045;  // Altura fija para botones, no elástica
        gbcFooter.insets = new Insets(12, 0, 0, 10); // Margen: superior, izquierda, abajo, derecha
        panelCuerpo.add(panelAccionesIzquierda, gbcFooter);

        gbcFooter.gridx = 1; // Columna derecha
        gbcFooter.gridy = 1; // Fila inferior
        gbcFooter.weightx = 0.42; // Mismo ancho que la boleta (42%)
        gbcFooter.weighty = 0.045;
        gbcFooter.insets = new Insets(12, 10, 0, 0);
        panelCuerpo.add(panelCobroDerecha, gbcFooter);

        add(panelCuerpo, BorderLayout.CENTER);
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
        lblTotal.setText("S/. " + String.format("%.2f", total));
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

    private void inicializarDatos() {
        baseDatosProductos = new ArrayList<>();

        // CAFÉS
        baseDatosProductos.add(new Producto("Espresso Doble", 4.50, "CAFÉS"));
        baseDatosProductos.add(new Producto("Latte Mediano", 5.50, "CAFÉS"));
        baseDatosProductos.add(new Producto("Capuccino Grande", 7.50, "CAFÉS"));
        baseDatosProductos.add(new Producto("Americano Regular", 4.00, "CAFÉS"));
        baseDatosProductos.add(new Producto("Mocaccino Especial", 8.00, "CAFÉS"));
        baseDatosProductos.add(new Producto("Frappé de Vainilla", 9.50, "CAFÉS"));

        // BEBIDAS FRÍAS
        baseDatosProductos.add(new Producto("Chicha Helada Jarra", 11.00, "BEBIDAS FRÍAS"));
        baseDatosProductos.add(new Producto("Chicha Helada Vaso", 4.00, "BEBIDAS FRÍAS"));
        baseDatosProductos.add(new Producto("Iced Tea Limón", 4.50, "BEBIDAS FRÍAS"));
        baseDatosProductos.add(new Producto("Jugo de Naranja", 6.00, "BEBIDAS FRÍAS"));
        baseDatosProductos.add(new Producto("Jugo de Fresa y Leche", 7.50, "BEBIDAS FRÍAS"));
        baseDatosProductos.add(new Producto("Limonada Frozen", 6.50, "BEBIDAS FRÍAS"));

        // POSTRES
        baseDatosProductos.add(new Producto("Muffin Arándanos", 4.50, "POSTRES"));
        baseDatosProductos.add(new Producto("Croissant Manjar", 4.00, "POSTRES"));
        baseDatosProductos.add(new Producto("Porción Torta Chocolate", 8.50, "POSTRES"));
        baseDatosProductos.add(new Producto("Pie de Limón", 7.50, "POSTRES"));
        baseDatosProductos.add(new Producto("Cheesecake de Fresa", 8.50, "POSTRES"));
        baseDatosProductos.add(new Producto("Alfajor de la Casa", 3.00, "POSTRES"));

        // SÁNDWICHES
        baseDatosProductos.add(new Producto("Sándwich de Pollo", 6.50, "SÁNDWICHES"));
        baseDatosProductos.add(new Producto("Sándwich Triple", 5.50, "SÁNDWICHES"));
        baseDatosProductos.add(new Producto("Sándwich de Asado", 9.00, "SÁNDWICHES"));
        baseDatosProductos.add(new Producto("Butifarra Tradicional", 8.00, "SÁNDWICHES"));
        baseDatosProductos.add(new Producto("Empanada de Carne", 4.50, "SÁNDWICHES"));
        baseDatosProductos.add(new Producto("Croissant de Jamón y Queso", 6.00, "SÁNDWICHES"));
    }
}
