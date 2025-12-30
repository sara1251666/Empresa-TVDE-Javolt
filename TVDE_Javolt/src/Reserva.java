import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma Intenção de Viagem Solicitada por um Cliente.
 * Contém os Dados do Serviço Solicitado Antes de ser Realizado.
 * @author Levi e Sara
 * @version 1
 * @since 2025-12-27
 */
public class Reserva {
    private Cliente cliente;
    private LocalDateTime dataHoraInicio;
    private String moradaOrigem;
    private String moradaDestino;
    private double kms;

    /**
     * Construtor Reserva Vazio.
     */
    public Reserva() {
    }

    /**
     * Constrói uma Nova Reserva de um Determinado Cliente.
     * @param cliente O cliente Que Fez a Reserva.
     * @param dataHoraInicio A Data e Hora Pretendida.
     * @param moradaOrigem Local de Recolha.
     * @param moradaDestino Local de Destino.
     * @param kms Distância Estimada.
     */
    public Reserva(Cliente cliente, LocalDateTime dataHoraInicio, String moradaOrigem, String moradaDestino, double kms) {
        this.cliente = cliente;
        this.dataHoraInicio = dataHoraInicio;
        this.moradaOrigem = moradaOrigem;
        this.moradaDestino = moradaDestino;
        this.kms = kms;
    }

    /**
     * Obtém o Cliente Associado à Reserva.
     * @return Retorna o Cliente.
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Define o Cliente Associado à Reserva.
     * @param cliente Atribui o Cliente à Reserva.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Obtém a Data e a Hora do Inicio da Reserva.
     * @return Retorna a Data e Hora da Reserva.
     */
    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    /**
     * Define a Data e a Hora de Inicio da Reserva.
     * @param dataHoraInicio Atribui a Nova data e a Hora de Início da Reserva.
     */
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    /**
     * Obtém a Morada de Origem.
     * @return Retorna a Morada de Origem.
     */
    public String getMoradaOrigem() {
        return moradaOrigem;
    }

    /**
     * Define a Nova Morada de Origem.
     * @param moradaOrigem Atribui a Nova Morada de Origem.
     */
    public void setMoradaOrigem(String moradaOrigem) {
        this.moradaOrigem = moradaOrigem;
    }

    /**
     * Obtém a Morada de Destino.
     * @return Retorna a Morada de Destino.
     */
    public String getMoradaDestino() {
        return moradaDestino;
    }

    /**
     * Define a Nova Morada de Destino.
     * @param moradaDestino Atribui a Nova Morada de Destino.
     */
    public void setMoradaDestino(String moradaDestino) {
        this.moradaDestino = moradaDestino;
    }

    /**
     * Obtém a Disância da Reserva em Kms.
     * @return Retorna a Distância da Viagem em Kms.
     */
    public double getKms() {
        return kms;
    }

    /**
     * Defime a Nova Distância da Reserva em Kms.
     * @param kms Agtribui a Nova Distância da Reserva em Kms.
     */
    public void setKms(double kms) {
        this.kms = kms;
    }

    /**
     * Representação em Texto da Reserva.
     * Formata a data para ser legível.
     * @return Detalhes da Reserva.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return " Reserva [Cliente: " + cliente.getNome() +
                " | Data: " + getDataHoraInicio().format(formatter) +
                " | De: " + moradaOrigem + " Para: " + moradaDestino + "]";
    }
}

