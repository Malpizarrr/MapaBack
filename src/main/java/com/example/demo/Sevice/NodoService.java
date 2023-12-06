package com.example.demo.Sevice;

import com.example.demo.Interfaces.NodoInterface;
import com.example.demo.Model.Nodo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NodoService {

    private final AristaService aristaService;

    @Autowired
    public NodoService(AristaService aristaService) {
        this.aristaService = aristaService;
    }

    @Autowired
    private NodoInterface nodoRepository;



    public Nodo guardarNodo(Nodo nodo) {
        return nodoRepository.save(nodo);
    }

    public List<Nodo> obtenerTodosLosNodos() {
        return nodoRepository.findAll();
    }

    public Optional<Nodo> obtenerNodoPorId(int id) {
        return nodoRepository.findById(id);
    }

    public boolean eliminarNodo(int id) {
        if (!nodoRepository.existsById(id)) {
            return false;
        }

        aristaService.eliminarAristasPorNodoId(id);

        nodoRepository.deleteById(id);
        return true;
    }


}
