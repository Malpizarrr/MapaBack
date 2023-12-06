package com.example.demo.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "aristas")
public class Arista {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origen_id")
    private Nodo origen;

    @ManyToOne
    @JoinColumn(name = "destino_id")
    private Nodo destino;

    private int peso;

    public Arista() {
    }

    public Arista(Long id, Nodo origen, Nodo destino, int peso) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Nodo getOrigen() {
        return origen;
    }

    public void setOrigen(Nodo origen) {
        this.origen = origen;
    }

    public Nodo getDestino() {
        return destino;
    }

    public void setDestino(Nodo destino) {
        this.destino = destino;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }
}
