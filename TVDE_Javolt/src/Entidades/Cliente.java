package Entidades;

/**
 * Representa um Cliente que utiliza os serviços da empresa.
 * <p>
 * Herda diretamente de {@link Pessoa}. Embora atualmente não tenha atributos adicionais,
 * a criação desta classe específica permite distinguir semanticamente um Cliente de um Condutor
 * e facilita a expansão futura (ex: adicionar pontos de fidelidade, histórico de reservas).
 * </p>
 * <p>
 * O NIF (herdado de {@link Pessoa}) serve como identificador único do cliente.
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
     * @param nif       O Número de Identificação Fiscal (NIF) - 9 dígitos.
     * @param tel       O número de telemóvel de contacto.
     * @param morada    A morada completa do Cliente.
     * @param cartaoCid O número do Cartão de Cidadão.
     */
    public Cliente(String nome, int nif, int tel, String morada, int cartaoCid) {
        super(nome, nif, tel, morada, cartaoCid);
    }

    /**
     * Devolve a representação textual do Cliente.
     * <p>
     * Adiciona o identificador "Cliente:" ao início e inclui o número do Cartão de Cidadão.
     * </p>
     *
     * @return Uma String formatada contendo os dados do cliente.
     */
    @Override
    public String toString() {
        return "Cliente: " + super.toString() + " | CC: " + getCartaoCid();
    }
}