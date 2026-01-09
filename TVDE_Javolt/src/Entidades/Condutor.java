package Entidades;

/**
 * Representa um Condutor profissional da empresa.
 * <p>
 * Esta classe herda de {@link Pessoa}, reaproveitando atributos comuns (nome, nif, etc.),
 * e adiciona os requisitos legais para condução (Carta e Segurança Social).
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2026-01-01
 */
public class Condutor extends Pessoa {

    /**
     * O número da carta de condução do condutor.
     */
    private String cartaCond;

    /**
     * O número de Segurança Social do condutor.
     */
    private int segSocial;

    /**
     * Constrói um novo Condutor com todos os dados completos.
     * Invoca o construtor da superclasse {@link Pessoa}.
     *
     * @param nome      Nome do Condutor.
     * @param nif       NIF do Condutor.
     * @param tel       Telemóvel do Condutor.
     * @param morada    Morada do Condutor.
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
     * Obtém o número da carta de condução.
     * @return A carta de condução.
     */
    public String getCartaCond() {
        return cartaCond;
    }

    /**
     * Define um novo valor para a carta de condução.
     * @param cartaCond A nova carta de condução a atribuir.
     */
    public void setCartaCond(String cartaCond) {
        this.cartaCond = cartaCond;
    }

    /**
     * Obtém o número da Segurança Social.
     * @return O número da Segurança Social.
     */
    public int getSegSocial() {
        return segSocial;
    }

    /**
     * Define um novo número de Segurança Social.
     * @param segSocial O novo número de Segurança Social.
     */
    public void setSegSocial(int segSocial) {
        this.segSocial = segSocial;
    }

    /**
     * Devolve a representação em texto do Condutor.
     * Junta os dados da classe {@link Pessoa} com os dados específicos do condutor.
     * @return Uma String formatada com os dados do Condutor.
     */
    @Override
    public String toString() {
        return "[" + super.toString() + " | Carta: " +cartaCond + " | SegSocial: " + segSocial + "]";
    }
}