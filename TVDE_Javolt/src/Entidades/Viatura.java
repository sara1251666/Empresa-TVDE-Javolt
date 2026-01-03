package Entidades;

/**
 * Classe Viaturas
 * Representa um carro da empresa TVDE.
 * Guarda os dados principais do veículo.
 * @author Leonardo
 * @version 1
 * @since 12/12/2025
 */
public class Viatura {

    private String matricula;
    private String marca;
    private String modelo;
    private int anoFabrico;

    /**
     * Construtor da Classe Viaturas.
     * Cria um Carro com Todos os Seus Dados.
     * @param matricula Matrícula do Carro.
     * @param marca Marca do Carro.
     * @param modelo Modelo do Carro.
     * @param anoFabrico Ano de Fabrico do Carro.
     */
    public Viatura(String matricula, String marca, String modelo, int anoFabrico) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabrico = anoFabrico;
    }

    /**
     * Devolve a matrícula do carro.
     * @return matrícula do carro
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Altera a matrícula do carro.
     * @param matricula nova matrícula do carro
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Devolve a marca do carro.
     * @return marca do carro
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Altera a marca do carro.
     * @param marca nova marca do carro
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Devolve o modelo do carro.
     * @return modelo do carro
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Altera o modelo do carro.
     * @param modelo novo modelo do carro
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Devolve o ano de fabrico do carro.
     * @return ano de fabrico do carro
     */
    public int getAnoFabrico() {
        return anoFabrico;
    }

    /**
     * Altera o ano de fabrico do carro.
     * @param anoFabrico novo ano de fabrico do carro
     */
    public void setAnoFabrico(int anoFabrico) {
        this.anoFabrico = anoFabrico;
    }

    /**
     * Converte os dados do carro para texto.
     * @return texto com matrícula, marca, modelo e ano de fabrico
     */
    @Override
    public String toString() {
        return "[" + matricula + " | " + marca + " | " + modelo + " | " + anoFabrico + "]";
    }
}