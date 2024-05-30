package com.example.fitnesscoval.ui.registroComidas;

public class Alimento {
    private int id;
    private String nombre;
    private int calorias_cien;
    private double proteinas;
    private double carbohidratos;
    private double grasas;

    public Alimento(int id, String nombre, int calorias_cien, double proteinas, double carbohidratos, double grasas) {
        this.id = id;
        this.nombre = nombre;
        this.calorias_cien = calorias_cien;
        this.proteinas = proteinas;
        this.carbohidratos = carbohidratos;
        this.grasas = grasas;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCalorias_cien() {
        return calorias_cien;
    }

    public double getProteinas() {
        return proteinas;
    }

    public double getCarbohidratos() {
        return carbohidratos;
    }

    public double getGrasas() {
        return grasas;
    }
}
