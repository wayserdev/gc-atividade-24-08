package br.com.caronas.service;

import br.com.caronas.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MockApiService implements ApiService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final List<Instituicao> instituicoes = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Carona> caronas = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();
    private final List<ApiCallListener> listeners = new ArrayList<>();
    private final JwtService jwtService = new JwtService();

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

        // Usuário motorista para testes de carona e reservas (DEV04 / seed_dados.sql)
        Usuario motoristaJoao = new Usuario(
                "55555555-5555-5555-5555-555555555555",
                ufg.getId(),
                ufg.getNome(),
                "João Paulo Motorista",
                "joao.motorista@gmail.com",
                "motorista123",
                "ALUNO",
                "62988887777",
                "Engenharia de Software",
                null,
                "4.90",
                "2026-08-25T08:00:00.000Z"
        );
        usuarios.add(motoristaJoao);

        // Os dados de demonstração também respeitam o contrato de segurança:
        // o serviço nunca compara nem mantém senhas em texto puro.
        for (Usuario usuario : usuarios) {
            usuario.setSenhaHash(PasswordHasher.hash(usuario.getSenhaHash()));
        }

        // Inicialização de Caronas para suporte a DEV04 (FT10-11)
        Carona caronaDisponivel = new Carona(
                "c1000000-0000-0000-0000-000000000001",
                motoristaJoao.getId(),
                motoristaJoao.getNome_completo(),
                3,
                3,
                "Praça Universitária - Setor Bueno",
                "2026-09-10T07:30:00.000Z",
                Carona.STATUS_AGENDADA
        );
        caronaDisponivel.setDirecao("BAIRRO_PARA_CAMPUS");
        caronaDisponivel.setValor_contribuicao(5.00);

        Carona caronaLotada = new Carona(
                "c1000000-0000-0000-0000-000000000002",
                motoristaJoao.getId(),
                motoristaJoao.getNome_completo(),
                2,
                0,
                "Av. T-63 - Setor Bueno",
                "2026-09-10T13:30:00.000Z",
                Carona.STATUS_AGENDADA
        );
        caronaLotada.setDirecao("BAIRRO_PARA_CAMPUS");
        caronaLotada.setValor_contribuicao(6.00);

        Carona caronaCancelada = new Carona(
                "c1000000-0000-0000-0000-000000000003",
                motoristaJoao.getId(),
                motoristaJoao.getNome_completo(),
                4,
                4,
                "Campus Samambaia",
                "2026-09-11T18:00:00.000Z",
                Carona.STATUS_CANCELADA
        );
        caronaCancelada.setDirecao("CAMPUS_PARA_BAIRRO");
        caronaCancelada.setValor_contribuicao(5.00);

        caronas.add(caronaDisponivel);
        caronas.add(caronaLotada);
        caronas.add(caronaCancelada);

        // Reserva inicial existente (lotou a carona 2 com 2 vagas para Mariana)
        Reserva reservaMariana = new Reserva(
                "r1000000-0000-0000-0000-000000000001",
                caronaLotada.getId(),
                alunoMariana.getId(),
                2,
                "Terminal Praça da Bíblia",
                Reserva.STATUS_ACEITA,
                "2026-09-01T10:00:00.000Z"
        );
        reservas.add(reservaMariana);

        // A aplicação inicia deslogada; o usuário precisa autenticar-se pela tela.
    }

    private String generateJwt(Usuario u) {
        return jwtService.createToken(u);
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
        String reqJson = "{\n"
                + "  \"nome_completo\": " + jsonString(nome) + ",\n"
                + "  \"email\": " + jsonString(email) + ",\n"
                + "  \"senha\": \"******\",\n"
                + "  \"telefone\": " + jsonString(telefone) + ",\n"
                + "  \"curso\": " + jsonString(curso) + ",\n"
                + "  \"instituicao_id\": " + jsonString(instituicaoId) + "\n"
                + "}";

        List<String> validacaoErros = new ArrayList<>();
        if (nome == null || nome.trim().length() < 3 || nome.trim().length() > 150) {
            validacaoErros.add("nome_completo deve ter entre 3 e 150 caracteres");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches() || email.trim().length() > 150) {
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
        } else if (getInstituicaoById(instituicaoId.trim()) == null) {
            validacaoErros.add("instituicao_id não pertence a uma instituição cadastrada");
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

        String instituicaoNormalizada = instituicaoId.trim();
        Instituicao inst = getInstituicaoById(instituicaoNormalizada);
        String instNome = inst != null ? inst.getNome() : "Universidade Estadual";

        String novoId = "u" + UUID.randomUUID().toString().substring(1);
        String criadoEm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        Usuario novoUsuario = new Usuario(
                novoId,
                instituicaoNormalizada,
                instNome,
                nome.trim(),
                email.trim(),
                PasswordHasher.hash(senha),
                "ALUNO",
                telefone.replaceAll("[^0-9]", ""),
                curso.trim(),
                null,
                "0.00",
                criadoEm
        );

        usuarios.add(novoUsuario);
        String token = generateJwt(novoUsuario);
        setUsuarioLogado(novoUsuario, token);

        String respJson = authToJson(token, novoUsuario);

        AuthResponse auth = new AuthResponse(token, publicCopy(novoUsuario), "Cadastro realizado com sucesso!");
        ApiResponse<AuthResponse> res = ApiResponse.ok(method, path, 201, auth, reqJson, respJson);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<AuthResponse> login(String email, String senha) {
        String method = "POST";
        String path = "/auth/login";
        String reqJson = "{\n  \"email\": " + jsonString(email) + ",\n  \"senha\": \"******\"\n}";

        if (email == null || email.trim().isEmpty() || senha == null || senha.isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", "email e senha são obrigatórios.");
            ApiResponse<AuthResponse> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Usuario encontrado = null;
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email.trim()) && PasswordHasher.matches(senha, u.getSenhaHash())) {
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

        String token = generateJwt(encontrado);
        setUsuarioLogado(encontrado, token);

        String respJson = authToJson(token, encontrado);

        AuthResponse auth = new AuthResponse(token, publicCopy(encontrado), "Login realizado com sucesso.");
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
            notifyListeners(res);
            return res;
        }

        ApiResponse<Usuario> res = ApiResponse.ok(method, path, 200, publicCopy(usuarioLogado), null, usuarioToJson(usuarioLogado));
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Usuario> updateMe(String nome, String telefone, String curso, String fotoUrl) {
        String method = "PUT";
        String path = "/usuarios/me";
        String reqJson = "{\n"
                + "  \"nome_completo\": " + jsonString(nome) + ",\n"
                + "  \"telefone\": " + jsonString(telefone) + ",\n"
                + "  \"curso\": " + jsonString(curso) + ",\n"
                + "  \"foto_url\": " + jsonString(fotoUrl) + "\n"
                + "}";

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou expirado.");
            ApiResponse<Usuario> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        List<String> validacaoErros = new ArrayList<>();
        if (nome != null && !nome.trim().isEmpty()
                && (nome.trim().length() < 3 || nome.trim().length() > 150)) {
            validacaoErros.add("nome_completo deve ter entre 3 e 150 caracteres");
        }
        if (telefone != null && !telefone.trim().isEmpty()
                && !telefone.replaceAll("[^0-9]", "").matches("\\d{10,11}")) {
            validacaoErros.add("telefone deve ser uma string numérica com DDD (10 ou 11 dígitos)");
        }
        if (curso != null && curso.trim().length() > 100) {
            validacaoErros.add("curso deve ter no máximo 100 caracteres");
        }
        if (fotoUrl != null && fotoUrl.trim().length() > 255) {
            validacaoErros.add("foto_url deve ter no máximo 255 caracteres");
        }
        if (!validacaoErros.isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", validacaoErros);
            ApiResponse<Usuario> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
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
        ApiResponse<Usuario> res = ApiResponse.ok(method, path, 200, publicCopy(usuarioLogado), reqJson, respJson);
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

        List<Usuario> paginadosSeguros = paginados.stream()
                .map(this::publicCopy)
                .collect(Collectors.toList());
        PageResult<Usuario> pageResult = new PageResult<>(total, pagina, limite, paginadosSeguros);

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"total\": ").append(total).append(",\n");
        sb.append("  \"pagina\": ").append(pagina).append(",\n");
        sb.append("  \"limite\": ").append(limite).append(",\n");
        sb.append("  \"usuarios\": [\n");
        for (int i = 0; i < paginadosSeguros.size(); i++) {
            sb.append("    ").append(usuarioToJson(paginadosSeguros.get(i)).replace("\n", "\n    "));
            if (i < paginadosSeguros.size() - 1) sb.append(",");
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
        String reqJson = "{\n"
                + "  \"nome_completo\": " + jsonString(nome) + ",\n"
                + "  \"email\": " + jsonString(email) + ",\n"
                + "  \"nivel\": " + jsonString(nivel) + ",\n"
                + "  \"telefone\": " + jsonString(telefone) + ",\n"
                + "  \"curso\": " + jsonString(curso) + "\n"
                + "}";

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

        List<String> validacaoErros = new ArrayList<>();
        if (nome == null || nome.trim().length() < 3 || nome.trim().length() > 150) {
            validacaoErros.add("nome_completo deve ter entre 3 e 150 caracteres");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches() || email.trim().length() > 150) {
            validacaoErros.add("email deve ser um e-mail válido");
        }
        if (nivel == null || !("ALUNO".equalsIgnoreCase(nivel.trim()) || "ADMIN".equalsIgnoreCase(nivel.trim()))) {
            validacaoErros.add("nivel deve ser ALUNO ou ADMIN");
        }
        if (telefone != null && !telefone.trim().isEmpty()
                && !telefone.replaceAll("[^0-9]", "").matches("\\d{10,11}")) {
            validacaoErros.add("telefone deve ser uma string numérica com DDD (10 ou 11 dígitos)");
        }
        if (curso != null && curso.trim().length() > 100) {
            validacaoErros.add("curso deve ter no máximo 100 caracteres");
        }
        for (Usuario usuario : usuarios) {
            if (!usuario.getId().equals(target.getId())
                    && usuario.getEmail().equalsIgnoreCase(email == null ? "" : email.trim())) {
                validacaoErros.add("Este e-mail já está em uso por outro usuário.");
                break;
            }
        }
        if (!validacaoErros.isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", validacaoErros);
            ApiResponse<Usuario> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        if (nome != null && !nome.trim().isEmpty()) target.setNome_completo(nome.trim());
        target.setEmail(email.trim());
        target.setNivel(nivel.trim().toUpperCase());
        if (telefone != null && !telefone.trim().isEmpty()) target.setTelefone(telefone.replaceAll("[^0-9]", ""));
        if (curso != null && !curso.trim().isEmpty()) target.setCurso(curso.trim());

        // Se o admin editou o seu próprio usuário logado, atualiza o usuário logado
        if (usuarioLogado != null && usuarioLogado.getId().equals(target.getId())) {
            usuarioLogado = target.clone();
        }

        String respJson = "{\n  \"mensagem\": \"Usuário atualizado pelo administrador com sucesso.\",\n  \"usuario\": " + usuarioToJson(target) + "\n}";
        ApiResponse<Usuario> res = ApiResponse.ok(method, path, 200, publicCopy(target), reqJson, respJson);
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
        return "{\n"
                + "  \"id\": " + jsonString(u.getId()) + ",\n"
                + "  \"instituicao_id\": " + jsonString(u.getInstituicao_id()) + ",\n"
                + "  \"instituicao_nome\": " + jsonString(u.getInstituicao_nome()) + ",\n"
                + "  \"nome_completo\": " + jsonString(u.getNome_completo()) + ",\n"
                + "  \"email\": " + jsonString(u.getEmail()) + ",\n"
                + "  \"nivel\": " + jsonString(u.getNivel()) + ",\n"
                + "  \"telefone\": " + jsonString(u.getTelefone()) + ",\n"
                + "  \"curso\": " + jsonString(u.getCurso()) + ",\n"
                + "  \"foto_url\": " + jsonString(u.getFoto_url()) + ",\n"
                + "  \"media_avaliacao\": " + jsonString(u.getMedia_avaliacao()) + ",\n"
                + "  \"criado_em\": " + jsonString(u.getCriado_em()) + "\n"
                + "}";
    }

    private Usuario publicCopy(Usuario usuario) {
        Usuario copy = usuario == null ? null : usuario.clone();
        if (copy != null) copy.setSenhaHash(null);
        return copy;
    }

    private String authToJson(String token, Usuario usuario) {
        return "{\n  \"token\": " + jsonString(token)
                + ",\n  \"usuario\": " + usuarioToJson(usuario) + "\n}";
    }

    private String jsonString(String value) {
        if (value == null) return "null";
        return "\"" + escapeJson(value) + "\"";
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    @Override
    public void logout() {
        this.usuarioLogado = null;
        this.tokenJwt = null;
    }

    @Override
    public void setUsuarioLogado(Usuario usuario, String token) {
        if (usuario == null || token == null || !isTokenForUser(token, usuario)) {
            logout();
            return;
        }
        this.usuarioLogado = usuario.clone();
        this.tokenJwt = token;
    }

    @Override
    public Usuario getUsuarioLogado() {
        return isAuthenticated() ? publicCopy(usuarioLogado) : null;
    }

    @Override
    public String getToken() {
        return isAuthenticated() ? tokenJwt : null;
    }

    @Override
    public boolean isAuthenticated() {
        if (usuarioLogado == null || tokenJwt == null) return false;
        if (!isTokenForUser(tokenJwt, usuarioLogado)) {
            logout();
            return false;
        }
        return usuarios.stream().anyMatch(u -> u.getId().equals(usuarioLogado.getId()));
    }

    @Override
    public boolean isAdmin() {
        return isAuthenticated() && usuarioLogado.isAdmin();
    }

    private boolean isTokenForUser(String token, Usuario usuario) {
        return jwtService.validate(token)
                .map(claims -> claims.getSubject().equals(usuario.getId()))
                .orElse(false);
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

    // ==========================================
    // DEV04 — RESERVAS (FT10-11 / UC06-08)
    // ==========================================

    private Usuario findUsuarioById(String id) {
        if (id == null) return null;
        for (Usuario u : usuarios) {
            if (u.getId().equalsIgnoreCase(id)) return u;
        }
        return null;
    }

    private Reserva findReservaById(String id) {
        if (id == null) return null;
        for (Reserva r : reservas) {
            if (r.getId().equalsIgnoreCase(id)) return r;
        }
        return null;
    }

    @Override
    public ApiResponse<ReservaResponse> criarReserva(ReservaRequest request) {
        String method = "POST";
        String path = "/reservas";
        String reqJson = request != null ? request.toJson() : "null";

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou ausente. Usuário deve estar autenticado.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        List<String> validacaoErros = new ArrayList<>();
        if (request == null) {
            validacaoErros.add("Corpo da requisição é obrigatório.");
        } else {
            if (request.getCaronaId() == null || request.getCaronaId().trim().isEmpty()) {
                validacaoErros.add("carona_id é obrigatório.");
            }
            if (request.getVagasSolicitadas() <= 0) {
                validacaoErros.add("vagas_solicitadas deve ser maior que zero.");
            }
            if (request.getPontoEmbarque() != null && request.getPontoEmbarque().trim().length() > 255) {
                validacaoErros.add("ponto_embarque deve ter no máximo 255 caracteres.");
            }
        }

        if (!validacaoErros.isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", validacaoErros);
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            if (tokenJwt != null) res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        // Determina o passageiro a partir do contexto de segurança ou solicitação admin
        String passageiroId;
        if (request.getPassageiroId() != null && !request.getPassageiroId().trim().isEmpty()) {
            if (!isAdmin() && !request.getPassageiroId().equals(usuarioLogado.getId())) {
                ApiError err = new ApiError(403, "Forbidden", "Um aluno comum só pode solicitar reservas para si mesmo.");
                ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
                res.addHeader("Authorization", "Bearer " + tokenJwt);
                notifyListeners(res);
                return res;
            }
            passageiroId = request.getPassageiroId().trim();
        } else {
            passageiroId = usuarioLogado.getId();
        }

        Usuario passageiro = findUsuarioById(passageiroId);
        if (passageiro == null) {
            ApiError err = new ApiError(404, "Not Found", "Passageiro não encontrado.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        Carona carona = getCaronaById(request.getCaronaId().trim());
        if (carona == null) {
            ApiError err = new ApiError(404, "Not Found", "Carona não encontrada.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        // Regra: Carona deve estar AGENDADA
        if (!carona.isAgendada()) {
            ApiError err = new ApiError(400, "Bad Request", "A carona não está disponível para reservas (status: " + carona.getStatus() + ").");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        // Regra: Motorista não pode reservar a própria carona
        if (carona.getMotorista_id().equals(passageiro.getId())) {
            ApiError err = new ApiError(400, "Bad Request", "O motorista não pode realizar reserva na sua própria carona.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        // Regra: Não permitir reserva duplicada para o mesmo passageiro na mesma carona
        boolean duplicada = reservas.stream().anyMatch(r ->
                r.getCarona_id().equals(carona.getId())
                        && r.getPassageiro_id().equals(passageiro.getId())
                        && !r.isCancelada()
                        && !r.isRecusada());
        if (duplicada) {
            ApiError err = new ApiError(409, "Conflict", "O passageiro já possui uma reserva ativa para esta carona.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        // Regra: Verificar disponibilidade de vagas
        if (!carona.temVagas(request.getVagasSolicitadas())) {
            ApiError err = new ApiError(400, "Bad Request",
                    "Não há assentos disponíveis suficientes para atender à solicitação (disponíveis: "
                            + carona.getAssentos_disponiveis() + ", solicitados: " + request.getVagasSolicitadas() + ").");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        // Decrementa vagas na carona
        carona.reservarVagas(request.getVagasSolicitadas());

        String novoId = "r" + UUID.randomUUID().toString().substring(1);
        String criadoEm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        String ponto = request.getPontoEmbarque() != null && !request.getPontoEmbarque().trim().isEmpty()
                ? request.getPontoEmbarque().trim()
                : carona.getPonto_encontro();

        Reserva novaReserva = new Reserva(
                novoId,
                carona.getId(),
                passageiro.getId(),
                request.getVagasSolicitadas(),
                ponto,
                Reserva.STATUS_PENDENTE,
                criadoEm
        );
        reservas.add(novaReserva);

        ReservaResponse respData = ReservaResponse.fromReserva(novaReserva, passageiro.getNome_completo(), "Reserva solicitada com sucesso!");
        ApiResponse<ReservaResponse> res = ApiResponse.ok(method, path, 201, respData, reqJson, respData.toJson());
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<ReservaResponse> getReservaById(String id) {
        String method = "GET";
        String path = "/reservas/" + (id != null ? id : "");

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou ausente.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        if (id == null || id.trim().isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", "ID da reserva é obrigatório.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        Reserva r = findReservaById(id.trim());
        if (r == null) {
            ApiError err = new ApiError(404, "Not Found", "Reserva não encontrada.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        // Autorização: passageiro, motorista da carona ou admin
        Carona carona = getCaronaById(r.getCarona_id());
        boolean isPassageiro = r.getPassageiro_id().equals(usuarioLogado.getId());
        boolean isMotorista = carona != null && usuarioLogado.getId().equals(carona.getMotorista_id());

        if (!isAdmin() && !isPassageiro && !isMotorista) {
            ApiError err = new ApiError(403, "Forbidden", "Acesso restrito ao passageiro, motorista ou administrador.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        Usuario pass = findUsuarioById(r.getPassageiro_id());
        String passNome = pass != null ? pass.getNome_completo() : "Passageiro";
        ReservaResponse respData = ReservaResponse.fromReserva(r, passNome);

        ApiResponse<ReservaResponse> res = ApiResponse.ok(method, path, 200, respData, null, respData.toJson());
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<List<ReservaResponse>> getReservas() {
        String method = "GET";
        String path = "/reservas";

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou ausente.");
            ApiResponse<List<ReservaResponse>> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        List<ReservaResponse> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            Carona carona = getCaronaById(r.getCarona_id());
            boolean isPassageiro = r.getPassageiro_id().equals(usuarioLogado.getId());
            boolean isMotorista = carona != null && usuarioLogado.getId().equals(carona.getMotorista_id());

            if (isAdmin() || isPassageiro || isMotorista) {
                Usuario pass = findUsuarioById(r.getPassageiro_id());
                String passNome = pass != null ? pass.getNome_completo() : "Passageiro";
                resultado.add(ReservaResponse.fromReserva(r, passNome));
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < resultado.size(); i++) {
            sb.append("  ").append(resultado.get(i).toJson().replace("\n", "\n  "));
            if (i < resultado.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");

        ApiResponse<List<ReservaResponse>> res = ApiResponse.ok(method, path, 200, resultado, null, sb.toString());
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<List<ReservaResponse>> getReservasByUsuario(String usuarioId) {
        String method = "GET";
        String path = "/reservas/usuario/" + (usuarioId != null ? usuarioId : "");

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou ausente.");
            ApiResponse<List<ReservaResponse>> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        if (usuarioId == null || usuarioId.trim().isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", "usuario_id é obrigatório.");
            ApiResponse<List<ReservaResponse>> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        Usuario usuarioAlvo = findUsuarioById(usuarioId.trim());
        if (usuarioAlvo == null) {
            ApiError err = new ApiError(404, "Not Found", "Usuário não encontrado.");
            ApiResponse<List<ReservaResponse>> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        if (!isAdmin() && !usuarioLogado.getId().equals(usuarioId.trim())) {
            ApiError err = new ApiError(403, "Forbidden", "Acesso restrito ao próprio usuário ou administrador.");
            ApiResponse<List<ReservaResponse>> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        List<ReservaResponse> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getPassageiro_id().equals(usuarioId.trim())) {
                resultado.add(ReservaResponse.fromReserva(r, usuarioAlvo.getNome_completo()));
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < resultado.size(); i++) {
            sb.append("  ").append(resultado.get(i).toJson().replace("\n", "\n  "));
            if (i < resultado.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");

        ApiResponse<List<ReservaResponse>> res = ApiResponse.ok(method, path, 200, resultado, null, sb.toString());
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<ReservaResponse> cancelarReserva(String id) {
        String method = "PUT";
        String path = "/reservas/" + (id != null ? id : "") + "/cancelar";

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou ausente.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        if (id == null || id.trim().isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", "ID da reserva é obrigatório.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        Reserva r = findReservaById(id.trim());
        if (r == null) {
            ApiError err = new ApiError(404, "Not Found", "Reserva não encontrada.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        Carona carona = getCaronaById(r.getCarona_id());
        boolean isPassageiro = r.getPassageiro_id().equals(usuarioLogado.getId());
        boolean isMotorista = carona != null && usuarioLogado.getId().equals(carona.getMotorista_id());

        if (!isAdmin() && !isPassageiro && !isMotorista) {
            ApiError err = new ApiError(403, "Forbidden", "Você não tem permissão para cancelar esta reserva.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        if (r.isCancelada()) {
            ApiError err = new ApiError(400, "Bad Request", "A reserva já está cancelada.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        if (r.isRecusada()) {
            ApiError err = new ApiError(400, "Bad Request", "A reserva foi recusada e não pode ser cancelada.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        // Devolve as vagas para a carona
        if (carona != null) {
            carona.devolverVagas(r.getVagas_solicitadas());
        }

        r.setStatus(Reserva.STATUS_CANCELADA);

        Usuario pass = findUsuarioById(r.getPassageiro_id());
        String passNome = pass != null ? pass.getNome_completo() : "Passageiro";
        ReservaResponse respData = ReservaResponse.fromReserva(r, passNome, "Reserva cancelada com sucesso. Vagas liberadas.");

        ApiResponse<ReservaResponse> res = ApiResponse.ok(method, path, 200, respData, null, respData.toJson());
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<ReservaResponse> updateStatusReserva(String id, String novoStatus) {
        String method = "PUT";
        String path = "/reservas/" + (id != null ? id : "") + "/status";
        String reqJson = "{\n  \"status\": " + jsonString(novoStatus) + "\n}";

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticação inválido ou ausente.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (id == null || id.trim().isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", "ID da reserva é obrigatório.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        Reserva r = findReservaById(id.trim());
        if (r == null) {
            ApiError err = new ApiError(404, "Not Found", "Reserva não encontrada.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        Carona carona = getCaronaById(r.getCarona_id());
        boolean isMotorista = carona != null && usuarioLogado.getId().equals(carona.getMotorista_id());

        if (!isAdmin() && !isMotorista) {
            ApiError err = new ApiError(403, "Forbidden", "Apenas o motorista da carona ou administrador podem alterar o status da reserva.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        if (novoStatus == null) {
            ApiError err = new ApiError(400, "Bad Request", "status é obrigatório.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        String statusNormalizado = novoStatus.trim().toUpperCase();
        if (!statusNormalizado.equals(Reserva.STATUS_PENDENTE)
                && !statusNormalizado.equals(Reserva.STATUS_ACEITA)
                && !statusNormalizado.equals(Reserva.STATUS_RECUSADA)
                && !statusNormalizado.equals(Reserva.STATUS_CANCELADA)) {
            ApiError err = new ApiError(400, "Bad Request", "Status inválido: " + novoStatus + ". Valores permitidos: PENDENTE, ACEITA, RECUSADA, CANCELADA.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        if (r.isCancelada()) {
            ApiError err = new ApiError(400, "Bad Request", "Não é permitido alterar o status de uma reserva cancelada.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        if (r.isRecusada() && !statusNormalizado.equals(Reserva.STATUS_RECUSADA)) {
            ApiError err = new ApiError(400, "Bad Request", "Não é permitido alterar o status de uma reserva recusada.");
            ApiResponse<ReservaResponse> res = ApiResponse.fail(method, path, err, reqJson);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        // Se recusada ou cancelada, devolve vagas se a reserva ocupava vaga
        if ((statusNormalizado.equals(Reserva.STATUS_RECUSADA) || statusNormalizado.equals(Reserva.STATUS_CANCELADA))
                && !r.isCancelada() && !r.isRecusada() && carona != null) {
            carona.devolverVagas(r.getVagas_solicitadas());
        }

        r.setStatus(statusNormalizado);

        Usuario pass = findUsuarioById(r.getPassageiro_id());
        String passNome = pass != null ? pass.getNome_completo() : "Passageiro";
        ReservaResponse respData = ReservaResponse.fromReserva(r, passNome, "Status da reserva atualizado para " + statusNormalizado + ".");

        ApiResponse<ReservaResponse> res = ApiResponse.ok(method, path, 200, respData, reqJson, respData.toJson());
        res.addHeader("Authorization", "Bearer " + tokenJwt);
        notifyListeners(res);
        return res;
    }

    @Override
    public List<Carona> getCaronas() {
        return Collections.unmodifiableList(caronas);
    }

    @Override
    public Carona getCaronaById(String id) {
        if (id == null) return null;
        for (Carona c : caronas) {
            if (c.getId().equalsIgnoreCase(id.trim())) return c;
        }
        return null;
    }
}
