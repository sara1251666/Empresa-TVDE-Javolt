/**
 * Representa um Cliente de uma Empresa TVDE
 * Herda de Pessoa
 * @author Levi e Sara
 * @version 2
 * @since 2025-12-12
 */
public class Cliente extends Pessoa {

    /**
     * Construtor Cliente Vazio.
     */
    public Cliente() {
    }

    /**
     * Constrói um Novo Cliente com os Dados Fornecidos.
     * Invoca o Construtor da Supercclasse (Pessoa).
     *
     * @param nome      Nome do Cliente.
     * @param nif       Número de Identificação Fiscal.
     * @param tel       Número de Telemóvel.
     * @param morada    Morada Completa.
     * @param cartaoCid Número do Cartão do Cidadão.
     */
    public Cliente(String nome, int nif, int tel, String morada, int cartaoCid) {
        super(nome, nif, tel, morada, cartaoCid);
    }

    /**
     * Devolve a Representação em Texto do Cliente.
     * Inclui a etiqueta "Cliente" e os dados herdados de Pessoa.
     * @return String Formatada com os dados do cliente.
     */
    @Override
    public String toString() {
        return "Cliente [" + super.toString() + "]";
    }
}