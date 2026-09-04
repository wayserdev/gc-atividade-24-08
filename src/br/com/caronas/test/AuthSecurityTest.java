package br.com.caronas.test;

import br.com.caronas.model.ApiResponse;
import br.com.caronas.model.AuthResponse;
import br.com.caronas.model.Usuario;
import br.com.caronas.service.JwtService;
import br.com.caronas.service.MockApiService;
import br.com.caronas.service.PasswordHasher;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

/** Testes de segurança do fluxo local de autenticação e autorização. */
public class AuthSecurityTest {
    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        try {
            String hash = PasswordHasher.hash("senhaSegura123");
            assert !hash.equals("senhaSegura123") : "senha não pode ser armazenada em texto puro";
            assert hash.startsWith("pbkdf2_sha256$") : "hash deve informar o algoritmo";
            assert PasswordHasher.matches("senhaSegura123", hash);
            assert !PasswordHasher.matches("senhaErrada", hash);
            assert !hash.equals(PasswordHasher.hash("senhaSegura123")) : "hash deve usar salt aleatório";
            System.out.println("✓ Hash de senha com salt e comparação segura");
            passed++;
        } catch (Throwable error) {
            System.err.println("✗ Hash de senha: " + error.getMessage());
            failed++;
        }

        try {
            String secret = "segredo-jwt-do-projeto-academico-2026";
            Instant now = Instant.parse("2026-09-03T12:00:00Z");
            Clock clock = Clock.fixed(now, ZoneOffset.UTC);
            JwtService jwt = new JwtService(secret, Duration.ofMinutes(5), clock);
            Usuario user = new Usuario("u-1", "inst-1", "Instituição", "Pessoa Teste",
                    "pessoa@teste.com", null, "ADMIN", "62999998888", "Computação", null, "5.00", "2026-09-03");

            String token = jwt.createToken(user);
            assert token.split("\\.", -1).length == 3 : "JWT deve possuir header, payload e assinatura";
            assert jwt.validate(token).isPresent();
            assert "ADMIN".equals(jwt.validate(token).get().getNivel());
            assert !jwt.validate(token.substring(0, token.length() - 1) + "x").isPresent();

            JwtService expiredJwt = new JwtService(secret, Duration.ofMinutes(5),
                    Clock.fixed(now.plusSeconds(301), ZoneOffset.UTC));
            assert !expiredJwt.validate(token).isPresent() : "JWT expirado deve ser rejeitado";
            System.out.println("✓ JWT HS256 com assinatura, claims e expiração");
            passed++;
        } catch (Throwable error) {
            System.err.println("✗ JWT: " + error.getMessage());
            failed++;
        }

        try {
            MockApiService api = new MockApiService();
            assert !api.isAuthenticated() : "a aplicação deve iniciar deslogada";
            assert api.getMe().getStatusCode() == 401;

            ApiResponse<AuthResponse> login = api.login("carlos.edu@gmail.com", "senhaSegura123");
            assert login.isSuccess();
            assert login.getData().getUsuario().getSenhaHash() == null : "hash não pode sair na resposta";
            assert api.getMe().getData().getSenhaHash() == null : "hash não pode sair em /usuarios/me";

            Usuario exposedCopy = api.getUsuarioLogado();
            exposedCopy.setNome_completo("Tentativa de alteração externa");
            assert !"Tentativa de alteração externa".equals(api.getUsuarioLogado().getNome_completo());

            api.setUsuarioLogado(api.getUsuarioLogado(), api.getToken() + "x");
            assert !api.isAuthenticated() : "assinatura adulterada deve invalidar a sessão";
            System.out.println("✓ Sessão, RBAC e isolamento do hash do usuário");
            passed++;
        } catch (Throwable error) {
            System.err.println("✗ Serviço de autenticação: " + error.getMessage());
            failed++;
        }

        try {
            MockApiService api = new MockApiService();
            ApiResponse<AuthResponse> invalidInstitution = api.cadastrar(
                    "Pessoa Válida", "pessoa.nova@teste.com", "senha123", "62999998888",
                    "Computação", "instituicao-inexistente");
            assert invalidInstitution.getStatusCode() == 400;

            api.login("ana.lima@faculdade.br", "admin123");
            ApiResponse<Usuario> duplicateEmail = api.updateUsuarioAdmin(
                    "u1u2u3u4-0000-0000-0000-000000000000", "Carlos", "ana.lima@faculdade.br",
                    "ALUNO", "62999998888", "Computação");
            assert duplicateEmail.getStatusCode() == 400;
            System.out.println("✓ Validações de instituição e e-mail duplicado");
            passed++;
        } catch (Throwable error) {
            System.err.println("✗ Validações: " + error.getMessage());
            failed++;
        }

        System.out.printf("SEGURANÇA: %d Testes Passaram | %d Falhas%n", passed, failed);
        if (failed > 0) System.exit(1);
    }
}
