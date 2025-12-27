import java.text.ParsePosition;

/**
 * Representa um Condutor
 * @author Levi e Sara
 * @version 1
 * @since 2025-12-12
 */
public class Condutor extends Pessoa {
    private String cartaCond;
    private int segSocial;

    /**
     * Construtor Condutor Vazio.
     */
    public Condutor() {
    }

    /**
     * Constroi um novo Condutor com os dados fornecidos.
     * Invoca o Construtor da Superclasse (Pessoa).
     * @param nome Nome do Condutor.
     * @param nif Nif do Condutor.
     * @param tel Telemóvel do Condutor.
     * @param morada Morada do Condutor.
     * @param cartaoCid Cartão de Cidadão do Condutor.
     * @param cartaCond Carta de Condução do Condutor.
     * @param segSocial Número de Segurança Social do Condutor.
     */
    public Condutor(String nome, int nif, int tel, String morada, int cartaoCid, String cartaCond, int segSocial) {
        super(nome, nif, tel, morada, cartaoCid);
        this.cartaCond = cartaCond;
        this.segSocial = segSocial;
    }

    /**
     * Obtém a Carta de Condução
     * @return Retorna a Carta de Condução.
     */
    public String getCartaCond() {
        return cartaCond;
    }

    /**
     * Define um Novo valor para a Carta de Condução.
     * @param cartaCond Atribui a Nova Carta de Condução.
     */
    public void setCartaCond(String cartaCond) {
        this.cartaCond = cartaCond;
    }

    /**
     * Obtém o Número da Segurança Social.
     * @return Retorna o Número da Segurança Social.
     */
    public int getSegSocial() {
        return segSocial;
    }

    /**
     * Define um Novo Número da Segurança Social.
      * @param segSocial Atribui o Novo Número da Segurança Social.
     */
    public void setSegSocial(int segSocial) {
        this.segSocial = segSocial;
    }

    /**
     * Devolve a Representação em Texto do Condutor
     * Junta os dados de PEssoa com os dados especifícos do condutor.
     * @return String com os Dados do Condutor.
     */
    @Override
    public String toString() {
        return "Condutor [" + super.toString() + " | Carta: " +cartaCond + " | SegSocial: " + segSocial + "]";
    }
}


