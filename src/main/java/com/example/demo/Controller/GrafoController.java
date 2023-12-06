package com.example.demo.Controller;

// ... other imports

import com.example.demo.Model.Arista;
import com.example.demo.Model.Nodo;
import com.example.demo.Sevice.Grafo;
import com.example.demo.Sevice.GrafoCSVService;
import com.example.demo.Sevice.NodoService;
import com.example.demo.Sevice.AristaService;  // Import the AristaService
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/grafo")
public class GrafoController {

    private final Grafo grafoService;
    private final GrafoCSVService grafoCSVService;
    private final NodoService nodoService;
    private final AristaService aristaService;

    @Autowired
    public GrafoController(Grafo grafoService, GrafoCSVService grafoCSVService, NodoService nodoService, AristaService aristaService) {
        this.grafoService = grafoService;
        this.grafoCSVService = grafoCSVService;
        this.nodoService = nodoService;  // Initialize the NodoService
        this.aristaService = aristaService;  // Initialize the AristaService
    }

//    @PostMapping("/nodos")
//    public ResponseEntity<?> createNodo(@RequestBody Nodo nodo) {
//        boolean agregado = grafoService.agregarNodo(nodo);
//        if (agregado) {
//            return ResponseEntity.ok(nodo);
//        } else {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }

    @PostMapping("/nodos")
    public ResponseEntity<?> createNodo(@RequestBody Nodo nodo) {
        Nodo savedNodo = nodoService.guardarNodo(nodo);  // Use NodoService to save the nodo
        if (savedNodo != null) {
            return ResponseEntity.ok(savedNodo);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }


//    @PostMapping("/aristas")
//    public ResponseEntity<Arista> agregarArista(@RequestBody Arista arista) {
//        boolean agregado = grafoService.agregarArista(arista);
//        if (agregado) {
//            return ResponseEntity.ok(arista);
//        } else {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }

    @PostMapping("/aristas")
    public ResponseEntity<Arista> agregarArista(@RequestBody Arista arista) {
        Arista savedArista = aristaService.guardarArista(arista);  // Use AristaService to save the arista
        if (savedArista != null) {
            return ResponseEntity.ok(savedArista);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/aristas")
    public ResponseEntity<Arista[]> obtenerAristas() {
        List<Arista> aristas = aristaService.obtenerTodasLasAristas();  // Use AristaService to get all aristas
        if (!aristas.isEmpty()) {
            return ResponseEntity.ok(aristas.toArray(new Arista[0]));
        } else {
            return ResponseEntity.noContent().build();
        }
    }


//    @GetMapping("/nodos")
//    public ResponseEntity<Nodo[]> obtenerNodos() {
//        Nodo[] nodos = grafoService.getNodos();
//        if (nodos != null) {
//            return ResponseEntity.ok(nodos);
//        } else {
//            return ResponseEntity.noContent().build();
//        }
//    }

    @GetMapping("/nodos")
    public ResponseEntity<Nodo[]> obtenerNodos() {
        List<Nodo> nodos = nodoService.obtenerTodosLosNodos();  // Use NodoService to get all nodos
        if (!nodos.isEmpty()) {
            return ResponseEntity.ok(nodos.toArray(new Nodo[0]));
        } else {
            return ResponseEntity.noContent().build();
        }
    }

//    @GetMapping("/aristas")
//    public ResponseEntity<Arista[]> obtenerAristas() {
//        Arista[] aristas = grafoService.getAristas();
//        if (aristas != null) {
//            return ResponseEntity.ok(aristas);
//        } else {
//            return ResponseEntity.noContent().build();
//        }
//    }

    @GetMapping("/ruta/{origenId}/{destinoId}")
    public ResponseEntity<?> calcularRuta(@PathVariable int origenId, @PathVariable int destinoId) {
        try {
            Nodo[] ruta = grafoService.calcularRutaMasCorta(origenId, destinoId);
            if (ruta != null) {
                return ResponseEntity.ok(ruta);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/exportar")
    public ResponseEntity<String> exportarNodosYAristas() {
        try {
            String csvData = grafoCSVService.exportarComoCSV();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=nodos_y_aristas.csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(csvData);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/importar")
    public ResponseEntity<?> importarNodosYAristas(@RequestParam("archivo") MultipartFile archivo) {
        try {
            grafoCSVService.importarDesdeCSV(archivo);
            return ResponseEntity.ok().body("{\"message\": \"Importación exitosa\"}");
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/nodos/{id}")
    public ResponseEntity<?> eliminarNodo(@PathVariable int id) {
        boolean eliminado = nodoService.eliminarNodo(id);
        if (eliminado) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/aristas/{id}")
    public ResponseEntity<?> eliminarArista(@PathVariable int id) {
        boolean eliminado = aristaService.eliminarArista(id);
        if (eliminado) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
