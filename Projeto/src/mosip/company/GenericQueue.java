package mosip.company;

import java.util.ArrayList;

public class GenericQueue {
    private ArrayList<Event> queue;
    private float timeOfLastEvent;
    private float areaNumInQueue;

    public GenericQueue() {
        this.queue = new ArrayList<>();
        this.timeOfLastEvent = 0;
        this.areaNumInQueue = 0;
    }

    public void addToQueue(Event event){
        this.queue.add(event);
    }

    public Event removeFromQueue(){
        Event event = this.queue.get(0);
        this.queue.remove(event);
        return event;
    }

    public void updateStatistic(){
        this.areaNumInQueue += this.getSizeOfQueue()*this.getTimeOfLastEvent();
    }

    public float getStatistic(){
        return this.areaNumInQueue;
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public int getSizeOfQueue(){
        return this.queue.size();
    }

    public float getTimeOfLastEvent() {
        return timeOfLastEvent;
    }

    public void setTimeOfLastEvent(float timeOfLastEvent) {
        this.timeOfLastEvent = timeOfLastEvent;
    }

}
