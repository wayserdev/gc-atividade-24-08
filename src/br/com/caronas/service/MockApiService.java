package br.com.caronas.service;

import br.com.caronas.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MockApiService implements ApiService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final List<Instituicao> instituicoes = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<ApiCallListener> listeners = new ArrayList<>();

    private Usuario usuarioLogado = null;
    private String tokenJwt = null;

    public MockApiService() {
        initDefaultData();
    }

    private void initDefaultData() {
        // Instituições
        Instituicao ifg = new Instituicao("c1a2b3c4-0000-0000-0000-000000000000", "Universidade Estadual de Goiás", "UEG");
        Instituicao ufg = new Instituicao("d2b3c4d5-0000-0000-0000-000000000000", "Universidade Federal de Goiás", "UFG");
        Instituicao ifgoiano = new Instituicao("e3c4d5e6-0000-0000-0000-000000000000", "Instituto Federal de Goiás", "IFG");
        instituicoes.add(ifg);
        instituicoes.add(ufg);
        instituicoes.add(ifgoiano);

        // Usuários padrão da documentação
        Usuario alunoCarlos = new Usuario(
                "u1u2u3u4-0000-0000-0000-000000000000",
                ifg.getId(),
                ifg.getNome(),
                "Carlos Eduardo",
                "carlos.edu@gmail.com",
                "senhaSegura123",
                "ALUNO",
                "62999998888",
                "Engenharia de Software",
                "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                "4.85",
                "2026-08-27T21:00:00.000Z"
        );

        Usuario adminCarlos = new Usuario(
                "a1a2a3a4-0000-0000-0000-000000000000",
                ifg.getId(),
                ifg.getNome(),
                "Carlos Eduardo Admin",
                "carlos.admin@faculdade.br",
                "admin123",
                "ADMIN",
                "62999998888",
                "Docente / Coordenação",
                "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                "4.95",
                "2026-08-20T14:30:00.000Z"
        );

        Usuario alunoMariana = new Usuario(
                UUID.randomUUID().toString(),
                ufg.getId(),
                ufg.getNome(),
                "Mariana Oliveira Santos",
                "mariana.oli@gmail.com",
                "senha123",
                "ALUNO",
                "62981112233",
                "Ciência da Computação",
                null,
                "4.92",
                "2026-08-28T10:15:00.000Z"
        );

        Usuario alunoLucas = new Usuario(
                UUID.randomUUID().toString(),
                ifg.getId(),
                ifg.getNome(),
                "Lucas Ribeiro Mendes",
                "lucas.rib@gmail.com",
                "senha123",
                "ALUNO",
                "62992223344",
                "Sistemas de Informação",
                null,
                "4.70",
                "2026-08-28T12:00:00.000Z"
        );

        Usuario alunoBeatriz = new Usuario(
                UUID.randomUUID().toString(),
                ifgoiano.getId(),
                ifgoiano.getNome(),
                "Beatriz Santos Pereira",
                "beatriz.s@gmail.com",
                "senha123",
                "ALUNO",
                "62993334455",
                "Engenharia Elétrica",
                null,
                "4.60",
                "2026-08-29T08:45:00.000Z"
        );

        Usuario adminAna = new Usuario(
                UUID.randomUUID().toString(),
                ufg.getId(),
                ufg.getNome(),
                "Ana Carolina Lima",
                "ana.lima@faculdade.br",
                "admin123",
                "ADMIN",
                "62984445566",
                "Coordenação de Transportes",
                null,
                "5.00",
                "2026-08-22T09:00:00.000Z"
        );

        usuarios.add(alunoCarlos);
        usuarios.add(adminCarlos);
        usuarios.add(alunoMariana);
        usuarios.add(alunoLucas);
        usuarios.add(alunoBeatriz);
        usuarios.add(adminAna);

        // Gera mais 15 alunos para enriquecer paginação
        String[] nomes = {"Rodrigo Alves", "Fernanda Costa", "Gabriel Moreira", "Juliana Paes", "Felipe Silva",
                "Camila Rocha", "Rafael Nogueira", "Larissa Dias", "Thiago Martins", "Amanda Barbosa",
                "Bruno Castro", "Patricia Gomes", "Gustavo Ramos", "Isabela Duarte", "Diego Freitas"};
        String[] cursos = {"Engenharia de Software", "Ciência da Computação", "Sistemas de Informação", "Direito", "Administração", "Medicina", "Engenharia Civil"};

        for (int i = 0; i < nomes.length; i++) {
            String nome = nomes[i];
            String email = nome.toLowerCase().replace(" ", ".") + "@aluno.ueg.br";
            Instituicao inst = instituicoes.get(i % instituicoes.size());
            String curso = cursos[i % cursos.length];
            usuarios.add(new Usuario(
                    UUID.randomUUID().toString(),
                    inst.getId(),
                    inst.getNome(),
                    nome,
                    email,
                    "aluno123",
                    "ALUNO",
                    "6299" + (1000000 + i * 11111),
                    curso,
                    null,
                    String.format(Locale.US, "%.2f", 4.0 + (i % 10) * 0.1),
                    "2026-08-30T" + String.format("%02d:00:00.000Z", (8 + i) % 24)
            ));
        }

        // Login padrão inicial para o Carlos Eduardo (Aluno)
        setUsuarioLogado(alunoCarlos, generateFakeJwt(alunoCarlos));
    }

    private String generateFakeJwt(Usuario u) {
        String header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(
                ("{\"sub\":\"" + u.getId() + "\",\"email\":\"" + u.getEmail() + "\",\"nivel\":\"" + u.getNivel() + "\",\"iat\":1724792400}").getBytes()
        );
        String signature = "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        return header + "." + payload + "." + signature;
    }

    private void notifyListeners(ApiResponse<?> response) {
        for (ApiCallListener listener : new ArrayList<>(listeners)) {
            listener.onApiCall(response);
        }
    }

    @Override
    public ApiResponse<AuthResponse> cadastrar(String nome, String email, String senha, String telefone, String curso, String instituicaoId) {
        String method = "POST";
        String path = "/auth/cadastrar";
        String reqJson = String.format("{\n  \"nome_completo\": \"%s\",\n  \"email\": \"%s\",\n  \"senha\": \"******\",\n  \"telefone\": \"%s\",\n  \"curso\": \"%s\",\n  \"instituicao_id\": \"%s\"\n}",
                nome != null ? nome : "", email != null ? email : "", telefone != null ? telefone : "", curso != null ? curso : "", instituicaoId != null ? instituicaoId : "");

        List<String> validacaoErros = new ArrayList<>();
        if (nome == null || nome.trim().length() < 3 || nome.trim().length() > 150) {
            validacaoErros.add("nome_completo deve ter entre 3 e 150 caracteres");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            validacaoErros.add("email deve ser um e-mail válido");
        }
        if (senha == null || senha.length() < 6) {
            validacaoErros.add("senha deve ter no mínimo 6 caracteres");
        }
        if (telefone == null || !telefone.replaceAll("[^0-9]", "").matches("\\d{10,11}")) {
            validacaoErros.add("telefone deve ser uma string numérica com DDD (10 ou 11 dígitos)");
        }
        if (curso == null || curso.trim().isEmpty()) {
            validacaoErros.add("curso é obrigatório");
        }
        if (instituicaoId == null || instituicaoId.trim().isEmpty()) {
            validacaoErros.add("instituicao_id é obrigatório");
        }

        if (!validacaoErros.isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", validacaoErros);
            ApiResponse<AuthResponse> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        // Verifica duplicidade de email
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                ApiError err = new ApiError(409, "Conflict", "Este e-mail já está em uso por outro usuário.");
                ApiResponse<AuthResponse> res = ApiResponse.fail(method, path, err, reqJson);
                notifyListeners(res);
                return res;
            }
        }

        Instituicao inst = getInstituicaoById(instituicaoId);
        String instNome = inst != null ? inst.getNome() : "Universidade Estadual";

        String novoId = "u" + UUID.randomUUID().toString().substring(1);
        String criadoEm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        Usuario novoUsuario = new Usuario(
                novoId,
                instituicaoId,
                instNome,
                nome.trim(),
                email.trim(),
                senha,
                "ALUNO",
                telefone.replaceAll("[^0-9]", ""),
                curso.trim(),
                null,
                "0.00",
                criadoEm
        );

        usuarios.add(novoUsuario);
        String token = generateFakeJwt(novoUsuario);
        setUsuarioLogado(novoUsuario, token);

        String respJson = String.format("{\n  \"token\": \"%s\",\n  \"usuario\": {\n    \"id\": \"%s\",\n    \"instituicao_id\": \"%s\",\n    \"nome_completo\": \"%s\",\n    \"email\": \"%s\",\n    \"nivel\": \"%s\",\n    \"telefone\": \"%s\",\n    \"curso\": \"%s\",\n    \"foto_url\": null,\n    \"media_avaliacao\": \"0.00\",\n    \"criado_em\": \"%s\"\n  }\n}",
                token, novoId, instituicaoId, novoUsuario.getNome_completo(), novoUsuario.getEmail(), novoUsuario.getNivel(), novoUsuario.getTelefone(), novoUsuario.getCurso(), criadoEm);

        AuthResponse auth = new AuthResponse(token, novoUsuario, "Cadastro realizado com sucesso!");
        ApiResponse<AuthResponse> res = ApiResponse.ok(method, path, 201, auth, reqJson, respJson);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<AuthResponse> login(String email, String senha) {
        String method = "POST";
        String path = "/auth/login";
        String reqJson = String.format("{\n  \"email\": \"%s\",\n  \"senha\": \"******\"\n}", email != null ? email : "");

        if (email == null || email.trim().isEmpty() || senha == null || senha.isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", "email e senha são obrigatórios.");
            ApiResponse<AuthResponse> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Usuario encontrado = null;
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email.trim()) && u.getSenha().equals(senha)) {
                encontrado = u;
                break;
            }
        }

        if (encontrado == null) {
            ApiError err = new ApiError(401, "Unauthorized", "E-mail ou senha inválidos.");
            ApiResponse<AuthResponse> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        String token = generateFakeJwt(encontrado);
        setUsuarioLogado(encontrado, token);

        String respJson = String.format("{\n  \"token\": \"%s\",\n  \"usuario\": {\n    \"id\": \"%s\",\n    \"nome_completo\": \"%s\",\n    \"email\": \"%s\",\n    \"nivel\": \"%s\",\n    \"telefone\": \"%s\",\n    \"curso\": \"%s\",\n    \"foto_url\": %s,\n    \"media_avaliacao\": \"%s\"\n  }\n}",
                token, encontrado.getId(), encontrado.getNome_completo(), encontrado.getEmail(), encontrado.getNivel(), encontrado.getTelefone(), encontrado.getCurso(),
                encontrado.getFoto_url() != null ? "\"" + encontrado.getFoto_url() + "\"" : "null", encontrado.getMedia_avaliacao());

        AuthResponse auth = new AuthResponse(token, encontrado, "Login realizado com sucesso.");
        ApiResponse<AuthResponse> res = ApiResponse.ok(method, path, 200, auth, reqJson, respJson);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Usuario> getMe() {
        String method = "GET";
        String path = "/usuarios/me";

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou expirado.");
            ApiResponse<Usuario> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer null");
            notifyListeners(res);
            return res;
        }

        ApiResponse<Usuario> res = ApiResponse.ok(method, path, 200, usuarioLogado.clone(), null, usuarioToJson(usuarioLogado));
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Usuario> updateMe(String nome, String telefone, String curso, String fotoUrl) {
        String method = "PUT";
        String path = "/usuarios/me";
        String reqJson = String.format("{\n  \"nome_completo\": \"%s\",\n  \"telefone\": \"%s\",\n  \"curso\": \"%s\",\n  \"foto_url\": %s\n}",
                nome != null ? nome : "", telefone != null ? telefone : "", curso != null ? curso : "", fotoUrl != null ? "\"" + fotoUrl + "\"" : "null");

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou expirado.");
            ApiResponse<Usuario> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (nome != null && !nome.trim().isEmpty()) {
            usuarioLogado.setNome_completo(nome.trim());
        }
        if (telefone != null && !telefone.trim().isEmpty()) {
            usuarioLogado.setTelefone(telefone.replaceAll("[^0-9]", ""));
        }
        if (curso != null && !curso.trim().isEmpty()) {
            usuarioLogado.setCurso(curso.trim());
        }
        usuarioLogado.setFoto_url(fotoUrl != null && !fotoUrl.trim().isEmpty() ? fotoUrl.trim() : null);

        // Atualiza na lista geral
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(usuarioLogado.getId())) {
                usuarios.set(i, usuarioLogado);
                break;
            }
        }

        String respJson = "{\n  \"mensagem\": \"Perfil atualizado com sucesso.\",\n  \"usuario\": " + usuarioToJson(usuarioLogado) + "\n}";
        ApiResponse<Usuario> res = ApiResponse.ok(method, path, 200, usuarioLogado.clone(), reqJson, respJson);
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<PageResult<Usuario>> getUsuariosAdmin(String busca, String nivel, int pagina, int limite) {
        String method = "GET";
        String query = String.format("?busca=%s&nivel=%s&pagina=%d&limite=%d",
                busca != null ? busca : "", nivel != null ? nivel : "", pagina, limite);
        String path = "/admin/usuarios" + query;

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou expirado.");
            ApiResponse<PageResult<Usuario>> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        if (!isAdmin()) {
            ApiError err = new ApiError(403, "Forbidden", "Acesso restrito para administradores.");
            ApiResponse<PageResult<Usuario>> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        // Filtra por busca e nível
        List<Usuario> filtrados = usuarios.stream().filter(u -> {
            boolean matchBusca = true;
            if (busca != null && !busca.trim().isEmpty()) {
                String b = busca.trim().toLowerCase();
                matchBusca = (u.getNome_completo() != null && u.getNome_completo().toLowerCase().contains(b))
                        || (u.getEmail() != null && u.getEmail().toLowerCase().contains(b));
            }

            boolean matchNivel = true;
            if (nivel != null && !nivel.trim().isEmpty() && !"TODOS".equalsIgnoreCase(nivel)) {
                matchNivel = nivel.equalsIgnoreCase(u.getNivel());
            }

            return matchBusca && matchNivel;
        }).collect(Collectors.toList());

        int total = filtrados.size();
        if (limite <= 0) limite = 20;
        if (pagina <= 0) pagina = 1;

        int fromIndex = (pagina - 1) * limite;
        List<Usuario> paginados;
        if (fromIndex >= total) {
            paginados = new ArrayList<>();
        } else {
            int toIndex = Math.min(fromIndex + limite, total);
            paginados = filtrados.subList(fromIndex, toIndex);
        }

        PageResult<Usuario> pageResult = new PageResult<>(total, pagina, limite, paginados);

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"total\": ").append(total).append(",\n");
        sb.append("  \"pagina\": ").append(pagina).append(",\n");
        sb.append("  \"limite\": ").append(limite).append(",\n");
        sb.append("  \"usuarios\": [\n");
        for (int i = 0; i < paginados.size(); i++) {
            sb.append("    ").append(usuarioToJson(paginados.get(i)).replace("\n", "\n    "));
            if (i < paginados.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}");

        ApiResponse<PageResult<Usuario>> res = ApiResponse.ok(method, path, 200, pageResult, null, sb.toString());
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Usuario> updateUsuarioAdmin(String id, String nome, String email, String nivel, String telefone, String curso) {
        String method = "PUT";
        String path = "/admin/usuarios/" + id;
        String reqJson = String.format("{\n  \"nome_completo\": \"%s\",\n  \"email\": \"%s\",\n  \"nivel\": \"%s\",\n  \"telefone\": \"%s\",\n  \"curso\": \"%s\"\n}",
                nome != null ? nome : "", email != null ? email : "", nivel != null ? nivel : "", telefone != null ? telefone : "", curso != null ? curso : "");

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou expirado.");
            ApiResponse<Usuario> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (!isAdmin()) {
            ApiError err = new ApiError(403, "Forbidden", "Acesso restrito para administradores.");
            ApiResponse<Usuario> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        Usuario target = null;
        for (Usuario u : usuarios) {
            if (u.getId().equals(id)) {
                target = u;
                break;
            }
        }

        if (target == null) {
            ApiError err = new ApiError(404, "Not Found", "Usuário não encontrado.");
            ApiResponse<Usuario> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        if (nome != null && !nome.trim().isEmpty()) target.setNome_completo(nome.trim());
        if (email != null && !email.trim().isEmpty()) target.setEmail(email.trim());
        if (nivel != null && !nivel.trim().isEmpty()) target.setNivel(nivel.trim().toUpperCase());
        if (telefone != null && !telefone.trim().isEmpty()) target.setTelefone(telefone.replaceAll("[^0-9]", ""));
        if (curso != null && !curso.trim().isEmpty()) target.setCurso(curso.trim());

        // Se o admin editou o seu próprio usuário logado, atualiza o usuário logado
        if (usuarioLogado != null && usuarioLogado.getId().equals(target.getId())) {
            usuarioLogado = target.clone();
        }

        String respJson = "{\n  \"mensagem\": \"Usuário atualizado pelo administrador com sucesso.\",\n  \"usuario\": " + usuarioToJson(target) + "\n}";
        ApiResponse<Usuario> res = ApiResponse.ok(method, path, 200, target.clone(), reqJson, respJson);
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<String> deleteUsuarioAdmin(String id) {
        String method = "DELETE";
        String path = "/admin/usuarios/" + id;

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou expirado.");
            ApiResponse<String> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        if (!isAdmin()) {
            ApiError err = new ApiError(403, "Forbidden", "Acesso restrito para administradores.");
            ApiResponse<String> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        // Regra de Validação: O Administrador não pode deletar a si mesmo (evitar auto-bloqueio)
        if (usuarioLogado != null && usuarioLogado.getId().equals(id)) {
            ApiError err = new ApiError(400, "Bad Request", "O Administrador não pode deletar a si mesmo (evitar auto-bloqueio).");
            ApiResponse<String> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        boolean removed = usuarios.removeIf(u -> u.getId().equals(id));
        if (!removed) {
            ApiError err = new ApiError(404, "Not Found", "Usuário não encontrado.");
            ApiResponse<String> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        String respJson = "{\n  \"mensagem\": \"Usuário removido da base de dados com sucesso.\"\n}";
        ApiResponse<String> res = ApiResponse.ok(method, path, 200, "Usuário removido da base de dados com sucesso.", null, respJson);
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    private String usuarioToJson(Usuario u) {
        if (u == null) return "null";
        return String.format("{\n  \"id\": \"%s\",\n  \"instituicao_id\": \"%s\",\n  \"instituicao_nome\": \"%s\",\n  \"nome_completo\": \"%s\",\n  \"email\": \"%s\",\n  \"nivel\": \"%s\",\n  \"telefone\": \"%s\",\n  \"curso\": \"%s\",\n  \"foto_url\": %s,\n  \"media_avaliacao\": \"%s\",\n  \"criado_em\": \"%s\"\n}",
                u.getId(), u.getInstituicao_id(), u.getInstituicao_nome(), u.getNome_completo(), u.getEmail(), u.getNivel(), u.getTelefone(), u.getCurso(),
                u.getFoto_url() != null ? "\"" + u.getFoto_url() + "\"" : "null", u.getMedia_avaliacao(), u.getCriado_em());
    }

    @Override
    public void logout() {
        this.usuarioLogado = null;
        this.tokenJwt = null;
    }

    @Override
    public void setUsuarioLogado(Usuario usuario, String token) {
        this.usuarioLogado = usuario;
        this.tokenJwt = token;
    }

    @Override
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    @Override
    public String getToken() {
        return tokenJwt;
    }

    @Override
    public boolean isAuthenticated() {
        return usuarioLogado != null && tokenJwt != null;
    }

    @Override
    public boolean isAdmin() {
        return isAuthenticated() && usuarioLogado.isAdmin();
    }

    @Override
    public List<Instituicao> getInstituicoes() {
        return Collections.unmodifiableList(instituicoes);
    }

    @Override
    public Instituicao getInstituicaoById(String id) {
        if (id == null) return null;
        for (Instituicao inst : instituicoes) {
            if (inst.getId().equalsIgnoreCase(id)) return inst;
        }
        return null;
    }

    @Override
    public void addApiCallListener(ApiCallListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeApiCallListener(ApiCallListener listener) {
        listeners.remove(listener);
    }
}
