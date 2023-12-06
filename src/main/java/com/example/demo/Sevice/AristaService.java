package com.example.demo.Sevice;

import com.example.demo.Interfaces.AristaInterface;
import com.example.demo.Model.Arista;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AristaService {

    @Autowired
    private AristaInterface aristaRepository;

    public Arista guardarArista(Arista arista) {
        return aristaRepository.save(arista);
    }

    public List<Arista> obtenerTodasLasAristas() {
        return aristaRepository.findAll();
    }

    public Optional<Arista> obtenerAristaPorId(int id) {
        return aristaRepository.findById(id);
    }

    public boolean eliminarArista(int id) {
        if (!aristaRepository.existsById(id)) {
            return false;
        }

        aristaRepository.deleteById(id);
        return true;
    }


    public void eliminarAristasPorNodoId(int nodoId) {
        List<Arista> aristasParaEliminar = aristaRepository.findAllByOrigenIdOrDestinoId(nodoId, nodoId);

        aristaRepository.deleteAll(aristasParaEliminar);
    }
}
