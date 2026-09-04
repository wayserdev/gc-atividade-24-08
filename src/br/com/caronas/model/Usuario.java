package br.com.caronas.model;

import java.io.Serializable;

public class Usuario implements Serializable, Cloneable {
    private String id;
    private String instituicao_id;
    private String instituicao_nome;
    private String nome_completo;
    private String email;
    /**
     * Senha armazenada somente como hash. Nunca mantenha a senha em texto
     * puro no modelo de usuário.
     */
    private String senhaHash;
    private String nivel; // "ALUNO" ou "ADMIN"
    private String telefone;
    private String curso;
    private String foto_url;
    private String media_avaliacao; // ex: "4.85"
    private String criado_em; // ISO 8601 ex: "2026-08-27T21:00:00.000Z"

    public Usuario() {
        this.nivel = "ALUNO";
        this.media_avaliacao = "0.00";
    }

    public Usuario(String id, String instituicao_id, String instituicao_nome, String nome_completo,
                   String email, String senhaHash, String nivel, String telefone, String curso,
                   String foto_url, String media_avaliacao, String criado_em) {
        this.id = id;
        this.instituicao_id = instituicao_id;
        this.instituicao_nome = instituicao_nome;
        this.nome_completo = nome_completo;
        this.email = email;
        this.senhaHash = senhaHash;
        this.nivel = nivel != null ? nivel : "ALUNO";
        this.telefone = telefone;
        this.curso = curso;
        this.foto_url = foto_url;
        this.media_avaliacao = media_avaliacao != null ? media_avaliacao : "0.00";
        this.criado_em = criado_em;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.nivel);
    }

    public boolean isAluno() {
        return "ALUNO".equalsIgnoreCase(this.nivel);
    }

    // Getters and Setters
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

    public String getNome_completo() {
        return nome_completo;
    }

    public void setNome_completo(String nome_completo) {
        this.nome_completo = nome_completo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Compatibilidade com o contrato antigo do projeto. O retorno é o hash,
     * nunca a senha original.
     */
    @Deprecated
    public String getSenha() {
        return senhaHash;
    }

    @Deprecated
    public void setSenha(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getFoto_url() {
        return foto_url;
    }

    public void setFoto_url(String foto_url) {
        this.foto_url = foto_url;
    }

    public String getMedia_avaliacao() {
        return media_avaliacao;
    }

    public void setMedia_avaliacao(String media_avaliacao) {
        this.media_avaliacao = media_avaliacao;
    }

    public String getCriado_em() {
        return criado_em;
    }

    public void setCriado_em(String criado_em) {
        this.criado_em = criado_em;
    }

    @Override
    public Usuario clone() {
        try {
            return (Usuario) super.clone();
        } catch (CloneNotSupportedException e) {
            Usuario u = new Usuario();
            u.id = this.id;
            u.instituicao_id = this.instituicao_id;
            u.instituicao_nome = this.instituicao_nome;
            u.nome_completo = this.nome_completo;
            u.email = this.email;
            u.senhaHash = this.senhaHash;
            u.nivel = this.nivel;
            u.telefone = this.telefone;
            u.curso = this.curso;
            u.foto_url = this.foto_url;
            u.media_avaliacao = this.media_avaliacao;
            u.criado_em = this.criado_em;
            return u;
        }
    }

    @Override
    public String toString() {
        return nome_completo + " (" + email + ") - [" + nivel + "]";
    }
}
