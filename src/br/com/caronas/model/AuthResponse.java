package br.com.caronas.model;

public class AuthResponse {
    private String token;
    private Usuario usuario;
    private String mensagem;

    public AuthResponse() {}

    public AuthResponse(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public AuthResponse(String token, Usuario usuario, String mensagem) {
        this.token = token;
        this.usuario = usuario;
        this.mensagem = mensagem;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
