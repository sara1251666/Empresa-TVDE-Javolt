import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma Viagem Realizada pela Empresa TVDE.
 * Esta Classe Agrega as Entidades Condutor, Cliente e Viatura,
 * Além de Registar o Tempo, Distância e o Custo Financeiro.
 * @author Levi e Sara
 * @version 1
 * @since 2025-12-27
 */
public class Viagem {
    private Condutor condutor;
    private Cliente cliente;
    private Viatura viatura;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String moradaOrigem;
    private String moradaDestino;
    private double kms;
    private double custo;

    /**
     * Constutor Viagem Vazio.
     */
    public Viagem() {
    }

    /**
     * Constutor Completo da CLasse Viagem.
     * @param condutor O Motorista que Realizou a Viagem.
     * @param cliente O CLiente Transportado.
     * @param viatura O Carro Utilizado.
     * @param dataHoraInicio Data e Hora do Inicio da Viagem.
     * @param dataHoraFim Data e Hora do Fim da Viagem.
     * @param moradaOrigem Local de Partida.
     * @param moradaDestino Local de Chegada.
     * @param kms Distância Percorrida em Kms.
     * @param custo Valor Final Cobrado Pela Viagem (em euros);
     */
    public Viagem(Condutor condutor, Cliente cliente, Viatura viatura, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, String moradaOrigem, String moradaDestino, double kms, double custo) {
        this.condutor = condutor;
        this.cliente = cliente;
        this.viatura = viatura;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.moradaOrigem = moradaOrigem;
        this.moradaDestino = moradaDestino;
        this.kms = kms;
        this.custo = custo;
    }

    /**
     * Obtém o Condutor da Viagem.
     * @return Retorna o Condutor da Viagem.
     */
    public Condutor getCondutor() {
        return condutor;
    }

    /**
     * Define Novo o Condutor da Viagem.
     * @param condutor Atribui o Novo Condutor da Viagem.
     */
    public void setCondutor(Condutor condutor) {
        this.condutor = condutor;
    }

    /**
     * Obtém o Cliente da Viagem.
     * @return Retorna o Cliente.
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Define o Novo CLiente da Viagem.
     * @param cliente Atribui o Novo Cliente da Viagem.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Obtém a Viatura Usada na Viagem.
     * @return Retorna a Viatura Usada na Viagem.
     */
    public Viatura getViatura() {
        return viatura;
    }

    /**
     * Define a Nova Viatura Para a Viagem.
     * @param viatura Atribui a Nova Viatura para a Viagem.
     */
    public void setViatura(Viatura viatura) {
        this.viatura = viatura;
    }

    /**
     * Obtém a Data e Hora do Inicio da Viagem.
     * @return Retorna a Data e Hora do Inicio da Viagem.
     */
    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    /**
     * Define a Nova data e Hora de Início da Viagem.
     * @param dataHoraInicio Atribui a Nova Data e Hora do Inicio da Viagem.
     */
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    /**
     * Obtém a Data e Hora do Fim da Viagem.
     * @return Retorna a Data e Hora do Fim da Viagem.
     */
    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    /**
     * Define a Nova Data e Hora do Fim da Viagem.
     * @param dataHoraFim Atribui a Nova Data e Hora do Fim da Viagem.
     */
    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    /**
     * Obtém a Morada de Origem da Viagem.
     * @return Retorma a Morada de Origem da Viagem .
     */
    public String getMoradaOrigem() {
        return moradaOrigem;
    }

    /**
     * Define a Nova Morada de Origem da Viagem.
     * @param moradaOrigem Atribui a Nova Morada de Origem da Viagem.
     */
    public void setMoradaOrigem(String moradaOrigem) {
        this.moradaOrigem = moradaOrigem;
    }

    /**
     * Obtém a Morada de Destino da Viagem.
     * @return Retorna a Morada de Destino da Viagem.
     */
    public String getMoradaDestino() {
        return moradaDestino;
    }

    /**
     * Define a Nova Morada de Destino da Viagem.
     * @param moradaDestino Atribui a Nova Morada de Destino da Viagem.
     */
    public void setMoradaDestino(String moradaDestino) {
        this.moradaDestino = moradaDestino;
    }

    /**
     * Obtém a Distâmncia Percorrida na Viagem em Kms.
     * @return Retorna a Distância Percorrida na Viagem em Kms.
     */
    public double getKms() {
        return kms;
    }

    /**
     * Define a Distância Percorrida na Viagem em Kms.
     * @param kms Atribui a Distância Percorrida na Viagem em Kms.
     */
    public void setKms(double kms) {
        this.kms = kms;
    }

    /**
     * Obtém o Custo da Viagem (em Euros).
     * @return Retorna o Valor da Viagem (em Euros).
     */
    public double getCusto() {
        return custo;
    }

    /**
     * Define o Custo da Viagem (em Euros).
     * @param custo Atribui o Valor da Viagem (em Euros).
     */
    public void setCusto(double custo) {
        this.custo = custo;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        return String.format(String.format(String.format(String.format("Viagem {" +
                "Condutor: " + condutor.getNome() +
                " | Cliente: " + cliente.getNome() +
                " | Viatura: " + viatura.getMatricula() +
                "\n Início: " + dataHoraInicio.format(formatter) +
                " | Fim: " + dataHoraFim.format(formatter) +
                "\n Trajeto: " +moradaOrigem + " -> " + moradaDestino +
                " | Distância: " + kms + " km | Custo: "+ custo + "€" +
                "}";
        )
    }
}
