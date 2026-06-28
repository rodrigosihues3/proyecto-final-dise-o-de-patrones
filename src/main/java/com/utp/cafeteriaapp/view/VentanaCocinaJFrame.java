package com.utp.cafeteriaapp.view;

import com.utp.cafeteriaapp.model.Pedido;
import com.utp.cafeteriaapp.model.Producto;
import com.utp.cafeteriaapp.service.Observador;
import com.utp.cafeteriaapp.service.RestauranteFacade;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class VentanaCocinaJFrame extends JFrame implements Observador {

    // Variable para conectar con las funciones lógicas de todo el restaurante
    private RestauranteFacade fachada;
    private JPanel panelColaHorizontal;
    private final SimpleDateFormat formatoHora = new SimpleDateFormat("hh:mm:ss a");

    public VentanaCocinaJFrame(RestauranteFacade fachada) {
        this.fachada = fachada;
        this.fachada.getCocina().enlazarObservador(this);

        configurarVentana();
        inicializarComponentes();
        actualizar();
    }

    private void configurarVentana() {
        setTitle("CAFETERIA - MONITOR DE COCINA");
        setSize(737, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0xF4, 0xF1, 0xEA));
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        // Barra Superior
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0x4A, 0x35, 0x25));
        panelHeader.setPreferredSize(new Dimension(1024, 60));

        JLabel lblTitulo = new JLabel("CAFETERIA - PANTALLA DE COCINA", JLabel.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        add(panelHeader, BorderLayout.NORTH);

        // Panel contenedor Horizontal con Scrollbar
        panelColaHorizontal = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        panelColaHorizontal.setBackground(new Color(0xF4, 0xF1, 0xEA));

        JScrollPane scrollHorizontal = new JScrollPane(panelColaHorizontal);
        scrollHorizontal.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollHorizontal.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollHorizontal.setBorder(BorderFactory.createEmptyBorder());
        scrollHorizontal.getViewport().setBackground(new Color(0xF4, 0xF1, 0xEA));

        add(scrollHorizontal, BorderLayout.CENTER);
    }

    @Override
    public void actualizar() {
        panelColaHorizontal.removeAll();

        List<Pedido> pedidosActivos = fachada.getCocina().getColaPedidosPendientes();

        if (pedidosActivos.isEmpty()) {
            JLabel lblVacio = new JLabel("Sin órdenes pendientes por preparar.", JLabel.CENTER);
            lblVacio.setFont(new Font("SansSerif", Font.ITALIC, 18));
            lblVacio.setForeground(new Color(0x7A, 0x7A, 0x7A));
            panelColaHorizontal.add(lblVacio);
        } else {
            for (Pedido ped : pedidosActivos) {
                JPanel tarjetaPedido = createTarjetaPedidoVisual(ped);
                panelColaHorizontal.add(tarjetaPedido);
            }
        }

        panelColaHorizontal.revalidate();
        panelColaHorizontal.repaint();
    }

    private JPanel createTarjetaPedidoVisual(Pedido pedido) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 5));
        tarjeta.setPreferredSize(new Dimension(240, 560));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createLineBorder(new Color(0x4A, 0x35, 0x25), 1, true));

        // Cabecera Interna de la Tarjeta
        JPanel panelInfoTarjeta = new JPanel(new GridLayout(3, 1, 0, 2));
        panelInfoTarjeta.setBackground(Color.WHITE);
        panelInfoTarjeta.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel lblId = new JLabel("ORDER #" + pedido.getIdPedido(), JLabel.LEFT);
        lblId.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblId.setForeground(new Color(0x4A, 0x35, 0x25));

        // Hora de ingreso para priorización visual
        String horaIngreso = formatoHora.format(new Date());
        JLabel lblHora = new JLabel("Ingreso: " + horaIngreso, JLabel.LEFT);
        lblHora.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblHora.setForeground(new Color(0x7A, 0x7A, 0x7A));

        JLabel lblTime = new JLabel("En cola...", JLabel.LEFT);
        lblTime.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTime.setForeground(Color.RED);

        panelInfoTarjeta.add(lblId);
        panelInfoTarjeta.add(lblHora);
        panelInfoTarjeta.add(lblTime);
        tarjeta.add(panelInfoTarjeta, BorderLayout.NORTH);

        // Scroll interno y ajuste de líneas para productos largos
        JTextArea txtItems = new JTextArea();
        txtItems.setEditable(false);
        txtItems.setFont(new Font("MonoSpaced", Font.BOLD, 14));
        txtItems.setForeground(new Color(0x21, 0x21, 0x21));
        txtItems.setLineWrap(true);
        txtItems.setWrapStyleWord(true);

        List<Producto> productos = pedido.getListaProductos();
        List<Integer> cantidades = pedido.getListaCantidades();

        for (int i = 0; i < productos.size(); i++) {
            txtItems.append(cantidades.get(i) + "x " + productos.get(i).getNombre() + "\n");
        }

        JScrollPane scrollInternoCard = new JScrollPane(txtItems);
        scrollInternoCard.setBorder(new EmptyBorder(5, 12, 5, 12));
        scrollInternoCard.getViewport().setBackground(Color.WHITE);
        scrollInternoCard.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollInternoCard.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tarjeta.add(scrollInternoCard, BorderLayout.CENTER);

        // Botón con diseño square y efecto hover interactivo
        JButton btnCompletado = new JButton("MARCAR COMPLETADO");
        btnCompletado.setBackground(new Color(0x1E, 0x39, 0x32));
        btnCompletado.setForeground(Color.WHITE);
        btnCompletado.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCompletado.setPreferredSize(new Dimension(0, 45));
        btnCompletado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompletado.putClientProperty("JButton.buttonType", "square");
        btnCompletado.addActionListener(e -> fachada.getCocina().despacharPedido(pedido.getIdPedido()));

        btnCompletado.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCompletado.setBackground(new Color(0x2D, 0x55, 0x4B));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCompletado.setBackground(new Color(0x1E, 0x39, 0x32));
            }
        });

        tarjeta.add(btnCompletado, BorderLayout.SOUTH);

        return tarjeta;
    }
}
