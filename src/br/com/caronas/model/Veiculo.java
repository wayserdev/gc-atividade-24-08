package br.com.caronas.model;

import java.io.Serializable;

public class Veiculo implements Serializable {
    private String id;
    private String motorista_id;
    private String modelo;
    private String placa;
    private int capacidade_assentos;

    public Veiculo() {}

    public Veiculo(String id, String motorista_id, String modelo, String placa, int capacidade_assentos) {
        this.id = id;
        this.motorista_id = motorista_id;
        this.modelo = modelo;
        this.placa = placa;
        this.capacidade_assentos = capacidade_assentos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMotorista_id() {
        return motorista_id;
    }

    public void setMotorista_id(String motorista_id) {
        this.motorista_id = motorista_id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getCapacidade_assentos() {
        return capacidade_assentos;
    }

    public void setCapacidade_assentos(int capacidade_assentos) {
        this.capacidade_assentos = capacidade_assentos;
    }

    @Override
    public String toString() {
        return modelo + " (" + placa + ") - " + capacidade_assentos + " assentos";
    }
}
