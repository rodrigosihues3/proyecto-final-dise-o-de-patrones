package com.utp.cafeteriaapp.service;

import com.utp.cafeteriaapp.model.Pedido;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class Cocina {

    private List<Pedido> colaPedidosPendientes;
    private List<Observador> observadores;

    public Cocina() {
        this.colaPedidosPendientes = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    // Métodos del patrón Observer
    public void enlazarObservador(Observador o) {
        if (!observadores.contains(o)) {
            observadores.add(o);
        }
    }

    public void desenlazarObservador(Observador o) {
        observadores.remove(o);
    }

    public void notificarObservadores() {
        for (Observador o : observadores) {
            o.actualizar();
        }
    }

    // Lógica de gestión de la cola FIFO
    public void agregarPedido(Pedido pedido) {
        pedido.setEstado("PENDIENTE");
        colaPedidosPendientes.add(pedido);
        notificarObservadores(); // Actualiza las pantallas en tiempo real
    }

    /**
     * Despacha un pedido de la cola activa por su ID.
     */
    public void despacharPedido(int idPedido) {
        Pedido pedidoEncontrado = null;
        for (Pedido p : colaPedidosPendientes) {
            if (p.getIdPedido() == idPedido) {
                pedidoEncontrado = p;
                break;
            }
        }

        if (pedidoEncontrado != null) {
            pedidoEncontrado.setEstado("COMPLETADO");
            colaPedidosPendientes.remove(pedidoEncontrado);
            notificarObservadores(); // Limpia la tarjeta del monitor de cocina
        }
    }

    // Getters
    public List<Pedido> getColaPedidosPendientes() {
        return colaPedidosPendientes;
    }
}
