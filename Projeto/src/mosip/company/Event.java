package mosip.company;

import org.jetbrains.annotations.NotNull;

public class Event{

    private state state;
    private Order order;
    private float EventOccurrency;


    public enum state {
        CHEGADA, PREP_ENVIO, COMPRA, ENVIO_CLIENTE, TRANSPORTADORA;
    }

    public Event() {
        state = state.CHEGADA;
        order = new Order();
        EventOccurrency = 0.0f;
    }

    public void setState(state state) {
        this.state = state;
    }

    public state getTipo() {
        return state;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public float getEventOccurrency() {
        return EventOccurrency;
    }

    public void setEventOccurrency(Float ocorrenciaDoEvento) {
        this.EventOccurrency = ocorrenciaDoEvento;
    }

    @Override
    public String toString() {
        return "Event{" +
                "state=" + state +
                '}';
    }


}
