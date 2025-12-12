/**
 * Representa um Condutor
 * @author Levi e Sara
 * @version 1
 * @since 2025-12-12
 */
public class Condutores {
    private int cartaoCid;  // Número de Cartão de Cidadão
    private String nome;    // Nome do Cliente
    private String cartaCond;  // Número de Cartão de Condução
    private int nif;        // Nif do CLiente
    private int segSocial;  // Número de Segurança Social
    private int tel;        // Número do Telemóvel
    private String morada;  // Morada do Cliente

    /**
     * Construtor Vazio
     */
    public Condutores() {
    }

    /**
     *
     * @param cartaoCid Dado de Cartão de Cidadão (identificador único)
     * @param nome Nome do Condutor
     * @param cartaCond Dado do Número da Carta de Condução do Condutor
     * @param nif Dado do NIF (Contribuinte) do Condutor
     * @param segSocial Dado do Número da Segurança Social do Condutor
     * @param tel Número de Telemóvel do Condutor
     * @param morada Morada do Condutor
     */
    public Condutores(int cartaoCid, String nome, String cartaCond, int nif, int segSocial, int tel, String morada) {
        this.cartaoCid = cartaoCid;
        this.nome = nome;
        this.cartaCond = cartaCond;
        this.nif = nif;
        this.segSocial = segSocial;
        this.tel = tel;
        this.morada = morada;
    }

    /**
     * Obtém o Número do Cartão de Cidadão
     * @return Retorna o número do Cartão de Cidadão
     */
    public int getCartaocid() {
        return cartaoCid;
    }

    /**
     * Define um Novo Número de Cartão de Cidadão
     * @param cartaoCid Atribui o novo valor ao Cartão de Cidadão
     */
    public void setCartaocid(int cartaoCid) {
        this.cartaoCid = cartaoCid;
    }

    /**
     * Obtém o Nome do Condutor
     * @return Retorna o Nome do Condutor
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define um Novo Nome ao CLiente
     * @param nome Atribui o novo nome ao Cliente
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém o Número da Carta de Condução
     * @return Retorna o número da Carta de Condução
     */
    public String getCartaCond() {
        return cartaCond;
    }

    /**
     * Define um Novo Número da Carta de Condução
     * @param cartaCond Atribui o novo valor à Carta de Condução
     */
    public void setCartacond(String cartaCond) {
        this.cartaCond = cartaCond;
    }

    /**
     * Obtém o Nif (Contribuinte) do Condutor
     * @return Retorna o Nif (Contribuinte) do Condutor
     */
    public int getNif() {
        return nif;
    }

    /**
     * Define um Novo NIF ao Condutor
     * @param nif Atribui o novo NIF (Contribuinte) ao Condutor
     */
    public void setNif(int nif) {
        this.nif = nif;
    }

    /**
     * Obtém o Número de Segurança Social do Condutor
     * @return Retorna o Número de Segurança Social do Condutor
     */
    public int getSegSocial() {
        return segSocial;
    }

    /**
     * Define um Novo Número de Segurança Social ao Condutor
     * @param segSocial Atribui o novo Número de Segurança Social ao Condutor
     */
    public void setSegSocial(int segSocial) {
        this.segSocial = segSocial;
    }

    /**
     * Obtém o Número do Telemóvel do Condutor
     * @return Retorna o Número de Telemóvel do Condutor
     */
    public int getTel() {
        return tel;
    }

    /**
     * Define um Novo Número de Telemóvel ao Condutor
     * @param tel Atribui o novo Número de Telemóvel ao Condutor
     */
    public void setTel(int tel) {
        this.tel = tel;
    }

    /**
     * Obtém a Morada do Condutor
     * @return Retorna a Morada do Condutor
     */
    public String getMorada() {
        return morada;
    }

    /**
     * Define uma Nova Morada ao Condutor
     * @param morada Atribui uma Nova Morada ao Condutor
     */
    public void setMorada(String morada) {
        this.morada = morada;
    }
}

