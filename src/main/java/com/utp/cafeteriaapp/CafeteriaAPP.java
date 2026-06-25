package com.utp.cafeteriaapp;

import com.formdev.flatlaf.FlatLightLaf;
import com.utp.cafeteriaapp.service.RestauranteFacade;
import com.utp.cafeteriaapp.view.VentanaCajaJFrame;
import com.utp.cafeteriaapp.view.VentanaCocinaJFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CafeteriaAPP {

    public static void main(String[] args) {
        // Inyectar y configurar la libreria Look and Feel de FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            // Personalización de los componente
            UIManager.put("Button.arc", 10); // Redondeado suave en botones
            UIManager.put("Component.arc", 10); // Redondeado en inputs y tablas
            UIManager.put("ScrollBar.thumbArc", 12); // Scrollbars minimalistas

        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Advertencia: No se pudo cargar el estilo FlatLaf, se usará la interfaz nativa.");
        }

        // Ejecutar el hilo de despacho de eventos de Swing de forma segura
        java.awt.EventQueue.invokeLater(() -> {
            // Carga el historial binario .dat si ya existía
            RestauranteFacade fachadaUnificada = new RestauranteFacade();

            // Levantar Pantalla 2: Monitor de la Cocina
            VentanaCocinaJFrame pantallaCocina = new VentanaCocinaJFrame(fachadaUnificada);
            pantallaCocina.setVisible(true);

            // Levantar Pantalla 1: Terminal del Cajero
            VentanaCajaJFrame pantallaCaja = new VentanaCajaJFrame(fachadaUnificada);
            pantallaCaja.setVisible(true);

            System.out.println(">>> Sistema de Cafetería iniciado con éxito.");
        });
    }
}
