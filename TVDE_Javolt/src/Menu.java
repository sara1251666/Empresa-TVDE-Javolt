/**
 * Menu
 * @author Leonardo
 * @version 1
 * @since 12/12/2025
 */

/*IMPORTAÇÕES NECESSÁRIAS
import Entidades.*;
import Gestao.Empresa;
import java.util.Scanner;

public class Menu {
    public static void displayMenu() {
        System.out.println("""
                |-------------------------------|
                |    MENU PRINCIPAL             |
                |-------------------------------|
                |    1 - Create                 |
                |    2 - Read                   |
                |    3 - Update                 |
                |    4 - Delete                 |
                |    5 - Exit                   |
                |-------------------------------|
                Escolha uma opção (1-5): """);
    }
}*/
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.ArrayList;
import Gestao.Empresa;
import Entidades.*;

/**
 * Classe de Interface com o Utilizador.
 * Gere os menus e a recolha de dados.
 */
public class Menu {

    // Instância única da Empresa (Base de Dados em Memória) [cite: 9]
    private static Empresa empresa = new Empresa();
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static void main(String[] args) {
        // Dados de teste para não começar vazio (Opcional)
        inicializarDadosTeste();

        displayMenuPrincipal();
    }

    /**
     * Menu Principal - Seleção de Entidade
     */
    public static void displayMenuPrincipal() {
        int opcao = 0;
        do {
            System.out.println("\n|-------------------------------|");
            System.out.println("|    TVDE - MENU PRINCIPAL      |");
            System.out.println("|-------------------------------|");
            System.out.println("|    1 - Gerir Viaturas         |");
            System.out.println("|    2 - Gerir Condutores       |");
            System.out.println("|    3 - Gerir Clientes         |");
            System.out.println("|    4 - Gerir Viagens          |");
            System.out.println("|    0 - Sair                   |");
            System.out.println("|-------------------------------|");
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> menuCRUD("Viaturas");
                case 2 -> menuCRUD("Condutores");
                case 3 -> menuCRUD("Clientes");
                case 4 -> menuViagens(); // Menu específico para viagens
                case 0 -> System.out.println("A encerrar sistema...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    /**
     * Menu Genérico para CRUD (Viaturas, Condutores, Clientes)
     */
    private static void menuCRUD(String entidade) {
        int opcao = 0;
        do {
            System.out.println("\n--- GESTÃO DE " + entidade.toUpperCase() + " ---");
            System.out.println("1 - Criar (Create)");
            System.out.println("2 - Listar (Read)");
            System.out.println("3 - Atualizar (Update)");
            System.out.println("4 - Apagar (Delete)");
            System.out.println("0 - Voltar");
            opcao = lerInteiro("Opção: ");

            switch (entidade) {
                case "Viaturas" -> processarViaturas(opcao);
                case "Condutores" -> processarCondutores(opcao);
                case "Clientes" -> processarClientes(opcao);
            }
        } while (opcao != 0);
    }

    // =======================================================
    // LÓGICA DE VIATURAS
    // =======================================================
    private static void processarViaturas(int opcao) {
        switch (opcao) {
            case 1 -> { // CREATE [cite: 25]
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
                ArrayList<Viatura> lista = empresa.getViaturas();
                if (lista.isEmpty()) System.out.println("Não há viaturas registadas.");
                for (Viatura v : lista) System.out.println(v);
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
                // As mensagens de erro de dependência já são impressas na classe Empresa
            }
        }
    }

    // =======================================================
    // LÓGICA DE CONDUTORES
    // =======================================================
    private static void processarCondutores(int opcao) {
        switch (opcao) {
            case 1 -> { // CREATE
                int nif = lerInteiro("NIF: ");
                String nome = lerTexto("Nome: ");
                int tel = lerInteiro("Telemóvel: ");
                String morada = lerTexto("Morada: ");
                int cc = lerInteiro("Cartão Cidadão: ");
                String carta = lerTexto("Carta Condução: ");
                int ss = lerInteiro("Segurança Social: ");

                Condutor c = new Condutor(nome, nif, tel, morada, cc, carta, ss);
                if (empresa.adicionarCondutor(c)) {
                    System.out.println("Condutor adicionado.");
                } else {
                    System.out.println("Erro: Condutor com este NIF já existe.");
                }
            }
            case 2 -> { // READ
                for (Condutor c : empresa.getCondutores()) System.out.println(c);
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

    // =======================================================
    // LÓGICA DE CLIENTES
    // =======================================================
    private static void processarClientes(int opcao) {
        switch (opcao) {
            case 1 -> { // CREATE
                int nif = lerInteiro("NIF: ");
                String nome = lerTexto("Nome: ");
                int tel = lerInteiro("Telemóvel: ");
                String morada = lerTexto("Morada: ");
                int cc = lerInteiro("Cartão Cidadão: ");

                Cliente c = new Cliente(nome, nif, tel, morada, cc);
                if (empresa.adicionarCliente(c)) {
                    System.out.println("Cliente adicionado.");
                } else {
                    System.out.println("Erro: Cliente com este NIF já existe.");
                }
            }
            case 2 -> { // READ
                for (Cliente c : empresa.getClientes()) System.out.println(c);
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
    // LÓGICA DE VIAGENS (Simplificada) [cite: 28]
    // =======================================================
    private static void menuViagens() {
        System.out.println("\n--- GESTÃO DE VIAGENS ---");
        System.out.println("1 - Registar Nova Viagem");
        System.out.println("2 - Listar Histórico de Viagens");
        int op = lerInteiro("Opção: ");

        if (op == 1) {
            // Para criar uma viagem, precisamos de associar objetos existentes
            int nifCondutor = lerInteiro("NIF do Condutor: ");
            Condutor condutor = empresa.procurarCondutor(nifCondutor);

            int nifCliente = lerInteiro("NIF do Cliente: ");
            Cliente cliente = empresa.procurarCliente(nifCliente);

            String matricula = lerTexto("Matrícula da Viatura: ");
            Viatura viatura = empresa.procurarViatura(matricula);

            if (condutor != null && cliente != null && viatura != null) {
                // Leitura de datas
                LocalDateTime inicio = lerData("Início (dd-MM-yyyy HH:mm): ");
                LocalDateTime fim = lerData("Fim (dd-MM-yyyy HH:mm): ");

                String origem = lerTexto("Origem: ");
                String destino = lerTexto("Destino: ");
                double kms = lerDouble("Kms: ");
                double custo = lerDouble("Custo (€): ");

                Viagem novaViagem = new Viagem(condutor, cliente, viatura, inicio, fim, origem, destino, kms, custo);
                empresa.adicionarViagem(novaViagem);
                System.out.println("Viagem registada com sucesso!");
            } else {
                System.out.println("Erro: Condutor, Cliente ou Viatura não encontrados. Crie-os primeiro.");
            }

        } else if (op == 2) {
            for(Viagem v : empresa.getViagens()) {
                System.out.println(v);
            }
        }
    }

    // =======================================================
    // MÉTODOS AUXILIARES (Input)
    // =======================================================

    private static String lerTexto(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }

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

    private static void inicializarDadosTeste() {
        // Dados dummy para testar sem ter de criar tudo do zero
        empresa.adicionarViatura(new Viatura("AA-00-BB", "Toyota", "Corolla", 2020));
        empresa.adicionarCliente(new Cliente("Joao Silva", 123456789, 910000000, "Porto", 111222333));
        empresa.adicionarCondutor(new Condutor("Maria Santos", 987654321, 930000000, "Lisboa", 444555666, "C-123", 123123123));
    }
}