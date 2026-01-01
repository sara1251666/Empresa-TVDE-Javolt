package Gestao;

import Entidades.*;
import java.util.ArrayList;

/**
 * Classe Gestora da Empresa TVDE.
 * Centraliza os ArrayLists e a lógica de CRUD.
 * @version 1.0
 */
public class Empresa {

    // Listas em memória conforme requisito
    private ArrayList<Viatura> viaturas;
    private ArrayList<Condutor> condutores;
    private ArrayList<Cliente> clientes;
    private ArrayList<Viagem> viagens;
    private ArrayList<Reserva> reservas;

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
     * CREATE: Adiciona uma nova viatura se a matrícula não existir.
     */
    public boolean adicionarViatura(Viatura v) {
        if (procurarViatura(v.getMatricula()) == null) {
            viaturas.add(v);
            return true;
        }
        return false; // Matrícula já existe
    }

    /**
     * READ: Devolve a lista de todas as viaturas.
     */
    public ArrayList<Viatura> getViaturas() {
        return viaturas;
    }

    /**
     * READ (Individual): Procura viatura pela matrícula[cite: 2973].
     * Útil também para o UPDATE (obter o objeto e depois usar os setters).
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
     * DELETE: Remove viatura garantindo que não há dependências.
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
     * CREATE: Adiciona cliente se o NIF não existir.
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

    public ArrayList<Cliente> getClientes() {
        return clientes;
    }

    /**
     * READ (Individual): Procura cliente pelo NIF.
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
     * DELETE: Remove cliente garantindo que não há dependências.
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

    public boolean adicionarCondutor(Condutor c) {
        if (procurarCondutor(c.getNif()) == null) {
            condutores.add(c);
            return true;
        }
        return false;
    }

    public ArrayList<Condutor> getCondutores() {
        return condutores;
    }

    public Condutor procurarCondutor(int nif) {
        for (Condutor c : condutores) {
            if (c.getNif() == nif) {
                return c;
            }
        }
        return null;
    }

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

    public void adicionarViagem(Viagem v) {
        // Aqui poderias adicionar validação de sobreposição
        viagens.add(v);
    }

    public ArrayList<Viagem> getViagens() {
        return viagens;
    }

    public void adicionarReserva(Reserva r) {
        reservas.add(r);
    }

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