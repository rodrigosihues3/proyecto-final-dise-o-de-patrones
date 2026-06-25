package com.utp.cafeteriaapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idPedido;
    private List<Producto> listaProductos;
    private List<Integer> listaCantidades;
    private String estado; // "PENDIENTE", "PREPARANDO", "COMPLETADO"
    private long marcaTiempoInicio; // Para el contador digital de la cocina

    public Pedido(int idPedido) {
        this.idPedido = idPedido;
        this.listaProductos = new ArrayList<>();
        this.listaCantidades = new ArrayList<>();
        this.estado = "PENDIENTE";
        this.marcaTiempoInicio = System.currentTimeMillis();
    }

    public void agregarProducto(Producto producto, int cantidad) {
        int index = listaProductos.indexOf(producto);
        if (index != -1) {
            // Si el producto ya existe en la orden, acumulamos la cantidad
            int nuevaCantidad = listaCantidades.get(index) + cantidad;
            listaCantidades.set(index, nuevaCantidad);
        } else {
            // Si es nuevo, lo añadimos a las listas paralelas
            listaProductos.add(producto);
            listaCantidades.add(cantidad);
        }
    }

    public void modificarCantidad(int index, int nuevaCantidad) {
        if (index >= 0 && index < listaProductos.size()) {
            if (nuevaCantidad <= 0) {
                eliminarProducto(index);
            } else {
                listaCantidades.set(index, nuevaCantidad);
            }
        }
    }

    public void eliminarProducto(int index) {
        if (index >= 0 && index < listaProductos.size()) {
            listaProductos.remove(index);
            listaCantidades.remove(index);
        }
    }

    public double calcularTotal() {
        double total = 0.0;
        for (int i = 0; i < listaProductos.size(); i++) {
            total += listaProductos.get(i).getPrecio() * listaCantidades.get(i);
        }
        return total;
    }

    // Getters y Setters
    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public List<Integer> getListaCantidades() {
        return listaCantidades;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getMarcaTiempoInicio() {
        return marcaTiempoInicio;
    }

    public void setMarcaTiempoInicio(long marcaTiempoInicio) {
        this.marcaTiempoInicio = marcaTiempoInicio;
    }
}
