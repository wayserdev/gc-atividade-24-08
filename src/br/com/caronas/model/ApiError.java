package br.com.caronas.model;

import java.util.ArrayList;
import java.util.List;

public class ApiError {
    private int statusCode;
    private String erro;
    private List<String> mensagens = new ArrayList<>();

    public ApiError(int statusCode, String erro, String mensagem) {
        this.statusCode = statusCode;
        this.erro = erro;
        this.mensagens.add(mensagem);
    }

    public ApiError(int statusCode, String erro, List<String> mensagens) {
        this.statusCode = statusCode;
        this.erro = erro;
        if (mensagens != null) {
            this.mensagens.addAll(mensagens);
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErro() {
        return erro;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public String getMensagemFormatada() {
        if (mensagens.isEmpty()) return erro;
        if (mensagens.size() == 1) return mensagens.get(0);
        return String.join("\n• ", mensagens);
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"erro\": \"").append(escapeJson(erro)).append("\",\n");
        if (mensagens.size() == 1) {
            sb.append("  \"mensagem\": \"").append(escapeJson(mensagens.get(0))).append("\"\n");
        } else {
            sb.append("  \"mensagem\": [\n");
            for (int i = 0; i < mensagens.size(); i++) {
                sb.append("    \"").append(escapeJson(mensagens.get(i))).append("\"");
                if (i < mensagens.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ]\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
