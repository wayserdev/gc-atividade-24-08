package br.com.caronas.service;

import br.com.caronas.model.*;

import java.util.List;

public interface ApiService {
    interface ApiCallListener {
        void onApiCall(ApiResponse<?> response);
    }

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

    List<Instituicao> getInstituicoes();

    Instituicao getInstituicaoById(String id);

    void addApiCallListener(ApiCallListener listener);

    void removeApiCallListener(ApiCallListener listener);

    // ==========================================
    // DEV04 — RESERVAS (FT10-11 / UC06-08)
    // ==========================================
    ApiResponse<ReservaResponse> criarReserva(ReservaRequest request);

    ApiResponse<ReservaResponse> getReservaById(String id);

    ApiResponse<List<ReservaResponse>> getReservas();

    ApiResponse<List<ReservaResponse>> getReservasByUsuario(String usuarioId);

    ApiResponse<ReservaResponse> cancelarReserva(String id);

    ApiResponse<ReservaResponse> updateStatusReserva(String id, String novoStatus);

    // Integração mínima com Caronas (DEV03)
    List<Carona> getCaronas();

    Carona getCaronaById(String id);
}
