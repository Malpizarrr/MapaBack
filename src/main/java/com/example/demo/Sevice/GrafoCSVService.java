package com.example.demo.Sevice;

import com.example.demo.Model.Arista;
import com.example.demo.Model.Nodo;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class GrafoCSVService {

    private final Grafo grafoService;

    public GrafoCSVService(Grafo grafoService) {
        this.grafoService = grafoService;
    }

    public String exportarComoCSV() throws IOException {
        StringWriter stringWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(stringWriter);

        // Escribir línea de encabezado para nodos
        writer.writeNext(new String[]{"ID Nodo", "X", "Y"});

        // Escribir datos de nodos
        for (Nodo nodo : grafoService.getNodos()) {
            if (nodo != null) {
                writer.writeNext(new String[]{
                        String.valueOf(nodo.getId()),
                        String.valueOf(nodo.getX()),
                        String.valueOf(nodo.getY())
                });
            }
        }

        writer.writeNext(new String[]{"ID Arista", "Origen", "Destino", "Peso"});

        // Escribir datos de aristas
        for (Arista arista : grafoService.getAristas()) {
            if (arista != null) {
                writer.writeNext(new String[]{
                        String.valueOf(arista.getId()),
                        String.valueOf(arista.getOrigen().getId()),
                        String.valueOf(arista.getDestino().getId()),
                        String.valueOf(arista.getPeso())
                });
            }
        }

        writer.close();
        return stringWriter.toString();
    }

    public void importarDesdeCSV(MultipartFile archivo) throws IOException, CsvValidationException {
        CSVReader reader = new CSVReader(new InputStreamReader(archivo.getInputStream()));
        String[] linea;

        // Omitir la línea de encabezado
        reader.readNext();

        // Leer nodos
        while ((linea = reader.readNext()) != null && !linea[0].equals("Origen")) {
            Nodo nodo = new Nodo(Integer.parseInt(linea[0]), Double.parseDouble(linea[1]), Double.parseDouble(linea[2]));
            grafoService.agregarNodo(nodo);
        }

        // Leer aristas
        while ((linea = reader.readNext()) != null) {
            // Cambio aquí para manejar el ID
            Nodo origen = grafoService.buscarNodoPorId(Integer.parseInt(linea[1]));
            Nodo destino = grafoService.buscarNodoPorId(Integer.parseInt(linea[2]));
            if (origen != null && destino != null) {
                Arista arista = new Arista(Long.parseLong(linea[0]), origen, destino, Integer.parseInt(linea[3]));
                grafoService.agregarArista(arista);
            }
        }

        reader.close();
    }
}
