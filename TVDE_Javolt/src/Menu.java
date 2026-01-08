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
 * @version 1.3
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

    /**
     * Inicia o ciclo de vida da aplicação.
     * <p>
     * 1. Deteta empresas existentes (pastas Logs_).
     * 2. Permite criar nova ou carregar existente (Menu Visual).
     * 3. Inicializa a Empresa e carrega dados.
     * </p>
     */
    public static void iniciar() {

        System.out.println("\n|-------------------------------------------|");
        System.out.println("|          SISTEMA DE GESTÃO TVDE           |");
        System.out.println("|-------------------------------------------|");

        String nomeEmpresaSelecionada = "";

        //1. Procurar empresas existentes (Pastas "Logs_")
        ArrayList<String> empresasExistentes = listarEmpresasDetetadas();
        boolean empresaCarregada = false;
        while (!empresaCarregada) {
            try {
                if (empresasExistentes.isEmpty()) {
                    //Caso 1: Não existem empresas, força a criação de uma nova.
                    System.out.println("\n>> Nenhuma empresa detetada no sistema.");
                    System.out.println("\n|----------------------------------|");
                    System.out.println("|      CRIAÇÃO DE NOVA EMPRESA     |");
                    System.out.println("|----------------------------------|");
                    nomeEmpresaSelecionada = lerTexto("Introduza o nome: ");
                    empresaCarregada = true;
                } else {
                    //Caso 2: Existem empresas, permite escolher ou criar uma nova.
                    System.out.println("\n|-------------------------------------------|");
                    System.out.println("|             SELEÇÃO DE EMPRESA            |");
                    System.out.println("|-------------------------------------------|");
                    System.out.println("| 1 - Criar Nova Empresa                    |");
                    System.out.println("| 2 - Carregar Empresa Existente (Listar)   |");
                    System.out.println("|-------------------------------------------|");
                    int opcao = lerOpcaoMenu("Escolha uma opção: ");

                    switch (opcao) {
                        case 1 -> {
                            //Opção 1 ou inválida: Criar nova.
                            System.out.println("\n|----------------------------------|");
                            System.out.println("|      CRIAÇÃO DE NOVA EMPRESA     |");
                            System.out.println("|----------------------------------|");
                            System.out.println("Prima 0 para cancelar a operação e retornar ao menu.");
                            nomeEmpresaSelecionada = lerTexto("Nome da nova empresa: ");
                            empresaCarregada = true;
                        }
                        case 2 -> {
                            //Listar opções para carregar
                            System.out.println("\n|----------------------------------|");
                            System.out.println("|       EMPRESAS ENCONTRADAS       |");
                            System.out.println("|----------------------------------|");
                            for (int i = 0; i < empresasExistentes.size(); i++) {
                                System.out.println("  " + (i + 1) + " - " + empresasExistentes.get(i));
                            }
                            System.out.println("  0 - Voltar");
                            System.out.println("|----------------------------------|");

                            int opcao2 = lerOpcaoMenu("Escolha a opção: ");

                            if (opcao2 == 0) {
                                System.out.println("\n>> A voltar ao Menu Principal...");
                            } else {
                                int index = opcao2 - 1;
                                if (index >= 0 && index < empresasExistentes.size()) {
                                    nomeEmpresaSelecionada = empresasExistentes.get(index);
                                    empresaCarregada = true;
                                } else {
                                    System.out.println("Opção inválida.");
                                }
                            }
                        }
                        default -> System.out.println("Opção inválida.");
                    }
                }
            } catch (OperacaoCanceladaException e) {
                System.out.println("\n>> Operação cancelada. A voltar ao menu de seleção....");
            }
        }

        //2. Inicializar a instância da Empresa com o nome definido
        empresa = new Empresa(nomeEmpresaSelecionada);
        System.out.println("\nBem vindo à gestão da empresa: " + nomeEmpresaSelecionada.toUpperCase());

        // 3. Verificar se já existem dados antes de perguntar
        File pastaEmpresa = new File("Empresas/Logs_" + nomeEmpresaSelecionada);

        String[] conteudoPasta = pastaEmpresa.list();

        //A lógica por trás: "A pasta existe?"/"É mesmo uma pasta?"/"Tem ficheiros lá dentro?"
        boolean temDados = pastaEmpresa.exists() &&
                pastaEmpresa.isDirectory() &&
                conteudoPasta != null &&
                conteudoPasta.length > 0;

        if (temDados) {
            try {
                //Caso A: A pasta existe e não está vazia -> pergunta se quer carregar
                System.out.println("\n>> ATENÇÃO: Foram encontrados registos anteriores desta empresa!");
                String respostaCarregar = lerTexto("Deseja carregar os dados guardados? (S/N): ");

                if (respostaCarregar.equalsIgnoreCase("S")) {
                    System.out.println("A carregar dados...");
                    empresa.carregarDados();
                } else {
                    System.out.println("\n!!! PERIGO: DETETADA POSSÍVEL PERDA DE DADOS !!!");
                    System.out.println("Se iniciar com a base de dados vazia e gravar no final,");
                    System.out.println("o histórico anterior será APAGADO PERMANENTEMENTE.");

                    String confirmacao = lerTexto("Tem a certeza que deseja continuar com a base de dados VAZIA? (S/N): ");

                    if (confirmacao.equalsIgnoreCase("S")) {
                        System.out.println("\n>> A iniciar sistema limpo...");
                    } else {
                        System.out.println("\n>> Operação cancelada. A carregar dados por segurança...");
                        empresa.carregarDados();
                    }
                }
            } catch (OperacaoCanceladaException e) {
                System.out.println("Seleção anulada. A carregar dados por segurança...");
            }
        } else {
            //Caso B: PAsta não existe ou está vazia -> Pergunta se quer dados de teste
            System.out.println("\n>> Primeira inicialização detetada (Sem histórico).");
        }

        // 4. Verificação de dados iniciais em memória
        // Se a base de dados estiver vazia (nova empresa ou sem ficheiros), sugere dados de teste.
        if (empresa.getViaturas().isEmpty() && empresa.getCondutores().isEmpty()) {
            try {
                System.out.println("\n>> A base de dados está vazia.");
                String respostaTeste = lerTexto("\nDeseja gerar dados de teste? (S/N): ");
                if (respostaTeste.equalsIgnoreCase("S")) {
                    inicializarDadosTeste();
                } else {
                    System.out.println("\n>> Sistema a iniciar com base de dados vazia.");
                }
            } catch (OperacaoCanceladaException e) {
                System.out.println("O+ção ignorada. Sistema vazio.");
            }
        }

        // 5. Executar o Loop do Menu Principal
        displayMenuPrincipal();

        // 6. Processo de Encerramento e Gravação
        //Só chega aqui quando o utilizador escolhe a opção "0-Sair".
        try {
            String respostaGravar = lerTexto("Deseja gravar os dados antes de sair? (S/N): ");
            if (respostaGravar.equalsIgnoreCase("S")) {
                System.out.println("A gravar alterações em Logs_" + nomeEmpresaSelecionada + "...");
                empresa.gravarDados();
            } else {
                System.out.println("As alterações não foram guardadas.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Saída forçada. As alterações não foram guardadas.");
        }
        System.out.println("Até logo!");
    }

    /**
     * Pesquisa e lista os nomes das empresas cujos dados já existem guardados no sistema.
     * <p>
     * O método procura especificamente dentro da diretoria "Empresas".
     * Identifica subpastas que comecem pelo prefixo "Logs_" e extrai o nome real da empresa
     * (removendo o prefixo) para apresentar ao utilizador.
     * </p>
     *
     * @return Uma lista (ArrayList) contendo os nomes das empresas encontradas.
     */
    private static ArrayList<String> listarEmpresasDetetadas() {
        ArrayList<String> nomesEmpresas = new ArrayList<>();
        File pastaPrincipal = new File("Empresas");

        if (pastaPrincipal.exists() && pastaPrincipal.isDirectory()) {
            File[] ficheiros = pastaPrincipal.listFiles();

            if (ficheiros != null) {
                for (File ficheiro : ficheiros) {
                    //Verifica se é uma pasta e se começa por "Logs_"
                    if (ficheiro.isDirectory() && ficheiro.getName().startsWith("Logs_")) {
                        //Extrai o nome real (remove "Logs_")
                        String nomeReal = ficheiro.getName().substring(5); // 5 é o tamanho de "Logs_".
                        nomesEmpresas.add(nomeReal);
                    }
                }
            }
        }
        return nomesEmpresas;
    }

    /**
     * Exibe o menu principal e gere o loop de execução do programa.
     * Permite navegar para os submenus de Viaturas, Condutores, Clientes, Viagens, Reservas e Estatísticas.
     */
    public static void displayMenuPrincipal() {
        int opcao;
        do {
            System.out.println("\n|--------------------------------|");
            System.out.println("|    TVDE - MENU PRINCIPAL       |");
            System.out.println("|--------------------------------|");
            System.out.println("| 1 - Gerir Viaturas             |");
            System.out.println("| 2 - Gerir Condutores           |");
            System.out.println("| 3 - Gerir Clientes             |");
            System.out.println("| 4 - Gerir Viagens              |");
            System.out.println("| 5 - Gerir Reservas             |");
            System.out.println("| 6 - Relatórios/Estatísticas    |");
            System.out.println("| 0 - Sair                       |");
            System.out.println("|--------------------------------|");
            opcao = lerOpcaoMenu("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> menuCRUD("Viaturas");
                case 2 -> menuCRUD("Condutores");
                case 3 -> menuCRUD("Clientes");
                case 4 -> menuViagens();
                case 5 -> menuReservas();
                case 6 -> menuEstatisticas();
                case 0 -> {
                    try {
                        String temCerteza = lerTexto("Tem a certeza que deseja sair? (S/N)");
                        if (!temCerteza.equalsIgnoreCase("S")) {
                            System.out.println("\n>> Saída Cancelada. A voltar para o Menu...");
                            opcao = -1;
                        } else {
                            System.out.println("A preparar encerramento...");
                        }
                    } catch (OperacaoCanceladaException e) {
                        opcao = -1;
                    }
                }
                default -> {
                    if (opcao != -1) {
                        System.out.println("Opção inválida.");
                    }
                }
            }
        } while (opcao != 0);
    }

    // =======================================================
    //            MENU VIAGENS E RESERVAS
    // =======================================================

    /**
     * Exibe o menu específico para gestão de Viagens.
     */
    private static void menuViagens() {
        int op;
        do {
            System.out.println("\n|---------------------------------------|");
            System.out.println("|           GESTÃO DE VIAGENS           |");
            System.out.println("|---------------------------------------|");
            System.out.println("| 1 - Registar Nova Viagem (Imediata)   |");
            System.out.println("| 2 - Listar Histórico de Viagens       |");
            System.out.println("| 3 - Apagar uma Viagem do Histórico    |");
            System.out.println("| 0 - Voltar                            |");
            System.out.println("|---------------------------------------|");
            op = lerOpcaoMenu("Escolha uma opção: ");

            switch (op) {
                case 1 -> tratarRegistarViagem();
                case 2 -> tratarListarViagens();
                case 3 -> tratarEliminarViagem();
            }
        } while (op != 0);
    }

    /**
     * Exibe o menu específico para gestão de Reservas.
     */
    private static void menuReservas() {
        int op;
        do {
            System.out.println("\n|---------------------------------------|");
            System.out.println("|           GESTÃO DE RESERVAS          |");
            System.out.println("|---------------------------------------|");
            System.out.println("| 1 - Criar Nova Reserva                |");
            System.out.println("| 2 - Listar Reservas Pendentes         |");
            System.out.println("| 3 - Consultar Reservas de um Cliente  |");
            System.out.println("| 4 - Alterar uma Reserva               |");
            System.out.println("| 5 - Converter Reserva em Viagem       |");
            System.out.println("| 6 - Cancelar/Apagar uma Reserva       |");
            System.out.println("| 0 - Voltar                            |");
            System.out.println("|---------------------------------------|");
            op = lerOpcaoMenu("Escolha uma opção: ");


            switch (op) {
                case 1 -> tratarCriarReserva();
                case 2 -> tratarListarReservas();
                case 3 -> tratarConsultarReservasCliente();
                case 4 -> tratarAlterarReserva();
                case 5 -> tratarConverterReserva();
                case 6 -> tratarEliminarReserva();
            }
        } while (op != 0);
    }

    // =======================================================
    //                  MENU CRUD (Lógica)
    // =======================================================


    /**
     * Apresenta o menu genérico para operações CRUD (Create, Read, Update, Delete).
     * Redireciona para o metodo de processamento específico consoante a entidade escolhida.
     *
     * @param entidade Nome da entidade a gerir (ex: "Viaturas", "Clientes") para personalização do título.
     */
    private static void menuCRUD(String entidade) {
        int opcao;
        do {
            System.out.println("\n|--------------------------------|");
            // Formatação simples para centrar o título (ajuste manual)
            System.out.println("     GESTÃO DE " + entidade.toUpperCase());
            System.out.println("|--------------------------------|");
            System.out.println("|    1 - Criar (Create)          |");
            System.out.println("|    2 - Listar (Read)           |");
            System.out.println("|    3 - Atualizar (Update)      |");
            System.out.println("|    4 - Apagar (Delete)         |");
            System.out.println("|    0 - Voltar                  |");
            System.out.println("|--------------------------------|");
            opcao = lerOpcaoMenu("Escolha a opção: ");

            switch (entidade) {
                case "Viaturas" -> processarViaturas(opcao);
                case "Condutores" -> processarCondutores(opcao);
                case "Clientes" -> processarClientes(opcao);
            }
        } while (opcao != 0);
    }

    // =======================================================
    //            MENU ESTATÍSTICAS
    // =======================================================

    /**
     * Exibe o menu de Estatísticas e Relatórios.
     */
    private static void menuEstatisticas() {
        int op;

        do {
            System.out.println("\n|----------------------------------------|");
            System.out.println("|      RELATÓRIOS E ESTATÍSTICAS         |");
            System.out.println("|----------------------------------------|");
            System.out.println("| 1 - Faturação Cliente                  |");
            System.out.println("| 2 - Clientes por Viatura               |");
            System.out.println("| 3 - Top Destinos                       |");
            System.out.println("| 4 - Distância Média                    |");
            System.out.println("| 5 - Clientes por Kms                   |");
            System.out.println("| 6 - Histórico Clientes                 |");
            System.out.println("| 0 - Voltar                             |");
            System.out.println("|----------------------------------------|");
            op = lerOpcaoMenu("Escolha uma opção: ");

            switch (op) {
                case 1 -> estatFaturacaoCondutor();
                case 2 -> estatClientesViatura();
                case 3 -> estatDestinoMaisSolicitado();
                case 4 -> estatDistanciaMedia();
                case 5 -> estatClientesPorIntervaloKms();
                case 6 -> estatHistoricoClientePorDatas();
            }
        } while (op != 0);
    }


    // =======================================================
    //            PROCESSAMENTO CRUD (Lógica)
    // =======================================================

    /**
     * Processa as operações CRUD específicas para a entidade {@link Viatura}.
     *
     * @param opcao A opção selecionada pelo utilizador no menu CRUD.
     */
    private static void processarViaturas(int opcao) {
        try {
            switch (opcao) {
                case 1 -> { // CREATE
                    System.out.println("\n--- Nova Viatura ---");
                    System.out.println("Prima 0 em qualquer momento para cancelar a operação.");

                    String mat = lerTexto("Matrícula: ");
                    while (empresa.procurarViatura(mat) != null) {
                        System.out.println("Erro: Viatura com essa matricula já existente.");
                        mat = lerTexto("Matricula: ");
                    }

                    String marca = lerTexto("Marca: ");
                    String modelo = lerTexto("Modelo: ");
                    int ano = lerInteiro("Ano de Fabrico: ");

                    Viatura viatura = new Viatura(mat, marca, modelo, ano);
                    if (empresa.adicionarViatura(viatura)) {
                        System.out.println("Viatura adicionada com sucesso!");
                    }
                }

                case 2 -> { // READ
                    if (empresa.getViaturas().isEmpty()) {
                        System.out.println("Nenhuma viatura registada.");
                    } else {
                        System.out.println("\n--- Lista de Viaturas ---");
                        int i = 1; //Contador inicaliza em 1
                        for (Viatura v : empresa.getViaturas()) {
                            System.out.println(i + ". " + v.toString());
                            i++;
                        }
                    }
                }

                case 3 -> { // UPDATE
                    String mat = lerTexto("Insira a matrícula da viatura a editar: ");
                    Viatura viatura = empresa.procurarViatura(mat);
                    if (viatura != null) {
                        System.out.println("Dados atuais: " + viatura);
                        viatura.setMarca(lerTexto("Nova Marca: "));
                        viatura.setModelo(lerTexto("Novo Modelo: "));
                        viatura.setAnoFabrico(lerInteiro("Novo Ano: "));
                        System.out.println("Viatura atualizada.");
                    } else {
                        System.out.println("Viatura não encontrada.");
                    }
                }
                case 4 -> { // DELETE
                    String mat = lerTexto("Matrícula a eliminar: ");
                    if (empresa.removerViatura(mat)) {
                        System.out.println("Viatura removida.");
                    } else {
                        System.out.println("Erro: Viatura não existe ou tem viagens associadas.");
                    }
                }
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }

    /**
     * Processa as operações CRUD específicas para a entidade {@link Condutor}.
     *
     * @param opcao A opção selecionada pelo utilizador no menu CRUD.
     */
    private static void processarCondutores(int opcao) {
        try {
            int nif;
            switch (opcao) {
                case 1 -> { // CREATE
                    System.out.println("\n--- Novo Condutor ---");
                    System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
                    while (true) {
                        nif = lerInteiro("NIF (9 dígitos): ");

                        if (String.valueOf(nif).length() != 9) {
                            System.out.println("Erro: O NIF tem que ser exatamente 9 digitos.");
                            continue;
                        }
                        if (empresa.procurarCondutor(nif) != null) {
                            System.out.println("Erro: Já existe um Condutor com esse NIF.");
                            continue;
                        }
                        break;
                    }

                    String nome = lerTexto("Nome: ");
                    int tel = lerInteiro("Telemóvel: ");
                    String morada = lerTexto("Morada: ");
                    int cc = lerInteiro("Cartão Cidadão: ");
                    String carta = lerTexto("Carta Condução: ");
                    int ss = lerInteiro("Segurança Social: ");

                    try {
                        Condutor condutor = new Condutor(nome, nif, tel, morada, cc, carta, ss);
                        if (empresa.adicionarCondutor(condutor)) {
                            System.out.println("Condutor registado com sucesso!");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("\nErro de validação nos dados: " + e.getMessage());
                    }
                }
                case 2 -> { // READ
                    ArrayList<Condutor> listaCondutores = empresa.getCondutores();
                    if (listaCondutores.isEmpty()) {
                        System.out.println("Não há condutores registados.");
                    } else {
                        System.out.println("\n--- Lista de Condutores ---");
                        int i = 1; // Contador inicializa em 1.
                        for (Condutor c : listaCondutores) {
                            System.out.println(i + ". " + c.toString());
                            i++;
                        }
                    }
                }
                case 3 -> { // UPDATE
                    nif = lerInteiro("NIF do condutor a editar: ");
                    Condutor condutor = empresa.procurarCondutor(nif);
                    if (condutor != null) {
                        condutor.setNome(lerTexto("Novo Nome: "));
                        condutor.setTel(lerInteiro("Novo Telemóvel: "));
                        condutor.setMorada(lerTexto("Nova Morada: "));
                        System.out.println("Condutor atualizado.");
                    } else {
                        System.out.println("Condutor não encontrado.");
                    }
                }
                case 4 -> { // DELETE
                    nif = lerInteiro("NIF a eliminar: ");
                    if (empresa.removerCondutor(nif)) {
                        System.out.println("Condutor removido.");
                    } else {
                        if (empresa.procurarCondutor(nif) != null) {
                            System.out.println("Erro: Não pode remover condutor com hisrórico de viagens.");
                        } else {
                            System.out.println("Erro: Condutor não encontrado.");
                        }
                    }
                }
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }

    /**
     * Processa as operações CRUD específicas para a entidade {@link Cliente}.
     *
     * @param opcao A opção selecionada pelo utilizador no menu CRUD.
     */
    private static void processarClientes(int opcao) {
        try {
            int nif;
            switch (opcao) {
                case 1 -> { // CREATE
                    System.out.println("\n--- Novo Cliente ---");
                    System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
                    while (true) {
                        nif = lerInteiro("NIF (9 dígitos): ");
                        if (String.valueOf(nif).length() != 9) {
                            System.out.println("Erro: O NIF tem que ser exatamente 9 digitos.");
                            continue;
                        }

                        if (empresa.procurarCliente(nif) != null) {
                            System.out.println("Erro: Já existe um Cliente com esse NIF.");
                            continue;
                        }
                        break;
                    }
                    String nome = lerTexto("Nome: ");
                    int tel = lerInteiro("Telemóvel: ");
                    String morada = lerTexto("Morada: ");
                    int cc = lerInteiro("Cartão Cidadão: ");

                    //Valida erros de validação da classe Cliente
                    try {
                        Cliente cliente = new Cliente(nome, nif, tel, morada, cc);
                        if (empresa.adicionarCliente(cliente)) {
                            System.out.println("Sucesso: Cliente registado!");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erro ao clirar cliente:" + e.getMessage());
                    }
                }
                case 2 -> { // READ
                    ArrayList<Cliente> listaClientes = empresa.getClientes();
                    if (listaClientes.isEmpty()) {
                        System.out.println("\n--- Não há clientes registados.");
                    } else {
                        System.out.println("\n--- Lista de Clientes ---");
                        int i = 1; // Contador inicia a 1
                        for (Cliente c : listaClientes) {
                            // Imprime o número seguido do objeto.
                            System.out.println(i + ". " + c.toString());
                            i++;
                        }
                    }
                }
                case 3 -> { // UPDATE
                    nif = lerInteiro("NIF do cliente a editar: ");
                    Cliente cliente = empresa.procurarCliente(nif);
                    if (cliente != null) {
                        cliente.setNome(lerTexto("Novo Nome: "));
                        cliente.setTel(lerInteiro("Novo Telemóvel: "));
                        cliente.setMorada(lerTexto("Nova Morada: "));
                        System.out.println("Cliente atualizado.");
                    } else {
                        System.out.println("Cliente não encontrado.");
                    }
                }
                case 4 -> { // DELETE
                    ArrayList<Cliente> listaClientes = empresa.getClientes();
                    if (listaClientes.isEmpty()) {
                        System.out.println("Não existem clientes registados para eliminar.");
                    } else {
                        System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
                        System.out.println("\n--- Escolha o Cliente a Eliminar ---");
                        for (Cliente clientes : listaClientes) {
                            System.out.println("[" + clientes.getNome() + " | Nif: " + clientes.getNif() + "]");
                        }
                        nif = lerInteiro("NIF a eliminar: ");
                        if (empresa.removerCliente(nif)) {
                            System.out.println("Cliente removido com sucesso.");
                        } else {
                            if (empresa.procurarCliente(nif) != null) {
                                System.out.println("Erro: Não pode remover cliente que já tem histórico de Viagens.");
                            }else {
                                System.out.println("Erro: Cliente não encontrado com esse NIF.");
                            }
                        }
                    }


                }
            }
        } catch (
                OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }

// =======================================================
//    MÉTODOS AUXILIARES - LÓGICA VIAGENS/RESERVAS
// =======================================================

    /**
     * Recolhe os dados para uma nova viagem.
     * <p>
     * Utiliza métodos auxiliares para selecionar Condutor, Cliente e Viatura,
     * garantindo que apenas recursos disponíveis são escolhidos.
     * </p>
     */
    private static void tratarRegistarViagem() {
        System.out.println("\n--- Nova Viagem ---");
        System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
        try {
            //1. Pede ao utilizador as datas. É o filtro principal deste metodo.
            LocalDateTime inicio = lerData("Início da Viagem(dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerData("Fim da Viagem(dd-MM-yyyy HH:mm): ");
            if (fim.isBefore(inicio)) {
                System.out.println("Erro: Data de fim anterior a data de inicio.");
                return;
            }

            //2. Seleção de Condutor
            Condutor condutor = selecionarCondutorDisponivel(inicio, fim);
            if (condutor == null) {
                return;
            }

            //3. Seleção de Cliente
            Cliente cliente = selecionarClienteDisponivel(inicio, fim);
            if (cliente == null) {
                return;
            }

            //4. Seleção de Viatura
            Viatura viatura = selecionarViaturaDisponivel(inicio, fim);
            if (viatura == null) {
                return;
            }

            //5.Dados finais.
            String origem = lerTexto("Origem: ");
            String destino = lerTexto("Destino: ");
            double kms = lerDouble("Kms: ");
            double custo = lerDouble("Custo: ");

            Viagem novaViagem = new Viagem(condutor, cliente, viatura, inicio, fim, origem, destino, kms, custo);

            //A validação final
            if (empresa.adicionarViagem(novaViagem)) {
                System.out.println("Viagem adicionada com sucesso.");
            } else {
                System.out.println("Erro: Conflito de horário detetado na gravação.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }

    /**
     * Lista todas as viagens armazenadas no histórico da empresa.
     */
    private static void tratarListarViagens() {
        if (empresa.getViagens().isEmpty()) {
            System.out.println("Sem viagens registadas!");
        } else {
            System.out.println("--- Histórico de Viagens ---");
            for (Viagem viagem : empresa.getViagens()) {
                System.out.println(viagem);
            }
        }
    }

    /**
     * Permite ao utilizador eliminar uma viagem do histórico.
     */
    private static void tratarEliminarViagem() {
        ArrayList<Viagem> viagens = empresa.getViagens();
        if (viagens.isEmpty()) {
            System.out.println("Não existem viagens no histórico para eliminar.");
            return;
        }
        System.out.println("--- Escolha a Viagem a Eliminar ---");
        System.out.println("Prima 0 em qualquer momento para cancelar a operação.");

        //Listar as viagens para o utilizador saber qual o número escolher.
        for (int i = 0; i < viagens.size(); i++) {
            System.out.println((i + 1) + ". " + viagens.get(i));
        }

        try {
            int index = lerInteiro("\n Número da viagem a apagar: ") - 1;
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
//            MÉTODOS DE RESERVAS (COM CANCELAMENTO)
// =======================================================

    /**
     * Recolhe os dados necessários para criar e registar uma nova {@link Reserva}.
     * <p>
     * Implementa uma lógica de seleção inteligente para evitar sobreposições:
     * 1. Solicita a data e hora em primeiro lugar.
     * 2. Calcula uma janela de tempo virtual (1 hora) para verificar a disponibilidade.
     * 3. Apresenta apenas os clientes que estão livres nesse horário.
     * </p>
     */
    private static void tratarCriarReserva() {
        try {
            System.out.println("\n--- Nova Reserva ---");
            System.out.println("Prima 0 em qualquer momento para cancelar a operação.");

            //1. Pedir a data primeiro (Fator crítico de disponibilidade)
            LocalDateTime inicio = lerData("Data/Hora da Reserva (dd-MM-yyyy HH:mm): ");

            // Nota Técnica: Como a Reserva não tem "Fim" definido, assumimos uma
            // duração virtual de 1 hora apenas para verificar se o cliente
            // não está ocupado nesse momento exato.
            LocalDateTime fimEstimado = inicio.plusHours(1);

            //2. Usar o metodo virtual para verificar disponibilidade
            Cliente cliente = selecionarClienteDisponivel(inicio, fimEstimado);

            if (cliente != null) {
                //3. Se escolheu um cliente válido, segue...
                String origem = lerTexto("Origem: ");
                String destino = lerTexto("Destino: ");
                double kms = lerDouble("Kms estimados: ");

                Reserva reserva = new Reserva(cliente, inicio, origem, destino, kms);
                empresa.adicionarReserva(reserva);
                System.out.println("Reserva registada com sucesso!");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }

    /**
     * Lista todas as reservas pendentes.
     */
    private static void tratarListarReservas() {
        if (empresa.getReservas().isEmpty()) {
            System.out.println("Sem nenhuma reserva pendente!");
        } else {
            int i = 1;
            for (Reserva reserva : empresa.getReservas()) {
                System.out.println(i + ". " + reserva);
                i++;
            }
        }
    }

    /**
     * Consulta e lista as reservas associadas a um cliente específico pelo NIF.
     */
    private static void tratarConsultarReservasCliente() {
        try {
            System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
            int nif = lerInteiro("NIF do Cliente: ");
            Cliente cliente = empresa.procurarCliente(nif);

            if (cliente != null) {
                ArrayList<Reserva> reservas = empresa.getReservasDoCliente(nif);
                if (reservas.isEmpty()) {
                    System.out.println("Este cliente não tem reservas pendentes.");
                } else {
                    System.out.println("--- Reservas de " + cliente.getNome() + " ---");
                    for (Reserva reserva : reservas) {
                        System.out.println(reserva);
                    }
                }
            } else {
                System.out.println("Cliente não encontrado.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }

    /**
     * Permite alterar os dados de uma reserva existente de um determinado cliente.
     */
    private static void tratarAlterarReserva() {
        System.out.println("\n--- Altera Reserva de Cliente ---");
        System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
        try {
            //1.Pedir o NIF para filtrar
            int nifCliente = lerInteiro("NIF Cliente: ");
            Cliente cliente = empresa.procurarCliente(nifCliente);

            if (cliente == null) {
                System.out.println("Cliente não encontrado.");
                return;
            }

            //2. Obter apenas as reservas deste cliente
            ArrayList<Reserva> reservasCliente = empresa.getReservasDoCliente(nifCliente);

            if (reservasCliente.isEmpty()) {
                System.out.println("Este cliente não tem reservas pendentes para alterar.");
                return;
            }
            //3. Listar para escolher
            System.out.println("--- Reservas de " + cliente.getNome() + " ---");
            for (int i = 0; i < reservasCliente.size(); i++) {
                System.out.println((i + 1) + ". " + reservasCliente.get(i));
            }

            //4. Selecionar
            int index = lerInteiro("Escolha a Reserva a Alterar: ") - 1;
            if (index >= 0 && index < reservasCliente.size()) {
                Reserva res = reservasCliente.get(index);

                System.out.println("--- O que deseja alterar? ---");
                System.out.println("1 - Data e Hora");
                System.out.println("2 - Origem");
                System.out.println("3 - Destino");
                System.out.println("4 - Distância (Kms)");
                System.out.println("0 - Cancelar");

                int opcao = lerInteiro("Escolha a opção: ");
                switch (opcao) {
                    case 1 -> {
                        LocalDateTime novaData = lerData("Nova Data/Hora (dd-MM-yyyy HH:mm): ");
                        res.setDataHoraInicio(novaData);
                        System.out.println("Data atualizada com sucesso!");
                    }
                    case 2 -> {
                        String novaOrigem = lerTexto("Nova Origem: ");
                        res.setMoradaOrigem(novaOrigem);
                        System.out.println("Morada origem atualizada com sucesso!");
                    }
                    case 3 -> {
                        String novoDestino = lerTexto("Novo Destino: ");
                        res.setMoradaDestino(novoDestino);
                        System.out.println("Morada destino atualizada com sucesso!");

                    }
                    case 4 -> {
                        double novosKms = lerDouble("Novos Kms: ");
                        res.setKms(novosKms);
                        System.out.println("Distância atualizada com sucesso!");
                    }
                    default -> System.out.println("Opção Inválida.");
                }
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }

    /**
     * Converte uma reserva existente numa viagem efetiva, atribuindo condutor e viatura.
     */
    private static void tratarConverterReserva() {
        try {
            ArrayList<Reserva> reservas = empresa.getReservas();
            if (reservas.isEmpty()) {
                System.out.println("Sem nenhuma Reserva para converter em Viagem");
                return;
            }
            // 1. Listar para escolher
            System.out.println("--- Escolha a Reserva a converter");
            for (int i = 0; i < reservas.size(); i++) {
                System.out.println((i + 1) + ". " + reservas.get(i));
            }
            //2. Selecionar
            int index = lerInteiro("Número da reserva: ") - 1;
            if (index >= 0 && index < reservas.size()) {
                Reserva resSelecionada = reservas.get(index);
                System.out.println("Reserva Selecionada: " + resSelecionada.getCliente().getNome() + " - " + resSelecionada.getDataHoraInicio().format(dateTimeFormatter));

                int duracao = lerInteiro("Duração estimada (minutos): ");
                LocalDateTime fimEstimado = resSelecionada.getDataHoraInicio().plusMinutes(duracao);

                Condutor condutor = selecionarCondutorDisponivel(resSelecionada.getDataHoraInicio(), fimEstimado);
                if (condutor == null) {
                    return;
                }

                Viatura viatura = selecionarViaturaDisponivel(resSelecionada.getDataHoraInicio(), fimEstimado);
                if (viatura == null) {
                    return;
                }

                double custo = lerDouble("Custo Final (€): ");

                if (empresa.converterReservaEmViagem(resSelecionada, condutor, viatura, custo)) {
                    System.out.println("Reserva convertida com sucesso!");
                } else {
                    System.out.println("Erro ao converter Reserva.");
                }
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }

    /**
     * Permite eliminar uma reserva pendente.
     */
    private static void tratarEliminarReserva() {
        try {
            ArrayList<Reserva> reservas = empresa.getReservas();
            if (reservas.isEmpty()) {
                System.out.println("Não existem reservas para eliminar.");
            }

            System.out.println("\n--- Escolha a Reserva a Eliminar ---");

            for (int i = 0; i < reservas.size(); i++) {
                System.out.println((i + 1) + ". " + reservas.get(i));
            }
            System.out.println("Prima 0 em qualquer momento para cancelar a operação.");

            int index = lerInteiro("\nNumero da reserva a apagar: ") - 1;

            if (index >= 0 && index < reservas.size()) {
                Reserva res = reservas.get(index);
                if (empresa.removerReserva(res)) {
                    System.out.println("Reserva removida com sucesso!");
                } else {
                    System.out.println("Erro ao remover Reserva.");
                }
            } else {
                System.out.println("Operação cancelada.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }


// =======================================================
//        MÉTODOS AUXILIARES DE ESTATÍSTICAS
// =======================================================

    /**
     * Calcula e apresenta a faturação de um condutor num intervalo de datas.
     * Pergunta se o utilizador quer ver a lista antes de pedir o NIF.
     */
    private static void estatFaturacaoCondutor() {
        try {
            System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
            String verLista = lerTexto("Deseja ver a lista de condutores? (S/N): ");

            if (verLista.equalsIgnoreCase("S")) {
                ArrayList<Condutor> condutores = empresa.getCondutores();
                if (condutores.isEmpty()) {
                    System.out.println("\n>> Não existem condutores registados.");
                    return;
                }
                System.out.println("\n--- Condutores Registados ---");
                for (Condutor condutor : condutores) {
                    System.out.println("- " + condutor.getNome() + " | NIF: " + condutor.getNif());
                }
                System.out.println("-----------------------------");
            }

            int nif = lerInteiro("NIF do Condutor a consultar: ");
            Condutor condutor = empresa.procurarCondutor(nif);
            if (condutor != null) {
                LocalDateTime inicio = lerData("Data inicio (dd-MM-yyyy HH:mm): ");
                LocalDateTime fim = lerData("Data fim (dd-MM-yyyy HH:mm): ");

                double total = empresa.calcularFaturacaoCondutor(nif, inicio, fim);
                System.out.println("O Condutor " + condutor.getNome() + " faturou: " + total + " € nesse periodo.");
            } else {
                System.out.println("Condutor não encontrado.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }


    /**
     * Lista os clientes distintos que viajaram numa determinada viatura.
     * Pergunta se o utilizador quer ver a lista de matrículas.
     */
    private static void estatClientesViatura() {
        try {
            System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
            String verLista = lerTexto("\nDeseja ver a lista de Viaturas? (S/N): ");

            if (verLista.equalsIgnoreCase("S")) {
                ArrayList<Viatura> lista = empresa.getViaturas();
                if (lista.isEmpty()) {
                    System.out.println("\n Não existem viaturas registadas.");
                    return;
                }
                System.out.println("\n--- Viaturas Registadas ---");
                for (Viatura viatura : lista) {
                    System.out.println("- " + viatura.getMarca() + " " + viatura.getModelo() + " | Matrícula: " + viatura.getMatricula());
                }
            }

            String matricula = lerTexto("\nMatricula da Viatura: ");
            Viatura viatura = empresa.procurarViatura(matricula);

            if (viatura != null) {
                ArrayList<Cliente> lista = empresa.getClientesPorViatura(matricula);
                if (lista.isEmpty()) {
                    System.out.println("Esta viatura ainda não transportou clientes.");
                } else {
                    System.out.println("--- Clientes da Viatura " + matricula + " ---");
                    for (Cliente cliente : lista) {
                        System.out.println("- " + cliente.getNome());
                    }
                }
            } else {
                System.out.println("Viatura não encontrada.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }

    /**
     * Apresenta o destino mais solicitado no sistema (considerando Viagens e Reservas) num intervalo de datas.
     */
    private static void estatDestinoMaisSolicitado() {
        try {
            LocalDateTime inicio = lerData("Data inicio (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerData("Data fim (dd-MM-yyyy HH:mm): ");

            String topDestino = empresa.getDestinoMaisSolicitado(inicio, fim);
            if (topDestino != null) {
                System.out.println(" O destino mais popular nesse período é: " + topDestino);
            } else {
                System.out.println("Sem dados para as datas indicadas.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }

    /**
     * Calcula e apresenta a média de Kms das viagens realizadas num intervalo de datas.
     */
    private static void estatDistanciaMedia() {
        try {
            System.out.println("\nPrima 0 em qualquer momento para cancelar a operação.");
            LocalDateTime inicio = lerData("Data inicio (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerData("Data fim (dd-MM-yyyy HH:mm): ");

            double media = empresa.calcularDistanciaMedia(inicio, fim);
            System.out.println("A distância médias das viagens foi: " + String.format("%.2f", media) + " Kms");
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }

    /**
     * Lista os clientes que efetuaram viagens com distância compreendida num determinado intervalo.
     */
    private static void estatClientesPorIntervaloKms() {
        try {
            System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
            double min = lerDouble("\nKms Mínimos: ");
            double max = lerDouble("Kms Maximos: ");

            ArrayList<Cliente> lista = empresa.getClientesPorIntervaloKms(min, max);
            if (lista.isEmpty()) {
                System.out.println("Nenhum cliente encontrado nesse intervalo");
            } else {
                System.out.println("\n--- Clientes com viagens entre " + min + " e " + max + " Kms ---");
                for (Cliente cliente : lista) {
                    double totalKms = empresa.calcularTotalKmsCliente(cliente.getNif());
                    System.out.println("[" + cliente.getNome() + " | Nif: " + cliente.getNif() + " | Total: " + totalKms + " Kms]");
                }
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");

        }
    }

    /**
     * Apresenta o histórico de viagens de um cliente filtrado por datas.
     * Pergunta se o utilizador quer ver a lista de clientes.
     */
    private static void estatHistoricoClientePorDatas() {
        try {
            System.out.println("Prima 0 em qualquer momento para cancelar a operação.");
            String verLista = lerTexto("\nDeseja ver a lista de Clientes? (S/N): ");
            if (verLista.equalsIgnoreCase("S")) {
                ArrayList<Cliente> lista = empresa.getClientes();

                if (lista.isEmpty()) {
                    System.out.println("\n>> Não existem clientes registados.");
                    return;
                }
                System.out.println("\n--- Clientes Registados ---");
                for (Cliente cliente : lista) {
                    System.out.println("- " + cliente.getNome() + " | NIF: " + cliente.getNif());
                }
            }

            int nif = lerInteiro("NIF do Cliente: ");
            Cliente cliente = empresa.procurarCliente(nif);

            if (cliente != null) {
                LocalDateTime inicio = lerData("Data inicio (dd-MM-yyyy HH:mm): ");
                LocalDateTime fim = lerData("Data fim (dd-MM-yyyy HH:mm): ");

                ArrayList<Viagem> viagensCliente = empresa.getViagensClientePorDatas(nif, inicio, fim);
                if (viagensCliente.isEmpty()) {
                    System.out.println("Nenhuma viagem registada nesse intervalo");
                } else {
                    System.out.println("--- Histórico de " + cliente.getNome() + " ---");
                    for (Viagem viagem : viagensCliente) {
                        System.out.println(viagem);
                    }
                }
            } else {
                System.out.println("Erro: Nenhum cliente encontrado com esse NIF.");
            }
        } catch (OperacaoCanceladaException e) {
            System.out.println("Operacao cancelada.");
        }
    }


// =======================================================
//                MÉTODOS AUXILIARES (SELEÇÃO)
// =======================================================

    /**
     * Metodo auxiliar para selecionar um condutor disponível.
     *
     * @param inicio Data início.
     * @param fim    Data fim.
     * @return Condutor selecionado ou null.
     */
    private static Condutor selecionarCondutorDisponivel(LocalDateTime inicio, LocalDateTime fim) throws
            OperacaoCanceladaException {
        ArrayList<Condutor> condutoresLivres = empresa.getCondutoresDisponiveis(inicio, fim);

        if (condutoresLivres.isEmpty()) {
            System.out.println("Sem condutores disponíveis para este horário.");
            return null;
        }

        System.out.println("\n>> Existem " + condutoresLivres.size() + " condutores livres.");
        String verListaCondutores = lerTexto("Ver lista? (S/N): ");
        if (verListaCondutores.equalsIgnoreCase("S")) {
            for (Condutor condutor : condutoresLivres) {
                System.out.println("[ Condutor: " + condutor.getNome() + " | NIF: " + condutor.getNif() + " ]");
            }
        }
        while (true) {
            int nifCondutor = lerInteiro("NIF do Condutor: ");
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
     * Metodo auxiliar para selecionar um cliente disponível.
     *
     * @param inicio Data início.
     * @param fim    Data fim.
     * @return Cliente selecionado ou null.
     */
    private static Cliente selecionarClienteDisponivel(LocalDateTime inicio, LocalDateTime fim) throws
            OperacaoCanceladaException {
        ArrayList<Cliente> clientesLivres = empresa.getClientesDisponiveis(inicio, fim);

        if (clientesLivres.isEmpty()) {
            System.out.println("Sem clientes disponíveis.");
            return null;
        }

        System.out.println("\n>> Existem " + clientesLivres.size() + " clientes livres.");
        String verListaClientes = lerTexto("Ver lista? (S/N): ");
        if (verListaClientes.equalsIgnoreCase("S")) {
            for (Cliente cliente : clientesLivres) {
                System.out.println("[ Cliente: " + cliente.getNome() + " | NIF: " + cliente.getNif() + " ]");
            }
        }
        while (true) {
            int nifCliente = lerInteiro("NIF do Cliente: ");
            Cliente cliente = empresa.procurarCliente(nifCliente);

            if (cliente == null) {
                System.out.println("Erro: Nenhum cliente encontrado.");
            } else if (!clientesLivres.contains(cliente)) {
                System.out.println("Erro: " + cliente.getNome() + "já tem uma viagem nesse horário.");
            } else {
                return cliente;
            }
        }
    }

    /**
     * Metodo auxiliar para selecionar uma viatura disponível.
     *
     * @param inicio Data início.
     * @param fim    Data fim.
     * @return Viatura selecionada ou null.
     */
    private static Viatura selecionarViaturaDisponivel(LocalDateTime inicio, LocalDateTime fim) throws
            OperacaoCanceladaException {
        ArrayList<Viatura> viaturasLivres = empresa.getViaturasDisponiveis(inicio, fim);

        if (viaturasLivres.isEmpty()) {
            System.out.println("Sem viaturas disponíveis para este horário.");
            return null;
        }

        System.out.println("\n>> Existem " + viaturasLivres.size() + " viaturas livres.");
        String verViaturas = lerTexto("Ver lista? (S/N): ");

        if (verViaturas.equalsIgnoreCase("S")) {
            for (Viatura viatura : viaturasLivres) {
                System.out.println("[ Matricula:  " + viatura.getMatricula() + " |  Marca: " + viatura.getMarca() + "  | Modelo: " + viatura.getModelo() + " ]");
            }
        }
        while (true) {
            String matricula = lerTexto("Matricula da Viatura: ");
            Viatura viatura = empresa.procurarViatura(matricula);

            if (viatura == null) {
                System.out.println("Erro: Nenhuma viatura encontrada.");
            } else if (!viaturasLivres.contains(viatura)) {
                System.out.println("Erro: A viatura (Matricula: " + viatura.getMatricula() + ") já tem uma viagem nesse horário.");
            } else {
                return viatura;
            }
        }
    }

// =======================================================
//         MÉTODOS EXCLUSIVO PARA MENUS (ler)
// =======================================================

    private static int lerOpcaoMenu(String msg) {
        System.out.println(msg);
        while (!scanner.hasNextInt()) {
            System.out.println("Valor inválido. Tente novamente.");
            scanner.next();
        }

        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
    }


    private static boolean confirmarSaida() {
        System.out.print("\n>> Deseja cancelar a operação e voltar ao menu? (S/N): ");
        String resp = scanner.nextLine().trim();
        return resp.equalsIgnoreCase("S");
    }

// =======================================================
//        MÉTODOS AUXILIARES PARA INSERÇÃO DE DADOS
//                   ((ler) com cancelamento)
// =======================================================

    /**
     * Lê uma linha de texto da consola, garantindo que não está vazia.
     * <p>
     * O método entra num ciclo até que o utilizador insira um texto válido.
     * Se o utilizador inserir "0", é solicitada uma confirmação para cancelar a operação.
     * </p>
     *
     * @param msg A mensagem a apresentar ao utilizador antes da leitura.
     * @return A String introduzida pelo utilizador (não vazia).
     * @throws OperacaoCanceladaException Se o utilizador digitar "0" e confirmar a saída.
     */
    private static String lerTexto(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine();

            //Se for 0 pergunta se quer cancelar
            if (input.trim().equals("0")) {
                if (confirmarSaida()) {
                    throw new OperacaoCanceladaException();
                } else {
                    System.out.println("Operação retomada.");
                    continue;
                }
            }
            if (input.isEmpty()) {
                System.out.println("Erro: O campo não pode estar vazio.");
                continue;
            }
            return input;

        }
    }

    /**
     * Lê um número inteiro da consola, garantindo que o input é um número válido.
     * <p>
     * O método protege contra erros de formato (ex: letras em vez de números).
     * Se o utilizador inserir "0", é solicitada uma confirmação para cancelar a operação.
     * </p>
     *
     * @param msg A mensagem a apresentar ao utilizador antes da leitura.
     * @return O número inteiro introduzido.
     * @throws OperacaoCanceladaException Se o utilizador digitar "0" e confirmar a saída.
     */
    private static int lerInteiro(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine();

            //1. Verifica o cancelamento
            if (input.trim().equals("0")) {
                if (confirmarSaida()) {
                    throw new OperacaoCanceladaException();
                } else {
                    continue;
                }
            }

            //2. Tenta converter para número
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Valor inválido. Insira um número inteiro.");
            }
        }
    }

    /**
     * Lê um valor decimal (double), garantindo que o input é válido.
     *
     * @param msg A mensagem a apresentar.
     * @return O valor double introduzido.
     */
    private static double lerDouble(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine();

            if (input.equals("0")) {
                if (confirmarSaida()) {
                    throw new OperacaoCanceladaException();
                } else {
                    continue;
                }
            }
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Erro: valor inválido.");
            }
        }
    }

    /**
     * Lê e converte uma data no formato "dd-MM-yyyy HH:mm".
     * Pede novamente se o formato estiver errado.
     *
     * @param msg A mensagem a apresentar.
     * @return O objeto LocalDateTime validado.
     */
    private static LocalDateTime lerData(String msg) throws OperacaoCanceladaException {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine();
            if (input.trim().equals("0")) {
                if (confirmarSaida()) {
                    throw new OperacaoCanceladaException();
                } else {
                    continue;
                }
            }
            try {
                return LocalDateTime.parse(input, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido! Use: dd-MM-yyyy HH:mm");
            }
        }
    }

// =======================================================
//            CLASSE EXCEÇÃO PERSONALIDADA
// =======================================================

    private static class OperacaoCanceladaException extends Exception {
    }

// =======================================================
//                    DADOS DE TESTE
// =======================================================

    /**
     * Preenche o sistema com um conjunto robusto de dados de teste.
     * Cria 3 Viaturas, 3 Clientes, 3 Condutores e 2 Viagens históricas.
     */
    public static void inicializarDadosTeste() {
        System.out.println("A gerar dados de teste completos...");
        try {
            // --- 1. VIATURAS ---
            Viatura viatura1 = new Viatura("AA-00-BB", "Toyota", "Corolla", 2020);
            Viatura viatura2 = new Viatura("XX-99-ZZ", "Tesla", "Model 3", 2024);
            Viatura viatura3 = new Viatura("CC-11-DD", "Renault", "Clio", 2018);

            empresa.adicionarViatura(viatura1);
            empresa.adicionarViatura(viatura2);
            empresa.adicionarViatura(viatura3);

            // --- 2. CLIENTES ---
            Cliente cliente1 = new Cliente("Joao Silva", 100000001, 910000001, "Porto", 11111111);
            Cliente cliente2 = new Cliente("Ana Pereira", 100000002, 910000002, "Lisboa", 22222222);
            Cliente cliente3 = new Cliente("Carlos Sousa", 100000003, 910000003, "Braga", 33333333);

            empresa.adicionarCliente(cliente1);
            empresa.adicionarCliente(cliente2);
            empresa.adicionarCliente(cliente3);

            // --- 3. CONDUTORES ---
            Condutor motorista1 = new Condutor("Maria Santos", 200000001, 930000001, "Porto", 44444444, "C-001", 12345);
            Condutor motorista2 = new Condutor("Pedro Gomes", 200000002, 930000002, "Gaia", 55555555, "C-002", 67890);
            Condutor motorista3 = new Condutor("Luisa Lima", 200000003, 930000003, "Matosinhos", 66666666, "C-003", 11223);

            empresa.adicionarCondutor(motorista1);
            empresa.adicionarCondutor(motorista2);
            empresa.adicionarCondutor(motorista3);

            // --- 4. VIAGENS (Histórico - Ontem) ---
            LocalDateTime ontem = LocalDateTime.now().minusDays(1);

            // Viagem 1: Maria (motorista1) levou o João (cliente1) no Toyota (viatura1)
            Viagem v1 = new Viagem(motorista1, cliente1, viatura1,
                    ontem.withHour(10).withMinute(0),
                    ontem.withHour(10).withMinute(30),
                    "Casa da Música", "Ribeira", 5.5, 8.50);

            // Viagem 2: Pedro (motorista2) levou a Ana (cliente2) no Tesla (viatura2)
            Viagem v2 = new Viagem(motorista2, cliente2, viatura2,
                    ontem.withHour(14).withMinute(0),
                    ontem.withHour(15).withMinute(0),
                    "Aeroporto", "Hotel", 20.0, 25.00);

            empresa.adicionarViagem(v1);
            empresa.adicionarViagem(v2);

            System.out.println("\n>> Dados carregados: 3 Viaturas, 3 Clientes, 3 Condutores, 2 Viagens");

        } catch (Exception e) {
            System.out.println("Erro ao carregar dados de teste: " + e.getMessage());
        }
    }
}
