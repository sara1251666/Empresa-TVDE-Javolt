package Entidades;

/**
 * Representa um Entidades.Cliente de uma Gestao.Empresa TVDE
 * Herda de Entidades.Pessoa
 * @author Levi e Sara
 * @version 2
 * @since 2025-12-12
 */
public class Cliente extends Pessoa {

    /**
     * Construtor Entidades.Cliente Vazio.
     */
    public Cliente() {
    }

    /**
     * Constrói um Novo Entidades.Cliente com os Dados Fornecidos.
     * Invoca o Construtor da Supercclasse (Entidades.Pessoa).
     *
     * @param nome      Nome do Entidades.Cliente.
     * @param nif       Número de Identificação Fiscal.
     * @param tel       Número de Telemóvel.
     * @param morada    Morada Completa.
     * @param cartaoCid Número do Cartão do Cidadão.
     */
    public Cliente(String nome, int nif, int tel, String morada, int cartaoCid) {
        super(nome, nif, tel, morada, cartaoCid);
    }

    /**
     * Devolve a Representação em Texto do Entidades.Cliente.
     * Inclui a etiqueta "Entidades.Cliente" e os dados herdados de Entidades.Pessoa.
     * @return String Formatada com os dados do cliente.
     */
    @Override
    public String toString() {
        return "[" + super.toString() + "]";
    }
}