package Entidades;

/**
 * Classe abstrata que representa uma Entidades.Pessoa Genérica.
 * Agrupa dados comuns a Clientes e Condutores.
 * @author Sara
 * @version 1
 * @since 2025-12-27
 */
public abstract class Pessoa {
    private String nome;
    private int nif;
    private int tel;
    private String morada;
    private int cartaoCid;

    /**
     * Construtor Entidades.Pessoa vazio.
     */
    public Pessoa() {
    }

    /**
     * Construtor completo de Entidades.Pessoa.
     * @param nome Nome da Entidades.Pessoa.
     * @param nif Número de Identificação Fiscal.
     * @param tel Número de Telemóvel da Entidades.Pessoa.
     * @param morada Morada Completa
     * @param cartaoCid Número de Cartão do Cidadão.
     */
    public Pessoa(String nome, int nif, int tel, String morada, int cartaoCid) {
        this.nome = nome;
        //this.nif = nif;
        setNif(nif);
        this.tel = tel;
        this.morada = morada;
        this.cartaoCid = cartaoCid;
    }

    /**
     * Obtém o Nome da Entidades.Pessoa.
     * @return Retorna o Nome da Entidades.Pessoa
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define um Novo Nome da Entidades.Pessoa
     * @param nome Atribui o novo nome.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém o NIF da Entidades.Pessoa.
     * @return Retorna o NIF da Entidades.Pessoa.
     */
    public int getNif() {
        return nif;
    }

    /**
     * Define um Novo Número NIF.
     * @param nif Atribui o novo NIF.
     */
    public void setNif(int nif) {
        //Converte para texto para contar o tamanho
        String nifTexto = String.valueOf(nif);

        if (nifTexto.length() == 9) {
            this.nif = nif;
        } else {
            //System.out.println("Erro: O NIF deve ter 9 digitos.");
            throw new IllegalArgumentException("NIF invaldo! tem de ter exatamente 9 digitos (ecebido: " + nif + ").");
        }
    }

    /**
     * Obtém o Telemóvel da Entidades.Pessoa.
     * @return Retorna o Número de Telemóvel.
     */
    public int getTel() {
        return tel;
    }

    /**
     * Define um Novo Número de Telemóvel.
     * @param tel Atribui um Novo Número de Telemóvel.
     */
    public void setTel(int tel) {
        this.tel = tel;
    }

    /**
     * Obtém a Morada da Entidades.Pessoa.
     * @return Retorna a Morada da Entidades.Pessoa.
     */
    public String getMorada() {
        return morada;
    }

    /**
     * Define uma Nova Morada.
     * @param morada Atribui a Nova Morada.
     */
    public void setMorada(String morada) {
        this.morada = morada;
    }

    /**
     * Obtém o número do Cartão do Cidadão.
     * @return Retorna o Número do Cartão do Cidadão.
     */
    public int getCartaoCid() {
        return cartaoCid;
    }

    /**
     * Define um novo Número do Cartão do Cidadão.
     * @param cartaoCid Define o Novo Número do Cartão do Cidadão.
     */
    public void setCartaoCid(int cartaoCid) {
        this.cartaoCid = cartaoCid;
    }

    public String toString(){
        return "Nome: " + nome + " | NIF: " + nif + " | Tel: " + tel;
    }
}
