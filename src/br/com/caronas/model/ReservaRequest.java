package br.com.caronas.model;

import java.io.Serializable;

/**
 * DTO para solicitação/criação de uma reserva (POST /reservas).
 */
public class ReservaRequest implements Serializable {
    private String caronaId;
    private String passageiroId;
    private int vagasSolicitadas;
    private String pontoEmbarque;

    public ReservaRequest() {
        this.vagasSolicitadas = 1;
    }

    public ReservaRequest(String caronaId) {
        this.caronaId = caronaId;
        this.vagasSolicitadas = 1;
    }

    public ReservaRequest(String caronaId, int vagasSolicitadas, String pontoEmbarque) {
        this.caronaId = caronaId;
        this.vagasSolicitadas = vagasSolicitadas;
        this.pontoEmbarque = pontoEmbarque;
    }

    public ReservaRequest(String caronaId, String passageiroId, int vagasSolicitadas, String pontoEmbarque) {
        this.caronaId = caronaId;
        this.passageiroId = passageiroId;
        this.vagasSolicitadas = vagasSolicitadas;
        this.pontoEmbarque = pontoEmbarque;
    }

    public String getCaronaId() {
        return caronaId;
    }

    public void setCaronaId(String caronaId) {
        this.caronaId = caronaId;
    }

    public String getPassageiroId() {
        return passageiroId;
    }

    public void setPassageiroId(String passageiroId) {
        this.passageiroId = passageiroId;
    }

    public int getVagasSolicitadas() {
        return vagasSolicitadas;
    }

    public void setVagasSolicitadas(int vagasSolicitadas) {
        this.vagasSolicitadas = vagasSolicitadas;
    }

    public String getPontoEmbarque() {
        return pontoEmbarque;
    }

    public void setPontoEmbarque(String pontoEmbarque) {
        this.pontoEmbarque = pontoEmbarque;
    }

    public String toJson() {
        return "{\n"
                + "  \"carona_id\": " + jsonVal(caronaId) + ",\n"
                + "  \"passageiro_id\": " + jsonVal(passageiroId) + ",\n"
                + "  \"vagas_solicitadas\": " + vagasSolicitadas + ",\n"
                + "  \"ponto_embarque\": " + jsonVal(pontoEmbarque) + "\n"
                + "}";
    }

    private String jsonVal(String val) {
        if (val == null) return "null";
        return "\"" + val.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
