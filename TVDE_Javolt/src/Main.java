/**
 * Classe principal responsável pelo arranque da aplicação de gestão de uma empresa de TVDE.
 * <p>
 * Esta classe funciona como o ponto de entrada do sistema. A sua única responsabilidade
 * é iniciar o ciclo de vida do programa e transferir imediatamente o controlo da execução
 * para a interface de utilizador, gerida pela classe {@link Menu}.
 * Não contém lógica de negócio nem manipulação direta de dados.
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2026-01-08
 */
public class Main {

    public static void main(String[] args) {
        //Arranca a aplicação chamando o metodo estatico do Menu
        Menu.iniciar();
    }
}
