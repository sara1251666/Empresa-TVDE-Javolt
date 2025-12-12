/**
 * Classe Veiculo
 * Representa uma viatura da empresa TVDE.
 * @author Leonardo
 * @version 1
 * @since 12/12/2025
 */
public class Viaturas {

    // Número da matrícula do carro
    private String matricula;

    // Marca do carro (ex: Toyota, BMW)
    private String marca;

    // Modelo do carro (ex: Corolla, Série 3)
    private String modelo;

    // Ano em que o carro foi fabricado
    private int anoFabrico;

    /**
     * Construtor da classe Viaturas
     * Serve para criar um novo carro com todos os seus dados.
     *
     * @param matricula matrícula do carro
     * @param marca marca do carro
     * @param modelo modelo do carro
     * @param anoFabrico ano em que o carro foi fabricado
     */
    public Viaturas(String matricula, String marca, String modelo, int anoFabrico) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabrico = anoFabrico;
    }

    /**
     * Devolve a matrícula do carro
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Altera a matrícula do carro
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Devolve a marca do carro
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Altera a marca do carro
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Devolve o modelo do carro
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Altera o modelo do carro
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Devolve o ano de fabrico do carro
     */
    public int getAnoFabrico() {
        return anoFabrico;
    }

    /**
     * Altera o ano de fabrico do carro
     */
    public void setAnoFabrico(int anoFabrico) {
        this.anoFabrico = anoFabrico;
    }

    /**
     * Mostra todas as informações do carro em texto
     */
    @Override
    public String toString() {
        return "Veículo {" +
                "Matrícula = '" + matricula + '\'' +
                ", Marca = '" + marca + '\'' +
                ", Modelo = '" + modelo + '\'' +
                ", Ano de Fabrico = " + anoFabrico +
                '}';
    }
}
