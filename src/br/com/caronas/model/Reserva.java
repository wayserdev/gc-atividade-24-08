package br.com.caronas.model;

import java.io.Serializable;

/**
 * Entidade Reserva representando a tabela 'reservas' (modelo_banco.sql).
 * Controla as solicitações de vagas feitas por passageiros para uma carona.
 */
public class Reserva implements Serializable, Cloneable {
    public static final String STATUS_PENDENTE = "PENDENTE";
    public static final String STATUS_ACEITA = "ACEITA";
    public static final String STATUS_RECUSADA = "RECUSADA";
    public static final String STATUS_CANCELADA = "CANCELADA";

    private String id;
    private String carona_id;
    private String passageiro_id;
    private int vagas_solicitadas;
    private String ponto_embarque;
    private String status;
    private String criado_em;

    public Reserva() {
        this.vagas_solicitadas = 1;
        this.status = STATUS_PENDENTE;
    }

    public Reserva(String id, String carona_id, String passageiro_id, int vagas_solicitadas,
                   String ponto_embarque, String status, String criado_em) {
        this.id = id;
        this.carona_id = carona_id;
        this.passageiro_id = passageiro_id;
        this.vagas_solicitadas = vagas_solicitadas > 0 ? vagas_solicitadas : 1;
        this.ponto_embarque = ponto_embarque;
        this.status = status != null ? status : STATUS_PENDENTE;
        this.criado_em = criado_em;
    }

    public boolean isPendente() {
        return STATUS_PENDENTE.equalsIgnoreCase(this.status);
    }

    public boolean isAceita() {
        return STATUS_ACEITA.equalsIgnoreCase(this.status);
    }

    public boolean isRecusada() {
        return STATUS_RECUSADA.equalsIgnoreCase(this.status);
    }

    public boolean isCancelada() {
        return STATUS_CANCELADA.equalsIgnoreCase(this.status);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarona_id() {
        return carona_id;
    }

    public void setCarona_id(String carona_id) {
        this.carona_id = carona_id;
    }

    public String getPassageiro_id() {
        return passageiro_id;
    }

    public void setPassageiro_id(String passageiro_id) {
        this.passageiro_id = passageiro_id;
    }

    public int getVagas_solicitadas() {
        return vagas_solicitadas;
    }

    public void setVagas_solicitadas(int vagas_solicitadas) {
        this.vagas_solicitadas = vagas_solicitadas;
    }

    public String getPonto_embarque() {
        return ponto_embarque;
    }

    public void setPonto_embarque(String ponto_embarque) {
        this.ponto_embarque = ponto_embarque;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCriado_em() {
        return criado_em;
    }

    public void setCriado_em(String criado_em) {
        this.criado_em = criado_em;
    }

    @Override
    public Reserva clone() {
        try {
            return (Reserva) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Reserva(id, carona_id, passageiro_id, vagas_solicitadas, ponto_embarque, status, criado_em);
        }
    }

    public String toJson() {
        return "{\n"
                + "  \"id\": " + jsonVal(id) + ",\n"
                + "  \"carona_id\": " + jsonVal(carona_id) + ",\n"
                + "  \"passageiro_id\": " + jsonVal(passageiro_id) + ",\n"
                + "  \"vagas_solicitadas\": " + vagas_solicitadas + ",\n"
                + "  \"ponto_embarque\": " + jsonVal(ponto_embarque) + ",\n"
                + "  \"status\": " + jsonVal(status) + ",\n"
                + "  \"criado_em\": " + jsonVal(criado_em) + "\n"
                + "}";
    }

    private String jsonVal(String val) {
        if (val == null) return "null";
        return "\"" + val.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
