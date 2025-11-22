package com.fiap.mindcare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

@Service
public class MindcareProcedureService {

    private final JdbcTemplate jdbcTemplate;
    private final String procedureSchema;

    public MindcareProcedureService(
            JdbcTemplate jdbcTemplate,
            @Value("${app.oracle.procedure-schema:}") String procedureSchema
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.procedureSchema = procedureSchema == null ? "" : procedureSchema.trim();
    }

    public String exportarJsonCompleto() {
        return jdbcTemplate.execute((ConnectionCallback<String>) connection -> {

            // 1) habilita buffer do DBMS_OUTPUT
            try (CallableStatement enable = connection.prepareCall("{ call dbms_output.enable(?) }")) {
                enable.setInt(1, 1_000_000); // 1MB de buffer
                enable.execute();
            }

            // 2) executa procedure de export
            String callExport = "{ call " + fullyQualifiedName("pkg_export_json_min.pr_all") + " }";
            try (CallableStatement cs = connection.prepareCall(callExport)) {
                cs.execute();
            }

            // 3) lê DBMS_OUTPUT linha a linha
            StringBuilder sb = new StringBuilder();
            try (CallableStatement getLine = connection.prepareCall("{ call dbms_output.get_line(?, ?) }")) {
                getLine.registerOutParameter(1, Types.VARCHAR); // line
                getLine.registerOutParameter(2, Types.INTEGER); // status

                while (true) {
                    getLine.execute();
                    String line = getLine.getString(1);
                    int status = getLine.getInt(2);

                    if (status != 0) break; // acabou
                    if (line != null) sb.append(line).append("\n");
                }
            }

            // 4) desconecta buffer (opcional)
            try (CallableStatement disable = connection.prepareCall("{ call dbms_output.disable }")) {
                disable.execute();
            }

            return sb.toString();
        });
    }

    // ==========================================================
    // ===================== EMPRESA ============================
    // pkg_empresa.inserir(p_id, p_cnpj, p_nome, p_plano)
    // ==========================================================
    public void inserirEmpresa(EmpresaProcRequest req) {
        executeProcedure(
                "pkg_empresa.inserir",
                ps -> {
                    ps.setInt(1, req.id());
                    ps.setString(2, req.cnpj());
                    ps.setString(3, req.nome());
                    ps.setString(4, req.planoSaude());
                },
                4
        );
    }

    // ==========================================================
    // ================== USUARIO_SISTEMA =======================
    // pkg_usuario_sistema.inserir(p_id, p_nome, p_email, p_senha, p_tipo, p_empresa_id)
    // ==========================================================
    public void inserirUsuarioSistema(UsuarioSistemaProcRequest req) {
        executeProcedure(
                "pkg_usuario_sistema.inserir",
                ps -> {
                    ps.setInt(1, req.id());
                    ps.setString(2, req.nome());
                    ps.setString(3, req.email());
                    ps.setString(4, req.senha());
                    ps.setString(5, req.tipo());       // ADMIN | USER
                    ps.setInt(6, req.empresaId());
                },
                6
        );
    }

    // ==========================================================
    // =================== PROFISSIONAL =========================
    // pkg_profissional.inserir(p_id, p_nome, p_especialidade, p_convenio, p_contato)
    // ==========================================================
    public void inserirProfissional(ProfissionalProcRequest req) {
        executeProcedure(
                "pkg_profissional.inserir",
                ps -> {
                    ps.setInt(1, req.id());
                    ps.setString(2, req.nome());
                    ps.setString(3, req.especialidade());
                    ps.setString(4, req.convenio());
                    ps.setString(5, req.contato());
                },
                5
        );
    }

    // ==========================================================
    // ====================== TRIAGEM ===========================
    // pkg_triagem.inserir(p_id, p_usuario_id, p_data_hora, p_relato, p_risco, p_sugestao)
    // ==========================================================
    public void inserirTriagem(TriagemProcRequest req) {
        executeProcedure(
                "pkg_triagem.inserir",
                ps -> {
                    ps.setInt(1, req.id());
                    ps.setInt(2, req.usuarioId());

                    if (req.dataHora() == null) {
                        ps.setNull(3, Types.TIMESTAMP);
                    } else {
                        ps.setTimestamp(3, Timestamp.valueOf(req.dataHora()));
                    }

                    ps.setString(4, req.relato());
                    ps.setString(5, req.risco()); // BAIXO | MODERADO | ALTO
                    ps.setString(6, req.sugestao());
                },
                6
        );
    }

    // ==========================================================
    // =================== ENCAMINHAMENTO =======================
    // pkg_encaminhamento.inserir(p_id, p_triagem_id, p_tipo, p_exame,
    //                            p_especialidade, p_prioridade, p_status,
    //                            p_observacao, p_profissional_id)
    // ==========================================================
    public void inserirEncaminhamento(EncaminhamentoProcRequest req) {
        executeProcedure(
                "pkg_encaminhamento.inserir",
                ps -> {
                    ps.setInt(1, req.id());
                    ps.setInt(2, req.triagemId());
                    ps.setString(3, req.tipo()); // EXAME | ESPECIALIDADE | PROFISSIONAL | HABITO
                    ps.setString(4, req.exame());
                    ps.setString(5, req.especialidade());
                    ps.setString(6, req.prioridade()); // BAIXA | MEDIA | ALTA
                    ps.setString(7, req.status());     // PENDENTE | AGENDADO | CONCLUIDO | CANCELADO
                    ps.setString(8, req.observacao());

                    if (req.profissionalId() == null) {
                        ps.setNull(9, Types.NUMERIC);
                    } else {
                        ps.setInt(9, req.profissionalId());
                    }
                },
                9
        );
    }

    // ==========================================================
    // =================== ACOMPANHAMENTO =======================
    // pkg_acompanhamento.inserir(p_id, p_encaminhamento_id, p_data_evento,
    //                             p_tipo_evento, p_descricao, p_anexo_url)
    // ==========================================================
    public void inserirAcompanhamento(AcompanhamentoProcRequest req) {
        executeProcedure(
                "pkg_acompanhamento.inserir",
                ps -> {
                    ps.setInt(1, req.id());
                    ps.setInt(2, req.encaminhamentoId());

                    if (req.dataEvento() == null) {
                        ps.setNull(3, Types.TIMESTAMP);
                    } else {
                        ps.setTimestamp(3, Timestamp.valueOf(req.dataEvento()));
                    }

                    ps.setString(4, req.tipoEvento()); // AGENDAMENTO | RESULTADO | ALTA | CANCELAMENTO | OBSERVACAO
                    ps.setString(5, req.descricao());
                    ps.setString(6, req.anexoUrl());
                },
                6
        );
    }

    // ==========================================================
    // ===================== CORE EXEC ==========================
    // ==========================================================
    private void executeProcedure(String procedureName, StatementSetter setter, int params) {
        try {
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                String call = buildCall(procedureName, params);
                try (CallableStatement cs = connection.prepareCall(call)) {
                    setter.set(cs);
                    cs.execute();
                }
                return null;
            });
        } catch (DataAccessException e) {
            Throwable root = e.getMostSpecificCause() != null ? e.getMostSpecificCause() : e;
            throw new IllegalStateException(
                    "Falha ao executar " + fullyQualifiedName(procedureName) + ": " + root.getMessage(), root
            );
        }
    }

    private String buildCall(String procedureName, int params) {
        String placeholders = String.join(",", java.util.Collections.nCopies(params, "?"));
        return "{ call " + fullyQualifiedName(procedureName) + "(" + placeholders + ") }";
    }

    private String fullyQualifiedName(String procedureName) {
        if (procedureSchema.isEmpty()) return procedureName;
        return procedureSchema + "." + procedureName;
    }

    @FunctionalInterface
    private interface StatementSetter {
        void set(CallableStatement ps) throws SQLException;
    }

    // ===================== REQUEST RECORDS =====================
    public record EmpresaProcRequest(Integer id, String cnpj, String nome, String planoSaude) {}
    public record UsuarioSistemaProcRequest(Integer id, String nome, String email, String senha,
                                            String tipo, Integer empresaId) {}
    public record ProfissionalProcRequest(Integer id, String nome, String especialidade,
                                          String convenio, String contato) {}
    public record TriagemProcRequest(Integer id, Integer usuarioId, LocalDateTime dataHora,
                                     String relato, String risco, String sugestao) {}
    public record EncaminhamentoProcRequest(Integer id, Integer triagemId, String tipo,
                                            String exame, String especialidade,
                                            String prioridade, String status,
                                            String observacao, Integer profissionalId) {}
    public record AcompanhamentoProcRequest(Integer id, Integer encaminhamentoId, LocalDateTime dataEvento,
                                            String tipoEvento, String descricao, String anexoUrl) {}
}