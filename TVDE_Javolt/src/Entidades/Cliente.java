package Entidades;

/**
 * Representa um Cliente que utiliza os serviços da empresa.
 * <p>
 * Herda diretamente de {@link Pessoa}. Embora atualmente não tenha atributos adicionais,
 * a criação desta classe específica permite distinguir semanticamente um Cliente de um Condutor
 * e facilita a expansão futura (ex: adicionar pontos de fidelidade).
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2026-01-01
 */
public class Cliente extends Pessoa {

     /**
     * Constrói um novo Cliente com todos os dados fornecidos.
     * Invoca o construtor da superclasse {@link Pessoa}.
     *
     * @param nome      O nome completo do Cliente.
     * @param nif       O Número de Identificação Fiscal (NIF).
     * @param tel       O número de telemóvel de contacto.
     * @param morada    A morada completa do Cliente.
     * @param cartaoCid O número do Cartão de Cidadão.
     */
    public Cliente(String nome, int nif, int tel, String morada, int cartaoCid) {
        super(nome, nif, tel, morada, cartaoCid);
    }

    /**
     * Devolve a representação textual do Cliente.
     * Adiciona o número do Cartão de Cidadão aos dados base herdados de Pessoa.
     * @return Uma String formatada contendo os dados do cliente.
     */
    @Override
    public String toString() {
        return "[" + super.toString() + " | CC: " + getCartaoCid() + "]";
    }
}