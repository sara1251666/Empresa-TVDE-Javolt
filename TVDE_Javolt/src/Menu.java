import Gestao.Empresa;
import Entidades.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Classe responsável pela Interface com o Utilizador (UI) via consola.
 * <p>
 * Gere a navegação entre menus, a recolha de dados do utilizador
 * e a invocação dos métodos da classe {@link Empresa}. Implementa um sistema
 * completo de menus hierárquicos para todas as operações do sistema TVDE.
 * </p>
 * <p>
 * REQUISITOS CUMPRIDOS:
 * - CRUD completo para todas as entidades (Viaturas, Condutores, Clientes, Reservas, Viagens)
 * - Transformação de reserva em viagem
 * - Verificação de sobreposição de reservas/viagens
 * - Estatísticas e relatórios exigidos
 * - Gestão de múltiplas empresas com ficheiros separados
 * - Validação de dependências na eliminação
 * - Interface robusta com validação de inputs
 * - Manipulação de ficheiros de texto conforme especificado (Formatter, Scanner, etc.)
 * </p>
 *
 * @author Grupo 1 - Javolt
 * @version 3.0
 * @since 2026-01-11
 */
public class Menu {

    private static Empresa empresa;
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    // =======================================================
    //           INICIALIZAÇÃO E SELEÇÃO DE EMPRESA
    // =======================================================

    public static void iniciar() {
        imprimirCabecalho("SISTEMA DE GESTÃO TVDE");

        String nomeEmpresa = obterNomeEmpresa();

        do {
            if (nomeEmpresa == null) {
                imprimirAviso("Operação cancelada. A sair...");
                return;
            }

            if (nomeEmpresa.isEmpty()) {
                imprimirErro("Nome da Empresa não pode ser vazio.");
                continue;
            }
            break; //Se o nome for válido, continua
        } while (true);

        empresa = new Empresa(nomeEmpresa);
        imprimirCabecalho("A gerir a empresa: " + nomeEmpresa);

        carregarDadosIniciais();
        executarMenuPrincipal();
        encerrarAplicacao(nomeEmpresa);
    }

    private static String obterNomeEmpresa() {
        ArrayList<String> empresas = Empresa.listarEmpresasExistentes();

        while (true) {
            if (empresas.isEmpty()) {
                imprimirErro("Nenhuma Empresa encontrada.");
                imprimirCabecalho("Menu Inicial ");
                System.out.println("| 1 - Criar Nova Empresa                         |");
                System.out.println("| 0 - Sair do Sistema                            |");
                imprimirLinha();

                int opcao = lerOpcaoMenu("Opção: ");
                switch (opcao) {
                    case 1 -> {
                        String nome = criarEmpresaNova();
                        if (nome == null) {
                            continue;
                        }
                        return nome;
                    }
                    case 0 -> {
                        return null;
                    }
                    default -> imprimirErro("Opção inválida.");
                }
            // É importante atualizar a lista caso a criação tenha falhado ou para a próxima iteração
            empresas = Empresa.listarEmpresasExistentes();
            } else {
                imprimirCabecalho("SELEÇÃO DE EMPRESA");
                System.out.println("| 1 - Criar Nova Empresa                           |");
                System.out.println("| 2 - Carregar Empresa Existente                   |");
                System.out.println("| 0 - Sair                                         |");
                imprimirLinha();

                int opcao = lerOpcaoMenu("Opção: ");
                switch (opcao) {
                    case 1 -> {
                        String nome = criarEmpresaNova();
                        if (nome == null) {
                            continue;
                        }
                        return nome;
                    }
                    case 2 -> {
                        String selecionada = listarESelecionarEmpresa(empresas);
                        if (selecionada == null) {
                            continue;
                        }
                        return selecionada;
                    }
                    case 0 -> {
                        return null;
                    }
                    default -> imprimirErro("Opção inválida.");
                }
            }
        }
    }

    private static String criarEmpresaNova() {
        try {
            imprimirCabecalho("CRIAÇÃO DE NOVA EMPRESA");
            exibirMsgCancelar();

            while (true) {
                String nome = lerTextoComCancelamento("Nome da nova empresa: ");
                if (Empresa.empresaExiste(nome)) {
                    imprimirErro("Já existe uma empresa com esse nome. Tente outro.");
                } else {
                    return nome;
                }
            }
        } catch (OperacaoCanceladaException e) {
            return null;
        }
    }

    private static String listarESelecionarEmpresa(ArrayList<String> empresas) {
        imprimirCabecalho("EMPRESAS ENCONTRADAS");
        for (int i = 0; i < empresas.size(); i++) {
            System.out.println("  " + (i + 1) + " - " + empresas.get(i));
        }
        System.out.println("  0 - Voltar");
        imprimirLinha();

        int opcao = lerOpcaoMenu("Opção: ");
        if (opcao == 0) {
            return null;
        } else if (opcao > 0 && opcao <= empresas.size()) {
            return empresas.get(opcao - 1);
        } else {
            imprimirErro("Opção inválida.");
            return null;
        }
    }

    private static void carregarDadosIniciais() {
        boolean temDados = empresa.existePastaEmpresa();

        if (temDados) {
            File pasta = new File(empresa.getCaminhoPastaEmpresa());
            String[] conteudo = pasta.list();
            if (conteudo == null || conteudo.length == 0) {
                temDados = false;
            }
        }

        try {
            if (temDados) {
                imprimirAviso("Foram encontrados registos anteriores!");
                String resposta = lerSimNaoCancelamento("Deseja carregar os dados guardados? (S/N): ");
                if (resposta.equalsIgnoreCase("S")) {
                    empresa.carregarDados();
                    imprimirAviso("Dados carregados com sucesso.");
                }
            }

            if (empresa.getViaturas().isEmpty() && empresa.getCondutores().isEmpty()) {
                imprimirAviso("A base de dados está vazia.");
                String resposta = lerSimNao("Deseja gerar dados de teste? (S/N): ");
                if (resposta.equalsIgnoreCase("S")) {
                    inicializarDadosTeste();
                }
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Configuração inicial interrompida.");
        }
    }

    // =======================================================
    //                  MENU PRINCIPAL
    // =======================================================

    private static void executarMenuPrincipal() {
        int opcao;
        do {
            imprimirCabecalho("MENU PRINCIPAL - TVDE");
            System.out.println("| 1 - Gestão de Viaturas                           |");
            System.out.println("| 2 - Gestão de Condutores                         |");
            System.out.println("| 3 - Gestão de Clientes                           |");
            System.out.println("| 4 - Gestão de Viagens                            |");
            System.out.println("| 5 - Gestão de Reservas                           |");
            System.out.println("| 6 - Consultas e Estatísticas                     |");
            System.out.println("| 7 - Gravar/Carregar Dados                        |");
            System.out.println("| 0 - Sair                                         |");
            imprimirLinha();

            opcao = lerOpcaoMenu("Opção: ");
            switch (opcao) {
                case 1 -> menuCRUD("Viaturas");
                case 2 -> menuCRUD("Condutores");
                case 3 -> menuCRUD("Clientes");
                case 4 -> menuViagens();
                case 5 -> menuReservas();
                case 6 -> menuEstatisticas();
                case 7 -> menuGestaoFicheiros();
                case 0 -> {
                    if (confirmarSaida()) return;
                }
                default -> imprimirErro("Opção inválida. Tente novamente.");
            }
        } while (true);
    }

    private static boolean confirmarSaida() {
        try {
            String resposta = lerSimNaoCancelamento("Tem a certeza que deseja sair? (S/N): ");
            return resposta.equalsIgnoreCase("S");
        } catch (OperacaoCanceladaException e) {
            return false;
        }
    }

    // =======================================================
    //               MENUS DE GESTÃO DE DADOS
    // =======================================================

    private static void menuGestaoFicheiros() {
        int opcao;
        do {
            imprimirCabecalho("GESTÃO DE DADOS - " + empresa.getNomeEmpresa());
            System.out.println("| 1 - Gravar Dados em Ficheiros                    |");
            System.out.println("| 2 - Carregar Dados de Ficheiro                   |");
            System.out.println("| 3 - Mudar de Empresa                             |");
            System.out.println("| 4 - Criar Nova Empresa                           |");
            System.out.println("| 5 - Eliminar Dados da Empresa                    |");
            System.out.println("| 0 - Voltar                                       |");
            imprimirLinha();

            opcao = lerOpcaoMenu("Opção: ");
            switch (opcao) {
                case 1 -> gravarDadosAtuais();
                case 2 -> carregarDadosAtuais();
                case 3 -> tratarMudarEmpresa();
                case 4 -> {
                    String novoNome = criarEmpresaNova();
                    if (novoNome != null) {
                        String mudar = lerSimNao("Empresa criada. Deseja mudar para " + novoNome + "? (S/N) ");
                        if (mudar.equalsIgnoreCase("S")) {

                            try {
                                String gravar = lerSimNaoCancelamento(
                                        "Deseja gravar os dados da empresa atual antes de mudar? (S/N): "
                                );

                                if (gravar.equalsIgnoreCase("S")) {
                                    empresa.gravarDados();
                                    imprimirAviso("Dados gravados com sucesso.");
                                }

                                empresa = new Empresa(novoNome);
                                imprimirAviso("A gerir a empresa: " + novoNome);

                                if (empresa.getViaturas().isEmpty() && empresa.getCondutores().isEmpty()) {
                                    String gerar = lerSimNao("Deseja gerar dados de teste? (S/N): ");
                                    if (gerar.equalsIgnoreCase("S")) {
                                        inicializarDadosTeste();
                                    }
                                }

                            } catch (OperacaoCanceladaException e) {
                                imprimirAviso("Mudança de empresa cancelada.");
                            }
                        }
                    }
                }

                case 5 -> eliminarDadosEmpresa();
                case 0 -> {
                    return;
                }
                default -> imprimirErro("Opção inválida.");
            }
        } while (true);
    }

    // =======================================================
    //           GRAVAR E CARREGAR DADOS ATUAIS
    // =======================================================

    private static void gravarDadosAtuais() {
        try {
            imprimirCabecalho("GRAVAR DADOS ATUAIS");
            imprimirAviso("Viaturas: " + empresa.getViaturas().size());
            imprimirAviso("Condutores: " + empresa.getCondutores().size());
            imprimirAviso("Clientes: " + empresa.getClientes().size());
            imprimirAviso("Viagens: " + empresa.getViagens().size());
            imprimirAviso("Reservas: " + empresa.getReservas().size());
            imprimirLinha();

            String caminhoPasta = empresa.getCaminhoPastaEmpresa();
            imprimirAviso("Local de gravação: " + caminhoPasta);

            File pasta = new File(caminhoPasta);
            if (pasta.exists()) {
                imprimirAviso("A pasta já existe.");
            } else {
                imprimirAviso("A pasta será criada automaticamente.");
            }

            String resposta = lerSimNaoCancelamento("\nTem a certeza que deseja gravar os dados? (S/N): ");

            if (resposta.equalsIgnoreCase("S")) {
                imprimirAviso("\nA gravar dados...");
                empresa.gravarDados();
                imprimirAviso("Dados gravados com sucesso!");
            } else {
                imprimirAviso("Operação cancelada.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    private static void carregarDadosAtuais() {
        try {
            imprimirCabecalho("CARREGAR DADOS DE OUTRA EMPRESA");
            imprimirAviso("ATENÇÃO: Esta operação irá substituir os dados atuais em memória.");
            imprimirAviso("Recomenda-se gravar os dados atuais antes de prosseguir.");
            System.out.println();
            imprimirTitulo("Dados atuais em memória:");
            imprimirAviso("Viaturas: " + empresa.getViaturas().size());
            imprimirAviso("Condutores: " + empresa.getCondutores().size());
            imprimirAviso("Clientes: " + empresa.getClientes().size());
            imprimirAviso("Viagens: " + empresa.getViagens().size());
            imprimirAviso("Reservas: " + empresa.getReservas().size());

            String resposta = lerSimNaoCancelamento("Tem a certeza que deseja continar? (S/N): ");

            if (!resposta.equalsIgnoreCase("S")) {
                imprimirAviso("Operação cancelada.");
                return;
            }

            imprimirCabecalho("INTRODUZA O NOME DA EMPRESA");
            exibirMsgCancelar();

            String nomeEmpresa = lerTextoComCancelamento("Nome da empresa a carregar: ");

            if (nomeEmpresa.equalsIgnoreCase(empresa.getNomeEmpresa())) {
                imprimirErro("Esta já é a Empresa atual.");
                return;
            }

            if (!Empresa.empresaExiste(nomeEmpresa)) {
                imprimirErro("A Empresa '" + nomeEmpresa + "' não foi encontrada.");

                resposta = lerSimNaoCancelamento("Deseja criar uma nova Empresa com este nome? ");
                if (!resposta.equalsIgnoreCase("S")) {
                    criarNovaEmpresaECarregar(nomeEmpresa);
                }
                return;
            }

            carregarEmpresaSelecionada(nomeEmpresa);

        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operacao cancelada.");
        }
    }

    private static void tratarMudarEmpresa(){
        ArrayList<String> empresas = Empresa.listarEmpresasExistentes();
        if (empresas.isEmpty()) {
            imprimirErro("Não existem outras empresas registadas.");
            return;
        }
        String novaEmpresa = listarESelecionarEmpresa(empresas);
        if (novaEmpresa != null) {
            try {
                String gravar = lerSimNaoCancelamento("Deseja gravar os dados da empresa atual antes de mudar? (S/N): ");
                if (gravar.equalsIgnoreCase("S")) {
                    empresa.gravarDados();
                    imprimirAviso("Dados gravados com sucesso.");
                }
                carregarEmpresaSelecionada(novaEmpresa);
            }catch (OperacaoCanceladaException e){
                imprimirAviso("Mudança de empresa cancelada.");
            }
        }
    }

    // =======================================================
    //           ELIMINAR DADOS DA EMPRESA
    // =======================================================

    private static void eliminarDadosEmpresa() {
        try {
            imprimirTitulo("ELIMINAR DADOS DA EMPRESA");
            imprimirAviso("ATENÇÃO: Esta operação irá APAGAR PERMANENTEMENTE todos os dados guardados.");
            imprimirAviso("Empresa: " + empresa.getNomeEmpresa());

            String caminhoPasta = empresa.getCaminhoPastaEmpresa();
            File pasta = new File(caminhoPasta);

            if (!pasta.exists()) {
                imprimirAviso("A empresa não tem dados guardados em ficheiro.");
                return;
            }

            String[] ficheiros = pasta.list();
            if (ficheiros == null || ficheiros.length == 0) {
                imprimirAviso("A pasta existe mas está vazia.");
                return;
            }

            imprimirTitulo("Ficheiros a Eliminar");
            for (String ficheiro : ficheiros) {
                System.out.println("-> " + ficheiro);
            }
            imprimirAviso("Total: " + ficheiros.length + " ficheiros");
            imprimirTitulo("DADOS ATUAIS EM MEMÓRIA");
            imprimirAviso("(não serão afetados)");
            imprimirAviso("Viaturas: " + empresa.getViaturas().size());
            imprimirAviso("Condutores: " + empresa.getCondutores().size());
            imprimirAviso("Clientes: " + empresa.getClientes().size());
            imprimirAviso("Viagens: " + empresa.getViagens().size());
            imprimirAviso("Reservas: " + empresa.getReservas().size());

            String resposta1 = lerSimNaoCancelamento("Tem a certeza que deseja eliminar os dados guardados? (S/N): ");
            if (!resposta1.equalsIgnoreCase("S")) {
                imprimirAviso("Operação cancelada.");
                return;
            }

            imprimirAviso("ATENÇÃO: Esta ação NÃO pode ser desfeita!");
            String resposta2 = lerSimNaoCancelamento("Confirmar eliminação PERMANENTE? (S/N): ");
            if (!resposta2.equalsIgnoreCase("S")) {
                imprimirAviso("Operação cancelada.");
                return;
            }
            imprimirAviso("A eliminar ficheiros...");
            int eliminados = 0;
            for (String ficheiro : ficheiros) {
                File file = new File(pasta, ficheiro);
                if (file.delete()) {
                    eliminados++;
                }
            }

            if (pasta.delete()) {
                imprimirAviso("Pasta da empresa eliminada com sucesso.");
            } else if (pasta.exists() && pasta.list() != null && pasta.list().length == 0) {
                imprimirAviso("Todos os ficheiros foram eliminados.");
            }
            imprimirAviso("Ficheiros eliminados: " + eliminados + " de " + ficheiros.length);
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operacao cancelada.");
        }
    }

    // =======================================================
    //           MÉTODOS AUXILIARES PARA CARREGAMENTO
    // =======================================================

    private static void criarNovaEmpresaECarregar(String nomeEmpresa) throws OperacaoCanceladaException {
        try {
            imprimirCabecalho("CRIAÇÃO DE NOVA EMPRESA: " + nomeEmpresa);

            if (Empresa.empresaExiste(nomeEmpresa)) {
                imprimirErro("Já existe uma empresa com esse nome. Operação cancelada.");
                return;
            }
            Empresa novaEmpresa = new Empresa(nomeEmpresa);

            imprimirAviso("Empresa '" + nomeEmpresa + "' criada com sucesso!");
            String resposta = lerSimNaoCancelamento("Deseja gerar dados de teste para esta empresa? (S/N): ");
            if (resposta.equalsIgnoreCase("S")) {
                inicializarDadosTeste();
            }

            empresa = novaEmpresa;
            imprimirTitulo("A gerir a empresa: " + nomeEmpresa);
        } catch (OperacaoCanceladaException e) {
            imprimirErro("Carregamento interrompido.");
        }
    }

    private static void carregarEmpresaSelecionada(String nomeEmpresa) {
        try {
            imprimirCabecalho("A CARREGAR EMPRESA " + nomeEmpresa);

            Empresa novaEmpresa = new Empresa(nomeEmpresa);

            if (novaEmpresa.existePastaEmpresa()) {
                File pasta = new File(novaEmpresa.getCaminhoPastaEmpresa());
                String[] conteudo = pasta.list();

                if (conteudo != null && conteudo.length > 0) {
                    imprimirAviso("Foram encontrados " + conteudo.length + " ficheiros de dados.");
                    String resposta = lerSimNaoCancelamento("Deseja carregar os dados guardados? (S/N): ");

                    if (resposta.equalsIgnoreCase("S")) {
                        novaEmpresa.carregarDados();
                        System.out.println();
                        imprimirAviso("Dados carregados com sucesso!");
                    } else {
                        imprimirAviso("A carregar Empresa sem dados!");
                    }
                } else {
                    imprimirAviso("A empresa existe mas não tem dados guardados.");
                }
            } else {
                imprimirAviso("A empresa '" + nomeEmpresa + "' não tem pasta de dados.");
            }
            empresa = novaEmpresa;
            if (empresa.getViaturas().isEmpty() && empresa.getCondutores().isEmpty()) {
                imprimirAviso("A base de dados está vazia.");
                String resposta = lerSimNaoCancelamento("Deseja gerar dados de teste? (S/N): ");

                if (resposta.equalsIgnoreCase("S")) {
                    inicializarDadosTeste();
                }
            }
            imprimirAviso("A gerir a empresa: " + nomeEmpresa);
        } catch (OperacaoCanceladaException e) {

        }
    }


    // =======================================================
    //               MENUS CRUD GENÉRICOS
    // =======================================================

    private static void menuCRUD(String tipo) {
        int opcao;
        do {
            imprimirCabecalho("GESTÃO DE " + tipo.toUpperCase());
            System.out.println("| 1 - Criar (Create)                               |");
            System.out.println("| 2 - Listar (Read)                                |");
            System.out.println("| 3 - Atualizar (Update)                           |");
            System.out.println("| 4 - Apagar (Delete)                              |");
            System.out.println("| 0 - Voltar                                       |");
            imprimirLinha();

            opcao = lerOpcaoMenu("Opção: ");
            try {
                switch (tipo) {
                    case "Viaturas" -> processarViaturas(opcao);
                    case "Condutores" -> processarCondutores(opcao);
                    case "Clientes" -> processarClientes(opcao);
                }
            } catch (OperacaoCanceladaException e) {
                imprimirAviso("Operação cancelada.");
            }
        } while (opcao != 0);
    }

    // =======================================================
    //                  GESTÃO DE VIATURAS
    // =======================================================

    private static void processarViaturas(int opcao) throws OperacaoCanceladaException {
        switch (opcao) {
            case 1 -> criarViatura();
            case 2 -> listarViaturas(empresa.getViaturas());
            case 3 -> atualizarViatura();
            case 4 -> eliminarViatura();
        }
    }

    private static void criarViatura() throws OperacaoCanceladaException {
        imprimirTitulo("NOVA VIATURA");
        exibirMsgCancelar();

        String matricula = lerMatriculaUnica();
        String marca = lerTextoComCancelamento("Marca: ");
        String modelo = lerTextoComCancelamento("Modelo: ");
        int ano = lerAnoValido();

        try {
            Viatura viatura = new Viatura(matricula, marca, modelo, ano);
            if (empresa.adicionarViatura(viatura)) {
                imprimirAviso("Viatura adicionada com sucesso!");
            } else {
                imprimirErro("Não foi possível adicionar a viatura.");
            }
        } catch (IllegalArgumentException e) {
            imprimirErro("Erro ao criar viatura: " + e.getMessage());
        }
    }

    private static String lerMatriculaUnica() throws OperacaoCanceladaException {
        String matricula;
        do {
            matricula = lerTextoComCancelamento("Matrícula: ");
            if (empresa.procurarViatura(matricula) != null) {
                imprimirErro("Viatura com essa matrícula já existe.");
            }
        } while (empresa.procurarViatura(matricula) != null);
        return matricula;
    }

    private static void atualizarViatura() throws OperacaoCanceladaException {
        imprimirTitulo("ATUALIZAR VIATURA");

        String verLista = lerSimNaoCancelamento("Deseja ver a lista de viaturas? (S/N): ");
        if (verLista.equalsIgnoreCase("S")) {
            listarViaturas(empresa.getViaturas());
        }

        String matricula = lerTextoComCancelamento("Matrícula da viatura a editar: ");
        Viatura viatura = empresa.procurarViatura(matricula);

        if (viatura == null) {
            imprimirErro("Viatura não encontrada.");
            return;
        }

        System.out.println("\nDados atuais: " + viatura);
        System.out.println("(Nota: Prima ENTER para avançar. Digite 0 para cancelar.)");

        System.out.print("\nNova Marca [" + viatura.getMarca() + "]: ");
        String novaMarca = lerTextoComCancelamento("");
        if (!novaMarca.isEmpty()) {
            viatura.setMarca(novaMarca);
        }

        System.out.print("Novo Modelo [" + viatura.getModelo() + "]: ");
        String novoModelo = lerTextoComCancelamento("");
        if (!novoModelo.isEmpty()) {
            viatura.setModelo(novoModelo);
        }

        System.out.print("Novo Ano [" + viatura.getAnoFabrico() + "]: ");
        String anoInput = lerTextoComCancelamento("");
        if (!anoInput.isEmpty()) {
            try {
                int ano = Integer.parseInt(anoInput);
                if (ano >= 1886 && ano <= 2026) {
                    viatura.setAnoFabrico(ano);
                } else {
                    imprimirErro("Ano inválido (1886-2026). Valor antigo mantido.");
                }
            } catch (NumberFormatException e) {
                imprimirErro("Ano inválido. Mantido o anterior.");
            }
        }
    }

    private static void eliminarViatura() throws OperacaoCanceladaException {
        imprimirTitulo("ELIMINAR VIATURA");

        String verLista = lerSimNaoCancelamento("Deseja ver a lista de viaturas? (S/N): ");
        if (verLista.equalsIgnoreCase("S")) {
            listarViaturas(empresa.getViaturas());
        }

        String matricula = lerTextoComCancelamento("Matrícula da viatura a eliminar: ");
        Viatura viatura = empresa.procurarViatura(matricula);

        if (viatura == null) {
            imprimirErro("Viatura com a matricula: [" + matricula + "] não existe.");
            return;
        }
        if (empresa.removerViatura(matricula)) {
            imprimirAviso("Viatura removida com sucesso.");
        } else {
            imprimirErro("Viatura tem viagens associadas.");
        }
    }

// =======================================================
//                  GESTÃO DE CONDUTORES
// =======================================================

    private static void processarCondutores(int opcao) throws OperacaoCanceladaException {
        switch (opcao) {
            case 1 -> criarCondutor();
            case 2 -> listarCondutores(empresa.getCondutores());
            case 3 -> atualizarCondutor();
            case 4 -> eliminarCondutor();
        }
    }

    private static void criarCondutor() throws OperacaoCanceladaException {
        imprimirTitulo("NOVO CONDUTOR");
        exibirMsgCancelar();

        int numeroIdentificacao = lerNumeroIdentificacaoUnico();
        String nome = lerTextoComCancelamento("Nome: ");
        int nif = lerNifUnico("Condutor");
        int telefone = lerInteiroComCancelamento("Telemóvel: ");
        String morada = lerTextoComCancelamento("Morada: ");
        int cartaoCidadao = lerInteiroComCancelamento("Cartão Cidadão: ");
        String cartaConducao = lerTextoComCancelamento("Carta Condução: ");
        int segurancaSocial = lerInteiroComCancelamento("Segurança Social: ");

        try {
            Condutor condutor = new Condutor(numeroIdentificacao, nome, nif, telefone,
                    morada, cartaoCidadao, cartaConducao, segurancaSocial);
            if (empresa.adicionarCondutor(condutor)) {
                imprimirAviso("Condutor registado com sucesso!");
            } else {
                imprimirErro("Não foi possível registar o condutor.");
            }
        } catch (IllegalArgumentException e) {
            imprimirErro("Erro nos dados: " + e.getMessage());
        }
    }

    private static int lerNumeroIdentificacaoUnico() throws OperacaoCanceladaException {
        int numeroId;
        while (true) {
            numeroId = lerInteiroComCancelamento("Número de identificação: ");

            if (numeroId <= 0) {
                imprimirErro("O número deve ser positivo.");
                continue;
            }

            if (empresa.procurarCondutorPorId(numeroId) != null) {
                imprimirErro("Já existe um condutor com esse número.");
                continue;
            }
            break;
        }
        return numeroId;
    }

    private static void atualizarCondutor() throws OperacaoCanceladaException {
        imprimirTitulo("ATUALIZAR CONDUTOR");

        String verLista = lerSimNaoCancelamento("Deseja ver a lista de condutores? (S/N): ");
        if (verLista.equalsIgnoreCase("S")) {
            listarCondutores(empresa.getCondutores());
        }

        int numeroId = lerInteiroComCancelamento("Número de identificação do condutor: ");
        Condutor condutor = empresa.procurarCondutorPorId(numeroId);

        if (condutor == null) {
            imprimirErro("Condutor não encontrado.");
            return;
        }

        System.out.println("\nDados atuais: " + condutor);
        System.out.println("(Nota: Prima ENTER para avançar. Digite 0 para cancelar.)");
        System.out.print("\nNovo Nome [" + condutor.getNome() + "]: ");
        String novoNome = lerTextoComCancelamento("");
        if (!novoNome.isEmpty()) {
            condutor.setNome(novoNome);
        }

        System.out.print("Novo Telemóvel [" + condutor.getTel() + "]: ");
        String novoTel = lerTextoComCancelamento("");
        if (!novoTel.isEmpty()) {
            try {
                condutor.setTel(Integer.parseInt(novoTel));
            } catch (NumberFormatException e) {
                imprimirErro("Telemóvel inválido. Mantido o anterior.");
            }
        }

        System.out.print("Nova Morada [" + condutor.getMorada() + "]: ");
        String novaMorada = lerTextoComCancelamento("");
        if (!novaMorada.isEmpty()) {
            condutor.setMorada(novaMorada);
        }

        System.out.print("Nova Carta Condução [" + condutor.getCartaCond() + "]: ");
        String novaCarta = lerTextoComCancelamento("");
        if (!novaCarta.isEmpty()) {
            condutor.setCartaCond(novaCarta);
        }

        imprimirAviso("Condutor atualizado com sucesso.");
    }

    private static void eliminarCondutor() throws OperacaoCanceladaException {
        imprimirTitulo("ELIMINAR CONDUTOR");

        String verLista = lerSimNaoCancelamento("Deseja ver a lista de condutores? (S/N): ");
        if (verLista.equalsIgnoreCase("S")) {
            listarCondutores(empresa.getCondutores());
        }

        int numeroId = lerInteiroComCancelamento("Número de identificação do condutor: ");
        Condutor condutor = empresa.procurarCondutorPorId(numeroId);
        if (condutor == null) {
            imprimirErro("Condutor com ID [" + numeroId + "] não existe.");
            return;
        }

        if (empresa.removerCondutor(numeroId)) {
            imprimirAviso("Condutor removido com sucesso.");
        } else {
            imprimirErro("Não pode remover condutor com histórico de viagens.");
        }
    }

// =======================================================
//                  GESTÃO DE CLIENTES
// =======================================================

    private static void processarClientes(int opcao) throws OperacaoCanceladaException {
        switch (opcao) {
            case 1 -> criarCliente();
            case 2 -> listarClientes(empresa.getClientes());
            case 3 -> atualizarCliente();
            case 4 -> eliminarCliente();
        }
    }

    private static void criarCliente() throws OperacaoCanceladaException {
        imprimirTitulo("NOVO CLIENTE");
        exibirMsgCancelar();

        int nif = lerNifUnico("Cliente");
        String nome = lerTextoComCancelamento("Nome: ");
        int telefone = lerInteiroComCancelamento("Telemóvel: ");
        String morada = lerTextoComCancelamento("Morada: ");
        int cartaoCidadao = lerInteiroComCancelamento("Cartão Cidadão: ");

        try {
            Cliente cliente = new Cliente(nome, nif, telefone, morada, cartaoCidadao);
            if (empresa.adicionarCliente(cliente)) {
                imprimirAviso("Cliente registado com sucesso!");
            }
        } catch (IllegalArgumentException e) {
            imprimirErro("Erro nos dados: " + e.getMessage());
        }
    }

    private static int lerNifUnico(String tipo) throws OperacaoCanceladaException {
        int nif;
        while (true) {
            nif = lerInteiroComCancelamento("NIF (9 dígitos): ");

            if (String.valueOf(nif).length() != 9) {
                imprimirErro("O NIF deve ter exatamente 9 dígitos.");
                continue;
            }

            if (empresa.nifExiste(nif)) {
                imprimirErro("Já existe um " + tipo + " com esse NIF.");
                continue;
            }
            break;
        }
        return nif;
    }

    private static void atualizarCliente() throws OperacaoCanceladaException {
        imprimirTitulo("ATUALIZAR CLIENTE");

        String verLista = lerSimNaoCancelamento("Deseja ver a lista de clientes? (S/N): ");
        if (verLista.equalsIgnoreCase("S")) {
            listarClientes(empresa.getClientes());
        }

        int nif = lerInteiroComCancelamento("NIF do cliente: ");
        Cliente cliente = empresa.procurarCliente(nif);

        if (cliente == null) {
            imprimirErro("Cliente não encontrado.");
            return;
        }

        System.out.println("\nDados atuais: " + cliente);
        System.out.println("(Nota: Prima ENTER para avançar. Digite 0 para cancelar.)");
        System.out.print("\nNovo Nome [" + cliente.getNome() + "]: ");
        String novoNome = lerTextoOpcional("");
        if (!novoNome.isEmpty()) {
            cliente.setNome(novoNome);
        }

        System.out.print("Novo Telemóvel [" + cliente.getTel() + "]: ");
        String novoTel = lerTextoOpcional("");
        if (!novoTel.isEmpty()) {
            try {
                cliente.setTel(Integer.parseInt(novoTel));
            } catch (NumberFormatException e) {
                imprimirErro("Telemóvel inválido. Mantido o anterior.");
            }
        }

        System.out.print("Nova Morada [" + cliente.getMorada() + "]: ");
        String novaMorada = lerTextoOpcional("");
        if (!novaMorada.isEmpty()) {
            cliente.setMorada(novaMorada);
        }

        imprimirAviso("Cliente atualizado com sucesso.");
    }

    private static void eliminarCliente() throws OperacaoCanceladaException {
        imprimirTitulo("ELIMINAR CLIENTE");

        String verLista = lerSimNaoCancelamento("Deseja ver a lista de clientes? (S/N): ");
        if (verLista.equalsIgnoreCase("S")) {
            listarClientes(empresa.getClientes());
        }

        int nif = lerInteiroComCancelamento("NIF do cliente: ");
        Cliente cliente = empresa.procurarCliente(nif);

        if (cliente == null) {
            imprimirErro("Cliente NIF [" + nif + "] não existe.");
            return;
        }

        if (empresa.removerCliente(nif)) {
            imprimirAviso("Cliente removido com sucesso.");
        } else {
            imprimirErro("Não pode remover cliente com histórico ou reservas.");
        }
    }

// =======================================================
//                  GESTÃO DE VIAGENS
// =======================================================

    private static void menuViagens() {
        int opcao;
        do {
            imprimirCabecalho("GESTÃO DE VIAGENS");
            System.out.println("| 1 - Registar Nova Viagem                         |");
            System.out.println("| 2 - Transformar Reserva em Viagem                |");
            System.out.println("| 3 - Listar Todas as Viagens                      |");
            System.out.println("| 4 - Eliminar Viagem                              |");
            System.out.println("| 5 - Verificar Conflitos de Horário               |");
            System.out.println("| 0 - Voltar                                       |");
            imprimirLinha();

            opcao = lerOpcaoMenu("Opção: ");
            switch (opcao) {
                case 1 -> tratarRegistarViagem();
                case 2 -> tratarConverterReserva();
                case 3 -> listarViagens(empresa.getViagens());
                case 4 -> tratarEliminarViagem();
                case 5 -> verificarConflitosHorario();
                case 0 -> {
                    return;
                }
                default -> imprimirErro("Opção inválida.");
            }
        } while (true);
    }

    private static void tratarRegistarViagem() {
        try {
            imprimirTitulo("NOVA VIAGEM");
            exibirMsgCancelar();

            LocalDateTime inicio = lerDataComCancelamento("Início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Fim (dd-MM-yyyy HH:mm): ");

            if (fim.isBefore(inicio)) {
                imprimirErro("Data de fim anterior à data de início.");
                return;
            }

            Condutor condutor = selecionarCondutorDisponivel(inicio, fim);
            if (condutor == null) return;

            Cliente cliente = selecionarClienteDisponivel(inicio, fim);
            if (cliente == null) return;

            Viatura viatura = selecionarViaturaDisponivel(inicio, fim);
            if (viatura == null) return;

            String origem = lerTextoComCancelamento("Origem: ");
            String destino = lerTextoComCancelamento("Destino: ");
            double kms = lerDoubleComCancelamento("Kms percorridos: ");
            double custo = lerDoubleComCancelamento("Custo (€): ");

            Viagem viagem = new Viagem(condutor, cliente, viatura, inicio, fim,
                    origem, destino, kms, custo);

            if (empresa.adicionarViagem(viagem)) {
                imprimirAviso("Viagem registada com sucesso!");
            } else {
                imprimirErro("Conflito de horário detetado.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void tratarEliminarViagem() {
        try {
            ArrayList<Viagem> viagens = empresa.getViagens();
            if (viagens.isEmpty()) {
                imprimirAviso("Não existem viagens para eliminar.");
                return;
            }

            imprimirTitulo("ELIMINAR VIAGEM");
            listarViagens(viagens);

            int index = lerInteiroComCancelamento("Número da viagem a eliminar: ") - 1;

            if (index >= 0 && index < viagens.size()) {
                Viagem viagem = viagens.get(index);
                if (empresa.removerViagem(viagem)) {
                    imprimirAviso("Viagem eliminada com sucesso!");
                } else {
                    imprimirErro("Não foi possível eliminar a viagem.");
                }
            } else {
                imprimirErro("Opção inválida.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

// =======================================================
//                  GESTÃO DE RESERVAS
// =======================================================

    private static void menuReservas() {
        int opcao;
        do {
            imprimirCabecalho("GESTÃO DE RESERVAS");
            System.out.println("| 1 - Criar Nova Reserva                           |");
            System.out.println("| 2 - Listar Reservas Pendentes                    |");
            System.out.println("| 3 - Consultar/Alterar Reservas de Cliente        |");
            System.out.println("| 5 - Eliminar Reserva                             |");
            System.out.println("| 6 - Verificar Conflitos de Horário               |");
            System.out.println("| 0 - Voltar                                       |");
            imprimirLinha();

            opcao = lerOpcaoMenu("Opção: ");
            switch (opcao) {
                case 1 -> tratarCriarReserva();
                case 2 -> listarReservas(empresa.getReservas());
                case 3 -> tratarConsultarReservasCliente();
                case 4 -> tratarAlterarReserva();
                case 5 -> verificarConflitoReserva();
                case 0 -> {
                    return;
                }
                default -> imprimirErro("Opção inválida.");
            }
        } while (true);
    }

    private static void tratarCriarReserva() {
        try {
            imprimirTitulo("NOVA RESERVA");
            exibirMsgCancelar();

            LocalDateTime inicio = lerDataComCancelamento("Inicio: Data/Hora (dd-MM-yyyy HH:mm): ");

            int nifCliente = lerInteiroComCancelamento("NIF do Cliente: ");
            Cliente cliente = empresa.procurarCliente(nifCliente);

            if (cliente == null) {
                imprimirErro("Cliente não encontrado.");
                return;
            }

            String origem = lerTextoComCancelamento("Origem: ");
            String destino = lerTextoComCancelamento("Destino: ");
            double kms = lerDoubleComCancelamento("Kms estimados: ");

            Reserva reserva = new Reserva(cliente, inicio, origem, destino, kms);
            empresa.adicionarReserva(reserva);
            imprimirAviso("Reserva criada com sucesso!");
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void tratarConsultarReservasCliente() {
        try {
            imprimirTitulo("RESERVAS DO CLIENTE");
            int nif = lerInteiroComCancelamento("NIF do cliente: ");
            Cliente cliente = empresa.procurarCliente(nif);

            if (cliente == null) {
                imprimirErro("Cliente não encontrado.");
                return;
            }

            ArrayList<Reserva> reservas = empresa.getReservasDoCliente(nif);
            if (reservas.isEmpty()) {
                imprimirAviso("Este cliente não tem reservas pendentes.");
            } else {
                imprimirTitulo("Reservas de " + cliente.getNome());
                listarReservas(reservas);
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void tratarAlterarReserva() {
        try {
            imprimirTitulo("ALTERAR RESERVA");

            int nif = lerInteiroComCancelamento("NIF do cliente: ");
            ArrayList<Reserva> reservas = empresa.getReservasDoCliente(nif);

            if (reservas.isEmpty()) {
                imprimirAviso("Este cliente não tem reservas para alterar.");
                return;
            }

            listarReservas(reservas);
            int index = lerInteiroComCancelamento("Número da reserva a alterar: ") - 1;

            if (index < 0 || index >= reservas.size()) {
                imprimirErro("Opção inválida.");
                return;
            }

            Reserva reserva = reservas.get(index);

            imprimirCabecalho("ALTERAÇÃO DE RESERVA");
            System.out.println("1 - Data/Hora");
            System.out.println("2 - Origem");
            System.out.println("3 - Destino");
            System.out.println("4 - Kms estimados");
            System.out.println("0 - Cancelar");

            int opcao = lerOpcaoMenu("O que deseja alterar? ");

            switch (opcao) {
                case 1 -> {
                    LocalDateTime novaData = lerDataComCancelamento("Nova data/hora: ");
                    reserva.setDataHoraInicio(novaData);
                }
                case 2 -> {
                    String novaOrigem = lerTextoComCancelamento("Nova origem: ");
                    reserva.setMoradaOrigem(novaOrigem);
                }
                case 3 -> {
                    String novoDestino = lerTextoComCancelamento("Novo destino: ");
                    reserva.setMoradaDestino(novoDestino);
                }
                case 4 -> {
                    double novosKms = lerDoubleComCancelamento("Novos kms: ");
                    reserva.setKms(novosKms);
                }
                case 0 -> {
                    imprimirAviso("Alteração cancelada.");
                    return;
                }
                default -> {
                    imprimirErro("Opção inválida.");
                    return;
                }
            }

            imprimirAviso("Reserva alterada com sucesso!");
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void tratarConverterReserva() {
        try {
            ArrayList<Reserva> reservas = empresa.getReservas();
            if (reservas.isEmpty()) {
                imprimirAviso("Não existem reservas para converter.");
                return;
            }

            imprimirTitulo("CONVERTER RESERVA EM VIAGEM");
            listarReservas(reservas);

            int index = lerInteiroComCancelamento("Número da reserva a converter: ") - 1;

            if (index < 0 || index >= reservas.size()) {
                imprimirErro("Opção inválida.");
                return;
            }

            Reserva reserva = reservas.get(index);
            LocalDateTime inicio = reserva.getDataHoraInicio();
            LocalDateTime fim = inicio.plusHours(1); // Duração estimada

            Condutor condutor = selecionarCondutorDisponivel(inicio, fim);
            if (condutor == null) return;

            Viatura viatura = selecionarViaturaDisponivel(inicio, fim);
            if (viatura == null) return;

            double custo = lerDoubleComCancelamento("Custo final da viagem (€): ");

            if (empresa.converterReservaEmViagem(reserva, condutor, viatura, custo)) {
                imprimirAviso("Reserva convertida em viagem com sucesso!");
            } else {
                imprimirErro("Não foi possível converter a reserva.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void tratarEliminarReserva() {
        try {
            ArrayList<Reserva> reservas = empresa.getReservas();
            if (reservas.isEmpty()) {
                imprimirAviso("Não existem reservas para eliminar.");
                return;
            }

            imprimirTitulo("ELIMINAR RESERVA");
            listarReservas(reservas);

            int index = lerInteiroComCancelamento("Número da reserva a eliminar: ") - 1;

            if (index >= 0 && index < reservas.size()) {
                Reserva reserva = reservas.get(index);
                if (empresa.removerReserva(reserva)) {
                    imprimirAviso("Reserva eliminada com sucesso!");
                } else {
                    imprimirErro("Não foi possível eliminar a reserva.");
                }
            } else {
                imprimirErro("Opção inválida.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void verificarConflitosHorario(){
        try {
            imprimirCabecalho("VERIFICAR DISPONIBILIDADE");
            exibirMsgCancelar();

            LocalDateTime inicio = lerDataComCancelamento("Início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Fim (dd-MM-yyyy HH:mm): ");

            if (fim.isBefore(inicio)) {
                imprimirErro("Data de fim anterior à data de início.");
                return;
            }
            System.out.println("| 1 - Verificar Disponibilidade de Condutor        |");
            System.out.println("| 2 - Verificar Disponibilidade de Viatura         |");
            System.out.println("| 0 - Voltar                                       |");
            imprimirLinha();

            int opcao = lerOpcaoMenu("Opção: ");

            switch (opcao) {
                case 1 -> {
                    int id = lerInteiroComCancelamento("ID do Condutor: ");
                    Condutor condutor = empresa.procurarCondutorPorId(id);
                    if (condutor == null) {
                        imprimirErro("Condutor não encontrado.");
                        return;
                    }

                    ArrayList<Condutor> disponiveis = empresa.getCondutoresDisponiveis(inicio, fim);
                    boolean disponivel = false;

                    for (int i = 0; i < disponiveis.size(); i++) {
                        if (disponiveis.get(i).equals(condutor)) {
                            disponivel = true;
                            break;
                        }
                    }

                    if (disponivel) {
                        imprimirAviso("STATUS: O condutor " + condutor.getNome() + " está DISPONÍVEL.");
                    }else {
                        imprimirErro("STATUS: O condutor " + condutor.getNome() + " está OCUPADO neste horário.");
                    }
                }
                case 2 -> {
                    String matricula = lerTextoComCancelamento("Matrícula da Viatura: ");
                    Viatura viatura = empresa.procurarViatura(matricula);
                    if (viatura == null) {
                        imprimirErro("Viatura não encontrada.");
                        return;
                    }

                    ArrayList<Viatura> disponiveis = empresa.getViaturasDisponiveis(inicio, fim);
                    boolean disponivel = false;

                    for (int i = 0; i < disponiveis.size(); i++) {
                        if (disponiveis.get(i).equals(viatura)) {
                            disponivel = true;
                            break;
                        }
                    }

                    if (disponivel) {
                        imprimirAviso("STATUS: A viatura " + viatura.getMatricula() + " está DISPONÍVEL.");
                    } else {
                        imprimirErro("STATUS: A viatura " + viatura.getMatricula() + " está OCUPADA neste horário.");
                    }
                }
                case 0 -> {
                    return;
                }
                default -> imprimirErro("Opção inválida.");
            }
        } catch (OperacaoCanceladaException e){
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void verificarConflitoReserva() {
        try{
            imprimirTitulo("VERIFICAR DISPONIBILIDADE DE CLIENTE");
            exibirMsgCancelar();

            int nif = lerInteiroComCancelamento("NIF do Cliente: ");
            Cliente cliente = empresa.procurarCliente(nif);

            if (cliente == null) {
                imprimirErro("Cliente não encontrado.");
                return;
            }
            LocalDateTime inicio = lerDataComCancelamento("Início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Fim (dd-MM-yyyy HH:mm): ");

            ArrayList<Cliente> disponiveis = empresa.getClientesDisponiveis(inicio, fim);

            boolean encontrado = false;

            for (int i = 0; i < disponiveis.size(); i++) {
                if (disponiveis.get(i).equals(cliente)) {
                    encontrado = true;
                    break;
                }
            }

            if (encontrado) {
                imprimirAviso("STATUS: O cliente " + cliente.getNome() + " está LIVRE para viajar neste horário.");
            } else {
                imprimirErro("STATUS: O cliente " + cliente.getNome() + " já tem viagem ou reserva sobreposta neste horário.");
            }

        }catch (OperacaoCanceladaException e){
            imprimirAviso("Operação cancelada.");
        }
    }
// =======================================================
//                  ESTATÍSTICAS E RELATÓRIOS
// =======================================================

    private static void menuEstatisticas() {
        int opcao;
        do {
            imprimirCabecalho("CONSULTAS E ESTATÍSTICAS");
            System.out.println("| 1 - Pesquisar Viatura por Matrícula               |");
            System.out.println("| 2 - Viagens de Cliente por Datas                  |");
            System.out.println("| 3 - Clientes de uma Viatura                       |");
            System.out.println("| 4 - Faturação por Motorista                       |");
            System.out.println("| 5 - Distância Média das Viagens                   |");
            System.out.println("| 6 - Destino Mais Solicitado                       |");
            System.out.println("| 7 - Clientes por Intervalo de Kms                 |");
            System.out.println("| 0 - Voltar                                        |");
            imprimirLinha();

            opcao = lerOpcaoMenu("Opção: ");
            switch (opcao) {
                case 1 -> pesquisarViaturaMatricula();
                case 2 -> estatHistoricoCliente();
                case 3 -> estatClientesViatura();
                case 4 -> estatFaturacaoCondutor();
                case 5 -> estatDistanciaMedia();
                case 6 -> estatDestinoMaisSolicitado();
                case 7 -> estatClientesPorIntervaloKms();
                case 0 -> {
                    return;
                }
                default -> imprimirErro("Opção inválida.");
            }
        } while (true);
    }

    private static void estatFaturacaoCondutor() {
        try {
            imprimirTitulo("FATURAÇÃO POR CONDUTOR");
            exibirMsgCancelar();

            String verLista = lerSimNaoCancelamento("Ver lista de condutores? (S/N): ");
            if (verLista.equalsIgnoreCase("S")) {
                listarCondutores(empresa.getCondutores());
            }

            int numeroId = lerInteiroComCancelamento("Número de identificação do condutor: ");
            Condutor condutor = empresa.procurarCondutorPorId(numeroId);

            if (condutor == null) {
                imprimirErro("Condutor não encontrado.");
                return;
            }

            LocalDateTime inicio = lerDataComCancelamento("Data início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Data fim (dd-MM-yyyy HH:mm): ");

            double faturacao = empresa.calcularFaturacaoCondutor(numeroId, inicio, fim);
            System.out.println(">> O condutor " + condutor.getNumeroIdentificacao() + "| Nome: " + condutor.getNome() +
                    " | faturou: " + faturacao);
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void estatClientesViatura() {
        try {
            imprimirTitulo("CLIENTES POR VIATURA");
            exibirMsgCancelar();

            String verLista = lerSimNaoCancelamento("Ver lista de viaturas? (S/N): ");
            if (verLista.equalsIgnoreCase("S")) {
                listarViaturas(empresa.getViaturas());
            }

            String matricula = lerTextoComCancelamento("Matrícula da viatura: ");
            Viatura viatura = empresa.procurarViatura(matricula);

            if (viatura == null) {
                imprimirErro("Viatura não encontrada.");
                return;
            }

            ArrayList<Cliente> clientes = empresa.getClientesPorViatura(matricula);
            if (clientes.isEmpty()) {
                imprimirAviso("Esta viatura ainda não transportou clientes.");
            } else {
                imprimirTitulo("Clientes da viatura: " + matricula);
                listarClientes(clientes);
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void estatDestinoMaisSolicitado() {
        try {
            imprimirTitulo("DESTINO MAIS SOLICITADO");
            exibirMsgCancelar();

            LocalDateTime inicio = lerDataComCancelamento("Data início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Data fim (dd-MM-yyyy HH:mm): ");

            String destino = empresa.getDestinoMaisSolicitado(inicio, fim);
            System.out.println("\n>> Destino mais popular: " + destino);
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void estatDistanciaMedia() {
        try {
            imprimirTitulo("DISTÂNCIA MÉDIA DAS VIAGENS");
            exibirMsgCancelar();

            LocalDateTime inicio = lerDataComCancelamento("Data início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Data fim (dd-MM-yyyy HH:mm): ");

            double media = empresa.calcularDistanciaMedia(inicio, fim);
            System.out.printf("\n>> Distância média: %.2f Kms\n", media);
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void estatClientesPorIntervaloKms() {
        try {
            imprimirTitulo("CLIENTES POR INTERVALO DE KMS");
            exibirMsgCancelar();

            double minimo = lerDoubleComCancelamento("Kms mínimos: ");
            double maximo = lerDoubleComCancelamento("Kms máximos: ");

            ArrayList<Cliente> clientes = empresa.getClientesPorIntervaloKms(minimo, maximo);
            if (clientes.isEmpty()) {
                imprimirAviso("Nenhum cliente encontrado nesse intervalo.");
            } else {
                imprimirTitulo("Clientes com viagens entre " + minimo + " e " + maximo + " Kms");
                for (Cliente cliente : clientes) {
                    double totalKms = empresa.calcularTotalKmsCliente(cliente.getNif());
                    System.out.printf("-> " + cliente.getNome() + " | NIF: " + cliente.getNif() + " | Total: " + totalKms + " Kms.");
                }
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void estatHistoricoCliente() {
        try {
            imprimirTitulo("HISTÓRICO DE CLIENTE");
            exibirMsgCancelar();

            String verLista = lerSimNaoCancelamento("Ver lista de clientes? (S/N): ");
            if (verLista.equalsIgnoreCase("S")) {
                listarClientes(empresa.getClientes());
            }

            int nif = lerInteiroComCancelamento("NIF do cliente: ");
            Cliente cliente = empresa.procurarCliente(nif);

            if (cliente == null) {
                imprimirErro("Cliente não encontrado.");
                return;
            }

            LocalDateTime inicio = lerDataComCancelamento("Data início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Data fim (dd-MM-yyyy HH:mm): ");

            ArrayList<Viagem> viagens = empresa.getViagensClientePorDatas(nif, inicio, fim);
            if (viagens.isEmpty()) {
                imprimirAviso("Nenhuma viagem registada nesse intervalo.");
            } else {
                imprimirTitulo("Histórico de " + cliente.getNome());
                listarViagens(viagens);
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    private static void pesquisarViaturaMatricula() {
        try {
            imprimirTitulo("PESQUISAR VIATURA POR MATRÍCULA");
            String verLista = lerSimNaoCancelamento("Deseja ver a lista de viaturas? (S/N): ");
            if (verLista.equalsIgnoreCase("S")) {
                listarViaturas(empresa.getViaturas());
            }
            String matricula = lerTextoComCancelamento("Matricula: ");
            Viatura viatura = empresa.procurarViatura(matricula);

            if (viatura == null) {
                imprimirAviso("Nenhuma viatura encontrada.");
            } else {
                imprimirTitulo("VIATURA ENCONTRADA");
                System.out.println("Matrícula: " + viatura.getMatricula());
                System.out.println("Marca: " + viatura.getMarca());
                System.out.println("Modelo: " + viatura.getModelo());
                System.out.println("Ano: " + viatura.getAnoFabrico());

                ArrayList<Viagem> viagens = empresa.getViagensPorViatura(matricula);

                if (!viagens.isEmpty()) {
                    System.out.println("Viagens Associadas: " + viagens.size());
                    for (Viagem viagem : viagens) {
                        System.out.println(" -> "+ viagem.toString());
                    }
                }
            }
        }catch (OperacaoCanceladaException e){
            imprimirErro("Operação cancelada.");
        }
    }

// =======================================================
//           SELEÇÃO DE RECURSOS DISPONÍVEIS
// =======================================================

    private static Condutor selecionarCondutorDisponivel(LocalDateTime inicio, LocalDateTime fim)
            throws OperacaoCanceladaException {

        ArrayList<Condutor> disponiveis = empresa.getCondutoresDisponiveis(inicio, fim);

        if (disponiveis.isEmpty()) {
            imprimirAviso("Nenhum condutor disponível neste horário.");
            return null;
        }

        System.out.println("\n>> Condutores disponíveis: " + disponiveis.size());
        String verLista = lerSimNaoCancelamento("Ver lista? (S/N): ");
        if (verLista.equalsIgnoreCase("S")) {
            listarCondutores(disponiveis);
        }

        while (true) {
            int numeroId = lerInteiroComCancelamento("Número de identificação do condutor: ");
            Condutor condutor = empresa.procurarCondutorPorId(numeroId);

            if (condutor == null) {
                imprimirErro("Condutor não encontrado.");
            } else {

                boolean encontrado = false;

                for (int i = 0; i < disponiveis.size(); i++) {
                    if (disponiveis.get(i).equals(condutor)) {
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    imprimirErro("Condutor não disponível neste horário.");
                } else {
                    return condutor;
                }
            }

        }
    }

    private static Cliente selecionarClienteDisponivel(LocalDateTime inicio, LocalDateTime fim)
            throws OperacaoCanceladaException {

        ArrayList<Cliente> disponiveis = empresa.getClientesDisponiveis(inicio, fim);

        if (disponiveis.isEmpty()) {
            imprimirAviso("Nenhum cliente disponível neste horário.");
            return null;
        }

        System.out.println("\n>> Clientes disponíveis: " + disponiveis.size());
        String verLista = lerSimNaoCancelamento("Ver lista? (S/N): ");
        if (verLista.equalsIgnoreCase("S")) {
            listarClientes(disponiveis);
        }

        while (true) {
            int nif = lerInteiroComCancelamento("NIF do cliente: ");
            Cliente cliente = empresa.procurarCliente(nif);

            if (cliente == null) {
                imprimirErro("Cliente não encontrado.");
            } else {

                boolean encontrado = false;

                for (int i = 0; i < disponiveis.size(); i++) {
                    if (disponiveis.get(i).equals(cliente)) {
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    imprimirErro("Cliente não disponível neste horário.");
                } else {
                    return cliente;
                }
            }
        }

    }

    private static Viatura selecionarViaturaDisponivel(LocalDateTime inicio, LocalDateTime fim)
            throws OperacaoCanceladaException {

        ArrayList<Viatura> disponiveis = empresa.getViaturasDisponiveis(inicio, fim);

        if (disponiveis.isEmpty()) {
            imprimirAviso("Nenhuma viatura disponível neste horário.");
            return null;
        }

        System.out.println("\n>> Viaturas disponíveis: " + disponiveis.size());
        String verLista = lerSimNaoCancelamento("Ver lista? (S/N): ");
        if (verLista.equalsIgnoreCase("S")) {
            listarViaturas(disponiveis);
        }

        while (true) {
            String matricula = lerTextoComCancelamento("Matrícula da viatura: ");
            Viatura viatura = empresa.procurarViatura(matricula);

            if (viatura == null) {
                imprimirErro("Viatura não encontrada.");
            } else {

                boolean encontrado = false;

                for (int i = 0; i < disponiveis.size(); i++) {
                    if (disponiveis.get(i).equals(viatura)) {
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    imprimirErro("Viatura não disponível neste horário.");
                } else {
                    return viatura;
                }
            }
        }
    }


// =======================================================
//           MÉTODOS AUXILIARES E GENÉRICOS
// =======================================================

    private static void listarViaturas(ArrayList<Viatura> viaturas) {
        if (viaturas.isEmpty()) {
            imprimirAviso("Nenhuma viatura encontrada.");
            return;
        }

        imprimirTitulo("VIATURAS");
        for (int i = 0; i < viaturas.size(); i++) {
            System.out.println((i + 1) + ". " + viaturas.get(i).toString());
        }
    }

    private static void listarClientes(ArrayList<Cliente> clientes) {
        if (clientes.isEmpty()) {
            imprimirAviso("Nenhum cliente encontrado.");
            return;
        }
        imprimirTitulo("CLIENTES");
        for (int i = 0; i < clientes.size(); i++) {
            System.out.println((i + 1) + ". " + clientes.get(i).toString());
        }
    }

    private static void listarCondutores(ArrayList<Condutor> condutores) {
        if (condutores.isEmpty()) {
            imprimirAviso("Nenhum condutor encontrado.");
            return;
        }
        imprimirTitulo("CONDUTORES");
        for (int i = 0; i < condutores.size(); i++) {
            System.out.println((i + 1) + ". " + condutores.get(i).toString());
        }
    }

    private static void listarViagens(ArrayList<Viagem> viagens) {
        if (viagens.isEmpty()) {
            imprimirAviso("Nenhuma Viagem encontrada.");
            return;
        }
        imprimirTitulo("VIAGENS");
        for (int i = 0; i < viagens.size(); i++) {
            System.out.println((i + 1) + ". " + viagens.get(i).toString());
        }
    }

    private static void listarReservas(ArrayList<Reserva> reservas) {
        if (reservas.isEmpty()) {
            imprimirAviso("Nenhuma reserva encontrada.");
            return;
        }
        imprimirTitulo("RESERVAS");
        for (int i = 0; i < reservas.size(); i++) {
            System.out.println((i + 1) + ". " + reservas.get(i).toString());
        }
    }

// =======================================================
//           MÉTODOS DE LEITURA DE INPUTS
// =======================================================

    private static int lerOpcaoMenu(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextInt()) {
            imprimirErro("Valor inválido. Tente novamente.");
            scanner.next();
            System.out.print(msg);
        }
        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
    }

    private static String lerTextoComCancelamento(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.print("Deseja cancelar a operação? (S/N): ");
                String confirmacao = scanner.nextLine().trim();
                if (confirmacao.equalsIgnoreCase("S")) {
                    throw new OperacaoCanceladaException();
                } else {
                    imprimirAviso("Operação anulada.");
                    continue;
                }
            }
            return input;
        }
    }

    private static String lerTextoOpcional(String msg) {
        System.out.print(msg);
        String input = scanner.nextLine();

        if (input.trim().isEmpty()) {
            return "";
        }
        return input.trim();
    }

    private static int lerInteiroComCancelamento(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Deseja cancelar a operação? (S/N): ");
                String confirmacao = scanner.nextLine().trim();
                if (confirmacao.equalsIgnoreCase("S")) {
                    throw new OperacaoCanceladaException();
                } else {
                    imprimirAviso("Operação anulada.");
                }
            }

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                imprimirErro("Insira um número inteiro válido.");
            }
        }
    }

    private static double lerDoubleComCancelamento(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Deseja cancelar a operação? (S/N): ");
                String confirmacao = scanner.nextLine().trim();
                if (confirmacao.equalsIgnoreCase("S")) {
                    throw new OperacaoCanceladaException();
                } else {
                    imprimirAviso("Operação anulada.");
                }
            }

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                imprimirErro("Número inválido. Tente novamente.");
            }
        }
    }

    private static LocalDateTime lerDataComCancelamento(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {

                System.out.println("Deseja cancelar a operação? (S/N): ");
                String confirmacao = scanner.nextLine().trim();
                if (confirmacao.equalsIgnoreCase("S")) {
                    throw new OperacaoCanceladaException();
                } else {
                    imprimirAviso("Operação anulada.");
                }
            }

            try {
                return LocalDateTime.parse(input, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                imprimirErro("Formato inválido. Use: dd-MM-yyyy HH:mm");
            }
        }
    }


    private static String lerSimNao(String msg) {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return "S";
            }
            if (input.equalsIgnoreCase("S") || input.equalsIgnoreCase("N")) {
                return input.toUpperCase();
            }
            System.out.println("Insira 'S' (Sim) ou 'N' (Não).");
        }
    }

    private static String lerSimNaoCancelamento(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Deseja cancelar a Operação? (S/N): ");
                String confirmacao = scanner.nextLine().trim();

                if (confirmacao.equalsIgnoreCase("S")) {
                    throw new OperacaoCanceladaException();
                } else {
                    continue;
                }
            }
            if (input.equalsIgnoreCase("S") || input.equalsIgnoreCase("N")) {
                return input.toUpperCase();
            } else if (input.isEmpty()) {
                return "S";
            } else {
                imprimirErro("Insira S (Sim) ou N (Não).");
            }
        }
    }

    private static int lerAnoValido() throws OperacaoCanceladaException {
        int anoAtual = 2026;
        int anoMinimo = 1886;

        while (true) {
            try {
                int ano = lerInteiroComCancelamento("Ano de Fabrico (" + anoMinimo + "-" + anoAtual + "): ");
                if (ano >= anoMinimo && ano <= anoAtual) {
                    return ano;
                } else {
                    imprimirErro("Ano inválido! Deve estar entre " + anoMinimo + " e " + anoAtual + ".");
                }
            } catch (NumberFormatException e) {
                imprimirErro("Insira um ano válido.");
            }
        }
    }

// =======================================================
//           ENCERRAR A APLICAÇÃO
// =======================================================

    private static void encerrarAplicacao(String nomeEmpresa) {
        try {
            imprimirCabecalho("SAIR DO SISTEMA - " + nomeEmpresa);
            String resposta = lerSimNaoCancelamento("Deseja gravar os dados antes de sair? (S/N): ");
            if (resposta.equalsIgnoreCase("S")) {
                empresa.gravarDados();
                imprimirAviso("Dados gravados com sucesso.");
            } else {
                imprimirAviso("As alterações não foram guardadas.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Saída forçada. As alterações não foram guardadas.");
        }
        imprimirAviso("Até logo!");
    }

// =======================================================
//           FORMATAÇÃO VISUAL
// =======================================================

    private static void imprimirCabecalho(String titulo) {
        int tamanhoFixo = 50;

        System.out.println();
        imprimirLinha();

        int espacos = tamanhoFixo - titulo.length();
        int esquerda = espacos / 2;
        int direita = espacos - esquerda;

        System.out.print("|");
        for (int i = 0; i < esquerda; i++) System.out.print(" ");
        System.out.print(titulo.toUpperCase());
        for (int i = 0; i < direita; i++) System.out.print(" ");
        System.out.println("|");

        imprimirLinha();
    }

    private static void imprimirLinha() {
        System.out.print("|");
        for (int i = 0; i < 50; i++) System.out.print("-");
        System.out.println("|");
    }

    private static void imprimirTitulo(String titulo) {
        System.out.println("\n--- " + titulo.toUpperCase() + " ---");
    }

    private static void exibirMsgCancelar() {
        System.out.println("(Prima 0 em qualquer momento para cancelar)");
    }

    private static void imprimirErro(String mensagem) {
        System.out.println(">> ERRO: " + mensagem);
    }

    private static void imprimirAviso(String mensagem) {
        System.out.println(">> " + mensagem);
    }

// =======================================================
//           DADOS DE TESTE
// =======================================================

    public static void inicializarDadosTeste() {
        try {
            // Viaturas
            int anoAtual = 2026;
            Viatura v1 = new Viatura("AA-00-AA", "Toyota", "Corolla", anoAtual - 3);
            Viatura v2 = new Viatura("BB-11-BB", "Tesla", "Model 3", anoAtual - 1);
            Viatura v3 = new Viatura("CC-22-CC", "Renault", "Clio", anoAtual - 5);

            empresa.adicionarViatura(v1);
            empresa.adicionarViatura(v2);
            empresa.adicionarViatura(v3);

            // Clientes
            Cliente c1 = new Cliente("João Silva", 100000001, 910000001, "Porto", 11111111);
            Cliente c2 = new Cliente("Ana Pereira", 100000002, 910000002, "Lisboa", 22222222);
            Cliente c3 = new Cliente("Carlos Santos", 100000003, 910000003, "Braga", 33333333);

            empresa.adicionarCliente(c1);
            empresa.adicionarCliente(c2);
            empresa.adicionarCliente(c3);

            // Condutores
            Condutor d1 = new Condutor(1001, "Maria Costa", 200000001, 920000001,
                    "Porto", 44444444, "C-001", 111111);
            Condutor d2 = new Condutor(1002, "Pedro Alves", 200000002, 920000002,
                    "Gaia", 55555555, "C-002", 222222);
            Condutor d3 = new Condutor(1003, "Luísa Mendes", 200000003, 920000003,
                    "Matosinhos", 66666666, "C-003", 333333);

            empresa.adicionarCondutor(d1);
            empresa.adicionarCondutor(d2);
            empresa.adicionarCondutor(d3);

            // Viagens de exemplo
            LocalDateTime ontem = LocalDateTime.now().minusDays(1);
            Viagem viagem1 = new Viagem(d1, c1, v1,
                    ontem.withHour(10).withMinute(0),
                    ontem.withHour(10).withMinute(30),
                    "Casa da Música", "Aeroporto", 15.5, 12.50);

            Viagem viagem2 = new Viagem(d2, c2, v2,
                    ontem.withHour(14).withMinute(0),
                    ontem.withHour(15).withMinute(0),
                    "Centro", "Shopping", 8.2, 9.80);

            empresa.adicionarViagem(viagem1);
            empresa.adicionarViagem(viagem2);

            // Reservas de exemplo
            Reserva r1 = new Reserva(c3, LocalDateTime.now().plusDays(1).withHour(9),
                    "Hotel", "Estação", 5.0);
            empresa.adicionarReserva(r1);

            imprimirAviso("Dados de teste carregados: 3 Viaturas, 3 Clientes, 3 Condutores, 2 Viagens, 1 Reserva");
        } catch (Exception e) {
            imprimirErro("Erro ao carregar dados de teste: " + e.getMessage());
        }
    }

// =======================================================
//           EXCEÇÃO PERSONALIZADA
// =======================================================

    private static class OperacaoCanceladaException extends Exception {
        public OperacaoCanceladaException() {
            super("Operação cancelada pelo utilizador");
        }
    }
}