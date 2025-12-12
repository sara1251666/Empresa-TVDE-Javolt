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


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        do {
            displayMenu();

            // Garante que o usuário digite um número
            while (!scanner.hasNextInt()) {
                System.out.println("Entrada inválida! Digite um número entre 1 e 5.");
                scanner.nextLine(); // limpa entrada inválida
                displayMenu();
            }

            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> System.out.println("Opção CREATE selecionada.");
                case 2 -> System.out.println("Opção READ selecionada.");
                case 3 -> System.out.println("Opção UPDATE selecionada.");
                case 4 -> System.out.println("Opção DELETE selecionada.");
                case 5 -> System.out.println("Saindo do programa...");
                default -> System.out.println("Opção inválida! Escolha entre 1 e 5.");
            }

            System.out.println(); // linha extra para ficar bonito
        } while (choice != 5);

        scanner.close();
    }
}