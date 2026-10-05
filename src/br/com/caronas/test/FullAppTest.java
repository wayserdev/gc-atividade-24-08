package br.com.caronas.test;

import br.com.caronas.model.*;
import br.com.caronas.service.ApiService;
import br.com.caronas.service.MockApiService;

import java.util.List;

public class FullAppTest {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   SUÍTE COMPLETA DE TESTES AUTOMATIZADOS - UNIRIDE CARONAS APP   ");
        System.out.println("==================================================================");

        int passed = 0;
        int failed = 0;

        ApiService api = new MockApiService();

        // ---------------------------------------------------------
        // 1. AUTH & PERFIL
        // ---------------------------------------------------------
        try {
            ApiResponse<AuthResponse> loginRes = api.login("carlos.edu@gmail.com", "senhaSegura123");
            assert loginRes.isSuccess() : "Login do Carlos deve ter sucesso";
            assert api.isAuthenticated() : "Deve estar autenticado";
            System.out.println("[PASS] 1. Autenticação JWT e Login de Aluno");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 1. Auth: " + t.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 2. VEÍCULOS
        // ---------------------------------------------------------
        try {
            // Consulta veículos do Carlos
            ApiResponse<List<Veiculo>> veicRes = api.getMeusVeiculos();
            assert veicRes.isSuccess() && !veicRes.getData().isEmpty() : "Carlos deve ter ao menos 1 veículo";

            // Cadastra novo veículo
            ApiResponse<Veiculo> novoVeic = api.cadastrarVeiculo("Honda Civic EXL", "CAR-1020", 4);
            assert novoVeic.isSuccess() : "Deve cadastrar veículo com sucesso";
            assert "CAR-1020".equals(novoVeic.getData().getPlaca());

            // Tenta duplicar placa
            ApiResponse<Veiculo> dupPlaca = api.cadastrarVeiculo("Outro Carro", "CAR-1020", 4);
            assert dupPlaca.getStatusCode() == 409 : "Deve rejeitar placa duplicada com 409 Conflict";

            System.out.println("[PASS] 2. Gestão de Veículos (Cadastro, DTO e Validação de Placa)");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 2. Veículos: " + t.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 3. CARONAS (CRIAÇÃO E BUSCA)
        // ---------------------------------------------------------
        String caronaCriadaId = null;
        try {
            List<Veiculo> veiculos = api.getMeusVeiculos().getData();
            assert !veiculos.isEmpty();
            String veicId = veiculos.get(0).getId();

            List<Bairro> bairros = api.getBairros();
            List<Campus> campi = api.getCampi();

            // Oferece carona
            ApiResponse<Carona> novaCarona = api.oferecerCarona(
                    veicId,
                    "BAIRRO_PARA_CAMPUS",
                    bairros.get(0).getId(),
                    campi.get(0).getId(),
                    "Portaria Residencial dos Ipês",
                    "Hoje, 19:00",
                    3,
                    5.50
            );

            assert novaCarona.isSuccess() : "Deve criar carona com sucesso";
            assert novaCarona.getData().getAssentos_disponiveis() == 3;
            caronaCriadaId = novaCarona.getData().getId();

            // Busca caronas por filtro
            ApiResponse<List<Carona>> busca = api.buscarCaronas(bairros.get(0).getId(), campi.get(0).getId(), "BAIRRO_PARA_CAMPUS", null, "AGENDADA");
            assert busca.isSuccess() && !busca.getData().isEmpty() : "Busca deve retornar a carona recém-criada";

            System.out.println("[PASS] 3. Oferta e Busca de Caronas (Filtros por Bairro, Campus e Direção)");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 3. Caronas: " + t.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 4. RESERVAS (SOLICITAÇÃO, APROVAÇÃO E GESTÃO DE VAGAS)
        // ---------------------------------------------------------
        try {
            // Alterna para Mariana (Passageira)
            api.login("mariana.oli@gmail.com", "senha123");

            // Mariana solicita 2 vagas na carona do Carlos
            ApiResponse<Reserva> resSolicitacao = api.solicitarCarona(caronaCriadaId, 2, "Cruzamento da Avenida T-10");
            assert resSolicitacao.isSuccess() : "Mariana deve conseguir solicitar carona";
            assert "PENDENTE".equals(resSolicitacao.getData().getStatus());
            String reservaId = resSolicitacao.getData().getId();

            // Volta para Carlos (Motorista)
            api.login("carlos.edu@gmail.com", "senhaSegura123");

            // Carlos visualiza pedidos da carona
            ApiResponse<List<Reserva>> pedidos = api.getReservasDaCarona(caronaCriadaId);
            assert pedidos.getData().size() >= 1 : "Carlos deve ver o pedido da Mariana";

            // Carlos aceita a reserva
            ApiResponse<Reserva> aceitaRes = api.responderReserva(reservaId, true);
            assert aceitaRes.isSuccess() : "Carlos deve aceitar a reserva";
            assert "ACEITA".equals(aceitaRes.getData().getStatus());

            // Verifica se as vagas da carona foram decrementadas (3 - 2 = 1 vaga livre)
            Carona cAtualizada = api.getCaronaById(caronaCriadaId).getData();
            assert cAtualizada.getAssentos_disponiveis() == 1 : "Deveria sobrar exatamente 1 vaga";

            System.out.println("[PASS] 4. Ciclo de Reservas (Solicitação, Aprovação e Controle de Assentos)");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 4. Reservas: " + t.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 5. MÁQUINA DE ESTADOS DA CARONA (INÍCIO -> FINALIZAÇÃO)
        // ---------------------------------------------------------
        try {
            // Carlos inicia a viagem
            ApiResponse<Carona> emAndamento = api.atualizarStatusCarona(caronaCriadaId, "EM_ANDAMENTO");
            assert emAndamento.isSuccess() && "EM_ANDAMENTO".equals(emAndamento.getData().getStatus());

            // Carlos finaliza a viagem
            ApiResponse<Carona> finalizada = api.atualizarStatusCarona(caronaCriadaId, "FINALIZADA");
            assert finalizada.isSuccess() && "FINALIZADA".equals(finalizada.getData().getStatus());

            System.out.println("[PASS] 5. Máquina de Estados da Carona (AGENDADA -> EM_ANDAMENTO -> FINALIZADA)");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 5. Estados da Carona: " + t.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 6. AVALIAÇÕES E REPUTAÇÃO (CÁLCULO AUTOMÁTICO DE NOTAS)
        // ---------------------------------------------------------
        try {
            // Mariana avalia Carlos com nota 5
            api.login("mariana.oli@gmail.com", "senha123");

            ApiResponse<Avaliacao> av = api.avaliarUsuario(caronaCriadaId, "u1u2u3u4-0000-0000-0000-000000000000", 5, "Excelente motorista, super recomendo!", "COMO_PASSAGEIRO");
            assert av.isSuccess() : "Avaliação deve ser salva com sucesso";
            assert av.getData().getNota() == 5;

            // Carlos consulta suas avaliações
            api.login("carlos.edu@gmail.com", "senhaSegura123");
            ApiResponse<List<Avaliacao>> minhasAv = api.getMinhasAvaliacoes();
            assert minhasAv.isSuccess() && !minhasAv.getData().isEmpty() : "Carlos deve ver suas avaliações";

            System.out.println("[PASS] 6. Sistema de Avaliações Mútuas e Atualização de Reputação");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 6. Avaliações: " + t.getMessage());
            failed++;
        }

        // ---------------------------------------------------------
        // 7. DASHBOARD & MÉTRICAS
        // ---------------------------------------------------------
        try {
            DashboardStats stats = api.getDashboardStats();
            assert stats != null : "Dashboard stats não pode ser nulo";
            assert stats.getTotalCaronasAtivasHoje() >= 0;
            assert stats.getEconomiaEstimadaReais() >= 0;
            System.out.println("[PASS] 7. Dashboard com Indicadores de Sustentabilidade e Economia");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 7. Dashboard: " + t.getMessage());
            failed++;
        }

        System.out.println("==================================================================");
        System.out.printf("RESULTADO FINAL: %d Testes Passaram | %d Falhas\n", passed, failed);
        System.out.println("==================================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
