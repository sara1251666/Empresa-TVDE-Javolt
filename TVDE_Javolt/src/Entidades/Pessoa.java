package Entidades;

/**
 * Classe abstrata que representa uma Pessoa genérica no sistema.
 * <p>
 * Esta classe serve como base para {@link Cliente} e {@link Condutor},
 * agrupando os dados comuns a todas as pessoas no sistema (nome, NIF, contacto, etc.).
 * É uma classe abstrata porque não faz sentido instanciar uma "Pessoa" genérica.
 * </p>
 * <p>
 * Inclui validação básica para o NIF (deve ter exatamente 9 dígitos).
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2025-12-27
 */
public abstract class Pessoa {
    /**
     * Nome completo da pessoa.
     */
    private String nome;

    /**
     * Número de Identificação Fiscal (NIF).
     * Deve ter exatamente 9 dígitos.
     */
    private int nif;

    /**
     * Número de telemóvel da pessoa.
     */
    private int tel;

    /**
     * Morada completa da pessoa.
     */
    private String morada;

    /**
     * Número do Cartão de Cidadão.
     */
    private int cartaoCid;

    /**
     * Construtor vazio de Pessoa.
     * <p>
     * Útil para inicialização básica quando os dados serão definidos posteriormente via setters.
     * </p>
     */
    public Pessoa() {
        // Construtor vazio para flexibilidade
    }

    /**
     * Construtor completo de Pessoa.
     * <p>
     * Valida automaticamente o NIF através do setter {@link #setNif(int)}.
     * </p>
     *
     * @param nome      Nome da pessoa.
     * @param nif       Número de Identificação Fiscal (9 dígitos).
     * @param tel       Número de telemóvel.
     * @param morada    Morada completa.
     * @param cartaoCid Número do Cartão de Cidadão.
     * @throws IllegalArgumentException se o NIF não tiver 9 dígitos.
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
     * Obtém o nome da pessoa.
     *
     * @return O nome da pessoa.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome da pessoa.
     *
     * @param nome O novo nome a atribuir.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém o NIF da pessoa.
     *
     * @return O NIF da pessoa.
     */
    public int getNif() {
        return nif;
    }

    /**
     * Define o NIF da pessoa com validação.
     * <p>
     * Verifica se o NIF tem exatamente 9 dígitos.
     * </p>
     *
     * @param nif O novo NIF a atribuir.
     * @throws IllegalArgumentException se o NIF não tiver 9 dígitos.
     */
    public void setNif(int nif) {
        //Converte para texto para contar o tamanho
        String nifTexto = String.valueOf(nif);

        if (nifTexto.length() == 9) {
            this.nif = nif;
        } else {
            throw new IllegalArgumentException("NIF invalido! tem de ter exatamente 9 digitos (recebido: " + nif + ").");
        }
    }

    /**
     * Obtém o número de telemóvel da pessoa.
     *
     * @return O número de telemóvel.
     */
    public int getTel() {
        return tel;
    }

    /**
     * Define o número de telemóvel da pessoa.
     *
     * @param tel O novo número de telemóvel.
     */
    public void setTel(int tel) {
        String telTexto = String.valueOf(tel);

        if (telTexto.length() == 9) {
            this.tel = tel;
        } else {
            throw new IllegalArgumentException(
                    "Telemóvel inválido! Deve ter 9 dígitos (recebido: " + tel + ")."
            );
        }
    }

    /**
     * Obtém a morada da pessoa.
     *
     * @return A morada da pessoa.
     */
    public String getMorada() {
        return morada;
    }

    /**
     * Define a morada da pessoa.
     *
     * @param morada A nova morada.
     */
    public void setMorada(String morada) {
        this.morada = morada;
    }

    /**
     * Obtém o número do Cartão de Cidadão.
     *
     * @return O número do Cartão de Cidadão.
     */
    public int getCartaoCid() {
        return cartaoCid;
    }

    /**
     * Define o número do Cartão de Cidadão.
     *
     * @param cartaoCid O novo número do Cartão de Cidadão.
     */
    public void setCartaoCid(int cartaoCid) {
        this.cartaoCid = cartaoCid;
    }

    /**
     * Devolve uma representação textual básica da pessoa.
     * <p>
     * Formato: "Nome: [nome] | NIF: [nif] | Tel: [tel]"
     * </p>
     *
     * @return String formatada com informações básicas da pessoa.
     */
    public String toString(){
        return "Nome: " + nome + " | NIF: " + nif + " | Tel: " + tel;
    }
}
