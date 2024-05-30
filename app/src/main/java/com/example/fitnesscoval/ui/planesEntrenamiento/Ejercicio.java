package com.example.fitnesscoval.ui.planesEntrenamiento;

public class Ejercicio {
    private int id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String detallesConsejos;
    private String equipoNecesario;
    private int repeticionesSugeridas;

    public Ejercicio(int id, String nombre, String descripcion, String categoria, String detallesConsejos, String equipoNecesario, int repeticionesSugeridas) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.detallesConsejos = detallesConsejos;
        this.equipoNecesario = equipoNecesario;
        this.repeticionesSugeridas = repeticionesSugeridas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDetallesConsejos() {
        return detallesConsejos;
    }

    public void setDetallesConsejos(String detallesConsejos) {
        this.detallesConsejos = detallesConsejos;
    }

    public String getEquipoNecesario() {
        return equipoNecesario;
    }

    public void setEquipoNecesario(String equipoNecesario) {
        this.equipoNecesario = equipoNecesario;
    }

    public int getRepeticionesSugeridas() {
        return repeticionesSugeridas;
    }

    public void setRepeticionesSugeridas(int repeticionesSugeridas) {
        this.repeticionesSugeridas = repeticionesSugeridas;
    }
}

