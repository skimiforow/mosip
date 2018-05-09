package mosip.company;

public class Evento {

    private state estado;
    private Cliente cliente;
    private Float ocorrenciaDoEvento;

    public enum state {
        CHEGADA, CORTE;
    }

    public Evento() {
        estado = state.CHEGADA;
        ocorrenciaDoEvento = 0.0f;
    }

    public void mudaEstado(){
        if (estado== state.CHEGADA){
            estado = state.CORTE;
        } else {
            estado = state.CHEGADA;
        }
    }

    public state getTipo() {
        return estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Float getOcorrenciaDoEvento() {
        return ocorrenciaDoEvento;
    }

    public void setOcorrenciaDoEvento(Float ocorrenciaDoEvento) {
        this.ocorrenciaDoEvento = ocorrenciaDoEvento;
    }
}
