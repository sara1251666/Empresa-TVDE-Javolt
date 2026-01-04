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
 * @author Levi e Sara
 * @version 1.3
 * @since 2026-01-03
 */
public class Menu {

    /**
     * Instância única da Empresa que armazena os dados e a lógica de negócio do programa.
     */
    private static Empresa empresa = new Empresa();

    /**
     * Objeto Scanner partilhado para leitura de inputs.
     * */
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Formatador de data padrão para o sistema (dd-MM-yyyy HH:mm).
     * */
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /**
     * Metodo responsável por iniciar o ciclo de vida da aplicação.
     * <p>
     * É invocado pela classe Main. Realiza as seguintes operações:
     * 1. Pergunta se carrega dados de ficheiro.
     * 2. Verifica se a base de dados está vazia e sugere dados de teste.
     * 3. Inicia o loop do Menu Principal.
     * 4. Pergunta se grava os dados ao sair.
     * </p>
     */
    public static void iniciar(){
        //1. Pergunta ao utilizador se deseja carregar os dados guardados.
        String respostaCarregar = lerTexto("Deseja carregar os dados guardados? (S/N): ");

        if (respostaCarregar.equalsIgnoreCase("S")) {
            System.out.println("A carregar dados...");
            empresa.carregarDados();
        } else {
            System.out.println("A iniciar com uma base de dados limpa...");
        }

        // Se a base de dados estiver vazia (porque não carregou ou porque os ficheiros estão vazios)
        // Podemos perguntar se quer gerar dados de teste ou gerar automaticamente.

        if (empresa.getViaturas().isEmpty() && empresa.getCondutores().isEmpty()){
            System.out.println("Base de dados vazia. A gerar dados de teste...");
            inicializarDadosTeste();
        }

        //2. Loop do Menu da aplicação.
        displayMenuPrincipal();

        //3. Pergunta ao utilizador se quer gravar a base de dados.
        //Só chega aqui quando o utilizador escolhe a opção "0-Sair".
        String respostaGravar = lerTexto("Deseja gravar os dados antes de sair? (S/N): ");
        if(respostaGravar.equalsIgnoreCase("S")) {
            System.out.println("A gravar alterações...");
            empresa.gravarDados();
            System.out.println("Dados guardados com sucesso.");
        }else {
            System.out.println("As alterações não foram guardadas.");
        }
        System.out.println("Até logo!");
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
                case 4 -> menuViagens();   // Só viagens
                case 5 -> menuReservas();  // Só reservas
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
    //            MENU VIAGENS (Só Histórico e Registo)
    // =======================================================

    /**
     * Exibe o menu específico para gestão de Viagens (Histórico e Registo Imediato).
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
            op = lerInteiro("Escolha a opção: ");

            switch (op) {
                case 1 -> tratarRegistarViagem();
                case 2 -> tratarListarViagens();
                case 3 -> tratarEliminarViagem();
            }
        }while  (op != 0);
    }

    // =======================================================
    //            MENU RESERVAS (Planeamento)
    // =======================================================

    /**
     * Exibe o menu específico para gestão de Reservas (Planeamento Futuro).
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
            op = lerInteiro("Escolha a opção: ");

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
     * Recolhe os dados necessários para registar uma nova {@link Viagem} e invoca a lógica na classe Empresa.
     */
    private static void tratarRegistarViagem() {
        System.out.println("--- Nova viagem ---");
        int nifCondutor = lerInteiro("NIF do Condutor: ");
        Condutor condutor = empresa.procurarCondutor(nifCondutor);

        int nifCliente = lerInteiro("NIF do Cliente: ");
        Cliente cliente = empresa.procurarCliente(nifCliente);

        String matricula = lerTexto("Matrícula da Viatura: ");
        Viatura viatura = empresa.procurarViatura(matricula);

        if (condutor != null && cliente != null && viatura != null) {
            LocalDateTime inicio = lerData("Início (dd-MM-yyyy HH:mm): ");
            LocalDateTime fim = lerData("Fim (dd-MM-yyyy HH:mm): ");

            if (empresa.verificarSobreposicao(viatura, condutor, inicio, fim)) {
                System.out.println("Erro: Sobreposição detetada (Condutor ou Viatura ocupados).");
            } else {
                String origem = lerTexto("Origem: ");
                String destino = lerTexto("Destino: ");
                double kms = lerDouble("Kms: ");
                double custo = lerDouble("Custo (€): ");

                Viagem novaViagem = new Viagem(condutor, cliente, viatura, inicio, fim, origem, destino, kms, custo);
                empresa.adicionarViagem(novaViagem);
                System.out.println("Viagem registada com sucesso!");
            }
        } else {
            System.out.println("Erro: Condutor, Cliente ou Viatura não encontrados. Crie-os primeiro.");
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

        //2. Obyer apenas as reservas deste cliente
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
            op = lerInteiro("Escolha a opção: ");

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

    // Métodos Auxiliares de Estatísticas

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
//                MÉTODOS AUXILIARES (Input)
// =======================================================

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
     * Preenche o sistema com dados iniciais (mock data) para facilitar testes.
     * Cria 1 Viatura, 1 Cliente e 1 Condutor.
     */
    public static void inicializarDadosTeste() {
        empresa.adicionarViatura(new Viatura("AA-00-BB", "Toyota", "Corolla", 2020));

        try {
            empresa.adicionarCliente(new Cliente("Joao Silva", 123456789, 910000000, "Porto", 111222333));
            empresa.adicionarCondutor(new Condutor("Maria Santos", 987654321, 930000000, "Lisboa", 444555666, "C-123", 123123123));
        } catch (Exception e) {
            System.out.println("Erro ao carregar dados de teste: " + e.getMessage());
        }
    }
}