package Gestao;

import Entidades.*;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A classe Empresa atua como o gestor central de dados do sistema TVDE.
 * <p>
 * Responsável por armazenar em memória (ArrayLists) todas as entidades
 * (Viaturas, Condutores, Clientes, Viagens e Reservas) e executar
 * a lógica de negócio principal, incluindo validações de integridade
 * referencial e persistência de dados em ficheiros.
 * Suporta Múltiplas empresas através da gestão de pastas de logs dinâmicas.
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2026-01-01
 */
public class Empresa {

    /**
     * Lista de viaturas registadas na empresa.
     */
    private ArrayList<Viatura> viaturas;

    /**
     * Lista de condutores que trabalham na empresa.
     */
    private ArrayList<Condutor> condutores;

    /**
     * Lista de clientes registados na plataforma.
     */
    private ArrayList<Cliente> clientes;

    /**
     * Histórico de viagens realizadas.
     */
    private ArrayList<Viagem> viagens;

    /**
     * Lista de reservas futuras efetuadas por clientes.
     */
    private ArrayList<Reserva> reservas;

    /**
     * O nome da pasta onde os ficheiros desta empresa específica serão guardados.
     */
    private String nomePasta;

    /**
     * Construtor da classe Empresa.
     * Inicializa todas as listas (ArrayLists) vazias prontas para armazenar dados.
     */
    public Empresa(String nomeEmpresa) {
        this.viaturas = new ArrayList<>();
        this.condutores = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.viagens = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.nomePasta = "Empresas/Logs_" + nomeEmpresa;
    }

    // ==========================================================
    //                        CRUD VIATURAS
    // ==========================================================

    /**
     * Adiciona uma nova viatura ao sistema.
     * Verifica se a matrícula já existe para evitar duplicados.
     *
     * @param v O objeto Viatura a ser adicionado.
     * @return {@code true} se a viatura foi adicionada com sucesso;
     * {@code false} se já existir uma viatura com a mesma matrícula.
     */
    public boolean adicionarViatura(Viatura v) {
        if (procurarViatura(v.getMatricula()) == null) {
            viaturas.add(v);
            return true;
        }
        return false; // Matrícula já existe
    }

    /**
     * Obtém a lista completa de viaturas.
     *
     * @return ArrayList contendo todas as viaturas registadas.
     */
    public ArrayList<Viatura> getViaturas() {
        return viaturas;
    }

    /**
     * Procura uma viatura específica através da matrícula.
     *
     * @param matricula A matrícula da viatura a pesquisar (ex: "AA-00-BB").
     * @return O objeto {@link Viatura} se encontrado, ou {@code null} caso contrário.
     */
    public Viatura procurarViatura(String matricula) {
        for (Viatura v : viaturas) {
            if (v.getMatricula().equalsIgnoreCase(matricula)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Remove uma viatura do sistema, garantindo a integridade dos dados.
     * Não permite a remoção se a viatura estiver associada a um histórico de viagens.
     *
     * @param matricula A matrícula da viatura a remover.
     * @return {@code true} se removido com sucesso;
     * {@code false} se a viatura não existir ou tiver viagens associadas.
     */
    public boolean removerViatura(String matricula) {
        Viatura v = procurarViatura(matricula);
        if (v != null) {
            // Verificar dependências em Viagens
            for (Viagem viagem : viagens) {
                if (viagem.getViatura().getMatricula().equalsIgnoreCase(matricula)) {
                    System.out.println("Erro: Não é possível remover. Viatura associada a uma viagem.");
                    return false;
                }
            }
            // Se não houver dependências, remove
            viaturas.remove(v);
            return true;
        }
        return false;
    }

    // ==========================================================
    //                        CRUD CLIENTES
    // ==========================================================

    /**
     * Adiciona um novo cliente ao sistema.
     * Verifica se o NIF já existe para evitar duplicados.
     *
     * @param c O objeto Cliente a adicionar.
     * @return {@code true} se adicionado com sucesso; {@code false} se o NIF já existir.
     */
    public boolean adicionarCliente(Cliente c) {
        // 1. Procura se já existe alguém com este NIF na lista
        if (procurarCliente(c.getNif()) == null) {
            // 2. Se devolver null (não encontrou), adiciona.
            clientes.add(c);
            return true;
        }
        // 3. Se encontrou, devolve falso e não adiciona.
        return false;
    }

    /**
     * Obtém a lista completa de clientes.
     *
     * @return ArrayList contendo todos os clientes.
     */
    public ArrayList<Cliente> getClientes() {
        return clientes;
    }

    /**
     * Procura um cliente específico através do NIF.
     *
     * @param nif O Número de Identificação Fiscal do cliente.
     * @return O objeto {@link Cliente} se encontrado, ou {@code null} caso contrário.
     */
    public Cliente procurarCliente(int nif) {
        for (Cliente c : clientes) {
            if (c.getNif() == nif) {
                return c;
            }
        }
        return null;
    }

    /**
     * Remove um cliente do sistema.
     * Impede a remoção se o cliente tiver histórico de viagens ou reservas ativas.
     *
     * @param nif O NIF do cliente a remover.
     * @return {@code true} se removido com sucesso;
     * {@code false} se tiver dependências ou não existir.
     */
    public boolean removerCliente(int nif) {
        Cliente c = procurarCliente(nif);
        if (c != null) {
            // Verificar dependências em Viagens
            for (Viagem viagem : viagens) {
                if (viagem.getCliente().getNif() == nif) {
                    System.out.println("Erro: Cliente possui histórico de viagens.");
                    return false;
                }
            }
            // Verificar dependências em Reservas
            for (Reserva reserva : reservas) {
                if (reserva.getCliente().getNif() == nif) {
                    System.out.println("Erro: Cliente possui reservas ativas.");
                    return false;
                }
            }
            clientes.remove(c);
            return true;
        }
        return false;
    }

    // ==========================================================
    //                        CRUD CONDUTORES
    // ==========================================================

    /**
     * Adiciona um novo condutor ao sistema.
     * Verifica se o NIF já existe para evitar duplicados.
     *
     * @param c O objeto Condutor a adicionar.
     * @return {@code true} se adicionado com sucesso; {@code false} se o NIF já existir.
     */
    public boolean adicionarCondutor(Condutor c) {
        if (procurarCondutor(c.getNif()) == null) {
            condutores.add(c);
            return true;
        }
        return false;
    }

    /**
     * Obtém a lista completa de condutores.
     *
     * @return ArrayList de condutores.
     */
    public ArrayList<Condutor> getCondutores() {
        return condutores;
    }

    /**
     * Procura um condutor pelo NIF.
     *
     * @param nif O NIF do condutor.
     * @return O objeto {@link Condutor} se encontrado, ou {@code null} caso contrário.
     */
    public Condutor procurarCondutor(int nif) {
        for (Condutor c : condutores) {
            if (c.getNif() == nif) {
                return c;
            }
        }
        return null;
    }

    /**
     * Remove um condutor do sistema se este não tiver viagens realizadas.
     *
     * @param nif O NIF do condutor a remover.
     * @return {@code true} se removido com sucesso; {@code false} caso contrário.
     */
    public boolean removerCondutor(int nif) {
        Condutor c = procurarCondutor(nif);
        if (c != null) {
            for (Viagem viagem : viagens) {
                if (viagem.getCondutor().getNif() == nif) {
                    System.out.println("Erro: Condutor possui histórico de viagens.");
                    return false;
                }
            }
            condutores.remove(c);
            return true;
        }
        return false;
    }

    // ==========================================================
    //                 GESTÃO DE VIAGENS E RESERVAS
    // ==========================================================

    /**
     * Obtém uma lista de condutores que não têm viagens marcadas no intervalo de tempo fornecido.
     * <p>
     * Percorre a lista de condutores e, para cada um, verifica se existe alguma sobreposição
     * com as viagens já registadas no sistema.
     * </p>
     *
     * @param inicio Data e Hora de início pretendida para o serviço.
     * @param fim    Data e Hora de fim pretendida para o serviço.
     * @return Uma lista (ArrayList) contendo apenas os condutores disponíveis.
     */
    public ArrayList<Condutor> getCondutoresDisponiveis(LocalDateTime inicio, LocalDateTime fim) {
        ArrayList<Condutor> condutoresDisponiveis = new ArrayList<>();

        for (Condutor condutor : condutores) {
            boolean estaOcupado = false;
            //Verifica se este condutor tem alguma viagem que colida com o horário.
            for (Viagem viagem : viagens) {
                if (viagem.getCondutor().getNif() == condutor.getNif()) {
                    //Lógica de sobreposição (InicioA < FimB) && (FimA > InicioB)
                    if (inicio.isBefore(viagem.getDataHoraFim()) && fim.isAfter(viagem.getDataHoraInicio())) {
                        estaOcupado = true;
                        break; //Já sabemos que o condutor está ocupado, então paramos a verificação.
                    }
                }
            }
            //Se correu as viagens todas e não encontrou conflito, adicionamos o condutor à lista
            if (!estaOcupado) {
                condutoresDisponiveis.add(condutor);
            }
        }
        return condutoresDisponiveis;
    }

    /**
     * Obtém uma lista de viaturas que não estão a ser usadas em nenhuma viagem no intervalo.
     *
     * @param inicio Data/Hora de início.
     * @param fim    Data/Hora de fim.
     * @return Lista de viaturas disponíveis.
     */
    public ArrayList<Viatura> getViaturasDisponiveis(LocalDateTime inicio, LocalDateTime fim) {
        ArrayList<Viatura> viaturasDisponiveis = new ArrayList<>();

        for (Viatura viatura : viaturas) {
            //Verifica se esta Viatura tem alguma viagem que colida com o horário.
            boolean estaOcupado = false;
            for (Viagem viagem : viagens) {
                if (viagem.getViatura().getMatricula().equalsIgnoreCase(viatura.getMatricula())) {
                    //Lógica de sobreposição (InicioA < FimB) && (FimA > InicioB)
                    if (inicio.isBefore(viagem.getDataHoraFim()) && fim.isAfter(viagem.getDataHoraInicio())) {
                        estaOcupado = true;
                        break;
                    }
                }
            }
            //Se correu as viagens todas e não encontrou conflito, adicionamos a viatura à lista
            if (!estaOcupado) {
                viaturasDisponiveis.add(viatura);
            }
        }
        return viaturasDisponiveis;
    }

    /**
     * Obtém uma lista de clientes que não têm viagens marcadas no intervalo.
     *
     * @param inicio Data/Hora de início.
     * @param fim    Data/Hora de fim.
     * @return Lista de clientes disponíveis (livres).
     */
    public ArrayList<Cliente> getClientesDisponiveis(LocalDateTime inicio, LocalDateTime fim) {
        ArrayList<Cliente> clientesDisponiveis = new ArrayList<>();

        //Verifica se o Cliente tem alguma viagem que colia com o horário.
        for (Cliente cliente : clientes) {
            boolean estaOcupado = false;
            for (Viagem viagem : viagens) {
                if (viagem.getCliente().getNif() == cliente.getNif()) {
                    //Lógica de sobreposição (InicioA < FimB) && (FimA > InicioB)
                    if (inicio.isBefore(viagem.getDataHoraFim()) && fim.isAfter(viagem.getDataHoraInicio())) {
                        estaOcupado = true;
                        break;
                    }
                }
            }
            if (!estaOcupado) {
                clientesDisponiveis.add(cliente);
            }
        }
        //Se correu as viagens todas e não encontrou conflito, adicionamos o Cliente à lista
        return clientesDisponiveis;
    }

    /**
     * Verifica se existe sobreposição de horários para a Viatura ou Condutor.
     * <p>
     * A sobreposição é detetada se o intervalo de tempo da nova viagem colidir
     * com qualquer viagem já existente para o mesmo carro ou motorista.
     * </p>
     *
     * @param v      A Viatura da nova viagem.
     * @param c      O condutor da nova viagem.
     * @param inicio Data/Hora de Início.
     * @param fim    Data/Hora de Fim.
     * @return {@code true} se houver sobreposição (ocupado); {@code false} se estiver livre.
     */
    public boolean verificarSobreposicao(Viatura v, Condutor c, LocalDateTime inicio, LocalDateTime fim) {
        for (Viagem viagemExistente : viagens) {
            boolean mesmaViatura = viagemExistente.getViatura().getMatricula().equalsIgnoreCase(v.getMatricula());
            boolean mesmoCondutor = viagemExistente.getCondutor().getNif() == c.getNif();

            if (mesmaViatura || mesmoCondutor) {
                if (inicio.isBefore(viagemExistente.getDataHoraFim()) && fim.isAfter(viagemExistente.getDataHoraInicio())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Regista uma nova viagem realizada no sistema.
     *
     * @param v A viagem a adicionar ao histórico.
     * @return {@code true} se adicionada com sucesso; {@code false} se houver sobreposição de horários.
     */
    public boolean adicionarViagem(Viagem v) {
        if (verificarSobreposicao(v.getViatura(), v.getCondutor(), v.getDataHoraInicio(), v.getDataHoraFim())) {
            System.out.println("Erro: Sobreposição detetada." +
                    "Viatura ou Condutor ocupados neste horário.");
            return false;
        }
        viagens.add(v);
        return true;
    }

    /**
     * Obtém o histórico completo de viagens.
     *
     * @return ArrayList de viagens.
     */
    public ArrayList<Viagem> getViagens() {
        return viagens;
    }

    /**
     * Regista uma nova reserva no sistema.
     *
     * @param r A reserva a adicionar.
     */
    public void adicionarReserva(Reserva r) {
        reservas.add(r);
    }

    /**
     * Obtém a lista de reservas ativas (pendentes).
     *
     * @return ArrayList de reservas.
     */
    public ArrayList<Reserva> getReservas() {
        return reservas;
    }

    /**
     * Tenta converter uma reserva existente numa viagem efetiva.
     * <p>
     * Cria uma viagem com os dados da reserva e tenta adicioná-la ao sistema.
     * Se não houver sobreposição, a reserva é removida da lista de pendentes.
     * </p>
     *
     * @param r        A reserva a converter.
     * @param condutor O condutor atribuído.
     * @param viatura  A viatura atribuída.
     * @param custo    O custo final da viagem.
     * @return {@code true} se convertida com sucesso; {@code false} caso contrário.
     */
    public boolean converterReservaEmViagem(Reserva r, Condutor condutor, Viatura viatura, double custo) {
        if (reservas.contains(r)) {
            // Cria a viagem temporária com os dados da reserva.
            Viagem novaViagem = new Viagem(
                    condutor,
                    r.getCliente(),
                    viatura,
                    r.getDataHoraInicio(),
                    r.getDataHoraInicio().plusMinutes(30),
                    r.getMoradaOrigem(),
                    r.getMoradaDestino(),
                    r.getKms(),
                    custo
            );
            boolean adicionou = adicionarViagem(novaViagem);
            if (adicionou) {
                reservas.remove(r); // Remove a reserva pois já foi efetuada
                return true;
            } else {
                return false;
            }
        }
        return false; //Devolve false se houver sobreposicção ou se a reserva não existir
    }

    /**
     * Remove uma reserva específica da lista de reservas.
     *
     * @param r A reserva a remover.
     * @return {@code true} se foi removida com sucesso.
     */
    public boolean removerReserva(Reserva r) {
        return reservas.remove(r);
    }

    /**
     * Remove uma viagem específica do histórico.
     *
     * @param v A viagem a remover.
     * @return {@code true} se foi removida com sucesso.
     */
    public boolean removerViagens(Viagem v) {
        return viagens.remove(v);
    }

    // ==========================================================
    //                 RELATÓRIOS E ESTATÍSTICAS
    // ==========================================================

    /**
     * Calcula o total faturado por um condutor num intervalo de tempo.
     *
     * @param nifCondutor O NIF do condutor.
     * @param inicio      Data de início do intervalo.
     * @param fim         Data de fim do intervalo.
     * @return O valor total faturado (em euros).
     */
    public double calcularFaturacaoCondutor(int nifCondutor, LocalDateTime inicio, LocalDateTime fim) {
        double total = 0.0;
        for (Viagem v : viagens) {
            if (v.getCondutor().getNif() == nifCondutor) {
                if (isDentroDoPrazo(v.getDataHoraInicio(), inicio, fim)) {
                    total += v.getCusto();
                }
            }
        }
        return total;
    }

    /**
     * Obtém a lista de clientes distintos que viajaram numa determinada viatura.
     *
     * @param matricula A matrícula da viatura.
     * @return Lista de clientes únicos.
     */
    public ArrayList<Cliente> getClientesPorViatura(String matricula) {
        ArrayList<Cliente> clientesViatura = new ArrayList<>();

        for (Viagem v : viagens) {
            if (v.getViatura().getMatricula().equalsIgnoreCase(matricula)) {
                Cliente c = v.getCliente();

                boolean jaExiste = false;
                for (Cliente existente : clientesViatura) {
                    if (existente.getNif() == c.getNif()) {
                        jaExiste = true;
                        break;
                    }
                }
                if (!jaExiste) {
                    clientesViatura.add(c);
                }
            }
        }
        return clientesViatura;
    }

    /**
     * Método auxiliar para contabilizar a frequência de um destino na lista de estatísticas.
     *
     * @param destino      O nome do destino.
     * @param nomesDestino Lista com os nomes dos destinos já encontrados.
     * @param contagens    Lista com as contagens correspondentes.
     */
    private void contabilizarDestino(String destino, ArrayList<String> nomesDestino, ArrayList<Integer> contagens) {
        int index = -1;

        for (int i = 0; i < nomesDestino.size(); i++) {
            if (nomesDestino.get(i).equalsIgnoreCase(destino)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            int valorAtual = contagens.get(index);
            contagens.set(index, valorAtual + 1);
        } else {
            nomesDestino.add(destino);
            contagens.add(1);
        }
    }

    // ==========================================================
    //           NOVOS MÉTODOS DE ESTATÍSTICA/PESQUISA
    // ==========================================================

    /**
     * Calcula a média de quilómetros das viagens realizadas num determinado intervalo de tempo.
     *
     * @param inicio Data e hora de inicio do intervalo.
     * @param fim    Data e hora de fim do intervalo.
     * @return O valor médio da distância em Kms, ou 0.0 se não existirem viagens.
     */
    public double calcularDistanciaMedia(LocalDateTime inicio, LocalDateTime fim) {
        double totalKms = 0;
        int contador = 0;

        for (Viagem v : viagens) {
            if (isDentroDoPrazo(v.getDataHoraInicio(), inicio, fim)) {
                totalKms += v.getKms();
                contador++;
            }
        }
        if (contador == 0) {
            return 0.0;
        }
        return totalKms / contador;
    }

    /**
     * Obtém uma lista de clientes que realizaram viagens cuja distância se encontra dentro de um intervalo.
     * Garante que não existem clientes duplicados na lista devolvida.
     *
     * @param minKms Distância mínima (limite inferior).
     * @param maxKms Distância máxima (limite superior).
     * @return Lista de clientes únicos que cumprem o critério.
     */
    public ArrayList<Cliente> getClientesPorIntervaloKms(double minKms, double maxKms) {
        ArrayList<Cliente> resultado = new ArrayList<>();

        for (Viagem v : viagens) {
            if (v.getKms() >= minKms && v.getKms() <= maxKms) {
                Cliente cliente = v.getCliente();

                //Verificar duplicados para não listar o mesmo cliente duas vezes.
                boolean jaExiste = false;
                for (Cliente existente : resultado) {
                    if (existente.getNif() == cliente.getNif()) {
                        jaExiste = true;
                        break;
                    }
                }
                if (!jaExiste) {
                    resultado.add(cliente);
                }
            }
        }
        return resultado;
    }

    /**
     * Pesquisa o histórico de viagens de um cliente específico dentro de um intervalo de datas.
     *
     * @param nifCliente NIF do Cliente a pesquisar.
     * @param inicio     Data de inicio.
     * @param fim        Data de fim.
     * @return Lista de Viagens encontradas.
     */
    public ArrayList<Viagem> getViagensClientePorDatas(int nifCliente, LocalDateTime inicio, LocalDateTime fim) {
        ArrayList<Viagem> resultado = new ArrayList<>();

        for (Viagem v : viagens) {
            if (v.getCliente().getNif() == nifCliente) {
                if (isDentroDoPrazo(v.getDataHoraInicio(), inicio, fim)) {
                    resultado.add(v);
                }
            }
        }
        return resultado;
    }

    /**
     * Obtém a lista de todas as reservas pendentes associadas a um determinado cliente.
     *
     * @param nifCliente NIF do Cliente.
     * @return Lista contendo as reservas desse cliente.
     */
    public ArrayList<Reserva> getReservasDoCliente(int nifCliente) {
        ArrayList<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getCliente().getNif() == nifCliente) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    /**
     * Determina o destino mais solicitado considerando tanto o histórico de Viagens como as Reservas pendentes.
     * A contagem é efetuada apenas dentro do intervalo de datas especificado.
     *
     * @param inicio Data e hora de ínicio do intervalo.
     * @param fim    Data e hora de fim do intervalo.
     * @return Uma string com o nome do destino e o número de ocorrências.
     */
    public String getDestinoMaisSolicitado(LocalDateTime inicio, LocalDateTime fim) {
        ArrayList<String> destinos = new ArrayList<>();
        ArrayList<Integer> contagens = new ArrayList<>();

        //1. Verificar em Viagens
        for (Viagem v : viagens) {
            if (isDentroDoPrazo(v.getDataHoraInicio(), inicio, fim)) {
                contabilizarDestino(v.getMoradaDestino(), destinos, contagens);
            }
        }

        //2. Verificar em Reservas
        for (Reserva r : reservas) {
            if (isDentroDoPrazo(r.getDataHoraInicio(), inicio, fim)) {
                contabilizarDestino(r.getMoradaDestino(), destinos, contagens);
            }
        }

        if (destinos.isEmpty()) {
            return "Sem dados neste período.";
        }

        //Encontrar o maior
        int maxIndex = -1;
        int maxValor = -1;
        for (int i = 0; i < contagens.size(); i++) {
            if (contagens.get(i) > maxValor) {
                maxValor = contagens.get(i);
                maxIndex = i;
            }
        }
        return destinos.get(maxIndex) + " (" + maxValor + " vezes)";
    }

    /**
     * Método auxiliar privado para verificar se uma data se encontra dentro de um intervalo fechado [inicio, fim].
     *
     * @param data   A data a verificar.
     * @param inicio O limite inferior do intervalo.
     * @param fim    O limite superior do intervalo.
     * @return {@code true} se a data for igual ou posterior ao início e igual ou anterior ao fim.
     */
    private boolean isDentroDoPrazo(LocalDateTime data, LocalDateTime inicio, LocalDateTime fim) {
        //Verifica se é (Depois ou Igual ao Início) e (Antes ou Igual ao Fim)
        return (data.isAfter(inicio) || data.equals(inicio)) && (data.isBefore(fim) || data.equals(fim));
    }

    // ==========================================================
    //                        PERSISTÊNCIA
    // ==========================================================

    /**
     * /**
     * Grava todos os dados. Usa try-catch centralizado para apanhar erros dos sub-métodos.
     * <p>
     * Este método deve ser chamado apenas no fecho da aplicação para garantir
     * que os dados (Viaturas, Clientes, Condutores e Viagens) não são perdidos.
     * </p>
     */
    public void gravarDados() {
        //1. Criar a pasta principal "Empresas" se não existir
        File pastaPrincipal = new File("Empresa");
        if (!pastaPrincipal.exists()) {
            pastaPrincipal.mkdir(); //Cria a diretoria/pasta
        }

        //2. Cria a subpasta da empresa
        File pastaEmpresa = new File(nomePasta);
        if (!pastaEmpresa.exists()) {
            pastaEmpresa.mkdir();
        }

        try {
            gravarViaturas();
            gravarClientes();
            gravarCondutores();
            gravarViagens();
            gravarReservas();
            System.out.println("Dados guardados com sucesso em " + nomePasta);
        } catch (IOException e) {
            System.out.println("Erro crítico ao gravar ficheiros: " + e.getMessage());
        }
    }

    /**
     * Coordena o carregamento de toda a informação dos ficheiros para a memória.
     * Este método deve ser chamado no arranque da aplicação.
     */
    public void carregarDados() {
        carregarViaturas();
        carregarClientes();
        carregarCondutores();
        carregarViagens();
        carregarReservas();

    }

    // ==========================================================
    //       MÉTODOS PRIVADOS DE GRAVAÇÃO DE FICHEIROS
    //               (com throws IOException)
    // ==========================================================

    /**
     * Escreve a lista de viaturas no ficheiro "viaturas.txt".
     */
    private void gravarViaturas() throws IOException {
        try (Formatter out = new Formatter(new File(nomePasta + "/viaturas.txt"))) {
            for (Viatura v : viaturas) {
                out.format("%s;%s;%s;%d%n", v.getMatricula(), v.getMarca(), v.getModelo(), v.getAnoFabrico());
            }
        }
    }

    /**
     * Escreve a lista de clientes no ficheiro "clientes.txt".
     */
    private void gravarClientes() throws IOException {
        try (Formatter out = new Formatter(new File(nomePasta + "/clientes.txt"))) {
            for (Cliente c : clientes) {
                out.format("%s;%d;%d;%s;%d%n", c.getNome(), c.getNif(), c.getTel(), c.getMorada(), c.getCartaoCid());
            }
        }
    }

    /**
     * Escreve a lista de condutores no ficheiro "condutores.txt".
     */
    private void gravarCondutores() throws IOException {
        try (Formatter out = new Formatter(new File(nomePasta + "/condutores.txt"))) {
            for (Condutor c : condutores) {
                out.format("%s;%d;%d;%s;%d;%s;%d%n", c.getNome(), c.getNif(), c.getTel(), c.getMorada(), c.getCartaoCid(), c.getCartaCond(), c.getSegSocial());
            }
        }
    }

    /**
     * Escreve o histórico de viagens no ficheiro "viagens.txt".
     */
    private void gravarViagens() throws IOException {
        try (Formatter out = new Formatter(new File(nomePasta + "/viagens.txt"))) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            for (Viagem viagem : viagens) {
                out.format("%d;%d;%s;%s;%s;%s;%s;%s;%s%n",
                        viagem.getCondutor().getNif(),
                        viagem.getCliente().getNif(),
                        viagem.getViatura().getMatricula(),
                        viagem.getDataHoraInicio().format(dtf),
                        viagem.getDataHoraFim().format(dtf),
                        viagem.getMoradaOrigem(),
                        viagem.getMoradaDestino(),
                        String.valueOf(viagem.getKms()).replace(',', '.'),
                        String.valueOf(viagem.getCusto()).replace(',', '.'));
            }
        }
    }

    /**
     * Escreve a lista de reservas pendentes no ficheiro "reservas.txt".
     * <p>
     * Guarda o NIF do cliente para manter a integridade referencial ao carregar,
     * a data/hora, moradas e distância.
     * </p>
     */
    private void gravarReservas() throws  IOException {
        try (Formatter out = new Formatter(new File(nomePasta + "/reservas.txt"))) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            for (Reserva reserva : reservas) {
                //Formato: NIF_Cliente;DATA_HORA;ORIGEM;DESTINO;KMS
                out.format("%d;%s;%s;%s;%s%n",
                        reserva.getCliente().getNif(),
                        reserva.getDataHoraInicio().format(dtf),
                        reserva.getMoradaOrigem(),
                        reserva.getMoradaDestino(),
                        String.valueOf(reserva.getKms()).replace(',', '.'));
            }
        }
    }

    // ==========================================================
    //       MÉTODOS PRIVADOS DE CARREGAMENTO DE FICHEIROS
    //        (com try-catch interno, pois se os ficheiros
    //            não existirem não dá um erro critico)
    // ==========================================================

    /**
     * Lê o ficheiro "viaturas.txt" e carrega as viaturas para o sistema.
     */
    private void carregarViaturas() {
        try (Scanner ler = new Scanner(new File(nomePasta + "/viaturas.txt"))) {
            while (ler.hasNextLine()) {
                String linha = ler.nextLine();
                String[] dados = linha.split(";");

                if (dados.length >= 4) {
                    Viatura v = new Viatura(dados[0], dados[1], dados[2], Integer.parseInt(dados[3]));
                    adicionarViatura(v);
                }
            }
        } catch (Exception e) {
            //Ignora se não existir.
        }
    }

    /**
     * Lê o ficheiro "clientes.txt" e carrega os clientes para o sistema.
     */
    private void carregarClientes() {
        try (Scanner ler = new Scanner(new File(nomePasta + "/clientes.txt"))) {
            while (ler.hasNextLine()) {
                String linha = ler.nextLine();
                String[] dados = linha.split(";");

                if (dados.length >= 5) {
                    Cliente c = new Cliente(dados[0], Integer.parseInt(dados[1]), Integer.parseInt(dados[2]), dados[3], Integer.parseInt(dados[4]));
                    adicionarCliente(c);
                }
            }
        } catch (Exception e) {
            //Ignora se não existir.
        }
    }

    /**
     * Lê o ficheiro "condutores.txt" e carrega os condutores para o sistema.
     */
    private void carregarCondutores() {
        try (Scanner ler = new Scanner(new File(nomePasta + "/condutores.txt"))) {
            while (ler.hasNextLine()) {
                String linha = ler.nextLine();
                String[] dados = linha.split(";");

                if (dados.length >= 7) {
                    Condutor c = new Condutor(dados[0], Integer.parseInt(dados[1]), Integer.parseInt(dados[2]), dados[3], Integer.parseInt(dados[4]), dados[5], Integer.parseInt(dados[6]));
                    adicionarCondutor(c);
                }
            }
        } catch (Exception e) {
            //Ignora se não existir.
        }
    }

    /**
     * Lê o ficheiro "viagens.txt" e reconstrói o historico de viagens.
     */
    private void carregarViagens() {
        try (Scanner ler = new Scanner(new File(nomePasta + "/viagens.txt"))) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            while (ler.hasNextLine()) {
                String linha = ler.nextLine();
                String[] dados = linha.split(";");

                if (dados.length >= 9) {
                    Condutor condutor = procurarCondutor(Integer.parseInt(dados[0]));
                    Cliente cliente = procurarCliente(Integer.parseInt(dados[1]));
                    Viatura viatura = procurarViatura(dados[2]);

                    if (condutor != null && cliente != null && viatura != null) {
                        try {
                            LocalDateTime dataHoraInicio = LocalDateTime.parse(dados[3], dtf);
                            LocalDateTime dataHoraFim = LocalDateTime.parse(dados[4], dtf);
                            double kms = Double.parseDouble(dados[7]);
                            double custo = Double.parseDouble(dados[8]);

                            Viagem v = new Viagem(condutor, cliente, viatura, dataHoraInicio, dataHoraFim, dados[5], dados[6], kms, custo);
                            viagens.add(v);
                        } catch (Exception e) {
                            System.out.println("Erro ao carregar viagens: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Nota: Histórico de viagens vazio ou ilegível.");
            //Ignora se não existir.
        }
    }

    /**
     * Lê o ficheiro "reservas.txt" e carrega as reservas pendentes para o sistema.
     * <p>
     * Reconstrói a ligação ao objeto {@link Cliente} utilizando o NIF guardado.
     * Se o cliente não for encontrado (ex: foi eliminado manualmente do ficheiro),
     * a reserva é ignorada para evitar inconsistências.
     * </p>
     */
    private void carregarReservas() {
        try (Scanner ler = new Scanner(new File(nomePasta + "/reservas.txt"))) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            while (ler.hasNextLine()) {
                String linha = ler.nextLine();
                if (linha.trim().isEmpty()) {
                    continue;
                }
                String[] dados = linha.split(";");

                if (dados.length >= 5) {
                    int nifCliente = Integer.parseInt(dados[0]);
                    Cliente cliente = procurarCliente(nifCliente);

                    if (cliente != null) {
                        try {
                            LocalDateTime dataHoraInicio = LocalDateTime.parse(dados[1], dtf);
                            String moradaOrigem = dados[2];
                            String moradaDestino = dados[3];
                            double kms = Double.parseDouble(dados[4]);

                            Reserva reserva = new Reserva(cliente, dataHoraInicio, moradaOrigem, moradaDestino, kms);
                            reservas.add(reserva);
                        } catch (Exception e) {
                            System.out.println("Erro ao carregar reservas: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            //Ignora se não existir.
        }
    }
}