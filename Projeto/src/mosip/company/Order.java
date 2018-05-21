package mosip.company;

public class Order {

    private state state;

    private float arrivalTime;
    private float endOfEventTime;
    private float globalArrivalTime;

    public enum state {
        ORDERED_BY_CLIENT, PURCHASED, PREPARING_TO_SEND,DELIVERED,SUPPLIER_SHIPPED;
    }

    public Order() {

        state = state.ORDERED_BY_CLIENT;
        arrivalTime = 0;
        globalArrivalTime = 0;
    }

    public void setState(state state) {
        this.state = state;
    }

    public void setArrivalTime(float tempo){
        arrivalTime = tempo;
    }


    public state getState() {
        return state;
    }

    public float getArrivalTime() {
        return arrivalTime;
    }

    public float getGlobalArrivalTime() {
        return globalArrivalTime;
    }

    public void setGlobalArrivalTime(float globalArrivalTime) {
        this.globalArrivalTime = globalArrivalTime;
    }
}
