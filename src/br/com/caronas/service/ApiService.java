package br.com.caronas.service;

import br.com.caronas.model.*;

import java.util.List;

public interface ApiService {
    interface ApiCallListener {
        void onApiCall(ApiResponse<?> response);
    }

    // --- Autenticação & Usuários ---
    ApiResponse<AuthResponse> cadastrar(String nome, String email, String senha, String telefone, String curso, String instituicaoId);
    ApiResponse<AuthResponse> login(String email, String senha);
    ApiResponse<Usuario> getMe();
    ApiResponse<Usuario> updateMe(String nome, String telefone, String curso, String fotoUrl);
    ApiResponse<PageResult<Usuario>> getUsuariosAdmin(String busca, String nivel, int pagina, int limite);
    ApiResponse<Usuario> updateUsuarioAdmin(String id, String nome, String email, String nivel, String telefone, String curso);
    ApiResponse<String> deleteUsuarioAdmin(String id);
    void logout();
    void setUsuarioLogado(Usuario usuario, String token);
    Usuario getUsuarioLogado();
    String getToken();
    boolean isAuthenticated();
    boolean isAdmin();

    // --- Instituições, Campi & Bairros ---
    List<Instituicao> getInstituicoes();
    Instituicao getInstituicaoById(String id);
    List<Campus> getCampi();
    List<Campus> getCampiByInstituicao(String instituicaoId);
    Campus getCampusById(String id);
    List<Bairro> getBairros();
    Bairro getBairroById(String id);
    ApiResponse<Bairro> cadastrarBairro(String nome, String cidade);
    ApiResponse<Campus> cadastrarCampus(String instituicaoId, String nome, String cidade, String endereco);

    // --- Veículos ---
    ApiResponse<List<Veiculo>> getMeusVeiculos();
    ApiResponse<Veiculo> cadastrarVeiculo(String modelo, String placa, int capacidade);
    ApiResponse<Veiculo> updateVeiculo(String id, String modelo, String placa, int capacidade);
    ApiResponse<String> deleteVeiculo(String id);

    // --- Caronas ---
    ApiResponse<List<Carona>> buscarCaronas(String bairroId, String campusId, String direcao, String busca, String status);
    ApiResponse<Carona> getCaronaById(String id);
    ApiResponse<List<Carona>> getMinhasCaronas();
    ApiResponse<Carona> oferecerCarona(String veiculoId, String direcao, String bairroId, String campusId, String pontoEncontro, String horarioSaida, int assentosTotais, double valorContribuicao);
    ApiResponse<Carona> atualizarStatusCarona(String caronaId, String novoStatus);
    ApiResponse<Carona> cancelarCarona(String caronaId, String motivo);

    // --- Reservas ---
    ApiResponse<Reserva> solicitarCarona(String caronaId, int vagas, String pontoEmbarque);
    ApiResponse<List<Reserva>> getMinhasReservas();
    ApiResponse<List<Reserva>> getReservasDaCarona(String caronaId);
    ApiResponse<Reserva> responderReserva(String reservaId, boolean aceitar);
    ApiResponse<String> cancelarReserva(String reservaId);

    // --- Avaliações ---
    ApiResponse<Avaliacao> avaliarUsuario(String caronaId, String avaliadoId, int nota, String comentario, String tipo);
    ApiResponse<List<Avaliacao>> getAvaliacoesDoUsuario(String usuarioId);
    ApiResponse<List<Avaliacao>> getMinhasAvaliacoes();

    // --- Estatísticas / Dashboard ---
    DashboardStats getDashboardStats();

    // --- API Logs & Listeners ---
    void addApiCallListener(ApiCallListener listener);
    void removeApiCallListener(ApiCallListener listener);
}
