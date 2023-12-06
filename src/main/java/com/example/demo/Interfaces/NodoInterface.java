package com.example.demo.Interfaces;

import com.example.demo.Model.Nodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodoInterface extends JpaRepository<Nodo, Integer> {
}
