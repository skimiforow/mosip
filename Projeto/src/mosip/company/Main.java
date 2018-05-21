package mosip.company;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static java.lang.Math.round;

public class Main {




    private static float clock;

    //Lista de eventos
    private static TreeMap<Float, Event> eventList;

    //Servidores
    private static Server warehouse;
    private static Server purchasing;

    //Fila de espera do armazém e das compras
    private static GenericQueue warehouseQueue;
    private static GenericQueue purchasingQueue;


    //Estatistica
    private static float numberOfDelays;
    private static float totalDelay;
    private static float timeSinceLastEvent;
    private static float timeLastEvent;
    private static float averageTimeToDeliverySinceOrder;
    private static float timeToDeliverySinceOrder;
    private static float numOfOrders;

    //Dados de alietoriedade
    private final static float END_OF_SIMULATION = 2400;

    private static final int chegada_de = 1;
    private static final int chegada_ate = 30;

    private static final int prep_envio_de = 10;
    private static final int prep_envio_ate = 60;

    private static final int compra_de = 10;
    private static final int compra_ate = 30;

    private static final int transportadora_de = 720;
    private static final int transportadora_ate = 1240;

    private static final int envio_para_cliente_de = 720;
    private static final int envio_para_cliente_ate = 1240;

    public static void main(String[] args) {
        initialization();
        while (clock < END_OF_SIMULATION){
            statisticsUpdate();
            statistics();
            routine();
        }
        imprimeRelatorios();    }

    private static void statisticsUpdate() {
        if (!eventList.isEmpty ()) {
            Event event = eventList.get(eventList.firstKey());
            System.out.println ( "O próximo event a decorrer é do tipo: " + event.getTipo () );
            clock = event.getEventOccurrency();
            System.out.println ( "Relógio : " + clock + "\n --------------------------------------- \n \n");
        } else {
            System.out.println ( "A lista de eventos encontra-se vazia!" );

        }
    }
    private static void statistics() {
        timeSinceLastEvent = clock - timeLastEvent;
        timeLastEvent = clock;

        purchasingQueue.updateStatistic();
        warehouseQueue.updateStatistic();

        purchasing.updateAreaServerStatus(timeSinceLastEvent);
        warehouse.updateAreaServerStatus(timeSinceLastEvent);
    }


    private static void routine() {
        Event event = eventList.get(eventList.firstKey());

        if(eventList.isEmpty ()){
            System.out.println("Gerei evento de chegada de encomenda porque a lista de eventos estava vazia");
            arrivalEvent(new Event());
        } else {
            //eventList.remove(eventList.firstKey());
            if (event.getTipo() == Event.state.CHEGADA){
                System.out.println("Gerei evento de chegada de encomenda");
                arrivalEvent(event);
            } else if (event.getTipo() == Event.state.PREP_ENVIO){
                System.out.println("Gerei evento de stock para preparação de envio");
                orderPreparationEvent(event);
            } else if (event.getTipo() == Event.state.COMPRA){
                System.out.println("Gerei evento de compra");
                orderPurchaseEvent(event);
            } else if (event.getTipo() == Event.state.TRANSPORTADORA){
                System.out.println("Gerei evento de transporte de fornecedor");
                transportationFromSupplierEvent(event);
            } else if (event.getTipo() == Event.state.ENVIO_CLIENTE){
                System.out.println("Gerei evento de Chegada");
                shipmentToClientEvent(event);
            }
        }
    }

    /**
     * @param event O Objeto evento a decorrer
     * @param from Para a fonte de alietoriedade, o inicio
     * @param to Para a fonte de alietoriedade, o fim
     * @param state Para que estado vai o Evento
     */
    private static void eventGenerator(Event event,int from, int to, Event.state state) {
        event.setState(state);

        if(state == Event.state.CHEGADA)
            event.getOrder().setState(Order.state.ORDERED_BY_CLIENT);
        if(state == Event.state.PREP_ENVIO)
            event.getOrder().setState(Order.state.PREPARING_TO_SEND);
        if(state == Event.state.COMPRA)
            event.getOrder().setState(Order.state.PURCHASED);
        if(state == Event.state.TRANSPORTADORA)
            event.getOrder().setState(Order.state.SUPPLIER_SHIPPED);
        if(state == Event.state.ENVIO_CLIENTE)
            event.getOrder().setState(Order.state.DELIVERED);

        float eventTimeDuration = timeGenerator(from,to);

        float eventOccurringTime = clock + eventTimeDuration;
        while (true) {
            if (!eventList.containsKey ( eventOccurringTime )) {
                break;
            }
            eventOccurringTime += 1f;
        }
        if(state == Event.state.CHEGADA)
            event.getOrder().setArrivalTime(eventOccurringTime - clock);
        event.getOrder().setGlobalArrivalTime(eventOccurringTime);
        event.setEventOccurrency(event.getOrder().getGlobalArrivalTime());

        eventList.put(event.getEventOccurrency(),event);
    }

    private static void orderPreparationEvent(Event event) {
        if (warehouseQueue.isEmpty()){
            eventList.remove(event.getEventOccurrency(),event);
            warehouse.setState(Server.state.LIVRE);
            System.out.println ("\n Armazém ficou livre! \n");
        } else {
            Event eventFromQueue = warehouseQueue.removeFromQueue();
            System.out.println ("Encomenda removido da fila de espera do armazém.");
            numberOfDelays++;
            totalDelay += clock - eventFromQueue.getEventOccurrency();
            eventGenerator(eventFromQueue,envio_para_cliente_de,envio_para_cliente_ate,Event.state.ENVIO_CLIENTE);
            System.out.println ("\n O Armazém está a preparar o próximo da fila de espera. \n");
        }
    }

    private static void shipmentToClientEvent(Event event) {
        eventList.remove(event.getEventOccurrency(),event);
        timeToDeliverySinceOrder = (clock - event.getOrder().getArrivalTime());
        numOfOrders++;
    }

    private static void orderPurchaseEvent(Event event) {
        if (purchasingQueue.isEmpty()){
            eventList.remove(event.getEventOccurrency(),event);
            purchasing.setState(Server.state.LIVRE);
            System.out.println ("\n Compras ficou livre! \n");
        } else {
            Event eventFromQueue = purchasingQueue.removeFromQueue();
            System.out.println ("Encomenda removido da fila de espera das compras.");
            numberOfDelays++;
            totalDelay += clock - eventFromQueue.getEventOccurrency() ;
            eventGenerator(eventFromQueue,transportadora_de,transportadora_ate,Event.state.TRANSPORTADORA);
            System.out.println ("\n As compras estão a comprar o próximo da fila de espera. \n");
        }
    }

    private static void transportationFromSupplierEvent(Event event) {
        eventList.remove(event.getEventOccurrency(),event);
    }

    private static void arrivalEvent(Event event) {
        //gera o próximo evento de chegada
        eventGenerator(new Event(),chegada_de,chegada_ate,Event.state.CHEGADA);

        Event.state state = nextEventRandomize();
            if (state == Event.state.PREP_ENVIO){
                System.out.println("Gerei encomenda de stock, para preparação de envio");

                if(warehouse.getState() == Server.state.OCUPADO){
                    System.out.println("Encomenda adicionada à lista de espera do armazém");
                    event.getOrder().setArrivalTime(clock);
                    warehouseQueue.addToQueue(event);
                } else {
                    System.out.println("Armazém ficou ocupado");
                    warehouse.setState(Server.state.OCUPADO);
                    numberOfDelays++;
                    eventGenerator(event,prep_envio_de,prep_envio_ate,Event.state.PREP_ENVIO);
                }
            } else {
                System.out.println("Gerei evento para encomenda a fornecedor");

                if(purchasing.getState() == Server.state.OCUPADO){
                    System.out.println("Encomenda adicionada à lista de espera das compras");
                    event.getOrder().setArrivalTime(clock);
                    purchasingQueue.addToQueue(event);
                } else {
                    System.out.println("Compras ficaram ocupadas");
                    purchasing.setState(Server.state.OCUPADO);
                    numberOfDelays++;
                    eventGenerator(event,compra_de,compra_ate,Event.state.COMPRA);
                }
            }
    }



    private static void imprimeRelatorios() {
        System.out.println ("\n\n ####################################### \n ");
        System.out.println("Relogio : "+ clock);
        System.out.println("Número de encomendas na fila de espera que ficaram por processar na fila do Armazém  : " + warehouseQueue.getSizeOfQueue());
        System.out.println("Número de encomendas na fila de espera que ficaram por processar na fila das Compras  : " + purchasingQueue.getSizeOfQueue());
        System.out.println("Número de clientes que estiveram na final de espera : " + numberOfDelays);

        System.out.println ("\n ####################################### \n ");
        float mediaEspera = totalDelay / numberOfDelays;
        double roundedMediaEspera = Math.round(mediaEspera * 100.0) / 100.0;
        System.out.println("Tempo médio de espera : " + roundedMediaEspera + " minutos");

        averageTimeToDeliverySinceOrder = timeToDeliverySinceOrder / numOfOrders;
        System.out.println("Tempo médio de entrega a cliente : " + averageTimeToDeliverySinceOrder + " minutos");

        System.out.println("Média de uso da fila de espera do armazém : " + round(warehouseQueue.getStatistic() / clock));
        System.out.println("Média de uso da fila de espera das compras : " + round(purchasingQueue.getStatistic() / clock));

        float mediaUtilizacaoArmazem = (warehouse.getArea_server_status() / clock) * 100;
        float mediaUtilizacaoCompras = (purchasing.getArea_server_status() / clock) * 100;
        double roundedMediaUtilizacaoServerArmazem = Math.round(mediaUtilizacaoArmazem * 100.0) / 100.0;
        double roundedMediaUtilizacaoServerCompras = Math.round(mediaUtilizacaoCompras * 100.0) / 100.0;
        System.out.println("Média de utilização do servidor Armazém: " + roundedMediaUtilizacaoServerArmazem + " %");
        System.out.println("Média de utilização do servidor Compras: " + roundedMediaUtilizacaoServerCompras + " %");
        System.out.println ("\n ####################################### \n \n");

    }


    private static void initialization() {
        //preparationQueue = new ArrayList<>(); // num_in_queue = 0
        //purchasingQueue = new ArrayList<>(); // num_in_queue = 0
        //area_num_in_q = 0; // Área de utilização da fila de espera
        //area_server_status = 0; // Área de utilização do servidor

        warehouse = new Server(); //server_status = FREE
        warehouseQueue  = new GenericQueue();

        purchasing = new Server(); //server_status = FREE
        purchasingQueue  = new GenericQueue();

        eventList = new TreeMap<>(); // eventList
        clock = 0; // Incializar relógio da simulação a 0
        timeSinceLastEvent = 0; // // timeLastEvent = 0
        timeLastEvent = 0; // // timeLastEvent = 0
        totalDelay = 0; // total_delay = 0
        averageTimeToDeliverySinceOrder = 0;
        timeToDeliverySinceOrder = 0;
        numOfOrders = 0;

        eventGenerator(new Event(),chegada_de,chegada_ate,Event.state.CHEGADA);
    }

    // Gerador de tempos aleatórios
    static private float timeGenerator(int from, int to){
        Random rand = new Random();
        //rand.setSeed(50);
        return rand.nextInt(to) + from;
    }

    //Gerar com probablidade 70/30 se é uma encomenda de stock para ou para compra
    static private Event.state nextEventRandomize(){
        Random rand = new Random();
        //rand.setSeed(50);
        int i = rand.nextInt(100) + 1;
        if (i<30)
            return Event.state.PREP_ENVIO;
        return Event.state.COMPRA;
    }
}
