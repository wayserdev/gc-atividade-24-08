package br.com.caronas.test;

import br.com.caronas.model.*;
import br.com.caronas.service.ApiService;
import br.com.caronas.service.MockApiService;

public class Feature1Test {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   EXECUÇÃO DOS TESTES AUTOMATIZADOS - FEATURE 1 (SPEC)   ");
        System.out.println("==========================================================");

        int passed = 0;
        int failed = 0;

        ApiService api = new MockApiService();

        // 1. TESTE: POST /auth/cadastrar - Sucesso (201 Created)
        try {
            ApiResponse<AuthResponse> res = api.cadastrar(
                    "Novo Estudante Teste",
                    "estudante.novo@teste.com",
                    "senhaSegura123",
                    "62999991111",
                    "Engenharia de Software",
                    "c1a2b3c4-0000-0000-0000-000000000000"
            );
            assert res.getStatusCode() == 201 : "Deveria retornar 201 Created";
            assert res.getData().getToken() != null : "Token JWT deve ser emitido";
            assert "ALUNO".equals(res.getData().getUsuario().getNivel()) : "Nível padrão deve ser ALUNO";
            assert "0.00".equals(res.getData().getUsuario().getMedia_avaliacao()) : "Média inicial deve ser 0.00";
            System.out.println("✓ [PASS] POST /auth/cadastrar - 201 Created com emissão de token JWT");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] POST /auth/cadastrar: " + t.getMessage());
            failed++;
        }

        // 2. TESTE: POST /auth/cadastrar - Erro 409 Conflict (e-mail duplicado)
        try {
            ApiResponse<AuthResponse> res = api.cadastrar(
                    "Carlos Eduardo Clone",
                    "carlos.edu@gmail.com", // email já existente
                    "senhaSegura123",
                    "62999998888",
                    "Engenharia de Software",
                    "c1a2b3c4-0000-0000-0000-000000000000"
            );
            assert res.getStatusCode() == 409 : "Deveria retornar 409 Conflict";
            assert "Conflict".equals(res.getError().getErro()) : "Erro deve ser Conflict";
            System.out.println("✓ [PASS] POST /auth/cadastrar - 409 Conflict ao tentar duplicar e-mail");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] POST /auth/cadastrar (409 Conflict): " + t.getMessage());
            failed++;
        }

        // 3. TESTE: POST /auth/cadastrar - Erro 400 Bad Request (validação de DTO)
        try {
            ApiResponse<AuthResponse> res = api.cadastrar(
                    "A", // nome menor que 3
                    "email-invalido", // email inválido
                    "123", // senha menor que 6
                    "123", // telefone inválido
                    "", // curso vazio
                    "" // instituição vazia
            );
            assert res.getStatusCode() == 400 : "Deveria retornar 400 Bad Request";
            assert res.getError().getMensagens().size() >= 5 : "Deveria conter todas as validações";
            System.out.println("✓ [PASS] POST /auth/cadastrar - 400 Bad Request para dados inválidos");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] POST /auth/cadastrar (400 Bad Request): " + t.getMessage());
            failed++;
        }

        // 4. TESTE: POST /auth/login - Sucesso (200 OK)
        try {
            ApiResponse<AuthResponse> res = api.login("carlos.edu@gmail.com", "senhaSegura123");
            assert res.getStatusCode() == 200 : "Deveria retornar 200 OK";
            assert res.getData().getToken() != null : "Token JWT deve ser retornado";
            assert "carlos.edu@gmail.com".equals(res.getData().getUsuario().getEmail());
            System.out.println("✓ [PASS] POST /auth/login - 200 OK com autenticação e token");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] POST /auth/login: " + t.getMessage());
            failed++;
        }

        // 5. TESTE: POST /auth/login - Erro 401 Unauthorized (credenciais inválidas)
        try {
            ApiResponse<AuthResponse> res = api.login("carlos.edu@gmail.com", "senhaErrada999");
            assert res.getStatusCode() == 401 : "Deveria retornar 401 Unauthorized";
            assert "Unauthorized".equals(res.getError().getErro());
            System.out.println("✓ [PASS] POST /auth/login - 401 Unauthorized com credenciais incorretas");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] POST /auth/login (401 Unauthorized): " + t.getMessage());
            failed++;
        }

        // 6. TESTE: GET /usuarios/me - Sucesso (200 OK)
        try {
            api.login("carlos.edu@gmail.com", "senhaSegura123");
            ApiResponse<Usuario> res = api.getMe();
            assert res.getStatusCode() == 200 : "Deveria retornar 200 OK";
            assert res.getData().getInstituicao_nome() != null : "Instituição deve vir populada";
            System.out.println("✓ [PASS] GET /usuarios/me - 200 OK com dados cadastrais e instituição");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] GET /usuarios/me: " + t.getMessage());
            failed++;
        }

        // 7. TESTE: PUT /usuarios/me - Sucesso na edição de campos permitidos
        try {
            api.login("carlos.edu@gmail.com", "senhaSegura123");
            ApiResponse<Usuario> res = api.updateMe("Carlos Eduardo Atualizado", "62988887777", "Engenharia de Software (8º Período)", "https://meuapp.com/foto.jpg");
            assert res.getStatusCode() == 200 : "Deveria retornar 200 OK";
            assert "Carlos Eduardo Atualizado".equals(res.getData().getNome_completo());
            assert "62988887777".equals(res.getData().getTelefone());
            assert "Engenharia de Software (8º Período)".equals(res.getData().getCurso());
            assert "ALUNO".equals(res.getData().getNivel()) : "Nível não deve ser alterado pelo aluno";
            System.out.println("✓ [PASS] PUT /usuarios/me - 200 OK com atualização de campos permitidos");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] PUT /usuarios/me: " + t.getMessage());
            failed++;
        }

        // 8. TESTE: GET /admin/usuarios - Erro 403 Forbidden para ALUNO
        try {
            api.login("carlos.edu@gmail.com", "senhaSegura123"); // Logado como ALUNO
            ApiResponse<PageResult<Usuario>> res = api.getUsuariosAdmin("", "TODOS", 1, 10);
            assert res.getStatusCode() == 403 : "Deveria retornar 403 Forbidden para Aluno";
            assert "Forbidden".equals(res.getError().getErro());
            System.out.println("✓ [PASS] GET /admin/usuarios - 403 Forbidden quando acessado por perfil ALUNO");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] GET /admin/usuarios (403 Forbidden): " + t.getMessage());
            failed++;
        }

        // 9. TESTE: GET /admin/usuarios - Sucesso (200 OK) para ADMIN com busca e paginação
        try {
            api.login("ana.lima@faculdade.br", "admin123"); // Logado como ADMIN
            ApiResponse<PageResult<Usuario>> res = api.getUsuariosAdmin("Carlos", "TODOS", 1, 10);
            assert res.getStatusCode() == 200 : "Deveria retornar 200 OK para Admin";
            assert res.getData().getUsuarios().size() > 0 : "Deve encontrar registros com filtro 'Carlos'";
            System.out.println("✓ [PASS] GET /admin/usuarios - 200 OK com busca ILIKE e paginação para ADMIN");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] GET /admin/usuarios (Admin): " + t.getMessage());
            failed++;
        }

        // 10. TESTE: PUT /admin/usuarios/:id - Promoção de ALUNO para ADMIN
        try {
            api.login("ana.lima@faculdade.br", "admin123");
            ApiResponse<Usuario> res = api.updateUsuarioAdmin(
                    "u1u2u3u4-0000-0000-0000-000000000000",
                    "Carlos Eduardo Promovido",
                    "carlos.promovido@faculdade.br",
                    "ADMIN",
                    "62999998888",
                    "Docente / Coordenação"
            );
            assert res.getStatusCode() == 200 : "Deveria retornar 200 OK";
            assert "ADMIN".equals(res.getData().getNivel()) : "Nível deve ter sido alterado para ADMIN";
            System.out.println("✓ [PASS] PUT /admin/usuarios/:id - 200 OK com promoção de nível RBAC");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] PUT /admin/usuarios/:id: " + t.getMessage());
            failed++;
        }

        // 11. TESTE: DELETE /admin/usuarios/:id - Auto-bloqueio (Regra de validação)
        try {
            api.login("ana.lima@faculdade.br", "admin123"); // Ana é o usuário logado
            Usuario ana = api.getUsuarioLogado();
            ApiResponse<String> res = api.deleteUsuarioAdmin(ana.getId()); // Tenta deletar a si mesma
            assert res.getStatusCode() == 400 : "Deveria retornar 400 Bad Request para auto-exclusão";
            System.out.println("✓ [PASS] DELETE /admin/usuarios/:id - 400 Bad Request ao tentar auto-bloqueio");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] DELETE /admin/usuarios/:id (Auto-bloqueio): " + t.getMessage());
            failed++;
        }

        // 12. TESTE: DELETE /admin/usuarios/:id - Sucesso na exclusão de outro usuário
        try {
            api.login("ana.lima@faculdade.br", "admin123");
            ApiResponse<String> res = api.deleteUsuarioAdmin("u1u2u3u4-0000-0000-0000-000000000000");
            assert res.getStatusCode() == 200 : "Deveria retornar 200 OK";
            System.out.println("✓ [PASS] DELETE /admin/usuarios/:id - 200 OK ao excluir usuário pelo Administrador");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] DELETE /admin/usuarios/:id: " + t.getMessage());
            failed++;
        }

        System.out.println("==========================================================");
        System.out.printf("RESULTADO FINAL: %d Testes Passaram | %d Falhas\n", passed, failed);
        System.out.println("==========================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
