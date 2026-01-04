package Entidades;

/**
 * Representa um Cliente da empresa TVDE.
 * <p>
 * Esta classe herda de {@link Pessoa} e representa os utilizadores
 * que utilizam os serviços de transporte da empresa.
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2026-01-01
 */
public class Cliente extends Pessoa {

    /**
     * Construtor vazio.
     * <p>
     * Permite instanciar um Cliente sem definir dados iniciais,
     * sendo necessário preenchê-los posteriormente via métodos <i>set</i>.
     * </p>
     */
    public Cliente() {
    }

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
     *
     * @return Uma String formatada contendo os dados do cliente.
     */
    @Override
    public String toString() {
        return "[" + super.toString() + " | CC: " + getCartaoCid() + "]";
    }
}