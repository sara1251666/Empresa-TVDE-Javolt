/**
 * Ponto de entrada principal da aplicação de gestão de uma empresa TVDE.
 * <p>
 * Esta classe é responsável apenas por iniciar o ciclo de vida do programa,
 * delegando todo o controlo de fluxo para a classe {@link Menu}.
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2026-01-01
 */
public class Main {

    public static void main(String[] args) {
        //Arranca a aplicação chamando o metodo estatico do Menu
        Menu.iniciar();
    }
}
