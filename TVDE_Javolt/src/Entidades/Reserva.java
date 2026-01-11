package Entidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Representa uma intenção de viagem solicitada por um cliente.
 * <p>
 * Contém os dados do serviço solicitado (quem, quando, onde) antes de este ser efetivamente realizado
 * ou atribuído a um condutor. Uma reserva pode ser convertida numa {@link Viagem} quando
 * são atribuídos um condutor e uma viatura.
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2026-01-01
 */
public class Reserva {
    /**
     * O cliente que efetuou a reserva.
     */
    private Cliente cliente;

    /**
     * A data e hora pretendida para o início do serviço.
     */
    private LocalDateTime dataHoraInicio;

    /**
     * A morada ou local de recolha do passageiro.
     */
    private String moradaOrigem;

    /**
     * A morada ou local de destino da viagem.
     */
    private String moradaDestino;

    /**
     * A distância estimada da viagem em quilómetros.
     */
    private double kms;

    /**
     * Constrói uma nova Reserva com todos os dados necessários.
     *
     * @param cliente        O cliente que solicitou a reserva.
     * @param dataHoraInicio A data e hora pretendida para a viagem.
     * @param moradaOrigem   O local de recolha.
     * @param moradaDestino  O local de destino.
     * @param kms            A distância estimada em Kms.
     */
    public Reserva(Cliente cliente, LocalDateTime dataHoraInicio, String moradaOrigem, String moradaDestino, double kms) {
        this.cliente = cliente;
        this.dataHoraInicio = dataHoraInicio;
        this.moradaOrigem = moradaOrigem;
        this.moradaDestino = moradaDestino;
        this.kms = kms;
    }

    /**
     * Obtém o cliente associado à reserva.
     *
     * @return O objeto {@link Cliente} desta reserva.
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Define o cliente associado à reserva.
     *
     * @param cliente O novo cliente a atribuir.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Obtém a data e hora marcada para a reserva.
     *
     * @return A data e hora de início.
     */
    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    /**
     * Define a data e hora de início da reserva.
     *
     * @param dataHoraInicio A nova data e hora de início.
     */
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    /**
     * Obtém a morada de origem.
     *
     * @return O local de recolha.
     */
    public String getMoradaOrigem() {
        return moradaOrigem;
    }

    /**
     * Define a morada de origem.
     *
     * @param moradaOrigem O novo local de recolha.
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
     * Obtém a distância estimada da reserva.
     *
     * @return A distância em quilómetros.
     */
    public double getKms() {
        return kms;
    }

    /**
     * Define a distância da reserva.
     *
     * @param kms A nova distância em Kms.
     */
    public void setKms(double kms) {
        this.kms = kms;
    }

    /**
     * Devolve a representação textual da Reserva.
     * <p>
     * Formata a data para leitura fácil e apresenta um resumo do pedido.
     * </p>
     *
     * @return Uma String formatada com os detalhes da reserva.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return "[" + getDataHoraInicio().format(formatter) + "] " + getCliente().getNome() + " | " +
                getMoradaOrigem() + " -> " + getMoradaDestino() + " (" + getKms() + " km)";
    }
}

