package mosip.company;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import static java.lang.Math.round;

public class Main {


    private static float FIM_DA_SIMULACAO = 480;


    private static Cliente cliente;
    private static ArrayList<Evento> fila_espera;
    private static TreeMap<Float, Evento> eventList;
    private static Barbeiro barbeiro;

    private static float relogio;

    //Estatistica
    private static float numeroAtrasos;
    private static float atrasoTotal;
    private static float area_num_in_q; // Tamanho da fila de espera
    private static float area_server_status; // Taxa utilização do servidor


    private static float time_since_last_event;
    private static float time_last_event;


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


        area_num_in_q += clienteEmEspera() * time_since_last_event;

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
        while (exists) {
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
        while (exists) {
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
            Evento eventoCliente = fila_espera.get ( 0 );
            fila_espera.remove ( eventoCliente );
            System.out.println ("Cliente removido da fila de espera.");
            numeroAtrasos++;
            atrasoTotal += relogio - eventoCliente.getCliente ().getHoraDeChegada () ;
            geraEventoFimDeAtendimento(evento.getCliente());
            System.out.println ("\n Babeiro está a atender o próximo da fila de espera. \n");
        }
    }

    private static void eventoChegada() {
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
            geraEventoFimDeAtendimento(chegada.getCliente());
        }

        System.out.println("Estão os seguintes clientes em espera: " + clienteEmEspera());
    }

    private static void imprimeRelatorios() {
        System.out.println ("\n\n ####################################### \n ");
        System.out.println("Relogio : "+relogio);
        System.out.println("Número de clientes na fila de espera que ficaram por atender : " + clienteEmEspera());
        System.out.println("Número de clientes que estiveram na final de espera : " + numeroAtrasos);

        System.out.println ("\n ####################################### \n ");
        float mediaEspera = atrasoTotal / numeroAtrasos;
        double roundedMediaEspera = Math.round(mediaEspera * 100.0) / 100.0;
        System.out.println("Tempo médio de espera : " + roundedMediaEspera + " minutos");

        System.out.println("Média de uso da fila de espera : " + round(area_num_in_q / relogio));

        float mediaUtilizacaoServer = (area_server_status / relogio) * 100;
        double roundedMediaUtilizacaoServer = Math.round(mediaUtilizacaoServer * 100.0) / 100.0;
        System.out.println("Média de utilização do servidor: " + roundedMediaUtilizacaoServer + " %");
        System.out.println ("\n ####################################### \n \n");

    }


    private static void inicializa() {
        fila_espera = new ArrayList<>(); // num_in_queue = 0
        eventList = new TreeMap<>(); // eventList
        relogio = 0; // Incializar relógio da simulação a 0
        time_since_last_event = 0; // // time_last_event = 0
        time_last_event = 0; // // time_last_event = 0
        atrasoTotal = 0; // total_delay = 0
        area_num_in_q = 0; // Área de utilização da fila de espera
        area_server_status = 0; // Área de utilização do servidor

        barbeiro = new Barbeiro(); //server_status = FREE
        geraEventoDeChegada();
    }

    static private float tempoDoChegada(){
        Random rand = new Random();
        rand.setSeed(123);
        return rand.nextInt(19) + 1;
    }


    static private float tempoDoServico(){
        Random rand = new Random();
        rand.setSeed(123);
        return rand.nextInt(22) + 1;
    }

    static private int clienteEmEspera() {
        return fila_espera.size();
    }
}
