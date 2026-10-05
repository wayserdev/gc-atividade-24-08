package br.com.caronas.model;

import java.io.Serializable;

public class Bairro implements Serializable {
    private String id;
    private String nome;
    private String cidade;

    public Bairro() {}

    public Bairro(String id, String nome, String cidade) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return nome + " (" + cidade + ")";
    }
}
