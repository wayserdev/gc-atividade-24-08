package br.com.caronas.model;

import java.io.Serializable;

public class DashboardStats implements Serializable {
    private int totalCaronasAtivasHoje;
    private int totalMinhasCaronas;
    private int totalMinhasReservas;
    private double economiaEstimadaReais;
    private double kgCo2Evitados;
    private double mediaAvaliacaoPessoal;
    private int totalAvaliacoesRecebidas;

    public DashboardStats() {}

    public DashboardStats(int totalCaronasAtivasHoje, int totalMinhasCaronas, int totalMinhasReservas,
                          double economiaEstimadaReais, double kgCo2Evitados, double mediaAvaliacaoPessoal,
                          int totalAvaliacoesRecebidas) {
        this.totalCaronasAtivasHoje = totalCaronasAtivasHoje;
        this.totalMinhasCaronas = totalMinhasCaronas;
        this.totalMinhasReservas = totalMinhasReservas;
        this.economiaEstimadaReais = economiaEstimadaReais;
        this.kgCo2Evitados = kgCo2Evitados;
        this.mediaAvaliacaoPessoal = mediaAvaliacaoPessoal;
        this.totalAvaliacoesRecebidas = totalAvaliacoesRecebidas;
    }

    public int getTotalCaronasAtivasHoje() {
        return totalCaronasAtivasHoje;
    }

    public void setTotalCaronasAtivasHoje(int totalCaronasAtivasHoje) {
        this.totalCaronasAtivasHoje = totalCaronasAtivasHoje;
    }

    public int getTotalMinhasCaronas() {
        return totalMinhasCaronas;
    }

    public void setTotalMinhasCaronas(int totalMinhasCaronas) {
        this.totalMinhasCaronas = totalMinhasCaronas;
    }

    public int getTotalMinhasReservas() {
        return totalMinhasReservas;
    }

    public void setTotalMinhasReservas(int totalMinhasReservas) {
        this.totalMinhasReservas = totalMinhasReservas;
    }

    public double getEconomiaEstimadaReais() {
        return economiaEstimadaReais;
    }

    public void setEconomiaEstimadaReais(double economiaEstimadaReais) {
        this.economiaEstimadaReais = economiaEstimadaReais;
    }

    public double getKgCo2Evitados() {
        return kgCo2Evitados;
    }

    public void setKgCo2Evitados(double kgCo2Evitados) {
        this.kgCo2Evitados = kgCo2Evitados;
    }

    public double getMediaAvaliacaoPessoal() {
        return mediaAvaliacaoPessoal;
    }

    public void setMediaAvaliacaoPessoal(double mediaAvaliacaoPessoal) {
        this.mediaAvaliacaoPessoal = mediaAvaliacaoPessoal;
    }

    public int getTotalAvaliacoesRecebidas() {
        return totalAvaliacoesRecebidas;
    }

    public void setTotalAvaliacoesRecebidas(int totalAvaliacoesRecebidas) {
        this.totalAvaliacoesRecebidas = totalAvaliacoesRecebidas;
    }
}
