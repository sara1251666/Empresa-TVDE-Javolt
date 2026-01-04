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
     * */
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Formatador de data padrão para o sistema (dd-MM-yyyy HH:mm).
     * */
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /**
     * Inicia o ciclo de vida da aplicação.
     * <p>
     * 1. Deteta empresas existentes (pastas Logs_).
     * 2. Permite criar nova ou carregar existente (Menu Visual).
     * 3. Inicializa a Empresa e carrega dados.
     * </p>
     */
    public static void iniciar(){

        System.out.println("\n|--------------------------------|");
        System.out.println("|    SISTEMA DE GESTÃO TVDE      |");
        System.out.println("|--------------------------------|");

        String nomeEmpresaSelecionada = "";

        //1. Procurar empresas existentes (Pastas "Logs_")
        ArrayList<String> empresasExistentes = listarEmpresasDetetadas();

        if (empresasExistentes.isEmpty()){
            //Caso 1: Não existem empresas, força a criação de uma nova.
            System.out.println(">> Nenhuma empresa detetada no sistema.");
            System.out.println("|----------------------------------|");
            System.out.println("|      CRIAÇÃO DE NOVA EMPRESA     |");
            System.out.println("|----------------------------------|");
            nomeEmpresaSelecionada = lerTexto("Introduza o nome: ");
        } else {
            //Caso 2: Existem empresas, permite escolher ou criar uma nova.
            System.out.println("\n|----------------------------------|");
            System.out.println("|        SELEÇÃO DE EMPRESA        |");
            System.out.println("|----------------------------------|");
            System.out.println("| 1 - Criar Nova Empresa           |");
            System.out.println("| 2 - Carregar Empresa Existente   |");
            System.out.println("|----------------------------------|");
            int opcao = lerInteiro("Escolha uma opção: ");

            if (opcao == 2){
                //Listar opções para carregar
                System.out.println("\n|----------------------------------|");
                System.out.println("|       EMPRESAS ENCONTRADAS       |");
                System.out.println("|----------------------------------|");
                for (int i = 0; i < empresasExistentes.size(); i++) {
                    System.out.println((i + 1) + " - " +  empresasExistentes.get(i));
                }

                System.out.println("|----------------------------------|");
                int index = lerInteiro("Escolha o número da empresa: ") - 1;

                if (index >= 0 && index < empresasExistentes.size()) {
                    nomeEmpresaSelecionada = empresasExistentes.get(index);
                } else {
                    System.out.println("Opção inválida. A criar nova empresa por defeito.");
                    nomeEmpresaSelecionada = lerTexto("Nome da nova empresa: ");
                }
            } else {
                //Opção 1 ou inválida: Criar nova.
                System.out.println("\n|----------------------------------|");
                System.out.println("|      CRIAÇÃO DE NOVA EMPRESA     |");
                System.out.println("|----------------------------------|");
                nomeEmpresaSelecionada = lerTexto("Nome da nova empresa: ");
            }
        }

        //2. Inicializar a instância da Empresa com o nome definido
        empresa = new Empresa(nomeEmpresaSelecionada);
        System.out.println("Bem vindo à gestão da empresa: "+ nomeEmpresaSelecionada.toUpperCase());

        // 3. Perguntar sobre o carregamento de dados (Persistência)
        String respostaCarregar = lerTexto("Deseja carregar os dados guardados? (S/N): ");

        if (respostaCarregar.equalsIgnoreCase("S")) {
            System.out.println("A carregar dados...");
            empresa.carregarDados();
        } else {
            System.out.println("A iniciar com uma base de dados limpa...");
        }

        // 4. Verificação de dados iniciais
        // Se a base de dados estiver vazia (nova empresa ou sem ficheiros), sugere dados de teste.
        if (empresa.getViaturas().isEmpty() && empresa.getCondutores().isEmpty()){
            System.out.println("Base de dados vazia. A gerar dados de teste...");
            inicializarDadosTeste();
        }

        // 5. Executar o Loop do Menu Principal
        displayMenuPrincipal();

        // 6. Processo de Encerramento e Gravação
        //Só chega aqui quando o utilizador escolhe a opção "0-Sair".
        String respostaGravar = lerTexto("Deseja gravar os dados antes de sair? (S/N): ");
        if(respostaGravar.equalsIgnoreCase("S")) {
            System.out.println("A gravar alterações em Logs_" + nomeEmpresaSelecionada + "...");
            empresa.gravarDados();
            System.out.println("Dados guardados com sucesso.");
        }else {
            System.out.println("As alterações não foram guardadas.");
        }
        System.out.println("Até logo!");
    }

    /**
     * Procura na diretoria do projeto por pastas que comecem por "Logs_".
     *
     * @return Uma lista com os nomes das empresas encontradas (sem o prefixo "Logs_").
     */
    private static ArrayList<String> listarEmpresasDetetadas() {
        ArrayList<String> nomesEmpresas = new ArrayList<>();
        File pastaAtual = new File("."); // "." Representa a pasta atual do projeto.
        File[] ficheiros = pastaAtual.listFiles();

        if (ficheiros != null) {
            for (File ficheiro : ficheiros) {
                //Verifica se é uma pasta e se começa por "Logs_"
                if(ficheiro.isDirectory() && ficheiro.getName().startsWith("Logs_")){
                    //Extrai o nome real (remove "Logs_")
                    String nomeReal = ficheiro.getName().substring(5); // 5 é o tamanho de "Logs_".
                    nomesEmpresas.add(nomeReal);
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
        int opcao = 0;
        do {
            System.out.println("\n|--------------------------------|");
            System.out.println("|    TVDE - MENU PRINCIPAL       |");
            System.out.println("|--------------------------------|");
            System.out.println("| 1 - Gerir Viaturas             |");
            System.out.println("| 2 - Gerir Condutores           |");
            System.out.println("| 3 - Gerir Clientes             |");
            System.out.println("| 4 - Gerir Viagens (Histórico)  |");
            System.out.println("| 5 - Gerir Reservas             |");
            System.out.println("| 6 - Relatórios/Estatísticas    |");
            System.out.println("| 0 - Sair                       |");
            System.out.println("|--------------------------------|");
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> menuCRUD("Viaturas");
                case 2 -> menuCRUD("Condutores");
                case 3 -> menuCRUD("Clientes");
                case 4 -> menuViagens();
                case 5 -> menuReservas();
                case 6 -> menuEstatisticas();
                case 0 -> System.out.println("A encerrar sistema...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    /**
     * Apresenta o menu genérico para operações CRUD (Create, Read, Update, Delete).
     * Redireciona para o metodo de processamento específico consoante a entidade escolhida.
     *
     * @param entidade Nome da entidade a gerir (ex: "Viaturas", "Clientes") para personalização do título.
     */
    private static void menuCRUD(String entidade) {
        int opcao = 0;
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
            opcao = lerInteiro("Escolha a opção: ");

            switch (entidade) {
                case "Viaturas" -> processarViaturas(opcao);
                case "Condutores" -> processarCondutores(opcao);
                case "Clientes" -> processarClientes(opcao);
            }
        } while (opcao != 0);
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
        switch (opcao) {
            case 1 -> { // CREATE
                String mat = lerTexto("Matrícula: ");
                String marca = lerTexto("Marca: ");
                String modelo = lerTexto("Modelo: ");
                int ano = lerInteiro("Ano de Fabrico: ");

                Viatura v = new Viatura(mat, marca, modelo, ano);
                if (empresa.adicionarViatura(v)) {
                    System.out.println("Viatura adicionada com sucesso!");
                } else {
                    System.out.println("Erro: Viatura com esta matrícula já existe.");
                }
            }
            case 2 -> { // READ
                ArrayList<Viatura> listaViaturas = empresa.getViaturas();
                if (listaViaturas.isEmpty()) {
                    System.out.println("Nenhuma viatura registada.");
                } else {
                    System.out.println("--- Lista de Viaturas ---");
                    int i = 1; //Contador inicaliza em 1
                    for (Viatura v : listaViaturas) {
                        System.out.println(i + ". " + v.toString());
                        i++;
                    }
                }
            }
            case 3 -> { // UPDATE
                String mat = lerTexto("Insira a matrícula da viatura a editar: ");
                Viatura v = empresa.procurarViatura(mat);
                if (v != null) {
                    System.out.println("Dados atuais: " + v);
                    v.setMarca(lerTexto("Nova Marca: "));
                    v.setModelo(lerTexto("Novo Modelo: "));
                    v.setAnoFabrico(lerInteiro("Novo Ano: "));
                    System.out.println("Viatura atualizada.");
                } else {
                    System.out.println("Viatura não encontrada.");
                }
            }
            case 4 -> { // DELETE
                String mat = lerTexto("Matrícula a eliminar: ");
                if (empresa.removerViatura(mat)) {
                    System.out.println("Viatura removida.");
                }
            }
        }
    }

    /**
     * Processa as operações CRUD específicas para a entidade {@link Condutor}.
     *
     * @param opcao A opção selecionada pelo utilizador no menu CRUD.
     */
    private static void processarCondutores(int opcao) {
        switch (opcao) {
            case 1 -> { // CREATE
                System.out.println("--- Novo Condutor ---");
                try {
                    int nif = lerInteiro("NIF (9 dígitos): ");
                    String nome = lerTexto("Nome: ");
                    int tel = lerInteiro("Telemóvel: ");
                    String morada = lerTexto("Morada: ");
                    int cc = lerInteiro("Cartão Cidadão: ");
                    String carta = lerTexto("Carta Condução: ");
                    int ss = lerInteiro("Segurança Social: ");

                    Condutor c = new Condutor(nome, nif, tel, morada, cc, carta, ss);

                    if (empresa.adicionarCondutor(c)) {
                        System.out.println("Sucesso: Condutor registado!");
                    } else {
                        System.out.println("Erro: Já existe um condutor com esse NIF.");
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println("\n!!! ERRO DE VALIDAÇÃO !!!");
                    System.out.println(e.getMessage());
                    System.out.println("O registo foi cancelado. Tente novamente.");
                }
            }
            case 2 -> { // READ
                ArrayList<Condutor> listaCondutores = empresa.getCondutores();
                if (listaCondutores.isEmpty()) {
                    System.out.println("Não há condutores registados.");
                } else {
                    System.out.println("--- Lista de Condutores ---");
                    int i = 1; // Contador inicializa em 1.
                    for (Condutor c : listaCondutores) {
                        System.out.println(i + ". " + c.toString());
                        i++;
                    }
                }
            }
            case 3 -> { // UPDATE
                int nif = lerInteiro("NIF do condutor a editar: ");
                Condutor c = empresa.procurarCondutor(nif);
                if (c != null) {
                    c.setNome(lerTexto("Novo Nome: "));
                    c.setTel(lerInteiro("Novo Telemóvel: "));
                    c.setMorada(lerTexto("Nova Morada: "));
                    System.out.println("Condutor atualizado.");
                } else {
                    System.out.println("Condutor não encontrado.");
                }
            }
            case 4 -> { // DELETE
                int nif = lerInteiro("NIF a eliminar: ");
                if (empresa.removerCondutor(nif)) System.out.println("Condutor removido.");
            }
        }
    }

    /**
     * Processa as operações CRUD específicas para a entidade {@link Cliente}.
     *
     * @param opcao A opção selecionada pelo utilizador no menu CRUD.
     */
    private static void processarClientes(int opcao) {
        switch (opcao) {
            case 1 -> { // CREATE
                System.out.println("--- Novo Cliente ---");
                try {
                    int nif = lerInteiro("NIF (9 dígitos): ");
                    String nome = lerTexto("Nome: ");
                    int tel = lerInteiro("Telemóvel: ");
                    String morada = lerTexto("Morada: ");
                    int cc = lerInteiro("Cartão Cidadão: ");

                    Cliente c = new Cliente(nome, nif, tel, morada, cc);

                    if (empresa.adicionarCliente(c)) {
                        System.out.println("Sucesso: Cliente registado!");
                    } else {
                        System.out.println("Erro: Já existe um cliente com esse NIF.");
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println("\n!!! ERRO DE VALIDAÇÃO !!!");
                    System.out.println(e.getMessage());
                    System.out.println("O registo foi cancelado.");
                }
            }
            case 2 -> { // READ
                ArrayList<Cliente> listaClientes = empresa.getClientes();
                if (listaClientes.isEmpty()) {
                    System.out.println("--- Não há clientes registados.");
                } else {
                    System.out.println("--- Lista de Clientes ---");
                    int i = 1; // Contador inicia a 1
                    for (Cliente c : listaClientes) {
                        // Imprime o número seguido do objeto.
                        System.out.println(i + ". " + c.toString());
                        i++;
                    }
                }
            }
            case 3 -> { // UPDATE
                int nif = lerInteiro("NIF do cliente a editar: ");
                Cliente c = empresa.procurarCliente(nif);
                if (c != null) {
                    c.setNome(lerTexto("Novo Nome: "));
                    c.setTel(lerInteiro("Novo Telemóvel: "));
                    c.setMorada(lerTexto("Nova Morada: "));
                    System.out.println("Cliente atualizado.");
                } else {
                    System.out.println("Cliente não encontrado.");
                }
            }
            case 4 -> { // DELETE
                int nif = lerInteiro("NIF a eliminar: ");
                if (empresa.removerCliente(nif)) System.out.println("Cliente removido.");
            }
        }
    }

    // =======================================================
    //            MENU VIAGENS E RESERVAS
    // =======================================================

    /**
     * Exibe o menu específico para gestão de Viagens.
     */
    private static void menuViagens() {
        int op = 0;
        do {
            System.out.println("\n|---------------------------------------|");
            System.out.println("|           GESTÃO DE VIAGENS           |");
            System.out.println("|---------------------------------------|");
            System.out.println("| 1 - Registar Nova Viagem (Imediata)   |");
            System.out.println("| 2 - Listar Histórico de Viagens       |");
            System.out.println("| 3 - Apagar uma Viagem do Histórico    |");
            System.out.println("| 0 - Voltar                            |");
            System.out.println("|---------------------------------------|");
            op = lerInteiro("Escolha uma opção: ");

            switch (op) {
                case 1 -> tratarRegistarViagem();
                case 2 -> tratarListarViagens();
                case 3 -> tratarEliminarViagem();
            }
        }while  (op != 0);
    }

    /**
     * Exibe o menu específico para gestão de Reservas.
     */
    private static void menuReservas(){
        int op = 0;
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
            op = lerInteiro("Escolha uma opção: ");

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
        System.out.println("--- Nova Viagem ---");

        //1. Pede ao utilizador as datas. É o filtro principal deste metodo.
        LocalDateTime inicio = lerData("Início da Viagem(dd-MM-yyyy): ");
        LocalDateTime fim = lerData("Fim da Viagem(dd-MM-yyyy): ");

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
        }
    }

    /**
     * Lista todas as viagens armazenadas no histórico da empresa.
     */
    private static void tratarListarViagens(){
        if (empresa.getViagens().isEmpty()) {
                System.out.println("Sem viagens registadas!");
        } else {
                System.out.println("--- Histórico de Viagens ---");
                for (Viagem v : empresa.getViagens()) {
                    System.out.println(v);
                }
        }
    }

    /**
     * Permite ao utilizador eliminar uma viagem do histórico.
     */
    private static void tratarEliminarViagem(){
        ArrayList<Viagem> viagens = empresa.getViagens();
        if (viagens.isEmpty()) {
            System.out.println("Não existem viagens no histórico para eliminar.");
        } else {
            System.out.println("--- Escolha a Viagem a Eliminar ---");
            //Listar as viagens para o utilizador saber qual o número escolher.
            for (int i = 0; i < viagens.size(); i++) {
                System.out.println((i + 1) + ". " + viagens.get(i));
            }

            int index = lerInteiro("Número da viagem a apagar (0 para cancelar): ") - 1;

            if (index >= 0 && index < viagens.size()) {
                Viagem viagem = viagens.get(index);
                if (empresa.removerViagens(viagem)) {
                    System.out.println("Viagem removida com sucesso!");
                } else {
                    System.out.println("Erro ao remover Viagem.");
                }
            } else {
                System.out.println("Operação Cancelada.");
            }

        }
    }

    /**
     * Recolhe dados para criar uma nova {@link Reserva} e adiciona-a ao sistema.
     */
    private static void tratarCriarReserva(){
        System.out.println("--- Nova Reserva ---");
        int nifCliente = lerInteiro("NIF Cliente: ");
        Cliente cliente = empresa.procurarCliente(nifCliente);

        if (cliente != null) {
            LocalDateTime inicio = lerData("Data/Hora Reserva (dd-MM-yyyy HH:mm): ");
            String origem = lerTexto("Origem: ");
            String destino = lerTexto("Destino: ");
            double kms = lerDouble("Kms estimados: ");

            Reserva reserva = new Reserva(cliente, inicio, origem, destino, kms);
            empresa.adicionarReserva(reserva);
            System.out.println("Reserva registada com sucesso!");
        } else {
            System.out.println("Cliente não encontrado");
        }
    }

    /**
     * Lista todas as reservas pendentes.
     */
    private static void tratarListarReservas(){
        ArrayList<Reserva> reservas = empresa.getReservas();
        if (reservas.isEmpty()) {
            System.out.println("Sem nenhuma reserva pendente!");
        } else{
            int i = 1;
            for (Reserva reserva : reservas) {
                System.out.println(i + ". " + reserva);
                i++;
            }
        }
    }

    /**
     * Consulta e lista as reservas associadas a um cliente específico pelo NIF.
     */
    private static void tratarConsultarReservasCliente(){
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
    }

    /**
     * Permite alterar os dados de uma reserva existente de um determinado cliente.
     */
    private static void tratarAlterarReserva(){
        System.out.println("--- Altera Reserva de Cliente ---");

        //1.Pedir o NIF para filtrar
        int nifCliente = lerInteiro("NIF Cliente: ");
        Cliente cliente = empresa.procurarCliente(nifCliente);

        if (cliente != null) {
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
        int index = lerInteiro("Escolha a Reserva a Alterar (0 para voltar)?") - 1;
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
                case 0 -> {
                    System.out.println("Alteração Cancelada.");
                }
                default -> {
                    System.out.println("Opção Inválida.");
                }
            }
        } else {
            if (index != -1) {
                System.out.println("Opção inválida.");
            }
        }
    }

    /**
     * Converte uma reserva existente numa viagem efetiva, atribuindo condutor e viatura.
     */
    private static void tratarConverterReserva(){
        ArrayList<Reserva> reservas = empresa.getReservas();
        if (reservas.isEmpty()) {
            System.out.println("Sem nenhuma reserva para converter em viagem");
            return;
        }
        // 1. Listar para escolher
        System.out.println("--- Escolha a Reserva a converter");
        for (int i= 0; i< reservas.size(); i++) {
            System.out.println((i + 1) + ". " + reservas.get(i));
        }
        //2. Selecionar
        int index = lerInteiro("Número da reserva: ") - 1;
        if (index >= 0 && index < reservas.size()) {
            Reserva resSelecionada = reservas.get(index);
            System.out.println("Reserva Selecionada: " + resSelecionada.getCliente().getNome() + " - " + resSelecionada.getDataHoraInicio().format(dateTimeFormatter));

            int nifCondutor = lerInteiro("Atribuir Condutor (NIF): ");
            Condutor condutor = empresa.procurarCondutor(nifCondutor);

            String matricula = lerTexto("Atribuir Viatura (Matrícula): ");
            Viatura viatura = empresa.procurarViatura(matricula);

            double custo = lerDouble("Custo Final (€): ");

            if (condutor != null && viatura != null) {
                boolean sucesso = empresa.converterReservaEmViagem(resSelecionada, condutor, viatura, custo);
                if (sucesso){
                    System.out.println("Reserva convertida com sucesso!");
                } else {
                    System.out.println("Erro ao converter Reserva.");
                }
            } else {
                System.out.println("Condutor ou Viatura inválidos.");
            }
        } else {
            System.out.println("Opção inválida!");
        }
    }

    /**
     * Permite eliminar uma reserva pendente.
     */
    private static void tratarEliminarReserva () {
        ArrayList<Reserva> reservas = empresa.getReservas();
        if (reservas.isEmpty()) {
            System.out.println("Não existem reservas para eliminar.");
        } else {
            System.out.println("--- Escolha a Reserva a Eliminar ---");
            for (int i = 0; i < reservas.size(); i++) {
                System.out.println((i + 1) + ". " + reservas.get(i));
            }
            int index = lerInteiro("Numero da reserva a apagar (0 para cancelar): ") - 1;

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
        }
    }

    // =======================================================
    //            MENU ESTATÍSTICAS
    // =======================================================

    /**
     * Exibe o menu de Estatísticas e Relatórios.
     */
    private static void menuEstatisticas() {
        int op = 0;

        do {
            System.out.println("\n|----------------------------------------------|");
            System.out.println("|         RELATÓRIOS E ESTATÍSTICAS            |");
            System.out.println("|----------------------------------------------|");
            System.out.println("| 1 - Total faturado por condutor (por datas)  |");
            System.out.println("| 2 - Lista de clientes de uma viatura         |");
            System.out.println("| 3 - Destino mais solicitado (por datas)      |");
            System.out.println("| 4 - Distância média das viagens (por datas)  |");
            System.out.println("| 5 - Clientes com viagens por intervalo Kms   |");
            System.out.println("| 6 - Histórico de Viagens de Cliente (datas)  |");
            System.out.println("| 0 - Voltar                                   |");
            System.out.println("|----------------------------------------------|");
            op = lerInteiro("Escolha uma opção: ");

            switch (op) {
                case 1 -> tratarFaturacaoCondutor();
                case 2 -> tratarClientesViatura();
                case 3 -> tratarDestinoMaisSolicitado();
                case 4 -> tratarDistanciaMedia();
                case 5 -> tratarClientesPorIntervaloKms();
                case 6 -> tratarHistoricoClientePorDatas();
            }
        } while (op != 0);
    }

    // =======================================================
    //        MÉTODOS AUXILIARES DE ESTATÍSTICAS
    // =======================================================

    /**
     * Calcula e apresenta a faturação de um condutor num intervalo de datas.
     */
    private static void tratarFaturacaoCondutor(){
        int nif = lerInteiro("NIF do Condutor: ");
        Condutor condutor = empresa.procurarCondutor(nif);
        if (condutor != null) {
            LocalDateTime inicio = lerData("Data inicio (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerData("Data fim (dd-MM-yyyy HH:mm): ");

            double total = empresa.calcularFaturacaoCondutor(nif, inicio, fim);
            System.out.println("O Condutor " + condutor.getNome() + " faturou: " + total + " € nesse periodo.");
        } else {
            System.out.println("Condutor não encontrado.");
        }
    }

    /**
     * Lista os clientes distintos que viajaram numa determinada viatura.
     */
    private static void tratarClientesViatura(){
        String matricula = lerTexto("Matricula da Viatura: ");
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
    }

    /**
     * Apresenta o destino mais solicitado no sistema (considerando Viagens e Reservas) num intervalo de datas.
     */
    private static void tratarDestinoMaisSolicitado(){
        LocalDateTime inicio = lerData("Data inicio (dd-MM-yyyy HH:mm): ");
        LocalDateTime fim = lerData("Data fim (dd-MM-yyyy HH:mm): ");

        String topDestino = empresa.getDestinoMaisSolicitado(inicio, fim);
        System.out.println(" O destino mais popular nesse período é: " + topDestino);
    }

    /**
     * Calcula e apresenta a média de Kms das viagens realizadas num intervalo de datas.
     */
    private static void tratarDistanciaMedia(){
        LocalDateTime inicio = lerData("Data inicio (dd-MM-yyyy HH:mm): ");
        LocalDateTime fim = lerData("Data fim (dd-MM-yyyy HH:mm): ");

        double media = empresa.calcularDistanciaMedia(inicio, fim);
        System.out.println("A distância médias das viagens foi: " + String.format("%.2f", media) + " Kms");
    }

    /**
     * Lista os clientes que efetuaram viagens com distância compreendida num determinado intervalo.
     */
    private static void tratarClientesPorIntervaloKms(){
        double min = lerDouble("Kms Mínimos: ");
        double max = lerDouble("Kms Maximos: ");

        ArrayList<Cliente> lista = empresa.getClientesPorIntervaloKms(min, max);
        if (lista.isEmpty()) {
            System.out.println("Nenhum cliente encontrado nesse intervalo");
        } else {
            System.out.println("-- Clientes com viagens entre " + min + " e " + max + " Kms ---");
            for (Cliente cliente : lista) {
                System.out.println("- " + cliente.getNome());
            }
        }
    }

    /**
    * Apresenta o histórico de viagens de um cliente filtrado por datas.
    */
    private static void tratarHistoricoClientePorDatas(){
        int nif = lerInteiro("NIF do Cliente: ");
        Cliente  cliente = empresa.procurarCliente(nif);

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
        }else  {
            System.out.println("Nenhum cliente encontrado.");
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
    private static Condutor selecionarCondutorDisponivel(LocalDateTime inicio, LocalDateTime fim) {
        ArrayList<Condutor> condutoresLivres = empresa.getCondutoresDisponiveis(inicio, fim);

        if (condutoresLivres.isEmpty()) {
            System.out.println("Sem condutores disponíveis para este horário.");
            return null;
        }

        System.out.println(">> Existem " + condutoresLivres.size() + " condutores livres.");
        String verListaCondutores = lerTexto("Ver lista? (S/N): ");
        if (verListaCondutores.equalsIgnoreCase("S")) {
            for (Condutor condutor : condutoresLivres) {
                System.out.println("- " + condutor.getNome() + " | NIF: " + condutor.getNif());
            }
        }

        int nifCondutor = lerInteiro("NIF do Condutor: ");
        Condutor condutor = empresa.procurarCondutor(nifCondutor);

        if (condutor == null) {
            System.out.println("Erro: Nenhum condutor encontrado.");
            return null;
        }
        if (!condutoresLivres.contains(condutor)) {
            System.out.println("Erro: Condutor " + condutor.getNome() + " já tem uma viagem nesse horário.");
            return null;
        }
        return condutor;
    }

    /**
     * Metodo auxiliar para selecionar um cliente disponível.
     *
     * @param inicio Data início.
     * @param fim    Data fim.
     * @return Cliente selecionado ou null.
     */
    private static Cliente selecionarClienteDisponivel(LocalDateTime inicio, LocalDateTime fim) {
        ArrayList<Cliente> clientesLivres = empresa.getClientesDisponiveis(inicio, fim);

        if (clientesLivres.isEmpty()) {
            System.out.println("Sem clientes disponíveis.");
            return null;
        }

        System.out.println(">> Existem " + clientesLivres.size() + " clientes livres.");
        String verListaClientes = lerTexto("Ver lista? (S/N): ");
        if (verListaClientes.equalsIgnoreCase("S")) {
            for (Cliente cliente : clientesLivres) {
                System.out.println("- " + cliente.getNome() + " | NIF: " + cliente.getNif());
            }
        }

        int nifCliente = lerInteiro("NIF do Cliente: ");
        Cliente cliente = empresa.procurarCliente(nifCliente);

        if (cliente == null) {
            System.out.println("Erro: Nenhum cliente encontrado.");
            return null;
        }

        if (!clientesLivres.contains(cliente)) {
            System.out.println("Erro: " + cliente.getNome() + "já tem uma viagem nesse horário.");
            return null;
        }
        return cliente;
    }

    /**
     * Metodo auxiliar para selecionar uma viatura disponível.
     *
     * @param inicio Data início.
     * @param fim    Data fim.
     * @return Viatura selecionada ou null.
     */
    private static Viatura selecionarViaturaDisponivel(LocalDateTime inicio, LocalDateTime fim) {
        ArrayList<Viatura> viaturasLivres = empresa.getViaturasDisponiveis(inicio, fim);

        if (viaturasLivres.isEmpty()) {
            System.out.println("Sem viaturas disponíveis para este horário.");
            return null;
        }

        System.out.println(">> Existem " + viaturasLivres.size() + " viaturas livres.");
        String verViaturas = lerTexto("Ver lista? (S/N): ");

        if (verViaturas.equalsIgnoreCase("S")) {
            for (Viatura viatura : viaturasLivres) {
                System.out.println("- " + viatura.getMarca() + " " + viatura.getModelo() + " (" + viatura.getMatricula() + ")");            }
        }

        String matricula = lerTexto("Matricula da Viatura: ");
        Viatura viatura = empresa.procurarViatura(matricula);

        if (viatura == null){
            System.out.println("Erro: Nenhuma viatura encontrada.");
            return null;
        }

        if (!viaturasLivres.contains(viatura)) {
            System.out.println("Erro: A viatura (" + viatura.getMatricula() + ") já tem uma viagem nesse horário.");
            return null;
        }
        return viatura;
    }

    /**
     * Lê uma linha de texto do utilizador.
     *
     * @param msg A mensagem a apresentar antes da leitura.
     * @return A String introduzida pelo utilizador.
     */
    private static String lerTexto(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }

    /**
     * Lê um número inteiro, garantindo que o input é válido.
     * @param msg A mensagem a apresentar.
     * @return O número inteiro introduzido.
     */
    private static int lerInteiro(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextInt()) {
            System.out.print("Valor inválido. Tente novamente: ");
            scanner.next();
        }
        int valor = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer
        return valor;
    }

    /**
     * Lê um valor decimal (double), garantindo que o input é válido.
     *
     * @param msg A mensagem a apresentar.
     * @return O valor double introduzido.
     */
    private static double lerDouble(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextDouble()) {
            System.out.print("Valor inválido. Tente novamente: ");
            scanner.next();
        }
        double valor = scanner.nextDouble();
        scanner.nextLine(); // Limpar buffer
        return valor;
    }

    /**
     * Lê e converte uma data no formato "dd-MM-yyyy HH:mm".
     * Pede novamente se o formato estiver errado.
     *
     * @param msg A mensagem a apresentar.
     * @return O objeto LocalDateTime validado.
     */
    private static LocalDateTime lerData(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                String input = scanner.nextLine();
                return LocalDateTime.parse(input, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido! Use: dd-MM-yyyy HH:mm");
            }
        }
    }

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

            System.out.println(">> Dados carregados: 3 Viaturas, 3 Clientes, 3 Condutores, 2 Viagens");

        } catch (Exception e) {
            System.out.println("Erro ao carregar dados de teste: " + e.getMessage());
        }
    }
}