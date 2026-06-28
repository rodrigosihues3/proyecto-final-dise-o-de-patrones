package com.utp.cafeteriaapp.service;

import com.utp.cafeteriaapp.model.Pedido;
import com.utp.cafeteriaapp.model.Producto;
import com.utp.cafeteriaapp.repository.GestorPedidos;
import java.util.List;

public class RestauranteFacade {

    private Cocina cocina;
    private CajaRegistradora caja;
    private List<Pedido> historialGeneral;
    private int contadorSiguientePedido;

    public RestauranteFacade() {
        this.cocina = new Cocina();
        this.caja = CajaRegistradora.getInstancia();
        // Carga el historial previo desde el archivo .dat
        this.historialGeneral = GestorPedidos.cargarPedidos();

        // Determina el ID para el siguiente pedido
        this.contadorSiguientePedido = historialGeneral.size() + 1;

        // Sincroniza el dinero histórico de las ventas previas cargadas en la caja
        double saldoHistorico = 0.0;
        for (Pedido p : historialGeneral) {
            saldoHistorico += p.calcularTotal();
        }
        this.caja.setTotalDinero(saldoHistorico);
    }

    // Genera un nuevo objeto Pedido inicializado con el ID autoincremental
    public Pedido crearNuevoPedido() {
        return new Pedido(contadorSiguientePedido++);
    }

    public void procesarCobroYDespacho(Pedido pedido) {
        if (pedido == null || pedido.getListaProductos().isEmpty()) {
            return;
        }

        // Registrar el ingreso financiero
        caja.registrarIngreso(pedido.calcularTotal());

        // Incrementar contadores de ventas de productos
        for (int i = 0; i < pedido.getListaProductos().size(); i++) {
            Producto prod = pedido.getListaProductos().get(i);
            int cantidad = pedido.getListaCantidades().get(i);
            prod.incrementarVentas(cantidad);
        }

        // Añade al historial y persistir en el archivo binario .dat
        historialGeneral.add(pedido);
        GestorPedidos.guardarPedidos(historialGeneral);

        // Emite el ticket físico simulado (.txt)
        GestorPedidos.emitirTicket(pedido);

        // Transfiere la orden a la cola de la cocina
        cocina.agregarPedido(pedido);
    }

    // Getters para conectar las ventanas Swing
    public Cocina getCocina() {
        return cocina;
    }

    public CajaRegistradora getCaja() {
        return caja;
    }

    public List<Pedido> getHistorialGeneral() {
        return historialGeneral;
    }
}
