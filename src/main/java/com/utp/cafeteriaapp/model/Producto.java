package com.utp.cafeteriaapp.model;

import java.io.Serializable;

public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private double precio;
    private String categoria; // "CAFÉS", "BEBIDAS FRÍAS", "POSTRES", "SANDWICHES"
    private int contadorVentas;

    // Constructor completo
    public Producto(String nombre, double precio, String categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.contadorVentas = 0;
    }

    // Método para el algoritmo de "Top Ventas"
    public void incrementarVentas(int cantidad) {
        if (cantidad > 0) {
            this.contadorVentas += cantidad;
        }
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getContadorVentas() {
        return contadorVentas;
    }

    public void setContadorVentas(int contadorVentas) {
        this.contadorVentas = contadorVentas;
    }

    @Override
    public String toString() {
        return nombre + " (S/. " + String.format("%.2f", precio) + ")";
    }
}
