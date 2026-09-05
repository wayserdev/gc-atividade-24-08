package br.com.caronas.test;

import br.com.caronas.model.*;
import br.com.caronas.service.ApiService;
import br.com.caronas.service.MockApiService;

import java.util.List;

public class ReservaTest {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   EXECUÇÃO DOS TESTES AUTOMATIZADOS - RESERVAS (DEV04)   ");
        System.out.println("==========================================================");

        int passed = 0;
        int failed = 0;

        ApiService api = new MockApiService();

        // 1. TESTE: POST /reservas - Criação válida (201 Created)
        try {
            // Login como aluno Lucas
            api.login("carlos.edu@gmail.com", "senhaSegura123");
            Carona carona = api.getCaronaById("c1000000-0000-0000-0000-000000000001");
            int vagasAntes = carona.getAssentos_disponiveis();

            ReservaRequest req = new ReservaRequest(
                    "c1000000-0000-0000-0000-000000000001",
                    1,
                    "Praça Universitária"
            );
            ApiResponse<ReservaResponse> res = api.criarReserva(req);

            assert res.getStatusCode() == 201 : "Deveria retornar 201 Created, retornou: " + res.getStatusCode();
            assert res.getData() != null : "Dados da reserva devem estar preenchidos";
            assert "PENDENTE".equals(res.getData().getStatus()) : "Status inicial deve ser PENDENTE";
            assert carona.getAssentos_disponiveis() == vagasAntes - 1 : "Vagas na carona devem ser decrementadas em 1";
            System.out.println("✓ [PASS] POST /reservas - 201 Created com criação válida e vagas decrementadas");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] POST /reservas (Criação válida): " + t.getMessage());
            failed++;
        }

        // 2. TESTE: POST /reservas - Tentativa de reserva sem vagas disponíveis (400 Bad Request)
        try {
            api.login("lucas.rib@gmail.com", "senha123");
            // Carona 2 está lotada (assentos_disponiveis == 0)
            ReservaRequest req = new ReservaRequest(
                    "c1000000-0000-0000-0000-000000000002",
                    1,
                    "Setor Bueno"
            );
            ApiResponse<ReservaResponse> res = api.criarReserva(req);

            assert res.getStatusCode() == 400 : "Deveria retornar 400 Bad Request, retornou: " + res.getStatusCode();
            assert res.getError() != null : "Deve conter erro de validação";
            assert res.getError().getMensagemFormatada().contains("assentos disponíveis suficientes")
                    : "Mensagem deve indicar falta de vagas disponíveis";
            System.out.println("✓ [PASS] POST /reservas - 400 Bad Request para carona sem vagas disponíveis");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] POST /reservas (Sem vagas): " + t.getMessage());
            failed++;
        }

        // 3. TESTE: POST /reservas - Tentativa de reserva duplicada para o mesmo passageiro (409 Conflict)
        try {
            api.login("carlos.edu@gmail.com", "senhaSegura123");
            // Carlos já possui reserva criada no Teste 1 para a Carona 1
            ReservaRequest reqDuplicada = new ReservaRequest(
                    "c1000000-0000-0000-0000-000000000001",
                    1,
                    "Ponto Alternativo"
            );
            ApiResponse<ReservaResponse> res = api.criarReserva(reqDuplicada);

            assert res.getStatusCode() == 409 : "Deveria retornar 409 Conflict, retornou: " + res.getStatusCode();
            assert "Conflict".equals(res.getError().getErro()) : "Tipo do erro deve ser Conflict";
            assert res.getError().getMensagemFormatada().contains("já possui uma reserva ativa")
                    : "Mensagem deve informar reserva ativa existente";
            System.out.println("✓ [PASS] POST /reservas - 409 Conflict ao tentar reserva duplicada na mesma carona");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] POST /reservas (Duplicada): " + t.getMessage());
            failed++;
        }

        // 4. TESTE: GET /reservas/{id} - Consulta por ID existente (200 OK)
        try {
            // Consulta reserva inicial existente da Mariana
            api.login("mariana.oli@gmail.com", "senha123");
            ApiResponse<ReservaResponse> res = api.getReservaById("r1000000-0000-0000-0000-000000000001");

            assert res.getStatusCode() == 200 : "Deveria retornar 200 OK, retornou: " + res.getStatusCode();
            assert res.getData() != null : "Dados da reserva devem existir";
            assert "r1000000-0000-0000-0000-000000000001".equals(res.getData().getId()) : "ID deve conferir";
            assert "ACEITA".equals(res.getData().getStatus()) : "Status deve conferir";
            System.out.println("✓ [PASS] GET /reservas/{id} - 200 OK para consulta de reserva existente");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] GET /reservas/{id} (Existente): " + t.getMessage());
            failed++;
        }

        // 5. TESTE: GET /reservas/{id} - Consulta de reserva inexistente (404 Not Found)
        try {
            api.login("carlos.edu@gmail.com", "senhaSegura123");
            ApiResponse<ReservaResponse> res = api.getReservaById("r9999999-9999-9999-9999-999999999999");

            assert res.getStatusCode() == 404 : "Deveria retornar 404 Not Found, retornou: " + res.getStatusCode();
            assert "Not Found".equals(res.getError().getErro()) : "Tipo do erro deve ser Not Found";
            System.out.println("✓ [PASS] GET /reservas/{id} - 404 Not Found para reserva inexistente");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] GET /reservas/{id} (Inexistente): " + t.getMessage());
            failed++;
        }

        // 6. TESTE: GET /reservas/usuario/{usuarioId} - Listagem de reservas por usuário (200 OK)
        try {
            api.login("carlos.edu@gmail.com", "senhaSegura123");
            Usuario carlos = api.getUsuarioLogado();
            ApiResponse<List<ReservaResponse>> res = api.getReservasByUsuario(carlos.getId());

            assert res.getStatusCode() == 200 : "Deveria retornar 200 OK, retornou: " + res.getStatusCode();
            assert res.getData() != null : "Lista de reservas não pode ser nula";
            assert res.getData().size() >= 1 : "Carlos deve possuir pelo menos a reserva criada no Teste 1";
            System.out.println("✓ [PASS] GET /reservas/usuario/{usuarioId} - 200 OK com listagem das reservas do passageiro");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] GET /reservas/usuario/{usuarioId}: " + t.getMessage());
            failed++;
        }

        // 7. TESTE: PUT /reservas/{id}/cancelar - Cancelamento válido e restauração de vagas (200 OK)
        String reservaCriadaId = null;
        try {
            api.login("beatriz.s@gmail.com", "senha123");
            Carona carona = api.getCaronaById("c1000000-0000-0000-0000-000000000001");
            int vagasAntes = carona.getAssentos_disponiveis();

            // Beatriz cria reserva
            ApiResponse<ReservaResponse> resCriacao = api.criarReserva(new ReservaRequest(
                    "c1000000-0000-0000-0000-000000000001",
                    1,
                    "Praça Universitária"
            ));
            assert resCriacao.getStatusCode() == 201;
            reservaCriadaId = resCriacao.getData().getId();
            assert carona.getAssentos_disponiveis() == vagasAntes - 1;

            // Beatriz cancela a própria reserva
            ApiResponse<ReservaResponse> resCancel = api.cancelarReserva(reservaCriadaId);

            assert resCancel.getStatusCode() == 200 : "Deveria retornar 200 OK, retornou: " + resCancel.getStatusCode();
            assert "CANCELADA".equals(resCancel.getData().getStatus()) : "Status deve ser CANCELADA";
            assert carona.getAssentos_disponiveis() == vagasAntes : "Vagas na carona devem ser integralmente restauradas";
            System.out.println("✓ [PASS] PUT /reservas/{id}/cancelar - 200 OK com cancelamento válido e restauração de vagas");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] PUT /reservas/{id}/cancelar (Cancelamento válido): " + t.getMessage());
            failed++;
        }

        // 8. TESTE: PUT /reservas/{id}/cancelar - Cancelamento inválido (reserva já cancelada) (400 Bad Request)
        try {
            api.login("beatriz.s@gmail.com", "senha123");
            // Tenta cancelar novamente a reserva da Beatriz que já foi cancelada no Teste 7
            ApiResponse<ReservaResponse> resCancel = api.cancelarReserva(reservaCriadaId);

            assert resCancel.getStatusCode() == 400 : "Deveria retornar 400 Bad Request para reserva já cancelada";
            assert resCancel.getError().getMensagemFormatada().contains("já está cancelada")
                    : "Mensagem deve indicar que a reserva já está cancelada";
            System.out.println("✓ [PASS] PUT /reservas/{id}/cancelar - 400 Bad Request ao tentar cancelar reserva já cancelada");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] PUT /reservas/{id}/cancelar (Já cancelada): " + t.getMessage());
            failed++;
        }

        // 9. TESTE: POST /reservas - Validações de campos obrigatórios (400 Bad Request)
        try {
            api.login("carlos.edu@gmail.com", "senhaSegura123");
            // Solicitação com caronaId vazio e vagas <= 0
            ReservaRequest reqInvalido = new ReservaRequest("", 0, "");
            ApiResponse<ReservaResponse> res = api.criarReserva(reqInvalido);

            assert res.getStatusCode() == 400 : "Deveria retornar 400 Bad Request, retornou: " + res.getStatusCode();
            assert res.getError().getMensagens().size() >= 2 : "Deve apontar múltiplos erros de validação";
            System.out.println("✓ [PASS] POST /reservas - 400 Bad Request para dados e campos obrigatórios inválidos");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] POST /reservas (Validações de campos): " + t.getMessage());
            failed++;
        }

        // 10. TESTE: Regras de autorização (401 Unauthorized e 403 Forbidden)
        try {
            // A. Usuário deslogado tentando criar reserva -> 401 Unauthorized
            api.logout();
            ApiResponse<ReservaResponse> resDeslogado = api.criarReserva(new ReservaRequest("c1000000-0000-0000-0000-000000000001"));
            assert resDeslogado.getStatusCode() == 401 : "Deveria retornar 401 Unauthorized para usuário não logado";

            // B. Aluno tentando cancelar reserva de outro passageiro -> 403 Forbidden
            api.login("lucas.rib@gmail.com", "senha123");
            // Lucas tenta cancelar reserva da Mariana
            ApiResponse<ReservaResponse> resAcessoNegado = api.cancelarReserva("r1000000-0000-0000-0000-000000000001");
            assert resAcessoNegado.getStatusCode() == 403 : "Deveria retornar 403 Forbidden para tentativa de cancelar reserva de outrem";
            assert "Forbidden".equals(resAcessoNegado.getError().getErro()) : "Tipo de erro deve ser Forbidden";

            System.out.println("✓ [PASS] Regras de Autorização - 401 Unauthorized e 403 Forbidden devidamente aplicados");
            passed++;
        } catch (Throwable t) {
            System.err.println("✗ [FAIL] Regras de autorização: " + t.getMessage());
            failed++;
        }

        System.out.println("==========================================================");
        System.out.printf("RESULTADO FINAL RESERVAS: %d Testes Passaram | %d Falhas\n", passed, failed);
        System.out.println("==========================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
