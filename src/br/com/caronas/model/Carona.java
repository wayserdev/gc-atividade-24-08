package br.com.caronas.model;

import java.io.Serializable;

/**
 * Modelo mínimo da entidade Carona (DEV03 / modelo_banco.sql).
 * Utilizado para integração com DEV04 (validação de vagas, status e integridade).
 */
public class Carona implements Serializable, Cloneable {
    public static final String STATUS_AGENDADA = "AGENDADA";
    public static final String STATUS_EM_ANDAMENTO = "EM_ANDAMENTO";
    public static final String STATUS_FINALIZADA = "FINALIZADA";
    public static final String STATUS_CANCELADA = "CANCELADA";

    private String id;
    private String motorista_id;
    private String motorista_nome;
    private String veiculo_id;
    private String direcao;
    private String ponto_encontro;
    private String horario_saida;
    private int assentos_totais;
    private int assentos_disponiveis;
    private double valor_contribuicao;
    private String status;
    private String criado_em;

    public Carona() {
        this.status = STATUS_AGENDADA;
    }

    public Carona(String id, String motorista_id, String motorista_nome, int assentos_totais,
                  int assentos_disponiveis, String ponto_encontro, String horario_saida, String status) {
        this.id = id;
        this.motorista_id = motorista_id;
        this.motorista_nome = motorista_nome;
        this.assentos_totais = assentos_totais;
        this.assentos_disponiveis = assentos_disponiveis;
        this.ponto_encontro = ponto_encontro;
        this.horario_saida = horario_saida;
        this.status = status != null ? status : STATUS_AGENDADA;
    }

    public boolean isAgendada() {
        return STATUS_AGENDADA.equalsIgnoreCase(this.status);
    }

    public boolean isCancelada() {
        return STATUS_CANCELADA.equalsIgnoreCase(this.status);
    }

    public boolean temVagas(int solicitadas) {
        return this.assentos_disponiveis >= solicitadas && solicitadas > 0;
    }

    public synchronized boolean reservarVagas(int vagas) {
        if (temVagas(vagas)) {
            this.assentos_disponiveis -= vagas;
            return true;
        }
        return false;
    }

    public synchronized void devolverVagas(int vagas) {
        this.assentos_disponiveis = Math.min(this.assentos_totais, this.assentos_disponiveis + vagas);
    }

    // Getters and Setters
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

    public String getMotorista_nome() {
        return motorista_nome;
    }

    public void setMotorista_nome(String motorista_nome) {
        this.motorista_nome = motorista_nome;
    }

    public String getVeiculo_id() {
        return veiculo_id;
    }

    public void setVeiculo_id(String veiculo_id) {
        this.veiculo_id = veiculo_id;
    }

    public String getDirecao() {
        return direcao;
    }

    public void setDirecao(String direcao) {
        this.direcao = direcao;
    }

    public String getPonto_encontro() {
        return ponto_encontro;
    }

    public void setPonto_encontro(String ponto_encontro) {
        this.ponto_encontro = ponto_encontro;
    }

    public String getHorario_saida() {
        return horario_saida;
    }

    public void setHorario_saida(String horario_saida) {
        this.horario_saida = horario_saida;
    }

    public int getAssentos_totais() {
        return assentos_totais;
    }

    public void setAssentos_totais(int assentos_totais) {
        this.assentos_totais = assentos_totais;
    }

    public int getAssentos_disponiveis() {
        return assentos_disponiveis;
    }

    public void setAssentos_disponiveis(int assentos_disponiveis) {
        this.assentos_disponiveis = assentos_disponiveis;
    }

    public double getValor_contribuicao() {
        return valor_contribuicao;
    }

    public void setValor_contribuicao(double valor_contribuicao) {
        this.valor_contribuicao = valor_contribuicao;
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
    public Carona clone() {
        try {
            return (Carona) super.clone();
        } catch (CloneNotSupportedException e) {
            Carona c = new Carona(id, motorista_id, motorista_nome, assentos_totais, assentos_disponiveis, ponto_encontro, horario_saida, status);
            c.setVeiculo_id(veiculo_id);
            c.setDirecao(direcao);
            c.setValor_contribuicao(valor_contribuicao);
            c.setCriado_em(criado_em);
            return c;
        }
    }

    public String toJson() {
        return "{\n"
                + "  \"id\": " + jsonVal(id) + ",\n"
                + "  \"motorista_id\": " + jsonVal(motorista_id) + ",\n"
                + "  \"motorista_nome\": " + jsonVal(motorista_nome) + ",\n"
                + "  \"assentos_totais\": " + assentos_totais + ",\n"
                + "  \"assentos_disponiveis\": " + assentos_disponiveis + ",\n"
                + "  \"ponto_encontro\": " + jsonVal(ponto_encontro) + ",\n"
                + "  \"horario_saida\": " + jsonVal(horario_saida) + ",\n"
                + "  \"status\": " + jsonVal(status) + "\n"
                + "}";
    }

    private String jsonVal(String val) {
        if (val == null) return "null";
        return "\"" + val.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
