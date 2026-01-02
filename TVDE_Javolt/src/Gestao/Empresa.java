package Gestao;

import Entidades.*;

import java.sql.SQLOutput;
import java.time.LocalDate;
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

            // Lógica simplificada: A viagem assume os dados da reserva + dados operacionais
            // O ideal seria passar também a dataHoraFim real

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

            adicionarViagem(novaViagem);
            reservas.remove(r); // Remove a reserva pois já foi efetuada
            return true;
        }
        return false;
    }

    // ==========================================================
    //                 RELATÓRIOS E ESTATÍSTICAS
    // ==========================================================

    public double calcularFaturacaoCondutor(int nifCondutor, LocalDateTime inicio, LocalDateTime fim) {
        double total = 0.0;
        for (Viagem viagem : viagens) {
            if (viagem.getCondutor().getNif() == nifCondutor) {

            }
        }
    }


    // ==========================================================
    //                        PERSISTÊNCIA
    // ==========================================================

    public void gravarDados(){
        gravarViaturas();
        gravarClientes();
        gravarCondutores();
        gravarViagens();
    }

    public void carregarDados(){
        carregarViaturas();
        carregarClientes();
        carregarCondutores();
        carregarViagens();
    }
    private void gravarViaturas() {
        try (Formatter out = new Formatter(new File("viaturas.txt"))) {
            for (Viatura v : viaturas){
                out.format("%s;%s;%s;%d%n", v.getMatricula(), v.getMarca(), v.getModelo(), v.getAnoFabrico());
                }
        } catch (FileNotFoundException ex) {
            System.out.println("Erro ao carregar viaturas: " + ex.getMessage());
        }
    }

    private void carregarViaturas() {
        try (Scanner ler = new Scanner(new File("viaturas.txt"))){
            while (ler.hasNextLine()) {
                String linha = ler.nextLine();
                String[] dados =  linha.split(";");

                if (dados.length >= 4) {
                    Viatura v = new Viatura(dados[0], dados[1], dados[2], Integer.parseInt(dados[3]));
                    adicionarViatura(v);
                }
            }
        }catch (FileNotFoundException ex){

        }
    }
    private void gravarClientes(){
        try (Formatter out = new Formatter(new File("clientes.txt"))){
            for (Cliente c : clientes){
                out.format("%s;%d;%d;%s;%d%n", c.getNome(), c.getNif(), c.getTel(), c.getMorada(), c.getCartaoCid());
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Erro ao carregar clientes: " + ex.getMessage());
        }
    }

    private void carregarClientes(){
        try (Scanner ler = new Scanner(new File("clientes.txt"))){
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

    private void gravarCondutores(){
        try ( Formatter out = new Formatter(new File("condutores.txt"))){
            for (Condutor c : condutores){
                out.format("%s;%d;%d;%s;%d;%s;%d%n", c.getNome(), c.getNif(), c.getTel(), c.getMorada(), c.getCartaoCid(), c.getCartaCond(), c.getSegSocial());
            }
        } catch (FileNotFoundException ex){
            System.out.println("Erro ao carregar condutores: " + ex.getMessage());
        }
    }

    private void carregarCondutores(){
        try (Scanner ler = new Scanner(new File("condutores.txt"))){
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
    private void gravarViagens(){
        try (Formatter out = new Formatter(new File("viagens.txt"))){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            for (Viagem v : viagens){
                out.format("%d;%d;%s;%s;%s;%s,%s;%s;%s%n",
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
            System.out.println("Erro ao carregar viagens: " + ex.getMessage());
        }
    }
}