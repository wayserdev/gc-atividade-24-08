package br.com.caronas.model;

import java.util.List;

public class PageResult<T> {
    private int total;
    private int pagina;
    private int limite;
    private List<T> usuarios;

    public PageResult(int total, int pagina, int limite, List<T> usuarios) {
        this.total = total;
        this.pagina = pagina;
        this.limite = limite;
        this.usuarios = usuarios;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    public List<T> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<T> usuarios) {
        this.usuarios = usuarios;
    }

    public int getTotalPaginas() {
        if (limite <= 0) return 1;
        return (int) Math.ceil((double) total / limite);
    }
}
