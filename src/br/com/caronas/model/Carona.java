package br.com.caronas.model;

import java.io.Serializable;

public class Carona implements Serializable {
    private String id;
    private String motorista_id;
    private String motorista_nome;
    private String motorista_foto;
    private String motorista_telefone;
    private String motorista_curso;
    private String motorista_avaliacao;

    private String veiculo_id;
    private String veiculo_modelo;
    private String veiculo_placa;
    private int veiculo_capacidade;

    private String direcao; // "BAIRRO_PARA_CAMPUS" ou "CAMPUS_PARA_BAIRRO"
    private String bairro_id;
    private String bairro_nome;
    private String campus_id;
    private String campus_nome;
    private String instituicao_nome;
    private String cidade;

    private String ponto_encontro;
    private String horario_saida; // Ex: "2026-08-30 07:30" ou formatado
    private int assentos_totais;
    private int assentos_disponiveis;
    private double valor_contribuicao;
    private String status; // "AGENDADA", "EM_ANDAMENTO", "FINALIZADA", "CANCELADA"
    private String criado_em;

    public Carona() {}

    public Carona(String id, String motorista_id, String motorista_nome, String motorista_foto, String motorista_telefone,
                  String motorista_curso, String motorista_avaliacao, String veiculo_id, String veiculo_modelo,
                  String veiculo_placa, int veiculo_capacidade, String direcao, String bairro_id, String bairro_nome,
                  String campus_id, String campus_nome, String instituicao_nome, String cidade, String ponto_encontro,
                  String horario_saida, int assentos_totais, int assentos_disponiveis, double valor_contribuicao,
                  String status, String criado_em) {
        this.id = id;
        this.motorista_id = motorista_id;
        this.motorista_nome = motorista_nome;
        this.motorista_foto = motorista_foto;
        this.motorista_telefone = motorista_telefone;
        this.motorista_curso = motorista_curso;
        this.motorista_avaliacao = motorista_avaliacao;
        this.veiculo_id = veiculo_id;
        this.veiculo_modelo = veiculo_modelo;
        this.veiculo_placa = veiculo_placa;
        this.veiculo_capacidade = veiculo_capacidade;
        this.direcao = direcao;
        this.bairro_id = bairro_id;
        this.bairro_nome = bairro_nome;
        this.campus_id = campus_id;
        this.campus_nome = campus_nome;
        this.instituicao_nome = instituicao_nome;
        this.cidade = cidade;
        this.ponto_encontro = ponto_encontro;
        this.horario_saida = horario_saida;
        this.assentos_totais = assentos_totais;
        this.assentos_disponiveis = assentos_disponiveis;
        this.valor_contribuicao = valor_contribuicao;
        this.status = status;
        this.criado_em = criado_em;
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

    public String getMotorista_nome() {
        return motorista_nome;
    }

    public void setMotorista_nome(String motorista_nome) {
        this.motorista_nome = motorista_nome;
    }

    public String getMotorista_foto() {
        return motorista_foto;
    }

    public void setMotorista_foto(String motorista_foto) {
        this.motorista_foto = motorista_foto;
    }

    public String getMotorista_telefone() {
        return motorista_telefone;
    }

    public void setMotorista_telefone(String motorista_telefone) {
        this.motorista_telefone = motorista_telefone;
    }

    public String getMotorista_curso() {
        return motorista_curso;
    }

    public void setMotorista_curso(String motorista_curso) {
        this.motorista_curso = motorista_curso;
    }

    public String getMotorista_avaliacao() {
        return motorista_avaliacao;
    }

    public void setMotorista_avaliacao(String motorista_avaliacao) {
        this.motorista_avaliacao = motorista_avaliacao;
    }

    public String getVeiculo_id() {
        return veiculo_id;
    }

    public void setVeiculo_id(String veiculo_id) {
        this.veiculo_id = veiculo_id;
    }

    public String getVeiculo_modelo() {
        return veiculo_modelo;
    }

    public void setVeiculo_modelo(String veiculo_modelo) {
        this.veiculo_modelo = veiculo_modelo;
    }

    public String getVeiculo_placa() {
        return veiculo_placa;
    }

    public void setVeiculo_placa(String veiculo_placa) {
        this.veiculo_placa = veiculo_placa;
    }

    public int getVeiculo_capacidade() {
        return veiculo_capacidade;
    }

    public void setVeiculo_capacidade(int veiculo_capacidade) {
        this.veiculo_capacidade = veiculo_capacidade;
    }

    public String getDirecao() {
        return direcao;
    }

    public void setDirecao(String direcao) {
        this.direcao = direcao;
    }

    public String getBairro_id() {
        return bairro_id;
    }

    public void setBairro_id(String bairro_id) {
        this.bairro_id = bairro_id;
    }

    public String getBairro_nome() {
        return bairro_nome;
    }

    public void setBairro_nome(String bairro_nome) {
        this.bairro_nome = bairro_nome;
    }

    public String getCampus_id() {
        return campus_id;
    }

    public void setCampus_id(String campus_id) {
        this.campus_id = campus_id;
    }

    public String getCampus_nome() {
        return campus_nome;
    }

    public void setCampus_nome(String campus_nome) {
        this.campus_nome = campus_nome;
    }

    public String getInstituicao_nome() {
        return instituicao_nome;
    }

    public void setInstituicao_nome(String instituicao_nome) {
        this.instituicao_nome = instituicao_nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
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

    public String getHorarioSaida() {
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

    public String getOrigemFormatada() {
        if ("CAMPUS_PARA_BAIRRO".equalsIgnoreCase(direcao)) {
            return (campus_nome != null ? campus_nome : "Campus") + " (" + (instituicao_nome != null ? instituicao_nome : "") + ")";
        } else {
            return (bairro_nome != null ? bairro_nome : "Bairro") + " (" + (cidade != null ? cidade : "") + ")";
        }
    }

    public String getDestinoFormatado() {
        if ("CAMPUS_PARA_BAIRRO".equalsIgnoreCase(direcao)) {
            return (bairro_nome != null ? bairro_nome : "Bairro") + " (" + (cidade != null ? cidade : "") + ")";
        } else {
            return (campus_nome != null ? campus_nome : "Campus") + " (" + (instituicao_nome != null ? instituicao_nome : "") + ")";
        }
    }

    public String getDirecaoLabel() {
        if ("CAMPUS_PARA_BAIRRO".equalsIgnoreCase(direcao)) {
            return "Campus -> Bairro";
        }
        return "Bairro -> Campus";
    }

    public boolean isAgendada() {
        return "AGENDADA".equalsIgnoreCase(status);
    }

    public boolean isEmAndamento() {
        return "EM_ANDAMENTO".equalsIgnoreCase(status);
    }

    public boolean isFinalizada() {
        return "FINALIZADA".equalsIgnoreCase(status);
    }

    public boolean isCancelada() {
        return "CANCELADA".equalsIgnoreCase(status);
    }
}
