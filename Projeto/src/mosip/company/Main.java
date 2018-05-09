package mosip.company;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class Main {


    static float FIM_DA_SIMULACAO = 480;


    static Cliente cliente;
    static ArrayList<Evento> fila_espera;
    static TreeMap<Float,Evento> eventList;
    static Barbeiro barbeiro;

    static float relogio;

    static float numeroAtrasos;
    static float atrasoTotal;
    static float área_num_in_q;
    static float area_server_status;
    static float time_since_last_event;
    static float time_last_event;


    public static void main(String[] args) {
        inicializa();
        while (relogio<FIM_DA_SIMULACAO){
            temporizacao();
            estatistica();
            rotina();
        }
        imprimeRelatorios();    }

    private static void temporizacao() {
        /*if(eventList.isEmpty()){
            System.out.println("Lista de eventos vazia, a terminar execução!");
            imprimeRelatorios();
            System.exit(5);
        }*/
        if (!eventList.isEmpty ()) {
            Evento evento = eventList.get ( eventList.firstKey () );
            System.out.println ( "O próximo evento a decorrer é do tipo: " + evento.getTipo () );

            relogio = evento.getOcorrenciaDoEvento ();
            System.out.println ( "Relógio : " + relogio + "\n --------------------------------------- \n \n");
        } else {
            System.out.println ( "A lista de eventos encontra-se vazia!" );

        }
    }
    private static void estatistica() {
        time_since_last_event = relogio - time_last_event;
        time_last_event = relogio;
        área_num_in_q += clienteEmEspera()*time_since_last_event;

        float status = 0;
        if (barbeiro.getEstado()==Barbeiro.state.OCUPADO){
            status = 1;
        }
        area_server_status += status*time_since_last_event;
    }

    private static void geraEventoDeChegada(){
        //primeiro cliente
        Cliente cliente = new Cliente();

        boolean exists = true;
        float horaDaChegada = 0.0f;
        while (exists == true) {
            horaDaChegada = relogio + tempoDoChegada();
            if (!eventList.containsKey ( horaDaChegada )) {
                exists = false;
                break;
            }
        }
        cliente.setTempoChegada(horaDaChegada);
        System.out.println("Cliente vai chegar a " + cliente.getTempoDeChegada());


        //Criação do evento de chegada com o cliente
        Evento chegada = new Evento();
        chegada.setCliente(cliente);
        chegada.setOcorrenciaDoEvento(cliente.getTempoDeChegada());

        eventList.put(cliente.getTempoDeChegada(),chegada);
    }

    private static void geraEventoFimDeAtendimento(Cliente cliente){
        Evento corte = new Evento();
        boolean exists = true;
        float fimDeCorte = 0.0f;
        while (exists == true) {
            fimDeCorte = relogio + tempoDoServico();
            if (!eventList.containsKey ( fimDeCorte )) {
                exists = false;
                break;
            }
        }
        corte.setOcorrenciaDoEvento(fimDeCorte);
        System.out.println("Tempo de evento fim de atendimento: " + corte.getOcorrenciaDoEvento());
        corte.mudaEstado();
        cliente.setTempoCorte(corte.getOcorrenciaDoEvento());
        corte.setCliente(cliente);
        eventList.put(corte.getOcorrenciaDoEvento(),corte);
    }


    private static void rotina() {
        if(eventList.isEmpty ()){
            System.out.println("Gerei evento de Chegada");
            eventoChegada();
        } else {
            Evento evento = eventList.get(eventList.firstKey());

            if (evento.getTipo() == Evento.state.CHEGADA){
                System.out.println("Gerei evento de Chegada");
                eventoChegada();
            } else if (evento.getTipo() == Evento.state.CORTE){
                System.out.println("Gerei evento de Fim de Atendimento");
                eventoFimAtendimento();
            }
        }
    }

    private static void eventoFimAtendimento() {
        Evento evento = eventList.get(eventList.firstKey());
        eventList.remove(eventList.firstKey());
        if (fila_espera.isEmpty()){
            barbeiro.setEstado(Barbeiro.state.LIVRE);
            System.out.println ("\n Barbeiro ficou livre! \n");
        } else {
            fila_espera.remove(fila_espera.get(0));
            System.out.println ("Cliente removido da fila de espera.");
            numeroAtrasos++;
            geraEventoFimDeAtendimento(evento.getCliente());
            System.out.println ("\n Babeiro está a atender o próximo da fila de espera. \n");
        }
    }

    public static void eventoChegada(){
        //Nova chegada de cliente
        geraEventoDeChegada();

        Evento chegada = eventList.get(eventList.firstKey());
        eventList.remove(eventList.firstKey());

        // Verificar se o barbeiro está livre ou ocupado
        if (barbeiro.getEstado() == Barbeiro.state.OCUPADO){
            System.out.println("Cliente adicionado à lista de espera!");
            chegada.getCliente().setHoraDeChegada(relogio);
            fila_espera.add(chegada);

        } else {
            barbeiro.setEstado ( Barbeiro.state.OCUPADO );
            System.out.println ("\n Barbeiro ficou ocupado! \n" );
            numeroAtrasos++;
            atrasoTotal += 0 ;
            geraEventoFimDeAtendimento(chegada.getCliente());
        }

        System.out.println("Estão os seguintes clientes em espera: " + clienteEmEspera());
    }

    private static void imprimeRelatorios() {

        System.out.println("Relogio : "+relogio);
        System.out.println("Average_delay_queue : "+(atrasoTotal/numeroAtrasos));
        System.out.println("Average_num_in_queue : "+(área_num_in_q/relogio));
        System.out.println("Atraso total : "+atrasoTotal);
        System.out.println("Area da taxa de utilização fila de espera: "+área_num_in_q);
        System.out.println("Area da taxa de utilização do servidor: "+area_server_status);
        System.out.println("Clientes na fila de espera: " + fila_espera.size());
    }


    public static void inicializa() {
        fila_espera = new ArrayList<>(); // num_in_queue = 0
        eventList = new TreeMap<>(); // eventList
        relogio = 0; // Incializar relógio da simulação a 0
        time_since_last_event = 0; // // time_last_event = 0
        time_last_event = 0; // // time_last_event = 0
        atrasoTotal = 0; // total_delay = 0
        área_num_in_q = 0; // Área de utilização da fila de espera
        area_server_status = 0; // Área de utilização do servidor

        barbeiro = new Barbeiro(); //server_status = FREE
        geraEventoDeChegada();
    }

    static private float tempoDoChegada(){
        float tempo = 0;
        Random rand = new Random();
        tempo = rand.nextInt(19) + 1;
        return tempo;
    }


    static private float tempoDoServico(){
        float tempo = 0;
        Random rand = new Random();
        tempo = rand.nextInt ( 22) + 1;
        return tempo;
    }

    static int clienteEmEspera(){
        return fila_espera.size();
    }
}
