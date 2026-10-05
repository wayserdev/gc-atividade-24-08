package br.com.caronas.model;

import java.io.Serializable;

public class Avaliacao implements Serializable {
    private String id;
    private String carona_id;
    private String avaliador_id;
    private String avaliador_nome;
    private String avaliador_foto;
    private String avaliado_id;
    private String avaliado_nome;
    private int nota; // 1 a 5
    private String comentario;
    private String tipo; // "COMO_MOTORISTA" ou "COMO_PASSAGEIRO"
    private String criado_em;

    public Avaliacao() {}

    public Avaliacao(String id, String carona_id, String avaliador_id, String avaliador_nome,
                     String avaliador_foto, String avaliado_id, String avaliado_nome,
                     int nota, String comentario, String tipo, String criado_em) {
        this.id = id;
        this.carona_id = carona_id;
        this.avaliador_id = avaliador_id;
        this.avaliador_nome = avaliador_nome;
        this.avaliador_foto = avaliador_foto;
        this.avaliado_id = avaliado_id;
        this.avaliado_nome = avaliado_nome;
        this.nota = Math.max(1, Math.min(5, nota));
        this.comentario = comentario;
        this.tipo = tipo;
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

    public String getAvaliador_id() {
        return avaliador_id;
    }

    public void setAvaliador_id(String avaliador_id) {
        this.avaliador_id = avaliador_id;
    }

    public String getAvaliador_nome() {
        return avaliador_nome;
    }

    public void setAvaliador_nome(String avaliador_nome) {
        this.avaliador_nome = avaliador_nome;
    }

    public String getAvaliador_foto() {
        return avaliador_foto;
    }

    public void setAvaliador_foto(String avaliador_foto) {
        this.avaliador_foto = avaliador_foto;
    }

    public String getAvaliado_id() {
        return avaliado_id;
    }

    public void setAvaliado_id(String avaliado_id) {
        this.avaliado_id = avaliado_id;
    }

    public String getAvaliado_nome() {
        return avaliado_nome;
    }

    public void setAvaliado_nome(String avaliado_nome) {
        this.avaliado_nome = avaliado_nome;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = Math.max(1, Math.min(5, nota));
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCriado_em() {
        return criado_em;
    }

    public void setCriado_em(String criado_em) {
        this.criado_em = criado_em;
    }

    public String getEstrelasFormatadas() {
        return nota + "/5 estrelas";
    }
}
