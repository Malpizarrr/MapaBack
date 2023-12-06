package com.example.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "nodos") // Asegúrate de que el nombre de la tabla sea correcto
public class Nodo {

    @Id
    private int id;
    private double x, y;

    // Constructor por defecto necesario para JPA
    public Nodo() {
    }

    public Nodo(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    // Getters
    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
