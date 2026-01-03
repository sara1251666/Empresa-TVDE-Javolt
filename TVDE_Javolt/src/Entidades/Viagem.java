package Entidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma Entidades.Viagem Realizada pela Gestao.Empresa TVDE.
 * Esta Classe Agrega as Entidades Entidades.Condutor, Entidades.Cliente e Entidades.Viatura,
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
     * Constutor Entidades.Viagem Vazio.
     */
    public Viagem() {
    }

    /**
     * Constutor Completo da CLasse Entidades.Viagem.
     *
     * @param condutor       O Motorista que Realizou a Entidades.Viagem.
     * @param cliente        O CLiente Transportado.
     * @param viatura        O Carro Utilizado.
     * @param dataHoraInicio Data e Hora do Inicio da Entidades.Viagem.
     * @param dataHoraFim    Data e Hora do Fim da Entidades.Viagem.
     * @param moradaOrigem   Local de Partida.
     * @param moradaDestino  Local de Chegada.
     * @param kms            Distância Percorrida em Kms.
     * @param custo          Valor Final Cobrado Pela Entidades.Viagem (em euros);
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
     * Obtém o Entidades.Condutor da Entidades.Viagem.
     *
     * @return Retorna o Entidades.Condutor da Entidades.Viagem.
     */
    public Condutor getCondutor() {
        return condutor;
    }

    /**
     * Define Novo o Entidades.Condutor da Entidades.Viagem.
     *
     * @param condutor Atribui o Novo Entidades.Condutor da Entidades.Viagem.
     */
    public void setCondutor(Condutor condutor) {
        this.condutor = condutor;
    }

    /**
     * Obtém o Entidades.Cliente da Entidades.Viagem.
     *
     * @return Retorna o Entidades.Cliente.
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Define o Novo CLiente da Entidades.Viagem.
     *
     * @param cliente Atribui o Novo Entidades.Cliente da Entidades.Viagem.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Obtém a Entidades.Viatura Usada na Entidades.Viagem.
     *
     * @return Retorna a Entidades.Viatura Usada na Entidades.Viagem.
     */
    public Viatura getViatura() {
        return viatura;
    }

    /**
     * Define a Nova Entidades.Viatura Para a Entidades.Viagem.
     *
     * @param viatura Atribui a Nova Entidades.Viatura para a Entidades.Viagem.
     */
    public void setViatura(Viatura viatura) {
        this.viatura = viatura;
    }

    /**
     * Obtém a Data e Hora do Inicio da Entidades.Viagem.
     *
     * @return Retorna a Data e Hora do Inicio da Entidades.Viagem.
     */
    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    /**
     * Define a Nova data e Hora de Início da Entidades.Viagem.
     *
     * @param dataHoraInicio Atribui a Nova Data e Hora do Inicio da Entidades.Viagem.
     */
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    /**
     * Obtém a Data e Hora do Fim da Entidades.Viagem.
     *
     * @return Retorna a Data e Hora do Fim da Entidades.Viagem.
     */
    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    /**
     * Define a Nova Data e Hora do Fim da Entidades.Viagem.
     *
     * @param dataHoraFim Atribui a Nova Data e Hora do Fim da Entidades.Viagem.
     */
    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    /**
     * Obtém a Morada de Origem da Entidades.Viagem.
     *
     * @return Retorma a Morada de Origem da Entidades.Viagem .
     */
    public String getMoradaOrigem() {
        return moradaOrigem;
    }

    /**
     * Define a Nova Morada de Origem da Entidades.Viagem.
     *
     * @param moradaOrigem Atribui a Nova Morada de Origem da Entidades.Viagem.
     */
    public void setMoradaOrigem(String moradaOrigem) {
        this.moradaOrigem = moradaOrigem;
    }

    /**
     * Obtém a Morada de Destino da Entidades.Viagem.
     *
     * @return Retorna a Morada de Destino da Entidades.Viagem.
     */
    public String getMoradaDestino() {
        return moradaDestino;
    }

    /**
     * Define a Nova Morada de Destino da Entidades.Viagem.
     *
     * @param moradaDestino Atribui a Nova Morada de Destino da Entidades.Viagem.
     */
    public void setMoradaDestino(String moradaDestino) {
        this.moradaDestino = moradaDestino;
    }

    /**
     * Obtém a Distâmncia Percorrida na Entidades.Viagem em Kms.
     *
     * @return Retorna a Distância Percorrida na Entidades.Viagem em Kms.
     */
    public double getKms() {
        return kms;
    }

    /**
     * Define a Distância Percorrida na Entidades.Viagem em Kms.
     *
     * @param kms Atribui a Distância Percorrida na Entidades.Viagem em Kms.
     */
    public void setKms(double kms) {
        this.kms = kms;
    }

    /**
     * Obtém o Custo da Entidades.Viagem (em Euros).
     *
     * @return Retorna o Valor da Entidades.Viagem (em Euros).
     */
    public double getCusto() {
        return custo;
    }

    /**
     * Define o Custo da Entidades.Viagem (em Euros).
     *
     * @param custo Atribui o Valor da Entidades.Viagem (em Euros).
     */
    public void setCusto(double custo) {
        this.custo = custo;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        return "[" + dataHoraInicio.format(formatter) + "] " +
                "Condutor: " + condutor.getNome() +
                " | Cliente: " + cliente.getNome() +
                " | " + moradaOrigem + " -> " + moradaDestino +
                " (" + kms + " km)" + " | Custo: " + custo + "€";
    }
}