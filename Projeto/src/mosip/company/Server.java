package mosip.company;

public class Server {

    private state state;
    private float area_server_status;
    private float totaldelay;
    private float numberOfDelays;


    public enum state {
        LIVRE, OCUPADO;
    }

    public Server() {
        this.area_server_status = 0;
        this.state = state.LIVRE;
        this.totaldelay = 0;
        this.numberOfDelays = 0;
    }

    public void setState(state state) {
        this.state = state;
    }

    public state getState() {
        return state;
    }

    public void updateAreaServerStatus(float timeSinceLastEvent) {
        float status = 0;
        if (this.state == state.OCUPADO){
            area_server_status += 1 * timeSinceLastEvent;
        } else {
            area_server_status += 0 * timeSinceLastEvent;
        }
    }
    public float getArea_server_status() {
        return area_server_status;
    }


    public float getTotaldelay() {
        return totaldelay;
    }

    public void setTotaldelay(float totaldelay) {
        this.totaldelay = totaldelay;
    }

    public float getNumberOfDelays() {
        return numberOfDelays;
    }

    public void addDelay() {
        this.numberOfDelays++;
    }
}