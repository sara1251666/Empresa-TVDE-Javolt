package Entidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma Viagem realizada no âmbito da atividade da empresa TVDE.
 * <p>
 * Esta classe é o núcleo da operação, agregando as entidades {@link Condutor},
 * {@link Cliente} e {@link Viatura}, além de registar os dados temporais,
 * espaciais e financeiros do serviço.
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2026-01-01
 */
public class Viagem {

    /**
     * O condutor responsável pela viagem.
     */
    private Condutor condutor;

    /**
     * O cliente que usufruiu do serviço de transporte.
     */
    private Cliente cliente;

    /**
     * A viatura utilizada para realizar o transporte.
     */
    private Viatura viatura;

    /**
     * A data e hora em que a viagem teve início.
     */
    private LocalDateTime dataHoraInicio;

    /**
     * A data e hora em que a viagem terminou.
     */
    private LocalDateTime dataHoraFim;

    /**
     * A morada ou local de partida.
     */
    private String moradaOrigem;

    /**
     * A morada ou local de chegada.
     */
    private String moradaDestino;

    /**
     * A distância percorrida durante a viagem (em quilómetros).
     */
    private double kms;

    /**
     * O custo final cobrado ao cliente pela viagem (em euros).
     */
    private double custo;

    /**
     * Construtor vazio.
     * <p>
     * Permite instanciar uma Viagem sem definir dados iniciais.
     * </p>
     */
    public Viagem() {
    }

    /**
     * Constrói uma nova Viagem com todos os dados necessários.
     *
     * @param condutor       O motorista que realizou a viagem.
     * @param cliente        O cliente transportado.
     * @param viatura        O carro utilizado.
     * @param dataHoraInicio Data e hora do início da viagem.
     * @param dataHoraFim    Data e hora do fim da viagem.
     * @param moradaOrigem   Local de partida.
     * @param moradaDestino  Local de chegada.
     * @param kms            Distância percorrida em Kms.
     * @param custo          Valor final cobrado pela viagem (em euros).
     */
    public Viagem(Condutor condutor, Cliente cliente, Viatura viatura,
                  LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                  String moradaOrigem, String moradaDestino, double kms, double custo) {

        if (condutor == null || cliente == null || viatura == null) {
            throw new IllegalArgumentException("Condutor, Cliente e Viatura são obrigatórios");
        }
        if (dataHoraFim.isBefore(dataHoraInicio)) {
            throw new IllegalArgumentException("Data de fim não pode ser anterior à data de início");
        }
        if (kms < 0 || custo < 0) {
            throw new IllegalArgumentException("Kms e custo não podem ser negativos");
        }

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
     * Obtém o condutor associado à viagem.
     *
     * @return O objeto {@link Condutor} da viagem.
     */
    public Condutor getCondutor() {
        return condutor;
    }

    /**
     * Define o condutor da viagem.
     *
     * @param condutor O novo condutor a atribuir.
     */
    public void setCondutor(Condutor condutor) {
        this.condutor = condutor;
    }

    /**
     * Obtém o cliente associado à viagem.
     *
     * @return O objeto {@link Cliente} da viagem.
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Define o cliente da viagem.
     *
     * @param cliente O novo cliente a atribuir.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Obtém a viatura utilizada na viagem.
     *
     * @return O objeto {@link Viatura} utilizado.
     */
    public Viatura getViatura() {
        return viatura;
    }

    /**
     * Define a viatura da viagem.
     *
     * @param viatura A nova viatura a atribuir.
     */
    public void setViatura(Viatura viatura) {
        this.viatura = viatura;
    }

    /**
     * Obtém a data e hora de início.
     *
     * @return O momento do início da viagem.
     */
    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    /**
     * Define a data e hora de início.
     *
     * @param dataHoraInicio O novo momento de início.
     */
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    /**
     * Obtém a data e hora de fim.
     *
     * @return O momento do fim da viagem.
     */
    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    /**
     * Define a data e hora de fim.
     *
     * @param dataHoraFim O novo momento de fim.
     */
    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    /**
     * Obtém a morada de origem.
     *
     * @return O local de partida.
     */
    public String getMoradaOrigem() {
        return moradaOrigem;
    }

    /**
     * Define a morada de origem.
     *
     * @param moradaOrigem O novo local de partida.
     */
    public void setMoradaOrigem(String moradaOrigem) {
        this.moradaOrigem = moradaOrigem;
    }

    /**
     * Obtém a morada de destino.
     *
     * @return O local de chegada.
     */
    public String getMoradaDestino() {
        return moradaDestino;
    }

    /**
     * Define a morada de destino.
     *
     * @param moradaDestino O novo local de chegada.
     */
    public void setMoradaDestino(String moradaDestino) {
        this.moradaDestino = moradaDestino;
    }

    /**
     * Obtém a distância percorrida.
     *
     * @return A distância em quilómetros.
     */
    public double getKms() {
        return kms;
    }

    /**
     * Define a distância percorrida.
     *
     * @param kms A nova distância em Kms.
     */
    public void setKms(double kms) {
        this.kms = kms;
    }

    /**
     * Obtém o custo da viagem.
     *
     * @return O valor em euros.
     */
    public double getCusto() {
        return custo;
    }

    /**
     * Define o custo da viagem.
     *
     * @param custo O novo valor em euros.
     */
    public void setCusto(double custo) {
        this.custo = custo;
    }

    /**
     * Devolve a representação textual da Viagem.
     * <p>
     * Formata a data e apresenta um resumo com Condutor, Cliente, Percurso, Distância e Custo.
     * </p>
     *
     * @return Uma String formatada com os detalhes da viagem.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        return "[" + dataHoraInicio.format(formatter) + "] " +
                "Condutor: " + condutor.getNome() +
                " | Cliente: " + cliente.getNome() +
                " | Matricula viatura: " + viatura.getMatricula() +
                " | " + moradaOrigem + " -> " + moradaDestino +
                " (" + kms + " km)" + " | Custo: " + custo + "€";
    }
}