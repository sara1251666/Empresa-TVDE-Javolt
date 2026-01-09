package Entidades;

/**
 * Representa uma viatura da frota da empresa TVDE.
 * <p>
 * Esta classe armazena as características físicas do veículo e serve como
 * objeto base para as validações de disponibilidade nas viagens.
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2026-01-08
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
     * Constrói uma nova instância de Viatura.
     * @param matricula  A matrícula (deve ser única no sistema).
     * @param marca      A marca do veículo.
     * @param modelo     O modelo do veículo.
     * @param anoFabrico O ano de fabrico.
     */
    public Viatura(String matricula, String marca, String modelo, int anoFabrico) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabrico = anoFabrico;
    }

    /**
     * Obtém a matrícula do veículo.
     * @return A matrícula do carro.
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Define uma nova matrícula para o veículo.
     * @param matricula A nova matrícula a atribuir.
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Obtém a marca do veículo.
     * @return A marca do carro.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Define a marca do veículo.
     * @param marca A nova marca do carro.
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Obtém o modelo do veículo.
     * @return O modelo do carro.
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Define o modelo do veículo.
     * @param modelo O novo modelo do carro.
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Obtém o ano de fabrico do veículo.
     * @return O ano de fabrico.
     */
    public int getAnoFabrico() {
        return anoFabrico;
    }

    /**
     * Define o ano de fabrico do veículo.
     * @param anoFabrico O novo ano de fabrico.
     */
    public void setAnoFabrico(int anoFabrico) {
        this.anoFabrico = anoFabrico;
    }

    /**
     * Devolve a representação textual da Viatura.
     * @return Uma String formatada com matrícula, marca, modelo e ano.
     */
    @Override
    public String toString() {
        return "[" + matricula + " | " + marca + " | " + modelo + " | " + anoFabrico + "]";
    }
}