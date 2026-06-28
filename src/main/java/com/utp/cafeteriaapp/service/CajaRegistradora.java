package com.utp.cafeteriaapp.service;

import java.io.Serializable;

public class CajaRegistradora implements Serializable {

    private static final long serialVersionUID = 1L;

    private static CajaRegistradora instancia;
    private double totalDinero;

    // Constructor privado para evitar instanciación externa
    private CajaRegistradora() {
        this.totalDinero = 0.0;
    }

    // Retorna la instancia única de la caja registradora
    public static synchronized CajaRegistradora getInstancia() {
        if (instancia == null) {
            instancia = new CajaRegistradora();
        }
        return instancia;
    }

    // Suma el monto de una transacción al acumulado del día
    public synchronized void registrarIngreso(double monto) {
        if (monto > 0) {
            this.totalDinero += monto;
        }
    }

    // Getters y Setters
    public double getTotalDinero() {
        return totalDinero;
    }

    public void setTotalDinero(double totalDinero) {
        this.totalDinero = totalDinero;
    }
}
