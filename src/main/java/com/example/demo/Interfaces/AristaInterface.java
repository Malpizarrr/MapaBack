package com.example.demo.Interfaces;

import com.example.demo.Model.Arista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AristaInterface extends JpaRepository<Arista, Integer> {
    List<Arista> findAllByOrigenIdOrDestinoId(int nodoId, int nodoId1);
}
