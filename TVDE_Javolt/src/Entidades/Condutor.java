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
     * Número de identificação único do condutor na empresa.
     * Diferente do NIF que é o identificador fiscal nacional.
     */
    private int numeroIdentificacao;

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
     *
     * @param numeroIdentificacao Número de ID único do condutor na empresa.
     * @param nome      Nome do Condutor.
     * @param nif       NIF do Condutor.
     * @param tel       Telemóvel do Condutor.
     * @param morada    Morada do Condutor.
     * @param cartaoCid Cartão de Cidadão do Condutor.
     * @param cartaCond Carta de Condução do Condutor.
     * @param segSocial Número de Segurança Social do Condutor.
     */
    public Condutor(int numeroIdentificacao, String nome, int nif, int tel,
                    String morada, int cartaoCid, String cartaCond, int segSocial) {
        super(nome, nif, tel, morada, cartaoCid);
        this.numeroIdentificacao = numeroIdentificacao;
        this.cartaCond = cartaCond;
        this.segSocial = segSocial;
    }

    /**
     * Obtém o número de identificação do condutor na empresa.
     *
     * @return O número de identificação único do condutor.
     */
    public int getNumeroIdentificacao() {
        return numeroIdentificacao;
    }

    /**
     * Define o número de identificação do condutor.
     * Verifica se o número é positivo.
     *
     * @param numeroIdentificacao O novo número de identificação.
     * @throws IllegalArgumentException se o número for negativo.
     */
    public void setNumeroIdentificacao(int numeroIdentificacao) {
        if (numeroIdentificacao <= 0) {
            throw new IllegalArgumentException("Número de identificação deve ser positivo");
        }
        this.numeroIdentificacao = numeroIdentificacao;
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
     *
     * @param cartaCond A nova carta de condução a atribuir.
     * @throws IllegalArgumentException se a carta for null ou vazia.
     */
    public void setCartaCond(String cartaCond) {
        if (cartaCond == null || cartaCond.trim().isEmpty()) {
            throw new IllegalArgumentException("Carta de condução não pode ser vazia");
        }
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
     *
     * @param segSocial O novo número de Segurança Social.
     * @throws IllegalArgumentException se o número for negativo.
     */
    public void setSegSocial(int segSocial) {
        if (segSocial < 0) {
            throw new IllegalArgumentException("Número de Segurança Social não pode ser negativo");
        }
        this.segSocial = segSocial;
    }

    /**
     * Devolve a representação em texto do Condutor.
     * <p>
     * Junta os dados da classe {@link Pessoa} com os dados específicos do condutor.
     * </p>
     *
     * @return Uma String formatada com os dados do Condutor.
     */
    @Override
    public String toString() {
        return "Condutor [ID: " + numeroIdentificacao + "] " + super.toString() +
                " | Carta: " + cartaCond + " | Seg.Social: " + segSocial;
    }
}