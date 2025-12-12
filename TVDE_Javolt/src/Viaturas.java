/**
 * Classe Veiculo
 * Representa uma viatura da empresa TVDE.
 * @author Leonardo
 * @version 1
 * @since 12/12/2025
 */
public class Viaturas {

    // Atributos
    private String matricula;
    private String marca;
    private String modelo;
    private int anoFabrico;

    /**
     * Construtor Viatura
     * @param matricula tipo string - matricula do carro
     * @param marca tipo string - marca do carro
     * @param modelo tipo string - modelo do carro
     * @param anoFabrico tipo int ano do carro
     */
    public Viaturas(String matricula, String marca, String modelo, int anoFabrico) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabrico = anoFabrico;
    }

    /**
     * Getter da Matricula
     * @return matricula - matricula do carro
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Setter da Matricula
     * @param matricula tipo string - matricula do carro
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Getter da Marca
     * @return marca - marca do carro
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Setter marca
     * @param marca - marca do carro
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Getter Modelo
     * @return Modelo - Modelo do Carro
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Setter Modelo
     * @param modelo - Modelo do Carro
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Getter Ano de Fabrico
     * @return AnoFabrico - Ano de Fabrico do Carro
     */
    public int getAnoFabrico() {
        return anoFabrico;
    }

    /**
     * Setter Ano de Fabrico
     * @param anoFabrico - Ano de Fabrico do Carro
     */
    public void setAnoFabrico(int anoFabrico) {
        this.anoFabrico = anoFabrico;
    }

    /**
     * Metodo toString
     * @return Detalhes do Veiculo (matricula, marca, modela e anoFabrico)
     */
    @Override
    public String toString() {
        return "Veiculo {" +
                "matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", anoFabrico=" + anoFabrico +
                '}';
    }
}

