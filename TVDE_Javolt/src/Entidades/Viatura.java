package Entidades;

/**
 * Representa uma viatura da frota da empresa TVDE.
 * <p>
 * Guarda os dados principais que identificam e caracterizam o veículo.
 * </p>
 *
 * @author Grupo 1 - Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2026-01-01
 */
public class Viatura {

    /**
     * A matrícula única do veículo.
     */
    private String matricula;

    /**
     * A marca do fabricante do veículo.
     */
    private String marca;

    /**
     * O modelo específico do veículo.
     */
    private String modelo;

    /**
     * O ano de fabrico do veículo.
     */
    private int anoFabrico;

    /**
     * Constrói uma nova instância de Viatura com todos os dados obrigatórios.
     *
     * @param matricula  A matrícula do carro.
     * @param marca      A marca do carro.
     * @param modelo     O modelo do carro.
     * @param anoFabrico O ano em que o carro foi fabricado.
     */
    public Viatura(String matricula, String marca, String modelo, int anoFabrico) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabrico = anoFabrico;
    }

    /**
     * Obtém a matrícula do veículo.
     *
     * @return A matrícula do carro.
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Define uma nova matrícula para o veículo.
     *
     * @param matricula A nova matrícula a atribuir.
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Obtém a marca do veículo.
     *
     * @return A marca do carro.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Define a marca do veículo.
     *
     * @param marca A nova marca do carro.
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Obtém o modelo do veículo.
     *
     * @return O modelo do carro.
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Define o modelo do veículo.
     *
     * @param modelo O novo modelo do carro.
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Obtém o ano de fabrico do veículo.
     *
     * @return O ano de fabrico.
     */
    public int getAnoFabrico() {
        return anoFabrico;
    }

    /**
     * Define o ano de fabrico do veículo.
     *
     * @param anoFabrico O novo ano de fabrico.
     */
    public void setAnoFabrico(int anoFabrico) {
        this.anoFabrico = anoFabrico;
    }

    /**
     * Devolve a representação textual da Viatura.
     *
     * @return Uma String formatada com matrícula, marca, modelo e ano.
     */
    @Override
    public String toString() {
        return "[" + matricula + " | " + marca + " | " + modelo + " | " + anoFabrico + "]";
    }
}