package br.com.caronas.model;

import java.io.Serializable;

public class Reserva implements Serializable {
    private String id;
    private String carona_id;
    private String passageiro_id;
    private String passageiro_nome;
    private String passageiro_email;
    private String passageiro_curso;
    private String passageiro_telefone;
    private String passageiro_foto;
    private String passageiro_avaliacao;

    private int vagas_solicitadas;
    private String ponto_embarque;
    private String status; // "PENDENTE", "ACEITA", "RECUSADA", "CANCELADA"
    private String criado_em;

    private Carona carona; // Vinculada para exibir resumo ao passageiro

    public Reserva() {}

    public Reserva(String id, String carona_id, String passageiro_id, String passageiro_nome,
                   String passageiro_email, String passageiro_curso, String passageiro_telefone,
                   String passageiro_foto, String passageiro_avaliacao, int vagas_solicitadas,
                   String ponto_embarque, String status, String criado_em) {
        this.id = id;
        this.carona_id = carona_id;
        this.passageiro_id = passageiro_id;
        this.passageiro_nome = passageiro_nome;
        this.passageiro_email = passageiro_email;
        this.passageiro_curso = passageiro_curso;
        this.passageiro_telefone = passageiro_telefone;
        this.passageiro_foto = passageiro_foto;
        this.passageiro_avaliacao = passageiro_avaliacao;
        this.vagas_solicitadas = vagas_solicitadas;
        this.ponto_embarque = ponto_embarque;
        this.status = status;
        this.criado_em = criado_em;
    }

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

    public String getPassageiro_nome() {
        return passageiro_nome;
    }

    public void setPassageiro_nome(String passageiro_nome) {
        this.passageiro_nome = passageiro_nome;
    }

    public String getPassageiro_email() {
        return passageiro_email;
    }

    public void setPassageiro_email(String passageiro_email) {
        this.passageiro_email = passageiro_email;
    }

    public String getPassageiro_curso() {
        return passageiro_curso;
    }

    public void setPassageiro_curso(String passageiro_curso) {
        this.passageiro_curso = passageiro_curso;
    }

    public String getPassageiro_telefone() {
        return passageiro_telefone;
    }

    public void setPassageiro_telefone(String passageiro_telefone) {
        this.passageiro_telefone = passageiro_telefone;
    }

    public String getPassageiro_foto() {
        return passageiro_foto;
    }

    public void setPassageiro_foto(String passageiro_foto) {
        this.passageiro_foto = passageiro_foto;
    }

    public String getPassageiro_avaliacao() {
        return passageiro_avaliacao;
    }

    public void setPassageiro_avaliacao(String passageiro_avaliacao) {
        this.passageiro_avaliacao = passageiro_avaliacao;
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

    public Carona getCarona() {
        return carona;
    }

    public void setCarona(Carona carona) {
        this.carona = carona;
    }

    public boolean isPendente() {
        return "PENDENTE".equalsIgnoreCase(status);
    }

    public boolean isAceita() {
        return "ACEITA".equalsIgnoreCase(status);
    }

    public boolean isRecusada() {
        return "RECUSADA".equalsIgnoreCase(status);
    }

    public boolean isCancelada() {
        return "CANCELADA".equalsIgnoreCase(status);
    }
}
