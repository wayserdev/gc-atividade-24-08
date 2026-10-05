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
    private final List<Campus> campi = new ArrayList<>();
    private final List<Bairro> bairros = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Veiculo> veiculos = new ArrayList<>();
    private final List<Carona> caronas = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();
    private final List<Avaliacao> avaliacoes = new ArrayList<>();

    private final List<ApiCallListener> listeners = new ArrayList<>();

    private Usuario usuarioLogado = null;
    private String tokenJwt = null;

    public MockApiService() {
        initDefaultData();
        // Não loga automaticamente no construtor para que a primeira tela seja a de LOGIN!
        this.usuarioLogado = null;
        this.tokenJwt = null;
    }

    public void initDefaultData() {
        instituicoes.clear();
        campi.clear();
        bairros.clear();
        usuarios.clear();
        veiculos.clear();
        caronas.clear();
        reservas.clear();
        avaliacoes.clear();

        // 1. Instituições
        Instituicao ueg = new Instituicao("c1a2b3c4-0000-0000-0000-000000000000", "Universidade Estadual de Goias", "UEG");
        Instituicao ufg = new Instituicao("d2b3c4d5-0000-0000-0000-000000000000", "Universidade Federal de Goias", "UFG");
        Instituicao ifg = new Instituicao("e3c4d5e6-0000-0000-0000-000000000000", "Instituto Federal de Goias", "IFG");
        instituicoes.add(ueg);
        instituicoes.add(ufg);
        instituicoes.add(ifg);

        // 2. Campi
        Campus uegCentral = new Campus("cp-ueg-1", ueg.getId(), ueg.getSigla(), "Campus Central Anapolis", "Anapolis", "BR-153, Km 99");
        Campus ufgSamambaia = new Campus("cp-ufg-1", ufg.getId(), ufg.getSigla(), "Campus Samambaia", "Goiania", "Av. Esperanca, s/n - Campus Universitario");
        Campus ufgColemar = new Campus("cp-ufg-2", ufg.getId(), ufg.getSigla(), "Campus Colemar Natal e Silva", "Goiania", "Praca Universitaria, Setor Leste Universitario");
        Campus ifgGoiania = new Campus("cp-ifg-1", ifg.getId(), ifg.getSigla(), "Campus Goiania Central", "Goiania", "Rua 75, n 46 - Setor Central");
        campi.add(uegCentral);
        campi.add(ufgSamambaia);
        campi.add(ufgColemar);
        campi.add(ifgGoiania);

        // 3. Bairros
        Bairro bBueno = new Bairro("b-1", "Setor Bueno", "Goiania");
        Bairro bUniv = new Bairro("b-2", "Setor Universitario", "Goiania");
        Bairro bMarista = new Bairro("b-3", "Setor Marista", "Goiania");
        Bairro bOeste = new Bairro("b-4", "Setor Oeste", "Goiania");
        Bairro bJardimGoias = new Bairro("b-5", "Jardim Goias", "Goiania");
        Bairro bCentro = new Bairro("b-6", "Setor Central", "Goiania");
        Bairro bNegrao = new Bairro("b-7", "Setor Negrao de Lima", "Goiania");
        Bairro bJundiai = new Bairro("b-8", "Bairro Jundiai", "Anapolis");
        Bairro bSaoCarlos = new Bairro("b-9", "Setor Sao Carlos", "Anapolis");
        bairros.add(bBueno);
        bairros.add(bUniv);
        bairros.add(bMarista);
        bairros.add(bOeste);
        bairros.add(bJardimGoias);
        bairros.add(bCentro);
        bairros.add(bNegrao);
        bairros.add(bJundiai);
        bairros.add(bSaoCarlos);

        // 4. Usuários
        Usuario alunoCarlos = new Usuario(
                "u1u2u3u4-0000-0000-0000-000000000000",
                ueg.getId(),
                ueg.getNome(),
                "Carlos Eduardo",
                "carlos.edu@gmail.com",
                "senhaSegura123",
                "ALUNO",
                "62999998888",
                "Engenharia de Software",
                null,
                "4.85",
                "2026-08-27T21:00:00.000Z"
        );

        Usuario adminCarlos = new Usuario(
                "a1a2a3a4-0000-0000-0000-000000000000",
                ueg.getId(),
                ueg.getNome(),
                "Carlos Eduardo Admin",
                "carlos.admin@faculdade.br",
                "admin123",
                "ADMIN",
                "62999998888",
                "Docente / Coordenacao",
                null,
                "4.95",
                "2026-08-20T14:30:00.000Z"
        );

        Usuario alunoMariana = new Usuario(
                "u-mariana-001",
                ufg.getId(),
                ufg.getNome(),
                "Mariana Oliveira Santos",
                "mariana.oli@gmail.com",
                "senha123",
                "ALUNO",
                "62981112233",
                "Ciencia da Computacao",
                null,
                "4.92",
                "2026-08-28T10:15:00.000Z"
        );

        Usuario alunoLucas = new Usuario(
                "u-lucas-002",
                ueg.getId(),
                ueg.getNome(),
                "Lucas Ribeiro Mendes",
                "lucas.rib@gmail.com",
                "senha123",
                "ALUNO",
                "62992223344",
                "Sistemas de Informacao",
                null,
                "4.70",
                "2026-08-28T12:00:00.000Z"
        );

        Usuario alunoBeatriz = new Usuario(
                "u-beatriz-003",
                ifg.getId(),
                ifg.getNome(),
                "Beatriz Santos Pereira",
                "beatriz.s@gmail.com",
                "senha123",
                "ALUNO",
                "62993334455",
                "Engenharia Eletrica",
                null,
                "4.60",
                "2026-08-29T08:45:00.000Z"
        );

        Usuario adminAna = new Usuario(
                "a-ana-004",
                ufg.getId(),
                ufg.getNome(),
                "Ana Carolina Lima",
                "ana.lima@faculdade.br",
                "admin123",
                "ADMIN",
                "62984445566",
                "Coordenacao de Transportes",
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

        // Usuários extras para paginação
        String[] nomes = {"Rodrigo Alves", "Fernanda Costa", "Gabriel Moreira", "Juliana Paes", "Felipe Silva",
                "Camila Rocha", "Rafael Nogueira", "Larissa Dias", "Thiago Martins", "Amanda Barbosa"};
        String[] cursos = {"Engenharia de Software", "Ciencia da Computacao", "Sistemas de Informacao", "Direito", "Administracao"};
        for (int i = 0; i < nomes.length; i++) {
            String nome = nomes[i];
            String email = nome.toLowerCase().replace(" ", ".") + "@aluno.ueg.br";
            Instituicao inst = instituicoes.get(i % instituicoes.size());
            String curso = cursos[i % cursos.length];
            usuarios.add(new Usuario(
                    "u-extra-" + (i + 1),
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
                    "2026-08-30T10:00:00.000Z"
            ));
        }

        // 5. Veículos
        Veiculo vCarlos = new Veiculo("v-carlos-1", alunoCarlos.getId(), "Chevrolet Onix Plus", "BRA-2E19", 4);
        Veiculo vMariana = new Veiculo("v-mariana-1", alunoMariana.getId(), "Hyundai HB20 Comfort", "GOI-4B22", 4);
        Veiculo vLucas = new Veiculo("v-lucas-1", alunoLucas.getId(), "Volkswagen Gol 1.6", "KDX-9981", 4);
        veiculos.add(vCarlos);
        veiculos.add(vMariana);
        veiculos.add(vLucas);

        // 6. Caronas Disponíveis
        Carona c1 = new Carona(
                "c-001",
                alunoCarlos.getId(), alunoCarlos.getNome_completo(), null, alunoCarlos.getTelefone(), alunoCarlos.getCurso(), alunoCarlos.getMedia_avaliacao(),
                vCarlos.getId(), vCarlos.getModelo(), vCarlos.getPlaca(), vCarlos.getCapacidade_assentos(),
                "BAIRRO_PARA_CAMPUS",
                bBueno.getId(), bBueno.getNome(),
                ufgSamambaia.getId(), ufgSamambaia.getNome(), ufg.getSigla(), "Goiania",
                "Em frente a Praca da T-25 (Ponto de Onibus)",
                "Hoje, 07:30",
                4, 2, 6.00,
                "AGENDADA", "2026-08-30T06:00:00.000Z"
        );

        Carona c2 = new Carona(
                "c-002",
                alunoMariana.getId(), alunoMariana.getNome_completo(), null, alunoMariana.getTelefone(), alunoMariana.getCurso(), alunoMariana.getMedia_avaliacao(),
                vMariana.getId(), vMariana.getModelo(), vMariana.getPlaca(), vMariana.getCapacidade_assentos(),
                "BAIRRO_PARA_CAMPUS",
                bUniv.getId(), bUniv.getNome(),
                uegCentral.getId(), uegCentral.getNome(), ueg.getSigla(), "Anapolis",
                "Posto Ipiranga na saida da BR-153",
                "Hoje, 18:30",
                4, 3, 7.50,
                "AGENDADA", "2026-08-30T09:00:00.000Z"
        );

        Carona c3 = new Carona(
                "c-003",
                alunoLucas.getId(), alunoLucas.getNome_completo(), null, alunoLucas.getTelefone(), alunoLucas.getCurso(), alunoLucas.getMedia_avaliacao(),
                vLucas.getId(), vLucas.getModelo(), vLucas.getPlaca(), vLucas.getCapacidade_assentos(),
                "CAMPUS_PARA_BAIRRO",
                bJardimGoias.getId(), bJardimGoias.getNome(),
                ufgSamambaia.getId(), ufgSamambaia.getNome(), ufg.getSigla(), "Goiania",
                "Estacionamento do Bloco de Engenharias",
                "Hoje, 22:15",
                4, 3, 5.00,
                "AGENDADA", "2026-08-30T11:30:00.000Z"
        );

        Carona c4 = new Carona(
                "c-004",
                alunoCarlos.getId(), alunoCarlos.getNome_completo(), null, alunoCarlos.getTelefone(), alunoCarlos.getCurso(), alunoCarlos.getMedia_avaliacao(),
                vCarlos.getId(), vCarlos.getModelo(), vCarlos.getPlaca(), vCarlos.getCapacidade_assentos(),
                "CAMPUS_PARA_BAIRRO",
                bBueno.getId(), bBueno.getNome(),
                ufgSamambaia.getId(), ufgSamambaia.getNome(), ufg.getSigla(), "Goiania",
                "Portaria Principal Samambaia",
                "Ontem, 18:00",
                4, 0, 6.00,
                "FINALIZADA", "2026-08-29T15:00:00.000Z"
        );

        Carona c5 = new Carona(
                "c-005",
                alunoMariana.getId(), alunoMariana.getNome_completo(), null, alunoMariana.getTelefone(), alunoMariana.getCurso(), alunoMariana.getMedia_avaliacao(),
                vMariana.getId(), vMariana.getModelo(), vMariana.getPlaca(), vMariana.getCapacidade_assentos(),
                "BAIRRO_PARA_CAMPUS",
                bMarista.getId(), bMarista.getNome(),
                ufgColemar.getId(), ufgColemar.getNome(), ufg.getSigla(), "Goiania",
                "Alameda Ricardo Paranhos com Rua 1131",
                "Hoje, 13:45",
                4, 1, 5.00,
                "EM_ANDAMENTO", "2026-08-30T12:00:00.000Z"
        );

        caronas.add(c1);
        caronas.add(c2);
        caronas.add(c3);
        caronas.add(c4);
        caronas.add(c5);

        // 7. Reservas
        Reserva r1 = new Reserva(
                "r-001",
                c1.getId(),
                alunoMariana.getId(), alunoMariana.getNome_completo(), alunoMariana.getEmail(), alunoMariana.getCurso(), alunoMariana.getTelefone(), null, alunoMariana.getMedia_avaliacao(),
                1, "Praca da T-25", "ACEITA", "2026-08-30T07:00:00.000Z"
        );
        r1.setCarona(c1);

        Reserva r2 = new Reserva(
                "r-002",
                c1.getId(),
                alunoLucas.getId(), alunoLucas.getNome_completo(), alunoLucas.getEmail(), alunoLucas.getCurso(), alunoLucas.getTelefone(), null, alunoLucas.getMedia_avaliacao(),
                1, "Cruzamento T-25 com T-63", "PENDENTE", "2026-08-30T07:10:00.000Z"
        );
        r2.setCarona(c1);

        Reserva r3 = new Reserva(
                "r-003",
                c4.getId(),
                alunoMariana.getId(), alunoMariana.getNome_completo(), alunoMariana.getEmail(), alunoMariana.getCurso(), alunoMariana.getTelefone(), null, alunoMariana.getMedia_avaliacao(),
                1, "Portaria Samambaia", "ACEITA", "2026-08-29T16:00:00.000Z"
        );
        r3.setCarona(c4);

        Reserva r4 = new Reserva(
                "r-004",
                c2.getId(),
                alunoCarlos.getId(), alunoCarlos.getNome_completo(), alunoCarlos.getEmail(), alunoCarlos.getCurso(), alunoCarlos.getTelefone(), null, alunoCarlos.getMedia_avaliacao(),
                1, "Em frente a Drogaria Pacheco", "ACEITA", "2026-08-30T10:00:00.000Z"
        );
        r4.setCarona(c2);

        reservas.add(r1);
        reservas.add(r2);
        reservas.add(r3);
        reservas.add(r4);

        // 8. Avaliações
        avaliacoes.add(new Avaliacao(
                "av-001",
                c4.getId(),
                alunoMariana.getId(), alunoMariana.getNome_completo(), null,
                alunoCarlos.getId(), alunoCarlos.getNome_completo(),
                5, "Carlos e super gente boa e muito pontual! Carro limpo e direcao super segura.", "COMO_PASSAGEIRO", "2026-08-29T19:30:00.000Z"
        ));

        avaliacoes.add(new Avaliacao(
                "av-002",
                c4.getId(),
                alunoCarlos.getId(), alunoCarlos.getNome_completo(), null,
                alunoMariana.getId(), alunoMariana.getNome_completo(),
                5, "Mariana estava no ponto no horario combinado. Otima conversa durante o trajeto.", "COMO_MOTORISTA", "2026-08-29T19:40:00.000Z"
        ));

        avaliacoes.add(new Avaliacao(
                "av-003",
                "c-old-1",
                alunoLucas.getId(), alunoLucas.getNome_completo(), null,
                alunoCarlos.getId(), alunoCarlos.getNome_completo(),
                5, "Excelente carona! Ajudou demais para chegar a tempo da prova.", "COMO_PASSAGEIRO", "2026-08-25T14:00:00.000Z"
        ));
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
            try {
                listener.onApiCall(response);
            } catch (Exception ignored) {}
        }
    }

    // ==========================================
    // AUTH & USUARIOS
    // ==========================================

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
            validacaoErros.add("email deve ser um e-mail valido");
        }
        if (senha == null || senha.length() < 6) {
            validacaoErros.add("senha deve ter no minimo 6 caracteres");
        }
        if (telefone == null || !telefone.replaceAll("[^0-9]", "").matches("\\d{10,11}")) {
            validacaoErros.add("telefone deve ser uma string numerica com DDD (10 ou 11 digitos)");
        }
        if (curso == null || curso.trim().isEmpty()) {
            validacaoErros.add("curso e obrigatorio");
        }
        if (instituicaoId == null || instituicaoId.trim().isEmpty()) {
            validacaoErros.add("instituicao_id e obrigatorio");
        }

        if (!validacaoErros.isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", validacaoErros);
            ApiResponse<AuthResponse> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                ApiError err = new ApiError(409, "Conflict", "Este e-mail ja esta em uso por outro usuario.");
                ApiResponse<AuthResponse> res = ApiResponse.fail(method, path, err, reqJson);
                notifyListeners(res);
                return res;
            }
        }

        Instituicao inst = getInstituicaoById(instituicaoId);
        String instNome = inst != null ? inst.getNome() : "Universidade Estadual";

        String novoId = "u-" + UUID.randomUUID().toString().substring(0, 8);
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

        String respJson = String.format("{\n  \"token\": \"%s\",\n  \"usuario\": {\n    \"id\": \"%s\",\n    \"nome_completo\": \"%s\",\n    \"email\": \"%s\",\n    \"nivel\": \"%s\",\n    \"telefone\": \"%s\",\n    \"curso\": \"%s\"\n  }\n}",
                token, novoId, novoUsuario.getNome_completo(), novoUsuario.getEmail(), novoUsuario.getNivel(), novoUsuario.getTelefone(), novoUsuario.getCurso());

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
            ApiError err = new ApiError(400, "Bad Request", "email e senha sao obrigatorios.");
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
            ApiError err = new ApiError(401, "Unauthorized", "E-mail ou senha invalidos.");
            ApiResponse<AuthResponse> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        String token = generateFakeJwt(encontrado);
        setUsuarioLogado(encontrado, token);

        String respJson = String.format("{\n  \"token\": \"%s\",\n  \"usuario\": {\n    \"id\": \"%s\",\n    \"nome_completo\": \"%s\",\n    \"email\": \"%s\",\n    \"nivel\": \"%s\"\n  }\n}",
                token, encontrado.getId(), encontrado.getNome_completo(), encontrado.getEmail(), encontrado.getNivel());

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
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticacao invalido ou expirado.");
            ApiResponse<Usuario> res = ApiResponse.fail(method, path, err, null);
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
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticacao invalido ou expirado.");
            ApiResponse<Usuario> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (nome != null && !nome.trim().isEmpty()) usuarioLogado.setNome_completo(nome.trim());
        if (telefone != null && !telefone.trim().isEmpty()) usuarioLogado.setTelefone(telefone.replaceAll("[^0-9]", ""));
        if (curso != null && !curso.trim().isEmpty()) usuarioLogado.setCurso(curso.trim());
        usuarioLogado.setFoto_url(fotoUrl != null && !fotoUrl.trim().isEmpty() ? fotoUrl.trim() : null);

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
        String query = String.format("?busca=%s&nivel=%s&pagina=%d&limite=%d", busca != null ? busca : "", nivel != null ? nivel : "", pagina, limite);
        String path = "/admin/usuarios" + query;

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticacao invalido ou expirado.");
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
        ApiResponse<PageResult<Usuario>> res = ApiResponse.ok(method, path, 200, pageResult, null, "{\n  \"total\": " + total + ",\n  \"usuarios\": " + paginados.size() + "\n}");
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
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticacao invalido ou expirado.");
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
            ApiError err = new ApiError(404, "Not Found", "Usuario nao encontrado.");
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

        if (usuarioLogado != null && usuarioLogado.getId().equals(target.getId())) {
            usuarioLogado = target.clone();
        }

        String respJson = "{\n  \"mensagem\": \"Usuario atualizado com sucesso pelo administrador.\",\n  \"usuario\": " + usuarioToJson(target) + "\n}";
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
            ApiError err = new ApiError(401, "Unauthorized", "Token de autenticacao invalido ou expirado.");
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

        if (usuarioLogado != null && usuarioLogado.getId().equals(id)) {
            ApiError err = new ApiError(400, "Bad Request", "O Administrador nao pode deletar a si mesmo (evitar auto-bloqueio).");
            ApiResponse<String> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        boolean removed = usuarios.removeIf(u -> u.getId().equals(id));
        if (!removed) {
            ApiError err = new ApiError(404, "Not Found", "Usuario nao encontrado.");
            ApiResponse<String> res = ApiResponse.fail(method, path, err, null);
            res.addHeader("Authorization", "Bearer " + tokenJwt);
            notifyListeners(res);
            return res;
        }

        String respJson = "{\n  \"mensagem\": \"Usuario removido da base de dados com sucesso.\"\n}";
        ApiResponse<String> res = ApiResponse.ok(method, path, 200, "Usuario removido da base de dados com sucesso.", null, respJson);
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

    // ==========================================
    // INSTITUIÇÕES, CAMPI & BAIRROS
    // ==========================================

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
    public List<Campus> getCampi() {
        return Collections.unmodifiableList(campi);
    }

    @Override
    public List<Campus> getCampiByInstituicao(String instituicaoId) {
        if (instituicaoId == null) return getCampi();
        return campi.stream().filter(c -> c.getInstituicao_id().equalsIgnoreCase(instituicaoId)).collect(Collectors.toList());
    }

    @Override
    public Campus getCampusById(String id) {
        if (id == null) return null;
        for (Campus c : campi) {
            if (c.getId().equalsIgnoreCase(id)) return c;
        }
        return null;
    }

    @Override
    public List<Bairro> getBairros() {
        return Collections.unmodifiableList(bairros);
    }

    @Override
    public Bairro getBairroById(String id) {
        if (id == null) return null;
        for (Bairro b : bairros) {
            if (b.getId().equalsIgnoreCase(id)) return b;
        }
        return null;
    }

    @Override
    public ApiResponse<Bairro> cadastrarBairro(String nome, String cidade) {
        String method = "POST";
        String path = "/bairros";
        String reqJson = String.format("{\n  \"nome\": \"%s\",\n  \"cidade\": \"%s\"\n}", nome, cidade);

        if (nome == null || nome.trim().isEmpty() || cidade == null || cidade.trim().isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", "Nome e Cidade do bairro sao obrigatorios.");
            ApiResponse<Bairro> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Bairro novo = new Bairro("b-" + UUID.randomUUID().toString().substring(0, 8), nome.trim(), cidade.trim());
        bairros.add(novo);

        String respJson = String.format("{\n  \"id\": \"%s\",\n  \"nome\": \"%s\",\n  \"cidade\": \"%s\"\n}", novo.getId(), novo.getNome(), novo.getCidade());
        ApiResponse<Bairro> res = ApiResponse.ok(method, path, 201, novo, reqJson, respJson);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Campus> cadastrarCampus(String instituicaoId, String nome, String cidade, String endereco) {
        String method = "POST";
        String path = "/campi";
        String reqJson = String.format("{\n  \"instituicao_id\": \"%s\",\n  \"nome\": \"%s\",\n  \"cidade\": \"%s\",\n  \"endereco_referencia\": \"%s\"\n}", instituicaoId, nome, cidade, endereco);

        Instituicao inst = getInstituicaoById(instituicaoId);
        if (inst == null || nome == null || nome.trim().isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", "Instituicao valida e nome do Campus sao obrigatorios.");
            ApiResponse<Campus> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Campus novo = new Campus("cp-" + UUID.randomUUID().toString().substring(0, 8), inst.getId(), inst.getSigla(), nome.trim(), cidade != null ? cidade.trim() : "Goiania", endereco);
        campi.add(novo);

        ApiResponse<Campus> res = ApiResponse.ok(method, path, 201, novo, reqJson, "{\n  \"id\": \"" + novo.getId() + "\",\n  \"nome\": \"" + novo.getNome() + "\"\n}");
        notifyListeners(res);
        return res;
    }

    // ==========================================
    // VEÍCULOS
    // ==========================================

    @Override
    public ApiResponse<List<Veiculo>> getMeusVeiculos() {
        String method = "GET";
        String path = "/veiculos";

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<List<Veiculo>> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        List<Veiculo> meus = veiculos.stream()
                .filter(v -> v.getMotorista_id().equals(usuarioLogado.getId()))
                .collect(Collectors.toList());

        ApiResponse<List<Veiculo>> res = ApiResponse.ok(method, path, 200, meus, null, "{\n  \"total\": " + meus.size() + "\n}");
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Veiculo> cadastrarVeiculo(String modelo, String placa, int capacidade) {
        String method = "POST";
        String path = "/veiculos";
        String reqJson = String.format("{\n  \"modelo\": \"%s\",\n  \"placa\": \"%s\",\n  \"capacidade_assentos\": %d\n}", modelo, placa, capacidade);

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<Veiculo> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        List<String> erros = new ArrayList<>();
        if (modelo == null || modelo.trim().length() < 2) erros.add("Modelo do veiculo e obrigatorio (minimo 2 letras).");
        if (placa == null || placa.trim().length() < 7) erros.add("Placa do veiculo invalida.");
        if (capacidade < 1 || capacidade > 8) erros.add("Capacidade deve ser entre 1 e 8 assentos.");

        if (!erros.isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", erros);
            ApiResponse<Veiculo> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        for (Veiculo v : veiculos) {
            if (v.getPlaca().equalsIgnoreCase(placa.trim())) {
                ApiError err = new ApiError(409, "Conflict", "Esta placa ja esta cadastrada no sistema.");
                ApiResponse<Veiculo> res = ApiResponse.fail(method, path, err, reqJson);
                notifyListeners(res);
                return res;
            }
        }

        Veiculo novo = new Veiculo("v-" + UUID.randomUUID().toString().substring(0, 8), usuarioLogado.getId(), modelo.trim(), placa.trim().toUpperCase(), capacidade);
        veiculos.add(novo);

        String respJson = String.format("{\n  \"id\": \"%s\",\n  \"modelo\": \"%s\",\n  \"placa\": \"%s\",\n  \"capacidade_assentos\": %d\n}", novo.getId(), novo.getModelo(), novo.getPlaca(), novo.getCapacidade_assentos());
        ApiResponse<Veiculo> res = ApiResponse.ok(method, path, 201, novo, reqJson, respJson);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Veiculo> updateVeiculo(String id, String modelo, String placa, int capacidade) {
        String method = "PUT";
        String path = "/veiculos/" + id;
        String reqJson = String.format("{\n  \"modelo\": \"%s\",\n  \"placa\": \"%s\",\n  \"capacidade_assentos\": %d\n}", modelo, placa, capacidade);

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<Veiculo> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Veiculo target = null;
        for (Veiculo v : veiculos) {
            if (v.getId().equals(id) && v.getMotorista_id().equals(usuarioLogado.getId())) {
                target = v;
                break;
            }
        }

        if (target == null) {
            ApiError err = new ApiError(404, "Not Found", "Veiculo nao encontrado ou nao pertence a voce.");
            ApiResponse<Veiculo> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (modelo != null && !modelo.trim().isEmpty()) target.setModelo(modelo.trim());
        if (placa != null && !placa.trim().isEmpty()) target.setPlaca(placa.trim().toUpperCase());
        if (capacidade >= 1 && capacidade <= 8) target.setCapacidade_assentos(capacidade);

        ApiResponse<Veiculo> res = ApiResponse.ok(method, path, 200, target, reqJson, "{\n  \"mensagem\": \"Veiculo atualizado com sucesso.\"\n}");
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<String> deleteVeiculo(String id) {
        String method = "DELETE";
        String path = "/veiculos/" + id;

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<String> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        boolean removed = veiculos.removeIf(v -> v.getId().equals(id) && v.getMotorista_id().equals(usuarioLogado.getId()));
        if (!removed) {
            ApiError err = new ApiError(404, "Not Found", "Veiculo nao encontrado.");
            ApiResponse<String> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        ApiResponse<String> res = ApiResponse.ok(method, path, 200, "Veiculo excluido com sucesso.", null, "{\n  \"mensagem\": \"Veiculo excluido com sucesso.\"\n}");
        notifyListeners(res);
        return res;
    }

    // ==========================================
    // CARONAS
    // ==========================================

    @Override
    public ApiResponse<List<Carona>> buscarCaronas(String bairroId, String campusId, String direcao, String busca, String status) {
        String method = "GET";
        String path = "/caronas";

        List<Carona> filtradas = caronas.stream().filter(c -> {
            boolean matchStatus = true;
            if (status != null && !status.trim().isEmpty() && !"TODAS".equalsIgnoreCase(status)) {
                matchStatus = c.getStatus().equalsIgnoreCase(status);
            } else {
                if (status == null || "TODAS_ATIVAS".equalsIgnoreCase(status) || "".equals(status.trim())) {
                    matchStatus = "AGENDADA".equalsIgnoreCase(c.getStatus()) || "EM_ANDAMENTO".equalsIgnoreCase(c.getStatus());
                }
            }

            boolean matchBairro = true;
            if (bairroId != null && !bairroId.trim().isEmpty() && !"TODOS".equalsIgnoreCase(bairroId)) {
                matchBairro = c.getBairro_id().equalsIgnoreCase(bairroId);
            }

            boolean matchCampus = true;
            if (campusId != null && !campusId.trim().isEmpty() && !"TODOS".equalsIgnoreCase(campusId)) {
                matchCampus = c.getCampus_id().equalsIgnoreCase(campusId);
            }

            boolean matchDirecao = true;
            if (direcao != null && !direcao.trim().isEmpty() && !"TODAS".equalsIgnoreCase(direcao)) {
                matchDirecao = c.getDirecao().equalsIgnoreCase(direcao);
            }

            boolean matchBusca = true;
            if (busca != null && !busca.trim().isEmpty()) {
                String b = busca.trim().toLowerCase();
                matchBusca = (c.getMotorista_nome() != null && c.getMotorista_nome().toLowerCase().contains(b))
                        || (c.getBairro_nome() != null && c.getBairro_nome().toLowerCase().contains(b))
                        || (c.getCampus_nome() != null && c.getCampus_nome().toLowerCase().contains(b))
                        || (c.getPonto_encontro() != null && c.getPonto_encontro().toLowerCase().contains(b));
            }

            return matchStatus && matchBairro && matchCampus && matchDirecao && matchBusca;
        }).collect(Collectors.toList());

        ApiResponse<List<Carona>> res = ApiResponse.ok(method, path, 200, filtradas, null, "{\n  \"total\": " + filtradas.size() + "\n}");
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Carona> getCaronaById(String id) {
        String method = "GET";
        String path = "/caronas/" + id;

        for (Carona c : caronas) {
            if (c.getId().equals(id)) {
                ApiResponse<Carona> res = ApiResponse.ok(method, path, 200, c, null, "{\n  \"id\": \"" + c.getId() + "\"\n}");
                notifyListeners(res);
                return res;
            }
        }

        ApiError err = new ApiError(404, "Not Found", "Carona nao encontrada.");
        ApiResponse<Carona> res = ApiResponse.fail(method, path, err, null);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<List<Carona>> getMinhasCaronas() {
        String method = "GET";
        String path = "/caronas/minhas";

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<List<Carona>> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        List<Carona> minhas = caronas.stream()
                .filter(c -> c.getMotorista_id().equals(usuarioLogado.getId()))
                .collect(Collectors.toList());

        ApiResponse<List<Carona>> res = ApiResponse.ok(method, path, 200, minhas, null, "{\n  \"total\": " + minhas.size() + "\n}");
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Carona> oferecerCarona(String veiculoId, String direcao, String bairroId, String campusId,
                                              String pontoEncontro, String horarioSaida, int assentosTotais, double valorContribuicao) {
        String method = "POST";
        String path = "/caronas";
        String reqJson = String.format("{\n  \"veiculo_id\": \"%s\",\n  \"direcao\": \"%s\",\n  \"bairro_id\": \"%s\",\n  \"campus_id\": \"%s\",\n  \"ponto_encontro\": \"%s\",\n  \"horario_saida\": \"%s\",\n  \"assentos_totais\": %d,\n  \"valor_contribuicao\": %.2f\n}",
                veiculoId, direcao, bairroId, campusId, pontoEncontro, horarioSaida, assentosTotais, valorContribuicao);

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<Carona> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        List<String> erros = new ArrayList<>();
        if (veiculoId == null || veiculoId.trim().isEmpty()) erros.add("Selecione um veiculo cadastrado.");
        if (bairroId == null || bairroId.trim().isEmpty()) erros.add("Selecione o bairro.");
        if (campusId == null || campusId.trim().isEmpty()) erros.add("Selecione o campus.");
        if (horarioSaida == null || horarioSaida.trim().isEmpty()) erros.add("Horario de saida e obrigatorio.");
        if (assentosTotais <= 0 || assentosTotais > 6) erros.add("Assentos totais deve ser entre 1 e 6.");
        if (valorContribuicao < 0) erros.add("Valor de contribuicao nao pode ser negativo.");

        if (!erros.isEmpty()) {
            ApiError err = new ApiError(400, "Bad Request", erros);
            ApiResponse<Carona> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Veiculo veiculo = null;
        for (Veiculo v : veiculos) {
            if (v.getId().equals(veiculoId)) {
                veiculo = v;
                break;
            }
        }
        if (veiculo == null) {
            ApiError err = new ApiError(404, "Not Found", "Veiculo nao encontrado.");
            ApiResponse<Carona> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Bairro bairro = getBairroById(bairroId);
        Campus campus = getCampusById(campusId);

        String novoId = "c-" + UUID.randomUUID().toString().substring(0, 8);
        String criadoEm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        Carona nova = new Carona(
                novoId,
                usuarioLogado.getId(),
                usuarioLogado.getNome_completo(),
                null,
                usuarioLogado.getTelefone(),
                usuarioLogado.getCurso(),
                usuarioLogado.getMedia_avaliacao(),
                veiculo.getId(),
                veiculo.getModelo(),
                veiculo.getPlaca(),
                veiculo.getCapacidade_assentos(),
                direcao != null ? direcao : "BAIRRO_PARA_CAMPUS",
                bairro != null ? bairro.getId() : bairroId,
                bairro != null ? bairro.getNome() : "Bairro",
                campus != null ? campus.getId() : campusId,
                campus != null ? campus.getNome() : "Campus",
                campus != null ? campus.getInstituicao_nome() : "Universidade",
                campus != null ? campus.getCidade() : "Goiania",
                pontoEncontro != null ? pontoEncontro.trim() : "A combinar",
                horarioSaida.trim(),
                assentosTotais,
                assentosTotais,
                valorContribuicao,
                "AGENDADA",
                criadoEm
        );

        caronas.add(0, nova);

        String respJson = String.format("{\n  \"mensagem\": \"Carona cadastrada com sucesso!\",\n  \"carona_id\": \"%s\"\n}", novoId);
        ApiResponse<Carona> res = ApiResponse.ok(method, path, 201, nova, reqJson, respJson);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Carona> atualizarStatusCarona(String caronaId, String novoStatus) {
        String method = "PATCH";
        String path = "/caronas/" + caronaId + "/status";
        String reqJson = String.format("{\n  \"status\": \"%s\"\n}", novoStatus);

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<Carona> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Carona target = null;
        for (Carona c : caronas) {
            if (c.getId().equals(caronaId)) {
                target = c;
                break;
            }
        }

        if (target == null) {
            ApiError err = new ApiError(404, "Not Found", "Carona nao encontrada.");
            ApiResponse<Carona> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (!target.getMotorista_id().equals(usuarioLogado.getId()) && !isAdmin()) {
            ApiError err = new ApiError(403, "Forbidden", "Apenas o motorista ou um administrador pode alterar o status desta carona.");
            ApiResponse<Carona> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        target.setStatus(novoStatus.toUpperCase());

        ApiResponse<Carona> res = ApiResponse.ok(method, path, 200, target, reqJson, "{\n  \"mensagem\": \"Status da carona atualizado para " + target.getStatus() + "\"\n}");
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Carona> cancelarCarona(String caronaId, String motivo) {
        return atualizarStatusCarona(caronaId, "CANCELADA");
    }

    // ==========================================
    // RESERVAS
    // ==========================================

    @Override
    public ApiResponse<Reserva> solicitarCarona(String caronaId, int vagas, String pontoEmbarque) {
        String method = "POST";
        String path = "/reservas";
        String reqJson = String.format("{\n  \"carona_id\": \"%s\",\n  \"vagas_solicitadas\": %d,\n  \"ponto_embarque\": \"%s\"\n}", caronaId, vagas, pontoEmbarque);

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<Reserva> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Carona carona = null;
        for (Carona c : caronas) {
            if (c.getId().equals(caronaId)) {
                carona = c;
                break;
            }
        }

        if (carona == null) {
            ApiError err = new ApiError(404, "Not Found", "Carona nao encontrada.");
            ApiResponse<Reserva> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (carona.getMotorista_id().equals(usuarioLogado.getId())) {
            ApiError err = new ApiError(400, "Bad Request", "Voce nao pode reservar vaga na sua propria carona.");
            ApiResponse<Reserva> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (!"AGENDADA".equalsIgnoreCase(carona.getStatus())) {
            ApiError err = new ApiError(400, "Bad Request", "Esta carona nao esta mais disponivel para reservas (Status: " + carona.getStatus() + ").");
            ApiResponse<Reserva> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (vagas <= 0 || vagas > carona.getAssentos_disponiveis()) {
            ApiError err = new ApiError(400, "Bad Request", "Numero de vagas solicitadas (" + vagas + ") indisponivel. Restam apenas " + carona.getAssentos_disponiveis() + " vagas.");
            ApiResponse<Reserva> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        for (Reserva r : reservas) {
            if (r.getCarona_id().equals(caronaId) && r.getPassageiro_id().equals(usuarioLogado.getId()) && !"CANCELADA".equalsIgnoreCase(r.getStatus())) {
                ApiError err = new ApiError(409, "Conflict", "Voce ja possui uma solicitacao ou reserva para esta carona.");
                ApiResponse<Reserva> res = ApiResponse.fail(method, path, err, reqJson);
                notifyListeners(res);
                return res;
            }
        }

        String novoId = "r-" + UUID.randomUUID().toString().substring(0, 8);
        String criadoEm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        Reserva nova = new Reserva(
                novoId,
                caronaId,
                usuarioLogado.getId(),
                usuarioLogado.getNome_completo(),
                usuarioLogado.getEmail(),
                usuarioLogado.getCurso(),
                usuarioLogado.getTelefone(),
                null,
                usuarioLogado.getMedia_avaliacao(),
                vagas,
                pontoEmbarque != null ? pontoEmbarque.trim() : "Ponto padrao",
                "PENDENTE",
                criadoEm
        );
        nova.setCarona(carona);
        reservas.add(0, nova);

        String respJson = String.format("{\n  \"mensagem\": \"Solicitacao de carona enviada ao motorista!\",\n  \"reserva_id\": \"%s\",\n  \"status\": \"PENDENTE\"\n}", novoId);
        ApiResponse<Reserva> res = ApiResponse.ok(method, path, 201, nova, reqJson, respJson);
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<List<Reserva>> getMinhasReservas() {
        String method = "GET";
        String path = "/reservas/minhas";

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<List<Reserva>> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        List<Reserva> minhas = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getPassageiro_id().equals(usuarioLogado.getId())) {
                if (r.getCarona() == null) {
                    for (Carona c : caronas) {
                        if (c.getId().equals(r.getCarona_id())) {
                            r.setCarona(c);
                            break;
                        }
                    }
                }
                minhas.add(r);
            }
        }

        ApiResponse<List<Reserva>> res = ApiResponse.ok(method, path, 200, minhas, null, "{\n  \"total\": " + minhas.size() + "\n}");
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<List<Reserva>> getReservasDaCarona(String caronaId) {
        String method = "GET";
        String path = "/caronas/" + caronaId + "/reservas";

        List<Reserva> filtradas = reservas.stream()
                .filter(r -> r.getCarona_id().equals(caronaId))
                .collect(Collectors.toList());

        ApiResponse<List<Reserva>> res = ApiResponse.ok(method, path, 200, filtradas, null, "{\n  \"total\": " + filtradas.size() + "\n}");
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<Reserva> responderReserva(String reservaId, boolean aceitar) {
        String method = "PATCH";
        String path = "/reservas/" + reservaId + "/resposta";
        String reqJson = String.format("{\n  \"aceitar\": %b\n}", aceitar);

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<Reserva> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Reserva target = null;
        for (Reserva r : reservas) {
            if (r.getId().equals(reservaId)) {
                target = r;
                break;
            }
        }

        if (target == null) {
            ApiError err = new ApiError(404, "Not Found", "Reserva nao encontrada.");
            ApiResponse<Reserva> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Carona carona = null;
        for (Carona c : caronas) {
            if (c.getId().equals(target.getCarona_id())) {
                carona = c;
                break;
            }
        }

        if (carona == null || (!carona.getMotorista_id().equals(usuarioLogado.getId()) && !isAdmin())) {
            ApiError err = new ApiError(403, "Forbidden", "Apenas o motorista da carona pode aceitar ou recusar reservas.");
            ApiResponse<Reserva> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (aceitar) {
            if (carona.getAssentos_disponiveis() < target.getVagas_solicitadas()) {
                ApiError err = new ApiError(400, "Bad Request", "Nao ha assentos disponiveis suficientes para aceitar esta reserva.");
                ApiResponse<Reserva> res = ApiResponse.fail(method, path, err, reqJson);
                notifyListeners(res);
                return res;
            }
            target.setStatus("ACEITA");
            carona.setAssentos_disponiveis(carona.getAssentos_disponiveis() - target.getVagas_solicitadas());
        } else {
            target.setStatus("RECUSADA");
        }

        ApiResponse<Reserva> res = ApiResponse.ok(method, path, 200, target, reqJson, "{\n  \"mensagem\": \"Reserva " + target.getStatus() + "\"\n}");
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<String> cancelarReserva(String reservaId) {
        String method = "DELETE";
        String path = "/reservas/" + reservaId;

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<String> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        Reserva target = null;
        for (Reserva r : reservas) {
            if (r.getId().equals(reservaId)) {
                target = r;
                break;
            }
        }

        if (target == null) {
            ApiError err = new ApiError(404, "Not Found", "Reserva nao encontrada.");
            ApiResponse<String> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        if (!target.getPassageiro_id().equals(usuarioLogado.getId()) && !isAdmin()) {
            ApiError err = new ApiError(403, "Forbidden", "Voce so pode cancelar suas proprias reservas.");
            ApiResponse<String> res = ApiResponse.fail(method, path, err, null);
            notifyListeners(res);
            return res;
        }

        if ("ACEITA".equalsIgnoreCase(target.getStatus())) {
            for (Carona c : caronas) {
                if (c.getId().equals(target.getCarona_id())) {
                    c.setAssentos_disponiveis(Math.min(c.getAssentos_totais(), c.getAssentos_disponiveis() + target.getVagas_solicitadas()));
                    break;
                }
            }
        }

        target.setStatus("CANCELADA");

        ApiResponse<String> res = ApiResponse.ok(method, path, 200, "Reserva cancelada com sucesso.", null, "{\n  \"mensagem\": \"Reserva cancelada com sucesso.\"\n}");
        notifyListeners(res);
        return res;
    }

    // ==========================================
    // AVALIAÇÕES
    // ==========================================

    @Override
    public ApiResponse<Avaliacao> avaliarUsuario(String caronaId, String avaliadoId, int nota, String comentario, String tipo) {
        String method = "POST";
        String path = "/avaliacoes";
        String reqJson = String.format("{\n  \"carona_id\": \"%s\",\n  \"avaliado_id\": \"%s\",\n  \"nota\": %d,\n  \"comentario\": \"%s\",\n  \"tipo\": \"%s\"\n}",
                caronaId, avaliadoId, nota, comentario != null ? comentario : "", tipo != null ? tipo : "COMO_PASSAGEIRO");

        if (!isAuthenticated()) {
            ApiError err = new ApiError(401, "Unauthorized", "Voce precisa estar autenticado.");
            ApiResponse<Avaliacao> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (usuarioLogado.getId().equals(avaliadoId)) {
            ApiError err = new ApiError(400, "Bad Request", "Voce nao pode avaliar a si mesmo.");
            ApiResponse<Avaliacao> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        if (nota < 1 || nota > 5) {
            ApiError err = new ApiError(400, "Bad Request", "A nota deve ser entre 1 e 5 estrelas.");
            ApiResponse<Avaliacao> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        Usuario avaliado = null;
        for (Usuario u : usuarios) {
            if (u.getId().equals(avaliadoId)) {
                avaliado = u;
                break;
            }
        }

        if (avaliado == null) {
            ApiError err = new ApiError(404, "Not Found", "Usuario avaliado nao encontrado.");
            ApiResponse<Avaliacao> res = ApiResponse.fail(method, path, err, reqJson);
            notifyListeners(res);
            return res;
        }

        String novoId = "av-" + UUID.randomUUID().toString().substring(0, 8);
        String criadoEm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        Avaliacao nova = new Avaliacao(
                novoId,
                caronaId,
                usuarioLogado.getId(),
                usuarioLogado.getNome_completo(),
                null,
                avaliado.getId(),
                avaliado.getNome_completo(),
                nota,
                comentario != null ? comentario.trim() : "",
                tipo != null ? tipo : "COMO_PASSAGEIRO",
                criadoEm
        );

        avaliacoes.add(0, nova);
        recalcularMediaUsuario(avaliado);

        String respJson = String.format("{\n  \"mensagem\": \"Avaliacao registrada com sucesso!\",\n  \"id\": \"%s\",\n  \"nova_media_avaliado\": \"%s\"\n}", novoId, avaliado.getMedia_avaliacao());
        ApiResponse<Avaliacao> res = ApiResponse.ok(method, path, 201, nova, reqJson, respJson);
        notifyListeners(res);
        return res;
    }

    private void recalcularMediaUsuario(Usuario u) {
        List<Avaliacao> recebidas = avaliacoes.stream()
                .filter(a -> a.getAvaliado_id().equals(u.getId()))
                .collect(Collectors.toList());

        if (recebidas.isEmpty()) return;

        double sum = 0;
        for (Avaliacao a : recebidas) {
            sum += a.getNota();
        }
        double media = sum / recebidas.size();
        u.setMedia_avaliacao(String.format(Locale.US, "%.2f", media));

        for (Carona c : caronas) {
            if (c.getMotorista_id().equals(u.getId())) {
                c.setMotorista_avaliacao(u.getMedia_avaliacao());
            }
        }
    }

    @Override
    public ApiResponse<List<Avaliacao>> getAvaliacoesDoUsuario(String usuarioId) {
        String method = "GET";
        String path = "/avaliacoes/usuario/" + usuarioId;

        List<Avaliacao> recebidas = avaliacoes.stream()
                .filter(a -> a.getAvaliado_id().equals(usuarioId))
                .collect(Collectors.toList());

        ApiResponse<List<Avaliacao>> res = ApiResponse.ok(method, path, 200, recebidas, null, "{\n  \"total\": " + recebidas.size() + "\n}");
        notifyListeners(res);
        return res;
    }

    @Override
    public ApiResponse<List<Avaliacao>> getMinhasAvaliacoes() {
        if (!isAuthenticated()) {
            return ApiResponse.fail("GET", "/avaliacoes/me", new ApiError(401, "Unauthorized", "Nao autenticado."), null);
        }
        return getAvaliacoesDoUsuario(usuarioLogado.getId());
    }

    // ==========================================
    // DASHBOARD & ESTATÍSTICAS
    // ==========================================

    @Override
    public DashboardStats getDashboardStats() {
        int totalAtivasHoje = 0;
        int minhasCaronas = 0;
        int minhasReservas = 0;
        int totalAvaliacoes = 0;
        double mediaPessoal = 5.0;

        String meuId = usuarioLogado != null ? usuarioLogado.getId() : "";

        for (Carona c : caronas) {
            if ("AGENDADA".equalsIgnoreCase(c.getStatus()) || "EM_ANDAMENTO".equalsIgnoreCase(c.getStatus())) {
                totalAtivasHoje++;
            }
            if (c.getMotorista_id().equals(meuId)) {
                minhasCaronas++;
            }
        }

        for (Reserva r : reservas) {
            if (r.getPassageiro_id().equals(meuId) && !"CANCELADA".equalsIgnoreCase(r.getStatus())) {
                minhasReservas++;
            }
        }

        List<Avaliacao> minhasAv = avaliacoes.stream().filter(a -> a.getAvaliado_id().equals(meuId)).collect(Collectors.toList());
        totalAvaliacoes = minhasAv.size();
        if (totalAvaliacoes > 0) {
            double sum = 0;
            for (Avaliacao a : minhasAv) sum += a.getNota();
            mediaPessoal = sum / totalAvaliacoes;
        } else if (usuarioLogado != null && usuarioLogado.getMedia_avaliacao() != null) {
            try {
                mediaPessoal = Double.parseDouble(usuarioLogado.getMedia_avaliacao());
            } catch (Exception ignored) {}
        }

        int totalViagens = minhasCaronas + minhasReservas;
        double economia = totalViagens * 18.50;
        double co2 = totalViagens * 4.2;

        return new DashboardStats(totalAtivasHoje, minhasCaronas, minhasReservas, economia, co2, mediaPessoal, totalAvaliacoes);
    }

    // ==========================================
    // LISTENERS
    // ==========================================

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
