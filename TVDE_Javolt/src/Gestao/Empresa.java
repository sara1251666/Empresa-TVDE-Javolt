package Gestao;

import Entidades.*;
import java.util.ArrayList;

/**
 * A classe Empresa atua como o gestor central de dados do sistema TVDE.
 * <p>
 * Responsável por armazenar em memória (ArrayLists) todas as entidades
 * (Viaturas, Condutores, Clientes, Viagens e Reservas) e executar
 * a lógica de negócio principal, incluindo validações de integridade
 * referencial (não apagar entidades com dependências).
 * </p>
 *
 * @author O Teu Grupo
 * @version 1.0
 * @since 2026-01-01
 */

public class Empresa {

    /** Lista de viaturas registadas na empresa. */
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
     * Regista uma nova viagem realizada.
     *
     * @param v A viagem a adicionar ao histórico.
     */

    public void adicionarViagem(Viagem v) {
        // Aqui poderias adicionar validação de sobreposição
        viagens.add(v);
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
}