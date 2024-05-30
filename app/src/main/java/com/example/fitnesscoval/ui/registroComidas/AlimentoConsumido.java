package com.example.fitnesscoval.ui.registroComidas;

public class AlimentoConsumido {
    private int idUsuario;
    private int idTipoComida;
    private int idAlimento;
    private double cantidadGramos;
    private String fechaConsumo;

    // Constructor
    public AlimentoConsumido(int idUsuario, int idTipoComida, int idAlimento, double cantidadGramos, String fechaConsumo) {
        this.idUsuario = idUsuario;
        this.idTipoComida = idTipoComida;
        this.idAlimento = idAlimento;
        this.cantidadGramos = cantidadGramos;
        this.fechaConsumo = fechaConsumo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdTipoComida() {
        return idTipoComida;
    }

    public void setIdTipoComida(int idTipoComida) {
        this.idTipoComida = idTipoComida;
    }

    public int getIdAlimento() {
        return idAlimento;
    }

    public void setIdAlimento(int idAlimento) {
        this.idAlimento = idAlimento;
    }

    public double getCantidadGramos() {
        return cantidadGramos;
    }

    public void setCantidadGramos(double cantidadGramos) {
        this.cantidadGramos = cantidadGramos;
    }

    public String getFechaConsumo() {
        return fechaConsumo;
    }

    public void setFechaConsumo(String fechaConsumo) {
        this.fechaConsumo = fechaConsumo;
    }
}
