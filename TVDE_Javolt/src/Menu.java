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
 * Características principais:
 * <ul>
 *   <li>Seleção/criação de empresas</li>
 *   <li>Carregamento/gravação automática de dados</li>
 *   <li>Operações CRUD completas para todas as entidades</li>
 *   <li>Validação robusta de inputs do utilizador</li>
 *   <li>Sistema de cancelamento em qualquer operação</li>
 *   <li>Interface visual formatada com cabeçalhos e menus</li>
 * </ul>
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.5
 * @since 2026-01-11
 */
public class Menu {

    /**
     * Instância da Empresa atual. Não é inicializada logo para permitir
     * seleção do nome da empresa no início da aplicação.
     */
    private static Empresa empresa;

    /**
     * Objeto Scanner partilhado para leitura de inputs do utilizador.
     * Usado em todos os métodos de leitura para evitar múltiplas instâncias.
     */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Formatador de data padrão para o sistema (dd-MM-yyyy HH:mm).
     * Usado para parsing e formatação de datas em toda a aplicação.
     */
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /**
     * Método principal de inicialização da aplicação.
     * <p>
     * Ordem de execução:
     * <ol>
     *   <li>Imprime cabeçalho inicial</li>
     *   <li>Obtém/seleciona nome da empresa</li>
     *   <li>Cria instância da Empresa</li>
     *   <li>Carrega dados existentes ou inicia com vazio</li>
     *   <li>Executa menu principal em loop</li>
     *   <li>Encerra com gravação opcional de dados</li>
     * </ol>
     * </p>
     */
    public static void iniciar() {
        imprimirCabecalho("SISTEMA DE GESTÃO TVDE");

        // 1. Selecionar empresa
        String nomeEmpresa = obterNomeEmpresa();
        if (nomeEmpresa == null || nomeEmpresa.isEmpty()) {
            imprimirAviso("Operação cancelada. A sair...");
            return;
        }

        // 2. Criar empresa
        empresa = new Empresa(nomeEmpresa);
        System.out.println("\nBem vindo à gestão da empresa: " + nomeEmpresa);

        // 3. Carregar dados iniciais
        carregarDadosIniciais();

        // 4. Executar menu principal
        executarMenuPrincipal();

        // 5. Encerrar aplicação
        encerrarAplicacao(nomeEmpresa);
    }

    // =======================================================
    //           INICIALIZAÇÃO E SELEÇÃO DE EMPRESA
    // =======================================================

    /**
     * Obtém o nome da empresa (nova ou existente).
     * <p>
     * Permite voltar ao menu de seleção se o utilizador escolher "0".
     * Se não existirem empresas, força criação de nova.
     * </p>
     *
     * @return Nome da empresa selecionada/criada, ou {@code null} se cancelado completamente.
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
                imprimirAviso("Nenhuma empresa selecionada.");
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
     * Cria uma nova empresa solicitando o nome ao utilizador.
     * <p>
     * O utilizador pode cancelar a operação a qualquer momento pressionando "0".
     * </p>
     *
     * @return Nome da nova empresa, ou {@code null} se cancelado.
     */
    private static String criarEmpresaNova() {
        try {
            imprimirCabecalho("CRIAÇÃO DE NOVA EMPRESA");
            exibirMsgCancelar();

            while (true) {
                String nome = lerTextoComCancelamento("Nome da nova empresa: ");

                if (Empresa.empresaExiste(nome)){
                    imprimirErro("Já existe uma empresa com esse nome. Tente outro.");
                } else {
                    return nome;
                }
            }
        } catch (OperacaoCanceladaException e) {
            return null;
        }
    }

    /**
     * Permite selecionar empresa existente ou criar nova.
     * <p>
     * Apresenta menu com duas opções: criar nova ou carregar existente.
     * </p>
     *
     * @param empresasExistentes Lista de empresas detetadas no sistema.
     * @return Nome da empresa selecionada, ou {@code null} se cancelado.
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
                    imprimirAviso("Opção inválida.");
            }
        }
    }

    /**
     * Lista empresas existentes e permite selecionar uma.
     * <p>
     * Apresenta lista numerada das empresas detetadas.
     * O utilizador pode selecionar uma ou voltar (opção 0).
     * </p>
     *
     * @param empresas Lista de nomes de empresas existentes.
     * @return Nome da empresa selecionada, ou {@code null} se cancelado.
     */
    private static String listarESelecionarEmpresa(ArrayList<String> empresas) {
        imprimirCabecalho("EMPRESAS ENCONTRADAS");
        for (int i = 0; i < empresas.size(); i++) {
            System.out.println("  " + (i + 1) + " - " + empresas.get(i));
        }
        System.out.println("  0 - Voltar");
        imprimirLinha();

        int opcao = lerOpcaoMenu("Escolha a opção: ");

        if (opcao == 0) {
            return null;
        } else if (opcao > 0 && opcao <= empresas.size()) {
            return empresas.get(opcao - 1);
        } else {
            imprimirAviso("Opção inválida.");
            return null;
        }
    }


    /**
     * Pesquisa e lista empresas detetadas no sistema.
     * <p>
     * Procura na pasta "Empresas" por subpastas com prefixo "Logs_".
     * Cada pasta representa uma empresa diferente.
     * </p>
     *
     * @return Lista de nomes de empresas encontradas (sem o prefixo "Logs_").
     */
    private static ArrayList<String> listarEmpresasDetetadas() {
        return Empresa.listarEmpresasExistentes();
    }

// =======================================================
//               CARREGAMENTO DE DADOS
// =======================================================

    /**
     * Carrega dados iniciais da empresa.
     * <p>
     * Verifica se existem dados guardados anteriormente.
     * Se existirem, pergunta se devem ser carregados.
     * Se não existirem, pergunta se devem ser gerados dados de teste.
     * </p>
     */
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
                tratarCarregamentoDadosExistente();
            } else {
                System.out.println("\n>> Primeira inicialização detetada (Sem histórico).");
            }
            verificarDadosTeste();
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Configuração inicial interrompida.");
        }
    }


    /**
     * Trata do carregamento de dados existentes.
     * <p>
     * Pergunta ao utilizador se deseja carregar dados guardados anteriormente.
     * Se o utilizador recusar, mostra aviso sobre possível perda de dados.
     * </p>
     */
    private static void tratarCarregamentoDadosExistente() throws OperacaoCanceladaException {
        System.out.println("\n>> ATENÇÃO: Foram encontrados registos anteriores!");
        String resposta = lerTextoComCancelamento("Deseja carregar os dados guardados? (S/N): ");

        if (resposta.equalsIgnoreCase("S")) {
            System.out.println("A carregar dados...");
            empresa.carregarDados();
        } else {
            mostrarAvisoPerdaDados();
        }
    }

    /**
     * Mostra aviso sobre possível perda de dados.
     * <p>
     * Alert o utilizador que iniciar com dados vazios pode apagar
     * permanentemente o histórico anterior se gravar no final.
     * </p>
     */
    private static void mostrarAvisoPerdaDados() {
        System.out.println("\n!!! PERIGO: DETETADA POSSÍVEL PERDA DE DADOS !!!");
        System.out.println("Se iniciar com a base de dados vazia e gravar no final,");
        System.out.println("o histórico anterior será APAGADO PERMANENTEMENTE.");

        System.out.println(">> Tem a certeza que deseja continuar com a base de dados VAZIA? (S/N): ");

        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            System.out.println("\n>> A iniciar sistema limpo...");
        } else {
            System.out.println("A carregar dados...");
            empresa.carregarDados();
        }
    }

    /**
     * Verifica se precisa de gerar dados de teste.
     * <p>
     * Se a base de dados estiver vazia (sem viaturas nem condutores),
     * pergunta ao utilizador se deseja gerar dados de teste automáticos.
     * </p>
     */
    private static void verificarDadosTeste() {
        if (empresa.getViaturas().isEmpty() && empresa.getCondutores().isEmpty()) {
            try {
                imprimirAviso("A base de dados está vazia.");
                System.out.println();
                String resposta = lerTextoComCancelamento("Deseja gerar dados de teste? (S/N): ");
                if (resposta.equalsIgnoreCase("S")) {
                    inicializarDadosTeste();
                } else {
                    imprimirAviso("Sistema a iniciar com base de dados vazia.");
                }
            } catch (OperacaoCanceladaException e) {
                imprimirAviso("Opção ignorada. Sistema vazio.");
            }
        }
    }

// =======================================================
//               MENU PRINCIPAL
// =======================================================

    /**
     * Executa o menu principal em loop.
     * <p>
     * Exibe o menu principal e processa opções até o utilizador
     * selecionar a opção de saída (0).
     * </p>
     */
    private static void executarMenuPrincipal() {
        int opcao = -1;
        do {
            exibirMenuPrincipal();
            opcao = lerOpcaoMenu("Escolha uma opção: ");
            if (opcao == 0) {
                if (!confirmarSaida()) {
                    opcao = -1;
                }
            } else {
                processarOpcaoPrincipal(opcao);
            }
        } while (opcao != 0);
    }

    /**
     * Exibe o menu principal formatado.
     * <p>
     * Mostra todas as opções principais do sistema.
     * </p>
     */
    private static void exibirMenuPrincipal() {
        imprimirCabecalho("TVDE - MENU PRINCIPAL");
        System.out.println("| 1 - Gerir Viaturas                               |");
        System.out.println("| 2 - Gerir Condutores                             |");
        System.out.println("| 3 - Gerir Clientes                               |");
        System.out.println("| 4 - Gerir Viagens                                |");
        System.out.println("| 5 - Gerir Reservas                               |");
        System.out.println("| 6 - Relatórios/Estatísticas                      |");
        System.out.println("| 0 - Sair                                         |");
        imprimirLinha();
    }

    /**
     * Processa opção do menu principal.
     * <p>
     * Encaminha para o submenu correspondente à opção selecionada.
     * </p>
     *
     * @param opcao Opção selecionada pelo utilizador.
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
     * <p>
     * Pergunta ao utilizador se tem certeza que deseja sair.
     * </p>
     * * @return true se o utilizador confirmar (S), false caso contrário.
     */
    private static boolean confirmarSaida() {
        try {
            String resposta = lerTextoComCancelamento("Tem a certeza que deseja sair? (S/N): ");
            if (resposta.equalsIgnoreCase("S")) {
                imprimirAviso("A preparar encerramento...");
                return true;
            } else {
                imprimirAviso("Saída Cancelada.");
                return false;
            }
        } catch (OperacaoCanceladaException e) {
            return false;
        }
    }

// =======================================================
//               SUBMENUS ESPECÍFICOS
// =======================================================

    /**
     * Menu genérico CRUD para entidades.
     * <p>
     * Implementa o padrão CRUD (Create, Read, Update, Delete)
     * para a entidade especificada.
     * </p>
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
     * Exibe menu CRUD formatado.
     *
     * @param entidade Nome da entidade a mostrar no cabeçalho.
     */
    private static void exibirMenuCRUD(String entidade) {
        imprimirCabecalho("GESTÃO DE " + entidade);
        System.out.println("|    1 - Criar (Create)                            |");
        System.out.println("|    2 - Listar (Read)                             |");
        System.out.println("|    3 - Atualizar (Update)                        |");
        System.out.println("|    4 - Apagar (Delete)                           |");
        System.out.println("|    0 - Voltar                                    |");
        imprimirLinha();
    }

    /**
     * Processa opção do menu CRUD.
     * <p>
     * Encaminha para os métodos específicos da entidade selecionada.
     * </p>
     *
     * @param entidade Nome da entidade.
     * @param opcao    Opção selecionada (1-4 para CRUD, 0 para voltar).
     */
    private static void processarOpcaoCRUD(String entidade, int opcao) {
        switch (entidade) {
            case "Viaturas" -> processarOpcaoViaturas(opcao);
            case "Condutores" -> processarOpcaoCondutores(opcao);
            case "Clientes" -> processarOpcaoClientes(opcao);
        }
    }

    /**
     * Menu de gestão de viagens.
     * <p>
     * Permite registar novas viagens, listar histórico e eliminar viagens.
     * </p>
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
        System.out.println("| 1 - Registar Nova Viagem (Imediata)              |");
        System.out.println("| 2 - Listar Histórico de Viagens                  |");
        System.out.println("| 3 - Apagar uma Viagem do Histórico               |");
        System.out.println("| 0 - Voltar                                       |");
        imprimirLinha();
    }

    /**
     * Processa opção do menu de viagens.
     *
     * @param opcao Opção selecionada (1-3 para operações, 0 para voltar).
     */
    private static void processarOpcaoViagens(int opcao) {
        switch (opcao) {
            case 1 -> tratarRegistarViagem();
            case 2 -> tratarListarViagens();
            case 3 -> tratarEliminarViagem();
        }
    }

    /**
     * Menu de gestão de reservas.
     * <p>
     * Permite todas as operações relacionadas com reservas,
     * incluindo conversão em viagens.
     * </p>
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
     * Exibe menu de reservas formatado.
     */
    private static void exibirMenuReservas() {
        imprimirCabecalho("GESTÃO DE RESERVAS");
        System.out.println("| 1 - Criar Nova Reserva                           |");
        System.out.println("| 2 - Listar Reservas Pendentes                    |");
        System.out.println("| 3 - Consultar Reservas de um Cliente             |");
        System.out.println("| 4 - Alterar uma Reserva                          |");
        System.out.println("| 5 - Converter Reserva em Viagem                  |");
        System.out.println("| 6 - Cancelar/Apagar uma Reserva                  |");
        System.out.println("| 0 - Voltar                                       |");
        imprimirLinha();
    }

    /**
     * Processa opção do menu de reservas.
     *
     * @param opcao Opção selecionada (1-6 para operações, 0 para voltar).
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
     * Menu de estatísticas e relatórios.
     * <p>
     * Permite aceder a várias estatísticas do sistema,
     * como faturação, destinos populares, distâncias médias, etc.
     * </p>
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
     * Exibe menu de estatísticas formatado.
     */
    private static void exibirMenuEstatisticas() {
        imprimirCabecalho("RELATÓRIOS E ESTATÍSTICAS");
        System.out.println("| 1 - Faturação Cliente                            |");
        System.out.println("| 2 - Clientes por Viatura                         |");
        System.out.println("| 3 - Top Destinos                                 |");
        System.out.println("| 4 - Distância Média                              |");
        System.out.println("| 5 - Clientes por Kms                             |");
        System.out.println("| 6 - Histórico Clientes                           |");
        System.out.println("| 0 - Voltar                                       |");
        imprimirLinha();
    }

    /**
     * Processa opção do menu de estatísticas.
     *
     * @param opcao Opção selecionada (1-6 para estatísticas, 0 para voltar).
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
     *
     * @param opcao Opção selecionada (1-Criar, 2-Listar, 3-Atualizar, 4-Eliminar).
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
            imprimirAviso("Operação cancelada.");
        }
    }


    /**
     * Cria uma nova viatura.
     * <p>
     * Solicita todos os dados necessários e cria uma nova {@link Viatura}.
     * Valida se a matrícula é única no sistema.
     * </p>
     *
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
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
            imprimirErro("Não foi possível adicionar a viatura.");
        }
    }

    /**
     * Lê matrícula garantindo que é única no sistema.
     * <p>
     * Continua a pedir matrícula até que seja inserida uma que não exista.
     * </p>
     *
     * @return Matrícula única válida.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static String lerMatriculaUnica() throws OperacaoCanceladaException {
        String matricula;
        do {
            matricula = lerTextoComCancelamento("Matrícula: ");
            if (empresa.procurarViatura(matricula) != null) {
                imprimirErro("Viatura com essa matrícula já existente.");
            }
        } while (empresa.procurarViatura(matricula) != null);
        return matricula;
    }

    /**
     * Lista todas as viaturas registadas.
     * <p>
     * Mostra lista numerada com todas as viaturas do sistema.
     * Se não houver viaturas, mostra mensagem apropriada.
     * </p>
     */
    private static void listarViaturas() {
        if (empresa.getViaturas().isEmpty()) {
            imprimirAviso("Nenhuma viatura registada.");
        } else {
            imprimirTitulo("Lista de Viaturas");
            int contador = 1;
            for (Viatura v : empresa.getViaturas()) {
                System.out.println(contador + ". " + v.toString());
                contador++;
            }
        }
    }

    /**
     * Atualiza uma viatura existente.
     * <p>
     * Solicita matrícula, localiza a viatura e permite editar
     * marca, modelo e ano de fabrico.
     * </p>
     *
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
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
            imprimirErro("Viatura não encontrada.");
        }
    }

    /**
     * Elimina uma viatura do sistema.
     * <p>
     * Verifica se a viatura não tem viagens associadas antes de permitir a eliminação.
     * </p>
     *
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static void eliminarViatura() throws OperacaoCanceladaException {
        System.out.println("--- Apagar Viatura ---");

        mostrarLista("Viaturas");

        String matricula = lerTextoComCancelamento("Matrícula a eliminar: ");
        if (empresa.removerViatura(matricula)) {
            System.out.println("Viatura removida.");
        } else {
            imprimirErro("Viatura não existe ou tem viagens associadas.");
        }
    }

    /**
     * Processa opções CRUD para Condutores.
     *
     * @param opcao Opção selecionada (1-Criar, 2-Listar, 3-Atualizar, 4-Eliminar).
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
            imprimirAviso("Operação cancelada.");
        }
    }


    /**
     * Cria um novo condutor.
     * <p>
     * Solicita todos os dados necessários, incluindo o número de identificação único.
     * Valida NIF e garante unicidade do número de identificação.
     * </p>
     *
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static void criarCondutor() throws OperacaoCanceladaException {
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
                imprimirErro("Número de identificação já existe!");
            }
        } catch (IllegalArgumentException e) {
            imprimirAviso("\nErro de validação nos dados: " + e.getMessage());
        }
    }

    /**
     * Lista todos os condutores registado.
     */
    private static void listarCondutores() {
        ArrayList<Condutor> lista = empresa.getCondutores();
        if (lista.isEmpty()) {
            imprimirAviso("Não há condutores registados.");
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
     * <p>
     * Permite editar nome, telemóvel, morada, carta de condução e segurança social.
     * Não permite alterar número de identificação ou NIF.
     * </p>
     *
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static void atualizarCondutor() throws OperacaoCanceladaException {
        int numeroId = lerInteiroComCancelamento("Número de identificação do condutor a editar: ");
        Condutor condutor = empresa.procurarCondutorPorId(numeroId);

        if (condutor != null) {
            System.out.println("Dados atuais: " + condutor);
            condutor.setNome(lerTextoComCancelamento("Novo Nome: "));
            condutor.setTel(lerInteiroComCancelamento("Novo Telemóvel: "));
            condutor.setMorada(lerTextoComCancelamento("Nova Morada: "));
            condutor.setCartaCond(lerTextoComCancelamento("Nova Carta Condução: "));
            condutor.setSegSocial(lerInteiroComCancelamento("Nova Segurança Social: "));
            System.out.println("Condutor atualizado.");
        } else {
            imprimirAviso("Condutor não encontrado.");
        }
    }

    /**
     * Elimina um condutor do sistema.
     * <p>
     * Verifica se o condutor não tem viagens associadas antes de permitir a eliminação.
     * </p>
     *
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static void eliminarCondutor() throws OperacaoCanceladaException {
        System.out.println("--- Apagar Condutor ---");

        mostrarLista("Condutores");
        int numeroId = lerInteiroComCancelamento("Número de identificação a eliminar: ");
        if (empresa.removerCondutor(numeroId)) {
            imprimirAviso("Condutor removido.");
        } else {
            imprimirErro("Não pode remover condutor com histórico de viagens.");
        }
    }

    /**
     * Processa opções CRUD para Clientes.
     *
     * @param opcao Opção selecionada (1-Criar, 2-Listar, 3-Atualizar, 4-Eliminar).
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
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Cria um novo cliente.
     * <p>
     * Solicita dados básicos do cliente e valida unicidade do NIF.
     * </p>
     *
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
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
                imprimirAviso("Sucesso: Cliente registado!");
            }
        } catch (IllegalArgumentException e) {
            imprimirErro("Erro ao criar cliente: " + e.getMessage());
        }
    }

    /**
     * Lista todos os clientes registados.
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
     * <p>
     * Permite editar nome, telemóvel e morada.
     * Não permite alterar NIF ou cartão de cidadão.
     * </p>
     *
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static void atualizarCliente() throws OperacaoCanceladaException {
        int nif = lerInteiroComCancelamento("NIF do cliente a editar: ");
        Cliente cliente = empresa.procurarCliente(nif);

        if (cliente != null) {
            cliente.setNome(lerTextoComCancelamento("Novo Nome: "));
            cliente.setTel(lerInteiroComCancelamento("Novo Telemóvel: "));
            cliente.setMorada(lerTextoComCancelamento("Nova Morada: "));
            imprimirAviso("Cliente atualizado.");
        } else {
            imprimirErro("Cliente não encontrado.");
        }
    }

    /**
     * Elimina um cliente do sistema.
     * <p>
     * Verifica se o cliente não tem viagens ou reservas associadas
     * antes de permitir a eliminação.
     * </p>
     *
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static void eliminarCliente() throws OperacaoCanceladaException {
        if (empresa.getClientes().isEmpty()) {
            imprimirAviso("Não existem clientes registados para eliminar.");
            return;
        }

        exibirMsgCancelar();
        imprimirTitulo("Escolha o Cliente a Eliminar");

        mostrarLista("Clientes");

        int nif = lerInteiroComCancelamento("NIF a eliminar: ");
        if (empresa.removerCliente(nif)) {
            imprimirAviso("Cliente removido com sucesso.");
        } else {
            imprimirErro("Não pode remover cliente com histórico ou reservas.");
        }
    }

    /**
     * Lê NIF garantindo que é único no sistema.
     * <p>
     * Valida que o NIF tem 9 dígitos e não existe já para o tipo especificado.
     * </p>
     *
     * @param tipo "Cliente" ou "Condutor" para verificação específica.
     * @return NIF válido e único.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static int lerNifUnico(String tipo) throws OperacaoCanceladaException {
        int nif;
        while (true) {
            nif = lerInteiroComCancelamento("NIF (9 dígitos): ");

            if (String.valueOf(nif).length() != 9) {
                imprimirErro("O NIF tem que ser exatamente 9 dígitos.");
                continue;
            }

            boolean existe = false;
            if (tipo.equals("Cliente")) {
                existe = empresa.procurarCliente(nif) != null;
            } else if (tipo.equals("Condutor")) {
                existe = empresa.procurarCondutorPorNif(nif) != null;
            }

            if (existe) {
                imprimirErro("Já existe um " + tipo + " com esse NIF.");
                continue;
            }

            break;
        }
        return nif;
    }

    /**
     * Lê número de identificação do condutor garantindo que é único.
     * <p>
     * Valida que o número é positivo e não existe já no sistema.
     * </p>
     *
     * @return Número de identificação único e válido.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static int lerNumeroIdentificacaoUnico() throws OperacaoCanceladaException {
        int numeroId;
        while (true) {
            numeroId = lerInteiroComCancelamento("Número de identificação do condutor: ");

            if (numeroId <= 0) {
                imprimirErro("O número deve ser positivo.");
                continue;
            }

            // Verifica se já existe condutor com este ID
            if (empresa.procurarCondutorPorId(numeroId) != null) {
                imprimirErro("Já existe um condutor com esse número de identificação.");
                continue;
            }

            break;
        }
        return numeroId;
    }

// =======================================================
//                  MÉTODOS DE VIAGENS
// =======================================================

    /**
     * Regista uma nova viagem.
     * <p>
     * Solicita todos os dados necessários para criar uma viagem,
     * incluindo seleção de condutor, cliente e viatura disponíveis.
     * Verifica conflitos de horário automaticamente.
     * </p>
     */
    private static void tratarRegistarViagem() {
        try {
            imprimirTitulo("NOVA VIAGEM");
            exibirMsgCancelar();

            // 1. Datas
            LocalDateTime inicio = lerDataComCancelamento("Início da Viagem (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Fim da Viagem (dd-MM-yyyy HH:mm): ");

            if (fim.isBefore(inicio)) {
                imprimirErro("Data de fim anterior a data de início.");
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
                imprimirAviso("Viagem adicionada com sucesso.");
            } else {
                imprimirErro("Conflito de horário detetado.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Lista todas as viagens registadas.
     */
    private static void tratarListarViagens() {
        ArrayList<Viagem> viagens = empresa.getViagens();
        if (viagens.isEmpty()) {
            imprimirAviso("Sem viagens registadas!");
        } else {
            imprimirTitulo("\nHistórico de Viagens");
            for (Viagem viagem : viagens) {
                System.out.println(viagem);
            }
        }
    }

    /**
     * Elimina uma viagem do histórico.
     * <p>
     * Mostra lista numerada de viagens e permite selecionar uma para eliminar.
     * </p>
     */
    private static void tratarEliminarViagem() {
        ArrayList<Viagem> viagens = empresa.getViagens();
        if (viagens.isEmpty()) {
            imprimirAviso("Não existem viagens no histórico para eliminar.");
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
                    imprimirAviso("Viagem removida com sucesso!");
                } else {
                    imprimirErro("Não foi possível remover a Viagem.");
                }
            } else {
                imprimirErro("Opção inválida.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação Cancelada.");
        }
    }

// =======================================================
//                  MÉTODOS DE RESERVAS
// =======================================================

    /**
     * Cria uma nova reserva.
     * <p>
     * Solicita dados da reserva e cria um novo objeto {@link Reserva}.
     * Assume duração padrão de 1 hora para verificação de disponibilidade.
     * </p>
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
            imprimirAviso("Reserva registada com sucesso!");
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Lista todas as reservas pendentes.
     */
    private static void tratarListarReservas() {
        ArrayList<Reserva> reservas = empresa.getReservas();
        if (reservas.isEmpty()) {
            imprimirAviso("Sem nenhuma reserva pendente!");
        } else {
            int contador = 1;
            for (Reserva reserva : reservas) {
                System.out.println(contador + ". " + reserva);
                contador++;
            }
        }
    }

    /**
     * Consulta reservas de um cliente específico.
     * <p>
     * Solicita NIF do cliente e mostra todas as suas reservas pendentes.
     * </p>
     */
    private static void tratarConsultarReservasCliente() {
        try {
            exibirMsgCancelar();
            int nif = lerInteiroComCancelamento("NIF do Cliente: ");
            Cliente cliente = empresa.procurarCliente(nif);

            if (cliente != null) {
                ArrayList<Reserva> reservas = empresa.getReservasDoCliente(nif);
                if (reservas.isEmpty()) {
                    imprimirAviso("Este cliente não tem reservas pendentes.");
                } else {
                    imprimirTitulo("Reservas de " + cliente.getNome());
                    for (Reserva reserva : reservas) {
                        System.out.println("[" + reserva + "]");
                    }
                }
            } else {
                imprimirErro("Cliente não encontrado.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Altera uma reserva existente.
     * <p>
     * Permite modificar data/hora, origem, destino ou distância de uma reserva.
     * </p>
     */
    private static void tratarAlterarReserva() {
        try {
            imprimirTitulo("ALTERAR RESERVA");
            exibirMsgCancelar();

            // 1. Selecionar cliente
            int nifCliente = lerInteiroComCancelamento("NIF Cliente: ");
            Cliente cliente = empresa.procurarCliente(nifCliente);

            if (cliente == null) {
                imprimirErro("Cliente não encontrado.");
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
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Seleciona reserva de um cliente específico.
     *
     * @param nifCliente NIF do cliente.
     * @return Reserva selecionada, ou {@code null} se cancelado.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
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
            imprimirErro("Opção inválida.");
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
     *
     * @param reserva Reserva a alterar.
     * @param opcao   Opção selecionada (1-Data, 2-Origem, 3-Destino, 4-Kms).
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static void processarAlteracaoReserva(Reserva reserva, int opcao) throws OperacaoCanceladaException {
        switch (opcao) {
            case 1 -> {
                LocalDateTime novaData = lerDataComCancelamento("Nova Data/Hora (dd-MM-yyyy HH:mm): ");
                reserva.setDataHoraInicio(novaData);
                imprimirAviso("Data atualizada com sucesso!");
            }
            case 2 -> {
                String novaOrigem = lerTextoComCancelamento("Nova Origem: ");
                reserva.setMoradaOrigem(novaOrigem);
                imprimirAviso("Morada origem atualizada com sucesso!");
            }
            case 3 -> {
                String novoDestino = lerTextoComCancelamento("Novo Destino: ");
                reserva.setMoradaDestino(novoDestino);
                imprimirAviso("Morada destino atualizada com sucesso!");
            }
            case 4 -> {
                double novosKms = lerDoubleComCancelamento("Novos Kms: ");
                reserva.setKms(novosKms);
                imprimirAviso("Distância atualizada com sucesso!");
            }
            case 0 -> imprimirAviso("Alteração cancelada.");
            default -> imprimirErro("Opção Inválida.");
        }
    }

    /**
     * Converte reserva em viagem.
     * <p>
     * Permite selecionar uma reserva pendente e convertê-la numa viagem real,
     * atribuindo condutor, viatura e custo.
     * </p>
     */
    private static void tratarConverterReserva() {
        try {
            ArrayList<Reserva> reservas = empresa.getReservas();
            if (reservas.isEmpty()) {
                imprimirAviso("Sem nenhuma Reserva para converter em Viagem");
                return;
            }

            // 1. Selecionar reserva
            imprimirTitulo("Reservas a converter");
            for (int i = 0; i < reservas.size(); i++) {
                System.out.println((i + 1) + ". " + reservas.get(i));
            }

            int index = lerInteiroComCancelamento("Número da reserva: ") - 1;
            if (index < 0 || index >= reservas.size()) {
                imprimirErro("Opção inválida.");
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
                imprimirAviso("Reserva convertida com sucesso!");
            } else {
                imprimirErro("Não foi possível converter Reserva.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Elimina uma reserva pendente.
     */
    private static void tratarEliminarReserva() {
        try {
            ArrayList<Reserva> reservas = empresa.getReservas();
            if (reservas.isEmpty()) {
                imprimirAviso("Não existem reservas para eliminar.");
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
                    imprimirAviso("Reserva removida com sucesso!");
                } else {
                    imprimirErro("Não foi possível remover a Reserva.");
                }
            } else {
                imprimirErro("Opção inválida.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

// =======================================================
//           SELEÇÃO DE RECURSOS DISPONÍVEIS
// =======================================================

    /**
     * Seleciona condutor disponível num intervalo de tempo.
     *
     * @param inicio Data/hora de início do intervalo.
     * @param fim    Data/hora de fim do intervalo.
     * @return Condutor disponível, ou {@code null} se nenhum disponível.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static Condutor selecionarCondutorDisponivel(LocalDateTime inicio, LocalDateTime fim)
            throws OperacaoCanceladaException {

        ArrayList<Condutor> condutoresLivres = empresa.getCondutoresDisponiveis(inicio, fim);

        if (condutoresLivres.isEmpty()) {
            imprimirAviso("Sem condutores disponíveis para este horário.");
            return null;
        }

        mostrarListaCondutores(condutoresLivres);
        return pedirCondutorValido(condutoresLivres);
    }

    /**
     * Mostra lista de condutores disponíveis.
     *
     * @param condutores Lista de condutores disponíveis.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static void mostrarListaCondutores(ArrayList<Condutor> condutores)
            throws OperacaoCanceladaException {

        System.out.println("\n>> Existem " + condutores.size() + " condutores livres.");
        String verLista = lerTextoComCancelamento("Ver lista? (S/N): ");

        if (verLista.equalsIgnoreCase("S")) {
            for (Condutor condutor : condutores) {
                System.out.println("[ID: " + condutor.getNumeroIdentificacao() + " | Nome:" + condutor.getNome() + " | NIF: " + condutor.getNif() + "]");
            }
        }
    }

    /**
     * Pede condutor válido da lista de disponíveis.
     *
     * @param condutoresLivres Lista de condutores disponíveis.
     * @return Condutor selecionado.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static Condutor pedirCondutorValido(ArrayList<Condutor> condutoresLivres)
            throws OperacaoCanceladaException {

        while (true) {
            int numeroId = lerInteiroComCancelamento("Número de identificação do Condutor: ");
            Condutor condutor = empresa.procurarCondutorPorId(numeroId);

            if (condutor == null) {
                imprimirErro("Nenhum condutor encontrado.");
            } else if (!condutoresLivres.contains(condutor)) {
                imprimirAviso("Condutor " + condutor.getNome() + " já tem uma viagem nesse horário.");
            } else {
                return condutor;
            }
        }
    }

    /**
     * Seleciona cliente disponível num intervalo de tempo.
     *
     * @param inicio Data/hora de início do intervalo.
     * @param fim    Data/hora de fim do intervalo.
     * @return Cliente disponível, ou {@code null} se nenhum disponível.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static Cliente selecionarClienteDisponivel(LocalDateTime inicio, LocalDateTime fim)
            throws OperacaoCanceladaException {

        ArrayList<Cliente> clientesLivres = empresa.getClientesDisponiveis(inicio, fim);

        if (clientesLivres.isEmpty()) {
            imprimirAviso("Sem clientes disponíveis.");
            return null;
        }

        mostrarListaClientes(clientesLivres);
        return pedirClienteValido(clientesLivres);
    }


    /**
     * Mostra lista de clientes disponíveis.
     *
     * @param clientes Lista de clientes disponíveis.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static void mostrarListaClientes(ArrayList<Cliente> clientes)
            throws OperacaoCanceladaException {

        System.out.println("\n>> Existem " + clientes.size() + " clientes livres.");
        String verLista = lerTextoComCancelamento("Ver lista? (S/N): ");

        if (verLista.equalsIgnoreCase("S")) {
            for (Cliente cliente : clientes) {
                System.out.println("[Cliente: " + cliente.getNome() + " | NIF: " + cliente.getNif() + "]");
            }
        }
    }

    /**
     * Pede cliente válido da lista de disponíveis.
     *
     * @param clientesLivres Lista de clientes disponíveis.
     * @return Cliente selecionado.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static Cliente pedirClienteValido(ArrayList<Cliente> clientesLivres)
            throws OperacaoCanceladaException {

        while (true) {
            int nifCliente = lerInteiroComCancelamento("NIF do Cliente: ");
            Cliente cliente = empresa.procurarCliente(nifCliente);

            if (cliente == null) {
                imprimirErro("Nenhum cliente encontrado.");
            } else if (!clientesLivres.contains(cliente)) {
                imprimirErro(cliente.getNome() + " já tem uma viagem nesse horário.");
            } else {
                return cliente;
            }
        }
    }

    /**
     * Seleciona viatura disponível num intervalo de tempo.
     *
     * @param inicio Data/hora de início do intervalo.
     * @param fim    Data/hora de fim do intervalo.
     * @return Viatura disponível, ou {@code null} se nenhuma disponível.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static Viatura selecionarViaturaDisponivel(LocalDateTime inicio, LocalDateTime fim)
            throws OperacaoCanceladaException {

        ArrayList<Viatura> viaturasLivres = empresa.getViaturasDisponiveis(inicio, fim);

        if (viaturasLivres.isEmpty()) {
            imprimirAviso("Sem viaturas disponíveis para este horário.");
            return null;
        }

        mostrarListaViaturas(viaturasLivres);
        return pedirViaturaValida(viaturasLivres);
    }

    /**
     * Mostra lista de viaturas disponíveis.
     *
     * @param viaturas Lista de viaturas disponíveis.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static void mostrarListaViaturas(ArrayList<Viatura> viaturas)
            throws OperacaoCanceladaException {

        System.out.println("\n>> Existem " + viaturas.size() + " viaturas livres.");
        String verLista = lerTextoComCancelamento("Ver lista? (S/N): ");

        if (verLista.equalsIgnoreCase("S")) {
            for (Viatura viatura : viaturas) {
                System.out.println("[Matrícula: " + viatura.getMatricula() +
                        " | Marca: " + viatura.getMarca() +
                        " | Modelo: " + viatura.getModelo() + "]");
            }
        }
    }

    /**
     * Pede viatura válida da lista de disponíveis.
     *
     * @param viaturasLivres Lista de viaturas disponíveis.
     * @return Viatura selecionada.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
     */
    private static Viatura pedirViaturaValida(ArrayList<Viatura> viaturasLivres)
            throws OperacaoCanceladaException {

        while (true) {
            String matricula = lerTextoComCancelamento("Matrícula da Viatura: ");
            Viatura viatura = empresa.procurarViatura(matricula);

            if (viatura == null) {
                imprimirErro("Nenhuma viatura encontrada.");
            } else if (!viaturasLivres.contains(viatura)) {
                imprimirErro("A viatura (Matrícula: " + viatura.getMatricula() + ") já tem uma viagem nesse horário.");
            } else {
                return viatura;
            }
        }
    }

    private static void mostrarLista(String tipo) throws OperacaoCanceladaException {
        String resposta = lerTextoComCancelamento("\nDeseja consultar a lista de " + tipo + "? (S/N): ");

        if (resposta.equalsIgnoreCase("S")) {
            System.out.println();
            switch (tipo) {
                case "Viaturas" -> listarViaturas();
                case "Condutores" -> listarCondutores();
                case "Clientes" -> listarClientes();
            }
            System.out.println();
        }
    }

// =======================================================
//                       ESTATÍSTICAS
// =======================================================

    /**
     * Calcula e mostra faturação de um condutor num intervalo de datas.
     */
    private static void estatFaturacaoCondutor() {
        try {
            exibirMsgCancelar();

            // Mostrar lista se solicitado
            String verLista = lerTextoComCancelamento("Deseja ver a lista de condutores? (S/N): ");
            if (verLista.equalsIgnoreCase("S")) {
                mostrarListaCondutoresCompleta();
            }

            int numeroId = lerInteiroComCancelamento("Número de identificação do Condutor a consultar: ");
            Condutor condutor = empresa.procurarCondutorPorId(numeroId);

            if (condutor != null) {
                LocalDateTime inicio = lerDataComCancelamento("Data início (dd-MM-yyyy HH:mm): ");
                LocalDateTime fim = lerDataComCancelamento("Data fim (dd-MM-yyyy HH:mm): ");

                double total = empresa.calcularFaturacaoCondutor(numeroId, inicio, fim);
                System.out.println("O Condutor " + condutor.getNome() + " faturou: " + total + " € nesse período.");
            } else {
                imprimirErro("Condutor não encontrado.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Mostra lista completa de condutores com seus IDs e NIFs.
     */
    private static void mostrarListaCondutoresCompleta() {
        ArrayList<Condutor> condutores = empresa.getCondutores();
        if (condutores.isEmpty()) {
            imprimirAviso("Não existem condutores registados.");
            return;
        }
        imprimirTitulo("Condutores Registados");
        for (Condutor condutor : condutores) {
            System.out.println("-> ID: " + condutor.getNumeroIdentificacao() +
                    " | Nome: " + condutor.getNome() +
                    " | NIF: " + condutor.getNif());
        }
    }

    /**
     * Mostra lista de clientes que viajaram numa viatura específica.
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
                    imprimirAviso("Esta viatura ainda não transportou clientes.");
                } else {
                    imprimirTitulo("Clientes da viatura: " + viatura.getMatricula());
                    for (Cliente cliente : clientes) {
                        System.out.println("-> " + cliente.getNome());
                    }
                }
            } else {
                imprimirErro("Viatura não encontrada.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Mostra lista completa de viaturas registadas.
     */
    private static void mostrarListaViaturasCompleta() {
        ArrayList<Viatura> viaturas = empresa.getViaturas();
        if (viaturas.isEmpty()) {
            imprimirAviso("Não existem viaturas registadas.");
            return;
        }
        imprimirTitulo("Viaturas Registadas");
        for (Viatura viatura : viaturas) {
            System.out.println("-> " + viatura.getMarca() + " " + viatura.getModelo() +
                    " | Matrícula: " + viatura.getMatricula());
        }
    }

    /**
     * Mostra o destino mais solicitado num intervalo de datas.
     */
    private static void estatDestinoMaisSolicitado() {
        try {
            LocalDateTime inicio = lerDataComCancelamento("Data início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Data fim (dd-MM-yyyy HH:mm): ");

            String topDestino = empresa.getDestinoMaisSolicitado(inicio, fim);
            System.out.println("O destino mais popular nesse período é: " + topDestino);
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Calcula e mostra a distância média das viagens num intervalo de datas.
     */
    private static void estatDistanciaMedia() {
        try {
            exibirMsgCancelar();
            LocalDateTime inicio = lerDataComCancelamento("Data início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerDataComCancelamento("Data fim (dd-MM-yyyy HH:mm): ");

            double media = empresa.calcularDistanciaMedia(inicio, fim);
            System.out.println("A distância média das viagens foi: " + media + " Kms");
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Mostra clientes cujas viagens estão dentro de um intervalo de quilómetros.
     */
    private static void estatClientesPorIntervaloKms() {
        try {
            exibirMsgCancelar();
            double minimo = lerDoubleComCancelamento("\nKms Mínimos: ");
            double maximo = lerDoubleComCancelamento("Kms Máximos: ");

            ArrayList<Cliente> clientes = empresa.getClientesPorIntervaloKms(minimo, maximo);
            if (clientes.isEmpty()) {
                imprimirErro("Nenhum cliente encontrado nesse intervalo");
            } else {
                imprimirTitulo("Clientes com viagens entre " + minimo + " e " + maximo + " Kms");
                for (Cliente cliente : clientes) {
                    double totalKms = empresa.calcularTotalKmsCliente(cliente.getNif());
                    System.out.println("[" + cliente.getNome() + " | Nif: " +
                            cliente.getNif() + " | Total: " + totalKms + " Kms]");
                }
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Mostra histórico de viagens de um cliente num intervalo de datas.
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
                    imprimirAviso("Nenhuma viagem registada nesse intervalo");
                } else {
                    imprimirTitulo("Histórico de " + cliente.getNome());
                    for (Viagem viagem : viagens) {
                        System.out.println(viagem);
                    }
                }
            } else {
                imprimirErro("Nenhum cliente encontrado com esse NIF.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Operação cancelada.");
        }
    }

    /**
     * Mostra lista completa de clientes.
     */
    private static void mostrarListaClientesCompleta() {
        ArrayList<Cliente> clientes = empresa.getClientes();
        if (clientes.isEmpty()) {
            imprimirAviso("Não existem clientes registados.");
            return;
        }
        imprimirTitulo("Clientes Registados");
        for (Cliente cliente : clientes) {
            System.out.println("[" + cliente.getNome() + " | NIF: " + cliente.getNif() + "]");
        }
    }

// =======================================================
//           ENCERRAMENTO DA APLICAÇÃO
// =======================================================

    /**
     * Encerra a aplicação com gravação opcional de dados.
     * <p>
     * Pergunta ao utilizador se deseja gravar as alterações antes de sair.
     * Se confirmar, chama o método {@link Empresa#gravarDados()}.
     * </p>
     *
     * @param nomeEmpresa Nome da empresa para referência na mensagem.
     */
    private static void encerrarAplicacao(String nomeEmpresa) {
        try {
            String resposta = lerTextoComCancelamento("Deseja gravar os dados antes de sair? (S/N): ");
            if (resposta.equalsIgnoreCase("S")) {
                System.out.println("A gravar alterações em Logs_" + nomeEmpresa + "...");
                empresa.gravarDados();
            } else {
                imprimirErro("As alterações não foram guardadas.");
            }
        } catch (OperacaoCanceladaException e) {
            imprimirAviso("Saída forçada. As alterações não foram guardadas.");
        }
        imprimirAviso("Até logo!");
    }

// =======================================================
//           MÉTODOS DE LEITURA DE INPUTS
// =======================================================

    /**
     * Lê uma opção do menu (inteiro).
     * <p>
     * Valida que a entrada é um número inteiro.
     * Limpa o buffer após a leitura.
     * </p>
     *
     * @param msg Mensagem a mostrar ao utilizador.
     * @return Opção inteira selecionada.
     */
    private static int lerOpcaoMenu(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextInt()) {
            imprimirErro("Valor inválido. Tente novamente.");
            scanner.next(); // Limpa input inválido
        }
        int valor = scanner.nextInt();
        scanner.nextLine(); // Limpa newline
        return valor;
    }

    /**
     * Lê texto com possibilidade de cancelamento.
     * <p>
     * Permite ao utilizador cancelar a operação pressionando "0".
     * Valida que o campo não está vazio.
     * </p>
     *
     * @param msg Mensagem a mostrar ao utilizador.
     * @return Texto inserido pelo utilizador.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
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
                imprimirAviso("Operação retomada. Continue a inserir dados.");
                continue; // Continua pedindo input
            }

            if (input.isEmpty()) {
                imprimirErro("O campo não pode estar vazio.");
                continue;
            }

            return input;
        }
    }

    /**
     * Lê inteiro com possibilidade de cancelamento.
     * <p>
     * Valida que a entrada é um número inteiro válido.
     * Permite cancelamento com "0".
     * </p>
     *
     * @param msg Mensagem a mostrar ao utilizador.
     * @return Inteiro inserido pelo utilizador.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
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
                imprimirAviso("Operação retomada. Continue a inserir dados.");
                continue; // Continua pedindo input
            }

            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                imprimirErro("Valor inválido. Insira um número inteiro.");
            }
        }
    }

    /**
     * Lê número decimal com possibilidade de cancelamento.
     * <p>
     * Valida que a entrada é um número decimal válido.
     * Permite cancelamento com "0".
     * </p>
     *
     * @param msg Mensagem a mostrar ao utilizador.
     * @return Número decimal inserido pelo utilizador.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
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
                imprimirAviso("Operação retomada. Continue a inserir dados.");
                continue; // Continua pedindo input
            }

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                imprimirErro("Valor inválido.");
            }
        }
    }

    /**
     * Lê data/hora com possibilidade de cancelamento.
     * <p>
     * Valida que a entrada está no formato correto (dd-MM-yyyy HH:mm).
     * Permite cancelamento com "0".
     * </p>
     *
     * @param msg Mensagem a mostrar ao utilizador.
     * @return Data/hora inserida pelo utilizador.
     * @throws OperacaoCanceladaException Se o utilizador cancelar a operação.
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
                imprimirAviso("Operação retomada. Continue a inserir dados.");
                continue; // Continua pedindo input
            }

            try {
                return LocalDateTime.parse(input, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                imprimirErro("Formato inválido! Use: dd-MM-yyyy HH:mm");
            }
        }
    }


// =======================================================
//           MÉTODOS DE FORMATAÇÃO VISUAL
// =======================================================

    /**
     * Imprime cabeçalho formatado com título centralizado.
     * <p>
     * Cria uma caixa de texto com o título centrado.
     * Tamanho fixo de 50 caracteres.
     * </p>
     *
     * @param titulo Título a mostrar no cabeçalho.
     */
    private static void imprimirCabecalho(String titulo) {
        int tamanhoFixo = 50;

        System.out.println();
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
     * <p>
     * Cria uma linha de 50 traços dentro de uma caixa.
     * </p>
     */
    private static void imprimirLinha() {
        System.out.print("|");
        for (int i = 0; i < 50; i++) {
            System.out.print("-");
        }
        System.out.println("|");
    }

    /**
     * Imprime linha separadora.
     * <p>
     * Cria uma linha de 50 traços dentro de uma caixa.
     * </p>
     */
    private static void imprimirTitulo(String titulo) {
        System.out.println("--- " + titulo.toUpperCase() + " ---");
    }

    /**
     * Exibe mensagem de cancelamento.
     * <p>
     * Informa o utilizador que pode pressionar "0" para cancelar.
     * </p>
     */
    private static void exibirMsgCancelar() {
        System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
    }

    /**
     * Imprime mensagem de erro formatada.
     *
     * @param mensagem Mensagem de erro a mostrar.
     */
    private static void imprimirErro(String mensagem) {
        System.out.println(">> Erro: " + mensagem);
    }

    /**
     * Imprime mensagem de aviso formatada.
     *
     * @param mensagem Mensagem de aviso a mostrar.
     */
    private static void imprimirAviso(String mensagem) {
        System.out.println(">> " + mensagem);
    }

// =======================================================
//                   DADOS DE TESTE
// =======================================================

    /**
     * Preenche o sistema com dados de teste.
     * <p>
     * Cria viaturas, clientes, condutores e viagens de exemplo
     * para facilitar testes e demonstração do sistema.
     * </p>
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

            // Condutores com números de identificação únicos
            Condutor motorista1 = new Condutor(1001, "Maria Santos", 200000001, 930000001, "Porto", 44444444, "C-001", 12345);
            Condutor motorista2 = new Condutor(1002, "Pedro Gomes", 200000002, 930000002, "Gaia", 55555555, "C-002", 67890);
            Condutor motorista3 = new Condutor(1003, "Luisa Lima", 200000003, 930000003, "Matosinhos", 66666666, "C-003", 11223);

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

            imprimirAviso("Dados carregados: 3 Viaturas, 3 Clientes, 3 Condutores, 2 Viagens");

        } catch (Exception e) {
            imprimirErro("Não é possível carregar dados de teste: " + e.getMessage());
        }
    }

// =======================================================
//           EXCEÇÃO PERSONALIZADA
// =======================================================

    /**
     * Exceção para operações canceladas pelo utilizador.
     * <p>
     * Usada para controlar o fluxo quando o utilizador cancela
     * uma operação a meio do preenchimento de dados.
     * </p>
     */
    private static class OperacaoCanceladaException extends Exception {
        /**
         * Construtor padrão da exceção.
         */
        public OperacaoCanceladaException() {
            super("Operação cancelada pelo utilizador");
        }
    }
}