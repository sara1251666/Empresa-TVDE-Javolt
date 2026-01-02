public class Main {

    public static void main(String[] args) {

        System.out.println("A carregar base de dados...");
        Menu.empresa.carregarDados();

        //funcionalidade para teste
        if (empresa.getViaturas().isEmpty() && empresa.getCondutores().isEmpty()) {
            System.out.println("Base de dados vazia. A gerar dados de teste...");
            Menu.inicializarDadosTeste();
        }

        // VAI BUSCAR MENU / INTERFACE
        Menu.displayMenuPrincipal();

        System.out.println("A gravar alterações...");
        Menu.empresa.gravarDados();
        System.out.println("Dados guardados. Até logo!");
    }
}

