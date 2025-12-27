/**
 * Representa um Cliente
 * @author Levi e Sara
 * @version 1
 * @since 2025-12-12
 */
public class Cliente {
    private int cartaoCid;  // Número de Cartão de Cidadão
    private String nome;    // Nome do Cliente
    private int nif;        // Nif do CLiente
    private int tel;        // Número do Telemóvel
    private String morada;  // Morada do Cliente

    /**
     * Construtor Vazio
     */
    public Cliente() {
    }

    /**
     * Construtor Clientes
     * @param cartaoCid Dado de Cartão de Cidadão (identificador único)
     * @param nome Nome do Cliente
     * @param nif Dado do NIF (Contribuinte) Cliente
     * @param tel Dado do Número de Telemóvel do Cliente
     * @param morada Morada do Cliente
     */
    public Cliente(int cartaoCid, String nome, int nif, int tel, String morada) {
        this.cartaoCid = cartaoCid;
        this.nome = nome;
        this.nif = nif;
        this.tel = tel;
        this.morada = morada;
    }

    /**
     * Obtém o Número do Cartão de Cidadão
     * @return Retorna o número do Cartão de Cidadão
     */
    public int getCartaoCid() {
        return cartaoCid;
    }

    /**
     * Define um Novo Número de Cartão de Cidadão
     * @param cartaoCid Atribui o novo valor ao Cartão de Cidadão
     */
    public void setCartaoCid(int cartaoCid) {
        this.cartaoCid = cartaoCid;
    }

    /**
     * Obtém o Nome do Cliente
     * @return Retorna o Nome do Cliente
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
     * Obtém o Nif (Contribuinte) do Cliente
     * @return Retorna o Nif (Contribuinte) do Cliente
     */
    public int getNif() {
        return nif;
    }

    /**
     * Define um Novo NIF ao CLiente
     * @param nif Atribui o novo NIF (Contribuinte) ao Cliente
     */
    public void setNif(int nif) {
        this.nif = nif;
    }

    /**
     * Obtém o Número do Telemóvel do Cliente
     * @return Retorna o Número de Telemóvel do Cliente
     */
    public int getTel() {
        return tel;
    }

    /**
     * Define um Novo Número de Telemóvel ao CLiente
     * @param tel Atribui o novo Número de Telemóvel ao Cliente
     */
    public void setTel(int tel) {
        this.tel = tel;
    }

    /**
     * Obtém a Morada do Cliente
     * @return Retorna a Morada do Cliente
     */
    public String getMorada() {
        return morada;
    }

    /**
     * Define uma Nova Morada ao Cliente
     * @param morada Atribui uma Nova Morada ao Cliente
     */
    public void setMorada(String morada) {
        this.morada = morada;
    }

}