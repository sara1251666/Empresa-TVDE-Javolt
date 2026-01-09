package Gestao;

import Entidades.*;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.util.Formatter;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe central de gestão do sistema (Business Logic Layer).
 * <p>
 * Esta classe armazena todas as listas de dados em memória (ArrayLists) e contém
 * a lógica para adicionar, remover e pesquisar dados, bem como as regras de negócio
 * (ex: verificar sobreposições de horários) e a persistência em ficheiros.
 * </p>
 *
 * @author Grupo 1 - Javolt (Levi, Sara, Leonardo, Micael)
 * @version 1.0
 * @since 2026-01-08
 */
public class Empresa {

    /**
     * Lista de viaturas registadas na empresa.
     */
    private final ArrayList<Viatura> viaturas;

    /**
     * Lista de condutores que trabalham na empresa.
     */
    private final ArrayList<Condutor> condutores;

    /**
     * Lista de clientes registados na plataforma.
     */
    private final ArrayList<Cliente> clientes;

    /**
     * Histórico de viagens realizadas.
     */
    private final ArrayList<Viagem> viagens;

    /**
     * Lista de reservas futuras efetuadas por clientes.
     */
    private final ArrayList<Reserva> reservas;

    /**
     * O nome da pasta onde os ficheiros desta empresa específica serão guardados.
     */
    private final String nomePasta;

    /**
     * Construtor da classe Empresa.
     * Inicializa todas as listas (ArrayLists) vazias prontas para armazenar dados.
     *
     * @param nomeEmpresa O nome da empresa (usado para criar a pasta "Logs_Nome").
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
     * @param viatura O objeto Viatura a ser adicionado.
     * @return {@code true} se a viatura foi adicionada com sucesso;
     * {@code false} se já existir uma viatura com a mesma matrícula.
     */
    public boolean adicionarViatura(Viatura viatura) {
        if (procurarViatura(viatura.getMatricula()) == null) {
            viaturas.add(viatura);
            return true;
        }
        return false; // Matrícula já existe
    }

    /**
     * Obtém a lista completa de viaturas.
     * @return ArrayList contendo todas as viaturas registadas.
     */
    public ArrayList<Viatura> getViaturas() {
        return viaturas;
    }

    /**
     * Procura uma viatura específica através da matrícula.
     * @param matricula A matrícula da viatura a pesquisar (ex: "AA-00-BB").
     * @return O objeto {@link Viatura} se encontrado, ou {@code null} se não existir.
     */
    public Viatura procurarViatura(String matricula) {
        for (Viatura viatura : viaturas) {
            if (viatura.getMatricula().equalsIgnoreCase(matricula)) {
                return viatura;
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
        Viatura viatura = procurarViatura(matricula);
        if (viatura != null) {
            // Verificar dependências em Viagens
            for (Viagem viagem : viagens) {
                if (viagem.getViatura().getMatricula().equalsIgnoreCase(matricula)) {
                    System.out.println("Erro: Não é possível remover. Viatura associada a uma viagem.");
                    return false;
                }
            }
            // Se não houver dependências, remove
            viaturas.remove(viatura);
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
     * @param cliente O objeto Cliente a adicionar.
     * @return {@code true} se adicionado com sucesso; {@code false} se o NIF já existir.
     */
    public boolean adicionarCliente(Cliente cliente) {
        // 1. Procura se já existe alguém com este NIF na lista
        if (procurarCliente(cliente.getNif()) == null) {
            // 2. Se devolver null (não encontrou), adiciona.
            clientes.add(cliente);
            return true;
        }
        // 3. Se encontrou, devolve falso e não adiciona.
        return false;
    }

    /**
     * Obtém a lista completa de clientes.
     * @return Lista contendo todos os clientes.
     */
    public ArrayList<Cliente> getClientes() {
        return clientes;
    }

    /**
     * Procura um cliente específico através do NIF.
     * @param nif O Número de Identificação Fiscal do cliente.
     * @return O objeto {@link Cliente} se encontrado, ou {@code null} caso contrário.
     */
    public Cliente procurarCliente(int nif) {
        for (Cliente cliente : clientes) {
            if (cliente.getNif() == nif) {
                return cliente;
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
        Cliente cliente = procurarCliente(nif);
        if (cliente != null) {
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
            clientes.remove(cliente);
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
     * @param condutor O objeto Condutor a adicionar.
     * @return {@code true} se adicionado com sucesso; {@code false} se o NIF já existir.
     */
    public boolean adicionarCondutor(Condutor condutor) {
        if (procurarCondutor(condutor.getNif()) == null) {
            condutores.add(condutor);
            return true;
        }
        return false;
    }

    /**
     * Obtém a lista completa de condutores.
     * @return Lista de condutores.
     */
    public ArrayList<Condutor> getCondutores() {
        return condutores;
    }

    /**
     * Procura um condutor pelo NIF.
     * @param nif O NIF do condutor.
     * @return O objeto {@link Condutor} se encontrado, ou {@code null} caso contrário.
     */
    public Condutor procurarCondutor(int nif) {
        for (Condutor condutor : condutores) {
            if (condutor.getNif() == nif) {
                return condutor;
            }
        }
        return null;
    }

    /**
     * Remove um condutor do sistema se este não tiver viagens realizadas.
     * @param nif O NIF do condutor a remover.
     * @return {@code true} se removido com sucesso; {@code false} caso contrário.
     */
    public boolean removerCondutor(int nif) {
        Condutor condutor = procurarCondutor(nif);
        if (condutor != null) {
            for (Viagem viagem : viagens) {
                if (viagem.getCondutor().getNif() == nif) {
                    System.out.println("Erro: Condutor possui histórico de viagens.");
                    return false;
                }
            }
            condutores.remove(condutor);
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
     * @param viatura  A Viatura da nova viagem.
     * @param condutor O condutor da nova viagem.
     * @param inicio   Data/Hora de Início.
     * @param fim      Data/Hora de Fim.
     * @return {@code true} se houver sobreposição (ocupado); {@code false} se estiver livre.
     */
    public boolean verificarSobreposicao(Viatura viatura, Condutor condutor, LocalDateTime inicio, LocalDateTime fim) {
        for (Viagem viagemExistente : viagens) {
            boolean mesmaViatura = viagemExistente.getViatura().getMatricula().equalsIgnoreCase(viatura.getMatricula());
            boolean mesmoCondutor = viagemExistente.getCondutor().getNif() == condutor.getNif();

            if (mesmaViatura || mesmoCondutor) {
                if (inicio.isBefore(viagemExistente.getDataHoraFim()) && fim.isAfter(viagemExistente.getDataHoraInicio())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Regista uma nova viagem realizada no sistema após verificar conflitos.
     *
     * @param viagem A viagem a adicionar ao histórico.
     * @return {@code true} se adicionada com sucesso; {@code false} se houver sobreposição de horários.
     */
    public boolean adicionarViagem(Viagem viagem) {
        if (verificarSobreposicao(viagem.getViatura(), viagem.getCondutor(), viagem.getDataHoraInicio(), viagem.getDataHoraFim())) {
            System.out.println("Erro: Sobreposição detetada." +
                    "Viatura ou Condutor ocupados neste horário.");
            return false;
        }
        viagens.add(viagem);
        return true;
    }

    /**
     * Obtém o histórico completo de viagens.
     * @return Lista de viagens.
     */
    public ArrayList<Viagem> getViagens() {
        return viagens;
    }

    /**
     * Regista uma nova reserva no sistema.
     * @param reserva A reserva a adicionar.
     */
    public void adicionarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    /**
     * Obtém a lista de reservas ativas (pendentes).
     * @return ArrayList de reservas.
     */
    public ArrayList<Reserva> getReservas() {
        return reservas;
    }

    /**
     * Converte uma Reserva em Viagem, atribuindo os recursos que faltavam.
     * Remove a reserva da lista se a conversão for bem-sucedida.
     *
     * @param reserva  A reserva original.
     * @param condutor O condutor selecionado.
     * @param viatura  A viatura selecionada.
     * @param custo    O custo final calculado.
     * @return {@code true} se sucesso.
     */
    public boolean converterReservaEmViagem(Reserva reserva, Condutor condutor, Viatura viatura, double custo) {
        if (reservas.contains(reserva)) {
            // Cria a viagem temporária com os dados da reserva.
            // Nota: Assumimos uma duração fixa de 30 mins por defeito se não especificada
            Viagem novaViagem = new Viagem(
                    condutor,
                    reserva.getCliente(),
                    viatura,
                    reserva.getDataHoraInicio(),
                    reserva.getDataHoraInicio().plusMinutes(30),
                    reserva.getMoradaOrigem(),
                    reserva.getMoradaDestino(),
                    reserva.getKms(),
                    custo );

            boolean adicionou = adicionarViagem(novaViagem);
            if (adicionou) {
                reservas.remove(reserva); // Remove a reserva pois já foi efetuada
                return true;
            }
        }
        return false; //Devolve false se houver sobreposicção ou se a reserva não existir
    }

    /**
     * Remove uma reserva específica da lista de reservas.
     *
     * @param reserva A reserva a remover.
     * @return {@code true} se foi removida com sucesso.
     */
    public boolean removerReserva(Reserva reserva) {
        return reservas.remove(reserva);
    }

    /**
     * Remove uma viagem específica do histórico.
     *
     * @param viagem A viagem a remover.
     * @return {@code true} se foi removida com sucesso.
     */
    public boolean removerViagens(Viagem viagem) {
        return viagens.remove(viagem);
    }

    // ==========================================================
    //                 RELATÓRIOS E ESTATÍSTICAS
    // ==========================================================

    /**
     * Calcula o total faturado (€) por um condutor num intervalo de tempo.
     * @param nifCondutor NIF do condutor.
     * @param inicio      Início do intervalo.
     * @param fim         Fim do intervalo.
     * @return Total faturado.
     */
    public double calcularFaturacaoCondutor(int nifCondutor, LocalDateTime inicio, LocalDateTime fim) {
        double total = 0.0;
        for (Viagem viagem : viagens) {
            if (viagem.getCondutor().getNif() == nifCondutor) {
                if (isDentroDoPrazo(viagem.getDataHoraInicio(), inicio, fim)) {
                    total += viagem.getCusto();
                }
            }
        }
        return total;
    }

    /**
     * Lista clientes únicos que viajaram numa viatura.
     * @param matricula Matrícula da viatura.
     * @return Lista de clientes (sem duplicados).
     */
    public ArrayList<Cliente> getClientesPorViatura(String matricula) {
        ArrayList<Cliente> clientesViatura = new ArrayList<>();

        for (Viagem viagem : viagens) {
            if (viagem.getViatura().getMatricula().equalsIgnoreCase(matricula)) {
                Cliente cliente = viagem.getCliente();

                boolean jaExiste = false;
                for (Cliente existente : clientesViatura) {
                    if (existente.getNif() == cliente.getNif()) {
                        jaExiste = true;
                        break;
                    }
                }
                if (!jaExiste) {
                    clientesViatura.add(cliente);
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
     * Calcula a média de Kms das viagens num intervalo.
     * @param inicio Início do intervalo.
     * @param fim    Fim do intervalo.
     * @return Média de Kms.
     */
    public double calcularDistanciaMedia(LocalDateTime inicio, LocalDateTime fim) {
        double totalKms = 0;
        int contador = 0;

        for (Viagem viagem : viagens) {
            if (isDentroDoPrazo(viagem.getDataHoraInicio(), inicio, fim)) {
                totalKms += viagem.getKms();
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

        for (Viagem viagem : viagens) {
            if (viagem.getKms() >= minKms && viagem.getKms() <= maxKms) {
                Cliente cliente = viagem.getCliente();

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

        for (Viagem viagem : viagens) {
            if (viagem.getCliente().getNif() == nifCliente) {
                if (isDentroDoPrazo(viagem.getDataHoraInicio(), inicio, fim)) {
                    resultado.add(viagem);
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
        for (Reserva reservas : reservas) {
            if (reservas.getCliente().getNif() == nifCliente) {
                resultado.add(reservas);
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

    public double calcularTotalKmsCliente(int nifCliente){
        double totalKms = 0;
        for (Viagem viagem : viagens) {
            if (viagem.getCliente().getNif() == nifCliente) {
                totalKms += viagem.getKms();
            }
        }
        return totalKms;
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
        File pastaPrincipal = new File("Empresas");
        if (!pastaPrincipal.exists()) {
            boolean criouPrincipal = pastaPrincipal.mkdir();

            if (!criouPrincipal) {
                System.out.println("Erro fatal: Não foi possível criar a pasta 'Empresas'.");
            }
        }

        //2. Cria a subpasta da empresa
        File pastaEmpresa = new File(nomePasta);
        if (!pastaEmpresa.exists()) {
            boolean criouEmpresa = pastaEmpresa.mkdir(); //Guardamos a resposta
            if (!criouEmpresa) {
                System.out.println("Erro fatal: Não foi possível criar a pasta " + nomePasta);
                return;
            }
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