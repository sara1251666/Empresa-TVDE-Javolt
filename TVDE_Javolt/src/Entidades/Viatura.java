package Entidades;

/**
 * Representa uma viatura da frota da empresa TVDE.
 * <p>
 * Esta classe armazena as características físicas do veículo e serve como
 * objeto base para as validações de disponibilidade nas viagens.
 * A matrícula serve como identificador único do veículo.
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.1
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
     * Construtor vazio (opcional, mas útil para algumas operações).
     */
    public Viatura() {
    }

    /**
     * Constrói uma nova instância de Viatura.
     * @param matricula  A matrícula (deve ser única no sistema).
     * @param marca      A marca do veículo.
     * @param modelo     O modelo do veículo.
     * @param anoFabrico O ano de fabrico.
     */
    public Viatura(String matricula, String marca, String modelo, int anoFabrico) {
        // Validações básicas
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("Matrícula não pode ser vazia");
        }
        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("Marca não pode ser vazia");
        }
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException("Modelo não pode ser vazio");
        }
        if (anoFabrico < 1886 || anoFabrico > java.time.Year.now().getValue() + 1) {
            throw new IllegalArgumentException("Ano de fabrico inválido");
        }

        this.matricula = matricula.trim();
        this.marca = marca.trim();
        this.modelo = modelo.trim();
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
     * @throws IllegalArgumentException Se a matrícula for nula ou vazia.
     */
    public void setMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("Matrícula não pode ser vazia");
        }
        this.matricula = matricula.trim().toUpperCase();
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
     * @throws IllegalArgumentException Se a marca for nula ou vazia.
     */
    public void setMarca(String marca) {
        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("Marca não pode ser vazia");
        }
        this.marca = marca.trim();
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
     * @throws IllegalArgumentException Se o modelo for nulo ou vazio.
     */
    public void setModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException("Modelo não pode ser vazio");
        }
        this.modelo = modelo.trim();
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
     * @throws IllegalArgumentException Se o ano for inválido.
     */
    public void setAnoFabrico(int anoFabrico) {
        // Primeiro carro foi em 1886 (Benz Patent-Motorwagen)
        int anoAtual = java.time.Year.now().getValue();
        if (anoFabrico < 1886 || anoFabrico > anoAtual + 1) { // +1 para carros futuros
            throw new IllegalArgumentException("Ano de fabrico inválido: " + anoFabrico);
        }
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