package br.com.caronas.model;

import java.io.Serializable;

public class Campus implements Serializable {
    private String id;
    private String instituicao_id;
    private String instituicao_nome;
    private String nome;
    private String cidade;
    private String endereco_referencia;

    public Campus() {}

    public Campus(String id, String instituicao_id, String instituicao_nome, String nome, String cidade, String endereco_referencia) {
        this.id = id;
        this.instituicao_id = instituicao_id;
        this.instituicao_nome = instituicao_nome;
        this.nome = nome;
        this.cidade = cidade;
        this.endereco_referencia = endereco_referencia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstituicao_id() {
        return instituicao_id;
    }

    public void setInstituicao_id(String instituicao_id) {
        this.instituicao_id = instituicao_id;
    }

    public String getInstituicao_nome() {
        return instituicao_nome;
    }

    public void setInstituicao_nome(String instituicao_nome) {
        this.instituicao_nome = instituicao_nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEndereco_referencia() {
        return endereco_referencia;
    }

    public void setEndereco_referencia(String endereco_referencia) {
        this.endereco_referencia = endereco_referencia;
    }

    @Override
    public String toString() {
        return nome + " (" + (instituicao_nome != null ? instituicao_nome : "") + " - " + cidade + ")";
    }
}
