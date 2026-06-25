package com.utp.cafeteriaapp.service;

import com.utp.cafeteriaapp.model.Pedido;
import com.utp.cafeteriaapp.model.Producto;
import com.utp.cafeteriaapp.repository.GestorPedidos;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class RestauranteFacade {

    private Cocina cocina;
    private CajaRegistradora caja;
    private List<Pedido> historialGeneral;
    private int contadorSiguientePedido;

    public RestauranteFacade() {
        this.cocina = new Cocina();
        this.caja = CajaRegistradora.getInstancia();
        // Carga el historial previo desde el archivo .dat de forma automática
        this.historialGeneral = GestorPedidos.cargarPedidos();

        // Determina el ID correlativo autoincremental correcto para el siguiente pedido
        this.contadorSiguientePedido = historialGeneral.size() + 1;

        // Sincroniza el dinero histórico de las ventas previas cargadas en la caja
        double saldoHistorico = 0.0;
        for (Pedido p : historialGeneral) {
            saldoHistorico += p.calcularTotal();
        }
        this.caja.setTotalDinero(saldoHistorico);
    }

    /**
     * Genera un nuevo objeto Pedido inicializado con el ID autoincremental
     * correcto.
     */
    public Pedido crearNuevoPedido() {
        return new Pedido(contadorSiguientePedido++);
    }

    /**
     * Procesa de forma atómica el flujo de facturación, persistencia y envío a
     * cocina.
     */
    public void procesarCobroYDespacho(Pedido pedido) {
        if (pedido == null || pedido.getListaProductos().isEmpty()) {
            return;
        }

        // 1. Registrar el ingreso financiero
        caja.registrarIngreso(pedido.calcularTotal());

        // 2. Incrementar contadores para el algoritmo "Top Ventas"
        for (int i = 0; i < pedido.getListaProductos().size(); i++) {
            Producto prod = pedido.getListaProductos().get(i);
            int cantidad = pedido.getListaCantidades().get(i);
            prod.incrementarVentas(cantidad);
        }

        // 3. Añadir al historial general y persistir en el archivo binario (.dat)
        historialGeneral.add(pedido);
        GestorPedidos.guardarPedidos(historialGeneral);

        // 4. Emitir el ticket físico simulado (.txt)
        GestorPedidos.emitirTicket(pedido);

        // 5. Transferir la orden a la cola de la cocina
        cocina.agregarPedido(pedido);
    }

    // Getters directos para conectar las ventanas Swing
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
