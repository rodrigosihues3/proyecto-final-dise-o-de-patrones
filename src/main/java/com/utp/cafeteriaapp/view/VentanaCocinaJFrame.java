package com.utp.cafeteriaapp.view;

import com.utp.cafeteriaapp.model.Pedido;
import com.utp.cafeteriaapp.model.Producto;
import com.utp.cafeteriaapp.service.Observador;
import com.utp.cafeteriaapp.service.RestauranteFacade;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class VentanaCocinaJFrame extends JFrame implements Observador {

    private RestauranteFacade fachada;
    private JPanel panelColaHorizontal;

    public VentanaCocinaJFrame(RestauranteFacade fachada) {
        this.fachada = fachada;
        // Enlazar esta ventana al patrón Observer de la cocina
        this.fachada.getCocina().enlazarObservador(this);

        configurarVentana();
        inicializarComponentes();
        actualizar();
    }

    private void configurarVentana() {
        setTitle("UTP CAFETERIA - MONITOR DE COCINA");
        setSize(1024, 728);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Evita que cierren la cocina por error
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0xF4, 0xF1, 0xEA));
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        // Barra Superior
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0x4A, 0x35, 0x25));
        panelHeader.setPreferredSize(new Dimension(1024, 60));

        JLabel lblTitulo = new JLabel("UTP CAFETERIA - PANTALLA DE COCINA", JLabel.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        add(panelHeader, BorderLayout.NORTH);

        // Panel contenedor Horizontal con Scrollbar
        panelColaHorizontal = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        panelColaHorizontal.setBackground(new Color(0xF4, 0xF1, 0xEA));

        JScrollPane scrollHorizontal = new JScrollPane(panelColaHorizontal);
        scrollHorizontal.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollHorizontal.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollHorizontal.setBorder(BorderFactory.createEmptyBorder());

        add(scrollHorizontal, BorderLayout.CENTER);
    }

    // Implementación del contrato Observador
    @Override
    public void actualizar() {
        // Limpiar el lienzo horizontal por completo antes de remaquetar
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

    // Contenedor visual de la columna para cada pedido.
    private JPanel createTarjetaPedidoVisual(Pedido pedido) {
        JPanel tarjeta = new JPanel(new BorderLayout(10, 10));
        tarjeta.setPreferredSize(new Dimension(220, 560)); // Columna Alta
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createLineBorder(new Color(0x4A, 0x35, 0x25), 2, true));

        // Cabecera Interna de la Tarjeta
        JPanel panelInfoTarjeta = new JPanel(new GridLayout(2, 1));
        panelInfoTarjeta.setBackground(Color.WHITE);

        JLabel lblId = new JLabel("  ORDER #" + pedido.getIdPedido(), JLabel.LEFT);
        lblId.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblId.setForeground(new Color(0x4A, 0x35, 0x25));

        // Simulación estática del cronómetro
        JLabel lblTime = new JLabel("  En cola...", JLabel.LEFT);
        lblTime.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTime.setForeground(Color.RED);

        panelInfoTarjeta.add(lblId);
        panelInfoTarjeta.add(lblTime);
        tarjeta.add(panelInfoTarjeta, BorderLayout.NORTH);

        // Lista de ítems en el centro (Text Area limpia y sin edición)
        JTextArea txtItems = new JTextArea();
        txtItems.setEditable(false);
        txtItems.setFont(new Font("MonoSpaced", Font.BOLD, 13));
        txtItems.setForeground(new Color(0x21, 0x21, 0x21));
        txtItems.setMargin(new Insets(10, 10, 10, 10));

        List<Producto> productos = pedido.getListaProductos();
        List<Integer> cantidades = pedido.getListaCantidades();

        for (int i = 0; i < productos.size(); i++) {
            txtItems.append(cantidades.get(i) + "x " + productos.get(i).getNombre() + "\n");
        }
        tarjeta.add(txtItems, BorderLayout.CENTER);

        // Botón inferior de despacho
        JButton btnCompletado = new JButton("MARCAR COMPLETADO");
        btnCompletado.setBackground(new Color(0x1E, 0x39, 0x32)); // Verde Esmeralda
        btnCompletado.setForeground(Color.WHITE);
        btnCompletado.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCompletado.setPreferredSize(new Dimension(220, 40));
        btnCompletado.addActionListener(e -> fachada.getCocina().despacharPedido(pedido.getIdPedido()));

        tarjeta.add(btnCompletado, BorderLayout.SOUTH);

        return tarjeta;
    }
}
