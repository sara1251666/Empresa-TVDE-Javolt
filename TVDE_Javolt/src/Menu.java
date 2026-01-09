import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.ArrayList;

import Gestao.Empresa;
import Entidades.*;

/**
 * Classe responsável pela Interface com o Utilizador (UI) via consola.
 * <p>
 * Gere a navegação entre menus, a recolha de dados do utilizador
 * e a invocação dos métodos da classe {@link Empresa}.
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.4
 * @since 2026-01-03
 */
public class Menu {

    /**
     * Instância da Empresa. Não é inicializada logo para permitir escolha do nome.
     */
    private static Empresa empresa;

    /**
     * Objeto Scanner partilhado para leitura de inputs.
     */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Formatador de data padrão para o sistema (dd-MM-yyyy HH:mm).
     */
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");


    public static void iniciar() {
        imprimirCabecalho("SISTEMA DE GESTÃO TVDE");

        // 1. Selecionar empresa
        String nomeEmpresa = obterNomeEmpresa();
        if (nomeEmpresa == null || nomeEmpresa.isEmpty()) {
            System.out.println("Operação cancelada. A sair...");
            return;
        }

        // 2. Criar empresa
        empresa = new Empresa(nomeEmpresa);
        System.out.println("\nBem vindo à gestão da empresa: " + nomeEmpresa);

        // 3. Carregar dados iniciais
        carregarDadosIniciais(nomeEmpresa);

        // 4. Executar menu principal
        executarMenuPrincipal();  //

        // 5. Encerrar aplicação
        encerrarAplicacao(nomeEmpresa);
    }

    // =======================================================
    //           INICIALIZAÇÃO E SELEÇÃO DE EMPRESA
    // =======================================================

    /**
     * Obtém o nome da empresa (nova ou existente).
     * Permite voltar ao menu de seleção se o utilizador escolher "0".
     *
     * @return Nome da empresa selecionada/criada, ou null se cancelado completamente.
     */
    private static String obterNomeEmpresa() {
        ArrayList<String> empresas = listarEmpresasDetetadas();

        // Loop até obter um nome válido ou cancelar completamente
        while (true) {
            if (empresas.isEmpty()) {
                return criarEmpresaNova();
            }

            // Menu de seleção
            String empresaSelecionada = selecionarEmpresa(empresas);

            if (empresaSelecionada == null) {
                System.out.println("\n>> Nenhuma empresa selecionada.");
                System.out.print("Deseja sair da aplicação? (S/N): ");
                String resposta = scanner.nextLine();

                if (resposta.equalsIgnoreCase("S")) {
                    return null;
                }
            } else {
                return empresaSelecionada; // Empresa selecionada com sucesso
            }
        }
    }

    /**
     * Cria uma nova empresa.
     *
     * @return Nome da nova empresa.
     */
    private static String criarEmpresaNova() {
        try {
            imprimirTitulo("CRIAÇÃO DE NOVA EMPRESA");
            exibirMsgCancelar();
            return lerTextoComCancelamento("Nome da nova empresa: ");
        } catch (OperacaoCanceladaException e) {
            return null;
        }
    }

    /**
     * Permite selecionar empresa existente ou criar nova.
     *
     * @param empresasExistentes Lista de empresas detetadas.
     * @return Nome da empresa selecionada.
     */
    private static String selecionarEmpresa(ArrayList<String> empresasExistentes) {
        while (true) {
            imprimirCabecalho("SELEÇÃO DE EMPRESA");
            System.out.println("| 1 - Criar Nova Empresa                           |");
            System.out.println("| 2 - Carregar Empresa Existente                   |");
            imprimirLinha();

            int opcao = lerOpcaoMenu("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    return criarEmpresaNova();
                case 2: {
                       String empresaSelecionada = listarESelecionarEmpresa(empresasExistentes);
                       if (empresaSelecionada != null) {
                           return empresaSelecionada;
                       }
                       break;
                    }
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }


    private static String listarESelecionarEmpresa(ArrayList<String> empresas) {
            imprimirTitulo("EMPRESAS ENCONTRADAS");
            for (int i = 0; i < empresas.size(); i++) {
                System.out.println("  " + (i + 1) + " - " + empresas.get(i));
            }
            System.out.println("  0 - Voltar");

            int opcao = lerOpcaoMenu("Escolha a opção: ");

            if (opcao == 0) {
                return null;
            } else if (opcao > 0 && opcao <= empresas.size()) {
                return empresas.get(opcao - 1);
            } else {
                System.out.println("Opção inválida.");
                return null;
            }
        }


    /**
     * Pesquisa e lista empresas detetadas no sistema.
     *
     * @return Lista de nomes de empresas.
     */
    private static ArrayList<String> listarEmpresasDetetadas() {
        ArrayList<String> nomesEmpresas = new ArrayList<>();
        File pastaPrincipal = new File("Empresas");

        if (pastaPrincipal.exists() && pastaPrincipal.isDirectory()) {
            File[] ficheiros = pastaPrincipal.listFiles();

            if (ficheiros != null) {
                for (File ficheiro : ficheiros) {
                    if (ficheiro.isDirectory() && ficheiro.getName().startsWith("Logs_")) {
                        String nomeReal = ficheiro.getName().substring(5); // Remove "Logs_"
                        nomesEmpresas.add(nomeReal);
                    }
                }
            }
        }
        return nomesEmpresas;
    }

// =======================================================
//               CARREGAMENTO DE DADOS
// =======================================================

    /**
     * Carrega dados iniciais da empresa.
     *
     * @param nomeEmpresa Nome da empresa.
     */
    private static void carregarDadosIniciais(String nomeEmpresa) {
        File pastaEmpresa = new File("Empresas/Logs_" + nomeEmpresa);
        String[] conteudoPasta = pastaEmpresa.list();

        boolean temDados = pastaEmpresa.exists() &&
                pastaEmpresa.isDirectory() &&
                conteudoPasta != null &&
                conteudoPasta.length > 0;

        if (temDados) {
            tratarCarregamentoDadosExistente();
        } else {
            System.out.println("\n>> Primeira inicialização detetada (Sem histórico).");
        }

        // Verificar se precisa de dados de teste
        verificarDadosTeste();
    }


    private static void tratarCarregamentoDadosExistente() {
        try {
            System.out.println("\n>> ATENÇÃO: Foram encontrados registos anteriores!");
            String resposta = lerTextoComCancelamento("Deseja carregar os dados guardados? (S/N): ");

            if (resposta.equalsIgnoreCase("S")) {
                System.out.println("A carregar dados...");
                empresa.carregarDados();
            } else {
                mostrarAvisoPerdaDados();
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Seleção anulada. A carregar dados por segurança...");
            empresa.carregarDados();
        }
    }

    /**
     * Mostra aviso sobre possível perda de dados.
     */
    private static void mostrarAvisoPerdaDados() throws OperacaoCanceladaException {
        System.out.println("\n!!! PERIGO: DETETADA POSSÍVEL PERDA DE DADOS !!!");
        System.out.println("Se iniciar com a base de dados vazia e gravar no final,");
        System.out.println("o histórico anterior será APAGADO PERMANENTEMENTE.");

        String confirmacao = lerTextoComCancelamento("Tem a certeza que deseja continuar com a base de dados VAZIA? (S/N): ");

        if (confirmacao.equalsIgnoreCase("S")) {
            System.out.println("\n>> A iniciar sistema limpo...");
        } else {
            System.out.println("A carregar dados...");
            empresa.carregarDados();
        }
    }

    /**
     * Verifica se precisa de gerar dados de teste.
     */
    private static void verificarDadosTeste() {
        if (empresa.getViaturas().isEmpty() && empresa.getCondutores().isEmpty()) {
            try {
                System.out.println("\n>> A base de dados está vazia.");
                String resposta = lerTextoComCancelamento("Deseja gerar dados de teste? (S/N): ");
                if (resposta.equalsIgnoreCase("S")) {
                    inicializarDadosTeste();
                } else {
                    System.out.println("\n>> Sistema a iniciar com base de dados vazia.");
                }
            } catch (OperacaoCanceladaException e) {
                System.out.println("Opção ignorada. Sistema vazio.");
            }
        }
    }

// =======================================================
//               MENU PRINCIPAL
// =======================================================

    /**
     * Executa o menu principal em loop.
     */
    private static void executarMenuPrincipal() {
        int opcao;
        do {
            exibirMenuPrincipal();
            opcao = lerOpcaoMenu("Escolha uma opção: ");
            processarOpcaoPrincipal(opcao);
        } while (opcao != 0);
    }

    /**
     * Exibe o menu principal.
     */
    private static void exibirMenuPrincipal() {
        imprimirCabecalho("TVDE - MENU PRINCIPAL");
        System.out.println("| 1 - Gerir Viaturas                              |");
        System.out.println("| 2 - Gerir Condutores                            |");
        System.out.println("| 3 - Gerir Clientes                              |");
        System.out.println("| 4 - Gerir Viagens                               |");
        System.out.println("| 5 - Gerir Reservas                              |");
        System.out.println("| 6 - Relatórios/Estatísticas                     |");
        System.out.println("| 0 - Sair                                        |");
        imprimirLinha();
    }

    /**
     * Processa opção do menu principal.
     *
     * @param opcao Opção selecionada.
     */
    private static void processarOpcaoPrincipal(int opcao) {
        switch (opcao) {
            case 1 -> menuCRUD("Viaturas");
            case 2 -> menuCRUD("Condutores");
            case 3 -> menuCRUD("Clientes");
            case 4 -> menuViagens();
            case 5 -> menuReservas();
            case 6 -> menuEstatisticas();
            case 0 -> confirmarSaida();
            default -> {
                if (opcao != -1) {
                    System.out.println("Opção inválida.");
                }
            }
        }
    }

    /**
     * Confirma a saída da aplicação.
     */
    private static void confirmarSaida() {
        try {
            String resposta = lerTextoComCancelamento("Tem a certeza que deseja sair? (S/N): ");
            if (!resposta.equalsIgnoreCase("S")) {
                System.out.println("\n>> Saída Cancelada.");
                opcaoVoltar(); // Força continuar o loop
            } else {
                System.out.println("A preparar encerramento...");
            }
        } catch (OperacaoCanceladaException e) {
            opcaoVoltar(); // Cancela saída
        }
    }

    /**
     * Força opção de voltar (usado para cancelar saída).
     */
    private static void opcaoVoltar() {
        // Define uma opção inválida que não seja 0 para continuar o loop
        // O loop principal vai recomeçar
    }

// =======================================================
//               SUBMENUS ESPECÍFICOS
// =======================================================

    /**
     * Menu genérico CRUD.
     *
     * @param entidade Nome da entidade (Viaturas, Condutores, Clientes).
     */
    private static void menuCRUD(String entidade) {
        int opcao;
        do {
            exibirMenuCRUD(entidade);
            opcao = lerOpcaoMenu("Escolha a opção: ");
            processarOpcaoCRUD(entidade, opcao);
        } while (opcao != 0);
    }

    /**
     * Exibe menu CRUD.
     */
    private static void exibirMenuCRUD(String entidade) {
        imprimirCabecalho("GESTÃO DE " + entidade);
        System.out.println("|    1 - Criar (Create)                           |");
        System.out.println("|    2 - Listar (Read)                            |");
        System.out.println("|    3 - Atualizar (Update)                       |");
        System.out.println("|    4 - Apagar (Delete)                          |");
        System.out.println("|    0 - Voltar                                   |");
        imprimirLinha();
    }

    /**
     * Processa opção do menu CRUD.
     */
    private static void processarOpcaoCRUD(String entidade, int opcao) {
        switch (entidade) {
            case "Viaturas" -> processarOpcaoViaturas(opcao);
            case "Condutores" -> processarOpcaoCondutores(opcao);
            case "Clientes" -> processarOpcaoClientes(opcao);
        }
    }

    /**
     * Menu de viagens.
     */
    private static void menuViagens() {
        int opcao;
        do {
            exibirMenuViagens();
            opcao = lerOpcaoMenu("Escolha uma opção: ");
            processarOpcaoViagens(opcao);
        } while (opcao != 0);
    }

    /**
     * Exibe menu de viagens.
     */
    private static void exibirMenuViagens() {
        imprimirCabecalho("GESTÃO DE VIAGENS");
        System.out.println("| 1 - Registar Nova Viagem (Imediata)                    |");
        System.out.println("| 2 - Listar Histórico de Viagens                        |");
        System.out.println("| 3 - Apagar uma Viagem do Histórico    |");
        System.out.println("| 0 - Voltar                            |");
        imprimirLinha();
    }

    /**
     * Processa opção do menu de viagens.
     */
    private static void processarOpcaoViagens(int opcao) {
        switch (opcao) {
            case 1 -> tratarRegistarViagem();
            case 2 -> tratarListarViagens();
            case 3 -> tratarEliminarViagem();
        }
    }

    /**
     * Menu de reservas.
     */
    private static void menuReservas() {
        int opcao;
        do {
            exibirMenuReservas();
            opcao = lerOpcaoMenu("Escolha uma opção: ");
            processarOpcaoReservas(opcao);
        } while (opcao != 0);
    }

    /**
     * Exibe menu de reservas.
     */
    private static void exibirMenuReservas() {
        imprimirCabecalho("GESTÃO DE RESERVAS");
        System.out.println("| 1 - Criar Nova Reserva                |");
        System.out.println("| 2 - Listar Reservas Pendentes         |");
        System.out.println("| 3 - Consultar Reservas de um Cliente  |");
        System.out.println("| 4 - Alterar uma Reserva               |");
        System.out.println("| 5 - Converter Reserva em Viagem       |");
        System.out.println("| 6 - Cancelar/Apagar uma Reserva       |");
        System.out.println("| 0 - Voltar                            |");
        imprimirLinha();
    }

    /**
     * Processa opção do menu de reservas.
     */
    private static void processarOpcaoReservas(int opcao) {
        switch (opcao) {
            case 1 -> tratarCriarReserva();
            case 2 -> tratarListarReservas();
            case 3 -> tratarConsultarReservasCliente();
            case 4 -> tratarAlterarReserva();
            case 5 -> tratarConverterReserva();
            case 6 -> tratarEliminarReserva();
        }
    }

    /**
     * Menu de estatísticas.
     */
    private static void menuEstatisticas() {
        int opcao;
        do {
            exibirMenuEstatisticas();
            opcao = lerOpcaoMenu("Escolha uma opção: ");
            processarOpcaoEstatisticas(opcao);
        } while (opcao != 0);
    }

    /**
     * Exibe menu de estatísticas.
     */
    private static void exibirMenuEstatisticas() {
        imprimirCabecalho("RELATÓRIOS E ESTATÍSTICAS");
        System.out.println("| 1 - Faturação Cliente                  |");
        System.out.println("| 2 - Clientes por Viatura               |");
        System.out.println("| 3 - Top Destinos                       |");
        System.out.println("| 4 - Distância Média                    |");
        System.out.println("| 5 - Clientes por Kms                   |");
        System.out.println("| 6 - Histórico Clientes                 |");
        System.out.println("| 0 - Voltar                             |");
        imprimirLinha();
    }

    /**
     * Processa opção do menu de estatísticas.
     */
    private static void processarOpcaoEstatisticas(int opcao) {
        switch (opcao) {
            case 1 -> estatFaturacaoCondutor();
            case 2 -> estatClientesViatura();
            case 3 -> estatDestinoMaisSolicitado();
            case 4 -> estatDistanciaMedia();
            case 5 -> estatClientesPorIntervaloKms();
            case 6 -> estatHistoricoClientePorDatas();
        }
    }

// =======================================================
//               PROCESSAMENTO CRUD
// =======================================================

    /**
     * Processa opções CRUD para Viaturas.
     */
    private static void processarOpcaoViaturas(int opcao) {
        try {
            switch (opcao) {
                case 1 -> criarViatura();
                case 2 -> listarViaturas();
                case 3 -> atualizarViatura();
                case 4 -> eliminarViatura();
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }


    /**
     * Cria uma nova viatura.
     */
    private static void criarViatura() throws OperacaoCanceladaException {
        exibirMsgCancelar();

        String matricula = lerMatriculaUnica();
        String marca = lerTextoComCancelamento("Marca: ");
        String modelo = lerTextoComCancelamento("Modelo: ");
        int ano = lerInteiroComCancelamento("Ano de Fabrico: ");

        Viatura viatura = new Viatura(matricula, marca, modelo, ano);
        if (empresa.adicionarViatura(viatura)) {
            System.out.println("Viatura adicionada com sucesso!");
        } else {
            System.out.println("Erro ao adicionar viatura.");
        }
    }

    /**
     * Lê matrícula garantindo que é única.
     */
    private static String lerMatriculaUnica() throws OperacaoCanceladaException {
        String matricula;
        do {
            matricula = lerTextoComCancelamento("Matrícula: ");
            if (empresa.procurarViatura(matricula) != null) {
                System.out.println("Erro: Viatura com essa matrícula já existente.");
            }
        } while (empresa.procurarViatura(matricula) != null);
        return matricula;
    }

    /**
     * Lista todas as viaturas.
     */
    private static void listarViaturas() {
        if (empresa.getViaturas().isEmpty()) {
            System.out.println("Nenhuma viatura registada.");
        } else {
            imprimirTitulo("\nLista de Viaturas");
            int contador = 1;
            for (Viatura v : empresa.getViaturas()) {
                System.out.println(contador + ". " + v.toString());
                contador++;
            }
        }
    }

    /**
     * Atualiza uma viatura existente.
     */
    private static void atualizarViatura() throws OperacaoCanceladaException {
        String matricula = lerTextoComCancelamento("Insira a matrícula da viatura a editar: ");
        Viatura viatura = empresa.procurarViatura(matricula);

        if (viatura != null) {
            System.out.println("Dados atuais: " + viatura);
            viatura.setMarca(lerTextoComCancelamento("Nova Marca: "));
            viatura.setModelo(lerTextoComCancelamento("Novo Modelo: "));
            viatura.setAnoFabrico(lerInteiroComCancelamento("Novo Ano: "));
            System.out.println("Viatura atualizada.");
        } else {
            System.out.println("Viatura não encontrada.");
        }
    }

    /**
     * Elimina uma viatura.
     */
    private static void eliminarViatura() throws OperacaoCanceladaException {
        String matricula = lerTextoComCancelamento("Matrícula a eliminar: ");
        if (empresa.removerViatura(matricula)) {
            System.out.println("Viatura removida.");
        } else {
            System.out.println("Erro: Viatura não existe ou tem viagens associadas.");
        }
    }

    /**
     * Processa opções CRUD para Condutores.
     */
    private static void processarOpcaoCondutores(int opcao) {
        try {
            switch (opcao) {
                case 1 -> criarCondutor();
                case 2 -> listarCondutores();
                case 3 -> atualizarCondutor();
                case 4 -> eliminarCondutor();
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }


    /**
     * Cria um novo condutor.
     */
    private static void criarCondutor() throws OperacaoCanceladaException {
        exibirMsgCancelar();

        int nif = lerNifUnico("Condutor");
        String nome = lerTextoComCancelamento("Nome: ");
        int telefone = lerInteiroComCancelamento("Telemóvel: ");
        String morada = lerTextoComCancelamento("Morada: ");
        int cartaoCidadao = lerInteiroComCancelamento("Cartão Cidadão: ");
        String cartaConducao = lerTextoComCancelamento("Carta Condução: ");
        int segurancaSocial = lerInteiroComCancelamento("Segurança Social: ");

        try {
            Condutor condutor = new Condutor(nome, nif, telefone, morada, cartaoCidadao, cartaConducao, segurancaSocial);
            if (empresa.adicionarCondutor(condutor)) {
                System.out.println("Condutor registado com sucesso!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nErro de validação nos dados: " + e.getMessage());
        }
    }

    /**
     * Lista todos os condutores.
     */
    private static void listarCondutores() {
        ArrayList<Condutor> lista = empresa.getCondutores();
        if (lista.isEmpty()) {
            System.out.println("Não há condutores registados.");
        } else {
            imprimirTitulo("\nLista de Condutores");
            int contador = 1;
            for (Condutor c : lista) {
                System.out.println(contador + ". " + c.toString());
                contador++;
            }
        }
    }

    /**
     * Atualiza um condutor existente.
     */
    private static void atualizarCondutor() throws OperacaoCanceladaException {
        int nif = lerInteiroComCancelamento("NIF do condutor a editar: ");
        Condutor condutor = empresa.procurarCondutor(nif);

        if (condutor != null) {
            condutor.setNome(lerTextoComCancelamento("Novo Nome: "));
            condutor.setTel(lerInteiroComCancelamento("Novo Telemóvel: "));
            condutor.setMorada(lerTextoComCancelamento("Nova Morada: "));
            System.out.println("Condutor atualizado.");
        } else {
            System.out.println("Condutor não encontrado.");
        }
    }

    /**
     * Elimina um condutor.
     */
    private static void eliminarCondutor() throws OperacaoCanceladaException {
        int nif = lerInteiroComCancelamento("NIF a eliminar: ");
        if (empresa.removerCondutor(nif)) {
            System.out.println("Condutor removido.");
        } else {
            System.out.println("Erro: Não pode remover condutor com histórico de viagens.");
        }
    }

    /**
     * Processa opções CRUD para Clientes.
     */
    private static void processarOpcaoClientes(int opcao) {
        try {
            switch (opcao) {
                case 1 -> criarCliente();
                case 2 -> listarClientes();
                case 3 -> atualizarCliente();
                case 4 -> eliminarCliente();
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Cria um novo cliente.
     */
    private static void criarCliente() throws OperacaoCanceladaException {
        exibirMsgCancelar();

        int nif = lerNifUnico("Cliente");
        String nome = lerTextoComCancelamento("Nome: ");
        int telefone = lerInteiroComCancelamento("Telemóvel: ");
        String morada = lerTextoComCancelamento("Morada: ");
        int cartaoCidadao = lerInteiroComCancelamento("Cartão Cidadão: ");

        try {
            Cliente cliente = new Cliente(nome, nif, telefone, morada, cartaoCidadao);
            if (empresa.adicionarCliente(cliente)) {
                System.out.println("Sucesso: Cliente registado!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao criar cliente: " + e.getMessage());
        }
    }

    /**
     * Lista todos os clientes.
     */
    private static void listarClientes() {
        ArrayList<Cliente> lista = empresa.getClientes();
        if (lista.isEmpty()) {
            imprimirAviso("Não há clientes registados.");
        } else {
            imprimirTitulo("\nLista de Clientes");
            int contador = 1;
            for (Cliente c : lista) {
                System.out.println(contador + ". " + c.toString());
                contador++;
            }
        }
    }

    /**
     * Atualiza um cliente existente.
     */
    private static void atualizarCliente() throws OperacaoCanceladaException {
        int nif = lerInteiroComCancelamento("NIF do cliente a editar: ");
        Cliente cliente = empresa.procurarCliente(nif);

        if (cliente != null) {
            cliente.setNome(lerTextoComCancelamento("Novo Nome: "));
            cliente.setTel(lerInteiroComCancelamento("Novo Telemóvel: "));
            cliente.setMorada(lerTextoComCancelamento("Nova Morada: "));
            System.out.println("Cliente atualizado.");
        } else {
            System.out.println("Cliente não encontrado.");
        }
    }

    /**
     * Elimina um cliente.
     */
    private static void eliminarCliente() throws OperacaoCanceladaException {
        ArrayList<Cliente> lista = empresa.getClientes();
        if (lista.isEmpty()) {
            System.out.println("Não existem clientes registados para eliminar.");
            return;
        }

        exibirMsgCancelar();
        imprimirTitulo("Escolha o Cliente a Eliminar");
        for (Cliente cliente : lista) {
            System.out.println("[" + cliente.getNome() + " | Nif: " + cliente.getNif() + "]");
        }

        int nif = lerInteiroComCancelamento("NIF a eliminar: ");
        if (empresa.removerCliente(nif)) {
            System.out.println("Cliente removido com sucesso.");
        } else {
            System.out.println("Erro: Não pode remover cliente com histórico ou reservas.");
        }
    }

    /**
     * Lê NIF garantindo que é único.
     *
     * @param tipo "Cliente" ou "Condutor".
     */
    private static int lerNifUnico(String tipo) throws OperacaoCanceladaException {
        int nif;
        while (true) {
            nif = lerInteiroComCancelamento("NIF (9 dígitos): ");

            if (String.valueOf(nif).length() != 9) {
                System.out.println("Erro: O NIF tem que ser exatamente 9 dígitos.");
                continue;
            }

            boolean existe = false;
            if (tipo.equals("Cliente")) {
                existe = empresa.procurarCliente(nif) != null;
            } else if (tipo.equals("Condutor")) {
                existe = empresa.procurarCondutor(nif) != null;
            }

            if (existe) {
                System.out.println("Erro: Já existe um " + tipo + " com esse NIF.");
                continue;
            }

            break;
        }
        return nif;
    }

// =======================================================
//                  MÉTODOS DE VIAGENS
// =======================================================

    /**
     * Regista uma nova viagem.
     */
    private static void tratarRegistarViagem() {
        try {
            imprimirTitulo("NOVA VIAGEM");
            exibirMsgCancelar();

            // 1. Datas
            LocalDateTime inicio = lerDataComCancelamento("Início da Viagem (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Fim da Viagem (dd-MM-yyyy HH:mm): ");

            if (fim.isBefore(inicio)) {
                System.out.println("Erro: Data de fim anterior a data de início.");
                return;
            }

            // 2. Selecionar recursos
            Condutor condutor = selecionarCondutorDisponivel(inicio, fim);
            if (condutor == null) return;

            Cliente cliente = selecionarClienteDisponivel(inicio, fim);
            if (cliente == null) return;

            Viatura viatura = selecionarViaturaDisponivel(inicio, fim);
            if (viatura == null) return;

            // 3. Dados restantes
            String origem = lerTextoComCancelamento("Origem: ");
            String destino = lerTextoComCancelamento("Destino: ");
            double kms = lerDoubleComCancelamento("Kms: ");
            double custo = lerDoubleComCancelamento("Custo: ");

            Viagem novaViagem = new Viagem(condutor, cliente, viatura, inicio, fim, origem, destino, kms, custo);

            if (empresa.adicionarViagem(novaViagem)) {
                System.out.println("Viagem adicionada com sucesso.");
            } else {
                System.out.println("Erro: Conflito de horário detetado.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Lista todas as viagens.
     */
    private static void tratarListarViagens() {
        ArrayList<Viagem> viagens = empresa.getViagens();
        if (viagens.isEmpty()) {
            System.out.println("Sem viagens registadas!");
        } else {
            imprimirTitulo("\nHistórico de Viagens");
            for (Viagem viagem : viagens) {
                System.out.println(viagem);
            }
        }
    }

    /**
     * Elimina uma viagem.
     */
    private static void tratarEliminarViagem() {
        ArrayList<Viagem> viagens = empresa.getViagens();
        if (viagens.isEmpty()) {
            System.out.println("Não existem viagens no histórico para eliminar.");
            return;
        }

        try {
            imprimirTitulo("Escolha a viagem a eliminar");
            exibirMsgCancelar();

            for (int i = 0; i < viagens.size(); i++) {
                System.out.println((i + 1) + ". " + viagens.get(i));
            }

            int index = lerInteiroComCancelamento("\nNúmero da viagem a apagar: ") - 1;
            if (index >= 0 && index < viagens.size()) {
                Viagem viagem = viagens.get(index);
                if (empresa.removerViagens(viagem)) {
                    System.out.println("Viagem removida com sucesso!");
                } else {
                    System.out.println("Erro ao remover Viagem.");
                }
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação Cancelada.");
        }
    }

// =======================================================
//                  MÉTODOS DE RESERVAS
// =======================================================

    /**
     * Cria uma nova reserva.
     */
    private static void tratarCriarReserva() {
        try {
            imprimirTitulo("NOVA RESERVA");
            exibirMsgCancelar();

            LocalDateTime inicio = lerDataComCancelamento("Data/Hora da Reserva (dd-MM-yyyy HH:mm): ");
            LocalDateTime fimEstimado = inicio.plusHours(1); // Duração virtual

            Cliente cliente = selecionarClienteDisponivel(inicio, fimEstimado);
            if (cliente == null) return;

            String origem = lerTextoComCancelamento("Origem: ");
            String destino = lerTextoComCancelamento("Destino: ");
            double kms = lerDoubleComCancelamento("Kms estimados: ");

            Reserva reserva = new Reserva(cliente, inicio, origem, destino, kms);
            empresa.adicionarReserva(reserva);
            System.out.println("Reserva registada com sucesso!");
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Lista todas as reservas.
     */
    private static void tratarListarReservas() {
        ArrayList<Reserva> reservas = empresa.getReservas();
        if (reservas.isEmpty()) {
            System.out.println("Sem nenhuma reserva pendente!");
        } else {
            int contador = 1;
            for (Reserva reserva : reservas) {
                System.out.println(contador + ". " + reserva);
                contador++;
            }
        }
    }

    /**
     * Consulta reservas de um cliente.
     */
    private static void tratarConsultarReservasCliente() {
        try {
            exibirMsgCancelar();
            int nif = lerInteiroComCancelamento("NIF do Cliente: ");
            Cliente cliente = empresa.procurarCliente(nif);

            if (cliente != null) {
                ArrayList<Reserva> reservas = empresa.getReservasDoCliente(nif);
                if (reservas.isEmpty()) {
                    System.out.println("Este cliente não tem reservas pendentes.");
                } else {
                    imprimirTitulo("Reservas de " + cliente.getNome());
                    for (Reserva reserva : reservas) {
                        System.out.println(reserva);
                    }
                }
            } else {
                System.out.println("Cliente não encontrado.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Altera uma reserva existente.
     */
    private static void tratarAlterarReserva() {
        try {
            imprimirTitulo("ALTERAR RESERVA");
            exibirMsgCancelar();

            // 1. Selecionar cliente
            int nifCliente = lerInteiroComCancelamento("NIF Cliente: ");
            Cliente cliente = empresa.procurarCliente(nifCliente);

            if (cliente == null) {
                System.out.println("Cliente não encontrado.");
                return;
            }

            // 2. Selecionar reserva
            Reserva reserva = selecionarReservaCliente(nifCliente);
            if (reserva == null) return;

            // 3. Menu de alteração
            exibirMenuAlteracaoReserva();
            int opcao = lerOpcaoMenu("Escolha a opção: ");
            processarAlteracaoReserva(reserva, opcao);

        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Seleciona reserva de um cliente.
     */
    private static Reserva selecionarReservaCliente(int nifCliente) throws OperacaoCanceladaException {
        ArrayList<Reserva> reservasCliente = empresa.getReservasDoCliente(nifCliente);

        if (reservasCliente.isEmpty()) {
            System.out.println("Este cliente não tem reservas pendentes para alterar.");
            return null;
        }

        imprimirTitulo("Reservas de " + empresa.procurarCliente(nifCliente).getNome());
        for (int i = 0; i < reservasCliente.size(); i++) {
            System.out.println((i + 1) + ". " + reservasCliente.get(i));
        }

        int index = lerInteiroComCancelamento("Escolha a Reserva a Alterar: ") - 1;
        if (index >= 0 && index < reservasCliente.size()) {
            return reservasCliente.get(index);
        } else {
            System.out.println("Opção inválida.");
            return null;
        }
    }

    /**
     * Exibe menu de alteração de reserva.
     */
    private static void exibirMenuAlteracaoReserva() {
        imprimirTitulo("Dados a alterar");
        System.out.println("1 - Data e Hora");
        System.out.println("2 - Origem");
        System.out.println("3 - Destino");
        System.out.println("4 - Distância (Kms)");
        System.out.println("0 - Cancelar");
    }

    /**
     * Processa alteração de reserva.
     */
    private static void processarAlteracaoReserva(Reserva reserva, int opcao) throws OperacaoCanceladaException {
        switch (opcao) {
            case 1 -> {
                LocalDateTime novaData = lerDataComCancelamento("Nova Data/Hora (dd-MM-yyyy HH:mm): ");
                reserva.setDataHoraInicio(novaData);
                System.out.println("Data atualizada com sucesso!");
            }
            case 2 -> {
                String novaOrigem = lerTextoComCancelamento("Nova Origem: ");
                reserva.setMoradaOrigem(novaOrigem);
                System.out.println("Morada origem atualizada com sucesso!");
            }
            case 3 -> {
                String novoDestino = lerTextoComCancelamento("Novo Destino: ");
                reserva.setMoradaDestino(novoDestino);
                System.out.println("Morada destino atualizada com sucesso!");
            }
            case 4 -> {
                double novosKms = lerDoubleComCancelamento("Novos Kms: ");
                reserva.setKms(novosKms);
                System.out.println("Distância atualizada com sucesso!");
            }
            case 0 -> System.out.println("Alteração cancelada.");
            default -> System.out.println("Opção Inválida.");
        }
    }

    /**
     * Converte reserva em viagem.
     */
    private static void tratarConverterReserva() {
        try {
            ArrayList<Reserva> reservas = empresa.getReservas();
            if (reservas.isEmpty()) {
                System.out.println("Sem nenhuma Reserva para converter em Viagem");
                return;
            }

            // 1. Selecionar reserva
            imprimirTitulo("Reservas a converter");
            for (int i = 0; i < reservas.size(); i++) {
                System.out.println((i + 1) + ". " + reservas.get(i));
            }

            int index = lerInteiroComCancelamento("Número da reserva: ") - 1;
            if (index < 0 || index >= reservas.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            Reserva reserva = reservas.get(index);
            System.out.println("Reserva Selecionada: " + reserva.getCliente().getNome() + " - " +
                    reserva.getDataHoraInicio().format(dateTimeFormatter));

            // 2. Duração estimada
            int duracao = lerInteiroComCancelamento("Duração estimada (minutos): ");
            LocalDateTime fimEstimado = reserva.getDataHoraInicio().plusMinutes(duracao);

            // 3. Selecionar recursos
            Condutor condutor = selecionarCondutorDisponivel(reserva.getDataHoraInicio(), fimEstimado);
            if (condutor == null) return;

            Viatura viatura = selecionarViaturaDisponivel(reserva.getDataHoraInicio(), fimEstimado);
            if (viatura == null) return;

            // 4. Custo final
            double custo = lerDoubleComCancelamento("Custo Final (€): ");

            if (empresa.converterReservaEmViagem(reserva, condutor, viatura, custo)) {
                System.out.println("Reserva convertida com sucesso!");
            } else {
                System.out.println("Erro ao converter Reserva.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Elimina uma reserva.
     */
    private static void tratarEliminarReserva() {
        try {
            ArrayList<Reserva> reservas = empresa.getReservas();
            if (reservas.isEmpty()) {
                System.out.println("Não existem reservas para eliminar.");
                return;
            }

            imprimirTitulo("Reservas a eliminar");
            for (int i = 0; i < reservas.size(); i++) {
                System.out.println((i + 1) + ". " + reservas.get(i));
            }

            exibirMsgCancelar();
            int index = lerInteiroComCancelamento("\nNúmero da reserva a apagar: ") - 1;

            if (index >= 0 && index < reservas.size()) {
                Reserva reserva = reservas.get(index);
                if (empresa.removerReserva(reserva)) {
                    System.out.println("Reserva removida com sucesso!");
                } else {
                    System.out.println("Erro ao remover Reserva.");
                }
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

// =======================================================
//           SELEÇÃO DE RECURSOS DISPONÍVEIS
// =======================================================

    /**
     * Seleciona condutor disponível.
     */
    private static Condutor selecionarCondutorDisponivel(LocalDateTime inicio, LocalDateTime fim)
            throws OperacaoCanceladaException {

        ArrayList<Condutor> condutoresLivres = empresa.getCondutoresDisponiveis(inicio, fim);

        if (condutoresLivres.isEmpty()) {
            System.out.println("Sem condutores disponíveis para este horário.");
            return null;
        }

        mostrarListaCondutores(condutoresLivres);
        return pedirCondutorValido(condutoresLivres);
    }

    /**
     * Mostra lista de condutores.
     */
    private static void mostrarListaCondutores(ArrayList<Condutor> condutores)
            throws OperacaoCanceladaException {

        System.out.println("\n>> Existem " + condutores.size() + " condutores livres.");
        String verLista = lerTextoComCancelamento("Ver lista? (S/N): ");

        if (verLista.equalsIgnoreCase("S")) {
            for (Condutor condutor : condutores) {
                System.out.println("[ Condutor: " + condutor.getNome() + " | NIF: " + condutor.getNif() + " ]");
            }
        }
    }

    /**
     * Pede condutor válido.
     */
    private static Condutor pedirCondutorValido(ArrayList<Condutor> condutoresLivres)
            throws OperacaoCanceladaException {

        while (true) {
            int nifCondutor = lerInteiroComCancelamento("NIF do Condutor: ");
            Condutor condutor = empresa.procurarCondutor(nifCondutor);

            if (condutor == null) {
                System.out.println("Erro: Nenhum condutor encontrado.");
            } else if (!condutoresLivres.contains(condutor)) {
                System.out.println("Erro: Condutor " + condutor.getNome() + " já tem uma viagem nesse horário.");
            } else {
                return condutor;
            }
        }
    }

    /**
     * Seleciona cliente disponível.
     */
    private static Cliente selecionarClienteDisponivel(LocalDateTime inicio, LocalDateTime fim)
            throws OperacaoCanceladaException {

        ArrayList<Cliente> clientesLivres = empresa.getClientesDisponiveis(inicio, fim);

        if (clientesLivres.isEmpty()) {
            System.out.println("Sem clientes disponíveis.");
            return null;
        }

        mostrarListaClientes(clientesLivres);
        return pedirClienteValido(clientesLivres);
    }


    /**
     * Mostra lista de clientes.
     */
    private static void mostrarListaClientes(ArrayList<Cliente> clientes)
            throws OperacaoCanceladaException {

        System.out.println("\n>> Existem " + clientes.size() + " clientes livres.");
        String verLista = lerTextoComCancelamento("Ver lista? (S/N): ");

        if (verLista.equalsIgnoreCase("S")) {
            for (Cliente cliente : clientes) {
                System.out.println("[ Cliente: " + cliente.getNome() + " | NIF: " + cliente.getNif() + " ]");
            }
        }
    }

    /**
     * Pede cliente válido.
     */
    private static Cliente pedirClienteValido(ArrayList<Cliente> clientesLivres)
            throws OperacaoCanceladaException {

        while (true) {
            int nifCliente = lerInteiroComCancelamento("NIF do Cliente: ");
            Cliente cliente = empresa.procurarCliente(nifCliente);

            if (cliente == null) {
                System.out.println("Erro: Nenhum cliente encontrado.");
            } else if (!clientesLivres.contains(cliente)) {
                System.out.println("Erro: " + cliente.getNome() + " já tem uma viagem nesse horário.");
            } else {
                return cliente;
            }
        }
    }

    /**
     * Seleciona viatura disponível.
     */
    private static Viatura selecionarViaturaDisponivel(LocalDateTime inicio, LocalDateTime fim)
            throws OperacaoCanceladaException {

        ArrayList<Viatura> viaturasLivres = empresa.getViaturasDisponiveis(inicio, fim);

        if (viaturasLivres.isEmpty()) {
            System.out.println("Sem viaturas disponíveis para este horário.");
            return null;
        }

        mostrarListaViaturas(viaturasLivres);
        return pedirViaturaValida(viaturasLivres);
    }

    /**
     * Mostra lista de viaturas.
     */
    private static void mostrarListaViaturas(ArrayList<Viatura> viaturas)
            throws OperacaoCanceladaException {

        System.out.println("\n>> Existem " + viaturas.size() + " viaturas livres.");
        String verLista = lerTextoComCancelamento("Ver lista? (S/N): ");

        if (verLista.equalsIgnoreCase("S")) {
            for (Viatura viatura : viaturas) {
                System.out.println("[ Matrícula: " + viatura.getMatricula() +
                        " | Marca: " + viatura.getMarca() +
                        " | Modelo: " + viatura.getModelo() + " ]");
            }
        }
    }

    /**
     * Pede viatura válida.
     */
    private static Viatura pedirViaturaValida(ArrayList<Viatura> viaturasLivres)
            throws OperacaoCanceladaException {

        while (true) {
            String matricula = lerTextoComCancelamento("Matrícula da Viatura: ");
            Viatura viatura = empresa.procurarViatura(matricula);

            if (viatura == null) {
                System.out.println("Erro: Nenhuma viatura encontrada.");
            } else if (!viaturasLivres.contains(viatura)) {
                System.out.println("Erro: A viatura (Matrícula: " + viatura.getMatricula() +
                        ") já tem uma viagem nesse horário.");
            } else {
                return viatura;
            }
        }
    }

// =======================================================
//                       ESTATÍSTICAS
// =======================================================

    /**
     * Faturação de condutor.
     */
    private static void estatFaturacaoCondutor() {
        try {
            exibirMsgCancelar();

            // Mostrar lista se solicitado
            String verLista = lerTextoComCancelamento("Deseja ver a lista de condutores? (S/N): ");
            if (verLista.equalsIgnoreCase("S")) {
                mostrarListaCondutoresCompleta();
            }

            // Selecionar condutor
            int nif = lerInteiroComCancelamento("NIF do Condutor a consultar: ");
            Condutor condutor = empresa.procurarCondutor(nif);

            if (condutor != null) {
                LocalDateTime inicio = lerDataComCancelamento("Data início (dd-MM-yyyy HH:mm): ");
                LocalDateTime fim = lerDataComCancelamento("Data fim (dd-MM-yyyy HH:mm): ");

                double total = empresa.calcularFaturacaoCondutor(nif, inicio, fim);
                System.out.println("O Condutor " + condutor.getNome() + " faturou: " + total + " € nesse período.");
            } else {
                System.out.println("Condutor não encontrado.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Mostra lista completa de condutores.
     */
    private static void mostrarListaCondutoresCompleta() {
        ArrayList<Condutor> condutores = empresa.getCondutores();
        if (condutores.isEmpty()) {
            System.out.println("\n>> Não existem condutores registados.");
            return;
        }
        imprimirTitulo("Condutores Registados");
        for (Condutor condutor : condutores) {
            System.out.println("- " + condutor.getNome() + " | NIF: " + condutor.getNif());
        }
    }

    /**
     * Clientes por viatura.
     */
    private static void estatClientesViatura() {
        try {
            exibirMsgCancelar();

            // Mostrar lista se solicitado
            String verLista = lerTextoComCancelamento("\nDeseja ver a lista de Viaturas? (S/N): ");
            if (verLista.equalsIgnoreCase("S")) {
                mostrarListaViaturasCompleta();
            }

            // Selecionar viatura
            String matricula = lerTextoComCancelamento("\nMatrícula da Viatura: ");
            Viatura viatura = empresa.procurarViatura(matricula);

            if (viatura != null) {
                ArrayList<Cliente> clientes = empresa.getClientesPorViatura(matricula);
                if (clientes.isEmpty()) {
                    System.out.println("Esta viatura ainda não transportou clientes.");
                } else {
                    imprimirTitulo("Clientes da viatura: " + viatura.getMatricula());
                    for (Cliente cliente : clientes) {
                        System.out.println("- " + cliente.getNome());
                    }
                }
            } else {
                System.out.println("Viatura não encontrada.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Mostra lista completa de viaturas.
     */
    private static void mostrarListaViaturasCompleta() {
        ArrayList<Viatura> viaturas = empresa.getViaturas();
        if (viaturas.isEmpty()) {
            System.out.println("\n Não existem viaturas registadas.");
            return;
        }
        imprimirTitulo("Viaturas Registadas");
        for (Viatura viatura : viaturas) {
            System.out.println("- " + viatura.getMarca() + " " + viatura.getModelo() +
                    " | Matrícula: " + viatura.getMatricula());
        }
    }

    /**
     * Destino mais solicitado.
     */
    private static void estatDestinoMaisSolicitado() {
        try {
            LocalDateTime inicio = lerDataComCancelamento("Data início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Data fim (dd-MM-yyyy HH:mm): ");

            String topDestino = empresa.getDestinoMaisSolicitado(inicio, fim);
            System.out.println("O destino mais popular nesse período é: " + topDestino);
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Distância média.
     */
    private static void estatDistanciaMedia() {
        try {
            exibirMsgCancelar();
            LocalDateTime inicio = lerDataComCancelamento("Data início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Data fim (dd-MM-yyyy HH:mm): ");

            double media = empresa.calcularDistanciaMedia(inicio, fim);
            System.out.println("A distância média das viagens foi: " +
                    String.format("%.2f", media) + " Kms");
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Clientes por intervalo de Kms.
     */
    private static void estatClientesPorIntervaloKms() {
        try {
            exibirMsgCancelar();
            double minimo = lerDoubleComCancelamento("\nKms Mínimos: ");
            double maximo = lerDoubleComCancelamento("Kms Máximos: ");

            ArrayList<Cliente> clientes = empresa.getClientesPorIntervaloKms(minimo, maximo);
            if (clientes.isEmpty()) {
                System.out.println("Nenhum cliente encontrado nesse intervalo");
            } else {
                imprimirTitulo("Clientes com viagens entre " + minimo + " e " + maximo + " Kms");
                for (Cliente cliente : clientes) {
                    double totalKms = empresa.calcularTotalKmsCliente(cliente.getNif());
                    System.out.println("[" + cliente.getNome() + " | Nif: " +
                            cliente.getNif() + " | Total: " + totalKms + " Kms]");
                }
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Histórico de cliente por datas.
     */
    private static void estatHistoricoClientePorDatas() {
        try {
            exibirMsgCancelar();

            // Mostrar lista se solicitado
            String verLista = lerTextoComCancelamento("\nDeseja ver a lista de Clientes? (S/N): ");
            if (verLista.equalsIgnoreCase("S")) {
                mostrarListaClientesCompleta();
            }

            // Selecionar cliente
            int nif = lerInteiroComCancelamento("NIF do Cliente: ");
            Cliente cliente = empresa.procurarCliente(nif);

            if (cliente != null) {
                LocalDateTime inicio = lerDataComCancelamento("Data início (dd-MM-yyyy HH:mm): ");
                LocalDateTime fim = lerDataComCancelamento("Data fim (dd-MM-yyyy HH:mm): ");

                ArrayList<Viagem> viagens = empresa.getViagensClientePorDatas(nif, inicio, fim);
                if (viagens.isEmpty()) {
                    System.out.println("Nenhuma viagem registada nesse intervalo");
                } else {
                    imprimirTitulo("Histórico de " + cliente.getNome());
                    for (Viagem viagem : viagens) {
                        System.out.println(viagem);
                    }
                }
            } else {
                System.out.println("Erro: Nenhum cliente encontrado com esse NIF.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operação cancelada.");
        }
    }

    /**
     * Mostra lista completa de clientes.
     */
    private static void mostrarListaClientesCompleta() {
        ArrayList<Cliente> clientes = empresa.getClientes();
        if (clientes.isEmpty()) {
            System.out.println("\n>> Não existem clientes registados.");
            return;
        }
        imprimirTitulo("Clientes Registados");
        for (Cliente cliente : clientes) {
            System.out.println("- " + cliente.getNome() + " | NIF: " + cliente.getNif());
        }
    }

// =======================================================
//           ENCERRAMENTO DA APLICAÇÃO
// =======================================================

    /**
     * Encerra a aplicação com gravação opcional.
     *
     * @param nomeEmpresa Nome da empresa.
     */
    private static void encerrarAplicacao(String nomeEmpresa) {
        try {
            String resposta = lerTextoComCancelamento("Deseja gravar os dados antes de sair? (S/N): ");
            if (resposta.equalsIgnoreCase("S")) {
                System.out.println("A gravar alterações em Logs_" + nomeEmpresa + "...");
                empresa.gravarDados();
            } else {
                System.out.println("As alterações não foram guardadas.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Saída forçada. As alterações não foram guardadas.");
        }
        System.out.println("Até logo!");
    }

// =======================================================
//           MÉTODOS DE LEITURA DE INPUTS
// =======================================================

    /**
     * Lê uma opção do menu (inteiro).
     */
    private static int lerOpcaoMenu(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextInt()) {
            System.out.println("Valor inválido. Tente novamente.");
            scanner.next(); // Limpa input inválido
        }
        int valor = scanner.nextInt();
        scanner.nextLine(); // Limpa newline
        return valor;
    }

    /**
     * Lê texto com CANCELAMENTO - usado no preenchimento de dados.
     */
    private static String lerTextoComCancelamento(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine();

            // Verifica se é "0" para cancelamento COM confirmação
            if (input.trim().equals("0")) {
                System.out.print("\n>> Deseja cancelar a operação? (S/N): ");
                String confirmacao = scanner.nextLine();
                if (confirmacao.equalsIgnoreCase("S")) {
                    throw new OperacaoCanceladaException();
                }
                System.out.println("Operação retomada. Continue a inserir dados.");
                continue; // Continua pedindo input
            }

            if (input.isEmpty()) {
                System.out.println("Erro: O campo não pode estar vazio.");
                continue;
            }

            return input;
        }
    }

    /**
     * Lê inteiro com CANCELAMENTO - usado no preenchimento de dados.
     */
    private static int lerInteiroComCancelamento(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine();

            if (input.trim().equals("0")) {
                System.out.print("\n>> Deseja cancelar a operação? (S/N): ");
                if (scanner.nextLine().equalsIgnoreCase("S")) {
                    throw new OperacaoCanceladaException();
                }
                System.out.println("Operação retomada. Continue a inserir dados.");
                continue; // Continua pedindo input
            }

            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Valor inválido. Insira um número inteiro.");
            }
        }
    }

    /**
     * Lê double com CANCELAMENTO - usado no preenchimento de dados.
     */
    private static double lerDoubleComCancelamento(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine();

            // Verifica se é "0" para cancelamento COM confirmação
            if (input.trim().equals("0")) {
                System.out.print("\n>> Deseja cancelar a operação? (S/N): ");
                if (scanner.nextLine().equalsIgnoreCase("S")) {
                    throw new OperacaoCanceladaException();
                }
                System.out.println("Operação retomada. Continue a inserir dados.");
                continue; // Continua pedindo input
            }

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Erro: valor inválido.");
            }
        }
    }

    /**
     * Lê data com CANCELAMENTO - usado no preenchimento de dados.
     */
    private static LocalDateTime lerDataComCancelamento(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine();

            // Verifica se é "0" para cancelamento COM confirmação
            if (input.trim().equals("0")) {
                System.out.print("\n>> Deseja cancelar a operação? (S/N): ");
                if (scanner.nextLine().equalsIgnoreCase("S")) {
                    throw new OperacaoCanceladaException();
                }
                System.out.println("Operação retomada. Continue a inserir dados.");
                continue; // Continua pedindo input
            }

            try {
                return LocalDateTime.parse(input, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido! Use: dd-MM-yyyy HH:mm");
            }
        }
    }

    /**
     * Confirma se o utilizador quer cancelar a operação atual.
     * Versão simplificada que evita recursão.
     */
    private static boolean confirmarSaidaOperacao() {
        System.out.print("\n>> Deseja cancelar a operação e voltar ao menu? (S/N): ");
        String input = scanner.nextLine();

        return input.equalsIgnoreCase("S");
    }

// =======================================================
//           MÉTODOS DE FORMATAÇÃO VISUAL
// =======================================================

    /**
     * Imprime cabeçalho formatado.
     */
    private static void imprimirCabecalho(String titulo) {
        int tamanhoFixo = 50;

        imprimirLinha();

        int espacos = tamanhoFixo - titulo.length();
        int esquerda = espacos / 2;
        int direita = espacos - esquerda;

        System.out.print("|");
        for (int i = 0; i < esquerda; i++) {
            System.out.print(" ");
        }
        System.out.print(titulo.toUpperCase());
        for (int i = 0; i < direita; i++) {
            System.out.print(" ");
        }
        System.out.println("|");

        imprimirLinha();
    }

    /**
     * Imprime linha separadora.
     */
    private static void imprimirLinha() {
        System.out.print("|");
        for (int i = 0; i < 50; i++) {
            System.out.print("-");
        }
        System.out.println("|");
    }

    /**
     * Imprime título simples.
     */
    private static void imprimirTitulo(String titulo) {
        System.out.println("\n--- " + titulo.toUpperCase() + " ---");
    }

    /**
     * Exibe mensagem de cancelamento.
     */
    private static void exibirMsgCancelar() {
        System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
    }

    /**
     * Imprime mensagem de erro.
     */
    private static void imprimirErro(String mensagem) {
        System.out.println(">>Erro: " + mensagem);
    }

    /**
     * Imprime mensagem de aviso.
     */
    private static void imprimirAviso(String mensagem) {
        System.out.println("\n--- " + mensagem);
    }

// =======================================================
//                   DADOS DE TESTE
// =======================================================

    /**
     * Preenche o sistema com dados de teste.
     */
    public static void inicializarDadosTeste() {
        System.out.println("A gerar dados de teste completos...");
        try {
            // Viaturas
            Viatura viatura1 = new Viatura("AA-00-BB", "Toyota", "Corolla", 2020);
            Viatura viatura2 = new Viatura("XX-99-ZZ", "Tesla", "Model 3", 2024);
            Viatura viatura3 = new Viatura("CC-11-DD", "Renault", "Clio", 2018);

            empresa.adicionarViatura(viatura1);
            empresa.adicionarViatura(viatura2);
            empresa.adicionarViatura(viatura3);

            // Clientes
            Cliente cliente1 = new Cliente("Joao Silva", 100000001, 910000001, "Porto", 11111111);
            Cliente cliente2 = new Cliente("Ana Pereira", 100000002, 910000002, "Lisboa", 22222222);
            Cliente cliente3 = new Cliente("Carlos Sousa", 100000003, 910000003, "Braga", 33333333);

            empresa.adicionarCliente(cliente1);
            empresa.adicionarCliente(cliente2);
            empresa.adicionarCliente(cliente3);

            // Condutores
            Condutor motorista1 = new Condutor("Maria Santos", 200000001, 930000001, "Porto", 44444444, "C-001", 12345);
            Condutor motorista2 = new Condutor("Pedro Gomes", 200000002, 930000002, "Gaia", 55555555, "C-002", 67890);
            Condutor motorista3 = new Condutor("Luisa Lima", 200000003, 930000003, "Matosinhos", 66666666, "C-003", 11223);

            empresa.adicionarCondutor(motorista1);
            empresa.adicionarCondutor(motorista2);
            empresa.adicionarCondutor(motorista3);

            // Viagens históricas
            LocalDateTime ontem = LocalDateTime.now().minusDays(1);

            Viagem v1 = new Viagem(motorista1, cliente1, viatura1,
                    ontem.withHour(10).withMinute(0),
                    ontem.withHour(10).withMinute(30),
                    "Casa da Música", "Ribeira", 5.5, 8.50);

            Viagem v2 = new Viagem(motorista2, cliente2, viatura2,
                    ontem.withHour(14).withMinute(0),
                    ontem.withHour(15).withMinute(0),
                    "Aeroporto", "Hotel", 20.0, 25.00);

            empresa.adicionarViagem(v1);
            empresa.adicionarViagem(v2);

            System.out.println("\n>> Dados carregados: 3 Viaturas, 3 Clientes, 3 Condutores, 2 Viagens");

        } catch (Exception e) {
            imprimirErro("Não é possível carregar dados de teste: " + e.getMessage());
        }
    }

// =======================================================
//           EXCEÇÃO PERSONALIZADA
// =======================================================

    /**
     * Exceção para operações canceladas pelo utilizador.
     */
    private static class OperacaoCanceladaException extends Exception {
    }
}