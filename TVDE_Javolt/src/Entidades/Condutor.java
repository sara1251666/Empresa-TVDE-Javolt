package Entidades;

/**
 * Representa um Entidades.Condutor
 * @author Levi e Sara
 * @version 1
 * @since 2025-12-12
 */
public class Condutor extends Pessoa {
    private String cartaCond;
    private int segSocial;

    /**
     * Construtor Entidades.Condutor Vazio.
     */
    public Condutor(String nome, String nif, String carta) {
    }

    /**
     * Constroi um novo Entidades.Condutor com os dados fornecidos.
     * Invoca o Construtor da Superclasse (Entidades.Pessoa).
     * @param nome Nome do Entidades.Condutor.
     * @param nif Nif do Entidades.Condutor.
     * @param tel Telemóvel do Entidades.Condutor.
     * @param morada Morada do Entidades.Condutor.
     * @param cartaoCid Cartão de Cidadão do Entidades.Condutor.
     * @param cartaCond Carta de Condução do Entidades.Condutor.
     * @param segSocial Número de Segurança Social do Entidades.Condutor.
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
     * Devolve a Representação em Texto do Entidades.Condutor
     * Junta os dados de PEssoa com os dados especifícos do condutor.
     * @return String com os Dados do Entidades.Condutor.
     */
    @Override
    public String toString() {
        return "Entidades.Condutor [" + super.toString() + " | Carta: " +cartaCond + " | SegSocial: " + segSocial + "]";
    }
}


