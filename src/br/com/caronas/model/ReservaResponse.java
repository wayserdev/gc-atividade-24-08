package br.com.caronas.model;

import java.io.Serializable;

/**
 * DTO de resposta para operações de Reserva (GET /reservas, POST /reservas, etc.).
 * Evita expor dados sensíveis e fornece informações resumidas e seguras.
 */
public class ReservaResponse implements Serializable {
    private String id;
    private String caronaId;
    private String passageiroId;
    private String passageiroNome;
    private int vagasSolicitadas;
    private String pontoEmbarque;
    private String status;
    private String criadoEm;
    private String mensagem;

    public ReservaResponse() {
    }

    public ReservaResponse(String id, String caronaId, String passageiroId, String passageiroNome,
                           int vagasSolicitadas, String pontoEmbarque, String status, String criadoEm) {
        this.id = id;
        this.caronaId = caronaId;
        this.passageiroId = passageiroId;
        this.passageiroNome = passageiroNome;
        this.vagasSolicitadas = vagasSolicitadas;
        this.pontoEmbarque = pontoEmbarque;
        this.status = status;
        this.criadoEm = criadoEm;
    }

    public ReservaResponse(String id, String caronaId, String passageiroId, String passageiroNome,
                           int vagasSolicitadas, String pontoEmbarque, String status, String criadoEm, String mensagem) {
        this(id, caronaId, passageiroId, passageiroNome, vagasSolicitadas, pontoEmbarque, status, criadoEm);
        this.mensagem = mensagem;
    }

    public static ReservaResponse fromReserva(Reserva r, String passageiroNome) {
        if (r == null) return null;
        return new ReservaResponse(
                r.getId(),
                r.getCarona_id(),
                r.getPassageiro_id(),
                passageiroNome,
                r.getVagas_solicitadas(),
                r.getPonto_embarque(),
                r.getStatus(),
                r.getCriado_em()
        );
    }

    public static ReservaResponse fromReserva(Reserva r, String passageiroNome, String mensagem) {
        if (r == null) return null;
        return new ReservaResponse(
                r.getId(),
                r.getCarona_id(),
                r.getPassageiro_id(),
                passageiroNome,
                r.getVagas_solicitadas(),
                r.getPonto_embarque(),
                r.getStatus(),
                r.getCriado_em(),
                mensagem
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPassageiroNome() {
        return passageiroNome;
    }

    public void setPassageiroNome(String passageiroNome) {
        this.passageiroNome = passageiroNome;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(String criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"id\": ").append(jsonVal(id)).append(",\n");
        sb.append("  \"carona_id\": ").append(jsonVal(caronaId)).append(",\n");
        sb.append("  \"passageiro_id\": ").append(jsonVal(passageiroId)).append(",\n");
        sb.append("  \"passageiro_nome\": ").append(jsonVal(passageiroNome)).append(",\n");
        sb.append("  \"vagas_solicitadas\": ").append(vagasSolicitadas).append(",\n");
        sb.append("  \"ponto_embarque\": ").append(jsonVal(pontoEmbarque)).append(",\n");
        sb.append("  \"status\": ").append(jsonVal(status)).append(",\n");
        sb.append("  \"criado_em\": ").append(jsonVal(criadoEm));
        if (mensagem != null) {
            sb.append(",\n  \"mensagem\": ").append(jsonVal(mensagem));
        }
        sb.append("\n}");
        return sb.toString();
    }

    private String jsonVal(String val) {
        if (val == null) return "null";
        return "\"" + val.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
