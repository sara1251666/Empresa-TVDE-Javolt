/**
 * Representa um Cliente de uma Empresa TVDE
 * Herda de Pessoa
 * @author Levi e Sara
 * @version 2
 * @since 2025-12-12
 */
public class Cliente extends Pessoa{

    public Cliente(String nome, int nif, int tel, String morada, int cartaoCid) {
        super(nome, nif, tel, morada, cartaoCid);
    }

    public String toString() {
        return "Cliente [" + super.toString() + "]";
    }
}