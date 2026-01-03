package Gestao;

import Entidades.*;
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
 * referencial (não apagar entidades com dependências).
 * </p>
 *
 * @author LEvi
 * @version 1.0
 * @since 2026-01-01
 */
public class Empresa {

    /** Lista de viaturas registadas na empresa.*/
    private ArrayList<Viatura> viaturas;

    /** Lista de condutores que trabalham na empresa. */
    private ArrayList<Condutor> condutores;

    /** Lista de clientes registados na plataforma. */
    private ArrayList<Cliente> clientes;

    /** Histórico de viagens realizadas. */
    private ArrayList<Viagem> viagens;

    /** Lista de reservas futuras efetuadas por clientes. */
    private ArrayList<Reserva> reservas;

    /**
     * Construtor da classe Empresa.
     * Inicializa todas as listas (ArrayLists) vazias prontas para armazenar dados.
     */
    public Empresa() {
        this.viaturas = new ArrayList<>();
        this.condutores = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.viagens = new ArrayList<>();
        this.reservas = new ArrayList<>();
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
     * @return O objeto Condutor ou null.
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
     * @return {@code true} se removido, {@code false} caso contrário.
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
     * Verifica se existe sobreposição de horários para a Viatura e Condutor.
     * Regra: (InicioA < FimB) e (FimA > InicioB).
     * @param v A Viatura da nova viagem.
     * @param c O condutor da nova viagem.
     * @param inicio Inicio Data/Hora de Inicio.
     * @param fim Fim Data/Hora de fim.
     * @return Se true (verdadeiro) Está Ocupado / Se False (Falso) Está livre.
     */
    public boolean verificarSobreposicao(Viatura v, Condutor c, LocalDateTime inicio, LocalDateTime fim) {
        for(Viagem viagemExistente : viagens) {
            boolean mesmaViatura = viagemExistente.getViatura().getMatricula().equals(v.getMatricula());
            boolean mesmoCondutor = viagemExistente.getCondutor().getNif() == c.getNif();

            if (mesmaViatura || mesmoCondutor) {
                if (inicio.isBefore(viagemExistente.getDataHoraFim()) && fim.isAfter(viagemExistente.getDataHoraInicio())) {}
                return true;
            }
        }
        return false;
    }

    /**
     * Regista uma nova viagem realizada.
     * @param v A viagem a adicionar ao histórico.
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
     * @return ArrayList de viagens.
     */
    public ArrayList<Viagem> getViagens() {
        return viagens;
    }

    /**
     * Regista uma nova reserva no sistema.
     * @param r A reserva a adicionar.
     */
    public void adicionarReserva(Reserva r) {
        reservas.add(r);
    }

    /**
     * Obtém a lista de reservas ativas.
     * @return ArrayList de reservas.
     */

    public ArrayList<Reserva> getReservas() {
        return reservas;
    }

    //Metodo auxiliar para converter Reserva em viagem  ----  falta testar

    public boolean converterReservaEmViagem(Reserva r, Condutor condutor, Viatura viatura, double custo) {
        if (reservas.contains(r)) {
            // Cria a viagem temporária com os dados da reserva.
            Viagem novaViagem = new Viagem(
                    condutor,
                    r.getCliente(),
                    viatura,
                    r.getDataHoraInicio(),
                    r.getDataHoraInicio().plusMinutes(30), // Exemplo: duração estimada
                    r.getMoradaOrigem(),
                    r.getMoradaDestino(),
                    r.getKms(),
                    custo
            );

            if adicionarViagem(novaViagem);
            {
                reservas.remove(r); // Remove a reserva pois já foi efetuada
                return true;
            }
        }
        return false; //Devolve false se houver sobreposicção ou se a reserva não existir
    }

    // ==========================================================
    //                 RELATÓRIOS E ESTATÍSTICAS
    // ==========================================================

    public double calcularFaturacaoCondutor(int nifCondutor, LocalDateTime inicio, LocalDateTime fim) {
        double total = 0.0;
        for (Viagem v : viagens) {
            if (v.getCondutor().getNif() == nifCondutor) {
                boolean depoisInicio = v.getDataHoraInicio().isAfter(inicio) || v.getDataHoraInicio().equals(inicio);
                boolean antesFim = v.getDataHoraInicio().isAfter(fim) || v.getDataHoraInicio().equals(fim);

                if (depoisInicio && antesFim) {
                    total += v.getCusto();
                }
            }
        }
        return total;
    }

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

    private void contabilizarDestino(String destino, ArrayList<String> nomesDestino, ArrayList<Integer> contagens) {
        int index = -1;

        for (int i = 0; i < nomesDestino.size(); i++) {
            if (nomesDestino.get(i).equalsIgnoreCase(destino)) {
                index = i;
                break;
            }
        }

        if (index != -1){
            int valorAtual = contagens.get(index);
            contagens.set(index, valorAtual + 1);
        } else{
            nomesDestino.add(destino);
            contagens.add(1);
        }
    }

    // ==========================================================
    //           NOVOS MÉTODOS DE ESTATÍSTICA/PESQUISA
    // ==========================================================

    /**
     * Calcula a media de quilómetros das viagens realizadas num determinado intervaki de tempo.
     * Requisito: Apresentar a distância media (em kms).
     * @param inicio Data e hora de inicio do intervalo.
     * @param fim Data e hora de fim do intervalo.
     * @return Retorna o valor médio da distância em Kms
     * ou 0.0 se não existirem viagens realizadas num intervalo de datas.
     */
    public double calcularDistanciaMedia(LocalDateTime inicio, LocalDateTime fim) {
        double totalKms = 0;
        int contador = 0;

        for (Viagem v : viagens) {
            if (isDentroDoPrazo(v.getDataHoraInicio(), inicio, fim)) {
                totalKms += v.getCusto();
                contador++;
            }
        }
        if (contador == 0){
            return 0.0;
        }
        return totalKms / contador;
    }

    /**
     * Obtém uma lista de clientes que realizaram viagens cuja distância se encontra dentro de um intervalo.
     * Garante que não existem clientes duplicados na lista devolvida.
     * Requisito: Apresentar uma lista dos clientes com viagens cuja distância se encontra dentro de um intervalo.
     * @param minKms Distância minima (limite inferior).
     * @param maxKms Distância máxima (limite superior).
     * @return Retorna a lista de clientes únicos que cumprem o critério.
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
     * Requisito: Pesquisar as viagens de cliente num intervalo de datas indicado pelo utilizador.
     * @param nifCliente Nif do Cliente a pesquisar.
     * @param inicio Data de inicio.
     * @param fim Data de fim.
     * @return Lista de Viagens encontradas.
     */
    public ArrayList<Viagem> getViagensClientePorDatas(int nifCliente, LocalDateTime inicio, LocalDateTime fim) {
        ArrayList<Viagem> resultado = new ArrayList<>();

        for (Viagem v : viagens) {
            if (v.getCliente().getNif() == nifCliente) {
                if (isDentroDoPrazo(v.getDataHoraInicio(), inicio, fim)){
                    resultado.add(v);
                }
            }
        }
        return resultado;
    }

    /**
     * Obtém a lista de todas as reservas pendentes associadas a um determinado cliente.
     * Requisito: Consultar lista de reservas de um cliente.
     * @param nifCliente Nif do Cliente.
     * @return Lista contendo as reservas desse cliente.
     */
    public ArrayList<Reserva> getReservasDoCliente(int nifCliente){
        ArrayList<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getCliente().getNif() == nifCliente) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    /**
     * Determina o destino mais solicitado considerando tamto o histórico de Viagems como as Reservas pendentes.
     * A contagem é efetuada apenas dentro do intervalo de datas especificado.
     * Requisito: Apresentar o destino mais solicitado (reservas e viagens) durante um intervalo de datas.
     * @param inicio Data e hora de ínicio do intervalo.
     * @param fim Data e hora de fim do intervalo.
     * @return Retorna uma lista com o nome do destino e o número de ocorrências, ou uma mensagem se não houver dados.
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
            if(isDentroDoPrazo(r.getDataHoraInicio(), inicio, fim)){
                contabilizarDestino(r.getMoradaDestino(), destinos, contagens);
            }
        }

        if(destinos.isEmpty()){
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
        return destinos.get(maxIndex)+ " (" + maxValor + " vezes)";
    }

    /**
     * Metodo auxiliar privado para verificar se uma data se encontra dentro de um intervalo fechado [inicio, fim].*
     * @param data A data a veridicar.
     * @param inicio O limite inferior do intervalo.
     * @param fim O limite superior do intervalo.
     * @return {@code true} se a data for igual ou posterior ao início e igual ou anterior ao fim.
     */
    private boolean isDentroDoPrazo (LocalDateTime data, LocalDateTime inicio, LocalDateTime fim) {
        //Verifica se é (Depois ou Igual ao Início) e (Antes ou Igual ao Fim)
        return (data.isAfter(inicio) || data.equals(inicio)) && (data.isBefore(fim) || data.equals(fim));
    }

    // ==========================================================
    //                        PERSISTÊNCIA
    // ==========================================================
    /**
     * Nome da Pasta onde os ficheiros serão guardados.
     */
    private static final String NOME_PASTA = "Logs";

    /**
     * Coordena a gravação de toda a informação do sistema em ficheiros de texto.
     * Este metodo deve ser chamado apenas no fecho da aplicação para garantir
     * que os dados (Viaturas, Cleintes, Condutores e Viagens) não são perdidos.
     */
    public void gravarDados(){
        //1. Verifica a existência da pasta para guardar os dados, senão, cria-a.
        File pasta = new File(NOME_PASTA);
        if (!pasta.exists()) {
            pasta.mkdir(); //Cria a diretoria/pasta
        }
        gravarViaturas();
        gravarClientes();
        gravarCondutores();
        gravarViagens();
    }

    /**
     * Coordena o carregamento de toda a informação dos ficheiros para a memória.
     * Este metodo deve ser chamado no arranque da aplicação.
     * A ordem de carregamento é importante: as Viagens são as últimas
     * porque depende da existencia de Condutores, Clientes e Viaturas.
     */
    public void carregarDados(){
        carregarViaturas();
        carregarClientes();
        carregarCondutores();
        carregarViagens();
    }

    /**
     * Escreve a lista de viaturas no ficheiro "viaturas.txt".
     * Usa {@code Formatter} com try-with-resources para garantir o fecho do ficheiro.
     * o formato de escrita é: matricula;marca;modelo;ano
     */
    private void gravarViaturas() {
        try (Formatter out = new Formatter(new File("Logs/viaturas.txt"))) {
            for (Viatura v : viaturas){
                out.format("%s;%s;%s;%d%n", v.getMatricula(), v.getMarca(), v.getModelo(), v.getAnoFabrico());
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Erro ao carregar viaturas: " + ex.getMessage());
        }
    }

    /**
     * Lê o ficheiro "viaturas.txt" e carrega as viaturas para o sistema.
     * usa {@code Scanner} para ler linha a linha e {@code split} para separar os campos.
     * Se o ficheiro não existir (primeira execução), a exceção é ignorada.
     */
    private void carregarViaturas() {
        try (Scanner ler = new Scanner(new File("Logs/viaturas.txt"))){
            while (ler.hasNextLine()) {
                String linha = ler.nextLine();
                String[] dados =  linha.split(";");

                if (dados.length >= 4) {
                    Viatura v = new Viatura(dados[0], dados[1], dados[2], Integer.parseInt(dados[3]));
                    adicionarViatura(v);
                }
            }
        }catch (FileNotFoundException ex){}
    }

    /**
     * Escreve a lista de clientes no ficheiro "clientes.txt"
     * O formato de escrita é: nome;nif;tel;morada;cartaoCidadao
     */
    private void gravarClientes(){
        try (Formatter out = new Formatter(new File("Logs/clientes.txt"))){
            for (Cliente c : clientes){
                out.format("%s;%d;%d;%s;%d%n", c.getNome(), c.getNif(), c.getTel(), c.getMorada(), c.getCartaoCid());
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Erro ao carregar clientes: " + ex.getMessage());
        }
    }

    /**
     * Lê o ficheiro "clientes.txt" e carrega os clientes para o sistema.
     */
    private void carregarClientes(){
        try (Scanner ler = new Scanner(new File("Logs/clientes.txt"))){
            while (ler.hasNextLine()) {
                String linha = ler.nextLine();
                String[] dados =  linha.split(";");

                if (dados.length >= 5) {
                    Cliente c = new Cliente(dados[0], Integer.parseInt(dados[1]), Integer.parseInt(dados[2]), dados[3], Integer.parseInt(dados[4]));
                    adicionarCliente(c);
                }
            }
        } catch (FileNotFoundException ex){
        }
    }

    /**
     * Escreve a lista de condutores no ficheiro "condutores.txt".
     * O formato de escrita é: nome;nif;tel;morada;cartaoCidadao;cartaConducao;segSocial
     */
    private void gravarCondutores(){
        try ( Formatter out = new Formatter(new File("Logs/condutores.txt"))){
            for (Condutor c : condutores){
                out.format("%s;%d;%d;%s;%d;%s;%d%n", c.getNome(), c.getNif(), c.getTel(), c.getMorada(), c.getCartaoCid(), c.getCartaCond(), c.getSegSocial());
            }
        } catch (FileNotFoundException ex){
            System.out.println("Erro ao carregar condutores: " + ex.getMessage());
        }
    }

    /**
     * LÊ o ficheiro "condutores.txt" e carrega os condutores para o sistema.
     */
    private void carregarCondutores(){
        try (Scanner ler = new Scanner(new File("Logs/condutores.txt"))){
            while (ler.hasNextLine()) {
                String linha = ler.nextLine();
                String[] dados =  linha.split(";");

                if (dados.length >= 7) {
                    Condutor c = new Condutor(dados[0], Integer.parseInt(dados[1]), Integer.parseInt(dados[2]), dados[3], Integer.parseInt(dados[4]),dados[5], Integer.parseInt(dados[6]));
                    adicionarCondutor(c);
                }
            }
        }catch (FileNotFoundException ex){}
    }

    /**
     * Escreve o histórico de viagens no ficheiro "viagens.txt".
     * Guarda apenas os identificadores (NIFs e Matrícula) para manter a integridade referencial.
     * Converte ps valores decimais (kms e custo) substituindo vírguklas por pontos para garantir compatibilidade.
     */
    private void gravarViagens(){
        try (Formatter out = new Formatter(new File("Logs/viagens.txt"))){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            for (Viagem v : viagens){
                out.format("%d;%d;%s;%s;%s;%s;%s;%s;%s%n",
                        v.getCondutor().getNif(),
                        v.getCliente().getNif(),
                        v.getViatura().getMatricula(),
                        v.getDataHoraInicio().format(dtf),
                        v.getDataHoraFim().format(dtf),
                        v.getMoradaOrigem(),
                        v.getMoradaDestino(),
                        String.valueOf(v.getKms()).replace(',','.'),
                        String.valueOf(v.getCusto()).replace(',','.'));
            }
        } catch (FileNotFoundException ex){
            System.out.println("Erro ao gravar viagens: " + ex.getMessage());
        }
    }

    /**
     * Lê o ficheiro "viagens.txt" e reconstrói o historico de viagens.
     * Utiliza os metodos de pesquisa ({@code procurarCondutor}, {@code procurarCliente}, etc.)
     * para associar a viagem aos objetos corretos que já estão em memória.
     */
    private void carregarViagens(){
        try (Scanner ler = new Scanner(new File("Logs/viagens.txt"))){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            while (ler.hasNextLine()) {
                String linha = ler.nextLine();
                String[] dados =  linha.split(";");

                if (dados.length >= 9) {
                    Condutor condutor = procurarCondutor(Integer.parseInt(dados[0]));
                    Cliente  cliente = procurarCliente(Integer.parseInt(dados[1]));
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
        } catch (FileNotFoundException ex){}
    }
}