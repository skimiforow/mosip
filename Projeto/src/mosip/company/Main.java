package mosip.company;

import java.util.Random;
import java.util.TreeMap;

public class Main {

    private  float clock;

    //Lista de eventos
    private  TreeMap<Float, Event> eventList;

    //Servidores
    private  Server warehouse;
    private  Server purchasing;

    //Fila de espera do armazém e das compras
    private GenericQueue warehouseQueue;
    private GenericQueue purchasingQueue;


    //Estatistica
    private  float numberOfDelays;
    private  float timeSinceLastEvent;
    private  float timeLastEvent;
    private  float averageTimeToDeliverySinceOrder;
    private  float timeToDeliverySinceOrder;
    private  float numOfOrders;
    private  float numOfOrdersDelivered;
    private int count_chegada;
    private int count_compra;
    private int count_stock;
    private int count_transportadora;

    //Dados de alietoriedade
    private final  float END_OF_SIMULATION = 9600; // Sensívelmente um mês
    private static final int NUM_ITERATIONS = 1000; // numero de testes

    private static float[] results;

    private final int chegada_de = 4;
    private final int chegada_ate = 6;

    private final int prep_envio_de = 8;
    private final int prep_envio_ate = 12; // uma encomenda demora entre 30 a 60 minutos a processar, visto que são 4 pessoas no armazém dividi os valores por 4

    private final int compra_de = 4;
    private final int compra_ate = 8; // uma compra demora entre 10 a 20 minutos a processar, visto que são 2 pessoas a processar encomendas divido o tempo a metade.

    private final int transportadora_de = 720;
    private final int transportadora_ate = 1240;

    private final int envio_para_cliente_de = 720;
    private final int envio_para_cliente_ate = 1240;

    public static void main(String[] args) {
        results = new float[12];
        int percentage = 0;

        for (int run = 0; run < NUM_ITERATIONS; run++) {
            if (run > 0)
                percentage = ((run * 100) / NUM_ITERATIONS) + 1;
            new Main().start();


            System.out.println("Completing: " + percentage + "%");

        }
        System.out.println();
        System.out.println("\n ####################################### \n ");
        System.out.println("Número de encomendas na fila de espera que ficaram por processar na fila do Armazém  : " + results[0] / NUM_ITERATIONS);
        System.out.println("Número de encomendas na fila de espera que ficaram por processar na fila das Compras  : " + results[1] / NUM_ITERATIONS);
        System.out.println("Ficaram " + (int) results[2] / NUM_ITERATIONS + " eventos por processar");
        System.out.println("Encomendas entregues " + (int) results[4] / NUM_ITERATIONS + "/" + (int) results[3] / NUM_ITERATIONS);
        System.out.println("Tempo médio de espera do armazém : " + (int) results[5] / NUM_ITERATIONS + " minutos");
        System.out.println("Tempo médio de espera das compras : " + (int) results[6] / NUM_ITERATIONS + " minutos");
        System.out.println("Tempo médio de entrega a cliente : " + (int) results[7] / NUM_ITERATIONS + " minutos");
        System.out.println("Média de uso da fila de espera do armazém : " + results[8] / NUM_ITERATIONS);
        System.out.println("Média de uso da fila de espera das compras : " + results[9] / NUM_ITERATIONS);
        System.out.println("Média de utilização do servidor Armazém: " + results[10] / NUM_ITERATIONS + " %");
        System.out.println("Média de utilização do servidor Compras: " + results[11] / NUM_ITERATIONS + " %");

    }

    public void start() {
        initialization();
        while (clock < END_OF_SIMULATION) {
            statisticsUpdate();
            statistics();
            routine();
        }
        imprimeRelatorios();
    }

    private  void statisticsUpdate() {
        if (!eventList.isEmpty ()) {
            Event event = eventList.get(eventList.firstKey());
            //System.out.println ( "O próximo evento a decorrer é do tipo: " + event.getTipo () );
            clock = event.getEventOccurrency();
            //System.out.println ( "Relógio : " + clock + "\n --------------------------------------- \n \n");
        } else {
            //System.out.println ( "A lista de eventos encontra-se vazia!" );

        }
    }
    private void statistics() {
        timeSinceLastEvent = clock - timeLastEvent;
        timeLastEvent = clock;
        purchasingQueue.setTimeOfLastEvent(timeSinceLastEvent);
        warehouseQueue.setTimeOfLastEvent(timeSinceLastEvent);

        purchasingQueue.updateStatistic();
        warehouseQueue.updateStatistic();

        purchasing.updateAreaServerStatus(timeSinceLastEvent);
        warehouse.updateAreaServerStatus(timeSinceLastEvent);
    }


    private void routine() {
        if(eventList.isEmpty ()){
            //System.out.println("Gerei evento de chegada de encomenda porque a lista de eventos estava vazia");
            arrivalEvent(new Event());
        } else {
            Event event = eventList.remove(eventList.firstKey());
            if (event.getTipo() == Event.state.CHEGADA){
                //System.out.println("Gerei evento de chegada de encomenda");
                count_chegada++;
                arrivalEvent(event);
            } else if (event.getTipo() == Event.state.PREP_ENVIO){
                //System.out.println("Gerei evento de stock para preparação de envio");
                count_stock++;
                orderPreparationEvent(event);
            } else if (event.getTipo() == Event.state.COMPRA){
                //System.out.println("Gerei evento de compra");
                count_compra++;
                orderPurchaseEvent(event);
            } else if (event.getTipo() == Event.state.TRANSPORTADORA){
                //System.out.println("Gerei evento de transporte de fornecedor");
                count_transportadora++;
                transportationFromSupplierEvent(event);
            } else if (event.getTipo() == Event.state.ENVIO_CLIENTE){
                //System.out.println("Gerei evento de Chegada");
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
    private void eventGenerator(Event event,int from, int to, Event.state state) {
        event.setState(state);

        if(state == Event.state.CHEGADA){
            event.getOrder().setState(Order.state.ORDERED_BY_CLIENT);
            numOfOrders++;
        }
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
            eventOccurringTime += 0.1f;
        }
        if(state == Event.state.CHEGADA)
            event.getOrder().setArrivalTime(eventOccurringTime - clock);
        event.getOrder().setGlobalArrivalTime(eventOccurringTime);
        event.setEventOccurrency(eventOccurringTime);

        eventList.put(event.getOrder().getGlobalArrivalTime(),event);
    }

    /**
     * @param event O Objeto evento a decorrer
     */
    private void orderPreparationEvent(Event event) {
        if (warehouseQueue.isEmpty()){
            warehouse.setState(Server.state.LIVRE);
            eventGenerator(event,envio_para_cliente_de,envio_para_cliente_ate,Event.state.ENVIO_CLIENTE);
            //System.out.println ("\n Armazém ficou livre! \n");
        } else {
            Event eventFromQueue = warehouseQueue.removeFromQueue();
            //System.out.println ("Encomenda removido da fila de espera do armazém.");
            warehouse.addDelay();
            warehouse.setTotaldelay(warehouse.getTotaldelay()+(clock - eventFromQueue.getEventOccurrency()));
            eventGenerator(event,prep_envio_de,prep_envio_ate,Event.state.PREP_ENVIO);
            eventGenerator(eventFromQueue,envio_para_cliente_de,envio_para_cliente_ate,Event.state.ENVIO_CLIENTE);
            //System.out.println ("\n O Armazém está a preparar o próximo da fila de espera. \n");
        }
    }

    private  void shipmentToClientEvent(Event event) {
        //eventList.remove(event.getEventOccurrency(),event);
        timeToDeliverySinceOrder += (clock - event.getOrder().getArrivalTime());
        numOfOrdersDelivered++;
    }

    private  void orderPurchaseEvent(Event event) {
        if (purchasingQueue.isEmpty()){
            purchasing.setState(Server.state.LIVRE);
            eventGenerator(event,transportadora_de,transportadora_ate,Event.state.TRANSPORTADORA);
            //System.out.println ("\n Compras ficou livre! \n");
        } else {
            Event eventFromQueue = purchasingQueue.removeFromQueue();
            //System.out.println ("Encomenda removido da fila de espera das compras.");
            purchasing.addDelay();
            purchasing.setTotaldelay(purchasing.getTotaldelay()+(clock - eventFromQueue.getEventOccurrency()));
            eventGenerator(event,compra_de,compra_ate,Event.state.COMPRA);
            eventGenerator(eventFromQueue,transportadora_de,transportadora_ate,Event.state.TRANSPORTADORA);
            //System.out.println ("\n As compras estão a comprar o próximo da fila de espera. \n");
        }
    }

    private  void transportationFromSupplierEvent(Event event) {
        //eventList.remove(event.getEventOccurrency(),event);
        eventGenerator(event,envio_para_cliente_de,envio_para_cliente_ate,Event.state.ENVIO_CLIENTE);

    }

    private  void arrivalEvent(Event event) {
        //gera o próximo evento de chegada
        eventGenerator(new Event(),chegada_de,chegada_ate,Event.state.CHEGADA);

        Event.state state = nextEventRandomize();
            if (state == Event.state.PREP_ENVIO){
                //System.out.println("Gerei encomenda de stock, para preparação de envio");

                if(warehouse.getState() == Server.state.OCUPADO){
                    //System.out.println("Encomenda adicionada à lista de espera do armazém");
                    event.getOrder().setArrivalTime(clock);
                    warehouseQueue.addToQueue(event);
                } else {
                    //System.out.println("Armazém ficou ocupado");
                    warehouse.setState(Server.state.OCUPADO);
                    warehouse.addDelay();
                    eventGenerator(event,prep_envio_de,prep_envio_ate,Event.state.PREP_ENVIO);
                }
            } else {
                //System.out.println("Gerei evento para encomenda a fornecedor");

                if(purchasing.getState() == Server.state.OCUPADO){
                    //System.out.println("Encomenda adicionada à lista de espera das compras");
                    event.getOrder().setArrivalTime(clock);
                    purchasingQueue.addToQueue(event);
                } else {
                    // System.out.println("Compras ficaram ocupadas");
                    purchasing.setState(Server.state.OCUPADO);
                    purchasing.addDelay();
                    eventGenerator(event,compra_de,compra_ate,Event.state.COMPRA);
                }
            }
    }



    private  void imprimeRelatorios() {
        //System.out.println ("\n\n ####################################### \n ");
        //System.out.println("Relógio : "+ clock);
        //System.out.println("Número de encomendas na fila de espera que ficaram por processar na fila do Armazém  : " + warehouseQueue.getSizeOfQueue());
        results[0] += warehouseQueue.getSizeOfQueue();
        //System.out.println("Número de encomendas na fila de espera que ficaram por processar na fila das Compras  : " + purchasingQueue.getSizeOfQueue());
        results[1] += warehouseQueue.getSizeOfQueue();

        //System.out.println("Ficaram " + eventList.size() + " eventos por processar");
        results[2] += eventList.size();

        //System.out.println("Encomendas entregues " + numOfOrdersDelivered + "/" + numOfOrders);
        results[3] += numOfOrders;
        results[4] += numOfOrdersDelivered;

        //System.out.println("Encomendas: " + count_chegada);
        //System.out.println("Enc. stock: " + count_stock);
        //System.out.println("Enc. compra: " + count_compra);
        //System.out.println("Enc. a vir do fornecedor: " + count_transportadora);

        //System.out.println ("\n ####################################### \n ");
        float mediaEspera = warehouse.getTotaldelay() / warehouse.getNumberOfDelays();
        float roundedMediaEspera = (float) (Math.round(mediaEspera * 100.0) / 100.0);
        //System.out.println("Tempo médio de espera do armazém : " + roundedMediaEspera + " minutos");
        results[5] += roundedMediaEspera;

        mediaEspera = purchasing.getTotaldelay() / purchasing.getNumberOfDelays();
        roundedMediaEspera = (float) ((float) Math.round(mediaEspera * 100.0) / 100.0);
        //System.out.println("Tempo médio de espera das compras : " + roundedMediaEspera + " minutos");
        results[6] += roundedMediaEspera;

        averageTimeToDeliverySinceOrder = timeToDeliverySinceOrder / numOfOrdersDelivered;
        //System.out.println("Tempo médio de entrega a cliente : " + averageTimeToDeliverySinceOrder + " minutos");
        results[7] += averageTimeToDeliverySinceOrder;

        //System.out.println("Média de uso da fila de espera do armazém : " + Math.round(warehouseQueue.getStatistic() / clock));
        results[8] += Math.round(warehouseQueue.getStatistic() / clock);

        //System.out.println("Média de uso da fila de espera das compras : " + Math.round(purchasingQueue.getStatistic() / clock));
        results[9] += Math.round(purchasingQueue.getStatistic() / clock);

        float mediaUtilizacaoArmazem = (warehouse.getArea_server_status() / clock) * 100;
        float mediaUtilizacaoCompras = (purchasing.getArea_server_status() / clock) * 100;
        float roundedMediaUtilizacaoServerArmazem = (float) (Math.round(mediaUtilizacaoArmazem * 100.0) / 100.0);
        float roundedMediaUtilizacaoServerCompras = (float) (Math.round(mediaUtilizacaoCompras * 100.0) / 100.0);
        //System.out.println("Média de utilização do servidor Armazém: " + roundedMediaUtilizacaoServerArmazem + " %");
        results[10] += roundedMediaUtilizacaoServerArmazem;

        //System.out.println("Média de utilização do servidor Compras: " + roundedMediaUtilizacaoServerCompras + " %");
        results[11] += roundedMediaUtilizacaoServerCompras;

        //System.out.println ("\n ####################################### \n \n");

    }


    private  void initialization() {

        warehouse = new Server(); //server_status = FREE
        warehouseQueue  = new GenericQueue();

        purchasing = new Server(); //server_status = FREE
        purchasingQueue  = new GenericQueue();

        eventList = new TreeMap<>(); // eventList
        clock = 0; // Incializar relógio da simulação a 0
        timeSinceLastEvent = 0; // // timeLastEvent = 0
        timeLastEvent = 0; // // timeLastEvent = 0
        averageTimeToDeliverySinceOrder = 0;
        timeToDeliverySinceOrder = 0;
        numOfOrders = 0;
        numOfOrdersDelivered = 0;

        eventGenerator(new Event(),chegada_de,chegada_ate,Event.state.CHEGADA);
    }

    // Gerador de tempos aleatórios
     private float timeGenerator(int from, int to){
        Random rand = new Random();
        //rand.setSeed(50);
        return rand.nextInt(to) + from;
    }

    //Gerar com probablidade 70/30 se é uma encomenda de stock para ou para compra
     private Event.state nextEventRandomize(){
        Random rand = new Random();
        //rand.setSeed(50);
        int i = rand.nextInt(100) + 1;
        if (i<30)
            return Event.state.PREP_ENVIO;
        return Event.state.COMPRA;
    }
}
