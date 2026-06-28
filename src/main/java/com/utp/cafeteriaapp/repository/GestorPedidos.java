package com.utp.cafeteriaapp.repository;

import com.utp.cafeteriaapp.model.Pedido;
import com.utp.cafeteriaapp.model.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorPedidos {

    private static final String ARCHIVO_DAT = "historial_pedidos.dat";

    // Guarda la lista completa de pedidos en un archivo binario serializado.
    public static void guardarPedidos(List<Pedido> pedidos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_DAT))) {
            oos.writeObject(pedidos);
        } catch (IOException e) {
            System.err.println("Error crítico al persistir datos en .dat: " + e.getMessage());
        }
    }

    // Recupera la lista de pedidos desde el archivo binario. Si no existe, retorna una lista vacía.
    public static List<Pedido> cargarPedidos() {
        File file = new File(ARCHIVO_DAT);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Pedido>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar el archivo de persistencia, iniciando lista limpia: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Genera un archivo de texto plano (.txt) simulando la impresión de un ticket de venta físico.
    public static void emitirTicket(Pedido pedido) {
        String nombreArchivo = "Ticket_Pedido_" + pedido.getIdPedido() + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            writer.write("========================================\n");
            writer.write("             UTP CAFETERIA              \n");
            writer.write("========================================\n");
            writer.write("Pedido ID : " + pedido.getIdPedido() + "\n");
            writer.write("Estado    : " + pedido.getEstado() + "\n");
            writer.write("----------------------------------------\n");
            writer.write(String.format("%-20s %-5s %-10s\n", "Producto", "Cant", "Subtotal"));
            writer.write("----------------------------------------\n");

            List<Producto> productos = pedido.getListaProductos();
            List<Integer> cantidades = pedido.getListaCantidades();

            for (int i = 0; i < productos.size(); i++) {
                Producto prod = productos.get(i);
                int cant = cantidades.get(i);
                double subtotal = prod.getPrecio() * cant;

                // Si el nombre es muy largo, lo recorta para no romper las columnas del ticket
                String nombreCorto = prod.getNombre().length() > 18
                        ? prod.getNombre().substring(0, 16) + ".."
                        : prod.getNombre();

                writer.write(String.format("%-20s %-5d S/.%-10.2f\n", nombreCorto, cant, subtotal));
            }

            writer.write("----------------------------------------\n");
            writer.write(String.format("TOTAL A PAGAR:           S/.%.2f\n", pedido.calcularTotal()));
            writer.write("========================================\n");
            writer.write("       ¡Gracias por su preferencia!     \n");
            writer.write("========================================\n");

        } catch (IOException e) {
            System.err.println("Error al generar el ticket de texto: " + e.getMessage());
        }
    }
}
