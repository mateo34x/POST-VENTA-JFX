package com.example.tars01.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileEditor {
    public static void insertarValorEnLinea(String archivo, int linea, String nuevoValor) {
        List<String> lineasArchivo = new ArrayList<>();
        File file = new File(archivo);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error al crear el archivo: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String lineaActual;

            while ((lineaActual = reader.readLine()) != null) {
                lineasArchivo.add(lineaActual);
            }

            while (lineasArchivo.size() < linea) {
                lineasArchivo.add("");
            }

            lineasArchivo.set(linea - 1, nuevoValor);

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            for (String lineaTexto : lineasArchivo) {
                writer.write(lineaTexto);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }



    public static String leerLineaEspecifica(String archivo, int numeroLinea) {
        String linea = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            for (int i = 1; i <= numeroLinea; i++) {
                linea = reader.readLine();
                if (linea == null) {
                    return null;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        if (linea != null && !linea.trim().isEmpty()) {
            return linea.replace("\\n", "\n") + "\n"; // Reemplazar \n por saltos de línea reales y añadir salto de línea
        }

        return linea;
    }

}
