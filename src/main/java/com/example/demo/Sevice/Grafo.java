package com.example.demo.Sevice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Arista;
import com.example.demo.Model.Nodo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class Grafo {
    private Nodo[] nodos;
    private Arista[] aristas;
    private int numNodos;
    private int numAristas;

    public Grafo(@Value("${grafo.max-nodos}") int maxNodos,
                 @Value("${grafo.max-aristas}") int maxAristas) {
        nodos = new Nodo[maxNodos];
        aristas = new Arista[maxAristas];
        numNodos = 0;
        numAristas = 0;
    }

    public boolean agregarNodo(Nodo nodo) {
        if (numNodos < nodos.length) {
            nodos[numNodos++] = nodo;
            return true;
        }
        return false;
    }

    public boolean agregarArista(Arista arista) {
        if (numAristas < aristas.length) {
            aristas[numAristas++] = arista;
            return true;
        }
        return false;
    }

    public Nodo[] dijkstra(Nodo origen, Nodo destino) {
        int[] distancias = new int[nodos.length];
        Arrays.fill(distancias, Integer.MAX_VALUE);
        distancias[obtenerIndice(origen)] = 0;

        boolean[] visitados = new boolean[nodos.length];
        Nodo[] predecesores = new Nodo[nodos.length];

        for (int i = 0; i < numNodos; i++) {
            int u = minDistancia(distancias, visitados);
            visitados[u] = true;

            for (int v = 0; v < numNodos; v++) {
                if (!visitados[v] && existeArista(u, v)) {
                    int newDist = distancias[u] + pesoArista(u, v);
                    if (newDist < distancias[v]) {
                        distancias[v] = newDist;
                        predecesores[v] = nodos[u];
                    }
                }
            }
        }
        return construirRuta(predecesores, origen, destino);
    }

    private int obtenerIndice(Nodo nodo) {
        for (int i = 0; i < numNodos; i++) {
            if (nodos[i].getId() == nodo.getId()) {
                return i;
            }
        }
        return -1;
    }

    private int minDistancia(int[] distancias, boolean[] visitados) {
        int min = Integer.MAX_VALUE, minIndice = -1;

        for (int v = 0; v < nodos.length; v++) {
            if (!visitados[v] && distancias[v] <= min) {
                min = distancias[v];
                minIndice = v;
            }
        }
        return minIndice;
    }

    private boolean existeArista(int u, int v) {
        for (Arista arista : aristas) {
            if (arista != null && arista.getOrigen() != null && arista.getDestino() != null &&
                arista.getOrigen().getId() == nodos[u].getId() && arista.getDestino().getId() == nodos[v].getId()) {
                return true;
            }
        }
        return false;
    }

    private int pesoArista(int u, int v) {
        for (Arista arista : aristas) {
            if (arista != null && arista.getOrigen().equals(nodos[u]) && arista.getDestino().equals(nodos[v])) {
                return arista.getPeso();
            }
        }
        return Integer.MAX_VALUE;
    }


    private Nodo[] construirRuta(Nodo[] predecesores, Nodo origen, Nodo destino) {
        List<Nodo> path = new ArrayList<>();
        Nodo step = destino;

        // Revisa si hay un camino disponible
        if (predecesores[obtenerIndice(destino)] == null && !origen.equals(destino)) {
            return new Nodo[0]; // No hay camino
        }

        path.add(destino);
        while (predecesores[obtenerIndice(step)] != null) {
            step = predecesores[obtenerIndice(step)];
            path.add(step);
        }
        // Asegurarse de que el origen esté en el camino
        if (!path.contains(origen)) {
            return new Nodo[0];
        }

        // Invertir el camino para tenerlo en el orden correcto
        Collections.reverse(path);

        return path.toArray(new Nodo[path.size()]);
    }




    public Nodo[] calcularRutaMasCorta(int origenId, int destinoId) {
        Nodo origen = buscarNodoPorId(origenId);
        Nodo destino = buscarNodoPorId(destinoId);
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Nodo origen o destino no encontrado");
        }

        Nodo[] ruta = dijkstra(origen, destino);

        if (ruta.length <= 1) {
            if (origen.equals(destino)) {
                return new Nodo[] { origen };
            } else {
                throw new IllegalArgumentException("No se encontró una ruta válida entre los nodos especificados");
            }
        }

        return ruta;
    }

    public Nodo buscarNodoPorId(int id) {
        for (Nodo nodo : nodos) {
            if (nodo != null && nodo.getId() == id) {
                return nodo;
            }
        }
        throw new IllegalArgumentException("Nodo con ID: " + id + " no encontrado");
    }

    public Arista[] getAristas() {
        return aristas;
    }

    public int getNumNodos() {
        return numNodos;
    }

    public Nodo[] getNodos() {
        return nodos;
    }

    public int getNumAristas() {
        return numAristas;
    }

    public boolean eliminarNodo(int id) {
        int indice = obtenerIndicePorId(id);
        if (indice == -1) {
            return false; // Nodo no encontrado
        }

        // Eliminar todas las aristas asociadas con este nodo
        eliminarAristasPorNodoId(id);

        // Desplazar los nodos siguientes una posición hacia atrás
        for (int i = indice; i < numNodos - 1; i++) {
            nodos[i] = nodos[i + 1];
        }
        nodos[numNodos - 1] = null;
        numNodos--;

        return true;
    }

    private int obtenerIndicePorId(int id) {
        for (int i = 0; i < numNodos; i++) {
            if (nodos[i].getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private void eliminarAristasPorNodoId(int nodoId) {
        for (int i = 0; i < numAristas; i++) {
            if (aristas[i].getOrigen().getId() == nodoId || aristas[i].getDestino().getId() == nodoId) {
                // Desplazar las aristas siguientes una posición hacia atrás
                for (int j = i; j < numAristas - 1; j++) {
                    aristas[j] = aristas[j + 1];
                }
                aristas[numAristas - 1] = null;
                numAristas--;
                i--; // Volver a revisar la misma posición, ya que se ha desplazado un elemento hacia atrás
            }
        }
    }

    public boolean eliminarArista(int id) {
        for (int i = 0; i < numAristas; i++) {
            if (aristas[i].getId() == id) {
                // Desplazar las aristas siguientes una posición hacia atrás
                for (int j = i; j < numAristas - 1; j++) {
                    aristas[j] = aristas[j + 1];
                }
                aristas[numAristas - 1] = null;
                numAristas--;
                return true;
            }
        }
        return false; // Arista no encontrada
    }



}
